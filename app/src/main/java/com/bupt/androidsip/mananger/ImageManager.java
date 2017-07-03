package com.bupt.androidsip.mananger;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.os.Looper;
import android.widget.ImageView;


import com.bupt.androidsip.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by xusong on 2017/7/3.
 * About:
 */

public class ImageManager {
    private static ImageManager instance;
    private ImageLoader imageLoader;
    // options
    private DisplayImageOptions portraitOption;
    private DisplayImageOptions captchaOption;
    private DisplayImageOptions defaultOption;

    private ImageManager(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).threadPoolSize(5).build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);

        portraitOption = new DisplayImageOptions.Builder()
                //.showImageForEmptyUri(R.drawable.ic_defult_profile)
                //.showImageOnLoading(R.drawable.ic_defult_profile)
                .build();

        captchaOption = new DisplayImageOptions.Builder() // setting
                .cacheInMemory(false)
                .showImageForEmptyUri(null)
                .showImageOnLoading(null)
                .showImageOnFail(null)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .cacheOnDisk(false)
                .build();

        defaultOption = new DisplayImageOptions.Builder() // setting
                .cacheInMemory(true)
                .cacheOnDisk(true)
               // .showImageForEmptyUri(R.drawable.bg_bitmap_profile)
               // .showImageOnFail(R.drawable.bg_bitmap_profile)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    public static ImageManager GetInstance(Context context) {
        if (instance == null) {
            synchronized (ImageManager.class) {
                if (instance == null) {
                    instance = new ImageManager(context);
                }
            }
        }
        return instance;
    }

    public static ImageManager GetInstance() {
        return instance;
    }

    public void displayPortrait(String url, ImageView imageView) {
        imageLoader.displayImage(url, imageView, portraitOption);
    }
    public void disPlayLocalDrawable(ImageView view, Drawable drawable){
        //imageLoader.dis
    }

    public void displayCaptcha(String url, ImageView imageView) {
        imageLoader.displayImage(url, imageView, captchaOption);
    }

    public void displayDefaultImage(String url, ImageView imageView) {
        imageLoader.displayImage(url, imageView, defaultOption);
    }







    //裁剪图片
    public Bitmap clipImage(String imageUri, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageUri, options);


        int beWidth = options.outWidth / width;
        int beHeight = options.outHeight / height;
        int size = beWidth < beHeight ? beWidth : beHeight;


        options.inJustDecodeBounds = false;
        options.inSampleSize = size <= 1 ? 1 : size;
        Bitmap bitmap = BitmapFactory.decodeFile(imageUri, options);
        //
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        int degree = readPhotoRotateDegree(imageUri);
        if (degree > 0) {
            bitmap = rotateBitmap(degree, bitmap);
        }

        return bitmap;
    }


    public Bitmap rotateBitmap(int degree, Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap newbitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
        return newbitmap;
    }

    public int readPhotoRotateDegree(String imageUri) {
        int degree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (exif != null) {
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        }
        return degree;
    }
    private ImageManager(){

    }
    public static ImageManager getInstance(){
        return instance;
    }
}
