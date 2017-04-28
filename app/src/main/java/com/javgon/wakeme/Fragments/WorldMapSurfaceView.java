package com.javgon.wakeme.Fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.javgon.wakeme.Model.LCoordinates;
import com.javgon.wakeme.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by http://android-coding.blogspot.com/2011/05/drawing-on-surfaceview.html
 * Modified by javier gonzalez on 4/27/2017.
 * Shows world map with locations of people who are about to wake up
 */

class WorldMapSurfaceView extends SurfaceView implements Runnable{

    Thread thread = null;
    SurfaceHolder surfaceHolder;
    volatile boolean running = false;
    Bitmap map,pinpoint;
    ArrayList<LCoordinates> locations = new ArrayList<>();

    public WorldMapSurfaceView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point screenSize = new Point();
        display.getSize(screenSize);
        surfaceHolder = getHolder();
        int layoutWidth=ViewGroup.LayoutParams.MATCH_PARENT;
        int layoutHeight=ViewGroup.LayoutParams.MATCH_PARENT;
        surfaceHolder.setFixedSize(layoutWidth,layoutHeight);
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        //bitmapOptions.inJustDecodeBounds = true; //gets size of image without creating image and adding memory overhead
        bitmapOptions.inScaled = false; //prevents drawing low resolution bitmap
        map = BitmapFactory.decodeResource(getResources(), R.mipmap.worldmapl,bitmapOptions);
        float ratio= (float)(bitmapOptions.outHeight)/bitmapOptions.outWidth; //to keep image scaled correctly
        map = Bitmap.createScaledBitmap(map, (int)(screenSize.x) , (int)(screenSize.x*ratio) , true);
        pinpoint=BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round);
        pinpoint= Bitmap.createScaledBitmap(pinpoint, 30 , 30 , true);

    }

    public void onResumeMySurfaceView(){
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    public void onPauseMySurfaceView(){
        boolean retry = true;
        running = false;
        while(retry){
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void onDraw(Canvas canvas) {


    }

   public void setCoordinates(ArrayList<LCoordinates> locations){
       this.locations=locations;
   }

    @Override
    public void run() {
        while(running){
            try {
                thread.sleep(250);  //we do not need to continously update canvas
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(surfaceHolder.getSurface().isValid()){
                Canvas canvas  = surfaceHolder.lockCanvas();
                //... actual drawing on canvas
                canvas.drawColor(Color.BLACK);
                canvas.drawBitmap(map, 0, 0, null);
                for (LCoordinates location : locations){
                    int convertLatitude=(int)(location.getLatitude())+180;
                    int convertLongitutde=(int)(location.getLongitude())+90;
                    canvas.drawBitmap(pinpoint,convertLatitude,convertLongitutde,null);
                }
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

}