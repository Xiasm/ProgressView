package com.xsm.progressview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by xiashengming on 2017/9/24.
 */

public class ProgressView extends View{
    private static final String TAG = "ProgressView";

    private Context context;
    private ProgressListener listener;
    Paint backgroudPaint;
    Paint mPaint;

    //控件宽高
    private int mWidth, mHeight;

    private int mMaxProgress = 100;
    private int mCurrentProgress = 0;
    private float unitWidth;


    float num8;
    float padding;

    //icon
    private Paint photoPaint;
    private Rect src;
    private Rect dst;
    private Bitmap photo;
    private int iconWidth;
    private int iconHeight;

    private int iconTop;
    private int iconBottom;

    private int progressTop;
    private int progressBottom;

    private int borderTop;
    private int borderBottom;
    private int borderColor;
    private int progressColor;
    private int iconResId;
    private int progressBarHeight;

    public void setProgressListener(ProgressListener listener) {
        this.listener = listener;
    }

    public ProgressView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.progress_view);

        iconResId = array.getResourceId(R.styleable.progress_view_icon, R.mipmap.pag);
        photo = BitmapFactory.decodeResource(context.getResources(), iconResId);
        iconHeight = (int) array.getDimension(R.styleable.progress_view_iconHeight, photo.getHeight());
        iconWidth = photo.getWidth();
        progressColor = array.getColor(R.styleable.progress_view_progressColor, Color.YELLOW);
        borderColor = array.getColor(R.styleable.progress_view_borderColor, progressColor);
        padding = dp2px(2);
        padding = array.getDimension(R.styleable.progress_view_progressPadding, padding);

        progressBarHeight = (int) array.getDimension(R.styleable.progress_view_progressBarHeight, dp2px(15));
        array.recycle();
        init();
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    /**
     * 初始化paint
     */
    private void init() {
        //背景设置
        backgroudPaint = new Paint();
        backgroudPaint.setAntiAlias(true);
        backgroudPaint.setStyle(Paint.Style.STROKE);
        backgroudPaint.setStrokeCap(Paint.Cap.ROUND);
        backgroudPaint.setColor(borderColor);

        //进度条设置
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(progressColor);

        photoPaint = new Paint();
        photoPaint.setDither(true);
        photoPaint.setFilterBitmap(true);

        src = new Rect(0, 0, iconWidth, iconHeight);
        dst = new Rect(0, 0, iconWidth, iconHeight);

        num8 = dp2px(8);

    }

    /**
     * 当view的大小发生变化时触发
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = progressBarHeight + iconHeight;

        //单位宽度
        unitWidth = (mWidth - 2 * padding) / mMaxProgress;

        iconTop = 0;
        iconBottom = iconHeight;

        borderTop = iconHeight;
        borderBottom = mHeight;

        progressTop = ((int) (iconHeight + padding));
        progressBottom = ((int) (mHeight - padding));

    }

    RectF mBackground = new RectF();
    RectF mProgress = new RectF();

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int mWidth = (int) dp2px(250);
        int mHeight = iconHeight + progressBarHeight;

        if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT && getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(mWidth, mHeight);
        } else if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(mWidth, heightSize);
        } else if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(widthSize, mHeight);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float x = unitWidth * mCurrentProgress;
        //画进度条背景框
        mBackground.set(0, borderTop, mWidth, borderBottom);
        canvas.drawRoundRect(mBackground, num8, num8, backgroudPaint);
        //画进度条
        if (mCurrentProgress == mMaxProgress) {
            mProgress.set(padding, progressTop, mWidth - padding, progressBottom);
        } else {
            mProgress.set(padding, progressTop, x + padding, progressBottom);
        }
        canvas.drawRoundRect(mProgress, num8, num8, mPaint);
        //画图标
        if (mCurrentProgress == mMaxProgress) {
            dst.set(mWidth - iconWidth, iconTop, mWidth, iconBottom);
            canvas.drawBitmap(photo, src, dst, photoPaint);
        } else {
            if (x <= iconWidth) {
                //起始位置 <= icon宽度
                dst.set(0, iconTop, iconWidth, iconBottom);
                canvas.drawBitmap(photo, src, dst, photoPaint);
            } else {
                dst.set((int) (x- iconWidth), iconTop, (int) (x + padding) , iconBottom);
                canvas.drawBitmap(photo, src, dst, photoPaint);
            }
        }

    }

    public float dp2px(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    /**
     * @param progress 0-100
     */
    public void setProgress(int progress) {
        if (progress <= 100 && progress >= 0) {
            this.mCurrentProgress = progress;
            invalidate();
            if (progress == 100) {
                if (listener != null) {
                    listener.progressFinish();
                }
            }
            if (listener != null) {
                listener.progress(progress);
            }
        }
    }

    public interface ProgressListener {
        void progressFinish();
        void progress(int progress);
    }

}
