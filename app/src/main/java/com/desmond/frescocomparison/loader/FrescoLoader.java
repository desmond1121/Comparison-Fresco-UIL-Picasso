package com.desmond.frescocomparison.loader;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.widget.GridLayout;

import com.facebook.cache.common.CacheKey;
import com.facebook.common.internal.AndroidPredicates;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

/**
 * Created by Jiayi Yao on 2015/9/18.
 */
public class FrescoLoader extends Loader {
    DraweeController mDraweeController;

    public FrescoLoader(Context context, LoaderCallback callback) {
        super(context, callback);
    }

    @Override
    public void clearCache() {
        ImagePipelineFactory factory = ImagePipelineFactory.getInstance();
        factory.getBitmapMemoryCache().removeAll(null);
        factory.getEncodedMemoryCache().removeAll(null);
        factory.getMainDiskStorageCache().clearAll();
    }

    @Override
    public void loadImage(GridLayout layout) {
        super.loadImage(layout);

        SimpleDraweeView view;
        for (int i = 0; i < 8; i++) {
            view = (SimpleDraweeView) layout.getChildAt(i);
            ImageRequest request = ImageRequestBuilder
                    .newBuilderWithSource(Uri.parse(urls[i]))
                    .setResizeOptions(
                            new ResizeOptions(view.getLayoutParams().width, view.getLayoutParams().height))
                    .setProgressiveRenderingEnabled(true)
                    .build();

            mDraweeController = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setControllerListener(new MyListener())
                    .setOldController(view.getController())
                    .build();

            view.setController(mDraweeController);
        }
    }

    @Override
    public void stop() {
        //Fresco stop request at invisible.
        super.stop();
    }

    class MyListener implements ControllerListener<ImageInfo> {

        @Override
        public void onSubmit(String s, Object o) {
            if (count == 0) mLoaderCallback.onStart();
            count++;
        }

        @Override
        public void onFinalImageSet(String s, ImageInfo imageInfo, Animatable animatable) {
            success++;
            if (success + fail == 8) {
                mLoaderCallback.onFinish(success, fail);
                success = 0;
                fail = 0;
                count = 0;
            }
        }

        @Override
        public void onIntermediateImageSet(String s, ImageInfo imageInfo) {

        }

        @Override
        public void onIntermediateImageFailed(String s, Throwable throwable) {

        }

        @Override
        public void onFailure(String s, Throwable throwable) {
            fail++;
            if (success + fail == 8) {
                mLoaderCallback.onFinish(success, fail);
                success = 0;
                fail = 0;
                count = 0;
            }
        }

        @Override
        public void onRelease(String s) {

        }
    }
}
