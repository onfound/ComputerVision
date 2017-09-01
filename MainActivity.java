package com.example.ilyad.opencvtest;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.*;
import org.opencv.android.*;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private CameraBridgeViewBase mOpenCvCameraView;
    private Mat mat;
    private float x = 100;
    private float y = 100;
    private float x1 = 300;
    private float y1 = 300;

    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        mOpenCvCameraView = findViewById(R.id.view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: // нажатие
                        for (int i = 0; i < motionEvent.getPointerCount(); i++) {
                            switch (motionEvent.getPointerId(i)) {
                                case 0:
                                    x = motionEvent.getX(0);
                                    y = motionEvent.getY(0);
                                    break;
                                case 1:
                                    x = motionEvent.getX(1);
                                    y = motionEvent.getY(1);
                            }
                        }
                        break;
                    case MotionEvent.ACTION_MOVE: // движение
                        try {
                            for (int i = 0; i < motionEvent.getPointerCount(); i++) {
                                switch (motionEvent.getPointerId(i)) {
                                    case 0:
                                        x1 = motionEvent.getX(0);
                                        y1 = motionEvent.getY(0);
                                        break;
                                    case 1:
                                        x = motionEvent.getX(1);
                                        y = motionEvent.getY(1);
                                        break;
                                }
                            }
                        } catch (Exception ignored) {

                        }
                        break;
                    case MotionEvent.ACTION_UP: // отпускание
                    case MotionEvent.ACTION_CANCEL:

                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();
        OpenCVLoader.initDebug();
        mOpenCvCameraView.enableView();
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mat = new Mat(height, width, CvType.CV_8UC4);
    }

    public void onCameraViewStopped() {
        mat.release();
    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mat = inputFrame.rgba();
        Point p1 = new Point(x, y);
        Point p2 = new Point(x1, y1);
        Rect roi = new Rect(p1, p2);
        Imgproc.rectangle(mat, p1, p2, new Scalar(255, 255, 255), 5);
        Mat matRoi = mat.submat(roi);

        /*запись в Pictures в корне BGR*/
        Mat matGrey = new Mat();
        Imgproc.cvtColor(matRoi, matGrey, Imgproc.COLOR_BGR2GRAY);
        Imgcodecs.imwrite(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/matGray.png", matGrey);

        /*запись RGB instead BRG*/
        Imgproc.cvtColor(matRoi, matRoi, Imgproc.COLOR_BGR2RGB);
        Imgcodecs.imwrite(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/matROI.png", matRoi);

        /*сглаживание*/
        Mat matMorph = new Mat();
        Mat kernel = new Mat(new Size(10, 10), CvType.CV_8U);
        Imgproc.morphologyEx(matGrey, matMorph, Imgproc.MORPH_OPEN, kernel);
        Imgcodecs.imwrite(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/matMorph.png", matMorph);

        Mat bin = new Mat();
        Core.inRange(matGrey, new Scalar(40),new Scalar(150),bin);

        /*поиск контуров*/
        List<MatOfPoint> list = new ArrayList<>();
        Mat hierarhy = new Mat();
        Imgproc.findContours(matGrey, list, hierarhy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);



        /*отрисовка контуров*/
        Mat drawCont = new Mat();




        System.out.println("кол-во контуров = " + list.size());
        for (int contourIdx = 0; contourIdx <list.size() ; contourIdx++) {
            Imgproc.drawContours(drawCont, list, contourIdx, new Scalar(0, 255, 0), 5);
        }
        Imgcodecs.imwrite(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/drawCont.png", drawCont);
        Imgproc.Canny(matGrey,drawCont,10,100,3,true);

        Imgcodecs.imwrite(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+ "/drawCont.png",drawCont);
//        Imgproc.contourArea();
        return mat;
    }
}
