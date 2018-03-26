package com.example.android.med_manager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Login activity that is a full screen
 */
public class LoginActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Sets up the layout to the correct view
        setContentView(R.layout.activity_login);
        // finds the image view
        ImageView appLogo = findViewById(R.id.app_logo);
        // Sets up the animation
        Animation fadeInAnim = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeInAnim.reset();
        // Starts the animation
        appLogo.startAnimation(fadeInAnim);

    }
}
