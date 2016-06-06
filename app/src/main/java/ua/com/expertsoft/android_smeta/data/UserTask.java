package ua.com.expertsoft.android_smeta.data;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/*
 * Created by mityai on 17.12.2015.
 */
public class UserTask implements Serializable{

    public final static String USER_TASK_FIELD_ID = "user_task_id";
    public final static String USER_TASK_FIELD_UPROJ_ID = "user_task_uproj_id";
    public final static String USER_TASK_FIELD_NAME = "user_task_name";
    public final static String USER_TASK_FIELD_IMPORTANCE = "user_task_importance";
    public final static String USER_TASK_FIELD_ISDONE = "user_task_is_done";
    public final static String USER_TASK_FIELD_DATE = "user_task_date";
    public final static String USER_TASK_FIELD_TIME = "user_task_time";
    public final static String USER_TASK_FIELD_GUID = "user_task_guid";

    @DatabaseField(canBeNull = false, generatedId = true, columnName = USER_TASK_FIELD_ID)
    private int userTaskId;

    @DatabaseField(columnName = USER_TASK_FIELD_UPROJ_ID)
    private int userTaskUProjId;

    @DatabaseField(canBeNull = false, columnName = USER_TASK_FIELD_GUID)
    private String userGuid;

    @DatabaseField(canBeNull = false, columnName = USER_TASK_FIELD_NAME)
    private String userTaskName;

    @DatabaseField(canBeNull = false, columnName = USER_TASK_FIELD_IMPORTANCE)
    private int userTaskimportance;

    @DatabaseField(canBeNull = false, columnName = USER_TASK_FIELD_ISDONE)
    private boolean userTaskDone;

    @DatabaseField(/*canBeNull = false,*/ foreign = true, foreignAutoRefresh = true)
    private UserProjects userTaskProject;

    @DatabaseField(columnName = USER_TASK_FIELD_DATE)
    private Date userCalendarDate;

    @DatabaseField(columnName = USER_TASK_FIELD_TIME)
    private Date userCalendarTime;

    private ArrayList<UserSubTask> userSubTasks;

    //////////////////////////////////////  PUBLIC METHODS /////////////////////////////////////
    public int getUserTaskId(){
        return userTaskId;
    }

    public void setUserTaskUProjId(int uProjId){
        userTaskUProjId = uProjId;
    }
    public int getUserTaskUProjId(){
        return userTaskUProjId;
    }

    public void setUserTaskName(String name){
        userTaskName = name;
    }
    public String getUserTaskName(){
        return userTaskName;
    }

    public void setUserTaskDone(boolean isDone){
        userTaskDone = isDone;
    }
    public boolean getUserTaskDone(){
        return userTaskDone;
    }

    public void setUserTaskProjectForeign(UserProjects proj){
        userTaskProject = proj;
    }
    public UserProjects getUserTaskProjectForeign(){
        return userTaskProject;
    }

    public void setUserTaskImportance(int Importance){userTaskimportance = Importance;}
    public int getUserTaskImportance(){return userTaskimportance;}

    public void setUserCalendarDate(Date date){userCalendarDate = date;}
    public Date getUserCalendarDate(){return userCalendarDate;}

    public void setUserCalendarTime(Date time){userCalendarTime = time;}
    public Date getUserCalendarTime(){return userCalendarTime;}

    public void setUserGuid(String guid){userGuid = guid;}
    public String getUserGuid(){return userGuid;}

    /**********************   USERS SUBTASKS   ***************************/
    public void setCurrentUserSubTask(UserSubTask task){userSubTasks.add(task);}
    public ArrayList<UserSubTask> getAllUsersSubTask(){return userSubTasks;}
    public void setAllUsersSubTask(ArrayList<UserSubTask> listSubTasks){userSubTasks = listSubTasks;}
    public UserSubTask getCurrentUserSubTask(int position){return userSubTasks.get(position);}
    public void clearUserSubTasks(){userSubTasks.clear();}
    public int getUserSubTasksCount(){return userSubTasks.size();}

    public void removeUsersSubTask(int position){
        userSubTasks.remove(position);
    }
    public void removeUsersSubTask(UserSubTask position){
        userSubTasks.remove(position);
    }

    public void removeAllUsersSubTasks(){
        Iterator<UserSubTask> i = userSubTasks.iterator();
        while(i.hasNext()){
            i.next();
            i.remove();
        }
    }

    public UserTask(){
        userSubTasks = new ArrayList<UserSubTask>();
    }

}
