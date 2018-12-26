//
// Created by KJ on 2018-12-02.
//
#include <jni.h>
#include "com_example_leehangeol_sakamaka_ARactivity.h"
#include <string>
#include <opencv2/opencv.hpp>

using namespace cv;
using namespace std;

extern "C" {

JNIEXPORT void JNICALL Java_com_example_leehangeol_sakamaka_ARactivity_loadImage
  (JNIEnv *env, jobject instance,
   jstring imageFileName_, jlong img)
  {
            Mat &img_input = *(Mat *) img;
            Mat image;
            const char *imageFileName = env->GetStringUTFChars(imageFileName_, JNI_FALSE);
            // TODO
            string baseDir(imageFileName);
            //baseDir.append(imageFileName);
            const char *pathDir = baseDir.c_str();
            img_input = imread(pathDir,IMREAD_COLOR);
            cvtColor(img_input,img_input,COLOR_BGR2RGB);


            image.create(img_input.size(),CV_8UC4);
  }

/*
 * Class:     com_example_leehangeol_sakamaka_ARactivity
 * Method:    imageprocessing
 * Signature: (JJ)V
 */
JNIEXPORT void JNICALL Java_com_example_leehangeol_sakamaka_ARactivity_imageprocessing
  (JNIEnv *env, jobject instance,
   jlong inputImage,
   jlong outputImage)
  {
         // TODO
            Mat &img_input = *(Mat *) inputImage;
            Rect rectangle(50 ,70, img_input.cols -80,img_input.rows -100);

            Mat image;
            cvtColor(img_input,image,COLOR_BGR2RGBA);

            Mat &img_output = *(Mat *) outputImage;
            //cvtColor(img_output,img_output,COLOR_BGR2RGBA);

            Mat result;
            result.create(img_input.size(),CV_8UC3);


            Mat bg_Model ;//= *(Mat *) outputImage;;
            //bg_Model.create(img_input.size(),CV_8UC4);
            Mat fg_Model ;//= *(Mat *) outputImage;;
            //fg_Model.create(img_input.size(),CV_8UC4);
            grabCut(img_input,result,rectangle,bg_Model,fg_Model,1,GC_INIT_WITH_RECT);
            compare(result,GC_PR_FGD, result,CMP_EQ);

            Mat foreground;
            foreground.create(img_input.size(),CV_8UC3);
            foreground = Scalar(0,0,0);
            img_input.copyTo(foreground,result);
           // Mat out = foreground(Range(50,foreground.cols),Range(70,foreground.rows));
            img_output = foreground;
  }
}