package ua.com.expertsoft.android_smeta.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

import ua.com.expertsoft.android_smeta.AddNewTasks;
import ua.com.expertsoft.android_smeta.MainActivity;
import ua.com.expertsoft.android_smeta.R;
import ua.com.expertsoft.android_smeta.data.User_Task;

/**
 * Created by mityai on 23.12.2015.
 */
public class UsersTasksAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener {

    public interface onGetUserTaskDoneListener{
        void onGetUserTaskDone(User_Task ut);
    }

    public interface onGetTaskDoneListener{
        void onGetUserTaskDone(User_Task ut);
    }

    private ArrayList<User_Task> tasks;
    private Context context;
    private LayoutInflater inflater;
    private View returnedView;
    User_Task currentTask;
    onGetUserTaskDoneListener doneListener;
    onGetTaskDoneListener calendarDoneListener;


    public UsersTasksAdapter(){}

    public UsersTasksAdapter(ArrayList<User_Task> userTasks, Context ctx){
        tasks = userTasks;
        context = ctx;
        if(ctx instanceof MainActivity) {
            doneListener = (onGetUserTaskDoneListener) ctx;
        }else{
            calendarDoneListener = (onGetTaskDoneListener)ctx;
        }
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setItem(User_Task task){
        tasks.add(task);
    }

    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public User_Task getItem(int position) {
        return tasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            returnedView = inflater.inflate(R.layout.show_user_tasks, parent, false);
        }else{
            returnedView = convertView;
        }
        currentTask = tasks.get(position);
        CheckBox isDone = (CheckBox)returnedView.findViewById(R.id.checkBoxUserTask);
        isDone.setTag(currentTask);
        isDone.setOnCheckedChangeListener(this);
        isDone.setChecked(currentTask.getUserTaskDone());
        //((CheckBox) returnedView.findViewById(R.id.checkBoxUserTask)).setButtonDrawable();
        String name = currentTask.getUserTaskName();
        if(name.length() <= 50) {
            ((TextView) returnedView.findViewById(R.id.textUserTask)).setText(name);
        }else{
            ((TextView) returnedView.findViewById(R.id.textUserTask)).setText(name.substring(0,49) + "...");
        }
        returnedView.setTag(currentTask);
        return returnedView;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        User_Task ut = (User_Task)buttonView.getTag();
        ut.setUserTaskDone(isChecked);
        if(doneListener != null) {
            doneListener.onGetUserTaskDone(ut);
        }else{
            calendarDoneListener.onGetUserTaskDone(ut);
        }
    }
}
