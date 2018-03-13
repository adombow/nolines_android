package com.nolines.nolines;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ARActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String TAG = "ARActivity";

    private CameraBridgeViewBase mOpenCvCameraView;
    Scalar RED = new Scalar(255, 0, 0);
    Scalar GREEN = new Scalar(0, 255, 0);
    FeatureDetector detector;
    DescriptorExtractor descriptor;
    DescriptorMatcher matcher;
    Mat descriptors2,descriptors1;
    Mat img1;
    MatOfKeyPoint srcKeypoints, inputKeypoints;


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    //updateCameraViewRotation(mOpenCvCameraView);
                    mOpenCvCameraView.enableView();

                    try {
                        initializeOpenCVDependencies();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    private void initializeOpenCVDependencies() throws IOException {
        mOpenCvCameraView.enableView();

        detector = FeatureDetector.create(FeatureDetector.ORB);
        descriptor = DescriptorExtractor.create(DescriptorExtractor.ORB);
        matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);

        /* Convert reference image to camera format  */
        img1 = new Mat();
        AssetManager assetManager = getAssets();
        InputStream istr = assetManager.open("poster.jpg");
        Bitmap bitmap = BitmapFactory.decodeStream(istr);
        Utils.bitmapToMat(bitmap, img1);
        Imgproc.cvtColor(img1, img1, Imgproc.COLOR_RGB2GRAY);
        img1.convertTo(img1, 0); //converting the image to match with the type of the cameras image

        descriptors1 = new Mat();
        srcKeypoints = new MatOfKeyPoint();

        /* Compute feature vector of reference image */
        detector.detect(img1, srcKeypoints);
        descriptor.compute(img1, srcKeypoints, descriptors1);

        Log.i(TAG,"Reference Image H: " + img1.height() + " W: " + img1.width());
        Log.i(TAG,"Finished Initializing OpenCVDependencies");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);;

        setContentView(R.layout.activity_ar);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.tutorial1_activity_java_surface_view);

        mOpenCvCameraView.setMaxFrameSize(1000, 1000);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        //updateCameraViewRotation(mOpenCvCameraView);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }

    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        Log.i(TAG,"onCameraViewStarted H: "+ height + "W: " + width);
    }

    public void onCameraViewStopped() {
    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        //return static_test(inputFrame.rgba());
        //System.out.println("H: "+ inputFrame.gray().height() + "W: "+ inputFrame.gray().width());
        return recognize(inputFrame.rgba());
    }

    public Mat recognize(Mat aInputFrame){

        Imgproc.cvtColor(aInputFrame, aInputFrame, Imgproc.COLOR_RGB2GRAY);
        descriptors2 = new Mat();
        inputKeypoints = new MatOfKeyPoint();

        /* Compute feature vector of new frame */
        detector.detect(aInputFrame, inputKeypoints);
        descriptor.compute(aInputFrame, inputKeypoints, descriptors2);

        /* Matching */

        MatOfDMatch matches = new MatOfDMatch();
        if (img1.type() == aInputFrame.type() && descriptors1.cols() == descriptors2.cols()) {
                matcher.match(descriptors1, descriptors2, matches);
        } else {
            return aInputFrame;
        }

        List<DMatch> matchesList = matches.toList();

        Double max_dist = 0.0;
        Double min_dist = 100.0;

        // Find max and min distances in matches
        for (int i = 0; i < matchesList.size(); i++) {
            Double dist = (double) matchesList.get(i).distance;
            if (dist < min_dist)
                min_dist = dist;
            if (dist > max_dist)
                max_dist = dist;
        }

        LinkedList<DMatch> good_matches = new LinkedList<DMatch>();
        for (int i = 0; i < matchesList.size(); i++) {
            if (matchesList.get(i).distance <= (2 * min_dist))
                good_matches.addLast(matchesList.get(i));
        }

        MatOfDMatch goodMatches = new MatOfDMatch();
        goodMatches.fromList(good_matches);

        Mat outputImg = new Mat();
        MatOfByte drawnMatches = new MatOfByte();
        if (aInputFrame.empty() || aInputFrame.cols() < 1 || aInputFrame.rows() < 1) {
            return aInputFrame;
        }


        Features2d.drawKeypoints(aInputFrame, inputKeypoints, outputImg, GREEN,0);
        /*Features2d.drawMatches(
                img1,
                srcKeypoints,
                aInputFrame,
                inputKeypoints,
                goodMatches,
                outputImg,
                GREEN,
                RED,
                drawnMatches,
                0);*/

        if(good_matches.size() > 5) {

            LinkedList<Point> srcPointList = new LinkedList<Point>();
            LinkedList<Point> inputPointList = new LinkedList<Point>();

            List<KeyPoint> srcKeypointsList = srcKeypoints.toList();
            List<KeyPoint> inputKeypointsList = inputKeypoints.toList();

            for (int i = 0; i < good_matches.size(); i++) {
                srcPointList.addLast(srcKeypointsList.get(good_matches.get(i).queryIdx).pt);
                inputPointList.addLast(inputKeypointsList.get(good_matches.get(i).trainIdx).pt);
            }

            MatOfPoint2f srcM = new MatOfPoint2f();
            srcM.fromList(srcPointList);

            MatOfPoint2f inputM = new MatOfPoint2f();
            inputM.fromList(inputPointList);

            Mat hg = Calib3d.findHomography(srcM, inputM);

            Mat srcCorners = new Mat(4, 1, CvType.CV_32FC2);
            Mat inputCorners = new Mat(4, 1, CvType.CV_32FC2);

            srcCorners.put(0, 0, new double[]{0, 0});
            srcCorners.put(1, 0, new double[]{img1.cols(), 0});
            srcCorners.put(2, 0, new double[]{img1.cols(), img1.rows()});
            srcCorners.put(3, 0, new double[]{0, img1.rows()});

            Core.perspectiveTransform(srcCorners, inputCorners, hg);

            Imgproc.line(outputImg, new Point(inputCorners.get(0, 0)), new Point(inputCorners.get(1, 0)), RED, 4);
            Imgproc.line(outputImg, new Point(inputCorners.get(1, 0)), new Point(inputCorners.get(2, 0)), RED, 4);
            Imgproc.line(outputImg, new Point(inputCorners.get(2, 0)), new Point(inputCorners.get(3, 0)), RED, 4);
            Imgproc.line(outputImg, new Point(inputCorners.get(3, 0)), new Point(inputCorners.get(0, 0)), RED, 4);
        }

        Imgproc.resize(outputImg, outputImg, aInputFrame.size());

        return outputImg;

    }


    private void updateCameraViewRotation(CameraBridgeViewBase mOpenCvCameraView){
        final Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        switch (display.getRotation()) {
            case Surface.ROTATION_0:
                System.out.println("SCREEN_ORIENTATION_PORTRAIT");
                mOpenCvCameraView.setUserRotation(90);
                break;

            case Surface.ROTATION_90:
                System.out.println("SCREEN_ORIENTATION_LANDSCAPE");
                mOpenCvCameraView.setUserRotation(0);
                break;

            case Surface.ROTATION_180:
                System.out.println("SCREEN_ORIENTATION_REVERSE_PORTRAIT");
                break;

            case Surface.ROTATION_270:
                System.out.println("SCREEN_ORIENTATION_REVERSE_LANDSCAPE");
                // This one is broken
                mOpenCvCameraView.setUserRotation(-90);
                break;
        }
    }
}
