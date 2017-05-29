package com.javgon.wakeme.Other;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;


/**
 * Created by javier gonzalez on 5/23/2017.
 */

public class ScreenUtils {


    public ScreenUtils(){
    }

    public static int[] getScreenSizeDp(Activity activity){

        Display display = activity.getWindowManager().getDefaultDisplay();
        Point outMetrics = new Point();
        display.getSize(outMetrics);

        float density  = activity.getResources().getDisplayMetrics().density;
        float dpHeight = outMetrics.y / density;
        float dpWidth  = outMetrics.x / density;
        Log.d("DENSITY", "density: "+ density + "dpHeight: " + dpHeight + "dpWidth: " + dpWidth);

        int [] result ={(int)dpWidth,(int)dpHeight};

        return result;

    }

    public static int[] getScreenPixelsToDp(Activity activity){

        Display display = activity.getWindowManager().getDefaultDisplay();
        Point outMetrics = new Point();
        display.getSize(outMetrics);

        Resources resources = activity.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dpX = 160*outMetrics.x / ((float)metrics.densityDpi);
        float dpY =160*outMetrics.y / ((float)metrics.densityDpi);
        Log.d("DENSITY", "densitydpi: "+ metrics.densityDpi + " density default: " +  DisplayMetrics.DENSITY_DEFAULT+  " dpHeight: " + dpY + "dpWidth: " + dpX);

        int [] result ={(int)dpX,(int)dpY};

        return result;
    }

}
