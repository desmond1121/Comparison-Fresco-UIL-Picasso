package com.desmond.frescocomparison.loader;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;

import com.desmond.frescocomparison.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Created by Jiayi Yao on 2015/9/18.
 */
public class UILLoader extends Loader{

    private ImageLoader mImageLoader;
    private DisplayImageOptions mOptions;

    public UILLoader(Context context, LoaderCallback callback) {
        super(context, callback);
        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(ImageLoaderConfiguration.createDefault(context));
        mOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_android_purple_300_48dp)
                .showImageOnFail(R.drawable.fail)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
    }

    @Override
    public void clearCache() {
        mImageLoader.clearMemoryCache();
        mImageLoader.clearDiskCache();
    }

    @Override
    public void loadImage(GridLayout layout) {
        super.loadImage(layout);

        ImageView view;
        for(int i=0; i<8; i++){
            view = (ImageView)mGridLayout.getChildAt(i);
            mImageLoader.displayImage(urls[i], view, mOptions, new MyListener());
        }
    }

    @Override
    public void stop() {
        for(int i=0; i<8; i++){
            mImageLoader.cancelDisplayTask((ImageView) mGridLayout.getChildAt(i));
        }
        super.stop();
    }

    class MyListener implements ImageLoadingListener {

        @Override
        public void onLoadingStarted(String imageUri, View view) {
            if(count == 0)mLoaderCallback.onStart();
            count++;
        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            fail++;
            if(fail+success == 8){
                mLoaderCallback.onFinish(success, fail);
                fail = 0;
                success = 0;
                count = 0;
            }
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            success++;
            if (fail+success == 8){
                mLoaderCallback.onFinish(success, fail);
                fail = 0;
                success = 0;
                count = 0;
            }
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {

        }
    }
}
