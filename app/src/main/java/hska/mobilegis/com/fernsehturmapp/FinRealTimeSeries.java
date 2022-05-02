package hska.mobilegis.com.fernsehturmapp;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.apache.commons.net.ftp.FTPClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class FinRealTimeSeries extends AppCompatActivity {

    public String objectType;
    public String date;
    public String startTime;
    public String endTime;
    public String dateTime, time;

    Button load_file_from_server;
    EditText current_time;

    Timer timer;
    TimerTask timerTask;
    final Handler myHandler = new Handler();

    RadioButton easting, northing;

    public List<DataPoint> list = new ArrayList<DataPoint>();
    GraphView positionTimeSeries;
    public LineGraphSeries<DataPoint> series1, series2, series3;

    SimpleDateFormat sdf_date, sdf_time;
    FTPClient ftpClient = new FTPClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_series_real_time);

        load_file_from_server = (Button) findViewById(R.id.load_file_from_server);
        positionTimeSeries = (GraphView) findViewById(R.id.positiontimeseries);
        current_time = findViewById(R.id.et_currentDateTime);
        easting = findViewById(R.id.rb_easterns);
        northing = findViewById(R.id.rb_northerns);
        EditText startTime = findViewById(R.id.start_time_input);
        EditText endTime = findViewById(R.id.end_time_input);

        //date Spinner
        Spinner objectSpinner = (Spinner) findViewById(R.id.objects_filter);

        //apply font
        ArrayAdapter<String> objectAdapter= new ArrayAdapter<String>(FinRealTimeSeries.this, //adapter
                R.layout.simple_expandable_list_item_1, getResources().getStringArray(R.array.objects)) {
        };

        objectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        objectSpinner.setAdapter(objectAdapter);

        objectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                doSomething(objectSpinner.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        //Start Time
        startTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                handleTimeInput(startTime, startTime.getText());
                System.out.println("Start Time:"+startTime);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //End Time
        endTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                handleTimeInput(endTime, endTime.getText());
                System.out.println("End Time:"+endTime);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //On screen date and time
        sdf_date = new SimpleDateFormat("dd.MM.yyyy"); //dd-MM-yyyy
        sdf_date.setTimeZone(TimeZone.getTimeZone("GMT"));
        dateTime = sdf_date.format(new Date());

        sdf_time = new SimpleDateFormat("dd.MM.yyyy  HH:mm:ss"); //hh:mm:ss
        sdf_time.setTimeZone(TimeZone.getTimeZone("GMT"));
        time = sdf_time.format(new Date());
        //current_date.setText(dateTime);
        current_time.setText(time);

        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateScreenDateAndTime();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        t.start();

        series1 = new LineGraphSeries<>();
        series2 = new LineGraphSeries<>();
        series3 = new LineGraphSeries<>();
        series1.setColor(Color.BLUE);
        series1.setThickness(5); // set the thickness

        series3.setColor(Color.GREEN);//Green
        series3.setThickness(5);

        positionTimeSeries.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    int hours = (int) (value/3600);
                    int minutes = (int)((value%3600)/60);
                    int seconds = (int)(value%60);

                    if(seconds>=60)
                    {
                        seconds = seconds-60;
                    }

                    if(minutes>=60)
                    {
                        minutes=minutes-60;
                    }

                    return super.formatLabel(hours,isValueX)+":"+minutes+":"+seconds;

                } else {
                    return super.formatLabel(value, isValueX)+" m";
                }
            }
        });

        positionTimeSeries.getViewport().setXAxisBoundsManual(false);
        positionTimeSeries.getViewport().setMinX(0);
        positionTimeSeries.getViewport().setMaxX(200);
        positionTimeSeries.getViewport().setScalable(true);
        positionTimeSeries.getViewport().setScrollable(true);
        positionTimeSeries.getViewport().setScalableY(true);
        positionTimeSeries.getViewport().setScrollableY(true);
        positionTimeSeries.getViewport().scrollToEnd();
        positionTimeSeries.addSeries(series1);

        //Button onClickListener
        load_file_from_server.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finFileDataRecordReader();
            }
        });

        //Timer for continuous data loading on the screen
        //startTimer();
    }

    //##############################################################################

    private void updateScreenDateAndTime(){
        sdf_time = new SimpleDateFormat("dd.MM.yyyy  HH:mm:ss"); //hh:mm:ss
        sdf_time.setTimeZone(TimeZone.getTimeZone("GMT"));
        time = sdf_time.format(new Date());
        //current_date.setText(dateTime);
        current_time.setText(time);
    }

    private void doSomething(Object o) {
        this.objectType = (String) o;
    }

    private void handleTimeInput(EditText iField, Editable text) {
        if (iField.getHint().toString().equals("Start Time hh:mm:ss")) {
            this.startTime = text.toString();

        } else if (iField.getHint().toString().equals("End Time hh:mm:ss")) {
            this.endTime = text.toString();
        }
    }

    public void setList(List<DataPoint> list) {
        this.list = list;
    }

    public void finFileDataRecordReader() {
        String day, month, year, fileName;
        day = this.dateTime.substring(0,2);
        month = this.dateTime.substring(3,5);
        year = this.dateTime.substring(8);
        fileName = year+month+day+".fin";
        //System.out.println("Time Series filename: "+fileName);
        new FinRealTimeSeriesAsync(this).execute(fileName);
    }

    //Timer implementation for continuous data loading on the screen
    public void startTimer(){
        timer=new Timer();
        initializeTimerTask();
        timer.schedule(timerTask, 1000, 100);
    }

    private void initializeTimerTask(){
        timerTask = new TimerTask() {
            @Override
            public void run() {
                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        finFileDataRecordReader();
                    }
                });
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTimer();





        String day, month, year, fileName;
        day = dateTime.substring(0,2);
        month = dateTime.substring(3,5);
        year = dateTime.substring(8);
        fileName = year+month+day+".fin";
        //new FinTimeSeriesAsync(this).execute(fileName);
    }
}