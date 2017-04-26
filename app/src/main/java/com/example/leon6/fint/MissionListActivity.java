package com.example.leon6.fint;

import android.app.Activity;
import android.os.Bundle;

public class MissionListActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_missionlist);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
