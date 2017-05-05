package com.example.leon6.fint;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class NewMissionActivity extends Activity implements OnMapReadyCallback {

    GoogleMap mMap;
    UiSettings uiSettings;

    ArrayList<MissionInfo> missioninfo = new ArrayList<MissionInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newmission);

        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.mapView2);
        mapFragment.getMapAsync(this);

        Button savemission = (Button) findViewById(R.id.savemission);
        savemission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotolist();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {

        LatLng SEOUL = new LatLng(37.56, 126.97);

        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(SEOUL);
//        markerOptions.title("힌트1");
//        markerOptions.snippet("Hint1");
//        map.addMarker(markerOptions);

        map.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
        map.animateCamera(CameraUpdateFactory.zoomTo(10));

        uiSettings = map.getUiSettings();
        uiSettings.setTiltGesturesEnabled(false);
        uiSettings.setRotateGesturesEnabled(false);

        mMap = map;

        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
//                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.logo3));
                markerOptions.position(latLng); //마커위치설정

                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));   // 마커생성위치로 이동
                mMap.addMarker(markerOptions); //마커 생성

                MissionInfo mission = new MissionInfo();

                mission.setLat(latLng.latitude);
                mission.setLon(latLng.longitude);
                missioninfo.add(mission);
            }
        });
    }

    public void gotolist(){
        Intent intent = new Intent(this, MissionListActivity.class);
        intent.putExtra("mission", missioninfo);
        startActivity(intent);
        finish();
    }
}
