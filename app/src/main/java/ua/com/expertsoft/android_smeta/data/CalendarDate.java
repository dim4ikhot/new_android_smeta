package ua.com.expertsoft.android_smeta.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by mityai on 24.02.2016.
 */
public class CalendarDate implements Serializable {

    private Date currentDate;
    ArrayList<UserTask> userTasks;

    public void setDate(Date date){
        currentDate = date;
    }

    public Date getDate(){
        return currentDate;
    }

    public CalendarDate() {
        userTasks = new ArrayList<>();
    }

    public void setAllTasks(ArrayList<UserTask> tasks){
        userTasks = tasks;
    }
    public ArrayList<UserTask> getAllTasks(){
        return userTasks;
    }

    public void setCurrentTask(UserTask task){
        userTasks.add(task);
    }

    public UserTask getTask(int position){
        return userTasks.get(position);
    }

    public int getPosition(UserTask task){
        return userTasks.indexOf(task);
    }

    public void removeTask(UserTask task){
        task.removeAllUsersSubTasks();
        userTasks.remove(task);
    }

    public boolean hasTasks(){
        return userTasks.size() > 0;
    }
}
