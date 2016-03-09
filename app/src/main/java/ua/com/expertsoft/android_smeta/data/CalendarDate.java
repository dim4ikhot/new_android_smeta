package ua.com.expertsoft.android_smeta.data;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by mityai on 24.02.2016.
 */
public class CalendarDate implements Serializable {

    private Date currentDate;
    ArrayList<User_Task> userTasks;

    public void setDate(Date date){
        currentDate = date;
    }

    public Date getDate(){
        return currentDate;
    }

    public CalendarDate() {
        userTasks = new ArrayList<>();
    }

    public void setAllTasks(ArrayList<User_Task> tasks){
        userTasks = tasks;
    }
    public ArrayList<User_Task> getAllTasks(){
        return userTasks;
    }

    public void setCurrentTask(User_Task task){
        userTasks.add(task);
    }

    public User_Task getTask(int position){
        return userTasks.get(position);
    }

    public int getPosition(User_Task task){
        return userTasks.indexOf(task);
    }

    public void removeTask(User_Task task){
        task.removeAllUsersSubTasks();
        userTasks.remove(task);
    }

    public boolean hasTasks(){
        return userTasks.size() > 0;
    }
}
