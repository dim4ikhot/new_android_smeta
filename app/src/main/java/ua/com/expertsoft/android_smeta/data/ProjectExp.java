package ua.com.expertsoft.android_smeta.data;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by mityai on 17.12.2015.
 */
public class ProjectExp implements Serializable{

    public static final String PROJECT_EXP_FIELD_ID = "proj_exp_id";
    public static final String PROJECT_EXP_FIELD_TYPE = "proj_exp_type";
    public static final String PROJECT_EXP_FIELD_NAME = "proj_exp_name";

    @DatabaseField(canBeNull = false, generatedId = true, columnName = PROJECT_EXP_FIELD_ID)
    private int projExpId;

    @DatabaseField(canBeNull = false, columnName = PROJECT_EXP_FIELD_TYPE)
    private int projExpType;

    @DatabaseField(canBeNull = false, columnName = PROJECT_EXP_FIELD_NAME)
    private String projExpName;

    //For default projects it will be list of loaded projects
    private ArrayList<Projects> projectsList;

    public int getProjExpId(){
        return projExpId;
    }

    public void setProjExpType(int type){
        projExpType = type;
    }

    public int getProjExpType(){
        return projExpType;
    }

    public void setProjExpName(String name){
        projExpName = name;
    }

    public String getProjExpName(){
        return projExpName;
    }

    /********   OPERATIONS WITH STANDARD PROJECTS   ***********/
    public ArrayList<Projects> getAllProjects(){
        return projectsList;
    }

    public void setCurrentProject(Projects proj){
        projectsList.add(proj);
    }

    public void setAllProject(ArrayList<Projects> proj){
        projectsList = proj;
    }

    public void setCurrentProject(int position, Projects proj){
        projectsList.set(position, proj);
    }

    public int getProjectsCount(){
        if(projectsList != null) {
            return projectsList.size();
        }else{
            return 0;
        }
    }

    public Projects getProjects(int position){
        return projectsList.get(position);
    }

    public int getCurrentProjectPosition(Projects project){
        int position = -1;
        for(Projects iterProj: projectsList){
            if(iterProj.getProjectId() == project.getProjectId()){
                position = projectsList.indexOf(iterProj);
                break;
            }
        }
        return position;
    }

    public void removeProjectFromList(Projects project){
        for(Projects iterProj: projectsList){
            if(iterProj.getProjectId() == project.getProjectId()){
                projectsList.remove(iterProj);
                break;
            }
        }
    }

    public ProjectExp(){
        projectsList = new ArrayList<Projects>();
    }

}
