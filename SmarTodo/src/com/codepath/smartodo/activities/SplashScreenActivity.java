package com.codepath.smartodo.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.codepath.smartodo.R;
import com.nineoldandroids.animation.ObjectAnimator;

public class SplashScreenActivity extends Activity {
	// Splash screen timer
	private static int SPLASH_TIME_OUT = 3000; // milliseconds

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
		ImageView logo = (ImageView) findViewById(R.id.ivPencilLogo);
		
		Animation logoAnimation = AnimationUtils.loadAnimation(this, R.anim.logo_animation);		
		logo.startAnimation(logoAnimation);
				
		new Handler().postDelayed(new Runnable() {
			/*
			 * Showing splash screen with a timer. 
			 */
			@Override
			public void run() {
				// This method will be executed once the timer is over
				// Start your app main activity
				Intent i = new Intent(SplashScreenActivity.this, LoginActivity.class);
				startActivity(i);
				// close this activity
				finish();
			}
		}, SPLASH_TIME_OUT);
	}
}
