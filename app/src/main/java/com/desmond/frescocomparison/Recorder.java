package com.desmond.frescocomparison;

import android.os.Debug;
import android.widget.TextView;

/**
 * Created by Jiayi Yao on 2015/9/19.
 */
public class Recorder {
    private static final String TAG = "Recorder";
    private Runtime mRuntime;
    private TextView mTextView;

    public Recorder(TextView textView){
        mTextView = textView;
        mRuntime = Runtime.getRuntime();
    }

    public void record(long usedTime, long javaHeapChange, long nativeHeapChange){
        final StringBuilder sb = new StringBuilder();
        append(sb, "Loading time:", usedTime, "ms");
        append(sb, "Java heap alloc size:", getJavaHeapSize(), "Kb");
        append(sb, "Java Heap change:", javaHeapChange, "Kb");
        append(sb, "Native heap alloc size:", getNativeHeapSize(), "Kb");
        append(sb, "Native heap change:", nativeHeapChange, "Kb");
        mTextView.setText(sb.toString());
    }

    private void append(StringBuilder sb, String text, long number, String unit){
        sb.append(text);
        sb.append(number);
        sb.append(unit);
        sb.append("\n");
    }

    private long getJavaHeapSize(){
        return (mRuntime.totalMemory() - mRuntime.freeMemory())/1000;
    }

    private long getNativeHeapSize(){
        return Debug.getNativeHeapSize()/1000;
    }
}
