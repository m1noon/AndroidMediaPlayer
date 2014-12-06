package com.test.sampleandroidmediaplayer.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.test.sampleandroidmediaplayer.R;

/**
 * Implementation of App Widget functionality.
 */
public class VisualizerView extends View {
    private static final String TAG = VisualizerView.class.getSimpleName();
    
    private byte[] mBytes;
    private float[] mPoints;
    private Rect mRect = new Rect();

    private Paint mForePaint = new Paint();

    private int lineColor;
    private int bgColor;
    private float strokeWidth;

    private static final int DEFAULT_LINE_COLOR = Color.argb(169,255, 0, 64);
    private static final int DEFAULT_BG_COLOR = Color.rgb(16,16,16);
    private static final float DEFAULT_STROKE_WIDTH = 3f;

    public VisualizerView(Context context) {
        super(context);
        this.lineColor = DEFAULT_LINE_COLOR;
        this.bgColor = DEFAULT_BG_COLOR;
        this.strokeWidth = DEFAULT_STROKE_WIDTH;
        init();
    }

    public VisualizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        init();
    }

    public VisualizerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context,attrs);
        init();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        Log.d(TAG, "initAttrs called.");
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.VisualizerView);
        this.lineColor = typedArray.getColor(0,DEFAULT_LINE_COLOR);
        this.bgColor = typedArray.getColor(1,DEFAULT_BG_COLOR);
        this.strokeWidth = typedArray.getFloat(2, DEFAULT_STROKE_WIDTH);
    }

    private void init() {
        mBytes = null;
        mForePaint.setStrokeWidth(strokeWidth);
        mForePaint.setAntiAlias(true);
        mForePaint.setColor(lineColor);
        this.setBackgroundColor(bgColor);
    }

    public void updateVisualizer(byte[] bytes) {
        mBytes = bytes;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mBytes == null) {
            return;
        }

        if (mPoints == null || mPoints.length < mBytes.length * 4) {
            mPoints = new float[mBytes.length * 4];
        }

        mRect.set(0, 0, getWidth(), getHeight());

        for (int i = 0; i < mBytes.length - 1; i++) {
            mPoints[i * 4] = mRect.width() * i / (mBytes.length - 1);
            mPoints[i * 4 + 1] = mRect.height() / 2
                    + ((byte) (mBytes[i] + 128)) * (mRect.height() / 2) / 128;
            mPoints[i * 4 + 2] = mRect.width() * (i + 1) / (mBytes.length - 1);
            mPoints[i * 4 + 3] = mRect.height() / 2
                    + ((byte) (mBytes[i + 1] + 128)) * (mRect.height() / 2) / 128;
        }

        canvas.drawLines(mPoints, mForePaint);
    }

}


