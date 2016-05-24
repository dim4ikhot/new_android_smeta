package ua.com.expertsoft.android_smeta.standard_project.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;

import ua.com.expertsoft.android_smeta.R;
import ua.com.expertsoft.android_smeta.data.LS;
import ua.com.expertsoft.android_smeta.data.ORMDatabaseHelper;
import ua.com.expertsoft.android_smeta.data.OS;
import ua.com.expertsoft.android_smeta.data.Projects;
import ua.com.expertsoft.android_smeta.data.Works;
import ua.com.expertsoft.android_smeta.data.WorksResources;

public class ArpParser {

	static final String LOGTAG = "LoadBuild";     
	
	Dao<Projects,Integer> projectsDao;
	Dao<OS,Integer> osDao;
	Dao<LS,Integer> lsDao;
	Dao<Works,Integer> worksDao;
	Dao<WorksResources,Integer> worksresDao;
	
	Projects projects;
	OS os;
	LS ls;
	Works works;
	WorksResources worksres;
	
	List<List<String>> lineNumberList;
	List<String> dataList;
	
	File arpFile;
	ORMDatabaseHelper databaseHelper;
	String projectName;
	String razdelTag = "";
	int parentNormID = 0;
	int globalNPP = 0;
	boolean projectAreFail = false;

	KoefValue PosZP = new KoefValue();
	KoefValue PosMM  = new KoefValue();
	KoefValue PosMAT  = new KoefValue();
	KoefValue PosDV  = new KoefValue();
	
	Context context;

	public ArpParser(Context ctx, File arpFile,ORMDatabaseHelper dataHelper, String prjName) {
				
		try{
			projectsDao = dataHelper.getProjectsDao();
			osDao = dataHelper.getOSDao();
			lsDao = dataHelper.getLSDao();
			worksDao = dataHelper.getWorksDao();
			worksresDao = dataHelper.getWorksResDao();
		}catch(SQLException e){
			Log.i(LOGTAG, e.getMessage());
		}		
		projects = new Projects();
		os = new OS();
		ls = new LS();
		works = new Works();
		worksres = new WorksResources();
		context = ctx;
		String[] projPathParts = prjName.split("/");		
		projectName = projPathParts[projPathParts.length - 1];
		projPathParts = projectName.split("\\.");
		if (projPathParts.length > 0){
			projectName = projPathParts[0];
		}else{
			projectName = context.getResources().getString(R.string.unknownBuildName);
		}
		
		this.arpFile = arpFile;
		databaseHelper = dataHelper;		
		lineNumberList = new ArrayList<>();
		dataList = new ArrayList<>();
	}
	
	public static float abs(float a) {
		return (a <= 0.0F) ? 0.0F - a : a;
	}

	public Projects getProject(){
		return projects;
	}

