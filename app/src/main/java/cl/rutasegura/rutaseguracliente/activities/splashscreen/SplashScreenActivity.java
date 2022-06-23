package cl.rutasegura.rutaseguracliente.activities.splashscreen;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import cl.rutasegura.rutaseguracliente.activities.login.LoginActivity;
import cl.rutasegura.rutaseguracliente.activities.maps.MapsActivity;
import cl.rutasegura.rutaseguracliente.provider.AuthProvider;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {
    private AuthProvider mAuthProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuthProvider = new AuthProvider();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN , WindowManager.LayoutParams.FLAG_FULLSCREEN);
       VerifyUser();

    }

    private void VerifyUser() {
        if (mAuthProvider.existSession()){
            gotoMapsActivty();
        }else {
            startTimer();
        }
    }

    private void gotoMapsActivty() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this , MapsActivity.class);
                startActivity(intent);
                finish();


            }
        } , 2000);
    }


    private void startTimer() {
        //duration splash screen
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                    Intent intent = new Intent(SplashScreenActivity.this , LoginActivity.class);
                    startActivity(intent);
                    finish();


            }
        } , 2000);
    }
}