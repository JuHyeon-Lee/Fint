package com.example.leon6.fint;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MissionListActivity extends Activity {

    String userID;

    int selectpos;

    ArrayList<MissionList> missionLists = new ArrayList<MissionList>();

    ListView listview;
    ListViewAdapter adapter = new ListViewAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_missionlist);

        getfromDatabase();

        listview = (ListView) findViewById(R.id.missionlistview2);
        listview.setAdapter(adapter);

        Handler hd = new Handler();
        hd.postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        }, 500);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectpos = position;
                DialogView();
            }
        });

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                selectpos = position;
                DialogView2();
                return false;
            }
        });

    }

    // 리스트뷰 어댑터
    public class ListViewAdapter extends BaseAdapter {

        ArrayList<MissionList> missionLists2 = missionLists;

        // ListViewAdapter의 생성자
        public ListViewAdapter() {

        }

        // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
        @Override
        public int getCount() {
            return missionLists2.size() ;
        }

        // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int pos = position;
            final Context context = parent.getContext();

            // "listview_item" Layout을 inflate하여 convertView 참조 획득.
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.missionlistview_row, parent, false);
            }

            // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
            TextView missionID = (TextView) convertView.findViewById(R.id.missionID);
            TextView missionTitle = (TextView) convertView.findViewById(R.id.missiontitle);
            TextView missionWriter = (TextView) convertView.findViewById(R.id.missionwriter);

            MissionList missionList = missionLists2.get(position);
            missionID.setText(missionList.getID());
            missionTitle.setText(missionList.getTitle());
            missionWriter.setText(missionList.getWriter());

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
            return missionLists2.get(position) ;
        }

    }

    // 사용자 미션 목록 불러오기
    private void getfromDatabase(){

        class InsertData extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
//                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                jsonParsing(s);
                super.onPostExecute(s);
            }

            @Override
            protected String doInBackground(String... params) {

                try{

                    SharedPreferences pref = getSharedPreferences("login", MODE_PRIVATE);
                    userID = pref.getString("userID", "error");

                    String link="http://leon6095.phps.kr/getmissionlist.php";

                    String data  = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(userID, "UTF-8");

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
    private void jsonParsing(String jsondata) {

        String[] mission = new String[5];

        try {
            JSONObject jsonObject = new JSONObject(jsondata);
            JSONArray result = jsonObject.getJSONArray("result");

            for(int i=0; i<result.length();i++){
                JSONObject resultinfo = result.getJSONObject(i);
                mission[0] = resultinfo.getString("mission1");
                mission[1] = resultinfo.getString("mission2");
                mission[2] = resultinfo.getString("mission3");
                mission[3] = resultinfo.getString("mission4");
                mission[4] = resultinfo.getString("mission5");

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        Toast.makeText(getApplicationContext(), mission[0]+"/"+mission[1]+"/"+mission[2]+"/"+mission[3]+"/"+mission[4] , Toast.LENGTH_SHORT).show();

        for(int i=0;i<5;i++){
            if(mission[i]==null){
                break;
            }
            else if(mission[i].equals("null")){
                break;
            }
            else{
                getfromDatabase2(mission[i]);
            }
        }

        adapter.notifyDataSetChanged();

//        for(int i=0 ; i<missionLists.size();i++){
//            MissionList missionList = missionLists.get(i);
//            Toast.makeText(getApplicationContext(), missionList.getID() , Toast.LENGTH_SHORT).show();
//        }


    }

    // 미션 정보 불러오기
    private void getfromDatabase2(final String mission){

        class InsertData extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
//                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                jsonParsing2(s,mission);
                super.onPostExecute(s);
            }

            @Override
            protected String doInBackground(String... params) {

                try{

                    String mission = (String)params[0];

                    String link="http://leon6095.phps.kr/getmissiondata.php";

                    String data  = URLEncoder.encode("missionID", "UTF-8") + "=" + URLEncoder.encode(mission, "UTF-8");

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
        task.execute(mission);
    }
    private void jsonParsing2(String jsondata, String missionID) {

        String writer = "";
        String title = "";

        try {
            JSONObject jsonObject = new JSONObject(jsondata);
            JSONArray result = jsonObject.getJSONArray("result");
            for(int i=0; i<result.length();i++){
                JSONObject resultinfo = result.getJSONObject(i);
                writer = resultinfo.getString("writer");
                title = resultinfo.getString("title");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MissionList missionList = new MissionList();
        missionList.setID(missionID);
        missionList.setTitle(title);
        missionList.setWriter(writer);
        missionLists.add(missionList);

//        Toast.makeText(getApplicationContext(), missionID+"/"+title+"/"+writer, Toast.LENGTH_SHORT).show();

    }

    private void DialogView() {

        final MissionList missionList = missionLists.get(selectpos);

        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(MissionListActivity.this);
        alert_confirm.setMessage("미션을 시작하시겠습니까?").setCancelable(false).setPositiveButton("네",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences pref = getSharedPreferences("login", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("missionID", missionList.getID() );
                        editor.commit();
                        dialog.dismiss();
                        finish();
                    }
                }).setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = alert_confirm.create();
        alert.show();
    }

    private void DialogView2() {

        final MissionList missionList = missionLists.get(selectpos);

        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(MissionListActivity.this);
        alert_confirm.setMessage("미션을 삭제하시겠습니까?").setCancelable(false).setPositiveButton("네",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletefromDatabase(missionList.getID());
                        missionLists.remove(selectpos);
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "삭제가 완료되었습니다." , Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = alert_confirm.create();
        alert.show();
    }

    private void deletefromDatabase(final String mission){

        class InsertData extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
//                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
//                getfromDatabase();
                super.onPostExecute(s);
            }

            @Override
            protected String doInBackground(String... params) {

                try{

                    SharedPreferences pref = getSharedPreferences("login", MODE_PRIVATE);
                    userID = pref.getString("userID", "error");

                    String missionID = (String)params[0];

                    String link="http://leon6095.phps.kr/deletemission.php";

                    String data  = URLEncoder.encode("missionID", "UTF-8") + "=" + URLEncoder.encode(missionID, "UTF-8");
                    data += "&" + URLEncoder.encode("userID", "UTF-8") + "=" + URLEncoder.encode(userID, "UTF-8");

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
        task.execute(mission);
    }

}
