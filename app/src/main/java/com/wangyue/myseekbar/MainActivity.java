package com.wangyue.myseekbar;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private MySeekBar mySeekBar;
    private View viewColor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mySeekBar = findViewById(R.id.my_seek_bar);
        viewColor = findViewById(R.id.view_color);

        mySeekBar.setOnChangePositionListener(new MySeekBar.OnChangePositionListener() {
            @Override
            public void onChangePosition(int position) {
                viewColor.setBackgroundColor(mySeekBar.getHsvColor());
                Log.i("position",position + "");
            }
        });

    }

    public void changLeftColor(View view){
        int color = Color.parseColor("#FFFF00FF");
        mySeekBar.setHsvColor(color);
    }

    public void changRightColor(View view){
        int color = Color.parseColor("#FFFFFF00");
        mySeekBar.setHsvColor(color);
    }

}