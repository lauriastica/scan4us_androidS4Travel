/*
 * FaceSDK Library Sample
 * Copyright (C) 2013 Luxand, Inc.
 * 
 * FaceImageView - modified ImageView to display photo and mark faces/features on draw
 * the class used for imageView1 object of MainActivity (res/layout/activity_main.xml)
 */

package com.scan4us.scan4travel.fragments;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class FaceImageView extends ImageView {
	private Paint painter, painter2;
    public com.luxand.FSDK.TFacePosition detectedFace;
    public com.luxand.FSDK.FSDK_Features facial_features;
    public int faceImageWidthOrig;
    
	public void Init() {
		faceImageWidthOrig = 0;
		facial_features = null;
		detectedFace = new com.luxand.FSDK.TFacePosition();
		detectedFace.w = 0;
		painter = new Paint();
        painter.setColor(Color.BLUE);
        painter.setStrokeWidth(1);
        painter.setStyle(Paint.Style.STROKE);
		painter2 = new Paint();
		painter2.setColor(Color.RED);
		painter2.setStrokeWidth(1);
		painter2.setStyle(Paint.Style.STROKE);
	}
	
	public FaceImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		Init();
	}
	public FaceImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Init();
	}
	public FaceImageView(Context context) {
		super(context);
		Init();
	}
	
	//display detected faces, features or eyes
	public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
		if (faceImageWidthOrig > 0 && detectedFace.w > 0) {
			//scale detected face
	        int displayedWidth = this.getWidth();
	        //int displayedHeight = this.getHeight();

	        double ratio = displayedWidth/(faceImageWidthOrig*1.0);
			int xc = (int)(detectedFace.xc * ratio);
			int yc = (int)(detectedFace.yc * ratio);
			int w = (int)(detectedFace.w * ratio);
        
			//draw detected face
            canvas.drawRect(xc - w/2, yc - w/2, xc + w/2, yc + w, painter2);
        }
        
        if (faceImageWidthOrig > 0 && facial_features != null) {
        	int displayedWidth = this.getWidth();
			double ratio = displayedWidth/(faceImageWidthOrig*1.0);
	        double ratio2 = displayedWidth/(faceImageWidthOrig*1.2);
        	for (int i=0; i< com.luxand.FSDK.FSDK_FACIAL_FEATURE_COUNT; ++i) { //for all facial features
        		//scale detected facial features
		        int cx = (int)(facial_features.features[i].x * ratio);
		        int cy = (int)(facial_features.features[i].y * ratio2);
				canvas.drawCircle(cx, cy + cy/3, 6, painter);
        	}
        }
    }
	
	//remove white borders of image to mark face correctly
	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
         Drawable drawable = getDrawable();
         if (drawable != null) {
                 int width = MeasureSpec.getSize(widthMeasureSpec);
                 int height = (int) Math.ceil((float) width * (float) drawable.getIntrinsicHeight() / (float) drawable.getIntrinsicWidth());
                 setMeasuredDimension(width, height);
         } else {
                 super.onMeasure(widthMeasureSpec, heightMeasureSpec);
         }
    }
}
