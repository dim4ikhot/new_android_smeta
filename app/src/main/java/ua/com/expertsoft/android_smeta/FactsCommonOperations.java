package ua.com.expertsoft.android_smeta;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import ua.com.expertsoft.android_smeta.data.Facts;
import ua.com.expertsoft.android_smeta.data.Works;
import ua.com.expertsoft.android_smeta.standard_project.parsers.ZmlParser;

/*
 * Created by mityai on 25.04.2016.
 */
public class FactsCommonOperations {

    private static GregorianCalendar calendar;
    private static float plan;

    public static boolean checkForWorkingDay(Date currentDate){
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(currentDate);
        int dayOfTheWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return (dayOfTheWeek > 1 & dayOfTheWeek < 7);
    }

    public static Date correctingStartDate(Date startDate){
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(startDate);
        while(! checkForWorkingDay(calendar.getTime())){
            calendar.add(Calendar.DAY_OF_MONTH,1);
        }
        return calendar.getTime();
    }

    public static Date correctingStopDate(Date stopDate){
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(stopDate);
        int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int currNumber;
        boolean continueDecrement = false;
        while(! checkForWorkingDay(calendar.getTime())){
            currNumber = calendar.get(Calendar.DAY_OF_MONTH);
            //if stop date day = total days in month then dec day by one
            if(maxDays == currNumber || continueDecrement) {
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                continueDecrement = true;
            }else{
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
        }
        return calendar.getTime();
    }

    public static void setStartDate(Date startDate){
        calendar = new GregorianCalendar();
        calendar.setTime(startDate);
    }

    public static float calculateWorkingHours(){
        float result = 0;
        //get count days in month
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        //in loop calculate total working hours only by working days.
        while(daysInMonth != 0){
            //get current day
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            //1 - sunday, 7 - saturday
            if (dayOfWeek > 1 & dayOfWeek < 7){
                result += 9;
            }
            daysInMonth--;
            if(daysInMonth != 0) {
                //increment calendar date by 1
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
        }
        return result;
    }

    public static float calculateWorkingHours(Date stopDate){
        float result;
        //get count days in month
        GregorianCalendar tempCalendar = new GregorianCalendar();
        tempCalendar.setTime(stopDate);
        float startH = (calendar.get(Calendar.HOUR_OF_DAY)+(float)calendar.get(Calendar.MINUTE)/60);
        float stopH = (float)ZmlParser.roundTo(
                (tempCalendar.get(Calendar.HOUR_OF_DAY)+
                        (float)tempCalendar.get(Calendar.MINUTE)/60)
                ,2);
        if(tempCalendar.get(Calendar.DAY_OF_MONTH) != calendar.get(Calendar.DAY_OF_MONTH)) {
            result = 17 - startH;
            result += stopH - 8;
        }
        else{
            result = stopH - startH;
        }
        int dayInMonth = tempCalendar.get(Calendar.DAY_OF_MONTH)-calendar.get(Calendar.DAY_OF_MONTH)-1;
        //in loop calculate total working hours only by working days.
        while(dayInMonth > 0){
            //get current day
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            //1 - sunday, 7 - saturday
            if (dayOfWeek > 1 & dayOfWeek < 7){
                result += 9;
            }
            dayInMonth--;
            if(dayInMonth > 0) {
                //increment calendar date by 1
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
        }
        return result;
    }

    public static Date compareMonth(Date stopDate){
        Date newStopDate;
        int startMonth = calendar.get(Calendar.MONTH);
        int startYear = calendar.get(Calendar.YEAR);
        int startDay = calendar.get(Calendar.DAY_OF_MONTH);
        GregorianCalendar tempCalendar = new GregorianCalendar();
        tempCalendar.setTime(stopDate);
        int stopMonth = tempCalendar.get(Calendar.MONTH);
        int stopYear = tempCalendar.get(Calendar.YEAR);
        int stopDay = tempCalendar.get(Calendar.DAY_OF_MONTH);
        if (startYear == stopYear){
            if(startMonth == stopMonth){
                if (stopDay> startDay) {
                    newStopDate = stopDate;
                }else{
                    tempCalendar.setTime(calendar.getTime());
                    tempCalendar.set(Calendar.HOUR_OF_DAY,17);
                    tempCalendar.set(Calendar.MINUTE,0);
                    newStopDate = tempCalendar.getTime();
                }
            }
            else{
                if(stopMonth > startMonth){
                    int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                    tempCalendar.set(Calendar.DAY_OF_MONTH, maxDays);
                    tempCalendar.set(Calendar.MONTH, startMonth);
                    tempCalendar.set(Calendar.HOUR_OF_DAY, 17);
                    tempCalendar.set(Calendar.MINUTE,0);
                    newStopDate = tempCalendar.getTime();
                }else{
                    tempCalendar.setTime(calendar.getTime());
                    tempCalendar.set(Calendar.HOUR_OF_DAY, 17);
                    tempCalendar.set(Calendar.MINUTE,0);
                    newStopDate = tempCalendar.getTime();
                }
            }
        }else{
            if (stopYear > startYear){
                int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                tempCalendar.set(Calendar.DAY_OF_MONTH, maxDays);
                tempCalendar.set(Calendar.YEAR, startYear);
                tempCalendar.set(Calendar.MONTH, startMonth);
                tempCalendar.set(Calendar.HOUR_OF_DAY, 17);
                tempCalendar.set(Calendar.MINUTE,0);
                newStopDate = tempCalendar.getTime();
            }else{
                tempCalendar.setTime(calendar.getTime());
                tempCalendar.set(Calendar.HOUR_OF_DAY, 17);
                tempCalendar.set(Calendar.MINUTE,0);
                newStopDate = tempCalendar.getTime();
            }
        }
        return correctingStopDate(newStopDate);
    }

    //checking for other facts start date. It doesn't might be higher then stop date
    public static Date checkFactForPeriodStop(Date startDate, Date stopDate, Works work, Facts currentFact){
        //First off all need check, that end date doesn't might be less than start date
        if (stopDate.before(startDate)|| stopDate.equals(startDate)){
            stopDate = FactsCommonOperations.compareMonth(stopDate);
        }
        for(Facts fact: work.getAllFacts()){
            if(currentFact != fact) {
                if((stopDate.after(fact.getFactsStart())|| stopDate.equals(fact.getFactsStart()))&
                        (stopDate.before(fact.getFactsStop()) || stopDate.equals(fact.getFactsStop()))){
                    stopDate = FactsCommonOperations.compareMonth(stopDate);
                }else if (((fact.getFactsStart().after(startDate)|| fact.getFactsStart().equals(startDate)) &
                        (fact.getFactsStart().before(stopDate)|| fact.getFactsStart().equals(stopDate))) ||
                        ((fact.getFactsStop().after(startDate)|| fact.getFactsStop().equals(startDate)) &
                                (fact.getFactsStop().before(stopDate)|| fact.getFactsStop().equals(stopDate)))) {
                    stopDate = FactsCommonOperations.compareMonth(stopDate);
                }
            }
        }
        return correctingStopDate(stopDate);
    }

    public static Date checkFactForPeriodStart(Date startDate,Date stopDate, Works work,Facts currentFact){
        for(Facts fact: work.getAllFacts()){
            if(currentFact != fact) {
                //NOTE: if(startDate >= fact.getFactsStart()) and (startDate<=fact.getFactsStop())
                if ((startDate.after(fact.getFactsStart())|| startDate.equals(fact.getFactsStart()))&
                    (startDate.before(fact.getFactsStop())|| startDate.equals(fact.getFactsStop()))) {
                    if(currentFact == null) {
                        startDate = calculateStartDate(fact.getFactsStop());
                    }
                    else{
                        startDate = currentFact.getFactsStart();
                        break;
                    }
                } else if (((fact.getFactsStart().after(startDate)|| fact.getFactsStart().equals(startDate)) &
                            (fact.getFactsStart().before(stopDate)||fact.getFactsStart().equals(stopDate))
                           ) ||
                            ((fact.getFactsStop().after(startDate)|| fact.getFactsStop().equals(startDate)) &
                            (fact.getFactsStop().before(stopDate)||fact.getFactsStop().equals(stopDate))
                           )) {
                    if(currentFact == null) {
                        startDate = calculateStartDate(fact.getFactsStop());
                    }else
                    {
                        startDate = currentFact.getFactsStart();
                        break;
                    }
                }
            }
        }
        return correctingStartDate(startDate);
    }

    public static Date calculateStartDate(Date date){
        GregorianCalendar tempCalendar = new GregorianCalendar();
        tempCalendar.setTime(date);
        tempCalendar.add(Calendar.DAY_OF_WEEK, 1);
        tempCalendar.set(Calendar.HOUR_OF_DAY, 8);
        tempCalendar.set(Calendar.MINUTE, 0);
        return correctingStartDate(tempCalendar.getTime());
    }

    public static Date recalculateStopDate(float byPlan, float byFact, Date defaultStopDate){
        plan = byPlan;
        if(byPlan > byFact){
            //get total working days
            int days = (int)byFact/9;
            //hours
            int hours = (int)(byFact - (days*9));
            //and minutes
            int minutes = (int)ZmlParser.roundTo((((byFact - (days*9)) - hours)*60),0);
            // if we have rest hours or minutes - add them to start hours of the day
            if(hours != 0 || minutes != 0) {
                days++;
                calendar.set(Calendar.HOUR_OF_DAY, 8 + hours);
                calendar.set(Calendar.MINUTE, minutes);
            }
            else{
                //set end working time
                calendar.set(Calendar.HOUR_OF_DAY, 17);
                calendar.set(Calendar.MINUTE, 0);
            }
            //in loop calculate new stop date without weekends.
            while(days != 0) {
                if (checkForWorkingDay(calendar.getTime())){
                    days--;
                }
                if (days != 0) {
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                }
            }
            plan = byFact;
        }
        else{
            return defaultStopDate;
        }
        return calendar.getTime();
    }

    public static float getNewPlan(){
        return plan;
    }

    public static Date getStopDate(){
        //set stop time
        calendar.set(Calendar.HOUR_OF_DAY, 17);
        calendar.set(Calendar.MINUTE, 0);
        //return stop date
        return calendar.getTime();
    }

}
