package ua.com.expertsoft.android_smeta.standard_project.parsers;

import java.io.InputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.text.ParseException;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.UUID;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;

import ua.com.expertsoft.android_smeta.FactsCommonOperations;
import ua.com.expertsoft.android_smeta.data.Facts;
import ua.com.expertsoft.android_smeta.data.LS;
import ua.com.expertsoft.android_smeta.data.ORMDatabaseHelper;
import ua.com.expertsoft.android_smeta.data.OS;
import ua.com.expertsoft.android_smeta.data.Projects;
import ua.com.expertsoft.android_smeta.data.Works;
import ua.com.expertsoft.android_smeta.data.WorksResources;

public class ZmlParser {
	
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
	Dao<Facts,Integer> factsDao;
	Dao<WorksResources,Integer> worksresDao;
	
	Projects projects;
	OS os;
	LS ls;
	Works works;
	WorksResources worksres;

	ORMDatabaseHelper databaseHelper;
	int counter = 0;
	String attrValue;
	String razdelTag = "";
	String currentRazdelTag = "";
	int parentNormID = 0;
	InputStream in;
	XmlPullParserFactory factory;
	XmlPullParser parsebuild;
	int loadingType;

	public ZmlParser(InputStream zmlFile, ORMDatabaseHelper dataHelper, int type) {
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
	public boolean startParser(int projectType){
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
								projects.setProjectGuid(parsebuild.getAttributeValue(null,"STROIKAKODSTR"));
								projects.setProjectNameRus(parsebuild.getAttributeValue(null,"STROIKANAMEBRIEF"));
								projects.setProjectNameUkr(parsebuild.getAttributeValue(null,"STROIKANAMEBRIEF"));
								projects.setProjectCipher(parsebuild.getAttributeValue(null,"STROIKAPROJECTSHIFR"));
								attrValue = parsebuild.getAttributeValue(null,"STROIKACREATIONDATE").replace("/", ".");
								SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm",Locale.getDefault());
								Date date;
								try{
									date = sdf.parse(attrValue);
								}catch(ParseException e){
									e.printStackTrace();
									sdf = new SimpleDateFormat("dd.MM.yyyy",Locale.getDefault());
									date = sdf.parse(attrValue);
								}
								projects.setProjectCreatedDate(date);
								projects.setProjectCustomer(parsebuild
										.getAttributeValue(null,"STROIKAZAKAZCHIK"));
								projects.setProjectContractor(parsebuild
										.getAttributeValue(null,"STROIKAGENPODR"));
								projects.setProjectTotal(Float.parseFloat(parsebuild
										.getAttributeValue(null,"STROIKATOTAL").replace(",", ".")));
								if (projects.getProjectCipher() == null){
									projects.setProjectCipher("");
								}
								if(projects.getProjectCustomer() == null){
									projects.setProjectCustomer("");
								}
								if(projects.getProjectContractor() == null){
									projects.setProjectContractor("");
								}
								projects.setProjectType(projectType);
								if(loadingType == 0) {
									addProjects(projects);
								}
								break;
							case "ОбъектнаяСмета":
								os = new OS();
								os.setOsGuid(parsebuild.getAttributeValue(null, "OSKODOS"));
								os.setOsNameRus(parsebuild.getAttributeValue(null, "OSNAME"));
								os.setOsNameUkr(parsebuild.getAttributeValue(null, "OSNAME"));
								os.setOsCipher(parsebuild.getAttributeValue(null, "OSNOOS"));
								os.setOsTotal(Float.parseFloat(parsebuild
										.getAttributeValue(null, "OSTOTAL").replace(",", ".")));
								if(os.getOsCipher() == null){
									os.setOsCipher("");
								}
								os.setOsProjects(projects);	
								os.setOsProjectId(projects.getProjectId());
								if(loadingType == 0) {
									addOS(os);
								}
								projects.setCurrentEstimate(os);
								break;
							case "ЛокальнаяСмета":
								ls = new LS();
								ls.setLsGuid(parsebuild.getAttributeValue(null, "LSKODLS"));
								ls.setLsNameRus(parsebuild.getAttributeValue(null, "LSNAME"));
								ls.setLsNameUkr(parsebuild.getAttributeValue(null, "LSNAME"));
								ls.setLsCipher(parsebuild.getAttributeValue(null, "LSNOLS"));
								ls.setLsTotal(Float.parseFloat(parsebuild
										.getAttributeValue(null, "LSTOTAL").replace(",", ".")));
								if(ls.getLsCipher() == null){
									ls.setLsCipher("");
								}
								ls.setLsHidden(false);
								ls.setLsProjectId(projects.getProjectId());
								ls.setLsOsId(os.getOsId());
								ls.setLsOs(os);								
								ls.setLsProjects(projects);
								if(loadingType == 0) {
									addLS(ls);
								}
								os.setCurrentEstimate(ls);
								break;
							case "ПозицияЛокальнойСметы":
								String rec;
								if(works != null) {
									rec = works.getWRec();
									if (rec != null) {
										if (rec.contains("record")) {
											parentNormID = works.getWorkId();
										}
										if ((rec.contains("razdel")) | (rec.contains("chast"))) {
											if(razdelTag.equals("")){
												razdelTag = works.getWName();
											}else{
												razdelTag += "/" + works.getWName();
											}
										}else{
											works.reCalculateExecuting();
											if(loadingType == 0) {
												updateWorks(works);
											}
										}
									}
								}
								works = new Works();
								works.setWGuid(parsebuild.getAttributeValue(null, "SLSKODSLS"));
								works.setWName(parsebuild.getAttributeValue(null, "SLSNAME").trim());
								works.setWNameUkr(parsebuild.getAttributeValue(null, "SLSNAME_U").trim());
								works.setWCipher(parsebuild.getAttributeValue(null, "SLSSHIFR"));
								works.setWCipherObosn(parsebuild.getAttributeValue(null, "SLSOBOSN"));
								//Fill this field some later
								rec = parsebuild.getAttributeValue(null, "SLSREC");
								attrValue = parsebuild.getAttributeValue(null, "SLSKOLVO").replace(",", ".");
								if(attrValue != null && !attrValue.equals("")) {
									works.setWCount(Float.parseFloat(attrValue));
								}
								works.setWMeasuredRus(parsebuild.getAttributeValue(null, "SLSIZM"));
								works.setWMeasuredUkr(parsebuild.getAttributeValue(null, "SLSIZM_U"));
								//PERCENT DONE
								attrValue = parsebuild.getAttributeValue(null, "SLSKOLVO_PRCNT").replace(",", ".");
								if(attrValue != null && !attrValue.equals("")) {
									works.setWPercentDone(Float.parseFloat(attrValue));
								}
								//COUNT DONE":
								attrValue = parsebuild.getAttributeValue(null, "SLSKOLVO_DONE").replace(",", ".");
								if(attrValue != null){
									if(!attrValue.equals("")) {
										works.setWCountDone(Float.parseFloat(attrValue));
									}
								}
								//TOTAL
								attrValue = parsebuild.getAttributeValue(null, "SLSTOTAL").replace(",", ".");
								if(attrValue != null){
									if(!attrValue.equals("")) {
										works.setWTotal(Float.parseFloat(attrValue));
									}
								}
								//NPP
								attrValue = parsebuild.getAttributeValue(null, "SLSNPP");
								if(attrValue != null){
									if(!attrValue.equals("")) {
										works.setWNpp(Integer.parseInt(attrValue));
									}
								}
								//ITOGO
								attrValue = parsebuild.getAttributeValue(null, "SLSITOGO").replace(",", ".");
								if(attrValue != null){
									if(!attrValue.equals("")) {
										works.setWItogo(Float.parseFloat(attrValue));
									}
								}
								//ZP
								attrValue = parsebuild.getAttributeValue(null, "SLSZP").replace(",", ".");
								if(attrValue != null){
									if(!attrValue.equals("")) {
										works.setWZP(Float.parseFloat(attrValue));
									}
								}
								//MACh
								attrValue = parsebuild.getAttributeValue(null, "SLSMACH").replace(",", ".");
								if(attrValue != null){
									if(!attrValue.equals("")) {
										works.setWMach(Float.parseFloat(attrValue));
									}
								}
								//ZP MACH
								attrValue = parsebuild.getAttributeValue(null, "SLSZPMACH").replace(",", ".");
								if(attrValue != null){
									if(!attrValue.equals("")) {
										works.setWZPMach(Float.parseFloat(attrValue));
									}
								}
								//ZP TOTAL
								attrValue = parsebuild.getAttributeValue(null, "SLSZPTOTAL").replace(",", ".");
								if(attrValue != null){
									if(!attrValue.equals("")) {
										works.setWZPTotal(Float.parseFloat(attrValue));
									}
								}
								//MACH TOTAL
								attrValue = parsebuild.getAttributeValue(null, "SLSMACHTOTAL").replace(",", ".");
								if(attrValue != null){
									if(!attrValue.equals("")) {
										works.setWMachTotal(Float.parseFloat(attrValue));
									}
								}
								//ZP MACH TOTAL
								attrValue = parsebuild.getAttributeValue(null, "SLSZPMACHTOTAL").replace(",", ".");
								if(attrValue != null){
									if(!attrValue.equals("")) {
										works.setWZPMachTotal(Float.parseFloat(attrValue));
									}
								}
								//TZ
								attrValue = parsebuild.getAttributeValue(null, "SLSTZ").replace(",", ".");
								if(attrValue != null){
									if(!attrValue.equals("")) {
										works.setWTz(Float.parseFloat(attrValue));
									}
								}
								//TZ MACH
								attrValue = parsebuild.getAttributeValue(null, "SLSTZMACH").replace(",", ".");
								if(attrValue != null){
									if(!attrValue.equals("")) {
										works.setWTZMach(Float.parseFloat(attrValue));
									}
								}
								//TZ TOTAL
								attrValue = parsebuild.getAttributeValue(null, "SLSTZTOTAL").replace(",", ".");
								if(attrValue != null){
									if(!attrValue.equals("")) {
										works.setWTZTotal(Float.parseFloat(attrValue));
									}
								}
								//TZ MACH TOTAL
								attrValue = parsebuild.getAttributeValue(null, "SLSTZMACHTOTAL").replace(",", ".");
								if(attrValue != null){
									if(!attrValue.equals("")) {
										works.setWTZMachTotal(Float.parseFloat(attrValue));
									}
								}
								//NAKL TOTAL
								attrValue = parsebuild.getAttributeValue(null, "SLSNAKLTOTAL").replace(",", ".");
								if(attrValue != null){
									if(!attrValue.equals("")) {
										works.setWNaklTotal(Float.parseFloat(attrValue));
									}
								}
								//EXEC
								attrValue = parsebuild.getAttributeValue(null,"SLSEXEC");
								if(attrValue!= null && !attrValue.equals("")) {
									works.setwExec(attrValue);
								}

								if (works.getWCipher() == null){
									works.setWCipher("");
								}
								if (works.getWCipherObosn() == null){
									works.setWCipherObosn("");
								}
								if(works.getWMeasuredRus() == null){
									works.setWMeasuredRus("");
								}
								works.setWProjectId(projects.getProjectId());
								works.setWOsId(os.getOsId());
								works.setWLsId(ls.getLsId());
								works.setWLSFK(ls);
								works.setWCurrStateDate(new Date());
								GregorianCalendar calendar = new GregorianCalendar();
								calendar.setTime(new Date());
								calendar.set(Calendar.HOUR_OF_DAY,8);
								calendar.set(Calendar.MINUTE,0);
								works.setWStartDate(calendar.getTime());
								calendar.set(Calendar.HOUR_OF_DAY,17);
								works.setWEndDate(calendar.getTime());

								works.setWOSFK(os);
								String cipher = works.getWCipher();								
								if((rec.equals(""))|(rec.equals("koef"))){
									works.setWRec("koef");
								}else if(((cipher.equals(""))&(rec.contains("record")))){
									works.setWRec("note");
								}else{
									if((rec.contains("razdel"))|(rec.contains("chast"))){
										works.setWRec(rec);
									}else{
										if (checkForNorm(cipher)){
											works.setWRec("record");
										}else{
											if ((cipher.length() > 2)&
													((cipher.contains("С2"))|
															(cipher.contains("СН2")))){
												works.setWRec("machine");
											}else{										
												works.setWRec("resource");
											}
										}
									}
								}
								if((works.getWRec().contains("resource"))|(works.getWRec().contains("machine"))){
									works.setWParentNormId(parentNormID);
								}
								works.setWProjectFK(projects);
								works.setWParentId(ls.getLsId());
								works.setWLayerTag("");
								//works.setWPartTag(razdelTag);
								works.setWGroupTag("");

								if(!works.getWRec().equals("razdel") & !works.getWRec().equals("chast")) {
									if(! razdelTag.equals("")){
										currentRazdelTag = razdelTag.substring(0,razdelTag.length()-1);
										razdelTag = "";
									}
									works.setWPartTag(currentRazdelTag);
									if(loadingType == 0) {
										if(!works.getWRec().equals("note")) {
											addWorks(works);
										}
										else if (! works.getWName().equals("")){
											addWorks(works);
										}

									}
									if(works.getwExec()!= null) {
										fillFactsListFromExec(works);
									}
									if(! works.getWRec().equals("koef")) {
										if(!works.getWRec().equals("note")) {
											ls.setCurrentWork(works);
										}
										else if (! works.getWName().equals("")){
											ls.setCurrentWork(works);
										}

									}
								}
								counter++;								
								break;
							case "СоставПозиции":
								worksres = new WorksResources();
								worksres.setWrGuid(parsebuild.getAttributeValue(null, "RSKODRS") );
								worksres.setWrNameRus(parsebuild.getAttributeValue(null, "RSNAME"));
								worksres.setWrNameUkr(parsebuild.getAttributeValue(null, "RSNAME_U"));
								worksres.setWrCipher(parsebuild.getAttributeValue(null, "RSSHIFR"));
								worksres.setWrMeasuredRus(parsebuild.getAttributeValue(null, "RSIZM"));
								worksres.setWrMeasuredUkr(parsebuild.getAttributeValue(null, "RSIZM_U"));
								attrValue = parsebuild.getAttributeValue(null, "RSKOLVO1").replace(",", ".");
								if(attrValue != null){
									if(!attrValue.equals("")) {
										worksres.setWrCount(Float.parseFloat(attrValue));
									}
								}
								attrValue = parsebuild.getAttributeValue(null, "RSSTOIM1").replace(",", ".");
								if(attrValue != null){
									if(!attrValue.equals("")) {
										worksres.setWrCost(Float.parseFloat(attrValue));
									}
								}
								worksres.setWrTotalCost(worksres.getWrCount() * worksres.getWrCost());
								String onoffValue = parsebuild.getAttributeValue(null, "RSONOFF");
								if(onoffValue.equals("true") || onoffValue.equals("-1") || onoffValue.equals("1")){
									worksres.setWrOnOff(1);
								}
								else {
									worksres.setWrOnOff(0);
								}

								worksres.setWrPart(Integer.parseInt(parsebuild.getAttributeValue(null, "RSRAZDEL")));
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
	}

	catch(Exception e){
			Log.i(LOGTAG,  e.getMessage());
			return false;
		}
		return true;
	}