	public boolean startParce(){
		double smetaTotal;
		float partTotal;
		try{
			FileInputStream is = new FileInputStream(arpFile);
			InputStreamReader inputreader = new InputStreamReader(is); 
            BufferedReader buffreader = new BufferedReader(inputreader); 					
			String currLine;
			while((currLine = buffreader.readLine())!= null){
				currLine += "#1";
				ExplodeString(currLine, "#");								
			}
			buffreader.close();
			inputreader.close();
			is.close();
			for(int i = 0; i < lineNumberList.size(); i++){
				dataList = lineNumberList.get(i);
				switch(dataList.get(0)){
				case "0":
					if(dataList.get(1).contains("Всего")){
						String[] total = dataList.get(1).split("\\s+");
						if(!total[total.length-1].replace(",", ".").equals("")){
							DecimalFormat df = new DecimalFormat("0.###");
							Number num =  df.parse(total[total.length-1].replace(",", "."));
							smetaTotal = num.doubleValue();
							//smetaTotal = Double.parseDouble(total[total.length-1].replace(",", "."));
						}else{
							smetaTotal = 0;
						}
						
						projects.setProjectTotal(smetaTotal);
						addProjects(projects);
					}
					if(dataList.get(1).contains("Итого по разделу")){
						String[] total = dataList.get(1).split("\\s+");
						if(!total[total.length-1].replace(",", ".").equals("")){
							DecimalFormat df = new DecimalFormat("0.###");
							Number num =  df.parse(total[total.length-1].replace(",", "."));
							partTotal = num.floatValue();
							//smetaTotal = Double.parseDouble(total[total.length-1].replace(",", "."));
						}else{
							partTotal = 0;
						}
						
						works.setWTotal(partTotal);
						updateWorks(works);
					}
					break;
				case "3":// Project
					projects.setProjectNameRus(projectName);
					projects.setProjectNameUkr(projectName);
					if(dataList.size() > 17){
						projects.setProjectCipher(dataList.get(17));
					}else{
						projects.setProjectCipher("");
					}
					projects.setProjectTotal(0);
					projects.setProjectContractor(dataList.get(8));
					projects.setProjectCreatedDate(new Date());
					projects.setProjectCustomer(dataList.get(6));	
					projects.setProjectSortId(getMaxProjectSortID() + 1);
					projects.setProjectGuid(UUID.randomUUID().toString());
					projects.setProjectType(1);
					addProjects(projects);

					os.setOsNameRus(context.getResources().getString(R.string.objectEstimateCapt));
					os.setOsNameUkr(context.getResources().getString(R.string.objectEstimateCapt));
					os.setOsGuid(UUID.randomUUID().toString());
					os.setOsCipher("");
					os.setOsTotal(0);
					os.setOsProjectId(projects.getProjectId());
					os.setOsProjects(projects);
					addOS(os);
					projects.setCurrentEstimate(os);

					ls.setLsNameRus(context.getResources().getString(R.string.localEstimateCapt));
					ls.setLsNameUkr(context.getResources().getString(R.string.localEstimateCapt));
					ls.setLsGuid(UUID.randomUUID().toString());
					ls.setLsCipher("");
					ls.setLsTotal(0);
					ls.setLsProjectId(projects.getProjectId());
					ls.setLsOsId(os.getOsId());
					ls.setLsProjects(projects);
					ls.setLsHidden(true);
					ls.setLsOs(os);
					addLS(ls);
					os.setCurrentEstimate(ls);
					break;
				case "10"://Part (������)				
					razdelTag = dataList.get(3);
					break;
				case "20"://Norms(�����)
					boolean isWorkRes = isNextNormResource(i+1);					
					String strForParse;
					//CALCULATE ZP
					switch(PosZP.operation){
	              	case 0:             
	              	  strForParse =dataList.get(16).replace(",", ".");
	              	  if(! strForParse.equals("")){
	              		  PosZP.price = PosZP.value * Float.parseFloat(strForParse);
	              	  }else{
	              		  PosZP.price = 0;
	              	  }
	              	  break;
	              	case 1:                    		
	              		strForParse =dataList.get(16).replace(",", ".");
		              	  if(! strForParse.equals("")){
		              		  PosZP.price = Float.parseFloat(strForParse) / PosZP.value;
		              	  }else{
		              		  PosZP.price = 0;
		              	  }
	              	  break;
	              	} 
					//CALCULATE MACHINES
					switch(PosMM.operation){                    	  
	              	case 0: 
	              		strForParse =dataList.get(17).replace(",", ".");
	              		if(! strForParse.equals("")){
	              			PosMM.price = PosMM.value * Float.parseFloat(strForParse);
	              		}else{
	              			PosMM.price = 0;
	              		}
	              	  break;
	              	case 1:                    		 
	              		strForParse =dataList.get(17).replace(",", ".");
	              		if(! strForParse.equals("")){
	              			PosMM.price = Float.parseFloat(strForParse) / PosMM.value;
	              		}else{
	              			PosMM.price = 0;
	              		}
	              	  break;
	              	}
					//CALCULATE RESOURCES
					switch(PosMAT.operation){
              	  	case 0:                    		 
						strForParse =dataList.get(19).replace(",", ".");
							if(! strForParse.equals("")){
								PosMAT.price = PosMAT.value * Float.parseFloat(strForParse);
							}else{
								PosMAT.price = 0;
							}
						break;
              	  	case 1:                    	
              	  	strForParse =dataList.get(19).replace(",", ".");
	              	  	if(! strForParse.equals("")){
	              	  		PosMAT.price = Float.parseFloat(strForParse) / PosMAT.value;
	              	  	}else{
	              	  		PosMAT.price = 0;
	              	  	}
              		  break;
              	  	}
					String rec = works.getWRec();
					if(rec != null){
						if(rec.contains("record")){
							parentNormID = works.getWorkId();
						}else{
							parentNormID = 0;
						}							
					}	
					works = new Works();  
                    if (((PosZP.value == PosMM.value) & (PosZP.value == PosMAT.value) & (PosZP.value == PosDV.value)&
                        (PosMM.value == PosMAT.value) & (PosMM.value == PosDV.value) & (PosMAT.value == PosDV.value))&
                        ((PosZP.operation == PosMM.operation) & (PosZP.operation == PosMAT.operation) & (PosZP.operation == PosDV.operation)&
                        (PosMM.operation == PosMAT.operation) & (PosMM.operation == PosDV.operation) & (PosMAT.operation == PosDV.operation)))
                    {                             
                       switch(PosZP.operation){
                       case 0:
                    	   strForParse =dataList.get(15).replace(",", ".");
                    	   if(! strForParse.equals("")){
                    		   works.setWItogo(Float.parseFloat(strForParse) * PosZP.value);
                    	   }else{
                    		   works.setWItogo(0f);
                    	   }
                      	 break;
                       case 1: 
                    	   strForParse =dataList.get(15).replace(",", ".");
                    	   if(! strForParse.equals("")){
                    		   works.setWItogo(Float.parseFloat(strForParse) / PosZP.value);
                    	   }else{
                    		   works.setWItogo(0f);
                    	   }
                       	break;
                       }
                    }else{                             
                    	works.setWItogo(PosZP.price + PosMM.price + PosMAT.price);
                    }
					works.setWName(dataList.get(4));
					works.setWNameUkr(dataList.get(4));
					works.setWCipher(dataList.get(2));
					works.setWCipherObosn("");					
					works.setWRec("record");
					strForParse = dataList.get(26).replace(",", ".");
					if (! strForParse.equals("")){
						works.setWCount(Float.parseFloat(strForParse));
					}else{
						works.setWCount(0f);
					}
					works.setWMeasuredRus(dataList.get(3));
					works.setWMeasuredUkr(dataList.get(3));
					works.setWPercentDone(0);
					works.setWCountDone(0);
					works.setWTotal(works.getWItogo() * works.getWCount());
					works.setWNpp(++globalNPP);						
					//works.setWItogo(Float.parseFloat(dataList.get(15).replace(",", ".")));						

					works.setWZP(PosZP.price);
					works.setWZPTotal(works.getWZP() * works.getWCount());
					works.setWMach(PosMM.price);
					works.setWMachTotal(works.getWMach() * works.getWCount());
					works.setWZPMach(PosDV.price);
                    works.setWZPMachTotal(works.getWZPMach() * works.getWCount());
                    
                    strForParse = dataList.get(23).replace(",", ".");
					if (! strForParse.equals("")) {
						works.setWTz(Float.parseFloat(dataList.get(23).replace(",", ".")));
					} else{
						works.setWTz(0f);
					}
					strForParse = dataList.get(24).replace(",", ".");
					if (! strForParse.equals("")){
						works.setWTZMach(Float.parseFloat(dataList.get(24).replace(",", ".")));
					}else{
						works.setWTZMach(0f);
					}
					works.setWTZTotal(works.getWTz() * works.getWCount());
					works.setWTZMachTotal(works.getWTZMach() * works.getWCount());
					works.setWNaklTotal(0);

					works.setWLSFK(ls);
					GregorianCalendar calendar = new GregorianCalendar();
					calendar.setTime(new Date());
					calendar.set(Calendar.HOUR_OF_DAY,8);
					calendar.set(Calendar.MINUTE,0);
					works.setWCurrStateDate(new Date());
					works.setWStartDate(calendar.getTime());
					calendar.set(Calendar.HOUR_OF_DAY,17);
					works.setWEndDate(calendar.getTime());

					works.setWOSFK(os);
					works.setWProjectFK(projects);
					works.setWProjectId(projects.getProjectId());
					works.setWOsId(os.getOsId());
					works.setWLsId(ls.getLsId());
					works.setWParentId(ls.getLsId());
					works.setWSortOrder(getMaxWorksSortid() + 1);
					try{																			                   				
						if( (abs(PosMAT.price - works.getWItogo()) <= 0.2f) & (! isWorkRes)){
							works.setWRec("resource");
						}
						else
						if ((abs(PosMM.price - works.getWItogo()) <= 0.2f)&(! isWorkRes)){
							works.setWRec("machine");
						}
						if((works.getWRec().contains("resource"))|(works.getWRec().contains("machine"))){
							works.setWParentNormId(parentNormID);
						}
					}catch(Exception e){
						e.printStackTrace();
					}
					works.setWPartTag(razdelTag);
					works.setWLayerTag("");
					works.setWGroupTag("");
					works.setWGuid(UUID.randomUUID().toString());

					if(!works.getWName().equals("")) {
						ls.setCurrentWork(works);
						addWorks(works);
					}
					break;				
				case "30"://Resources (������)
					worksres = new WorksResources();
					worksres.setWrResGroupTag("");
					worksres.setWrGuid(UUID.randomUUID().toString());
					worksres.setWrNameRus(dataList.get(3));
					worksres.setWrNameUkr(dataList.get(3));
					worksres.setWrCipher(dataList.get(1));						
					worksres.setWrMeasuredRus(dataList.get(2));
					worksres.setWrMeasuredUkr(dataList.get(2));
					if(! dataList.get(5).equals("")){
						worksres.setWrCount(Float.parseFloat(dataList.get(5).replace(",", ".")));
					}else{
						worksres.setWrCount(0);
					}
					//Need do like in KLP
					if(! dataList.get(6).equals("")){	
						worksres.setWrCost(Float.parseFloat(dataList.get(6).replace(",", ".")));
					}else{
						worksres.setWrCost(0);
					}					
					worksres.setWrTotalCost(worksres.getWrCount() * worksres.getWrCost());
					/*
					* RUS   UKR
					*  0  =  1    ZP
					*  1  =  2    MACHINE
					*  2  =  3    MATERIAL
					*  */
					if(! dataList.get(4).equals("")){
						if(!projectAreFail){
							if (dataList.get(3).contains("Затраты труда")
									&& Integer.parseInt(dataList.get(4)) == 1) {
								projectAreFail = true;
								worksres.setWrPart(Integer.parseInt(dataList.get(4))-1);
							}
							else {
								worksres.setWrPart(Integer.parseInt(dataList.get(4)));
							}
						}else {
							switch (Integer.parseInt(dataList.get(4))) {
								case 0:
									worksres.setWrPart(Integer.parseInt(dataList.get(4)));
									break;
								case 1:
									worksres.setWrPart(Integer.parseInt(dataList.get(4))-1);
									break;
								case 2:
									worksres.setWrPart(Integer.parseInt(dataList.get(4))-1);
									break;
							}
						}
					}else{
						worksres.setWrPart(2);
					}
					worksres.setWrPart(worksres.getWrPart()+1);
					worksres.setWrOnOff(1);
					worksres.setWrWork(works);
					worksres.setWrWorkId(works.getWorkId());
					addWorksRes(worksres);
					works.setCurrentResource(worksres);
					break;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private void ExplodeString(String explodedstr, String border){
		String exploded = explodedstr + border;
		String[] temp = exploded.split(border);
		List<String> datalist = new ArrayList<>();
		for(String t : temp){
			datalist.add(t);
		}
		lineNumberList.add(datalist);
	}
	
	private boolean isNextNormResource(int next){
		List<String> tempDataList;
		for(int i = next; i < lineNumberList.size(); i++){
			tempDataList = lineNumberList.get(i);
			if(tempDataList.get(0).equals("25")){
				if (tempDataList.get(1).equals("0"))
				{
                  switch(tempDataList.get(2)){
                  case "0":
                	  PosZP.value = Float.parseFloat(tempDataList.get(4).replace(",", "."));
                	  PosZP.operation = Byte.parseByte(tempDataList.get(3));	                    	                    	  
                      break;
                  case "1":
                	  PosMM.value = Float.parseFloat(tempDataList.get(4).replace(",", "."));
                	  PosMM.operation = Byte.parseByte(tempDataList.get(3));	                    	                     	  
                      break;
                  case "2":
                	  PosMAT.value = Float.parseFloat(tempDataList.get(4).replace(",", "."));
                	  PosMAT.operation = Byte.parseByte(tempDataList.get(3));	                    	          
                      break;
                  case "3":
                	  PosDV.value = Float.parseFloat(tempDataList.get(4).replace(",", "."));  
                	  PosDV.operation = Byte.parseByte(tempDataList.get(3));
                      break;
                  }
				}else{
					PosZP.value = 1;
                    PosZP.operation = 0;
                    PosMM.value = 1;
                    PosMM.operation = 0;
                    PosMAT.value = 1;
                    PosMAT.operation = 0;
                    PosDV.value = 1;
                    PosDV.operation = 0;
				}				
			}else if (tempDataList.get(0).equals("30")){
					return true;
			}else if ((tempDataList.get(0).equals("20"))|(tempDataList.get(0).equals("10"))){
					return false;
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
		try{
			projectsDao.createOrUpdate(proj);
		}catch(SQLException e){
			Log.i(LOGTAG, e.getMessage());
		}
	}
	
	private void addOS(OS os){		
		os.setOsSortId(getMaxOSSortID() + 1);
		try{
			osDao.createOrUpdate(os);
		}catch(SQLException e){
			Log.i(LOGTAG, e.getMessage());
		}
	}
	
	private void addLS(LS ls){	
		ls.setLsSortId(getMaxLSSortID() + 1);
		try{
			lsDao.createOrUpdate(ls);
		}catch(SQLException e){
			Log.i(LOGTAG, e.getMessage());
		}
	}

	private void addWorks(Works work){		
		try{
			worksDao.create(work);
		}catch(SQLException e){
			Log.i(LOGTAG, e.getMessage());
		}
	}
	
	private void updateWorks(Works work){		
		try{
			worksDao.createOrUpdate(work);
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
	
	private void updateWorksRes(WorksResources wr){			
		try{
			worksresDao.createOrUpdate(wr);
		}catch(SQLException e){
			Log.i(LOGTAG, e.getMessage());
		}
	}
	
	class KoefValue{
		float value;
		float price;
		byte operation;
	}

}
