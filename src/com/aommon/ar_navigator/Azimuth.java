package com.aommon.ar_navigator;

public class Azimuth {
	public static double calAngle(double s_lat, double s_long, double d_lat, double d_long){		
		s_lat = Math.toRadians(s_lat); 
		double a = Math.atan(Math.abs((d_long-s_long)/(d_lat-s_lat)))*(180/Math.PI);
		return a;
	}
	
	static Double degToRad = Math.PI / 180.0;

    static public Double initial (Double lat1, Double long1, Double lat2, Double long2)
    {
        return (_bearing(lat1, long1, lat2, long2) + 360.0) % 360;
    }

    public static double Double (Double lat1, Double long1, Double lat2, Double long2)
    {
        return (_bearing(lat2, long2, lat1, long1) + 180.0) % 360;
    }

    static private Double _bearing(Double lat1, Double long1, Double lat2, Double long2)
    {
        Double phi1 = lat1 * degToRad;
        Double phi2 = lat2 * degToRad;
        Double lam1 = long1 * degToRad;
        Double lam2 = long2 * degToRad;

        return Math.atan2(Math.sin(lam2-lam1)*Math.cos(phi2),
            Math.cos(phi1)*Math.sin(phi2) - Math.sin(phi1)*Math.cos(phi2)*Math.cos(lam2-lam1)
        ) * 180/Math.PI;
    }

}
