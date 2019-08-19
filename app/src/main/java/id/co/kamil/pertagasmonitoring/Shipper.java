package id.co.kamil.pertagasmonitoring;

public class Shipper {
    String sumber,comment,alamat;
    String no,normal,dp,temp,pressure,vol_last_hour,vol_last_day,flow_rate,diff;

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getSumber() {
        return sumber;
    }

    public void setSumber(String sumber) {
        this.sumber = sumber;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getNormal() {
        return normal;
    }

    public void setNormal(String normal) {
        this.normal = normal;
    }

    public String getDp() {
        return dp;
    }

    public void setDp(String dp) {
        this.dp = dp;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getVol_last_hour() {
        return vol_last_hour;
    }

    public void setVol_last_hour(String vol_last_hour) {
        this.vol_last_hour = vol_last_hour;
    }

    public String getVol_last_day() {
        return vol_last_day;
    }

    public void setVol_last_day(String vol_last_day) {
        this.vol_last_day = vol_last_day;
    }

    public String getFlow_rate() {
        return flow_rate;
    }

    public void setFlow_rate(String flow_rate) {
        this.flow_rate = flow_rate;
    }

    public String getDiff() {
        return diff;
    }

    public void setDiff(String diff) {
        this.diff = diff;
    }

    @Override
    public String toString() {
        return "Shipper{" +
                "sumber='" + sumber + '\'' +
                ", comment='" + comment + '\'' +
                ", no='" + no + '\'' +
                ", normal='" + normal + '\'' +
                ", dp='" + dp + '\'' +
                ", temp='" + temp + '\'' +
                ", pressure='" + pressure + '\'' +
                ", vol_last_hour='" + vol_last_hour + '\'' +
                ", vol_last_day='" + vol_last_day + '\'' +
                ", flow_rate='" + flow_rate + '\'' +
                ", diff='" + diff + '\'' +
                '}';
    }
}
