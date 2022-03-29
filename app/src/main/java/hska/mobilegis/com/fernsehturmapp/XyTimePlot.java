package hska.mobilegis.com.fernsehturmapp;

public class XyTimePlot {
    private String x;
    private String y;
    private String time;

    public XyTimePlot() {}

    public XyTimePlot(String y, String time){
        this.y = y;
        this.time = time;
    }


    public XyTimePlot(String x, String y, String time){
        this.x = x;
        this.y = y;
        this.time = time;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "XyTimePlot{" +
                "x='" + x + '\'' +
                ", y='" + y + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
