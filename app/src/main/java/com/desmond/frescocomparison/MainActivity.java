package com.desmond.frescocomparison;

import android.app.Activity;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.desmond.frescocomparison.loader.FrescoLoader;
import com.desmond.frescocomparison.loader.Loader;
import com.desmond.frescocomparison.loader.PicassoLoader;
import com.desmond.frescocomparison.loader.UILLoader;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.decoder.SimpleProgressiveJpegConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jiayi Yao on 2015/9/18.
 */
public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    private long javaHeapStartSize = 0;
    private long nativeHeapStartSize = 0;
    private long end;
    private long start = 0;

    private int lastPosition = 0;
    private FrameLayout container = null;
    private List<GridLayout> mLayouts = null;
    private List<Loader> mLoaders = null;
    private Recorder mRecorder = null;
    private Runtime mRuntime = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_main);

        mRuntime = Runtime.getRuntime();
        container = (FrameLayout) findViewById(R.id.container);
        TextView statRecorder = (TextView) findViewById(R.id.stat_recorder);
        mRecorder = new Recorder(statRecorder);

        initLayouts();
        initLoaders();

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mLoaders.get(lastPosition).stop();
                container.removeAllViews();
                container.addView(mLayouts.get(position));
                mLoaders.get(position).loadImage(mLayouts.get(position));
                lastPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mRecorder.record(0, 0, 0);
    }

    private void initFresco(){

    }

    private void initLayouts() {
        mLayouts = new ArrayList<>();
        mLayouts.add((GridLayout) LayoutInflater.from(this).inflate(R.layout.fresco_layout, null));
        mLayouts.add((GridLayout) LayoutInflater.from(this).inflate(R.layout.normal_layout, null));
        mLayouts.add((GridLayout) LayoutInflater.from(this).inflate(R.layout.normal_layout, null));
    }

    private void initLoaders() {
        mLoaders = new ArrayList<>();
        mLoaders.add(new FrescoLoader(this, new MyLoaderCallback("fresco")));
        mLoaders.add(new UILLoader(this, new MyLoaderCallback("UIL")));
        mLoaders.add(new PicassoLoader(this, new MyLoaderCallback("Picasso")));
    }

    class MyLoaderCallback implements Loader.LoaderCallback {
        private String tag;

        public MyLoaderCallback(String tag) {
            this.tag = tag;
        }

        @Override
        public void onStart() {
            start = System.currentTimeMillis();
            javaHeapStartSize = getJavaHeapSize();
            nativeHeapStartSize = getNativeHeapSize();
        }

        @Override
        public void onFinish(int success, int fail) {
            end = System.currentTimeMillis();
            Log.i(TAG, "MyLoaderCallback->onFinish --info log-- " + tag + " total time: " + (end - start) + "ms");
            Log.i(TAG, "MyLoaderCallback->onFinish --info log-- " + tag + " java heap change: " + (getJavaHeapSize() - javaHeapStartSize) + "Kb");
            Log.i(TAG, "MyLoaderCallback->onFinish --info log-- " + tag + " native heap change: " + (getNativeHeapSize() - nativeHeapStartSize) + "Kb");
            mRecorder.record(end - start, getJavaHeapSize() - javaHeapStartSize, getNativeHeapSize() - nativeHeapStartSize);
        }
    }

    public long getJavaHeapSize() {
        return (mRuntime.totalMemory() - mRuntime.freeMemory()) / 1000;
    }

    public long getNativeHeapSize() {
        return Debug.getNativeHeapSize() / 1000;
    }

//    public void count() {
//        count++;
//        if (count >= 8) {
//            Log.i(TAG, "MyListener->count --info log-- complete! success: " + (8 - failed) + " fail: " + failed);
//            end = System.currentTimeMillis();
//            Log.i(TAG, "MyListener->count --info log-- total time: " + (end - start));
//            Log.i(TAG, "MyListener->count --info log-- native heap size change" + Debug.getNativeHeapAllocatedSize() / 1000 + "/" + Debug.getNativeHeapSize() / 1000);
//            Log.i(TAG, "MyListener->count --info log-- java heap size " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000);
//            long nativeSize = Debug.getNativeHeapSize() / 1000;
//            long javaSize = Runtime.getRuntime().totalMemory() / 1000 - Runtime.getRuntime().freeMemory() / 1000;
//            Log.i(TAG, "MyListener->count --info log-- native heap size change: " + (nativeSize - nativeHeapStartSize));
//            Log.i(TAG, "MyListener->count --info log-- java heap size change: " + (javaSize - javaHeapStartSize));
//            offset++;
//            count = 0;
//            failed = 0;
//        }
//    }
}
