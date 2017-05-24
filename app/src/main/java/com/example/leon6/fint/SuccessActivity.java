package com.example.leon6.fint;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.kakaotalk.KakaoTalkService;
import com.kakao.kakaotalk.callback.TalkResponseCallback;
import com.kakao.kakaotalk.response.KakaoTalkProfile;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;


public class SuccessActivity extends AppCompatActivity {

    long userID=0;
    String nickName="";
    String email="";

    ViewPager vp;

    Button gotomap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        Intent intent = new Intent(this, SplashActivity.class);
        startActivity(intent);

        SharedPreferences pref = getSharedPreferences("login", MODE_PRIVATE);
        Boolean tut = pref.getBoolean("tutorial", false);
        if(tut==true){
            Handler hd = new Handler();

            hd.postDelayed(new Runnable() {
                @Override
                public void run() {
                    gotomap();
                }
            }, 1900);

        }

        vp = (ViewPager)findViewById(R.id.vp);
        vp.setAdapter(new pagerAdapter(getSupportFragmentManager()));
        vp.setCurrentItem(0);
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position==2)
                    gotomap.setVisibility(View.VISIBLE);
                else
                    gotomap.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        gotomap = (Button) findViewById(R.id.gotomap);
        gotomap.setVisibility(View.INVISIBLE);
        gotomap.bringToFront();
        gotomap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotomap();
            }
        });

        Button nextpage = (Button) findViewById(R.id.nextpage);
        nextpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int next = vp.getCurrentItem();

                if(next==0||next==1)
                    next++;
                else;

                vp.setCurrentItem(next);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        KakaoTalkService.requestProfile(new KakaoTalkResponseCallback<KakaoTalkProfile>() {
            @Override
            public void onSuccess(KakaoTalkProfile talkProfile) {
                nickName = talkProfile.getNickName();
                SharedPreferences pref = getSharedPreferences("login", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("nickname", nickName);
                editor.commit();
            }
        });

        requestMe();

        insertToDatabase(Long.toString(userID), nickName, email);

    }

    // 프로필 요청
    public void requestMe() {
        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Logger.d(message);

                redirectLoginActivity();
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                redirectLoginActivity();
            }

            @Override
            public void onSuccess(UserProfile userProfile) {
                Logger.d("UserProfile : " + userProfile);
                userID = userProfile.getId();
                email = userProfile.getEmail();

                SharedPreferences pref = getSharedPreferences("login", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("userID", Long.toString(userID));
                editor.commit();
            }

            @Override
            public void onNotSignedUp() {
            }
        });
    }

    // 연결 해제 시
    private void redirectLoginActivity() {
        final Intent intent = new Intent(this, MainActivity.class);
        Toast.makeText(getApplicationContext(), "로그인 창으로 돌아갑니다.", Toast.LENGTH_SHORT).show();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    // 로그인 판단
    private abstract class KakaoTalkResponseCallback<T> extends TalkResponseCallback<T> {
        @Override
        public void onNotKakaoTalkUser() {
            Logger.w("not a KakaoTalk user");
        }

        @Override
        public void onFailure(ErrorResult errorResult) {
            Logger.e("failure : " + errorResult);
        }

        @Override
        public void onSessionClosed(ErrorResult errorResult) {
            redirectLoginActivity();
        }

        @Override
        public void onNotSignedUp() {
            redirectLoginActivity();
        }
    }

    // DB에 회원정보 저장
    private void insertToDatabase(String id, String nickName, String email){

        class InsertData extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }

            @Override
            protected String doInBackground(String... params) {

                try{
                    String id = (String)params[0];
                    String nickName = (String)params[1];
                    String email = (String)params[2];

                    String link="http://leon6095.phps.kr/putdata.php";
                    String data  = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");
                    data += "&" + URLEncoder.encode("nickname", "UTF-8") + "=" + URLEncoder.encode(nickName, "UTF-8");
                    data += "&" + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");

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
        task.execute(id,nickName,email);
    }

    public void gotomap(){

        SharedPreferences pref = getSharedPreferences("login", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("tutorial", true);
        editor.commit();

        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
        finish();
    }

    private class pagerAdapter extends FragmentStatePagerAdapter {

        public pagerAdapter(android.support.v4.app.FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position)
        {
            switch(position)
            {
                case 0:
                    return new ViewpagerFragment1();
                case 1:
                    return new ViewpagerFragment2();
                case 2:
                    return new ViewpagerFragment3();
                default:
                    return null;
            }
        }

        @Override
        public int getCount()
        {
            return 3;
        }
    }


    // 로그아웃 및 탈퇴하기
//    private void onClickLogout() {
//        UserManagement.requestLogout(new LogoutResponseCallback() {
//            @Override
//            public void onCompleteLogout() {
//                requestMe();
//            }
//        });
//    }
//
//    private void onClickUnlink() {
//        final String appendMessage = getString(R.string.com_kakao_confirm_unlink);
//        new AlertDialog.Builder(this)
//                .setMessage(appendMessage)
//                .setPositiveButton(getString(R.string.com_kakao_ok_button),
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                UserManagement.requestUnlink(new UnLinkResponseCallback() {
//                                    @Override
//                                    public void onFailure(ErrorResult errorResult) {
//                                        Logger.e(errorResult.toString());
//                                    }
//
//                                    @Override
//                                    public void onSessionClosed(ErrorResult errorResult) {
//                                        requestMe();
//                                    }
//
//                                    @Override
//                                    public void onNotSignedUp() {
//                                    }
//
//                                    @Override
//                                    public void onSuccess(Long userId) {
//                                        requestMe();
//                                    }
//                                });
//                                dialog.dismiss();
//                            }
//                        })
//                .setNegativeButton(getString(R.string.com_kakao_cancel_button),
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        }).show();
//
//    }

}