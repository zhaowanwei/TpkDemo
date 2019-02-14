package com.smism.jc.tpkdemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.util.Log;

import com.esri.arcgisruntime.arcgisservices.LevelOfDetail;
import com.esri.arcgisruntime.arcgisservices.TileInfo;
import com.esri.arcgisruntime.data.TileKey;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.layers.ImageTiledLayer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WWTilesLayer extends ImageTiledLayer {
    private static WWTilesLayer wwTilesLayer;
    private static TileInfo tileInfo;
    private static Envelope fullExtent;
    private String wwTilesPath;



    public WWTilesLayer(String wwTilesPath, TileInfo tileInfo, Envelope fullExtent){
        super( tileInfo, fullExtent);
        this.wwTilesPath = wwTilesPath;
    }
    public static WWTilesLayer getInstance(String wwTilesPath, TileInfo tileInfo, Envelope fullExtent){
        if (wwTilesLayer==null){
            wwTilesLayer=new WWTilesLayer(wwTilesPath,tileInfo,fullExtent);
        }
        return wwTilesLayer;
    }
    public static double[] scale ={
            591657527.591555,
            295828763.79577702,
            147914381.89788899,
            73957190.948944002,
            36978595.474472001,
            18489297.737236001,
            9244648.8686180003,
            4622324.4343090001,
            2311162.2171550002,
            1155581.108577,
            577790.55428899999,
            288895.27714399999,
            144447.638572,
            72223.819285999998,
            36111.909642999999,
            18055.954822,
            9027.9774109999998,
            4513.9887049999998,
            2256.994353,
            1128.497176000000,
    };
    public static double[] resolution ={
            156543.03392799999,
            78271.516963999893,
            39135.758482000099,
            19567.879240999901,
            9783.9396204999593,
            4891.9698102499797,
            2445.9849051249898,
            1222.9924525624899,
            611.49622628138002,
            305.74811314055802,
            152.874056570411,
            76.437028285073197,
            38.218514142536598,
            19.109257071268299,
            9.5546285356341496,
            4.7773142679493699,
            2.38865713397468,
            1.1943285668550501,
            0.59716428355981699,
            0.29858214164761698,
    };


    @SuppressLint("DefaultLocale")
    @Override
    protected byte[] getTile(TileKey tileKey) {



        //一个bundle只能存储128*128个切片，有多个bundle，通过tilekey的行列值，判断属于哪个bundle
        byte[] result=null;
        String path;
        int R = 128 * (tileKey.getRow() / 128);//十进制
        int C = 128 * (tileKey.getColumn()/128);//十进制

        path = wwTilesPath + "L" + String.format("%02d", tileKey.getLevel()) + "/";
        path = path + "R" + String.format("%04x", R) + "C" + String.format("%04x", C);

        Log.i("-----",path);

        try {
            FileInputStream fin = new FileInputStream(path + ".bundle");
            FileInputStream findex = new FileInputStream(path + ".bundlx");
            //bundlx只存储了对应bundle中图片的偏移量，所有要想知道在bundlx中的偏移量，首先要把前面所有的bundlx的长度给减掉
            //index为tileKey在bundlx偏移的单位数(每个单位为5个字节，没有计算在内)
            int index = 128 * (tileKey.getColumn() - C) + (tileKey.getRow() - R);
            byte[] imageIndex = readByteFromFileStreams(findex, 16 + index * 5, 5);//找到索引



            //根据得到的索引值，转换成十进制，即为图片在bundle中的偏移量
            long offset = (long) (imageIndex[0] & 0xff) + (long) (imageIndex[1] & 0xff) * 256 + (long) (imageIndex[2] & 0xff) * 65536 + (long) (imageIndex[3] & 0xff) * 16777216+ (long) (imageIndex[4] & 0xff) * 4294967296L;//十进制
            long startOffset = offset ;
            //先获得切片长度
            byte[] imageLength = readByteFromFileStreams(fin, (int) startOffset, 4);
            int length = (int) (imageLength[0] & 0xff) + (int) (imageLength[1] & 0xff) * 256 + (int) (imageLength[2] & 0xff) * 65536 + (int) (imageLength[3] & 0xff) * 16777216;
            //读取切片
            result=new byte[length];
            result = readByteFromFileStreams(fin, (int) offset, length);
            return result;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public TileInfo getTileInfo() {
        return tileInfo;
    }
    private byte[] readByteFromFileStreams(InputStream fileStream, int offset, int len) throws IOException {
        byte[] result = new byte[len];
        fileStream.skip(offset);
        fileStream.read(result);
        return result;
    }

    public static TileInfo buildTileInfo() {
        Point iPoint = new Point(-20037508.342787,20037508.342787,SpatialReference.create(102100));
        List<LevelOfDetail> levelOfDetails=new ArrayList<>();
        for (int i=0;i<resolution.length;i++){
            LevelOfDetail levelOfDetail=new LevelOfDetail(i,resolution[i],scale[i]);
            levelOfDetails.add(levelOfDetail);
        }
        tileInfo = new TileInfo(96, TileInfo.ImageFormat.PNG, levelOfDetails, iPoint, SpatialReference.create(102100), 256, 256);
        return tileInfo;
    }
    public static Envelope buildEnvelope() {
//        fullExtent = new Envelope(121.643853696777, 31.1331561908896, 121.678479436772, 31.1714844852604,SpatialReference.create(102100));//全服范围  错误
//        fullExtent = new Envelope(13541507.057099674, 3652074.2996890345, 13545011.162841443, 3653019.0753587633,SpatialReference.create(102100));//全服范围
        fullExtent = new Envelope(13541331.9174028, 3655039.84690429, 13545186.3142694, 3650054.12230305,SpatialReference.create(102100));//全服范围





        return fullExtent;

    }








}