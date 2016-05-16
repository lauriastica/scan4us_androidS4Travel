package com.scan4us.scan4travel.fragments;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;

import com.scan4us.scan4travel.R;
import com.scan4us.scan4travel.utils.ImageProcessing;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by RanKey on 15/05/2016.
 */
public class HeartFragment extends Fragment {

    private static final String TAG = "HeartPulseActivity";
    private static final AtomicBoolean processing = new AtomicBoolean(false);

    private static SurfaceView preview = null;
    private static SurfaceHolder previewHolder = null;
    private static Camera camera = null;
    private static View image = null;
    private static TextView text = null;
    static String beatsPerMinuteValue="";
    private static PowerManager.WakeLock wakeLock = null;
    private static Button mTxtVwStopWatch;
    private static int averageIndex = 0;
    private static final int averageArraySize = 4;
    private static final int[] averageArray = new int[averageArraySize];
    private static Context parentReference = null;

    public static enum TYPE {
        GREEN, RED
    };

    private static TYPE currentType = TYPE.GREEN;

    private static int beatsIndex = 0;
    private static final int beatsArraySize = 3;
    private static final int[] beatsArray = new int[beatsArraySize];
    private static double beats = 0;
    private static long startTime = 0;
    private static Vibrator v ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.heart_fragment, container, false);

        //init();

        return view;
    }

    public void init(){
        v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        parentReference = getContext();
        preview = (SurfaceView) getActivity().findViewById(R.id.preview);
        mTxtVwStopWatch =(Button) getActivity().findViewById(R.id.txtvwStopWatch);
        previewHolder = preview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        PowerManager pm = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");

        image = getActivity().findViewById(R.id.image);
        text = (TextView) getActivity().findViewById(R.id.text_bpm);

        prepareCountDownTimer();

        //camera=Camera.open();
        //comienzo la animacion de las pulsaciones en 20bpm
        setBPM(140);
        playHeartBeat(140);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        init();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onResume() {
        super.onResume();
        wakeLock.acquire();
        camera = Camera.open();
        startTime = System.currentTimeMillis();
    }

    @Override
    public void onPause() {
        super.onPause();
        wakeLock.release();
        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    public static Animator mBeatingAnimation;
    public  static boolean mBeating = false;
    public static int mCurrentBpm;

    public static void setBPM(int bpm) {
        if (bpm == 0) {
            bpm = 1;
        }
        mCurrentBpm = bpm;
    }

    public static void playHeartBeat(int bpm) {
        if (mBeatingAnimation != null) {
            mBeatingAnimation.cancel();
            mBeatingAnimation = null;
        }

        long beatDuration = 60000/bpm;

        AnimatorSet animatorSet = new AnimatorSet();
        final ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(image, "scaleX", 1.4f, 1f);
        final ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(image, "scaleY", 1.4f, 1f);
        animatorSet.play(scaleXAnimator).with(scaleYAnimator);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.setDuration(beatDuration);
        animatorSet.addListener(new Animator.AnimatorListener() {
            private boolean cancelled = false;
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!cancelled && mBeating) {
                    playHeartBeat(mCurrentBpm);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                cancelled = true;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.start();
        mBeatingAnimation = animatorSet;
    }

    private static Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void onPreviewFrame(byte[] data, Camera cam) {
            if (data == null) throw new NullPointerException();
            Camera.Size size = cam.getParameters().getPreviewSize();
            if (size == null) throw new NullPointerException();

            if (!processing.compareAndSet(false, true)) return;

            int width = size.width;
            int height = size.height;

            int imgAvg = ImageProcessing.decodeYUV420SPtoRedAvg(data.clone(), height, width);
            // Log.i(TAG, "imgAvg="+imgAvg);
            if (imgAvg == 0 || imgAvg == 255) {
                processing.set(false);
                return;
            }

            int averageArrayAvg = 0;
            int averageArrayCnt = 0;
            for (int i = 0; i < averageArray.length; i++) {
                if (averageArray[i] > 0) {
                    averageArrayAvg += averageArray[i];
                    averageArrayCnt++;
                }
            }

            int rollingAverage = (averageArrayCnt > 0) ? (averageArrayAvg / averageArrayCnt) : 0;
            TYPE newType = currentType;
            if (imgAvg < rollingAverage) {
                newType = TYPE.RED;
                if (newType != currentType) {
                    beats++;
                    // Log.d(TAG, "BEAT!! beats="+beats);
                }
            } else if (imgAvg > rollingAverage) {
                newType = TYPE.GREEN;
            }

            if (averageIndex == averageArraySize) averageIndex = 0;
            averageArray[averageIndex] = imgAvg;
            averageIndex++;

            // Transitioned from one state to another to the same
            if (newType != currentType) {
                currentType = newType;
                image.postInvalidate();
            }

            long endTime = System.currentTimeMillis();
            double totalTimeInSecs = (endTime - startTime) / 1000d;
            if (totalTimeInSecs >= 15) {
                double bps = (beats / totalTimeInSecs);
                int dpm = (int) (bps * 60d);
                if (dpm < 30 || dpm > 180) {
                    startTime = System.currentTimeMillis();
                    beats = 0;
                    processing.set(false);
                    return;
                }

                // Log.d(TAG,
                // "totalTimeInSecs="+totalTimeInSecs+" beats="+beats);

                if (beatsIndex == beatsArraySize) beatsIndex = 0;
                beatsArray[beatsIndex] = dpm;
                beatsIndex++;

                int beatsArrayAvg = 0;
                int beatsArrayCnt = 0;
                for (int i = 0; i < beatsArray.length; i++) {
                    if (beatsArray[i] > 0) {
                        beatsArrayAvg += beatsArray[i];
                        beatsArrayCnt++;
                    }
                }
                int beatsAvg = (beatsArrayAvg / beatsArrayCnt);
                text.setText(String.valueOf(beatsAvg));
                beatsPerMinuteValue=String.valueOf(beatsAvg);
                setBPM(beatsAvg);
                playHeartBeat(beatsAvg);
                makePhoneVibrate();
                //--------------------
                //showReadingCompleteDialog("Listo");
                startTime = System.currentTimeMillis();
                beats = 0;
            }
            processing.set(false);
        }
    };


    private static void makePhoneVibrate(){
        v.vibrate(500);
    }

    private static SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                camera.setPreviewDisplay(previewHolder);
                camera.setPreviewCallback(previewCallback);
            } catch (Throwable t) {

            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Camera.Parameters parameters = camera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            Camera.Size size = getSmallestPreviewSize(width, height, parameters);
            if (size != null) {
                parameters.setPreviewSize(size.width, size.height);
                Log.d(TAG, "Using width=" + size.width + " height=" + size.height);
            }
            camera.setParameters(parameters);
            camera.startPreview();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // Ignore
        }
    };

    private static Camera.Size getSmallestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size result = null;
        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;

                    if (newArea < resultArea) result = size;
                }
            }
        }
        return result;
    }

    private static void prepareCountDownTimer(){
        mTxtVwStopWatch.setText("---");
        new CountDownTimer(15000, 1000) {

            public void onTick(long millisUntilFinished) {
                mTxtVwStopWatch.setText("Pon el dedo en la cámara y\n espera: " + (millisUntilFinished) / 1000+" segundos");
            }
            public void onFinish() {
                mTxtVwStopWatch.setText("Listo!");
            }
        }.start();
    }

    private static void showReadingCompleteDialog(String mensaje){
        final AlertDialog.Builder builder = new AlertDialog.Builder(parentReference);
        final AlertDialog alert = builder.create();
        builder.setTitle("Recuerde!");
        builder.setMessage(mensaje)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        alert.dismiss();
                    }
                });
        alert.show();
    }
}
