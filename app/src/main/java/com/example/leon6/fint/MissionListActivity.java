package com.example.leon6.fint;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.kakaolink.AppActionBuilder;
import com.kakao.kakaolink.AppActionInfoBuilder;
import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;
import com.kakao.util.KakaoParameterException;

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

    private KakaoLink kakaoLink;
    private KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder;

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
                Dialog_select();
            }
        });

        Button addmission = (Button) findViewById(R.id.addmission);
        addmission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dialog_addmission("");

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

        if(title.equals("")){
            Toast.makeText(getApplicationContext(), "등록되어있지 않은 미션입니다.", Toast.LENGTH_SHORT).show();
            deletefromDatabase(missionID);
        }
        else{
            MissionList missionList = new MissionList();
            missionList.setID(missionID);
            missionList.setTitle(title);
            missionList.setWriter(writer);
            missionLists.add(missionList);
        }

//        Toast.makeText(getApplicationContext(), missionID+"/"+title+"/"+writer, Toast.LENGTH_SHORT).show();

    }

    // 미션 시작
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

    // 미션 삭제
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

    // 미션 등록
    private void Dialog_addmission(String missionID){
        AlertDialog.Builder ad = new AlertDialog.Builder(this);

        ad.setTitle("미션 추가");       // 제목 설정
        ad.setMessage("추가하실 미션 ID를 입력해주세요");   // 내용 설정

        // EditText 삽입하기
        final EditText et = new EditText(this);
        if(!missionID.equals("")){
            et.setText(missionID);
        }
        ad.setView(et);

        // 확인 버튼 설정
        ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Text 값 받아서 로그 남기기
                String value = et.getText().toString();

                for(int i=0; i<missionLists.size();i++){
                    MissionList missionList = missionLists.get(i);
                    if(missionList.getID().equals(value)){
                        Toast.makeText(getApplicationContext(), "이미 등록된 미션입니다.", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        break;
                    }
                    else if(i==missionLists.size()-1){
                        insertmissionID(value);
                        dialog.dismiss();     //닫기
                        // Event

                        missionLists.clear();

                        getfromDatabase();

                        Handler hd = new Handler();
                        hd.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        }, 500);
                    }
                }

            }
        });

        // 취소 버튼 설정
        ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();     //닫기
                // Event
            }
        });

        // 창 띄우기
        ad.show();
    }
    private void insertmissionID(String mission){

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

                    SharedPreferences pref = getSharedPreferences("login", MODE_PRIVATE);
                    userID = pref.getString("userID", "error");

                    String missionID = (String)params[0];

                    String link="http://leon6095.phps.kr/saveIDtoUser.php";

                    String data  = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(userID, "UTF-8");
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
        task.execute(mission);
    }

    // 미션 클릭시 선택지
    private void Dialog_select(){
        final CharSequence[] items = { "시작하기", "삭제하기", "카카오톡으로 공유" };
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MissionListActivity.this);

        // 제목셋팅
        alertDialogBuilder.setTitle("선택");
        alertDialogBuilder.setItems(items,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if(items[id].equals("시작하기")){
                            dialog.dismiss();
                            DialogView();
                        }
                        else if(items[id].equals("삭제하기")){
                            dialog.dismiss();
                            DialogView2();
                        }
                        else if(items[id].equals("카카오톡으로 공유")){
                            dialog.dismiss();
                            SendtoKakao();
//                            Toast.makeText(getApplicationContext(), "카톡", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // 다이얼로그 생성
        AlertDialog alertDialog = alertDialogBuilder.create();

        // 다이얼로그 보여주기
        alertDialog.show();

    }

    // 카카오톡으로 미션 전송
    private void SendtoKakao(){

        try {
            kakaoLink = KakaoLink.getKakaoLink(this);
            kakaoTalkLinkMessageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();
        } catch (KakaoParameterException e) {
            e.printStackTrace();
        }

        try {
            kakaoTalkLinkMessageBuilder.addText("미션이 도착했습니다!");
            MissionList missionList = missionLists.get(selectpos);
            kakaoTalkLinkMessageBuilder.addAppButton("미션 받기",
                    new AppActionBuilder().addActionInfo(AppActionInfoBuilder.createAndroidActionInfoBuilder().setExecuteParam("execparamkey1="+missionList.getID()).build())
                            .build());
            kakaoTalkLinkMessageBuilder.build();
            kakaoLink.sendMessage(kakaoTalkLinkMessageBuilder, this);
        } catch (KakaoParameterException e) {
            e.getMessage();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        Uri uri = intent.getData();

        if(uri != null)
        {
            String execparamkey1	=	uri.getQueryParameter("execparamkey1");

            if(execparamkey1 != null)
                Dialog_addmission(execparamkey1);
//                Toast.makeText(getApplicationContext(), execparamkey1, Toast.LENGTH_SHORT).show();
        }
    }
}
