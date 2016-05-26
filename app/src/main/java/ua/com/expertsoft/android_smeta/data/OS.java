package ua.com.expertsoft.android_smeta.data;

import java.io.Serializable;
import java.util.ArrayList;

import com.j256.ormlite.field.DatabaseField;

public class OS implements Serializable {

	private static final long serialVersionUID = -222864131214757024L;
	
	public static final String TOS_FIELD_ID = "os_id";
	public static final String TOS_FIELD_GUID = "orig_guid";
	public static final String TOS_FIELD_PROJECT_ID = "os_project_id";
	public static final String TOS_FIELD_NAME_RUS = "os_name_rus";
	public static final String TOS_FIELD_NAME_UKR = "os_name_ukr";
	public static final String TOS_FIELD_CIPHER = "os_cipher";
	public static final String TOS_FIELD_SORT_ID = "os_sort_id";
	public static final String TOS_FIELD_TOTAL = "os_total";
	public static final String TOS_FIELD_DESCRIPTION = "os_description";

	@DatabaseField(canBeNull = false, generatedId = true, columnName = TOS_FIELD_ID)
	private int osId;

	@DatabaseField(canBeNull = false,  columnName = TOS_FIELD_GUID)
	private String osGuid;
	
	@DatabaseField(columnName = TOS_FIELD_PROJECT_ID, index = true)
	private int osProjectId;
	
	@DatabaseField(columnName = TOS_FIELD_NAME_RUS)
	private String osNameRus;

	@DatabaseField(columnName = TOS_FIELD_NAME_UKR)
	private String osNameUkr;

	@DatabaseField(columnName = TOS_FIELD_CIPHER)
	private String osCipher;
	
	@DatabaseField(columnName = TOS_FIELD_SORT_ID)
	private int osSortId;
	
	@DatabaseField(columnName = TOS_FIELD_TOTAL)
	private float osTotal;

	@DatabaseField(columnName = TOS_FIELD_DESCRIPTION)
	private String osDescription;
	
	@DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true, index = true)
	private Projects osProjects;

	private ArrayList<LS> LSList;

	private boolean isOpenBranch = false;


	public int getOsId(){
		return osId;
	}

	//...GUID
	public void setOsGuid(String guid){
		osGuid = guid;
	}

	public String getOsGuid(){
		return osGuid;
	}

	//Setter&Getter of project id
	public void setOsProjectId(int projId){
		osProjectId = projId;
	}
	
	public int getOsProjectId(){
		return osProjectId;
	}
	
	//... NameRus
	public void setOsNameRus(String name){
		osNameRus = name;
	}
	
	public String getOsNameRus(){
		return osNameRus;
	}

	//... NameUkr
	public void setOsNameUkr(String name){
		osNameUkr = name;
	}

	public String getOsNameUkr(){
		return osNameUkr;
	}

	//... Shifr
	public void setOsCipher(String cipher){
		osCipher = cipher;
	}
	
	public String getOsCipher(){
		return osCipher;
	}
	//... SortId
	public void setOsSortId(int sortId){
		osSortId = sortId;
	}
	
	public int getOsSortId(){
		return osSortId;
	}
	//... Total
	public void setOsTotal(float total){
		osTotal = total;
	}
	
	public float getOsTotal(){
		return osTotal;
	}

	//... OS Description
	public void setOsDescription(String desc){osDescription = desc;}

	public String getOsDescription(){return osDescription;}

	//... Foreign key Projects
	public void setOsProjects(Projects proj){
		osProjects = proj;
	}

	public Projects getOsProjects(){
		return osProjects;
	}

	public void setIsOpen(boolean isOpen){isOpenBranch = isOpen;}
	public boolean getIsOpen(){return isOpenBranch;}

	public void recalcOSTotal(){
		float total = 0;
		for(LS ls: LSList){
			ls.recalcLSTotal();
			total += ls.getLsTotal();
		}
		osTotal = total;
	}

	//   FOR OS LIST
	public ArrayList<LS> getAllLocalEstimates(){
		return LSList;
	}
	public LS getCurrentEstimate(int position){
		return LSList.get(position);
	}

	public int getCurrentEstimatePosition(LS ls){
		int position = -1;
		for(LS currLs: LSList){
			if(currLs.getLsId() == ls.getLsId()){
				position = LSList.indexOf(currLs);
				break;
			}
		}
		return position;
	}

	public int getEstimatePositionByGuid(LS ls){
		for(LS currLs: LSList){
			if(currLs.getLsGuid().equals(ls.getLsGuid())){
				return LSList.indexOf(currLs);
			}
		}
		return -1;
	}

	public void removeLocalEstimate(LS ls){
		for(LS currLs: LSList){
			if(currLs.getLsId() == ls.getLsId()){
				LSList.remove(currLs);
				break;
			}
		}
	}

	public int findWorkPositionByGuid(Works work){
		int position = 0;
		for(LS ls : LSList){
			for(Works w : ls.getAllWorks()){
				++position;
				if(w.getWGuid().equals(work.getWGuid())){
					return position;
				}
			}
		}
		return -1;
	}

	public void setAllEstimates(ArrayList<LS> localEstim){LSList = localEstim;}

	public void setCurrentEstimate(LS estimate){LSList.add(estimate);}

	public void setCurrentEstimate(int position, LS estimate){LSList.set(position,estimate);}



	public OS() {
		// TODO Auto-generated constructor stub
		LSList = new ArrayList<LS>();
	}
	
}
