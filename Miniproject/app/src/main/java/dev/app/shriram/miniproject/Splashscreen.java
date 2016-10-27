package dev.app.shriram.miniproject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

/**
 * Created by pshri on 4/23/2016.
 */
public class Splashscreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final int check = prefs.getInt("Login_Check", 0);
        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    if (check == 0) {
                        Intent intent = new Intent(Splashscreen.this, Signin.class);
                        startActivity(intent);
                    } else if (check == 1) {
                        Intent intent = new Intent(Splashscreen.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(Splashscreen.this, MainActivityR.class);
                        startActivity(intent);
                    }
                }
            }
        };
        timerThread.start();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }
}
