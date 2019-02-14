package com.smism.jc.tpkdemo;

import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.esri.arcgisruntime.arcgisservices.TileInfo;
import com.esri.arcgisruntime.data.TileCache;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private MapView mMapView;
    private Button mButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        mButton=(Button)findViewById(R.id.mButton);


//        String theOfflineTiledLayers= Environment.getExternalStorageDirectory()+"/test_app/test.tpk";
        String theOfflineTiledLayers= Environment.getExternalStorageDirectory()+"/test_app/files/v101/Layers/_alllayers/";



        File dir = new File(theOfflineTiledLayers);
        if (dir.exists()) {
            Toast.makeText(this,"存在",Toast.LENGTH_SHORT).show();
        }
        mMapView=(MapView)findViewById(R.id.mMapView);

        //离线切片对象
//        TileCache mTileCache=new TileCache(theOfflineTiledLayers);
//        mTileCache.loadAsync();
//        ArcGISTiledLayer mArcGisTiledLayer=new ArcGISTiledLayer(theOfflineTiledLayers);

        WWTilesLayer wwTilesLayer=WWTilesLayer.getInstance(theOfflineTiledLayers,WWTilesLayer.buildTileInfo(),WWTilesLayer.buildEnvelope());

//        String a=String.valueOf(mTileCache.getTileInfo().getDpi());
//        String b=mTileCache.getTileInfo().getFormat().toString();
//        String c=mTileCache.getTileInfo().getLevelsOfDetail().toString();
//        String d=mTileCache.getTileInfo().getOrigin().toString();
//        String e=mTileCache.getTileInfo().getSpatialReference().toString();
//        String f=String.valueOf(mTileCache.getTileInfo().getTileHeight());
//        String g=String.valueOf(mTileCache.getTileInfo().getTileWidth());
//        Log.i("---",a);
//        Log.i("---",b);
//        Log.i("---",c);
//        Log.i("---",d);
//        Log.i("---",e);
//        Log.i("---",f);
//        Log.i("---",g);





        //底图对象
        Basemap mBaseMap=new Basemap(wwTilesLayer);
        //创建地图对象
        ArcGISMap mArcGisMap=new ArcGISMap(mBaseMap);
        //显示地图
        mMapView.setMap(mArcGisMap);
        //初始化视点



        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Viewpoint viewpoint=new Viewpoint(31.14,121.66322,34481);
                mMapView.setViewpointAsync(viewpoint,1);
            }
        });



    }



    @Override
    protected void onPause() {
        super.onPause();
        mMapView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.dispose();
    }




}
