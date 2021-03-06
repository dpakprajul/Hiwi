package hska.mobilegis.com.fernsehturmapp;

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
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.ZoomEstimator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;


public class FinPolygonVisualization extends AppCompatActivity {

    public String objectType;
    public String startTime;
    public String endTime;
    public String dateTime,time;
    public String minsec;
    SimpleDateFormat sdf_date, sdf_time;
    Button load_file_from_server;
    EditText current_time;



    Timer timer;
    TimerTask timerTask;
    final Handler myHandler = new Handler();

    public List<XyTimePlot> list = new ArrayList<XyTimePlot>();
    public XYPlot plot;
    XYSeries series1 = null;
    //PanZoom panZoom;
    PanZoomCustomization panZoom1;
    FinPolygonVisualizationAsync finPolygonVisualizationAsync;
    private Object a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fin_polygon_visualization);

        load_file_from_server = (Button) findViewById(R.id.load_file_from_server);
        current_time = findViewById(R.id.et_currentDateTime);
        EditText startTime = findViewById(R.id.start_time_input);
        EditText endTime = findViewById(R.id.end_time_input);

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final TextView timeTV = findViewById(R.id.time_text_view);
        timeTV.setOnClickListener(new View.OnClickListener() {
            private String minsecout;

            @Override
            public void onClick(View v) {
                View view = View.inflate(FinPolygonVisualization.this, R.layout.timedialog, null);
                final NumberPicker numberPickerHour = view.findViewById(R.id.numpicker_hours);


                numberPickerHour.setMaxValue(23);
                numberPickerHour.setValue(sharedPreferences.getInt("Hours", 0));
                final NumberPicker numberPickerMinutes = view.findViewById(R.id.numpicker_minutes);
                numberPickerMinutes.setMaxValue(59);
                numberPickerMinutes.setValue(sharedPreferences.getInt("Minutes", 0));
                final NumberPicker numberPickerSeconds = view.findViewById(R.id.numpicker_seconds);
                numberPickerSeconds.setMaxValue(59);
                numberPickerSeconds.setValue(sharedPreferences.getInt("Seconds", 0));
                Button cancel = view.findViewById(R.id.cancel);
                Button ok = view.findViewById(R.id.ok);
                String minsec = String.format("%1$02d:%2$02d:%3$02d",numberPickerHour.getValue(),numberPickerMinutes.getValue(),numberPickerSeconds.getValue());



                AlertDialog.Builder builder = new AlertDialog.Builder(FinPolygonVisualization.this);
                builder.setView(view);
                final AlertDialog alertDialog = builder.create();
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        timeTV.setText(numberPickerHour.getValue() + ":" + numberPickerMinutes.getValue() + ":" + numberPickerSeconds.getValue());
                        timeTV.setText(String.format("%1$02d:%2$02d:%3$02d", numberPickerHour.getValue(), numberPickerMinutes.getValue(), numberPickerSeconds.getValue()));
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("Hours", numberPickerHour.getValue());
                        editor.putInt("Minutes", numberPickerMinutes.getValue());
                        editor.putInt("Seconds", numberPickerSeconds.getValue());
                        editor.apply();
                        alertDialog.dismiss();
                    }
                });
                Log.d("output", minsec);


                alertDialog.show();

            }

String outData(String minsecout){
    return minsec;

}

        });
