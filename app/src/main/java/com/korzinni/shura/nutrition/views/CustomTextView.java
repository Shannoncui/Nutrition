package com.korzinni.shura.nutrition.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Display;
import android.view.WindowManager;
import android.widget.TextView;

import com.korzinni.shura.nutrition.R;


public class CustomTextView extends TextView {
    String text;
    String letter;
    int color;
    Drawable ball;
    public CustomTextView(Context context) {

        super(context);
        WindowManager winManager=(WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        Point size=new Point();
        Display display= winManager.getDefaultDisplay();
        int width= display.getWidth();
        setMinimumWidth(width/4);

    }
    public CustomTextView(Context context, AttributeSet attr) {

        super(context,attr);
        TypedArray a=(TypedArray)context.obtainStyledAttributes(attr, R.styleable.CustomTextView,0,0);
        color=a.getColor(R.styleable.CustomTextView_mainColor, Color.BLACK);
        letter=a.getString(R.styleable.CustomTextView_letter);
        ball=a.getDrawable(R.styleable.CustomTextView_ball);
        WindowManager winManager=(WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        Display display= winManager.getDefaultDisplay();
        int width=display.getWidth();
        setMinimumWidth(width/4);
        setMinimumHeight(ball.getMinimumHeight());

    }
    public void setBall(int id){
        ball=getResources().getDrawable(id);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        this.text=(String)text;
        invalidate();
    }
    @Override
    protected void onMeasure(int w,int h){
        setMeasuredDimension(getSuggestedMinimumWidth(),getSuggestedMinimumHeight());
    }

    @Override
    public void onDraw(Canvas canvas){
        Paint paint=new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.STROKE);
        int halfHeight=canvas.getHeight()/2;
        int textSize1=halfHeight;
        int textSize2=textSize1/3*2;

        Bitmap b=((BitmapDrawable) ball).getBitmap();
        canvas.drawBitmap(b,0,0,getPaint());

        //paint.setColor(Color.BLACK);
        paint.setTextSize(textSize1);
        canvas.drawText(letter, halfHeight -halfHeight/3, halfHeight +textSize2/2, paint);
        paint.setTextSize(textSize2);
        canvas.drawText(text,canvas.getHeight(),halfHeight+textSize2/2,paint);


    }
}
