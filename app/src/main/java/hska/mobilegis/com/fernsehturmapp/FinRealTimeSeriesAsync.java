package hska.mobilegis.com.fernsehturmapp;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;

import com.jjoe64.graphview.series.DataPoint;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPCmd;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class FinRealTimeSeriesAsync extends AsyncTask<String, Void, List<DataPoint>> {

    String sHrs, sMins, sSecs;
    String eHrs, eMins, eSecs;
    private FinRealTimeSeries activity;
    private ProgressDialog dialog;

    public FinRealTimeSeriesAsync(FinRealTimeSeries activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(this.activity);
        dialog.setMessage("Please wait...");
        dialog.setIndeterminate(true);
        //dialog.show();
        super.onPreExecute();
    }

    @Override
    protected List<DataPoint> doInBackground(String... strings) {
        String fileName = strings[0];
        FTPClient ftpClient = new FTPClient();
        InputStream inStream = null;
        BufferedReader br = null;
        String line = "";
        String object = null;

        List<DataPoint> dataPoints = new ArrayList<>();
        try {
            ftpClient.connect("212.9.161.90", 21);
            System.out.println(ftpClient.getReplyString());

            ftpClient.login("fern2seh0_Tu2rm0", "tu02moRm$2nito0ri!ng");
            System.out.println(ftpClient.getReplyString());

            ftpClient.enterLocalPassiveMode();
            System.out.println(ftpClient.getReplyString());

            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            System.out.println(ftpClient.getReplyString());

            ftpClient.sendCommand(FTPCmd.CWD, "/raw");
            System.out.println(ftpClient.getReplyString());

            inStream = new BufferedInputStream(ftpClient.retrieveFileStream(fileName));

            br = new BufferedReader(new InputStreamReader(inStream));

            //starting from 0, read only the 4th lines and skip the covariance lines
            int lineNum = 0;

            while ((line = br.readLine()) != null) {
                if((lineNum % 4) > 0) {

                }else {
                    String rowValue = line.trim().replaceAll("\\s+", ",");//trim white spaces at the edges of file, replace white spaces with comma, stores result in rowValue
                    String[] arr = rowValue.split(","); //split rowValue into comma separated values (?), text split into an array of rows
                    if(arr.length != 1){  //if its not ENDE
                        object = arr[0];
                        if(object.equals(activity.objectType)){
                            if(activity.objectType.equals("Turm")){
                                if(activity.easting.isChecked()){
                                    dataPoints.add(new DataPoint(Double.valueOf(arr[4]), Double.valueOf(arr[1])));
                                    activity.series1.setColor(Color.BLUE);
                                }else if(activity.northing.isChecked()){
                                    dataPoints.add(new DataPoint(Double.valueOf(arr[4]), Double.valueOf(arr[2])));
                                    activity.series1.setColor(Color.GREEN);
                                }else{
                                    dataPoints.add(new DataPoint(Double.valueOf(arr[4]), Double.valueOf(arr[1])));
                                    activity.series1.setColor(Color.BLUE);
                                }
                            }else if(activity.objectType.equals("Turm_ubx")){
                                if(activity.easting.isChecked()){
                                    dataPoints.add(new DataPoint(Double.valueOf(arr[4]), Double.valueOf(arr[1])));
                                    activity.series1.setColor(Color.CYAN);
                                }else if(activity.northing.isChecked()){
                                    dataPoints.add(new DataPoint(Double.valueOf(arr[4]), Double.valueOf(arr[2])));
                                    activity.series1.setColor(Color.MAGENTA);
                                }else{
                                    dataPoints.add(new DataPoint(Double.valueOf(arr[4]), Double.valueOf(arr[1])));
                                    activity.series1.setColor(Color.CYAN);
                                }

                            }

                        }
                    }

                }
                lineNum++;
            }
            ftpClient.completePendingCommand();
            inStream.close();
            return dataPoints;
        }catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(ftpClient.isConnected()){
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<DataPoint>str) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        super.onPostExecute(str);
        if(str==null || str.size()==0){
            activity.positionTimeSeries.removeAllSeries();
            AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
            builder.setCancelable(false);
            builder.setTitle(R.string.file_not_found_title);
            if(activity.objectType.equals("Turm")){
                builder.setMessage(R.string.file_not_found_Turm);
            }else{
                builder.setMessage(R.string.file_not_found_Turm_Ubx);
            }
            builder.setInverseBackgroundForced(true);
            builder.setNegativeButton(
                    R.string.response_dialog,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(
                                DialogInterface dialog,
                                int which) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }else {
            activity.setList(str);
            finFileDataRecord(str);
        }
    }
    //Get list of datapoints from general datalist and draw the graph
    public void finFileDataRecord(List<DataPoint>list) {

        String checkValue=activity.objectType;
        if ((activity.startTime != null && !activity.startTime.isEmpty())
                && (activity.endTime != null && !activity.endTime.isEmpty())) {

            // Parse given start time (String) hr, mins and secs    10:11:29
            sHrs = activity.startTime.substring(0,2);
            sMins = activity.startTime.substring(3,5);
            sSecs = activity.startTime.substring(6);

            // Parse given end time (String) hr, mins and secs
            eHrs = activity.endTime.substring(0,2);
            eMins = activity.endTime.substring(3,5);
            eSecs = activity.endTime.substring(6);

            Double startT = Double.valueOf(sHrs) * 3600 + Double.valueOf(sMins) * 60 + Double.valueOf(sSecs);
            Double endT = Double.valueOf(eHrs) * 3600 + Double.valueOf(eMins) * 60 + Double.valueOf(eSecs);

            List<DataPoint> filteredDataPoints = new ArrayList<>();
            for (DataPoint xyT : list) {
                if(Double.valueOf(xyT.getX())>=startT && Double.valueOf(xyT.getX())<=endT){
                    filteredDataPoints.add(xyT);
                }
            }

            /*To convert GPS time to local time
            for (DataPoint xyT : list) {
                SimpleDateFormat sdf_date = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss"); //dd-MM-yyyy
                String dateTime = sdf_date.format(new Date());
                LocalDateTime date = LocalDateTime.now();
                int seconds = date.toLocalTime().toSecondOfDay();
                Double gpsTime = Double.valueOf(xyT.getX());

                //System.out.println("Difference of time: "+ (seconds-gpsTime)/3600);

                Double yVal = Double.valueOf(xyT.getY());
                if(gpsTime>=startT && gpsTime<=endT){
                    DataPoint changedDatapoint = new DataPoint(gpsTime, yVal);
                    filteredDataPoints.add(changedDatapoint);
                }
            }
            */

            if((list.size()!= 0) && (filteredDataPoints.size()<= 0)){
                AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
                builder.setCancelable(false);
                builder.setTitle(R.string.no_datapoints_title);
                if(checkValue.equalsIgnoreCase("Turm")){
                    builder.setMessage(R.string.no_datapoints_Turm);
                }else if(checkValue.equalsIgnoreCase("Turm_ubx")){
                    builder.setMessage(R.string.no_datapoints_Turm_Ubx);
                }
                builder.setInverseBackgroundForced(true);
                builder.setNegativeButton(
                        "Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    DialogInterface dialog,
                                    int which) {
                                dialog.dismiss();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();
            }else{
                DataPoint[] resetDataPoints = (DataPoint[]) filteredDataPoints.toArray(new DataPoint[filteredDataPoints.size()]);
                for(DataPoint dp:filteredDataPoints){
                    activity.series1.resetData(resetDataPoints);
                    //activity.series1.appendData(dp,false, 500000);
                }
                activity.positionTimeSeries.refreshDrawableState();
                activity.positionTimeSeries.addSeries(activity.series1);
            }
        } else {
            SimpleDateFormat sdf_time = new SimpleDateFormat("HH:mm:ss"); //hh:mm:ss
            sdf_time.setTimeZone(TimeZone.getTimeZone("GMT"));;

            activity.startTime = sdf_time.format(new Date(System.currentTimeMillis()- (60)*1000));
            activity.endTime = sdf_time.format(new Date(System.currentTimeMillis()));

            // Parse given start time (String) hr, mins and secs
            sHrs = activity.startTime.substring(0,2);
            sMins = activity.startTime.substring(3,5);
            sSecs = activity.startTime.substring(6);

            // Parse given end time (String) hr, mins and secs
            eHrs = activity.endTime.substring(0,2);
            eMins = activity.endTime.substring(3,5);
            eSecs = activity.endTime.substring(6);

            //Calculate start and end time interval from user input
            Double startT = Double.valueOf(sHrs) * 3600 + Double.valueOf(sMins) * 60 + Double.valueOf(sSecs);
            Double endT = Double.valueOf(eHrs) * 3600 + Double.valueOf(eMins) * 60 + Double.valueOf(eSecs);

            List<DataPoint> filteredDataPoints = new ArrayList<>();

            //Get datapoints from parent list for a specific time interval
            //and add to a new list
            for (DataPoint xyT : list) {
                if(Double.valueOf(xyT.getX())>=startT && Double.valueOf(xyT.getX())<=endT){
                    filteredDataPoints.add(xyT);
                }
            }

            if((list.size()!= 0) && (filteredDataPoints.size()<= 0)){
                AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
                builder.setCancelable(false);
                if(checkValue.equalsIgnoreCase("Turm")){
                    builder.setMessage(R.string.no_datapointsDefault_Turm);
                }else if(checkValue.equalsIgnoreCase("Turm_ubx")){
                    builder.setMessage(R.string.no_datapointsDefault_Turm_Ubx);
                }
                builder.setInverseBackgroundForced(true);
                builder.setNegativeButton(
                        "Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    DialogInterface dialog,
                                    int which) {
                                dialog.dismiss();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();
            }else{
                DataPoint[] resetDataPoints = (DataPoint[]) filteredDataPoints.toArray(new DataPoint[filteredDataPoints.size()]);
                for(DataPoint dp:filteredDataPoints){
                    activity.series1.resetData(resetDataPoints);
                    //activity.series1.appendData(dp,false, 500000);
                }
                activity.positionTimeSeries.refreshDrawableState();
                activity.positionTimeSeries.addSeries(activity.series1);
            }
            activity.startTime = null;
            activity.endTime = null;
        }
    }

    //For multiLine graph
/*
    public void finFileDataRecordEN(List<DataPoint>list) {

        if ((activity.startTime != null && !activity.startTime.isEmpty()) && (activity.endTime != null && !activity.endTime.isEmpty())) {
            String sHrs, sMins, sSecs;
            String eHrs, eMins, eSecs;

            // Parse given start time (String) hr, mins and secs    10:11:29
            sHrs = activity.startTime.substring(0,2);
            sMins = activity.startTime.substring(3,5);
            sSecs = activity.startTime.substring(6);

            // Parse given end time (String) hr, mins and secs
            eHrs = activity.endTime.substring(0,2);
            eMins = activity.endTime.substring(3,5);
            eSecs = activity.endTime.substring(6);

            Double startT = Double.valueOf(sHrs) * 3600 + Double.valueOf(sMins) * 60 + Double.valueOf(sSecs);
            Double endT = Double.valueOf(eHrs) * 3600 + Double.valueOf(eMins) * 60 + Double.valueOf(eSecs);

            List<DataPoint> filteredDataPoints = new ArrayList<>();

            for (DataPoint xyT : list) {
                if(Double.valueOf(xyT.getX())>=startT && Double.valueOf(xyT.getX())<=endT){
                    filteredDataPoints.add(xyT);
                }
            }
            DataPoint[] resetDataPoints = (DataPoint[]) filteredDataPoints.toArray(new DataPoint[filteredDataPoints.size()]);

            for(DataPoint dp:filteredDataPoints){
                activity.series1.resetData(resetDataPoints);
                //activity.series3.resetData(resetDataPoints);
                //activity.series1.appendData(dp,false, 500000);
            }
            activity.positionTimeSeries.refreshDrawableState();
            activity.positionTimeSeries.addSeries(activity.series1);
            activity.positionTimeSeries.addSeries(activity.series3);
        } else {
            DataPoint[] resetDataPoints = (DataPoint[]) list.toArray(new DataPoint[list.size()]);

            for(DataPoint dp:list){
                activity.series1.resetData(resetDataPoints);
                //activity.series3.resetData(resetDataPoints);
                //activity.series1.appendData(dp,false, 500000);
            }
            activity.positionTimeSeries.refreshDrawableState();
            activity.positionTimeSeries.addSeries(activity.series1);
            activity.positionTimeSeries.addSeries(activity.series3);
        }

    }

 */

}
