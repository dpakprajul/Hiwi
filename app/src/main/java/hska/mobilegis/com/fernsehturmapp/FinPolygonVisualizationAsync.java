package hska.mobilegis.com.fernsehturmapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;

import com.androidplot.ui.Insets;
import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPCmd;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class FinPolygonVisualizationAsync extends AsyncTask<String, Void, List<XyTimePlot>>{

    String sHrs, sMins, sSecs;
    String eHrs, eMins, eSecs;
    private FinPolygonVisualization activity;
    public ProgressDialog dialog;

    List<Number> xList = new ArrayList<>();
    List<Number> yList = new ArrayList<>();



    public FinPolygonVisualizationAsync(FinPolygonVisualization activity) {
        this.activity = activity;
    }

    public FinPolygonVisualizationAsync() {
        this.activity=activity;
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(this.activity);
        dialog.setMessage("Please wait...");
        dialog.setIndeterminate(true);
        dialog.show();
        super.onPreExecute();
    }

    @Override
    protected List<XyTimePlot> doInBackground(String... strings) {
        String fileName = strings[0];
        FTPClient ftpClient = new FTPClient();
        BufferedReader br = null;
        String line = "";
        String object = null;
        InputStream inStream = null;
        List<XyTimePlot> xyTimePlots = new ArrayList<>();

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

            int linenum = 0;
            while ((line = br.readLine()) != null) {
                if ((linenum % 4) > 0) {

                } else {
                    String rowValue = line.trim().replaceAll("\\s+", ",");//trim white spaces at the edges of file, replace white spaces with comma, stores result in rowValue
                    String[] arr = rowValue.split(","); //split rowValue into comma separated values (?), text split into an array of rows
                    if (arr.length != 1) {
                        object = arr[0];
                        if(object.equals(activity.objectType)){

                            if(activity.objectType.equals("Turm_ubx")){
                                xyTimePlots.add(new XyTimePlot(arr[1], arr[2], arr[4]));
                                //activity.plot.setDomainBoundaries(32513978.495, 32513978.515, FIXED);
                                //activity.plot.setRangeBoundaries(5400319.55, 5400319.584, FIXED);
                                //System.out.println("xyTimeplots size turm_ubx:"+xyTimePlots.size());
                            }else if(activity.objectType.equals("Turm")){
                                xyTimePlots.add(new XyTimePlot(arr[1], arr[2], arr[4]));
                                //activity.plot.setDomainBoundaries(32513978.07, 32513978.115, FIXED);
                                //activity.plot.setRangeBoundaries(5400318.33, 5400318.41, FIXED);
                                //System.out.println("xyTimeplots size turm:"+xyTimePlots.size());
                            }
                        }
                    }
                }
                linenum++;
            }
            return xyTimePlots;
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
    protected void onPostExecute(List<XyTimePlot>str) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        super.onPostExecute(str);
        if(str==null || str.size()==0){
            activity.plot.clear();
            activity.plot.redraw();

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
        }else if(str.size()!=0){
            activity.setList(str);
            finFileDataRecord(str);
        }
    }

    //Get list of datapoints from general datalist and draw the graph
    public void finFileDataRecord(List<XyTimePlot> xyTimePlots){

        String checkValue=activity.objectType;

String minsec=activity.outData();

        if ((minsec != null && !minsec.isEmpty())
                && (activity.endTime != null && !activity.endTime.isEmpty())) {

            // Parse given start time (String) hr, mins and secs    10:11:29
            sHrs = activity.minsec.substring(0, 2);
            sMins = activity.minsec.substring(3, 5);
            sSecs = activity.minsec.substring(6);


            // Parse given end time (String) hr, mins and secs
            eHrs = activity.endTime.substring(0, 2);
            eMins = activity.endTime.substring(3, 5);
            eSecs = activity.endTime.substring(6);

            Double startT = Double.valueOf(sHrs) * 3600 + Double.valueOf(sMins) * 60 + Double.valueOf(sSecs); //Double startT
            Double endT = Double.valueOf(eHrs) * 3600 + Double.valueOf(eMins) * 60 + Double.valueOf(eSecs);

            for (XyTimePlot xyT : xyTimePlots) {
                if((Double.valueOf(xyT.getTime())>=startT) && (Double.valueOf(xyT.getTime())<=endT)){
                    xList.add(Double.valueOf(xyT.getX()));
                    yList.add(Double.valueOf(xyT.getY()));
                }
                //activity.series1 = new SimpleXYSeries(xList, yList, " ");

            }


            if(xList.size()<=2 || yList.size()<=2){
                activity.series1 = new SimpleXYSeries(xList, yList, " ");
                AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
                builder.setCancelable(false);
                builder.setTitle(R.string.no_datapoints_title);
                if(checkValue.equalsIgnoreCase("Turm")){
                    System.out.println("Object Type Turm:"+activity.objectType);
                    builder.setMessage(R.string.no_datapoints_Turm);
                }else if(checkValue.equalsIgnoreCase("Turm_ubx")){
                    System.out.println("Object Type:"+activity.objectType);
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
                /*
                for (XyTimePlot xyT : xyTimePlots) {
                    if((Double.valueOf(xyT.getTime())>=startT) && (Double.valueOf(xyT.getTime())<=endT)){

                        xList.add(Double.valueOf(xyT.getX()));
                        yList.add(Double.valueOf(xyT.getY()));
                    }
                    activity.series1 = new SimpleXYSeries(xList, yList, " ");
                }
                */
                activity.series1 = new SimpleXYSeries(xList, yList, " ");
            }
        }else {

            SimpleDateFormat sdf_time = new SimpleDateFormat("HH:mm:ss"); //hh:mm:ss
            sdf_time.setTimeZone(TimeZone.getTimeZone("GMT"));

            activity.minsec = sdf_time.format(new Date(System.currentTimeMillis()- (5)*1000));
            activity.endTime = sdf_time.format(new Date(System.currentTimeMillis()));

            // Parse given start time (String) hr, mins and secs    10:11:29
            sHrs = activity.minsec.substring(0, 2);
            sMins = activity.minsec.substring(3, 5);
            sSecs = activity.minsec.substring(6);

            // Parse given end time (String) hr, mins and secs
            eHrs = activity.endTime.substring(0, 2);
            eMins = activity.endTime.substring(3, 5);
            eSecs = activity.endTime.substring(6);

            Double startT = Double.valueOf(sHrs) * 3600 + Double.valueOf(sMins) * 60 + Double.valueOf(sSecs); //Double startT
            Double endT = Double.valueOf(eHrs) * 3600 + Double.valueOf(eMins) * 60 + Double.valueOf(eSecs);

            for (XyTimePlot xyT : xyTimePlots) {
                if((Double.valueOf(xyT.getTime())>=startT) && (Double.valueOf(xyT.getTime())<=endT)){
                    xList.add(Double.valueOf(xyT.getX()));
                    yList.add(Double.valueOf(xyT.getY()));
                }
            }

            if(xList.size()<=2 || yList.size()<=2){
                activity.series1 = new SimpleXYSeries(xList, yList, " ");
                AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
                builder.setCancelable(false);
                builder.setTitle(R.string.file_not_found_title);
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
                activity.series1 = new SimpleXYSeries(xList, yList, "");
            }
            activity.minsec = null;
            activity.endTime = null;
        }

        LineAndPointFormatter series1Format = new LineAndPointFormatter(activity, R.xml.point_formatter);

        series1Format.setInterpolationParams(
                new CatmullRomInterpolator.Params(20, CatmullRomInterpolator.Type.Centripetal)); // configure interpolation on the formatter, for line smoothening

        activity.plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.TOP).setFormat(new DecimalFormat("#.##"));
        activity.plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.RIGHT).setFormat(new DecimalFormat("#.##"));

        activity.plot.clear();
        activity.plot.addSeries(activity.series1, series1Format);
        activity.plot.getGraph().setGridInsets(new Insets(20, 20, 20, 20)); //new Insets(120, 120, 120, 120) 25, 25, 25, 25
        activity.plot.redraw();
    }

    public String values() {
        return "hello";
    }

    public List listValue(){
        return xList;
    }
}
