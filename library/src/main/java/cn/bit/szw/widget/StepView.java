package cn.bit.szw.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by szw on 16/3/24.
 */
public class StepView extends View {
    public static final int TEXT_POSITION_TOP = 0;
    public static final int TEXT_POSTION_BOTTOM = 1;

    private Paint mTextPaint;
    private Paint mLinePaint;

    private int mReachedTextColor;
    private int mUnreachedTextColor;
    private int mCurrentTextColor;
    private int mTextColor = 0xffff1335;
    private float mTextSize = 36;
    private Drawable mReachedDrawable;
    private Drawable mUnreachedDrawable;
    private Drawable mCurrentDrawable;
    private Drawable mDrawable;
    private int mDrawableSize = 24;
    private int mDrawableMargin = 0;

    private int mReachedLineColor;
    private int mUnreachedLineColor;
    private int mLineColor = 0xffdddddd;
    private int mLineHeight = 6;

    private int mTextPostion = TEXT_POSTION_BOTTOM;
    private int mVerticalSpace = 12;

    private int mTextHeight;

    private List<Float> mXPosList;
    private List<String> mLables;
    private int mCurStep = 0;

    public StepView(Context context) {
        this(context, null);
    }

    public StepView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.StepViewStyle);
    }

    public StepView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.StepView, defStyleAttr, 0);

        mDrawable = a.getDrawable(R.styleable.StepView_drawable);
        mReachedDrawable = a.getDrawable(R.styleable.StepView_reachedDrawable);
        if (mReachedDrawable == null) {
            mReachedDrawable = mDrawable;
        }
        mUnreachedDrawable = a.getDrawable(R.styleable.StepView_unreachedDrawable);
        if (mUnreachedDrawable == null) {
            mUnreachedDrawable = mDrawable;
        }
        mCurrentDrawable = a.getDrawable(R.styleable.StepView_currentDrawable);
        if (mCurrentDrawable == null) {
            mCurrentDrawable = mDrawable;
        }
        mDrawableSize = a.getDimensionPixelSize(R.styleable.StepView_drawableSize, mDrawableSize);
        mDrawableMargin = a.getDimensionPixelSize(R.styleable.StepView_drawableMargin, mDrawableMargin);

        mTextColor = a.getColor(R.styleable.StepView_textColor, mTextColor);
        mReachedTextColor = a.getColor(R.styleable.StepView_reachedTextColor, mTextColor);
        mUnreachedTextColor = a.getColor(R.styleable.StepView_unreachedTextColor, mTextColor);
        mCurrentTextColor = a.getColor(R.styleable.StepView_currentTextColor, mTextColor);
        mTextSize = a.getDimension(R.styleable.StepView_textSize, mTextSize);

        mLineColor = a.getColor(R.styleable.StepView_lineColor, mLineColor);
        mReachedLineColor = a.getColor(R.styleable.StepView_reachedLineColor, mLineColor);
        mUnreachedLineColor = a.getColor(R.styleable.StepView_unreachedLineColor, mLineColor);
        mLineHeight = a.getDimensionPixelSize(R.styleable.StepView_lineHeight, mLineHeight);

        mVerticalSpace = a.getDimensionPixelSize(R.styleable.StepView_verticalSpace, mVerticalSpace);
        mTextPostion = a.getInt(R.styleable.StepView_textPostion, mTextPostion);

        a.recycle();

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mLinePaint = new Paint();
        mLinePaint.setColor(mLineColor);
        mTextHeight = getTextHeight(mTextPaint);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getPaddingTop() + getPaddingBottom() + mTextHeight + mDrawableSize + mVerticalSpace);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mLables == null || mLables.size() == 0) {
            return;
        }
        if (mXPosList == null) {
            mXPosList = calLableXpos();
        }

        float textYPos;
        int drawableTop;
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();

        if (mTextPostion == TEXT_POSTION_BOTTOM) {
            textYPos = getHeight() - getPaddingBottom() - fontMetrics.bottom;
            drawableTop = getPaddingTop();
        } else {
            textYPos = getPaddingTop() +  mTextHeight -fontMetrics.bottom;
            drawableTop = getHeight() - getPaddingBottom() - mDrawableSize;
        }
        //draw lables
        for (int i = 0; i < mLables.size(); i++) {
            int step = i + 1;
            if (step == mCurStep) {
                mTextPaint.setColor(mCurrentTextColor);
            } else if (step < mCurStep) {
                mTextPaint.setColor(mReachedTextColor);
            } else {
                mTextPaint.setColor(mUnreachedTextColor);
            }
            canvas.drawText(mLables.get(i), mXPosList.get(i), textYPos, mTextPaint);
        }

        //draw markers
        int top = drawableTop;
        int bottom = drawableTop + mDrawableSize;

        for (int i = 0; i < mXPosList.size(); i++) {
            float left = mXPosList.get(i) - mDrawableSize * 0.5f;
            float right = left + mDrawableSize;
            Drawable drawable;
            int step = i + 1;
            if (step < mCurStep) {
                drawable = mReachedDrawable;
            } else if (step == mCurStep) {
                drawable = mCurrentDrawable;
            } else {
                drawable = mUnreachedDrawable;
            }

            drawable.setBounds((int) left, top, (int) right, bottom);
            drawable.draw(canvas);
        }

        //draw line
        int lineTop = drawableTop + mDrawableSize / 2 - mLineHeight / 2;
        int lineBottom = lineTop + mLineHeight;
        for (int i = 0; i < mXPosList.size() - 1; i++) {
            int step = i + 2;
            float lineLeft = mXPosList.get(i) + mDrawableSize * 0.5f + mDrawableMargin;
            float lineRight = mXPosList.get(i + 1) - mDrawableSize * 0.5f - mDrawableMargin;
            if (step <= mCurStep) {
                mLinePaint.setColor(mReachedLineColor);
            } else {
                mLinePaint.setColor(mUnreachedLineColor);
            }
            canvas.drawRect(lineLeft, lineTop, lineRight, lineBottom, mLinePaint);
        }

    }


    private List<Float> calLableXpos() {
        List<Float> xPosList = new ArrayList<Float>();
        if (mLables == null || mLables.size() == 0) {
            return xPosList;
        }
        int left = getPaddingLeft();
        int right = getWidth() - getPaddingRight();
        int width = right - left;
        if (mLables.size() == 1) {
            Float x = left + width * 1.0f / 2;
            xPosList.add(x);
            return xPosList;
        }
        float firstX = left + mTextPaint.measureText(mLables.get(0)) * 0.5f;
        float lastX = right - mTextPaint.measureText(mLables.get(mLables.size() - 1)) * 0.5f;
        float internalLen = (lastX - firstX) / (mLables.size() - 1);
        xPosList.add(firstX);
        float x = firstX;
        for (int i = 1; i < mLables.size() - 1; i++) {
            x += internalLen;
            xPosList.add(x);
        }
        xPosList.add(lastX);
        return xPosList;
    }


    private int getTextHeight(Paint paint) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return (int) (fontMetrics.bottom - fontMetrics.top);
    }

    public void setStepText(List<String> lables) {
        mLables = lables;
        mXPosList = null;
        invalidate();
    }

    public void setCurrentStep(int step) {
        mCurStep = step;
    }

    public void setTextSize(float textSize) {
        if (mTextSize != textSize) {
            mTextSize = textSize;
            mTextPaint.setTextSize(textSize);
            mTextHeight = getTextHeight(mTextPaint);
            requestLayout();
        }
    }
    public void setTextColor(int color) {
        if (mTextColor != color) {
            mTextColor = color;
            mReachedTextColor = mTextColor;
            mUnreachedTextColor = mTextColor;
            mCurrentTextColor = mTextColor;
            invalidate();
        }
    }
    public void setReachedTextColor(int reachedTextColor) {
        if (mReachedTextColor != reachedTextColor) {
            mReachedTextColor = reachedTextColor;
            invalidate();
        }
    }

    public void setUnreachedTextColor(int unreachedTextColor) {
        if (mUnreachedTextColor != unreachedTextColor) {
            this.mUnreachedTextColor = unreachedTextColor;
            invalidate();
        }
    }

    public void setCurrentTextColor(int mCurrentTextColor) {
        if (this.mCurrentTextColor != mCurrentTextColor) {
            this.mCurrentTextColor = mCurrentTextColor;
            invalidate();
        }
    }

    public void setReachedDrawable(Drawable mReachedDrawable) {
        if (this.mReachedDrawable != mReachedDrawable) {
            this.mReachedDrawable = mReachedDrawable;
            invalidate();
        }
    }

    public void setUnreachedDrawable(Drawable mUnreachedDrawable) {
        if (this.mUnreachedDrawable != mUnreachedDrawable) {
            this.mUnreachedDrawable = mUnreachedDrawable;
            invalidate();
        }
    }

    public void setCurrentDrawable(Drawable mCurrentDrawable) {
        if (this.mCurrentDrawable != mCurrentDrawable) {
            this.mCurrentDrawable = mCurrentDrawable;
            invalidate();
        }
    }

    public void setDrawable(Drawable mDrawable) {
        if (this.mDrawable != mDrawable) {
            this.mDrawable = mDrawable;
            mReachedDrawable = mDrawable;
            mUnreachedDrawable = mDrawable;
            mCurrentDrawable = mDrawable;
            invalidate();
        }
    }

    public void setDrawableMargin(int mDrawableMargin) {
        if (this.mDrawableMargin != mDrawableMargin) {
            this.mDrawableMargin = mDrawableMargin;
            invalidate();
        }
    }

    public void setDrawableSize(int mDrawableSize) {
        if (this.mDrawableSize != mDrawableSize) {
            this.mDrawableSize = mDrawableSize;
            requestLayout();
        }
    }

    public void setReachedLineColor(int mReachedLineColor) {
        if (this.mReachedLineColor != mReachedLineColor) {
            this.mReachedLineColor = mReachedLineColor;
            invalidate();
        }
    }

    public void setUnreachedLineColor(int mUnreachedLineColor) {
        if (this.mUnreachedLineColor != mUnreachedLineColor) {
            this.mUnreachedLineColor = mUnreachedLineColor;
            invalidate();
        }
    }

    public void setLineColor(int mLineColor) {
        if (this.mLineColor != mLineColor) {
            this.mLineColor = mLineColor;
            this.mReachedLineColor = mLineColor;
            this.mUnreachedLineColor = mLineColor;
            invalidate();
        }
    }

    public void setLineHeight(int mLineHeight) {
        if (this.mLineHeight != mLineHeight) {
            this.mLineHeight = mLineHeight;
            invalidate();
        }
    }

    public void setTextPostion(int mTextPostion) {
        if (this.mTextPostion != mTextPostion) {
            this.mTextPostion = mTextPostion;
            invalidate();
        }
    }

    public void setVerticalSpace(int mVerticalSpace) {
        if (this.mVerticalSpace != mVerticalSpace) {
            this.mVerticalSpace = mVerticalSpace;
            requestLayout();
        }
    }

}