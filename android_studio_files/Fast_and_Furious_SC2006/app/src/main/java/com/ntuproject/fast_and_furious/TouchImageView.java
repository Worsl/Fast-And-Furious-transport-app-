package com.ntuproject.fast_and_furious;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

public class TouchImageView extends androidx.appcompat.widget.AppCompatImageView {

    Matrix matrix;

    // We can be in one of these 3 states
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    // Remember some things for zooming
    PointF last = new PointF();
    PointF start = new PointF();
    float minScale = 1f;
    float maxScale = 3f;
    float[] m;
    float redundantXSpace, redundantYSpace, origRedundantXSpace, origRedundantYSpace;

    int viewWidth, viewHeight;
    static final int CLICK = 3;
    static final float SAVE_SCALE = 1f;
    float saveScale = SAVE_SCALE;
    protected float origWidth, origHeight;
    int oldMeasuredWidth, oldMeasuredHeight;
    float origScale, bottom, origBottom, right, origRight;

    ScaleGestureDetector mScaleDetector;
    GestureDetector mGestureDetector;

    Context context;

    public TouchImageView(Context context) {
        super(context);
        sharedConstructing(context);
    }

    public TouchImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        sharedConstructing(context);
    }

    private void sharedConstructing(Context context) {
        super.setClickable(true);
        this.context = context;
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        matrix = new Matrix();
        m = new float[9];
        setImageMatrix(matrix);
        setScaleType(ScaleType.MATRIX);

        setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                boolean onDoubleTapEvent = mGestureDetector.onTouchEvent(event);
                if (onDoubleTapEvent) {
                    // Reset Image to original scale values
                    mode = NONE;
                    bottom = origBottom;
                    right = origRight;
                    last = new PointF();
                    start = new PointF();
                    m = new float[9];
                    saveScale = SAVE_SCALE;
                    matrix = new Matrix();
                    matrix.setScale(origScale, origScale);
                    matrix.postTranslate(origRedundantXSpace, origRedundantYSpace);
                    setImageMatrix(matrix);
                    invalidate();
                    return true;
                }

                mScaleDetector.onTouchEvent(event);
                PointF curr = new PointF(event.getX(), event.getY());

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        last.set(curr);
                        start.set(last);
                        mode = DRAG;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if (mode == DRAG) {
                            float deltaX = curr.x - last.x;
                            float deltaY = curr.y - last.y;
                            float fixTransX = getFixDragTrans(deltaX, viewWidth, origWidth * saveScale);
                            float fixTransY = getFixDragTrans(deltaY, viewHeight, origHeight * saveScale);
                            matrix.postTranslate(fixTransX, fixTransY);
                            fixTrans();
                            last.set(curr.x, curr.y);
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        mode = NONE;
                        int xDiff = (int) Math.abs(curr.x - start.x);
                        int yDiff = (int) Math.abs(curr.y - start.y);
                        if (xDiff < CLICK && yDiff < CLICK) performClick();
                        break;

                    case MotionEvent.ACTION_POINTER_UP:
                        mode = NONE;
                        break;
                }

                setImageMatrix(matrix);
                invalidate();
                return true; // indicate event was handled
            }

        });

        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                return true;
            }
        });
    }

    public void setMaxZoom(float x) {
        maxScale = x;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            mode = ZOOM;
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float mScaleFactor = detector.getScaleFactor();
            //float mScaleFactor = (float) Math.min(Math.max(.95f, detector.getScaleFactor()), 1.05);
            float origScale = saveScale;
            saveScale *= mScaleFactor;
            if (saveScale > maxScale) {
                saveScale = maxScale;
                mScaleFactor = maxScale / origScale;
            } else if (saveScale < minScale) {
                saveScale = minScale;
                mScaleFactor = minScale / origScale;
            }

            right = viewWidth * saveScale - viewWidth - (2 * redundantXSpace * saveScale);
            bottom = viewHeight * saveScale - viewHeight - (2 * redundantYSpace * saveScale);

            if (origWidth * saveScale <= viewWidth || origHeight * saveScale <= viewHeight)
                matrix.postScale(mScaleFactor, mScaleFactor, viewWidth / 2, viewHeight / 2);
            else
                matrix.postScale(mScaleFactor, mScaleFactor, detector.getFocusX(), detector.getFocusY());

            fixTrans();
            return true;
        }
    }

    void fixTrans() {
        matrix.getValues(m);
        float transX = m[Matrix.MTRANS_X];
        float transY = m[Matrix.MTRANS_Y];

        float fixTransX = getFixTrans(transX, viewWidth, origWidth * saveScale);
        float fixTransY = getFixTrans(transY, viewHeight, origHeight * saveScale);

        if (fixTransX != 0 || fixTransY != 0)
            matrix.postTranslate(fixTransX, fixTransY);
    }

    float getFixTrans(float trans, float viewSize, float contentSize) {
        float minTrans, maxTrans;

        if (contentSize <= viewSize) {
            minTrans = 0;
            maxTrans = viewSize - contentSize;
        } else {
            minTrans = viewSize - contentSize;
            maxTrans = 0;
        }

        if (trans < minTrans)
            return -trans + minTrans;
        if (trans > maxTrans)
            return -trans + maxTrans;
        return 0;
    }

    float getFixDragTrans(float delta, float viewSize, float contentSize) {
        if (contentSize <= viewSize) {
            return 0;
        }
        return delta;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);

        //
        // Rescales image on rotation
        //
        if (oldMeasuredHeight == viewWidth && oldMeasuredHeight == viewHeight || viewWidth == 0 || viewHeight == 0) return;

        oldMeasuredHeight = viewHeight;
        oldMeasuredWidth = viewWidth;

        if (saveScale == 1) {
            // Fit to screen.
            float scale;
            int bmWidth,bmHeight;
            ImageView img = (ImageView) findViewById(R.id.mrt_map);
            //Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.mrt_map);
            bmWidth = img.getWidth();
            bmHeight = img.getHeight();

            int  w = bmWidth;
            int  h = bmHeight;
            viewWidth = resolveSize(w, widthMeasureSpec);
            viewHeight = resolveSize(h, heightMeasureSpec);

            float scaleX = (float) viewWidth / (float) bmWidth;
            float scaleY = (float) viewHeight / (float) bmHeight;
            scale = Math.min(scaleX, scaleY);
            matrix.setScale(scale, scale);
            saveScale = SAVE_SCALE;
            origScale = scale;

            // Center the image
            redundantYSpace = (float) viewHeight - (scale * (float) bmHeight);
            redundantXSpace = (float) viewWidth - (scale * (float) bmWidth);
            redundantYSpace /= (float) 2;
            redundantXSpace /= (float) 2;

            origRedundantXSpace = redundantXSpace;
            origRedundantYSpace = redundantYSpace;

            matrix.postTranslate(redundantXSpace, redundantYSpace);

            origWidth = viewWidth - 2 * redundantXSpace;
            origHeight = viewHeight - 2 * redundantYSpace;

            right = viewWidth * saveScale - viewWidth - (2 * redundantXSpace * saveScale);
            bottom = viewHeight * saveScale - viewHeight - (2 * redundantYSpace * saveScale);
            origRight = right;
            origBottom = bottom;

            setImageMatrix(matrix);
        }
        fixTrans();
    }
}