package com.scan4us.scan4travel.fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.scan4us.scan4travel.R;

/**
 * Created by RanKey on 15/05/2016.
 */
public class ScanFragment extends Fragment{

    TextView tv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        LayoutInflater lf = getActivity().getLayoutInflater();
        View view = lf.inflate(R.layout.scan_fragment, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        init();
    }

    private void init(){
        tv = (TextView) getActivity().findViewById(R.id.txtScanProccesing);

        try {
            int res = com.luxand.FSDK.ActivateLibrary("iwNr3igXqI5XxOwiRJCwemgbX3WAN7fuaf5N5fWgEOIO7ff58TDXJDQyR2Tm3p4XyMIS/zx7jBQeUl/2rS0iauGX/yLlfK91Q9jO29yfQW4oyupkWOGW7ryoT0m86QGIOv0hjJMFJsONVjccL6tP/gRx7aVDCG3ivRzzHdaXwy8=");
            com.luxand.FSDK.Initialize();
            com.luxand.FSDK.SetFaceDetectionParameters(false, false, 100);
            com.luxand.FSDK.SetFaceDetectionThreshold(5);

            if (res == com.luxand.FSDK.FSDKE_OK) {
                tv.setText("FaceSDK activated\n");
            } else {
                tv.setText("Error activating FaceSDK: " + res + "\n");
            }
        }
        catch (Exception e) {
            tv.setText("exception " + e.getMessage());
        }

        if (!processing) {
            processing = true;
            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, RESULT_LOAD_IMAGE);
        }

        // Adding button
        Button buttonLoadImage1 = (Button) getActivity().findViewById(R.id.btnAbrir);
        buttonLoadImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg) {
                if (!processing) {
                    processing = true;
                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, RESULT_LOAD_IMAGE);
                }
            }
        });

        processing = false;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    protected com.luxand.FSDK.HImage oldpicture;
    private static int RESULT_LOAD_IMAGE = 1;
    protected boolean processing;


    // Subclass for async processing of FaceSDK functions.
    // If long-run task runs in foreground - Android kills the process.
    private class DetectFaceInBackground extends AsyncTask<String, Void, String> {
        protected com.luxand.FSDK.FSDK_Features features;
        protected com.luxand.FSDK.TFacePosition faceCoords;
        protected String picturePath;
        protected com.luxand.FSDK.HImage picture;
        protected int result;

        @Override
        protected String doInBackground(String... params) {
            String log = new String();
            picturePath = params[0];
            faceCoords = new com.luxand.FSDK.TFacePosition();
            faceCoords.w = 0;
            picture = new com.luxand.FSDK.HImage();
            result = com.luxand.FSDK.LoadImageFromFile(picture, picturePath);
            if (result == com.luxand.FSDK.FSDKE_OK) {
                result = com.luxand.FSDK.DetectFace(picture, faceCoords);
                features = new com.luxand.FSDK.FSDK_Features();
                if (result == com.luxand.FSDK.FSDKE_OK) {
                    result = com.luxand.FSDK.DetectFacialFeaturesInRegion(picture, faceCoords, features);
                }
            }
            processing = false; //long-running code is complete, now user may push the button
            return log;
        }

        @Override
        protected void onPostExecute(String resultstring) {

            TextView tv = (TextView) getView().findViewById(R.id.txtScanProccesing);

            if (result != com.luxand.FSDK.FSDKE_OK)
                return;

            FaceImageView imageView = (FaceImageView) getView().findViewById(R.id.imageView1);

            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

            tv.setText(resultstring);

            imageView.detectedFace = faceCoords;

            if (features.features[0] != null) // if detected
                imageView.facial_features = features;

            int [] realWidth = new int[1];
            com.luxand.FSDK.GetImageWidth(picture, realWidth);
            imageView.faceImageWidthOrig = realWidth[0];
            imageView.invalidate(); // redraw, marking up faces and features

            if (oldpicture != null)
                com.luxand.FSDK.FreeImage(oldpicture);
            oldpicture = picture;
        }

        @Override
        protected void onPreExecute() {
        }
        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK  && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            TextView tv = (TextView) getView().findViewById(R.id.txtScanProccesing);
            tv.setText("processing...");
            new DetectFaceInBackground().execute(picturePath);
        } else {
            processing = false;
        }
    }

}
