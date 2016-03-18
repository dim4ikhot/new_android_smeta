package ua.com.expertsoft.android_smeta.custom_calendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import ua.com.expertsoft.android_smeta.R;
import ua.com.expertsoft.android_smeta.data.CalendarDate;
import ua.com.expertsoft.android_smeta.data.DBORM;
import ua.com.expertsoft.android_smeta.data.UserTask;

/**
 * Created by mityai on 18.02.2016.
 */
public class ExCalendar extends View implements View.OnTouchListener{

    private static int ARRAY_LENGTH = 7;
    private static int ARRAY_HEIGHT = 6;
    private static int DAYS_COUNT = 42;

    TypedArray a;
    //PRIVATE VARIABLES
    private boolean isShowWeekends;
    private Paint pDrawText, pDrawTextWeekEnd;
    DateFormatSymbols symbols = new DateFormatSymbols(new Locale("en"));
    private String[] daysShortNames;
    private int[] daysOfWeek = new int[DAYS_COUNT];
    ArrayList<Date> cells = new ArrayList<>();
    private Context context;
    private String[] mothShortNames;
    private CalendarItem[][] days = new CalendarItem[ARRAY_LENGTH][ARRAY_HEIGHT];
    private CalendarItem selectedItem;
    private float deltaX, deltaY;
    private int totalX, totalY;
    private GregorianCalendar calendarDays;
    private Rect r = new Rect();
    private int textWidth;
    private int textHeight;
    private int currentDay;
    private Date currentDate = new Date();
    private int currentMonth;
    private int currentYear;
    private int startDayOfTheWeek;
    private int maxDaysInMonth;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
    private int frameType = 0;
    private boolean showBackground = true;
    private boolean showFrame = true;
    private DBORM database;


