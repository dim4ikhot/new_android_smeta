package ua.com.expertsoft.android_smeta.standard_project.parsers;

import java.io.FileInputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.text.ParseException;
import java.util.Locale;
import java.util.UUID;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;

import ua.com.expertsoft.android_smeta.data.Facts;
import ua.com.expertsoft.android_smeta.data.LS;
import ua.com.expertsoft.android_smeta.data.ORMDatabaseHelper;
import ua.com.expertsoft.android_smeta.data.OS;
import ua.com.expertsoft.android_smeta.data.Projects;
import ua.com.expertsoft.android_smeta.data.Works;
import ua.com.expertsoft.android_smeta.data.WorksResources;

public class CplnParser {

    public static final String LOGTAG = "LoadBuild";

    static final String[] CiphersNorm = {"Е",  "М",  "Р",  "ШД", "П",  "ПП",
            "В",  "ПУ", "ПР", "ПХ", "ПМ", "ПЕ",
            "ЖТ", "ЖР", "ТР", "ТЕ", "ВМ", "ПЖ",
            "С3", "ДА", "ТРУ","РУ", "ЖС", "ЖС",
            "ЩД", "ВЕ", "ТГ", "ТП"};
    Dao<Projects,Integer> projectsDao;
    Dao<OS,Integer> osDao;
    Dao<LS,Integer> lsDao;
    Dao<Works,Integer> worksDao;
    Dao<WorksResources,Integer> worksresDao;
    Dao<Facts,Integer> factsDao;

    Projects projects;
    OS os;
    LS ls;
    Works works;
    WorksResources worksres;
    Facts facts;

