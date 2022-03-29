package hska.mobilegis.com.fernsehturmapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import hska.mobilegis.com.fernsehturmapp.weather.WeatherMainActivity;


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, WeatherMainActivity.class);
        startActivity(intent);
        finish();
    }
}