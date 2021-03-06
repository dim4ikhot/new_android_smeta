package ua.com.expertsoft.android_smeta.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

import ua.com.expertsoft.android_smeta.MainActivity;
import ua.com.expertsoft.android_smeta.R;
import ua.com.expertsoft.android_smeta.data.UserTask;

/*
 * Created by mityai on 23.12.2015.
 */
public class UsersTasksAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener {

    public interface onGetUserTaskDoneListener{
        void onGetUserTaskDone(UserTask ut);
    }

    public interface onGetTaskDoneListener{
        void onGetUserTaskDone(UserTask ut);
    }

    private ArrayList<UserTask> tasks;
    Context context;
    private LayoutInflater inflater;
    View returnedView;
    UserTask currentTask;
    onGetUserTaskDoneListener doneListener;
    onGetTaskDoneListener calendarDoneListener;

    public UsersTasksAdapter(ArrayList<UserTask> userTasks, Context ctx){
        tasks = userTasks;
        context = ctx;
        if(ctx instanceof MainActivity) {
            doneListener = (onGetUserTaskDoneListener) ctx;
        }else{
            calendarDoneListener = (onGetTaskDoneListener)ctx;
        }
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setItem(UserTask task){
        tasks.add(task);
    }

    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public UserTask getItem(int position) {
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
        if(name.length() >= 50) {
            name = name.substring(0, 49) + "...";
        }
        ((TextView) returnedView.findViewById(R.id.textUserTask)).setText(name);
        returnedView.setTag(currentTask);
        return returnedView;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        UserTask ut = (UserTask)buttonView.getTag();
        ut.setUserTaskDone(isChecked);
        if(doneListener != null) {
            doneListener.onGetUserTaskDone(ut);
        }else{
            calendarDoneListener.onGetUserTaskDone(ut);
        }
    }
}
