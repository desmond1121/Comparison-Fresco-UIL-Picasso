package com.desmond.frescocomparison;

import android.app.Activity;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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
    private Handler mHandler = null;
    private Button clearCacheButton = null;
    private boolean isFinish = false;
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mRecorder.record(getCurrentTime()-start, getJavaHeapSize()-javaHeapStartSize, getNativeHeapSize()-nativeHeapStartSize);
            if(!isFinish)mHandler.postDelayed(this, 500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_main);

        mHandler = new Handler(Looper.getMainLooper());
        mRuntime = Runtime.getRuntime();
        container = (FrameLayout) findViewById(R.id.container);
        TextView statRecorder = (TextView) findViewById(R.id.stat_recorder);
        mRecorder = new Recorder(statRecorder);

        initLayouts();
        initLoaders();
        View sContainer = findViewById(R.id.spinner_container);
        Spinner spinner = (Spinner) sContainer.findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mHandler.removeCallbacks(mRunnable);
                if (lastPosition != 0) mLoaders.get(lastPosition - 1).stop();
                else if(position == lastPosition)return;
                container.removeAllViews();
                lastPosition = position;
                mRecorder.record(0, 0, 0);
                if (position == 0) return;

                container.addView(mLayouts.get(position - 1));
                mLoaders.get(position - 1).loadImage(mLayouts.get(position - 1));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mHandler.removeCallbacks(mRunnable);
            }
        });

        clearCacheButton = (Button) sContainer.findViewById(R.id.button);
        clearCacheButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < 3; i++) {
                    mLoaders.get(i).clearCache();
                }
                Runtime.getRuntime().gc();
                mRecorder.record(0, 0, 0);
            }
        });

        mRecorder.record(0, 0, 0);
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
            clearCacheButton.setEnabled(false);
            start = getCurrentTime();
            javaHeapStartSize = getJavaHeapSize();
            nativeHeapStartSize = getNativeHeapSize();
            mHandler.postDelayed(mRunnable, 500);
            isFinish = false;
        }

        @Override
        public void onFinish(int success, int fail) {
            clearCacheButton.setEnabled(true);
            isFinish = true;
            end = getCurrentTime();
            mHandler.removeCallbacks(mRunnable);
            Log.i(TAG, "MyLoaderCallback->onFinish --info log-- " + tag + " total time: " + (end - start) + "ms");
            Log.i(TAG, "MyLoaderCallback->onFinish --info log-- " + tag + " java heap change: " + (getJavaHeapSize() - javaHeapStartSize) + "Kb");
            Log.i(TAG, "MyLoaderCallback->onFinish --info log-- " + tag + " native heap change: " + (getNativeHeapSize() - nativeHeapStartSize) + "Kb");
            mRecorder.record(end - start, getJavaHeapSize() - javaHeapStartSize, getNativeHeapSize() - nativeHeapStartSize);
        }
    }

    public long getCurrentTime(){
        return System.currentTimeMillis();
    }

    public long getJavaHeapSize() {
        return (mRuntime.totalMemory() - mRuntime.freeMemory()) / 1000;
    }

    public long getNativeHeapSize() {
        return Debug.getNativeHeapSize() / 1000;
    }

}
