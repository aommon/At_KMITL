package com.aommon.ar_navigator;

public class Harversine {
	public static double haversine(double s_lat, double s_long, double d_lat, double d_long) {
		int earth_radius = 6371000; //m
		s_lat = Math.toRadians(s_lat);
		d_lat = Math.toRadians(d_lat);
		double diff_latitude = d_lat-s_lat;
		double diff_latitude_rad = Math.toRadians(diff_latitude);
		
		double diff_longitude = d_long-s_long;
		double diff_longitude_rad = Math.toRadians(diff_longitude);
		
		double a = (Math.sin(diff_latitude_rad/2) * Math.sin(diff_latitude_rad/2)) + (Math.cos(s_lat) * Math.cos(d_lat) * Math.sin(diff_longitude_rad/2) * Math.sin(diff_longitude_rad/2));
		double c = 2*Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		double d = earth_radius*c;
		 
		return d;
	}
    public static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
     }
     public static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
     }

}
