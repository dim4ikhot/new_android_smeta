package ua.com.expertsoft.android_smeta.sheet;

import java.util.ArrayList;

import ua.com.expertsoft.android_smeta.data.Works;
import ua.com.expertsoft.android_smeta.data.WorksResources;

/*
 * Created by mityai on 20.05.2016.
 */
public class SheetBody {
    private String cipher;
    private String name;
    private String measure;
    private double count;
    private double cost;
    private double totalCost;
    private double salary;
    private boolean isCanEditSalary;
    private boolean isDone;
    private boolean isIncluded;
    private ArrayList<Works> works;
    private ArrayList<WorksResources> resources;

    //Cipher
    public void setCipher(String cipher){this.cipher = cipher;}
    public String getCipher(){return cipher;}

    //Name
    public void setName(String name){this.name = name;}
    public String getName(){return name;}

    //Measure
    public void setMeasure(String measure){this.measure = measure;}
    public String getMeasure(){return measure;}

    //Count
    public void setCount(double count){this.count = count;}
    public double getCount(){return count;}

    //Cost
    public void setCost(double cost){this.cost = cost;}
    public double getCost(){return cost;}

    //Total cost
    public void setTotalCost(double totalCost){this.totalCost = totalCost;}
    public double getTotalCost(){return totalCost;}

    //Salary
    public void setSalary(double salary){this.salary = salary;}
    public double getSalary(){return salary;}

    //Flag is can edit salary
    public void setCanEditSalary(boolean isCan){isCanEditSalary = isCan;}
    public boolean getCanEditSalary(){return isCanEditSalary;}

    //Done
    public void setIsDone(boolean isDone){this.isDone = isDone;}
    public boolean getIsDone(){return isDone;}

    //Included
    public void setIsIncluded(boolean isIncluded){this.isIncluded = isIncluded;}
    public boolean getIsIncluded(){return isIncluded;}

    public SheetBody(){
        works = new ArrayList<>();
        resources = new ArrayList<>();
        setCanEditSalary(true);
        isDone = false;
    }

    //BLOCK WORK WITH "WORKS"
    public Works findWork(Works work){
        for(Works w: works){
            boolean rusNamesEqual = w.getWName().equals(work.getWName());
            boolean ukrNamesEqual = w.getWNameUkr().equals(work.getWNameUkr());
            boolean rusMeasureEqual = w.getWMeasuredRus().equals(work.getWMeasuredRus());
            boolean ukrMeasureEqual = w.getWMeasuredUkr().equals(work.getWMeasuredUkr());
            boolean costEqual = w.getWItogo() == work.getWItogo();
            boolean salaryEqyals = w.getWZP() == work.getWZP();
            if (rusNamesEqual & rusMeasureEqual & ukrNamesEqual & ukrMeasureEqual & costEqual & salaryEqyals){
                return w;
            }
        }
        return null;
    }
    public void addWork(Works work){
        works.add(work);
    }
    public ArrayList<Works> getAllWorks(){return works;}


    //BLOCK WORK WITH "RESOURCES"
    public void addResource(WorksResources resource){
        resources.add(resource);
    }
    public ArrayList<WorksResources> getAllResources(){return resources;}
}
