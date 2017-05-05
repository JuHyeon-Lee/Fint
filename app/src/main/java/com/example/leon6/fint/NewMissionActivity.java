package com.example.leon6.fint;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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

        Toast.makeText(getApplicationContext(), "원하는 위치를 길게 터치하시면\n새로운 힌트가 만들어집니다.", Toast.LENGTH_SHORT).show();

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
                markerOptions.title("힌트");
                markerOptions.snippet("힌트 내용");

                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));   // 마커생성위치로 이동
                Marker marker = mMap.addMarker(markerOptions); //마커 생성

                MissionInfo mission = new MissionInfo();

                mission.setId(marker.getId());
                mission.setHint("힌트 내용");
                mission.setLat(latLng.latitude);
                mission.setLon(latLng.longitude);
                missioninfo.add(mission);
            }
        });

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                int pos=0;

                // 어레이리스트에서 marker ID 값을 검색해서 position 반환
                for(int i=0; i<missioninfo.size(); i++){
                    MissionInfo ft=missioninfo.get(i);
                    if(ft.getId().equals(marker.getId())){
                        pos=i+1;
                        break;
                    }
                }

                marker.setTitle("힌트"+pos);

                return false;
            }
        });

        //정보창 클릭 리스너
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                int pos=0;

                // 어레이리스트에서 marker ID 값을 검색해서 position 반환
                for(int i=0; i<missioninfo.size(); i++){
                    MissionInfo ft=missioninfo.get(i);
                    if(ft.getId().equals(marker.getId())){
                        pos=i;
                        break;
                    }
                }

                edithint(pos, marker);
                marker.hideInfoWindow();
            }
        });
    }

    public void gotolist(){
        Intent intent = new Intent(this, MissionListActivity.class);
        intent.putExtra("mission", missioninfo);
        startActivity(intent);
        finish();
    }

    public void edithint(final int pos, final Marker marker){

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("힌트 내용 수정");

        MissionInfo mission = missioninfo.get(pos);
        final EditText name = new EditText(this);
        name.setText(mission.getHint());
        alert.setView(name);

        alert.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String username = name.getText().toString();
                MissionInfo mission = missioninfo.get(pos);
                mission.setHint(username);
                marker.setSnippet(username);
            }
        });


        alert.setNegativeButton("no",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });

        alert.show();
    }
}
