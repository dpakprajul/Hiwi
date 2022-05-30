package hska.mobilegis.com.fernsehturmapp;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.niwattep.materialslidedatepicker.SlideDatePickerDialog;
import com.niwattep.materialslidedatepicker.SlideDatePickerDialogCallback;

import org.apache.commons.net.ftp.FTPClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class FinTimeSeries extends AppCompatActivity implements SlideDatePickerDialogCallback {

    public String objectType;
    public String date;
    public String startTime;
    public String endTime;
    public String dateTime, time;
    public String message;
    public String message2;
    public String minsec=null;

    Button load_file_from_server;
    EditText current_time;
    public String endTime1;
    TextView button;
    public TextView selectDate;
    public String sCertDate;
    private SimpleDateFormat defaultDate;

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
        setContentView(R.layout.activity_time_series_visualization);

        load_file_from_server = (Button) findViewById(R.id.load_file_from_server);
        positionTimeSeries = (GraphView) findViewById(R.id.positiontimeseries);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences sharedPreferences1 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        current_time = findViewById(R.id.et_currentDateTime);
        easting = findViewById(R.id.rb_easterns);
        northing = findViewById(R.id.rb_northerns);
        EditText startTime = findViewById(R.id.start_time_input);
        EditText endTime = findViewById(R.id.end_time_input);

        final TextView timeTV = findViewById(R.id.time_text_view);
        final TextView timeTVend = findViewById(R.id.time_text_view_end);
        selectDate = (TextView) findViewById(R.id.dateselection);
        // button and text view called using id
        button = (TextView) findViewById(R.id.button);

        //when the date is not selected, send the current date by default
        defaultDate = new SimpleDateFormat("dd.MM.yyyy"); //dd-MM-yyyy
        defaultDate.setTimeZone(TimeZone.getTimeZone("GMT"));
        sCertDate = defaultDate.format(new Date());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar endDate = Calendar.getInstance();
                Calendar startDate= Calendar.getInstance();
                SimpleDateFormat formatter = new SimpleDateFormat("MM");
                String dateString = formatter.format(new Date());
                System.out.println(dateString);
                startDate.set(Calendar.MONTH, Integer.parseInt(dateString)-2);

                long time= System.currentTimeMillis();

                //endDate.set(Calendar.YEAR, 2040);
                SlideDatePickerDialog.Builder builder = new SlideDatePickerDialog.Builder();
                //builder.setEndDate(endDate);
                builder.setStartDate(startDate);

                SlideDatePickerDialog dialog = builder.build();
                dialog.show(getSupportFragmentManager(), "Dialog");


            }
        });

        timeTV.setOnClickListener(new View.OnClickListener() {



            @Override
            public void onClick(View v) {

                View view = View.inflate(FinTimeSeries.this, R.layout.timedialog, null);
                NumberPicker numberPickerHour = view.findViewById(R.id.numpicker_hours);

                //numberPickerHour.setDisplayedValues(null);
                numberPickerHour.setMaxValue(23);
                numberPickerHour.setValue(sharedPreferences.getInt("Hours", 0));
                NumberPicker numberPickerMinutes = view.findViewById(R.id.numpicker_minutes);
                numberPickerMinutes.setMaxValue(59);
                //numberPickerMinutes.setDisplayedValues(null);
                numberPickerMinutes.setValue(sharedPreferences.getInt("Minutes", 0));
                NumberPicker numberPickerSeconds = view.findViewById(R.id.numpicker_seconds);
                numberPickerSeconds.setMaxValue(59);
                //numberPickerSeconds.setDisplayedValues(null);
                numberPickerSeconds.setValue(sharedPreferences.getInt("Seconds", 0));
                Button cancel = view.findViewById(R.id.cancel);
                Button ok = view.findViewById(R.id.ok);
                Button ok1 = view.findViewById(R.id.ok);
                //minsec = String.format("%1$02d:%2$02d:%3$02d",numberPickerHour.getValue(),numberPickerMinutes.getValue(),numberPickerSeconds.getValue());
                //Log.d("output1", minsec);


                AlertDialog.Builder builder = new AlertDialog.Builder(FinTimeSeries.this);
                builder.setView(view);
                final AlertDialog alertDialog = builder.create();
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                ok.setOnClickListener(v1 -> {
                    timeTV.setText(numberPickerHour.getValue() + ":" + numberPickerMinutes.getValue() + ":" + numberPickerSeconds.getValue());
                    timeTV.setText(String.format("%1$02d:%2$02d:%3$02d", numberPickerHour.getValue(), numberPickerMinutes.getValue(), numberPickerSeconds.getValue()));
                    message= timeTV.getText().toString().trim();
                    Log.d("message", message);
                    outData(message);
                    //startActivity(i);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("Hours", numberPickerHour.getValue());
                    editor.putInt("Minutes", numberPickerMinutes.getValue());
                    editor.putInt("Seconds", numberPickerSeconds.getValue());
                    editor.apply();
                    alertDialog.dismiss();
                });
                // Log.d("output", minsec);

                // Log.d("message", message);
                alertDialog.show();

            }



        });

        timeTVend.setOnClickListener(v -> {

            View viewend = View.inflate(FinTimeSeries.this, R.layout.timedialog_end, null);

            NumberPicker numberPickerHourend = viewend.findViewById(R.id.numpicker_hours);
            numberPickerHourend.setDisplayedValues(null);
            numberPickerHourend.setMaxValue(23);
            numberPickerHourend.setValue(sharedPreferences1.getInt("Hours", 0));
            NumberPicker numberPickerMinutesend = viewend.findViewById(R.id.numpicker_minutes);
            numberPickerMinutesend.setDisplayedValues(null);
            numberPickerMinutesend.setMaxValue(59);
            numberPickerMinutesend.setValue(sharedPreferences1.getInt("Minutes", 0));
            NumberPicker numberPickerSecondsend = viewend.findViewById(R.id.numpicker_seconds);
            numberPickerMinutesend.setDisplayedValues(null);
            numberPickerSecondsend.setMaxValue(59);
            numberPickerSecondsend.setValue(sharedPreferences1.getInt("Seconds", 0));
            Button cancel = viewend.findViewById(R.id.cancel);
            Button ok1 = viewend.findViewById(R.id.ok);
            endTime1 = String.format("%1$02d:%2$02d:%3$02d",numberPickerHourend.getValue(),numberPickerMinutesend.getValue(),numberPickerSecondsend.getValue());
            //System.out.println(endTime1);


            AlertDialog.Builder builder1 = new AlertDialog.Builder(FinTimeSeries.this);
            builder1.setView(viewend);
            final AlertDialog alertDialog1 = builder1.create();
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v2) {
                    alertDialog1.dismiss();
                }
            });
            ok1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v2) {

                    timeTVend.setText(numberPickerHourend.getValue() + ":" + numberPickerMinutesend.getValue() + ":" + numberPickerSecondsend.getValue());
                    timeTVend.setText(String.format("%1$02d:%2$02d:%3$02d", numberPickerHourend.getValue(), numberPickerMinutesend.getValue(), numberPickerSecondsend.getValue()));
                    message2= timeTVend.getText().toString().trim();
                    System.out.println("message2"+ message2);

                    SharedPreferences.Editor editorend = sharedPreferences1.edit();
                    editorend.putInt("Hours", numberPickerHourend.getValue());
                    editorend.putInt("Minutes", numberPickerMinutesend.getValue());
                    editorend.putInt("Seconds", numberPickerSecondsend.getValue());
                    editorend.apply();
                    alertDialog1.dismiss();
                    Log.d("output2", endTime1);
                }
            });



            alertDialog1.show();


        });



        //date Spinner
        Spinner objectSpinner = (Spinner) findViewById(R.id.objects_filter);

        //apply font
        ArrayAdapter<String> objectAdapter= new ArrayAdapter<String>(FinTimeSeries.this, //adapter
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
//        startTime.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                handleTimeInput(startTime, startTime.getText());
//                System.out.println("Start Time:"+startTime);
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });
//
//        //End Time
//        endTime.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                handleTimeInput(endTime, endTime.getText());
//                System.out.println("End Time:"+endTime);
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });

        //On screen date and time
        sdf_date = new SimpleDateFormat("dd.MM.yyyy"); //dd-MM-yyyy
        sdf_date.setTimeZone(TimeZone.getTimeZone("GMT"));
        dateTime = sdf_date.format(new Date());

        sdf_time = new SimpleDateFormat("dd.MM.yyyy  HH:mm:ss"); //hh:mm:ss
        sdf_time.setTimeZone(TimeZone.getTimeZone("GMT"));
        time = sdf_time.format(new Date());
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
        day = this.sCertDate.substring(0,2);
        month = this.sCertDate.substring(3,5);
        year = this.sCertDate.substring(8);
        fileName = year+month+day+".fin";
        //System.out.println("Time Series filename: "+fileName);
        new FinTimeSeriesAsync(this).execute(fileName);
    }

    //Timer implementation for continuous data loading on the screen
    public void startTimer(){
        timer=new Timer();
        initializeTimerTask();
        timer.schedule(timerTask, 20000, 20000);
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
       // startTimer();




        String day, month, year, fileName;
        day = dateTime.substring(0,2);
        month = dateTime.substring(3,5);
        year = dateTime.substring(8);
        fileName = year+month+day+".fin";
        //new FinTimeSeriesAsync(this).execute(fileName);
    }
    public String outData(String message) {

        return message;
    }

    public String endTimeValue(){
        return endTime1;

    }

    @Override
    public void onPositiveClick(int i, int i1, int i2, @NonNull Calendar calendar) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());


        String date = format.format(Calendar.getInstance().getTime());
        System.out.println(date);
        button.setText(format.format(calendar.getTime()));
        //button.setTextColor(Color.parseColor("#009688"));
        sCertDate = format.format(calendar.getTime());
    }
}
