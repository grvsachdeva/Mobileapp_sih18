package com.example.namankhanna.sihmobileapp;

public class Attendance {
    String date;
    String image;
    String location;
    String remarks;
    String time_in;
    String time_out;

    public String getDate() {
        return date;
    }

    public String getImage() {
        return image;
    }

    public String getLocation() {
        return location;
    }

    public String getRemarks() {
        return remarks;
    }

    public String getTime_in() {
        return time_in;
    }

    public String getTime_out() {
        return time_out;
    }

    @Override
    public String toString() {
        return "Attendance{" +
                "date='" + date + '\'' +
                ", image='" + image + '\'' +
                ", location='" + location + '\'' +
                ", remarks='" + remarks + '\'' +
                ", time_in='" + time_in + '\'' +
                ", time_out='" + time_out + '\'' +
                '}';
    }

    public Attendance(String date, String image, String location, String remarks, String time_in, String time_out) {
        this.date = date;
        this.image = image;
        this.location = location;
        this.remarks = remarks;
        this.time_in = time_in;
        this.time_out = time_out;
    }

    public Attendance() {
    }
}
