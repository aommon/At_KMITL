package com.aommon.ar_navigator;

import java.util.List;

import android.hardware.Camera;
import android.hardware.Camera.Size;

public class ImageMaxSize {
	public static int maxSize(List<Camera.Size> image) {
    	int max = 0;
        int index = 0;

        for (int i = 0; i < image.size(); i++){
             Size s = image.get(i);
             int size = s.height * s.width;
             if (size > max) {
                 index = i;
                 max = size;
             }
        }
		return index;
	}

}
