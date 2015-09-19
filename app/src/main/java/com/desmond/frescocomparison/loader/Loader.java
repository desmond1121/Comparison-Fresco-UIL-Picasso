package com.desmond.frescocomparison.loader;

import android.content.Context;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by Jiayi Yao on 2015/9/18.
 */
public abstract class Loader {
    private static final String TAG = "Loader";

    protected String[] urls = {
        "http://i.imgur.com/x8P4vQBl.jpg",
        "http://i.imgur.com/iPlS1VYl.jpg",
        "http://i.imgur.com/c74rqsCl.jpg",
        "http://i.imgur.com/96C2rSjl.jpg",
        "http://i.imgur.com/0ZmMlKul.jpg",
        "http://i.imgur.com/28lzY1yl.jpg",
        "http://i.imgur.com/C0vahU8l.jpg",
        "http://i.imgur.com/PtveszVl.jpg"
    };

    public interface LoaderCallback {
        void onStart();

        void onFinish(int success, int fail);
    }

    protected int count = 0;
    protected int success = 0;
    protected int fail = 0;

    protected LoaderCallback mLoaderCallback;
    protected Context mContext;
    protected GridLayout mGridLayout;

    public void loadImage(GridLayout layout) {
        mGridLayout = layout;
        setChildVisibility(true);
    }

    public void stop(){
        count = 0;
        fail = 0;
        setChildVisibility(false);
    }

    public Loader(Context context, LoaderCallback callback) {
        this.mContext = context;
        this.mLoaderCallback = callback;
    }

    protected void setChildVisibility(boolean visible) {
        if(mGridLayout == null)return;
        int size = mGridLayout.getChildCount();

        for (int i = 0; i < size; i++) {
            mGridLayout.getChildAt(i).setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }
}
