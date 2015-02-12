package com.aommon.ar_navigator;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;

import com.aommon.ar_navigator.GMapV2Direction.OnDirectionResponseListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.internal.ln;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import android.R.bool;
import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PointF;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements SurfaceHolder.Callback, SensorEventListener, AutoFocusCallback {

	Camera mCamera;
    SurfaceView mPreview;
    
    SensorManager sensorManager;
    Sensor accelerometer,magnetometer;
    float degree,con_degree,azimuthInDegress,old_rotate;
    float[] mGravity;
    float[] mGeomagnetic;
    
    TextView txtHeading,textInf,txtSoLat,txtSoLng,txtBetlat,txtBetlng,txtDesLat,txtDesLng,txtAngle,txtI,txtEnd,txtFin,txtCheck;
    ImageView imgArr;
    Button btnSearch;
    private float currentDegree = 0f;
    
    //Database
    SQLiteDatabase mDb;  
    Database mHelper;  
    Cursor mCursor_near,mCurcor;
    ArrayList<String> arr_list = new ArrayList<String>();
    GridView gridView;
    
    //Map
    LocationClient mLocationClient;
    double lat,lng,angle,dlat,dlng;
    public static final String TAG = "InMain";
    GMapV2Direction md;
    ArrayList<LatLng> arr_pos;
    int i;
    boolean getInput,getGPS;
    PointF a[] = new PointF[4];
    PointF at_target[] = new PointF[4];
    PointF near_target[] = new PointF[4];
    
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN 
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_main);
		
		mPreview = (SurfaceView)findViewById(R.id.preview);
        mPreview.getHolder().addCallback(this);
        mPreview.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
        Database mHelper = new Database(this);
    	SQLiteDatabase mDb = mHelper.getmDbHelper().getWritableDatabase();
    	mHelper.close();
    	mDb.close();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        
        txtHeading = (TextView) findViewById(R.id.txtHeading);
        textInf = (TextView) findViewById(R.id.textInf);
        txtSoLat = (TextView) findViewById(R.id.txtSoLat);
        txtSoLng = (TextView) findViewById(R.id.txtSoLng);
        txtBetlat = (TextView) findViewById(R.id.txtBetLat);
        txtBetlng = (TextView) findViewById(R.id.txtBetLng);
        txtDesLat = (TextView) findViewById(R.id.txtDesLat);
        txtDesLng = (TextView) findViewById(R.id.txtDesLng);
        txtAngle = (TextView) findViewById(R.id.txtAngle);
        txtI = (TextView) findViewById(R.id.txtI);
        txtEnd = (TextView) findViewById(R.id.txtEnd);
        txtFin = (TextView) findViewById(R.id.txtFin);
        txtCheck = (TextView) findViewById(R.id.txtCheck);
        imgArr = (ImageView)findViewById(R.id.imgArr);
        btnSearch = (Button) findViewById(R.id.b_search);
        gridView = (GridView) findViewById(R.id.grid);
        md = new GMapV2Direction(this);
        
        //GPS
        boolean result = isServicesAvailable();        
        if(result) {
            // ในเครื่องมี Google Play Services
        	mLocationClient = new LocationClient(this, mCallback, mListener);
        } else {
            // ในเครื่องไม่มี Google Play Services ติดตั้งอยู่
        	finish();
        }  
        
        // click autofocus
        mPreview.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mCamera.autoFocus(MainActivity.this);
            }
        });
        
		btnSearch.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						SearchLocation.class);
				//startActivity(intent);
				startActivityForResult(intent, 999);
				txtCheck.setText("Click");
			}
		});
	}
	
	protected void onActivityResult ( int requestCode, int resultCode, Intent data )
	{
		if(requestCode == 999)
		{
			if(resultCode == RESULT_OK){
				txtCheck.setText("Send Data to Google");
				String dName = data.getStringExtra("mydName");
				dlat = data.getDoubleExtra("mydLat", lat);
				dlng = data.getDoubleExtra("mydLong", lng);
				Log.e(TAG, "Destination : " + dName + " " + dlat + "," + dlng);
				
				LatLng startPosition = new LatLng(lat, lng);
				LatLng endPosition = new LatLng(dlat , dlng);
				
				Log.e(TAG,"start : "+startPosition);
				Log.e(TAG,"end : "+endPosition);

				Log.e(TAG, GMapV2Direction.MODE_DRIVING);
				//try{
				md.request(startPosition
		                , endPosition, GMapV2Direction.MODE_DRIVING);
				Log.e("onclick","1");
				md.setOnDirectionResponseListener(new OnDirectionResponseListener() {
			        public void onResponse(String status, Document doc, GMapV2Direction gd) {
			        	Log.e("onclick","2");
		        		int distance = gd.getTotalDistanceValue(doc);
		        		Log.e(TAG,"Total Distance : "+distance);
		        		int duration = gd.getTotalDurationValue(doc);
		        		Log.e(TAG,"Total Duration : "+duration);
		        		textInf.setText("Distance : " + distance + " m\n"
		        				+"Duration : " + duration + " sec");
		                arr_pos = gd.getDirection(doc);
		    			for(int j = 0 ; j < arr_pos.size() ; j++) {
		                    Log.e("Position " + j, arr_pos.get(j).latitude
		                            + ", " + arr_pos.get(j).longitude);
		    			}
		    			txtCheck.setText("Recieve Data from Google");
		    			getInput = true;
		    			i = 0;
		    			imgArr.setImageResource(R.drawable.arrow_red);
			        }
				});
			}
		}
	}

	public void workspace(){

		txtHeading.setText("Heading"+ (String.format("%.8f", azimuthInDegress) + "degrees"));
    	txtSoLat.setText("S_la : " + (String.format("%.8f", lat)));
		txtSoLng.setText("S_long : " + (String.format("%.8f", lng)));
		txtDesLat.setText("D_la : " + (String.format("%.8f", dlat)));
		txtDesLng.setText("D_long : " + (String.format("%.8f", dlng)));
		
	
		
		if((azimuthInDegress > con_degree+5 || azimuthInDegress < con_degree-5)){
			con_degree = azimuthInDegress;
			if(getInput){
				Log.e("onclick","3");		

				near_target = nearby.nearbyLaLong(dlat, dlng, 10);
				if(lat > near_target[2].x && lat < near_target[0].x && lng < near_target[1].y && lng > near_target[3].y ){
					double t_angle = Azimuth.initial(lat, lng, dlat, dlng);
					old_rotate = Navigator.Rotate_arrow(con_degree, t_angle, old_rotate, imgArr);
					txtCheck.setText("Near Target");
					if(lat > at_target[2].x && lat < at_target[0].x && lng < at_target[1].y && lng > at_target[3].y ){
						txtFin.setText("DONE");
						getInput = false;
						txtCheck.setText("@Target DONE!!");		
						imgArr.setImageBitmap(null);
					}
				} else {
					at_target = nearby.nearbyLaLong(dlat, dlng,7);
					double be_lat = arr_pos.get(i).latitude;
		        	double be_lng = arr_pos.get(i).longitude;
		        	Log.e("be_lat",""+(String.format("%.8f", be_lat)));
		        	Log.e("be_lng",""+(String.format("%.8f", be_lng)));
		        	txtBetlat.setText("be_lat "+ (String.format("%.8f", be_lat)));
		        	txtBetlng.setText("be_lng "+ (String.format("%.8f", be_lng)));
		        	txtI.setText("I_count "+ (String.format("%d", i)));
		        	txtEnd.setText("End "+ (String.format("%d", arr_pos.size())));
		        	
		        	a = nearby.nearbyLaLong(be_lat, be_lng,7);
					if(lat > a[2].x && lat < a[0].x && lng < a[1].y && lng > a[3].y ){
						Log.e("i+new",""+i);
						i++;
						txtCheck.setText("I++");
		         	}else{
		        		//Log.e("i_old",""+i);
		        		angle = Azimuth.initial(lat, lng, be_lat, be_lng);
		                txtAngle.setText("angle : " + (String.format("%.8f", angle)));
		                old_rotate = Navigator.Rotate_arrow(con_degree, angle, old_rotate, imgArr);
				        txtCheck.setText("Rotate Arrow");
		        	}
				}
			} else if(getGPS){
				//nearby
			   	 a = nearby.nearbyLaLong(lat, lng, 70);
			   	mCursor_near = mDb.rawQuery("SELECT  " +  Database.COL_NAME  + "," + Database.COL_LATITUDE + "," + 
		        		Database.COL_LONGITUDE  + " FROM " + Database.TABLE_NAME + " WHERE " +
		        		Database.COL_LATITUDE + " > " + a[2].x + " AND " +
		        		Database.COL_LATITUDE + " < " + a[0].x + " AND " +
		        		Database.COL_LONGITUDE + " < " + a[1].y + " AND " +
		        		Database.COL_LONGITUDE + " >= " + a[3].y + " ORDER BY " + Database.COL_LATITUDE + " , " + 
		        		Database.COL_LONGITUDE , null);        
		        
			   	 mCursor_near.moveToFirst();
			
		        if(mCursor_near.moveToFirst()){
		        	arr_list.clear();
		        	do{
			        	double pl_distance = Harversine.haversine(lat, lng, mCursor_near.getDouble(mCursor_near.getColumnIndex(Database.COL_LATITUDE)), 
			        							mCursor_near.getDouble(mCursor_near.getColumnIndex(Database.COL_LONGITUDE)));
			        	
			        	double pl_azimuth = Azimuth.initial(lat, lng, mCursor_near.getDouble(mCursor_near.getColumnIndex(Database.COL_LATITUDE)), 
								mCursor_near.getDouble(mCursor_near.getColumnIndex(Database.COL_LONGITUDE)));
			        	
			        	if(pl_azimuth > nearby.degree_right(con_degree) && pl_azimuth < nearby.degree_left(con_degree)){
			        		arr_list.add("" + mCursor_near.getString(mCursor_near.getColumnIndex(Database.COL_NAME))); 
			        	}
			        	
		        	} while (mCursor_near.moveToNext());
		        }
		        //show gridview
		        ArrayAdapter<String> adapter_place = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arr_list);
		        gridView.setAdapter(adapter_place);
			}
		}
		

	}
	
	 public void onResume() {
        Log.d("System","onResume");
        super.onResume();
        mCamera = Camera.open();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI); 
    }
    
    public void onPause() {
        Log.d("System","onPause");
        super.onPause();
        mCamera.release();
        sensorManager.unregisterListener(this);
    }
    
    public void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this);
        mLocationClient.disconnect();
    }
    protected void onStart(){
    	super.onStart();
    	//GPS
    	mLocationClient.connect();
    	
    }

    public void surfaceChanged(SurfaceHolder arg0
            , int arg1, int arg2, int arg3) {
        Log.d("CameraSystem","surfaceChanged");
        Camera.Parameters params = mCamera.getParameters();
        List<Camera.Size> previewSize = params.getSupportedPreviewSizes();
        List<Camera.Size> pictureSize = params.getSupportedPictureSizes();
        int preview_index = ImageMaxSize.maxSize(previewSize);
        int picture_index = ImageMaxSize.maxSize(pictureSize);
        params.setPictureSize(previewSize.get(preview_index).width, previewSize.get(preview_index).height);
        params.setPreviewSize(previewSize.get(picture_index).width,previewSize.get(picture_index).height);
        params.setJpegQuality(100);
        mCamera.setParameters(params);
        
        try {
            mCamera.setPreviewDisplay(mPreview.getHolder());
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void surfaceCreated(SurfaceHolder arg0) {
        Log.d("CameraSystem","surfaceCreated");
        try {
            mCamera.setPreviewDisplay(mPreview.getHolder());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void surfaceDestroyed(SurfaceHolder arg0) { }
    
    public void onAutoFocus(boolean success, Camera camera) {
        Log.d("CameraSystem","onAutoFocus");
    }
    
    @Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub		
	}
    
    @Override
	public void onSensorChanged(SensorEvent event) {
		//compass
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            mGravity = event.values;
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
            	float orientation[] = new float[3];
            	SensorManager.getOrientation(R, orientation);
            	degree = orientation[0]; // orientation contains: azimut, pitch and roll
            	//Log.e("azi", "" + degree);
            	azimuthInDegress = (float)Math.toDegrees(degree);
            	if (azimuthInDegress < 0.0f) {
            		azimuthInDegress += 360.0f;
            	}           	
            }
        }
        workspace();
	}
    
  //GPS
    private ConnectionCallbacks mCallback = new ConnectionCallbacks() {
        public void onConnected(Bundle bundle) {
            // เชื่อมต่อกับ Google Play Services ได้
        	Toast.makeText(MainActivity.this, "Services connected", Toast.LENGTH_SHORT).show();

            LocationRequest mRequest = new LocationRequest()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(5000).setFastestInterval(1000);

            mLocationClient.requestLocationUpdates(mRequest, locationListener);
        }

        public void onDisconnected() {
            // หยุดเชื่อมต่อกับ Google Play Services
        	Toast.makeText(MainActivity.this, "Services disconnected", Toast.LENGTH_SHORT).show();
        }
    };
    
    private OnConnectionFailedListener mListener = new OnConnectionFailedListener() {
        public void onConnectionFailed(ConnectionResult result) {
            // เมื่อเปิดปัญหาเชื่อมต่อกับ Google Play Services ไม่ได้
        	Toast.makeText(MainActivity.this, "Services connection failed", Toast.LENGTH_SHORT).show();
        }
    };
    private boolean isServicesAvailable() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        return (resultCode == ConnectionResult.SUCCESS);
    }

    LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {

        	LatLng coordinate = new LatLng(location.getLatitude(),location.getLongitude());
        	lat = location.getLatitude();
        	lng = location.getLongitude();
        	//Log.e("onclick","4");
        	getGPS=true;
        	workspace();
        	
		}       
    };
	    
}
