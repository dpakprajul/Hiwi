package hska.mobilegis.com.fernsehturmapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import hska.mobilegis.com.fernsehturmapp.weather.WeatherMainActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button timeSeries, motionGraph, weather, mapView, aboutProj, newMPAGraph, RTTimeSeries;
    Intent intent;
    private Button btn_en, btn_de; //buttons
    private TextView tv_option;
    private Locale myLocale; //Current local application

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_option = findViewById(R.id.tv_select);
        timeSeries = findViewById(R.id.btn_timeSeries);
        motionGraph = findViewById(R.id.btn_motionGraph);
        newMPAGraph = findViewById(R.id.btn_motionGraph_real_time);
        RTTimeSeries = findViewById(R.id.btn_position_real_time);
        weather = findViewById(R.id.btn_weather);
        mapView = findViewById(R.id.btn_map);
        aboutProj = findViewById(R.id.btn_aboutProject);

//        newMPAGraph.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                intent = new Intent(MainActivity.this, MPAAndroid.class);
//                startActivity(intent);
//            }
//        });

        RTTimeSeries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MainActivity.this, FinRealTimeSeries.class);
                startActivity(intent);
            }
        });

        timeSeries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MainActivity.this, FinTimeSeries.class);
                startActivity(intent);
            }
        });
        timeSeries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MainActivity.this, FinTimeSeries.class);
                startActivity(intent);
            }
        });

        motionGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //changed here
                intent = new Intent(MainActivity.this, FinPolygonVisualization.class);
                startActivity(intent);
            }
        });

        newMPAGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //changed here
                intent = new Intent(MainActivity.this, MPAAndroid.class);
                startActivity(intent);
            }
        });

        weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MainActivity.this, WeatherMainActivity.class);
                startActivity(intent);
            }
        });

        mapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri location = Uri.parse("geo:48.755882,9.190184?z=16");
                Intent location_intent = new Intent(Intent.ACTION_VIEW, location);
                startActivity(location_intent);
            }
        });

        aboutProj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MainActivity.this, AboutProject.class);
                startActivity(intent);
            }
        });


        //for language change
        this.btn_en = (Button) findViewById(R.id.btn_en);
        this.btn_de = (Button) findViewById(R.id.btn_de);

        this.btn_en.setOnClickListener(this);
        this.btn_de.setOnClickListener(this);

        loadLocale();
    }


    public void changeToDeutsch(View view) {
        loadLocale();
    }

    public void changeToEnglish(View view) {
        loadLocale();
    }


    /*public void changeLang(Context context, String lang) {  //function to change language
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Language", lang);
        editor.apply();
    }

    @Override
    protected void attachBaseContext(Context newBase) {  //function to change language

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(newBase);
        LANG_CURRENT = preferences.getString("Language", "en");

        super.attachBaseContext(MyContextWrapper.wrap(newBase, LANG_CURRENT));
    }*/ //old change_language code


    //Develop necessary methods to change the language in the application.

    public void changeLang(String lang) //Changing the language in the application:
    {
        if (lang.equalsIgnoreCase(""))
            return;
        myLocale = new Locale(lang);
        saveLocale(lang);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        updateTexts();
    }


    public void saveLocale(String lang) //Save the current locale
    {
        String langPref = "Language";
        SharedPreferences prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(langPref, lang);
        editor.commit();
    }

    public void loadLocale()//Loading a saved locale
    {
        String langPref = "Language";
        SharedPreferences prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        String language = prefs.getString(langPref, "");
        changeLang(language);
    }


    /*Updating the UI elements of the current screen
            (you need to update only the screen in which there is a change of locale):*/
    private void updateTexts() {
        btn_en.setText(R.string.btn_en);
        btn_de.setText(R.string.btn_de);
        timeSeries.setText(R.string.spectral_visualization);
        motionGraph.setText(R.string.circular_motion_of_tower);
        weather.setText(R.string.weather_condition);
        mapView.setText(R.string.maps);
        aboutProj.setText(R.string.about_project);
        tv_option.setText(R.string.select_option);
    }

    /*Add events to the buttons. For this implement OnClickListener interface for our Activity (implements OnClickListener).
    And implement the method onClick().*/
    public void onClick(View v) {
        String lang = "en";
        switch (v.getId()) {
            case R.id.btn_en:
                lang = "en";
                break;
            case R.id.btn_de:
                lang = "de";
                break;
            default:
                break;
        }
        changeLang(lang);
    }

    @Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (myLocale != null) {
            newConfig.locale = myLocale;
            Locale.setDefault(myLocale);
            getBaseContext().getResources().updateConfiguration(newConfig, getBaseContext().getResources().getDisplayMetrics());
        }
    }

}

