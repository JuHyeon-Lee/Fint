package com.example.leon6.fint;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class ViewpagerFragment1 extends android.support.v4.app.Fragment{

    public ViewpagerFragment1()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.viewpager_page1, container, false);
        return layout;
    }

}
