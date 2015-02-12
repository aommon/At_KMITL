package com.aommon.ar_navigator;

import java.util.Arrays;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PointF;
import android.util.Log;

public class nearby {
	public static PointF calculateDerivedPosition(PointF point,double range, double bearing){
        double EarthRadius = 6371000; // m

        double latA = Math.toRadians(point.x);
        double lonA = Math.toRadians(point.y);
        double angularDistance = range / EarthRadius;
        double trueCourse = Math.toRadians(bearing);

        double lat = Math.asin(
                Math.sin(latA) * Math.cos(angularDistance) +
                        Math.cos(latA) * Math.sin(angularDistance)
                        * Math.cos(trueCourse));

        double dlon = Math.atan2(
                Math.sin(trueCourse) * Math.sin(angularDistance)
                        * Math.cos(latA),
                Math.cos(angularDistance) - Math.sin(latA) * Math.sin(lat));

        double lon = ((lonA + dlon + Math.PI) % (Math.PI * 2)) - Math.PI;

        lat = Math.toDegrees(lat);
        lon = Math.toDegrees(lon);

        PointF newPoint = new PointF((float) lat, (float) lon);

        return newPoint;
    }
	
	public static float degree_right(float deg){ //more than
		float right = deg-31+90;//deg+60 deg-120
		if(right>360){
        	right = right-360;
        }else if(right<0){
        	right = 360+right;
        }
        return right;
	}
	
	public static float degree_left(float deg){ //less than
		float left = deg+31+90;//deg+120 //deg-60
        if(left>360){
        	left = left-360;
        }else if(left<0){
        	left = 360+left;
        }
        return left;
	}
	
	public static float true_compass(float deg){ //True degree when watching AR
		float t_de = deg+90;
        if(t_de>360){
        	t_de = t_de-360;
        }else if(t_de<0){
        	t_de = 360+t_de;
        }
        return t_de;
	}
	
	public static PointF[] nearbyLaLong (double s_la, double s_long,int distance){
		//nearby
        PointF center = new PointF((float)s_la, (float)s_long);
        final double mult = 1.1; // mult = 1.1; is more reliable
        //double distance = 50;
        
        PointF p1 = nearby.calculateDerivedPosition(center, mult * distance, 0); //right
        PointF p2 = nearby.calculateDerivedPosition(center, mult * distance, 90); //forward
        PointF p3 = nearby.calculateDerivedPosition(center, mult * distance, 180);//left
        PointF p4 = nearby.calculateDerivedPosition(center, mult * distance, 270);//back
        
        PointF p[] = new PointF[4];
        p[0]=p1;
        p[1]=p2;
        p[2]=p3;
        p[3]=p4;              
        return p;
	}
	
	public static boolean pointIsInCircle(double center_la, double center_long ,double des_la , double des_long, double radius) {
        if (Harversine.haversine(center_la, center_long, des_la, des_long) < radius)
            return true;
        else
            return false;
    }
	
	
	

}
