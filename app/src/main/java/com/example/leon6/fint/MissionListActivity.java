package com.example.leon6.fint;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class MissionListActivity extends Activity {

    String userID;

    String mission1;
    String mission2;
    String mission3;
    String mission4;
    String mission5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_missionlist);

        getfromDatabase();

    }

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
    private void getfromDatabase2(){

        class InsertData extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
//                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                jsonParsing2(s);
                super.onPostExecute(s);
            }

            @Override
            protected String doInBackground(String... params) {

                try{

                    String link="http://leon6095.phps.kr/getmissiondata.php";

                    String data  = URLEncoder.encode("missionID", "UTF-8") + "=" + URLEncoder.encode(mission1, "UTF-8");

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
        try {
            JSONObject jsonObject = new JSONObject(jsondata);
            JSONArray result = jsonObject.getJSONArray("result");
            for(int i=0; i<result.length();i++){
                JSONObject resultinfo = result.getJSONObject(i);
                mission1 = resultinfo.getString("mission1");
                mission2 = resultinfo.getString("mission2");
                mission3 = resultinfo.getString("mission3");
                mission4 = resultinfo.getString("mission4");
                mission5 = resultinfo.getString("mission5");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        Toast.makeText(getApplicationContext(), mission1+" / "+mission2+" / "+mission3+" / "+mission4+" / "+mission5, Toast.LENGTH_SHORT).show();
        getfromDatabase2();
    }
    private void jsonParsing2(String jsondata) {
        String writer = "";
        String title = "";
        String loc0 = "";
        String hint0 = "";
        String loc1 = "";
        String hint1 = "";
        String loc2 = "";
        String hint2 = "";
        String loc3 = "";
        String hint3 = "";
        String loc4 = "";
        String hint4 = "";
        try {
            JSONObject jsonObject = new JSONObject(jsondata);
            JSONArray result = jsonObject.getJSONArray("result");
            for(int i=0; i<result.length();i++){
                JSONObject resultinfo = result.getJSONObject(i);
                writer = resultinfo.getString("writer");
                title = resultinfo.getString("title");
                loc0 = resultinfo.getString("loc0");
                hint0 = resultinfo.getString("hint0");
                loc1 = resultinfo.getString("loc1");
                hint1 = resultinfo.getString("hint1");
                loc2 = resultinfo.getString("loc2");
                hint2 = resultinfo.getString("hint2");
                loc3 = resultinfo.getString("loc3");
                hint3 = resultinfo.getString("hint3");
                loc4 = resultinfo.getString("loc4");
                hint4 = resultinfo.getString("hint4");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Toast.makeText(getApplicationContext(), writer+" / "+title+" / "+loc0+" / "+hint0+" / "+loc1+" / "+hint1+" / "+loc2+" / "+hint2+" / "+loc3+" / "+hint3+" / "+loc4+" / "+hint4, Toast.LENGTH_SHORT).show();

    }
}