	public static double roundTo(double value, int places) {
		if (places < 0) throw new IllegalArgumentException();

		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
	}
	public static float roundTo(float value, int places) {
		if (places < 0) throw new IllegalArgumentException();

		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return  tmp / factor;
	}

	public void fillFactsListFromExec(Works work){
		String[] splitedExec = work.getwExec().split(";");
		String stringDateStart;
		String stringMadeCount;
		Facts newFact;
		float wCountDone = 0;
		float wPercentDone = 0;
		float wCount = work.getWCount();
		String[] dividedExec;
		if(splitedExec.length > 0){
			for(String exec : splitedExec) {
				dividedExec = exec.split("-");
				if(dividedExec.length > 1) {
					stringDateStart = "01." + dividedExec[0] + " 08:00";
					stringMadeCount = dividedExec[1].replace(",",".");
					//get count and percent from exec
					float madeCount = Float.parseFloat(stringMadeCount);
					float madePercent = (float)roundTo(100*madeCount/wCount,2);
					//total works execution
					wCountDone += madeCount;
					wPercentDone += madePercent;
					//Create new fact
					newFact = new Facts();
					Date startDate = new Date();
					Date stopDate = new Date();
					float byPlan = 0;
					float byFact;
					try {
						//Convert string date to real date
						startDate = new SimpleDateFormat("dd.MM.yyyy hh:mm", Locale.getDefault()).parse(stringDateStart);
						FactsCommonOperations.setStartDate(startDate);
						byPlan = FactsCommonOperations.calculateWorkingHours();
						stopDate = FactsCommonOperations.getStopDate();
						/*
						calendar.setTime(startDate);
						//get count days in month
						daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
						//in loop calculate total working hours only by working days.
						while(daysInMonth != 0){
							//get current day
							int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
							//1 - sunday, 7 - saturday
							if (dayOfWeek > 1 & dayOfWeek < 7){
								byPlan += 9;
							}
							daysInMonth--;
							if(daysInMonth != 0) {
								//increment calendar date by 1
								calendar.add(Calendar.DAY_OF_MONTH, 1);
							}
						}
						//calendar.add(Calendar.DAY_OF_MONTH, daysInMonth-1);
						//set end working time
						calendar.set(Calendar.HOUR_OF_DAY, 17);
						calendar.set(Calendar.MINUTE, 0);
						//get stop date
						stopDate = calendar.getTime();*/
					}catch(Exception e){
						e.printStackTrace();
					}
					startDate = FactsCommonOperations.correctingStartDate(startDate);
					//set Facts Params
					newFact.setFactsMakesCount(madeCount);
					newFact.setFactsMakesPercent(madePercent);
					//Calculate from percent working hours by Fact
					byFact = (work.getWTZTotal() * newFact.getFactsMakesPercent())/100 ;
					//if work done
					if(wPercentDone == 100){
						FactsCommonOperations.setStartDate(startDate);
						stopDate = FactsCommonOperations.recalculateStopDate(byPlan,byFact, stopDate);
						byPlan = FactsCommonOperations.getNewPlan();
						/*
						//We do not need, for example, 1200h. That's why we need recalculate
						//stop date.
						if(byPlan > byFact){
							//get total working days
							int days = (int)byFact/9;
							//hours
							int hours = (int)(byFact - (days*9));
							//and minutes
							int minutes = (int)(((byFact - (days*9)) - hours)*60);
							//set calendar to 1-st number on month
							calendar.setTime(startDate);
							// if we have rest hours or minutes - add them to start hours of the day
							if(hours != 0 || minutes != 0) {
								days++;
								calendar.set(Calendar.HOUR_OF_DAY, 8 + hours);
								calendar.set(Calendar.MINUTE, minutes);
							}
							else{
								//set end working time
								calendar.set(Calendar.HOUR_OF_DAY, 17);
								calendar.set(Calendar.MINUTE, 0);
							}
							//in loop calculate new stop date without weekends.
							while(days != 0) {
								if (FactsCommonOperations.checkForWorkingDay(calendar.getTime())){
									days--;
								}
								if (days != 0) {
									calendar.add(Calendar.DAY_OF_MONTH, 1);
								}
							}
							stopDate = calendar.getTime();
							byPlan = byFact;
						}
						*/
					}
					stopDate = FactsCommonOperations.correctingStopDate(stopDate);
					newFact.setFactsStart(startDate);
					newFact.setFactsStop(stopDate);
					newFact.setFactsByPlan(byPlan);
					newFact.setFactsByFacts(byFact);
					newFact.setFactsParent(work);
					newFact.setFactsWorkId(work.getWorkId());
					newFact.setFactsGuid(UUID.randomUUID().toString());
					work.setCurrentFact(newFact);
					if(loadingType == 0){
						addFact(newFact);
					}
				}
			}
		}
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

	private void addFact(Facts fact){
		try{
			factsDao.create(fact);
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
	
}