    public ExCalendar(Context context, AttributeSet attrs) {
        super(context, attrs);
        a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ExCalendar,
                0, 0);
        this.context = context;
        setWillNotDraw(false);
        database = new DBORM(context);
        frameType = a.getInteger(R.styleable.ExCalendar_frame_type, 0);
        daysShortNames = symbols.getShortWeekdays();
        mothShortNames = symbols.getShortMonths();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        totalX = size.x;
        totalY = size.x;
        deltaX = totalX / 7;
        deltaY = totalY / 6;
        calendarDays = new GregorianCalendar();
        initDefaults();
        initDaysOfWeek(maxDaysInMonth);
        setClickable(false);
        setOnTouchListener(this);
        initPaints();
        initCalendarItems(context, attrs);
    }

    @Override
    public void onMeasure(int width, int height){
        super.onMeasure(width, height);
        setMeasuredDimension(totalX, totalY);
    }

    private void initDefaults(){
        startDayOfTheWeek = calendarDays.getFirstDayOfWeek();
        maxDaysInMonth = calendarDays.getActualMaximum(Calendar.DAY_OF_MONTH);
        currentMonth = calendarDays.get(Calendar.MONTH);
        currentYear = calendarDays.get(Calendar.YEAR);
        int day = calendarDays.get(Calendar.DAY_OF_WEEK);//DAY_OF_WEEK - (1 = Sunday; 7 = Saturday)
        day = calendarDays.get(Calendar.DAY_OF_WEEK_IN_MONTH);//DAY_OF_WEEK_IN_MONTH = number of week
        day = calendarDays.get(Calendar.DAY_OF_MONTH);//DAY_OF_MONTH - current day
        currentDay = Integer.parseInt(android.text.format.DateFormat.format("dd", calendarDays.getTime()).toString());
        updateCalendar();
    }

    private void updateCalendar(){
        // determine the cell for current month's beginning
        calendarDays.set(Calendar.DAY_OF_MONTH, 1);
        int monthBeginningCell = calendarDays.get(Calendar.DAY_OF_WEEK) - 1;
        // move calendar backwards to the beginning of the week
        calendarDays.add(Calendar.DAY_OF_MONTH, -monthBeginningCell);
        // fill cells (42 days calendar as per our business logic)
        cells.clear();
        while (cells.size() < DAYS_COUNT)
        {
            cells.add(calendarDays.getTime());
            calendarDays.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    private Date getCorrectDate(Date incorrectDate){
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.setTime(incorrectDate);
        currentDate.set(Calendar.HOUR_OF_DAY, 0);
        currentDate.set(Calendar.MINUTE, 0);
        currentDate.set(Calendar.SECOND, 0);
        currentDate.set(Calendar.MILLISECOND, 0);
        return currentDate.getTime();
    }

    private void initCalendarItems(Context ctx, AttributeSet attrs){
        int counter = 0;
        for(int j = 0; j < ARRAY_HEIGHT; j++){
            for(int i = 0; i < ARRAY_LENGTH; i++){
                days[i][j] = new CalendarItem(ctx, attrs);
                CalendarDate calendarDates = new CalendarDate();
                calendarDates.setDate(getCorrectDate(cells.get(counter)));
                ArrayList<UserTask> userTasks = database.getUsersTasksByDate(getCorrectDate(cells.get(counter)));
                for (UserTask ut : userTasks) {
                    ut.setAllUsersSubTask(database.getUsersSubTasks(ut));
                }
                calendarDates.setAllTasks(userTasks);
                days[i][j].setOnTouchListener(this);
                days[i][j].setHasSomeTasks(calendarDates.hasTasks());
                days[i][j].setTag(calendarDates);
                counter++;
            }
        }
    }

    private void reInitCalendarItems(){
        int counter = 0;
        for(int j = 0; j < ARRAY_HEIGHT; j++){
            for(int i = 0; i < ARRAY_LENGTH; i++){
                if(days[i][j]!=null) {
                    Date currentDate = cells.get(counter);
                    CalendarDate calendarDates = new CalendarDate();
                    calendarDates.setDate(getCorrectDate(cells.get(counter)));
                    ArrayList<UserTask> userTasks = database.getUsersTasksByDate(getCorrectDate(cells.get(counter)));
                    for (UserTask ut : userTasks) {
                        ut.setAllUsersSubTask(database.getUsersSubTasks(ut));
                    }
                    calendarDates.setAllTasks(userTasks);
                    days[i][j].setTag(calendarDates);
                    days[i][j].setHasSomeTasks(calendarDates.hasTasks());
                    counter++;
                }
                //days[i][j].setOnTouchListener(this);
            }
        }
    }

    private void initDaysOfWeek(int totalDays){
        for(int i = 0; i < 42; i++){
            if(i < totalDays) {
                daysOfWeek[i] = i + 1;
            }else{
                daysOfWeek[i] = (i-totalDays) + 1;
                //setCurrentMonth(++currentMonth);
            }
        }
    }

    public void setFrameType(int frame){
        frameType = frame;
        refresh();
    }

    private void initPaints(){
        pDrawText = new Paint(Paint.ANTI_ALIAS_FLAG);
        pDrawText.setColor(Color.BLACK);
        pDrawText.setTextSize(20);
        pDrawTextWeekEnd = new Paint(Paint.ANTI_ALIAS_FLAG);
        pDrawTextWeekEnd.setColor(Color.RED);
        pDrawTextWeekEnd.setTextSize(20);
    }

    public boolean getShowWeekEnds(){
        return isShowWeekends;
    }

    public void setShowWeekends(boolean isShow){
        isShowWeekends = isShow;
        invalidate();
        requestLayout();
    }

    public void setShowBackground(boolean isShow){
        showBackground = isShow;
        refresh();
    }

    public void setShowFrame(boolean isShow){
        showFrame = isShow;
        refresh();
    }

    public boolean getShowBackground(){
        return showBackground;
    }

    public boolean getShowFrame(){
        return showFrame;
    }

    public void nextMonth(){
        //currentMonth++;
        calendarDays.add(Calendar.MONTH, 0);
        //calendarDays.set(Calendar.MONTH, currentMonth);
        currentMonth = calendarDays.get(Calendar.MONTH);
        currentYear = calendarDays.get(Calendar.YEAR);
        updateCalendar();
        reInitCalendarItems();
        refresh();
    }

    public void priorMonth(){
        currentMonth--;
        if (currentMonth < 0 ){
            currentMonth = 11;
            currentYear--;
        }
        calendarDays.set(Calendar.MONTH, currentMonth);
        calendarDays.set(Calendar.YEAR, currentYear);
        updateCalendar();
        reInitCalendarItems();
        refresh();
    }

    public void refreshSelectedItemTag(Object newTag){
        if ( newTag != null) {
            selectedItem.setTag(newTag);
            selectedItem.setHasSomeTasks(((CalendarDate) newTag).hasTasks());
            refresh();
        }
    }

    public void setCurrentYear(int year){
        currentYear = year;
        calendarDays.set(Calendar.YEAR, currentYear);
    }

    public String getCurrentYear(){
        return String.valueOf(currentYear);
    }

    public String getShortMonthName(){
        return mothShortNames[currentMonth];
    }

    private void refresh(){
        invalidate();
        requestLayout();
    }
    @Override
    public void onDraw(Canvas canvas){
        String day;
        float x = 0f;
        for(int i = 0; i < daysShortNames.length; i++){
            day = daysShortNames[i];
            if (!day.equals("")) {
                pDrawText.getTextBounds(day,0,day.length(),r);
                textWidth = r.width()/2;
                textHeight = r.height();
                if(i == 1) {
                    x = deltaX/2;
                    canvas.drawText(day, x - textWidth, textHeight , pDrawTextWeekEnd);
                }else{
                    if(i != daysShortNames.length-1) {
                        canvas.drawText(day, (i * deltaX) - x - textWidth, textHeight, pDrawText);
                    }
                    else{
                        canvas.drawText(day, (i * deltaX) - x - textWidth , textHeight, pDrawTextWeekEnd);
                    }
                }
            }
        }
        int counter = 0;
        for(int j = 0; j < ARRAY_HEIGHT; j++){
            for(int i = 0; i < ARRAY_LENGTH; i++){
                days[i][j].setRadius(x);
                if(i == 0) {
                    days[i][j].setX(deltaX / 2);
                    days[i][j].setIsWeekEnd(true);
                }else{
                    days[i][j].setX(((i + 1) * deltaX) - (deltaX / 2));
                    days[i][j].setIsWeekEnd(false);
                    if(i == ARRAY_LENGTH - 1){
                        days[i][j].setIsWeekEnd(true);
                    }
                }
                days[i][j].setTextSize(deltaX / 2);
                Calendar tmp = Calendar.getInstance(new Locale("en"));
                tmp.setTimeInMillis(cells.get(counter).getTime());
                int calendarDay = tmp.get(Calendar.DAY_OF_MONTH);
                int calendarMonth = tmp.get(Calendar.MONTH);

                if(sdf.format(tmp.getTime()).equals(sdf.format(currentDate))){
                    days[i][j].setCurrentDate(true);
                }else{
                    days[i][j].setCurrentDate(false);
                }
                if(calendarMonth == currentMonth){
                    days[i][j].setCurrentMonth(true);
                }else{
                    days[i][j].setCurrentMonth(false);
                }
                float textY = (textHeight + 5) + x;
                days[i][j].setY((j * deltaX) + textY);
                days[i][j].setTextToDraw(String.valueOf(calendarDay));
                //days[i][j].setTag(cells.get(counter));
                days[i][j].setFrameType(frameType);
                days[i][j].setShowBackground(showBackground);
                days[i][j].setShowFrame(showFrame);
                counter++;
                days[i][j].draw(canvas);
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_UP) {
            float x = event.getX() / deltaX;
            float y = (event.getY()-(textHeight + 5)) / deltaX;
            int ix = (int) x;
            int iy = (int) y;
            if((ix < ARRAY_LENGTH)&&(iy< ARRAY_HEIGHT)) {
                selectedItem = days[ix][iy];
                days[ix][iy].callOnClick();
            }
        }
        return false;
    }
}
