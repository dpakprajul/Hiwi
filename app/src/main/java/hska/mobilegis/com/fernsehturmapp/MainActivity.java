package hska.mobilegis.com.fernsehturmapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;

import java.util.Locale;

import hska.mobilegis.com.fernsehturmapp.weather.WeatherMainActivity;
import hska.mobilegis.com.fernsehturmapp.weather.WeatherRecyclerAdapter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button timeSeries, motionGraph, weather, mapView, aboutProj, newMPAGraph, RTTimeSeries;
    Intent intent;
    private Button btn_en, btn_de; //buttons
    private TextView tv_option;
    private Locale myLocale; //Current local application
//    private View help;


    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.dynamical, menu);

        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        }
        if(menu instanceof MenuBuilder){
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }

        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        int item_id= item.getItemId();
        if(item_id==R.id.checkWeather){
            //Toast.makeText(this, "This is weather", Toast.LENGTH_SHORT).show();
           Intent intent = new Intent(MainActivity.this, WeatherMainActivity.class);
           startActivity(intent);
        }
        else if(item_id==R.id.map){
                String uri = String.format(Locale.ENGLISH, "https://www.google.com/maps/place/Stuttgart+TV+Tower/@48.755857,9.1879146,17z/data=!4m5!3m4!1s0x4799c4a78c941ea5:0xee74d8b131b9a572!8m2!3d48.755857!4d9.1901086", 12f, 2f, "");
                //Uri location = Uri.parse("geo:48.755882,9.190184?z=16");
                Intent location_intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                //location_intent.setPackage("com.google.android.apps.maps");
                startActivity(location_intent);
        }
        else if(item_id==R.id.about){
            Intent intent = new Intent(MainActivity.this, AboutProject.class);
            startActivity(intent);
        }
        else if(item_id==R.id.help){
            Intent intent = new Intent(MainActivity.this, MyCustomAppIntro.class);
            startActivity(intent);
        }
        else if(item_id==R.id.contact_us){
            Intent intent = new Intent(MainActivity.this, Contactus.class);
            startActivity(intent);
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


       //TODO if full screeen looks good or the default one is good
        setContentView(R.layout.activity_main);
//        //to remove "information bar" above the action bar
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        //to remove the action bar (title bar)
//        getSupportActionBar().hide();

        SharedPreferences preferencesintro = getSharedPreferences("PREFERENCE", MODE_PRIVATE);
        String FirstTime = preferencesintro.getString("FirstTimeInstall","");

        if(FirstTime.equals("YES")){

        }else{
            SharedPreferences.Editor editorintro= preferencesintro.edit();
            editorintro.putString("FirstTimeInstall", "YES");
            editorintro.apply();
            Intent intent1 = new Intent(MainActivity.this, MyCustomAppIntro.class);
            startActivity(intent1);
        }




        tv_option = findViewById(R.id.tv_select);
        timeSeries = findViewById(R.id.btn_timeSeries);
        motionGraph = findViewById(R.id.btn_motionGraph);
        newMPAGraph = findViewById(R.id.btn_motionGraph_real_time);
        RTTimeSeries = findViewById(R.id.btn_position_real_time);
//        weather = findViewById(R.id.btn_weather);
//        mapView = findViewById(R.id.btn_map);
//        aboutProj = findViewById(R.id.btn_aboutProject);
//        help = findViewById(R.id.help);

//        newMPAGraph.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                intent = new Intent(MainActivity.this, MPAAndroid.class);
//                startActivity(intent);
//            }
//        });

//        help.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                intent= new Intent(MainActivity.this, MyCustomAppIntro.class);
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

//        weather.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                intent = new Intent(MainActivity.this, WeatherMainActivity.class);
//                startActivity(intent);
//            }
//        });

//        mapView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                String uri = String.format(Locale.ENGLISH, "https://www.google.com/maps/place/Stuttgart+TV+Tower/@48.755857,9.1879146,17z/data=!4m5!3m4!1s0x4799c4a78c941ea5:0xee74d8b131b9a572!8m2!3d48.755857!4d9.1901086", 12f, 2f, "");
////                //Uri location = Uri.parse("geo:48.755882,9.190184?z=16");
////                Intent location_intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
////                //location_intent.setPackage("com.google.android.apps.maps");
////                startActivity(location_intent);
//                Uri.Builder builder = new Uri.Builder();
//                builder.scheme("https")
//                        .authority("www.google.com")
//                        .appendPath("maps")
//                        .appendPath("dir")
//                        .appendPath("")
//                        .appendQueryParameter("api", "1")
//                        .appendQueryParameter("destination", 48.755882 + "," + 9.1879146);
//                String url = builder.build().toString();
//                Intent i = new Intent(Intent.ACTION_VIEW);
//                i.setData(Uri.parse(url));
//                startActivity(i);
//            }
//        });

//        aboutProj.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                intent = new Intent(MainActivity.this, AboutProject.class);
//                startActivity(intent);
//            }
//        });




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
        //weather.setText(R.string.weather_condition);
        //mapView.setText(R.string.maps);
        //aboutProj.setText(R.string.about_project);
        tv_option.setText(R.string.select_option);
        newMPAGraph.setText(R.string.rt_circular_motion_of_tower);
        RTTimeSeries.setText(R.string.rt_position_of_tower);


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

