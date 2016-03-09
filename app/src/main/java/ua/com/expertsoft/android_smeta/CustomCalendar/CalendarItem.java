package ua.com.expertsoft.android_smeta.CustomCalendar;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PathDashPathEffect;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import ua.com.expertsoft.android_smeta.MainActivity;
import ua.com.expertsoft.android_smeta.R;
import ua.com.expertsoft.android_smeta.data.CalendarDate;

/**
 * Created by mityai on 18.02.2016.
 */
public class CalendarItem extends View{

    private TypedArray attr;
    private float x = 0;
    private float y = 0;
    private float radius = 0;
    private String textToDraw = "";
    private Paint pDrawText, pDrawCircle,pDrawCurrentText, pNotCurrentMonth, pFrame;
    private Rect r = new Rect();
    private boolean isShowFrame = true;
    private boolean isShowBackground = true;
    private int frameType;
    private boolean isWeekEnd = false;
    private boolean isCurrentDate = false;
    private boolean isCurrentMonth = false;
    private Context context;
    private OnClickListener listener;
    private Drawable img;
    private boolean hasSomeTasks = false;
    private int delta = 0;

    public CalendarItem(final Context context, AttributeSet attrs) {
        super(context, attrs);
        attr =context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ExCalendar,
                0, 0);
        this.context = context;
        setWillNotDraw(false);
        //isShowFrame = attr.getBoolean(R.styleable.ExCalendar_show_background, true);
        isShowBackground = attr.getBoolean(R.styleable.ExCalendar_show_background, true);
        frameType = attr.getInteger(R.styleable.ExCalendar_frame_type, 0);
        isShowFrame = attr.getBoolean(R.styleable.ExCalendar_show_frame, true);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AppCompatActivity)context).startActivityForResult(new Intent(
                        context, ViewCalendarTasks.class)
                        .putExtra(
                                "calendarDate", (CalendarDate) v.getTag()
                        ), 1);
            }
        });
        img = context.getResources().getDrawable(R.drawable.ic_info);
        initPaints();
    }


    public void setCurrentDate(boolean isCurrentDate){
        this.isCurrentDate = isCurrentDate;
    }
    public void setCurrentMonth(boolean isCurrentMonth){this.isCurrentMonth = isCurrentMonth;}
    @Override
    public void setOnClickListener(OnClickListener listener){
        super.setOnClickListener(listener);
        if(! isClickable()){
            setClickable(true);
        }
        this.listener = listener;
    }

    private void initPaints(){
        pDrawText = new Paint(Paint.ANTI_ALIAS_FLAG);
        pDrawText.setColor(Color.BLACK);
        pDrawCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
        pDrawCircle.setStyle(Paint.Style.FILL_AND_STROKE);

        pFrame = new Paint(Paint.ANTI_ALIAS_FLAG);
        pFrame.setColor(Color.BLACK);
        pFrame.setStyle(Paint.Style.STROKE);

        pDrawCurrentText = new Paint(Paint.ANTI_ALIAS_FLAG);
        pDrawCurrentText.setStyle(Paint.Style.STROKE);
        pDrawCurrentText.setColor(Color.BLUE);
        pDrawCurrentText.setAlpha(80);

        pNotCurrentMonth = new Paint(Paint.ANTI_ALIAS_FLAG);
        pNotCurrentMonth.setColor(Color.GRAY);
        pNotCurrentMonth.setAlpha(30);
    }

    public void setShowBackground(boolean isShow){
        isShowBackground = isShow;
    }
    public void setShowFrame(boolean isShow){
        isShowFrame = isShow;
    }

    public void setX(float x){
        this.x = x;
        refresh();
    }

    public void setRadius(float r){
        radius = r;
        delta = (int)(radius)/4;
        refresh();
    }

    public  void setTextToDraw(String text){
        textToDraw = text;
        refresh();
    }

    public  void setTextSize(float size){
        pDrawText.setTextSize(size);
    }

    public void setIsWeekEnd(boolean isEnd){
        isWeekEnd = isEnd;
        if(isWeekEnd) {
            pDrawCircle.setColor(Color.RED);
            //pFrame.setColor(Color.RED);
        }else{
            pDrawCircle.setColor(Color.GREEN);
            //pFrame.setColor(Color.GREEN);
        }
        pDrawCircle.setAlpha(10);
    }

    public  void setY(float y){
        this.y = y;
        refresh();
    }

    public void setHasSomeTasks(boolean tasksExists){
        hasSomeTasks = tasksExists;
    }

    public void setFrameType(int frame){
        frameType = frame;
    }

    private void refresh(){
        invalidate();
        requestLayout();
    }

    @Override
    public void onDraw(Canvas canvas){
        switch (frameType) {
            case 0:
                if(isShowBackground) {
                    if (!isCurrentDate) {
                        if (!isCurrentMonth) {
                            canvas.drawCircle(x, y, radius, pNotCurrentMonth);
                            if(isShowFrame){
                                canvas.drawCircle(x, y, radius, pFrame);
                            }
                        } else {
                            canvas.drawCircle(x, y, radius, pDrawCircle);
                            if(isShowFrame){
                                canvas.drawCircle(x, y, radius, pFrame);
                            }
                        }
                    } else {
                        canvas.drawCircle(x, y, radius, pDrawCurrentText);
                    }
                }else{
                    if (!isCurrentDate) {
                        if (isShowFrame) {
                            canvas.drawCircle(x, y, radius, pFrame);
                        }
                    } else {
                        canvas.drawCircle(x, y, radius, pDrawCurrentText);
                    }
                }
                break;
            case 1:
                if(isShowBackground) {
                    if (!isCurrentDate) {
                        if (!isCurrentMonth) {
                            canvas.drawRect(x - radius, y - radius, x + radius, y + radius, pNotCurrentMonth);
                            if (isShowFrame) {
                                canvas.drawRect(x - radius, y - radius, x + radius, y + radius, pFrame);
                            }
                        } else {
                            canvas.drawRect(x - radius, y - radius, x + radius, y + radius, pDrawCircle);
                            if (isShowFrame) {
                                canvas.drawRect(x - radius, y - radius, x + radius, y + radius, pFrame);
                            }
                        }
                    } else {
                        canvas.drawRect(x - radius, y - radius, x + radius, y + radius, pDrawCurrentText);
                    }
                }else{
                    if (!isCurrentDate) {
                        if (isShowFrame) {
                            canvas.drawRect(x - radius, y - radius, x + radius, y + radius, pFrame);
                        }
                    } else {
                        canvas.drawRect(x - radius, y - radius, x + radius, y + radius, pDrawCurrentText);
                    }
                }
                break;
        }
        int textWidth;
        int textHeight;
        pDrawText.getTextBounds(textToDraw,0,textToDraw.length(),r);
        textWidth = r.width()/2;
        textHeight = r.height()/2;
        canvas.drawText(textToDraw, x - textWidth, y+textHeight, pDrawText);

        if(hasSomeTasks) {
            int endOfXPoint = (int)(x - textWidth);
            int endOfYPoint = (int)(y - textHeight);
            int centerPointX = endOfXPoint - delta;
            int centerPointY = endOfYPoint - delta;
            img.setBounds(centerPointX - delta, centerPointY - delta, centerPointX + delta, centerPointY + delta);

            img.draw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if(action == MotionEvent.ACTION_UP){
            listener.onClick(this);
        }
        return super.onTouchEvent(event);
    }
}
