package ua.com.expertsoft.android_smeta;

import java.io.Serializable;
import java.util.ArrayList;

import ua.com.expertsoft.android_smeta.data.UserProjects;

/**
 * Created by mityai on 23.12.2015.
 */
public class UserProjectsCollection implements Serializable {

    private static final long serialVersionUID = -222864131214757024L;

    private ArrayList<ProjectsData> userProjCollection;

    public UserProjectsCollection(){
        userProjCollection = new ArrayList<ProjectsData>();
    }

    public void addNewProject(ProjectsData newProj){
        userProjCollection.add(newProj);
    }

    public void addNewProject(int position, ProjectsData newProj){
        userProjCollection.set(position, newProj);
    }

    public ProjectsData getProject(int position){
        return userProjCollection.get(position);
    }

    public ArrayList<ProjectsData> getAllProject(){
        return userProjCollection;
    }

    public void removeProject(ProjectsData proj){
        proj.getProjectsTypeUsers().removeAllUsersTasks();
        userProjCollection.remove(proj);
    }

    public void removeProject(int position){
        userProjCollection.get(position).getProjectsTypeUsers().removeAllUsersTasks();
        userProjCollection.remove(position);
    }

    public int getProjectCount(){
        return userProjCollection.size();
    }

    public void updateProject(int position, UserProjects oldProject){
        userProjCollection.get(position).setProjectsTypeUsers(oldProject);
    }
}
