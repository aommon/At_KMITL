package com.aommon.ar_navigator;

import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class Navigator {
	
	public static float Rotate_arrow(float con_degree,double angle,float old_rotate,ImageView imgArr){
        
        double true_degree = nearby.true_compass(con_degree);
        float rotate = (float) (angle-true_degree);
        if(rotate > 180){
        	rotate = -(360-rotate);
        }
        
        RotateAnimation ra = new RotateAnimation(
    		old_rotate,
    		rotate,
            Animation.RELATIVE_TO_SELF, 0.5f, 
            Animation.RELATIVE_TO_SELF, 0.5f
        );
        
 
        // how long the animation will take place
        ra.setDuration(200);
 
        // set the animation after the end of the reservation status
        ra.setFillAfter(true);
 
        // Start the animation
        imgArr.startAnimation(ra);
        old_rotate = rotate;
        return old_rotate;
	}

}
