package com.rhodes.demo.activity;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.rhodes.demo.R;

/**
 * Created by xiet on 2016/4/11.
 */
public class AnimationActivity extends ActionBarActivity {

    ImageView iv;
    Button    btn;

    AnimationDrawable drawable;
    boolean isPlaying = true;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setContentView(R.layout.activity_animation);

        iv = (ImageView) findViewById(R.id.image);
        btn = (Button) findViewById(R.id.button);

        iv.setImageResource(R.drawable.loading_animation);
        drawable = (AnimationDrawable) iv.getDrawable();

        doPlay();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPlay();
            }
        });
    }

    void doPlay() {
        isPlaying = !isPlaying;

        if (isPlaying) {
            btn.setText("START");
            drawable.stop();
        } else {
            btn.setText("STOP");
            drawable.start();
        }
    }
}
