package com.example.leehangeol.sakamaka;

/*
 * Copyright 2018 Google LLC. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.PixelCopy;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.opencv.core.CvType.CV_8UC4;

/**
 * This is an example activity that uses the Sceneform UX package to make common AR tasks easier.
 */
public class ARactivity extends AppCompatActivity {

    static {
        System.loadLibrary("opencv_java4");
        System.loadLibrary("native-lib");
    }

    private String mImgPath = null;
    private String mImgTitle = null;
    private String mImgOrient = null;
    private Mat img_input;
    private Mat img_output;

    private final int REQ_CODE_SELECT_IMAGE = 1001;

    private int imagecnt = 0;

    private static final String TAG = ARactivity.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;

    private ArFragment arFragment;
    private ViewRenderable image;

    private View header;

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = VERSION_CODES.N)
    @Override
    @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
    // CompletableFuture requires api level 24
    // FutureReturnValueIgnored is not valid
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (!checkIsSupportedDeviceOrFinish(this)) {
            return;
        }

        header = getLayoutInflater().inflate(R.layout.imgboard,null);

        getGallery();

        setContentView(R.layout.activity_ux);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

        ViewRenderable.builder()
                .setView(this, header)
                .build()
                .thenAccept(renderable -> image = renderable);


//         When you build a Renderable, Sceneform loads its resources in the background while returning
//         a CompletableFuture. Call thenAccept(), handle(), or check isDone() before calling get().
//        ModelRenderable.builder()
//                .setSource(this, R.raw.andy)
//                .build()
//                .thenAccept(renderable -> andyRenderable = renderable)
//                .exceptionally(
//                        throwable -> {
//                            Toast toast =
//                                    Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
//                            toast.setGravity(Gravity.CENTER, 0, 0);
//                            toast.show();
//                            return null;
//                        });

        arFragment.setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {

                    if (image == null) {
                        return;
                    }

                    if(imagecnt == 1)
                    {
                        return;
                    }
                    // Create the Anchor.
                    Anchor anchor = hitResult.createAnchor();
                    AnchorNode anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(arFragment.getArSceneView().getScene());

                    // Create the transformable andy and add it to the anchor.
                    TransformableNode andy = new TransformableNode(arFragment.getTransformationSystem());
                    andy.setParent(anchorNode);
                    image.setShadowCaster(false);
                    andy.setRenderable(image);
                    andy.select();

                    imagecnt++;
                });
    }

    /**
     * Returns false and displays an error message if Sceneform can not run, true if Sceneform can run
     * on this device.
     * <p>
     * <p>Sceneform requires Android N on the device as well as OpenGL 3.0 capabilities.
     * <p>
     * <p>Finishes the activity if Sceneform can not run
     */

    public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
        if (Build.VERSION.SDK_INT < VERSION_CODES.N) {
            Log.e(TAG, "Sceneform requires Android N or later");
            Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show();
            activity.finish();
            return false;
        }
        String openGlVersionString =
                ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
                        .getDeviceConfigurationInfo()
                        .getGlEsVersion();
        if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later");
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG).show();
            activity.finish();
            return false;
        }
        return true;
    }

    @RequiresApi(api = VERSION_CODES.N)
    public void onSavePhoto(View view){
        takePhoto();
        Toast toast = Toast.makeText(ARactivity.this, "사진이 저장되고 있습니다. 잠시만 기다려주세요.", Toast.LENGTH_LONG);
        toast.show();
    }

    private void saveBitmapToDisk(Bitmap bitmap, String filename) throws IOException {

        File out = new File(filename);
        if (!out.getParentFile().exists()) {
            out.getParentFile().mkdirs();
        }

        try (FileOutputStream outputStream = new FileOutputStream(filename);
             ByteArrayOutputStream outputData = new ByteArrayOutputStream()) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputData);
            outputData.writeTo(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException ex) {
            throw new IOException("Failed to save bitmap to disk", ex);
        }
    }

    private String generateFilename() {
        String date =
                new SimpleDateFormat("yyyyMMddHHmmss", java.util.Locale.getDefault()).format(new Date());
        return Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + File.separator + "Screenshots/" + date + "_screenshot.jpg";
    }

    @RequiresApi(api = VERSION_CODES.N)
    private void takePhoto() {
        final String filename = generateFilename();
        ArSceneView view = arFragment.getArSceneView();

        // Create a bitmap the size of the scene view.
        final Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
                Bitmap.Config.ARGB_8888);

        // Create a handler thread to offload the processing of the image.
        final HandlerThread handlerThread = new HandlerThread("PixelCopier");
        handlerThread.start();
        // Make the request to copy.
        PixelCopy.request(view, bitmap, (copyResult) -> {
            if (copyResult == PixelCopy.SUCCESS) {
                try {
                    saveBitmapToDisk(bitmap, filename);
                } catch (IOException e) {
                    Toast toast = Toast.makeText(ARactivity.this, e.toString(),
                            Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }

                File photoFile = new File(filename);

                Uri photoURI = FileProvider.getUriForFile(ARactivity.this,
                        ARactivity.this.getPackageName() + ".ar.codelab.name.provider",
                        photoFile);
                Intent intent = new Intent(Intent.ACTION_VIEW, photoURI);
                intent.setDataAndType(photoURI, "image/*");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(photoFile)));

                startActivity(intent);

            } else {
                Toast toast = Toast.makeText(ARactivity.this,
                        "Failed to copyPixels: " + copyResult, Toast.LENGTH_LONG);
                toast.show();
            }
            handlerThread.quitSafely();
        }, new Handler(handlerThread.getLooper()));
    }

    public void onClick(View view) {
        getGallery();
    }

    private void getGallery()
    {
        Intent intent = null;

        // 안드로이드 KitKat(level 19)부터는 ACTION_PICK 이용
        if(Build.VERSION.SDK_INT >= 19)
        {
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        else
        {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        }

        intent.setType("image/*");
        startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 선택된 사진을 받아 서버에 업로드한다.
        if (requestCode == REQ_CODE_SELECT_IMAGE)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                Uri uri = data.getData();
                getImageNameToUri(uri);
                try
                {

                    Mat result =new Mat(img_output.size(),CV_8UC4);
                    //Imgproc.cvtColor(result, img_output, Imgproc.COLOR_BGR2BGRA);

                    for (int i=0; i<img_output.rows(); i++){
                        for (int j=0; j<img_output.cols(); j++){
                            double[] buff = img_output.get(i, j);
                            double[] rebuff = result.get(i, j);

                            if (buff[0] == 0 && buff[1] == 0 && buff[2] == 0) {
                                rebuff[0] = buff[0];
                                rebuff[1] = buff[1];
                                rebuff[2] = buff[2];
                                rebuff[3] = 0;
                            }
                            else{
                                rebuff[0] = buff[0];
                                rebuff[1] = buff[1];
                                rebuff[2] = buff[2];
                                rebuff[3] = 255;
                            }
                            result.put(i, j, rebuff);
                        }
                    }


                    Bitmap bitmapOutput = Bitmap.createBitmap(result.cols(), result.rows(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(result, bitmapOutput);

                    Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    ImageView img = (ImageView)header.findViewById(R.id.imageCard);
                    img.setImageBitmap(bitmapOutput);

                    //img.set
                    Toast toast = Toast.makeText(ARactivity.this,
                            "사진이 설정되었습니다." , Toast.LENGTH_LONG);
                    toast.show();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Toast toast = Toast.makeText(ARactivity.this,
                            "사진이 설정 실패습니다." , Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        }
    }

    /**
     * URI 정보를 이용하여 사진 정보 가져옴
     */
    private void getImageNameToUri(Uri data) {
        String[] proj = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.ORIENTATION
        };

        Cursor cursor = this.getContentResolver().query(data, proj, null, null, null);
        cursor.moveToFirst();

        int column_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        int column_title = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
        int column_orientation = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.ORIENTATION);
        mImgPath = cursor.getString(column_data);


        imageprocess_and_showResult();
    }

    /////////////////////// opencv 추가 부분 ////////////////////

    private void imageprocess_and_showResult() {

        img_input = new Mat();
        img_output = new Mat();
        loadImage(mImgPath, img_input.getNativeObjAddr());
        imageprocessing(img_input.getNativeObjAddr(), img_output.getNativeObjAddr());
    }
    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native void loadImage(String imageFileName, long img);
    public native void imageprocessing(long inputImage, long outputImage);
}