package ua.com.expertsoft.android_smeta.data;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by mityai on 17.12.2015.
 */
public class Facts implements Serializable{


    public static final String FACTS_FIELD_ID = "facts_id";
    public static final String FACTS_FIELD_GUID = "orig_work_guid";
    public static final String FACTS_FIELD_WORK_ID = "facts_works_id";
    public static final String FACTS_FIELD_MAKES_PERCENT = "facts_make_percent";
    public static final String FACTS_FIELD_MAKES_COUNT = "facts_make_count";
    public static final String FACTS_FIELD_START_PERIOD = "facts_start_period";
    public static final String FACTS_FIELD_STOP_PERIOD = "facts_stop_period";
    public static final String FACTS_FIELD_DESCRIPTION = "facts_description";
    public static final String FACTS_FIELD_BY_FACTS = "facts_by_facts";
    public static final String FACTS_FIELD_BY_PLAN = "facts_by_plan";

    @DatabaseField(canBeNull = false, generatedId = true, columnName = FACTS_FIELD_ID)
    int factsId;

    @DatabaseField(canBeNull = false, columnName = FACTS_FIELD_GUID)
    private String factsGuid;
    @DatabaseField(canBeNull = false, columnName = FACTS_FIELD_WORK_ID, index = true)
    private int factsWorkId;
    @DatabaseField(canBeNull = false, columnName = FACTS_FIELD_MAKES_PERCENT)
    private float factsMakesPercent;
    @DatabaseField(canBeNull = false, columnName = FACTS_FIELD_MAKES_COUNT)
    private float factsMakesCount;
    @DatabaseField(format = "dd.MM.yyyy hh:mm", columnName = FACTS_FIELD_START_PERIOD)
    private Date factsStart;
    @DatabaseField(format = "dd.MM.yyyy hh:mm", columnName = FACTS_FIELD_STOP_PERIOD)
    private Date factsStop;
    @DatabaseField(columnName = FACTS_FIELD_DESCRIPTION)
    private String factsDesc;
    @DatabaseField(columnName = FACTS_FIELD_BY_FACTS)
    private float factsByFacts;
    @DatabaseField(columnName = FACTS_FIELD_BY_PLAN)
    private float factsByPlan;

    private Works parent;

    public int getFactsId(){
        return factsId;
    }

    public void setFactsGuid(String guid){
        factsGuid = guid;
    }
    public String getFactsGuid(){
        return factsGuid;
    }

    public void setFactsWorkId(int workId){
        factsWorkId = workId;
    }
    public int getFactsWorkId(){
        return factsWorkId;
    }

    public void setFactsMakesPercent(float makePerc){
        factsMakesPercent = makePerc;
    }
    public float getFactsMakesPercent(){
        return factsMakesPercent;
    }

    public void setFactsMakesCount(float makesCount){
        factsMakesCount = makesCount;
    }
    public float getFactsMakesCount(){
        return factsMakesCount;
    }

    public void setFactsStart(Date start){
        factsStart = start;
    }
    public Date getFactsStart(){
        return factsStart;
    }

    public void setFactsStop(Date stop){
        factsStop = stop;
    }
    public Date getFactsStop(){
        return factsStop;
    }

    public void setFactsDesc(String desc){
        factsDesc = desc;
    }
    public String getFactsDesc(){return factsDesc;}

    public void setFactsByFacts(float byFacts){
        factsByFacts = byFacts;
    }
    public float getFactsByFacts(){return factsByFacts;}

    public void setFactsByPlan(float byPlan){
        factsByPlan = byPlan;
    }
    public float getFactsByPlan(){return factsByPlan;}

    public void setFactsParent(Works parent){
        this.parent = parent;
    }
    public Works getFactsParent(){return parent;}

    public Facts(){

    }
}
