package com.example.leon6.fint;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SaveMissionActivity extends Activity{

    ArrayList<MissionInfo> missionInfos = new ArrayList<MissionInfo>();
    String writer;
    String title;
    String[] location;
    String[] hints;

    String missionID;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savemission);

        ListView listview ;
        ListViewAdapter adapter;

        // Adapter 생성
        adapter = new ListViewAdapter() ;

        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) findViewById(R.id.missionlistview);
        listview.setAdapter(adapter);

        TextView missiontitle = (TextView) findViewById(R.id.missiontitle);
        TextView missionwriter = (TextView) findViewById(R.id.missionwriter);

        SharedPreferences pref = getSharedPreferences("login", MODE_PRIVATE);
        missionwriter.setText(pref.getString("nickname", "작성자"));
        writer = pref.getString("nickname", "작성자");

        missiontitle.setText(getIntent().getStringExtra("title"));
        title = getIntent().getStringExtra("title");

        id = pref.getString("userID","error");
        Toast.makeText(getApplicationContext(), id, Toast.LENGTH_SHORT).show();

        Button finishsave = (Button) findViewById(R.id.finishsave);
        finishsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savetodatabase();
            }
        });

    }

    // 리스트뷰 어댑터
    public class ListViewAdapter extends BaseAdapter {
        // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
        private ArrayList<MissionInfo>  missioninfo = (ArrayList<MissionInfo>) getIntent().getSerializableExtra("mission");

        // ListViewAdapter의 생성자
        public ListViewAdapter() {

        }

        // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
        @Override
        public int getCount() {
            return missioninfo.size() ;
        }

        // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int pos = position;
            final Context context = parent.getContext();

            missionInfos = missioninfo;

            // "listview_item" Layout을 inflate하여 convertView 참조 획득.
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.listview_row, parent, false);
            }

            // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
            TextView missionnum = (TextView) convertView.findViewById(R.id.missionnum);
            TextView geoinfo = (TextView) convertView.findViewById(R.id.geoinfo);
            TextView hint_text = (TextView) convertView.findViewById(R.id.hint);

            MissionInfo missionInfo = missioninfo.get(position);

            String hint = "힌트"+ String.valueOf(position+1);
            missionnum.setText(hint);

            String add = getLocation(missionInfo.getLat(),missionInfo.getLon());
            geoinfo.setText(add);

            hint_text.setText(missionInfo.getHint());

            return convertView;
        }

        // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
        @Override
        public long getItemId(int position) {
            return position ;
        }

        // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
        @Override
        public Object getItem(int position) {
            return missioninfo.get(position) ;
        }

    }

    // Geocoder를 이용한 좌표 -> 주소
    public String getLocation(double lat, double lng){
        String str = null;
        Geocoder geocoder = new Geocoder(this, Locale.KOREA);

        List<Address> address;
        try {
            if (geocoder != null) {
                address = geocoder.getFromLocation(lat, lng, 1);
                if (address != null && address.size() > 0) {
                    str = address.get(0).getAddressLine(0).toString();
                }
            }
        } catch (IOException e) {
            Log.e("MainActivity", "주소를 찾지 못하였습니다.");
            e.printStackTrace();
        }

        return str;

    }

    private void savetodatabase(){

        String loc[] = new String[missionInfos.size()];
        String hint[] = new String[missionInfos.size()];

        for(int i=0 ; i<missionInfos.size() ; i++){
            MissionInfo missioninfo = missionInfos.get(i);

            String latitude = String.valueOf(missioninfo.getLat());
            String longitude = String.valueOf(missioninfo.getLon());

            String location = latitude+","+longitude;

            loc[i]=location;
            hint[i]=missioninfo.getHint();
        }

        location = loc;
        hints = hint;

        insertToDatabase();

    }

    // DB에 회원정보 저장
    private void insertToDatabase(){

        class InsertData extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                missionID = s;
                insertmissionID();

                SharedPreferences pref = getSharedPreferences("login", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("missionID", s);
                editor.commit();

                super.onPostExecute(s);
            }

            @Override
            protected String doInBackground(String... params) {

                try{

                    String link="http://leon6095.phps.kr/savemission.php";

                    String data  = URLEncoder.encode("title", "UTF-8") + "=" + URLEncoder.encode(title, "UTF-8");
                    data += "&" + URLEncoder.encode("writer", "UTF-8") + "=" + URLEncoder.encode(writer, "UTF-8");
                    for(int i=0; i<location.length; i++){
                        data += "&" + URLEncoder.encode("loc"+i, "UTF-8") + "=" + URLEncoder.encode(location[i], "UTF-8");
                        data += "&" + URLEncoder.encode("hint"+i, "UTF-8") + "=" + URLEncoder.encode(hints[i], "UTF-8");
                    }

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write( data );
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while((line = reader.readLine()) != null)
                    {
                        sb.append(line);
                        break;
                    }
                    return sb.toString();
                }
                catch(Exception e){
                    return new String("Exception: " + e.getMessage());
                }

            }
        }

        InsertData task = new InsertData();
        task.execute();
    }

    private void insertmissionID(){

        class InsertData extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
//                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                super.onPostExecute(s);
            }

            @Override
            protected String doInBackground(String... params) {

                try{

                    String link="http://leon6095.phps.kr/saveIDtoUser.php";

                    String data  = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");
                    data += "&" + URLEncoder.encode("missionID", "UTF-8") + "=" + URLEncoder.encode(missionID, "UTF-8");

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write( data );
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while((line = reader.readLine()) != null)
                    {
                        sb.append(line);
                        break;
                    }
                    return sb.toString();
                }
                catch(Exception e){
                    return new String("Exception: " + e.getMessage());
                }

            }
        }

        InsertData task = new InsertData();
        task.execute();
    }

}
