package com.autel.sdksample.evo.avoid;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.ImageView;

import com.autel.sdksample.R;

/**
 * Created by A15387 on 2017/7/24.
 */

public class AvoidActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    private AutelAvoidView avoidView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avoid);
        avoidView = new AutelAvoidView();
        avoidView.setAvoidImg(AutelAvoidView.STATE_ONE,(ImageView) findViewById(R.id.avoid_img_1));
        avoidView.setAvoidImg(AutelAvoidView.STATE_ONE,(ImageView) findViewById(R.id.avoid_img_2));
        avoidView.setAvoidImg(AutelAvoidView.STATE_ONE,(ImageView) findViewById(R.id.avoid_img_3));
        avoidView.setAvoidImg(AutelAvoidView.STATE_ONE,(ImageView) findViewById(R.id.avoid_img_4));
    }
}
