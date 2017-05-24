package com.example.leon6.fint;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
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

public class MapActivity extends Activity implements OnMapReadyCallback {

    GoogleMap mMap;
    UiSettings uiSettings;

    double longitude;
    double latitude;
    double altitude;
    float accuracy;
    String provider;

    LatLng HERE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);

        Button gotolist = (Button) findViewById(R.id.gotolist);
        gotolist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                gotomap();
            }
        });

        Button newmission = (Button) findViewById(R.id.newmission);
        newmission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                newmission();
            }
        });

    }

    // 맵 불러오기 완료
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

        permission();

        final LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {

        }
        else{
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, // 등록할 위치제공자
                    100, // 통지사이의 최소 시간간격 (miliSecond)
                    1, // 통지사이의 최소 변경거리 (m)
                    mLocationListener);
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자
                    100, // 통지사이의 최소 시간간격 (miliSecond)
                    1, // 통지사이의 최소 변경거리 (m)
                    mLocationListener);


        }

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                // GPS 활성화 체크, 활성화 - return true
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                    Toast.makeText(getApplicationContext(), "GPS를 켜주세요.", Toast.LENGTH_SHORT).show();
                    Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(gpsOptionsIntent);
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
    private void DialogView() {
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

    // 액티비티 이동
    public void gotomap() {
        Intent intent = new Intent(this, MissionListActivity.class);
        startActivity(intent);
    }
    public void newmission() {
        Intent intent = new Intent(this, NewMissionActivity.class);
        startActivity(intent);
    }

    // 위치정보 권한 얻기
    public void permission() {
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
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                permission();
            }

        }
    }

    // 위치정보 획득
    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            //여기서 위치값이 갱신되면 이벤트가 발생한다.
            //값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.

            Log.d("test", "onLocationChanged, location:" + location);
            longitude = location.getLongitude(); //경도
            latitude = location.getLatitude();   //위도
            altitude = location.getAltitude();   //고도
            accuracy = location.getAccuracy();    //정확도
            provider = location.getProvider();   //위치제공자
            //Gps 위치제공자에 의한 위치변화. 오차범위가 좁다.
            //Network 위치제공자에 의한 위치변화
            //Network 위치는 Gps에 비해 정확도가 많이 떨어진다.

            HERE = new LatLng(latitude,longitude);
//            Toast.makeText(getApplicationContext(), latitude+"/"+longitude,Toast.LENGTH_SHORT).show();
            mMap.moveCamera(CameraUpdateFactory.newLatLng(HERE));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }
        public void onProviderDisabled(String provider) {
            // Disabled시
            Log.d("test", "onProviderDisabled, provider:" + provider);
        }

        public void onProviderEnabled(String provider) {
            // Enabled시
            Log.d("test", "onProviderEnabled, provider:" + provider);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // 변경시
            Log.d("test", "onStatusChanged, provider:" + provider + ", status:" + status + " ,Bundle:" + extras);
        }
    };

}
