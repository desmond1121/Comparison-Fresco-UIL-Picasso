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
            "http://i1.tietuku.com/3b45754d9bbadd9e.png",
            "http://i1.tietuku.com/c19cbb37282582ee.png",
            "http://i1.tietuku.com/d6248b8148174547.png",
            "http://i1.tietuku.com/4c556b2e77cc0c18.png",
            "http://i1.tietuku.com/f7e2aabbb2807fd9.png",
            "http://i1.tietuku.com/ea463543e6750b79.jpg",
            "http://i1.tietuku.com/8f9e99ce28cf3161.png",
            "http://i1.tietuku.com/7aa38cb091d48490.png"};

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