//Log.d("output1",minsec);


        // initialize XYPlot reference:
        plot = (XYPlot) findViewById(R.id.plot);

        /*Pan Zoom Enable/Disable
        //panZoom1.attach(plot);
        plot.setDomainBoundaries(32513977.95, 32513978.12, BoundaryMode.FIXED);
        plot.setRangeBoundaries(5400318.30, 5400318.42, BoundaryMode.FIXED);


        plot.getOuterLimits().set(32513978.00, 32513978.12, 5400318.30, 5400319.42);
        plot.getInnerLimits().set(32513978.00,32513978.12, 5400318.30, 5400318.45);
        //plot.getRegistry().setEstimator(new ZoomEstimator());
         */

        plot.setDomainBoundaries(32513977.00, 32513977.42, BoundaryMode.AUTO);
        plot.setRangeBoundaries(5401318.30, 5401318.42, BoundaryMode.AUTO);
        plot.getOuterLimits().set(32513977.00, 32513990.12, 5400318.30, 5400330.42);
        plot.getInnerLimits().set(32513976.00,32513981.12, 5400316.30, 5400323.45);
        plot.getRegistry().setEstimator(new ZoomEstimator());

        plot.getGraph().getDomainOriginLinePaint().setColor(Color.LTGRAY);
        plot.getGraph().getRangeOriginLinePaint().setColor(Color.LTGRAY);
        plot.getGraph().setMarginBottom(10);
        plot.getGraph().setMarginTop(205);
        plot.getGraph().setMarginLeft(10);
        plot.getGraph().setMarginRight(230);
        plot.getLegend().setVisible(false);

        PanZoom.attach(plot);

// PanZoom.attach(plot);
 plot.setRangeStep(StepMode.INCREMENT_BY_VAL, 0.09);
 plot.setDomainStep(StepMode.INCREMENT_BY_VAL, 0.9);
//plot.setDomainBoundaries(0, 3000000, BoundaryMode.FIXED);
//plot.setRangeBoundaries(0,300000, BoundaryMode.FIXED);


        //Object Type Spinner
        Spinner objectSpinner = (Spinner) findViewById(R.id.objects_filter);

        //apply font
        ArrayAdapter<String> objectAdapter= new ArrayAdapter<String>(FinPolygonVisualization.this, //adapter
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
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //On screen date and time
        sdf_date = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY); //dd-MM-yyyy
        sdf_date.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
        dateTime = sdf_date.format(new Date());

        sdf_time = new SimpleDateFormat("dd.MM.yyyy  HH:mm:ss", Locale.GERMANY); //hh:mm:ss
        sdf_time.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
        time = sdf_time.format(new Date());
        //current_date.setText(dateTime);
        current_time.setText(time);

        //Updating Time and Date continuously
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

    //###############################################################
    private void updateScreenDateAndTime(){
        sdf_time = new SimpleDateFormat("dd.MM.yyyy  HH:mm:ss", Locale.GERMANY); //hh:mm:ss
        sdf_time.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
        time = sdf_time.format(new Date());
        current_time.setText(time);
    }

    private void doSomething(Object o) {
        this.objectType = (String) o;
    }

    private void handleTimeInput(EditText iField, Editable text) {
        if (iField.getHint().toString().equals("Start Time hh:mm:ss")) {
            this.minsec = text.toString();
        } else if (iField.getHint().toString().equals("End Time hh:mm:ss")) {
            this.endTime = text.toString();
        }
    }

    public void setList(List<XyTimePlot> list) {
        this.list = list;
    }

    public void finFileDataRecordReader() {
        String day, month, year, fileName;
        day = this.dateTime.substring(0,2);
        month = this.dateTime.substring(3,5);
        year = this.dateTime.substring(8);
        fileName = year+month+day+".fin";
        //System.out.println("date time: "+fileName);
        new FinPolygonVisualizationAsync(this).execute(fileName);
    }

    //Timer implementation for continuous data loading on the screen
    public void startTimer(){
        timer=new Timer();
        initializeTimerTask();
        timer.schedule(timerTask, 10000, 10000);
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
        plot.refreshDrawableState();

        /*
        String day, month, year, fileName;
        day = dateTime.substring(0,2);
        month = dateTime.substring(3,5);
        year = dateTime.substring(8);
        fileName = year+month+day+".fin";
        //new FinPolygonVisualizationAsync(this).execute(fileName);
        */

    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
//        bundle.putSerializable("todo", panZoom1.getState());
    }

    @Override
    public void onRestoreInstanceState(Bundle bundle) {
       // PanZoomCustomization.State state = (PanZoomCustomization.State) bundle.getSerializable("todo");
        //panZoom1.setState(state);
        plot.redraw();
    }

    public String outData() {
        return minsec;
    }
}