    ORMDatabaseHelper databaseHelper;
    int counter = 0;
    String attrValue;
    String razdelTag = "";
    String currentRazdelTag = "";
    int parentNormID = 0;
    FileInputStream in;
    XmlPullParserFactory factory;
    XmlPullParser parsebuild;
    int loadingType;
    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());

    public CplnParser(FileInputStream zmlFile,ORMDatabaseHelper dataHelper, int type) {
        try{
            projectsDao = dataHelper.getProjectsDao();
            osDao = dataHelper.getOSDao();
            lsDao = dataHelper.getLSDao();
            worksDao = dataHelper.getWorksDao();
            worksresDao = dataHelper.getWorksResDao();
            factsDao = dataHelper.getFactsDao();
        }catch(SQLException e){
            Log.i(LOGTAG, e.getMessage());
        }
        projects = new Projects();
        os = new OS();
        ls = new LS();
        works = new Works();
        worksres = new WorksResources();

        databaseHelper = dataHelper;
        loadingType = type;
        in = zmlFile;
    }
    public Projects getProject(){
        return projects;
    }
    public boolean startParser(){
        try{
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            parsebuild = factory.newPullParser();
            parsebuild.setInput(in, "Windows-1251");
            while(parsebuild.getEventType() != XmlPullParser.END_DOCUMENT){
                switch(parsebuild.getEventType()){
                    case XmlPullParser.START_DOCUMENT:
                        //TODO Message or dialog, which will be signal parser started
                        Log.i(LOGTAG, "Loading start");
                        break;
                    case XmlPullParser.START_TAG:
                        switch(parsebuild.getName()){
                            case "Стройка":
                                projects.setProjectNameRus(parsebuild.getAttributeValue(null, "TEMPPROJECTSNAMEBRIEF"));
                                projects.setProjectNameUkr(parsebuild.getAttributeValue(null, "TEMPPROJECTSNAMEBRIEF"));
                                projects.setProjectGuid(parsebuild.getAttributeValue(null, "TEMPPROJECTSGUID"));
                                projects.setProjectCipher("");
                                projects.setProjectTotal(0f);
                                projects.setProjectType(1);
                                break;
                            case "ДеталиСтройки":
                                attrValue = parsebuild
                                        .getAttributeValue(null, "TEMPPROJECTDETAILCREATIONDATE");
                                Date date;
                                if(attrValue != null){
                                    attrValue = attrValue.replace("/", ".");
                                    try{
                                        date = sdf.parse(attrValue);
                                    }catch(ParseException e){
                                        e.printStackTrace();
                                        sdf = new SimpleDateFormat("dd.MM.yyyy",Locale.getDefault());
                                        date = sdf.parse(attrValue);
                                    }
                                }else{
                                    date = new Date();
                                }
                                projects.setProjectCreatedDate(date);
                                projects.setProjectContractor(
                                        parsebuild.getAttributeValue(null, "TEMPPROJECTDETAILGENPODR"));
                                projects.setProjectCustomer(
                                        parsebuild.getAttributeValue(null, "TEMPPROJECTDETAILZAKAZCHIK"));

                                if(projects.getProjectCustomer() == null){
                                    projects.setProjectCustomer("");
                                }
                                if(projects.getProjectContractor() == null){
                                    projects.setProjectContractor("");
                                }
                                if(loadingType == 0) {
                                    addProjects(projects);
                                }
                                break;
                            case "ПозицияПроекта":
                                attrValue = parsebuild.getAttributeValue(null, "TEMPPOSITIONREC");
                                switch(attrValue) {
                                    case "os":
                                        os = new OS();
                                        os.setOsGuid(parsebuild.getAttributeValue(null,"TEMPPOSITIONGUID"));
                                        os.setOsNameRus(parsebuild.getAttributeValue(null,"TEMPPOSITIONNAME"));
                                        os.setOsNameUkr(parsebuild.getAttributeValue(null,"TEMPPOSITIONNAME"));
                                        os.setOsCipher("");
                                        os.setOsTotal(0f);
                                        os.setOsProjects(projects);
                                        os.setOsProjectId(projects.getProjectId());
                                        if (loadingType == 0) {
                                            addOS(os);
                                        }
                                        projects.setCurrentEstimate(os);
                                        break;
                                    case "ls":
                                        ls = new LS();
                                        ls.setLsGuid(parsebuild.getAttributeValue(null,"TEMPPOSITIONGUID"));
                                        ls.setLsNameRus(parsebuild.getAttributeValue(null,"TEMPPOSITIONNAME"));
                                        ls.setLsNameUkr(parsebuild.getAttributeValue(null,"TEMPPOSITIONNAME"));
                                        ls.setLsCipher("");
                                        ls.setLsTotal(0f);
                                        ls.setLsHidden(false);
                                        ls.setLsProjectId(projects.getProjectId());
                                        ls.setLsOsId(os.getOsId());
                                        ls.setLsOs(os);
                                        ls.setLsProjects(projects);
                                        if (loadingType == 0) {
                                            addLS(ls);
                                        }
                                        os.setCurrentEstimate(ls);
                                        break;
                                    case "razdel":
                                        if (razdelTag.equals("")) {
                                            razdelTag = parsebuild.getAttributeValue(null,"TEMPPOSITIONNAME");
                                        } else {
                                            razdelTag += "/" + parsebuild.getAttributeValue(null, "TEMPPOSITIONNAME");
                                        }
                                        break;
                                    case "chast":
                                        if (razdelTag.equals("")) {
                                            razdelTag = parsebuild.getAttributeValue(null, "TEMPPOSITIONNAME");
                                        } else {
                                            razdelTag += "/" + parsebuild.getAttributeValue(null, "TEMPPOSITIONNAME");
                                        }
                                        break;
                                    case "record_2":
                                    case "record_3":
                                    case "record_1":
                                        String rec;
                                        if (works != null) {
                                            rec = works.getWRec();
                                            if (rec != null) {
                                                if (rec.contains("record")) {
                                                    parentNormID = works.getWorkId();
                                                }
                                                works.reCalculateExecuting();
                                                if (loadingType == 0) {
                                                    updateWorks(works);
                                                }
                                            }
                                        }
                                        works = new Works();
                                        works.setWGuid(parsebuild.getAttributeValue(null,"TEMPPOSITIONGUID"));
                                        works.setWName(parsebuild.getAttributeValue(null,"TEMPPOSITIONNAME").trim());
                                        works.setWNameUkr(parsebuild.getAttributeValue(null,"TEMPPOSITIONNAME_U").trim());
                                        works.setWCipher(parsebuild.getAttributeValue(null,"TEMPPOSITIONSHIFR"));
                                        works.setWCipherObosn(parsebuild.getAttributeValue(null,"TEMPPOSITIONSHIFR"));
                                        rec = parsebuild.getAttributeValue(null,"TEMPPOSITIONREC");
                                        //Count
                                        attrValue = parsebuild.getAttributeValue(null, "TEMPPOSITIONKOLVO")
                                                .replace(",", ".");
                                        works.setWCount(Float.parseFloat(attrValue));
                                        works.setWMeasuredRus(parsebuild.getAttributeValue(null,"TEMPPOSITIONIZM"));
                                        works.setWMeasuredUkr(parsebuild.getAttributeValue(null,"TEMPPOSITIONIZM_U"));
                                        // Percent done
                                        attrValue = parsebuild.getAttributeValue(null, "TEMPPOSITIONTA_PROGRESS")
                                                .replace(",", ".");
                                        works.setWPercentDone(Float.parseFloat(attrValue));
                                        // Count done
                                        attrValue = parsebuild.getAttributeValue(null, "TEMPPOSITIONCOST_OF_COUNT")
                                                .replace(",", ".");
                                        works.setWCountDone(works.getWCount() - Float.parseFloat(attrValue));
                                        works.setWNpp(Integer.parseInt(parsebuild.getAttributeValue(null, "TEMPPOSITIONSORTID")));

                                        if (works.getWCipher() == null) {
                                            works.setWCipher("");
                                        }
                                        if (works.getWCipherObosn() == null) {
                                            works.setWCipherObosn("");
                                        }
                                        if (works.getWMeasuredRus() == null) {
                                            works.setWMeasuredRus("");
                                        }
                                        works.setWProjectId(projects.getProjectId());
                                        works.setWOsId(os.getOsId());
                                        works.setWLsId(ls.getLsId());
                                        works.setWLSFK(ls);
                                        works.setWCurrStateDate(new Date());
                                        attrValue = parsebuild.getAttributeValue(null,"TEMPPOSITIONTA_STOP");
                                        try {
                                            works.setWEndDate(sdf.parse(attrValue));
                                        }catch(Exception e){
                                            works.setWEndDate(new Date());
                                        }
                                        attrValue = parsebuild.getAttributeValue(null,"TEMPPOSITIONTA_START");
                                        try {
                                            works.setWStartDate(sdf.parse(attrValue));
                                        }catch(Exception e){
                                            works.setWStartDate(new Date());
                                        }
                                        works.setWOSFK(os);

                                        String cipher = works.getWCipher();
                                        if ((rec.equals("")) | (rec.equals("koef"))) {
                                            works.setWRec("koef");
                                        } else if (((cipher.equals("")) & (rec.contains("record")))) {
                                            works.setWRec("note");
                                        } else {
                                            if ((rec.contains("razdel")) | (rec.contains("chast"))) {
                                                works.setWRec(rec);
                                            } else {
                                                if (checkForNorm(cipher)) {
                                                    works.setWRec("record");
                                                } else {
                                                    if ((cipher.length() > 2) &
                                                            ((cipher.contains("С2")) |
                                                                    (cipher.contains("СН2")))) {
                                                        works.setWRec("machine");
                                                    } else {
                                                        works.setWRec("resource");
                                                    }
                                                }
                                            }
                                        }
                                        if ((works.getWRec().contains("resource")) | (works.getWRec().contains("machine"))) {
                                            works.setWParentNormId(parentNormID);
                                        }
                                        works.setWProjectFK(projects);
                                        works.setWParentId(ls.getLsId());
                                        works.setWLayerTag("");
                                        works.setWGroupTag("");

                                        if (!razdelTag.equals("")) {
                                            currentRazdelTag = razdelTag.substring(0, razdelTag.length() - 1);
                                            razdelTag = "";
                                        }
                                        works.setWPartTag(currentRazdelTag);
                                        counter++;
                                        break;
                                }
                                break;
                            case "ДеталиПозиции":
                                //TOTAL
                                attrValue = parsebuild.getAttributeValue(null, "TABLEWORKDETAILTOTAL").replace(",", ".");
                                works.setWTotal(Float.parseFloat(attrValue));
                                //ITOGO
                                attrValue = parsebuild.getAttributeValue(null, "TABLEWORKDETAILITOGO").replace(",", ".");
                                works.setWItogo(Float.parseFloat(attrValue));
                                //ZP
                                attrValue = parsebuild.getAttributeValue(null, "TABLEWORKDETAILZP").replace(",", ".");
                                works.setWZP(Float.parseFloat(attrValue));
                                //MACH
                                attrValue = parsebuild.getAttributeValue(null, "TABLEWORKDETAILMACH").replace(",", ".");
                                works.setWMach(Float.parseFloat(attrValue));
                                //ZP MACH
                                attrValue = parsebuild.getAttributeValue(null, "TABLEWORKDETAILZPMACH").replace(",", ".");
                                works.setWZPMach(Float.parseFloat(attrValue));
                                //ZP TOTAL
                                attrValue = parsebuild.getAttributeValue(null, "TABLEWORKDETAILZPTOTAL").replace(",", ".");
                                works.setWZPTotal(Float.parseFloat(attrValue));
                                //MACH TOTAL
                                attrValue = parsebuild.getAttributeValue(null, "TABLEWORKDETAILMACHTOTAL").replace(",", ".");
                                works.setWMachTotal(Float.parseFloat(attrValue));
                                // ZP MACH TOTAL
                                attrValue = parsebuild.getAttributeValue(null, "TABLEWORKDETAILZPMACHTOTAL").replace(",", ".");
                                works.setWZPMachTotal(Float.parseFloat(attrValue));
                                //TZ
                                attrValue = parsebuild.getAttributeValue(null, "TABLEWORKDETAILTZ").replace(",", ".");
                                works.setWTz(Float.parseFloat(attrValue));
                                //TZ MACH
                                attrValue = parsebuild.getAttributeValue(null, "TABLEWORKDETAILTZMACH").replace(",", ".");
                                works.setWTZMach(Float.parseFloat(attrValue));
                                //TZ TOTAL
                                attrValue = parsebuild.getAttributeValue(null, "TABLEWORKDETAILTZTOTAL").replace(",", ".");
                                works.setWTZTotal(Float.parseFloat(attrValue));
                                //MACH TOTAL
                                attrValue = parsebuild.getAttributeValue(null, "TABLEWORKDETAILTZMACHTOTAL").replace(",", ".");
                                works.setWTZMachTotal(Float.parseFloat(attrValue));
                                //NAKL TOTAL
                                attrValue = parsebuild.getAttributeValue(null, "TABLEWORKDETAILNAKLTOTAL").replace(",", ".");
                                works.setWNaklTotal(Float.parseFloat(attrValue));
                                works.setwExec(parsebuild.getAttributeValue(null, "TABLEWORKDETAILPERCENTEXECUTION"));

                                if (loadingType == 0) {
                                    addWorks(works);
                                }
                                if (!works.getWRec().equals("koef")) {
                                    ls.setCurrentWork(works);
                                }
                                break;
                            case "СоставПозиции":
                                worksres = new WorksResources();
                                worksres.setWrGuid(parsebuild.getAttributeValue(null, "TABLEWORKRESGUID"));
                                worksres.setWrNameRus(parsebuild.getAttributeValue(null, "TABLEWORKRESNAME"));
                                worksres.setWrNameUkr(parsebuild.getAttributeValue(null, "TABLEWORKRESNAME_U"));
                                worksres.setWrCipher(parsebuild.getAttributeValue(null, "TABLEWORKRESSHIFR"));
                                worksres.setWrMeasuredRus(parsebuild.getAttributeValue(null, "TABLEWORKRESIZM"));
                                worksres.setWrMeasuredUkr(parsebuild.getAttributeValue(null, "TABLEWORKRESIZM_U"));
                                attrValue = parsebuild.getAttributeValue(null, "TABLEWORKRESKOLVO1").replace(",", ".");
                                worksres.setWrCount(Float.parseFloat(attrValue));
                                attrValue = parsebuild.getAttributeValue(null, "TABLEWORKRESSTOIM1").replace(",", ".");
                                worksres.setWrCost(Float.parseFloat(attrValue));
                                worksres.setWrTotalCost(worksres.getWrCount() * worksres.getWrCost());
                                worksres.setWrOnOff(Integer.parseInt(parsebuild.getAttributeValue(null, "TABLEWORKRESONOFF")));
                                worksres.setWrPart(Integer.parseInt(parsebuild.getAttributeValue(null, "TABLEWORKRESRAZDEL")));
                                if (worksres.getWrCipher() == null){
                                    worksres.setWrCipher("");
                                }
                                if(worksres.getWrMeasuredRus()==null){
                                    worksres.setWrMeasuredRus("");
                                }
                                worksres.setWrWorkId(works.getWorkId());
                                worksres.setWrWork(works);
                                if(loadingType == 0) {
                                    addWorksRes(worksres);
                                }
                                works.setCurrentResource(worksres);
                                break;
                            case "ФактическоеВыполнениеПозиции":
                                facts = new Facts();
                                facts.setFactsGuid(UUID.randomUUID().toString());
                                facts.setFactsParent(works);
                                facts.setFactsWorkId(works.getWorkId());
                                //PERCENT
                                attrValue = parsebuild.getAttributeValue(null, "TABLEFACTSMAKE_PERCENT").replace(",", ".");
                                facts.setFactsMakesPercent(Float.parseFloat(attrValue));
                                //COUNT
                                attrValue = parsebuild.getAttributeValue(null, "TABLEFACTSMAKE_KOLVO").replace(",", ".");
                                facts.setFactsMakesCount(Float.parseFloat(attrValue));
                                //BY FACT
                                attrValue = parsebuild.getAttributeValue(null, "TABLEFACTSBYFACTS").replace(",", ".");
                                facts.setFactsByFacts(Float.parseFloat(attrValue));
                                //BY PLAN
                                attrValue = parsebuild.getAttributeValue(null, "TABLEFACTSBYPLAN").replace(",", ".");
                                facts.setFactsByPlan(Float.parseFloat(attrValue));
                                //START
                                sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss",Locale.getDefault());
                                facts.setFactsStart(sdf.parse(parsebuild.getAttributeValue(null, "TABLEFACTSSTART_PERIOD")));
                                //STOP
                                facts.setFactsStop(sdf.parse(parsebuild.getAttributeValue(null, "TABLEFACTSSTOP_PERIOD")));
                                facts.setFactsDesc("");
                                if(loadingType == 0){
                                    addWorksFact(facts);
                                }
                                works.setCurrentFact(facts);
                                break;
                        }
                        break;

                    case XmlPullParser.TEXT:
                        break;

                    case XmlPullParser.END_TAG:
                        break;
                }
                parsebuild.next();
            }
            //STOPED parse
            Log.i(LOGTAG, "Loading stoped");
        }catch(Exception e){
            Log.i(LOGTAG,  e.getMessage());
            return false;
        }
        return true;
    }

    private boolean checkForNorm(String Cipher){
        if(Cipher != null){
            for(String CipPart: CiphersNorm){
                if(Cipher.contains(CipPart)){
                    return true;
                }
            }
        }
        return false;
    }
    private int getMaxProjectSortID(){
        int counter = 0;
        try{
            final QueryBuilder<Projects, Integer> queryBuilder = projectsDao.queryBuilder();
            // select 2 aggregate functions as the return
            queryBuilder.selectRaw("MAX("+Projects.TP_FIELD_SORTID+")");
            // the results will contain 2 string values for the min and max
            GenericRawResults<String[]> rawResult = worksDao.queryRaw(queryBuilder.prepareStatementString());
            String[] results = rawResult.getFirstResult();
            if (results[0] != null){
                counter = Integer.parseInt(results[0]);
            }
        }catch(SQLException e){
            Log.i(LOGTAG,  e.getMessage());
        }
        return counter;
    }

    private int getMaxOSSortID(){
        int counter = 0;
        try{
            final QueryBuilder<OS, Integer> queryBuilder = osDao.queryBuilder();
            // select 2 aggregate functions as the return
            queryBuilder.selectRaw("MAX("+OS.TOS_FIELD_SORT_ID+")");
            queryBuilder.where().eq(OS.TOS_FIELD_PROJECT_ID, projects.getProjectId());
            // the results will contain 2 string values for the min and max
            GenericRawResults<String[]> rawResult = worksDao.queryRaw(queryBuilder.prepareStatementString());
            String[] results = rawResult.getFirstResult();
            if (results[0] != null){
                counter = Integer.parseInt(results[0]);
            }
        }catch(SQLException e){
            Log.i(LOGTAG,  e.getMessage());
        }
        return counter;
    }

    private int getMaxLSSortID(){
        int counter = 0;
        try{
            final QueryBuilder<LS, Integer> queryBuilder = lsDao.queryBuilder();
            // select 2 aggregate functions as the return
            queryBuilder.selectRaw("MAX("+LS.TLS_FIELD_SORT_ID+")");
            queryBuilder.where().eq(LS.TLS_FIELD_PROJECT_ID, projects.getProjectId());
            // the results will contain 2 string values for the min and max
            GenericRawResults<String[]> rawResult = worksDao.queryRaw(queryBuilder.prepareStatementString());
            String[] results = rawResult.getFirstResult();
            if (results[0] != null){
                counter = Integer.parseInt(results[0]);
            }
        }catch(SQLException e){
            Log.i(LOGTAG,  e.getMessage());
        }
        return counter;
    }

    private int getMaxWorksSortid(){
        int counter = 0;
        try{
            final QueryBuilder<Works, Integer> queryBuilder = worksDao.queryBuilder();
            // select 2 aggregate functions as the return
            queryBuilder.selectRaw("MAX("+Works.TW_FIELD_SORT_ORDER+")");
            queryBuilder.where().eq(Works.TW_FIELD_PROJECT_ID, projects.getProjectId());
            // the results will contain 2 string values for the min and max
            GenericRawResults<String[]> rawResult = worksDao.queryRaw(queryBuilder.prepareStatementString());
            String[] results = rawResult.getFirstResult();
            if (results[0] != null){
                counter = Integer.parseInt(results[0]);
            }
        }catch(SQLException e){
            Log.i(LOGTAG,  e.getMessage());
        }
        return counter;
    }

    private void addProjects(Projects proj){
        proj.setProjectSortId(getMaxProjectSortID() + 1);
        try{
            projectsDao.create(proj);
        }catch(SQLException e){
            Log.i(LOGTAG, e.getMessage());
        }
    }

    private void addOS(OS os){
        os.setOsSortId(getMaxOSSortID() + 1);
        try{
            osDao.create(os);
        }catch(SQLException e){
            Log.i(LOGTAG, e.getMessage());
        }
    }

    private void addLS(LS ls){
        ls.setLsSortId(getMaxLSSortID() + 1);
        try{
            lsDao.create(ls);
        }catch(SQLException e){
            Log.i(LOGTAG, e.getMessage());
        }
    }

    private void addWorks(Works work){
        work.setWSortOrder(getMaxWorksSortid() + 1);
        try{
            worksDao.create(work);
        }catch(SQLException e){
            Log.i(LOGTAG, e.getMessage());
        }
    }

    private void updateWorks(Works work){
        work.setWSortOrder(getMaxWorksSortid() + 1);
        try{
            worksDao.update(work);
        }catch(SQLException e){
            Log.i(LOGTAG, e.getMessage());
        }
    }

    private void addWorksRes(WorksResources wr){
        try{
            worksresDao.create(wr);
        }catch(SQLException e){
            Log.i(LOGTAG, e.getMessage());
        }
    }

    private void addWorksFact(Facts f){
        try{
            factsDao.create(f);
        }catch(SQLException e){
            Log.i(LOGTAG, e.getMessage());
        }
    }
}

