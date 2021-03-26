package com.example.contactless_attendance;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.Toast;
import com.google.android.gms.common.images.Size;
import com.google.android.gms.vision.CameraSource;

import java.io.IOException;

public class CameraPreview extends ViewGroup {
    private Context context;
    private SurfaceView surfaceView;
    private CameraSource cameraSource;

    private boolean wantToStart;
    private boolean surfaceAvailable;

    public CameraPreview(Context context) {
        super(context);
    }

    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        this.surfaceView = new SurfaceView(context);
        this.surfaceView.getHolder().addCallback(new SurfaceCallback());
        addView(this.surfaceView,480,480);
    }

    public void start(CameraSource cameraSource) throws IOException {

        if (cameraSource == null) { stop(); }
        this.cameraSource = cameraSource;
        if (this.cameraSource != null) {

            wantToStart = true;
            initView();
        }
    }

    public void stop() {
        if (this.cameraSource != null) {
            this.cameraSource.stop();
        }
    }
    public void release() {
        if (cameraSource != null) {
            cameraSource.release();
            cameraSource = null;
        }
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        try {
            int width = 480;
            int height = 480;
            if (cameraSource != null) {
                Size size = cameraSource.getPreviewSize();
                if (size != null) {
                    width = size.getWidth();
                    height = size.getHeight();
                }
            }
            final int layoutWidth = r - l;
            final int layoutHeight = b - t;

            int childWidth = layoutWidth;
            int childHeight = (int)(((float) layoutWidth / (float) width) * height);

            if (childHeight > layoutHeight) {
                childHeight = layoutHeight;
                childWidth = (int)(((float) layoutHeight / (float) height) * width);
            }

            for (int i = 0; i < getChildCount(); ++i) {
                getChildAt(i).layout(0, 0, childWidth, childHeight);
            }
            initView();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initView() throws IOException {
//        Toast.makeText(context,"CAM STARTED", Toast.LENGTH_LONG).show();
        Log.e("HERE___________","HERE:"+wantToStart+","+surfaceAvailable);
        if (wantToStart && surfaceAvailable) {
            cameraSource.start(surfaceView.getHolder());

            wantToStart = false;
        }
    }

    private class SurfaceCallback implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder surface) {
            surfaceAvailable = true;
            try {
                initView();
            } catch (IOException e) {
                Log.e("CAM PREVIEW CLASS:", "Could not start camera source.", e);
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surface) {
            surfaceAvailable = false;
        }
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }
    }
}
