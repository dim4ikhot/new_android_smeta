package ua.com.expertsoft.android_smeta.data;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

/**
 * Created by mityai on 17.12.2015.
 */
public class UserSubTask implements Serializable{

    public final static String USER_SUBTASK_FIELD_ID = "user_subtask_id";
    public final static String USER_SUBTASK_FIELD_UTASK_ID = "user_subtask_task_id";
    public final static String USER_SUBTASK_FIELD_NAME = "user_subtask_name";
    public final static String USER_SUBTASK_FIELD_ISDONE = "user_subtask_is_done";

    @DatabaseField(canBeNull = false, generatedId = true, columnName = USER_SUBTASK_FIELD_ID)
    private int userSubTaskId;

    @DatabaseField(canBeNull = false, columnName = USER_SUBTASK_FIELD_UTASK_ID)
    private int userSubTaskTaskId;

    @DatabaseField(canBeNull = false, columnName = USER_SUBTASK_FIELD_NAME)
    private String userSubTaskName;

    @DatabaseField(canBeNull = false, columnName = USER_SUBTASK_FIELD_ISDONE)
    private boolean userSubTaskDone;

    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private UserTask userSubTaskTask;

    public int getUserSubTaskId(){
        return userSubTaskId;
    }

    public void setUserSubTaskTaskId(int taskId){
        userSubTaskTaskId = taskId;
    }
    public int getUserSubTaskTaskId(){
        return userSubTaskTaskId;
    }

    public void setUserSubTaskName(String name){
        userSubTaskName = name;
    }
    public String getUserSubTaskName(){
        return userSubTaskName;
    }

    public void setUserSubTaskDone(boolean isDone){
        userSubTaskDone = isDone;
    }
    public boolean getUserSubTaskDone(){
        return userSubTaskDone;
    }

    public void setUserSubTaskTaskForeign(UserTask task){
        userSubTaskTask = task ;
    }
    public UserTask getUserSubTaskProjectForeign(){
        return userSubTaskTask;
    }

    public UserSubTask(){

    }
}
