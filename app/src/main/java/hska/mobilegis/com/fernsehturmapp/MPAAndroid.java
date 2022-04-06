package hska.mobilegis.com.fernsehturmapp;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MPAAndroid extends DemoBase implements OnChartValueSelectedListener {
    public LineChart chart;
    public List<Number> xLists;
    public List<Number> yLists;

    public String objectType;
    public String startTime;
    public String endTime;
    public String dateTime,time;
    public String minsec;
    SimpleDateFormat sdf_date, sdf_time;
    Button load_file_from_server;
    EditText current_time;
    private MPAAndroid activityMP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_linechart_noseekbar);

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
                View view = View.inflate(MPAAndroid.this, R.layout.timedialog, null);
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
                minsec = String.format("%1$02d:%2$02d:%3$02d",numberPickerHour.getValue(),numberPickerMinutes.getValue(),numberPickerSeconds.getValue());

                AlertDialog.Builder builder = new AlertDialog.Builder(MPAAndroid.this);
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

        });
//Log.d("output1",minsec);

        //Object Type Spinner
        Spinner objectSpinner = (Spinner) findViewById(R.id.objects_filter);

        //apply font
        ArrayAdapter<String> objectAdapter= new ArrayAdapter<String>(MPAAndroid.this, //adapter
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
                // activita.addEntry();
                //TODO also execute addEntry on Update Graph
            }
        });



        setTitle("DynamicalAddingActivity");
        chart = findViewById(R.id.chart1);
        chart.setOnChartValueSelectedListener(this);
        chart.setDrawGridBackground(false);
        chart.getDescription().setEnabled(false);
        chart.setNoDataText("No chart data available. Use the menu to add entries and data sets!");

//        chart.getXAxis().setDrawLabels(false);
//        chart.getXAxis().setDrawGridLines(false);

    }

    String outData(String minsecout){
        return minsec;
    }

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

    public void finFileDataRecordReader() {
        String day, month, year, fileName;
        day = this.dateTime.substring(0,2);
        month = this.dateTime.substring(3,5);
        year = this.dateTime.substring(8);
        fileName = year+month+day+".fin";
        //System.out.println("date time: "+fileName);
        new MPAAndroidAsync(this).execute(fileName);
    }

    public List<Number> setXValueLists(List<Number> xValues) {
        xLists = xValues;
        System.out.println("X List values inside finVisualize " +xLists.size());
        return xLists;
    }
    public List<Number> setYValueLists(List<Number> yValues) {
        yLists = yValues;
        System.out.println("Y List values inside finVisualize " +yLists.size());
        return yLists;
    }

    private final int[] colors = ColorTemplate.VORDIPLOM_COLORS;

    public void addEntry() {

        LineData data = chart.getData();

        if (data == null) {
            data = new LineData();
            chart.setData(data);
        }

        ILineDataSet set = data.getDataSetByIndex(0);
        // set.addEntry(...); // can be called as well

        if (set == null) {
            set = createSet();
            data.addDataSet(set);
        }

        // choose a random dataSet
        System.out.println("X List values" +xLists);
        System.out.println("Y List values" +yLists);

        //TODO getting the list until the length of the list and import it into the randomDataSetIndex

        int randomDataSetIndex = (int) (Math.random() * data.getDataSetCount());
        ILineDataSet randomSet = data.getDataSetByIndex(randomDataSetIndex);
        float value = (float) (Math.random() * 0.05) + 0.005f * (randomDataSetIndex + 1);


        data.addEntry(new Entry(randomSet.getEntryCount(), value), randomDataSetIndex);
        data.notifyDataChanged();


        // let the chart know it's data has changed
        chart.notifyDataSetChanged();

        chart.setVisibleXRangeMaximum(6);
        //chart.setVisibleYRangeMaximum(15, AxisDependency.LEFT);
//
//            // this automatically refreshes the chart (calls invalidate())
        chart.moveViewTo(data.getEntryCount() - 7, 50f, YAxis.AxisDependency.LEFT);

    }

    private void removeLastEntry() {

        LineData data = chart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);

            if (set != null) {

                Entry e = set.getEntryForXValue(set.getEntryCount() - 1, Float.NaN);

                data.removeEntry(e, 0);
                // or remove by index
                // mData.removeEntryByXValue(xIndex, dataSetIndex);
                data.notifyDataChanged();
                chart.notifyDataSetChanged();
                chart.invalidate();
            }
        }
    }

    private void addDataSet() {

        LineData data = chart.getData();

        if (data == null) {
            chart.setData(new LineData());
        } else {

            int count = (data.getDataSetCount() + 1);
            int amount = data.getDataSetByIndex(0).getEntryCount();

            ArrayList<Entry> values = new ArrayList<>();

            for (int i = 0; i < amount; i++) {
                values.add(new Entry(i, (float) (Math.random() * 50f) + 50f * count));
            }

            LineDataSet set = new LineDataSet(values, "DataSet " + count);
            set.setLineWidth(2.5f);
            set.setCircleRadius(4.5f);

            int color = colors[count % colors.length];

            set.setColor(color);
            set.setCircleColor(color);
            set.setHighLightColor(color);
            set.setValueTextSize(10f);
            set.setValueTextColor(color);

            data.addDataSet(set);
            data.notifyDataChanged();
            chart.notifyDataSetChanged();
            chart.invalidate();
        }
    }

    private void removeDataSet() {

        LineData data = chart.getData();

        if (data != null) {

            data.removeDataSet(data.getDataSetByIndex(data.getDataSetCount() - 1));

            chart.notifyDataSetChanged();
            chart.invalidate();
        }
    }

    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "DataSet 1");
        set.setLineWidth(2.5f);
        set.setCircleRadius(4.5f);
        set.setColor(Color.rgb(240, 99, 99));
        set.setCircleColor(Color.rgb(240, 99, 99));
        set.setHighLightColor(Color.rgb(190, 190, 190));
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setValueTextSize(10f);

        return set;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected() {}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dynamical, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.viewGithub: {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://www.facebook.com/deepusanta"));
                startActivity(i);
                break;
            }
            case R.id.actionAddEntry: {
                addEntry();
                Toast.makeText(this, "Entry added!", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.actionRemoveEntry: {
                removeLastEntry();
                Toast.makeText(this, "Entry removed!", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.actionAddDataSet: {
                addDataSet();
                Toast.makeText(this, "DataSet added!", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.actionRemoveDataSet: {
                removeDataSet();
                Toast.makeText(this, "DataSet removed!", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.actionClear: {
                chart.clear();
                Toast.makeText(this, "Chart cleared!", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.actionSave: {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    saveToGallery();
                } else {
                    requestStoragePermission(chart);
                }
                break;
            }
        }

        return true;
    }

    @Override
    protected void saveToGallery() {
        saveToGallery(chart, "DynamicalAddingActivity");
    }
}
