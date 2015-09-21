package com.desmond.frescocomparison.loader;

import android.content.Context;
import android.net.Uri;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.desmond.frescocomparison.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

/**
 * Created by Jiayi Yao on 2015/9/19.
 */
public class PicassoLoader extends Loader{
    private Picasso mPicasso;
    private LruCache mCache;

    public PicassoLoader(Context context, LoaderCallback callback) {
        super(context, callback);
        mCache = new LruCache(context);

        mPicasso = new Picasso.Builder(context)
                .memoryCache(mCache)
                .build();
    }

    @Override
    public void clearCache() {
        if(mCache != null) mCache.clear();
    }

    @Override
    public void stop() {
        for(int i=0; i<8; i++){
            mPicasso.cancelRequest((ImageView) mGridLayout.getChildAt(i));
        }
        super.stop();
    }

    @Override
    public void loadImage(GridLayout layout) {
        super.loadImage(layout);
        mPicasso = Picasso.with(mContext);

        ImageView view;
        for(int i=0; i<8; i++){
            view = (ImageView) mGridLayout.getChildAt(i);
            if(i==0)mLoaderCallback.onStart();
            mPicasso.load(urls[i])
                    .placeholder(R.drawable.ic_android_purple_300_48dp)
                    .error(R.drawable.fail)
                    .into(view, new MyCallback());
        }
    }

    class MyCallback implements Callback {

        @Override
        public void onSuccess() {
            success++;
            if(success + fail == 8){
                mLoaderCallback.onFinish(success, fail);
                success = 0;
                fail = 0;
            }
        }

        @Override
        public void onError() {
            fail++;
            if(success + fail == 8){
                mLoaderCallback.onFinish(success, fail);
                success = 0;
                fail = 0;
            }
        }
    }
}
