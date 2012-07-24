package com.kerpie.MyCam;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.Toast;

public class MyCam extends Activity implements SurfaceHolder.Callback{
	
	private static final String TAG = "kerpie.hardware";
	private LayoutInflater mInflater = null;
	Camera camera;
	byte[] tempdata;
	boolean mPreviewRunning = false;
	private SurfaceHolder mSurfaceHolder;
	private SurfaceView mSurfaceView;
	Button takePicture;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.main);
        
        mSurfaceView = (SurfaceView) findViewById(R.id.surface);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
        mInflater = LayoutInflater.from(this);
        View overView = mInflater.inflate(R.layout.segundacapa,null);
        this.addContentView(overView, new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
        
        takePicture = (Button) findViewById(R.id.button);
        takePicture.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				camera.takePicture(mShutterCallback, mPictureCallback, mjpeg);
			}
		});
        
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }
    
    ShutterCallback mShutterCallback = new ShutterCallback() {
		
		@Override
		public void onShutter() {
		}
	};
	
	PictureCallback mPictureCallback = new PictureCallback() {
		
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			
		}
	};
	
	PictureCallback mjpeg = new PictureCallback() {
		
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			if(data != null){
				tempdata = data;
				done();
			}
		}
	};
	
	void done(){
		Bitmap bm = BitmapFactory.decodeByteArray(tempdata, 0, tempdata.length);
		String url = Images.Media.insertImage(getContentResolver(), bm, null, null);
		bm.recycle();
		Bundle bundle = new Bundle();
		if(url != null){
			bundle.putString("url",url);
			
			Intent mIntent = new Intent();
			mIntent.putExtras(bundle);
			setResult(RESULT_OK, mIntent);
		}
		else{
			Toast.makeText(this, "Picture can not be saved", Toast.LENGTH_SHORT).show();
		}
		finish();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		Log.e(TAG,"SurfaceChanged");
		try{
			if(mPreviewRunning){
				camera.stopPreview();
				mPreviewRunning = false;
			}
			
			Camera.Parameters p = camera.getParameters();
			p.setPreviewSize(width,height);
			
			camera.setParameters(p);
			camera.setPreviewDisplay(holder);
			camera.startPreview();
			mPreviewRunning = true;
		}catch(Exception e){
			Log.d("",e.toString());
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Log.e(TAG,"Surface Created");
		camera = Camera.open();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Log.e(TAG, "Surface Destroyed");
		camera.stopPreview();
		mPreviewRunning = false;
		camera.release();
		camera = null;
	}
	
	
}