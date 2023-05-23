package com.example.systemlrswipe;

import android.content.Context;
import android.util.Log;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.material.snackbar.Snackbar;

import android.graphics.Path;
import static androidx.constraintlayout.widget.Constraints.TAG;


import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EyesTracker extends Tracker<Face> {
    private final float THRESHOLD_LEFT = 0.85f; //HAV FULL L EYE
    private final float THRESHOLD_RIHGT= 0.85f; //HAV FULL R EYE


    int time_max = 10;  //delay from min and max time close
    int time_min = 5;

    List<Integer> Left;
    List<Integer> Right;



    public Integer sum(List<Integer> arr){ //for suming time 
        int result = 0;

        for (int n=0;n<arr.size();n+=1)
            result+=arr.get(n);
        return result;
    }

    private Context context;

    void initArray(){
        Left = new ArrayList<>();
        Right = new ArrayList<>();
        for (int x = 0 ; x<time_max*2;x++) {
            Left.add(0);
            Right.add(0);
        }
    }

    public EyesTracker(Context context) {
        this.context = context;
        initArray();

    }


    @Override
        public void onUpdate(Detector.Detections<Face> detections, Face face) {


        Log.i("THRESHOLD_LEFT", String.valueOf(face.getIsRightEyeOpenProbability()));
        Log.i("THRESHOLD_RIGHT", String.valueOf(face.getIsLeftEyeOpenProbability()));

        Log.i("LEFT", String.valueOf(Left));
        Log.i("RIGHT", String.valueOf(Right));

        Left.remove(0);

        Left.add(face.getIsRightEyeOpenProbability() > THRESHOLD_RIHGT?0:1);

        Right.remove(0);

        Right.add(face.getIsLeftEyeOpenProbability() > THRESHOLD_LEFT?0:1);

        int SumL = sum(Left);

            if (SumL >= time_min && SumL <= time_max) {
                ((AutoClickService) context).update(Condition.LEFT);
                initArray();

        }
        int SumR = sum(Right);

        if (SumR >= time_min && SumR <= time_max) {
                ((AutoClickService) context).update(Condition.RIGHT);
                initArray();
            }
    }

        @Override
        public void onMissing(Detector.Detections<Face> detections) {
            super.onMissing(detections);

            Log.i(TAG, "onUpdate: Face Not Detected!");

        }

        @Override
        public void onDone() {
            super.onDone();
        }


}
