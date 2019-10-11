package com.arminbrandy.ethme;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
/*
    Thx to Keval Patel for that Singleton pattern
    https://medium.com/@kevalpatel2106/how-to-make-the-perfect-singleton-de6b951dfdb0
 */
public class AndroidUtils {

    private static volatile AndroidUtils sAndroidUtils;

    //private constructor.
    private AndroidUtils(){
        //Prevent form the reflection api.
        if (sAndroidUtils != null){
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public synchronized static AndroidUtils getInstance(){
        if (sAndroidUtils == null){ //if there is no instance available... create new one
            synchronized (AndroidUtils.class) {   //Check for the second time.
                //if there is no instance available... create new one
                if (sAndroidUtils == null) sAndroidUtils = new AndroidUtils();
            }
        }
        return sAndroidUtils;
    }

    public static synchronized void vibrate(Context c){
        Vibrator v = (Vibrator) c.getSystemService(c.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            if(v.hasVibrator()) {
                v.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK));
            }
        }
    }
}