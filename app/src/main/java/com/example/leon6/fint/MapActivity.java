package com.example.leon6.fint;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.Manifest;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

public class MapActivity extends Activity implements OnMapReadyCallback {

    private SessionCallback callback;

    GoogleMap mMap;
    UiSettings uiSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);

        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment)fragmentManager.findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);

        Button gotolist = (Button) findViewById(R.id.gotolist);
        gotolist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Session.getCurrentSession().checkAndImplicitOpen();
            }
        });

    }

    // 맵 불러오기 완료
    @Override
    public void onMapReady(GoogleMap map) {

        LatLng SEOUL = new LatLng(37.56, 126.97);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(SEOUL);
        markerOptions.title("힌트1");
        markerOptions.snippet("Hint1");
        map.addMarker(markerOptions);

        map.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
        map.animateCamera(CameraUpdateFactory.zoomTo(15));

        uiSettings = map.getUiSettings();
        uiSettings.setTiltGesturesEnabled(false);
        uiSettings.setRotateGesturesEnabled(false);

        mMap=map;

        permission();

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                LocationManager locationManager = (LocationManager)getSystemService( LOCATION_SERVICE );
                // GPS 활성화 체크, 활성화 - return true
                if( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER)) {

                    Toast.makeText(getApplicationContext(),"GPS를 켜주세요.",Toast.LENGTH_SHORT).show();
                    Intent gpsOptionsIntent = new Intent( android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS );
                    startActivity( gpsOptionsIntent );
                }
                return false;
            }
        });
    }

    // 어플리케이션 종료
    @Override
    public void onBackPressed() {
        DialogView();
    }
    private void DialogView(){
        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(MapActivity.this);
        alert_confirm.setMessage("종료하시겠습니까?").setCancelable(false).setPositiveButton("네",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        moveTaskToBack(true);
                        finish();
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                }).setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
        AlertDialog alert = alert_confirm.create();
        alert.show();
    }

    // 액티비티 전환 시 필요
    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            gotomap();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if(exception != null) {
                Logger.e(exception);
            }
        }
    }
    public void gotomap(){
        Intent intent = new Intent(this, MissionListActivity.class);
        startActivity(intent);
    }

    // 위치정보 권한 얻기
    public void permission(){
        /* 사용자의 OS 버전이 마시멜로우 이상인지 체크한다. */
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {

                    /* 사용자 단말기의 권한 중 "전화걸기" 권한이 허용되어 있는지 체크한다.
                    *  int를 쓴 이유? 안드로이드는 C기반이기 때문에, Boolean 이 잘 안쓰인다.
                    */
            int permissionResult = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);

                    /* CALL_PHONE의 권한이 없을 때 */
            // 패키지는 안드로이드 어플리케이션의 아이디다.( 어플리케이션 구분자 )
            if (permissionResult == PackageManager.PERMISSION_DENIED) {


                        /* 사용자가 CALL_PHONE 권한을 한번이라도 거부한 적이 있는 지 조사한다.
                        * 거부한 이력이 한번이라도 있다면, true를 리턴한다.
                        */
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {

                    AlertDialog.Builder dialog = new AlertDialog.Builder(MapActivity.this);
                    dialog.setTitle("권한이 필요합니다.")
                            .setMessage("Fint를 이용하시려면 위치정보 권한이 꼭 필요합니다. 계속하시겠습니까?")
                            .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
                                    }

                                }
                            })
                            .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    moveTaskToBack(true);
                                    finish();
                                    android.os.Process.killProcess(android.os.Process.myPid());
                                }
                            })
                            .create()
                            .show();
                }

                //최초로 권한을 요청할 때
                else {
                    // CALL_PHONE 권한을 Android OS 에 요청한다.
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
                }

            }
                    /* CALL_PHONE의 권한이 있을 때 */
            else {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            }

        }
                /* 사용자의 OS 버전이 마시멜로우 이하일 떄 */
        else {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1000) {

            /* 요청한 권한을 사용자가 "허용"했다면 인텐트를 띄워라
                내가 요청한 게 하나밖에 없기 때문에. 원래 같으면 for문을 돈다.*/
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                }
            }
            else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                permission();
            }

        }
    }


}
