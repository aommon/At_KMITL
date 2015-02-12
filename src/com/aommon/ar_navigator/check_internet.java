package com.aommon.ar_navigator;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class check_internet {
	public boolean isNetworkConnected() {
		  ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		  NetworkInfo ni = cm.getActiveNetworkInfo();
		  if (ni == null) {
		   // There are no active networks.
		   return false;
		  } else
		   return true;
	}

	private ConnectivityManager getSystemService(String connectivityService) {
		// TODO Auto-generated method stub
		return null;
	}
}
