package ua.com.expertsoft.android_smeta.data;

import java.io.Serializable;
import java.util.ArrayList;

import com.j256.ormlite.field.DatabaseField;

public class LS implements Serializable {

	private static final long serialVersionUID = -222864131214757024L;
	
	public static final String TLS_FIELD_ID = "ls_id";
	public static final String TLS_FIELD_PROJECT_ID = "ls_project_id";
	public static final String TLS_FIELD_GUID = "ls_origin_guid";
	public static final String TLS_FIELD_OS_ID = "ls_os_id";
	public static final String TLS_FIELD_HIDDEN = "ls_hidden";
	public static final String TLS_FIELD_NAME_RUS = "ls_name_rus";
	public static final String TLS_FIELD_NAME_UKR = "ls_name_ukr";
	public static final String TLS_FIELD_CIPHER = "ls_cipher";
	public static final String TLS_FIELD_SORT_ID = "ls_sort_id";
	public static final String TLS_FIELD_TOTAL = "ls_total";
	public static final String TLS_FIELD_DESCRIPTION = "ls_description";

	@DatabaseField(canBeNull = false, generatedId = true, columnName = TLS_FIELD_ID)
	private int lsId;
	
	@DatabaseField(columnName = TLS_FIELD_PROJECT_ID, index = true)
	private int lsProjectId;
	
	@DatabaseField(columnName = TLS_FIELD_OS_ID, index = true)
	private int lsOsId;

	@DatabaseField(columnName = TLS_FIELD_GUID)
	private String lsGuid;
	
	@DatabaseField(columnName = TLS_FIELD_HIDDEN)
	private boolean lsHidden;
	
	@DatabaseField(columnName = TLS_FIELD_NAME_RUS)
	private String lsNameRus;

	@DatabaseField(columnName = TLS_FIELD_NAME_UKR)
	private String lsNameUkr;
	
	@DatabaseField(columnName = TLS_FIELD_CIPHER)
	private String lsCipher;
	
	@DatabaseField(columnName = TLS_FIELD_SORT_ID)
	private int lsSortId;
	
	@DatabaseField(columnName = TLS_FIELD_TOTAL)
	private float lsTotal;

	@DatabaseField(columnName = TLS_FIELD_DESCRIPTION)
	private String lsDescription;

	
	@DatabaseField(canBeNull = false, foreign = true, index = true)
	private Projects lsProjects;
	
	@DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true, index = true)
	private OS lsOsTable;


	private ArrayList<Works> worksList;


	public int getLsId(){
		return lsId;
	}
	
	//Setter&Getter of project id
	public void setLsProjectId(int projId){
		lsProjectId = projId;
	}
	
	public int getLsProjectId(){
		return lsProjectId;
	}
	
	//... Os id
	public void setLsOsId(int osId){
		lsOsId = osId;
	}
	
	public int getLsOsId(){
		return lsOsId;
	}
	
	//... Hidden
	public void setLsHidden(boolean isHidden){
		lsHidden = isHidden;
	}
	
	public boolean getLsHidden(){
		return lsHidden;
	}
	
	//... NameRus
	public void setLsNameRus(String name){
		lsNameRus = name;
	}
	
	public String getLsNameRus(){
		return lsNameRus;
	}
	//... NameUkr
	public void setLsNameUkr(String name){
		lsNameUkr = name;
	}

	public String getLsNameUkr(){
		return lsNameUkr;
	}
	//... Guid
	public void setLsGuid(String Name){
		lsGuid = Name;
	}

	public String getLsGuid(){
		return lsGuid;
	}

	//... Shifr
	public void setLsCipher(String cipher){
		lsCipher = cipher;
	}
	
	public String getLsCipher(){
		return lsCipher;
	}
	//... SortId
	public void setLsSortId(int sortId){
		lsSortId = sortId;
	}
	
	public int getLsSortId(){
		return lsSortId;
	}
	//... Total
	public void setLsTotal(float total){
		lsTotal = total;
	}
	
	public float getLsTotal(){
		return lsTotal;
	}

	//... LS Description
	public void setLsDescription(String desc){lsDescription = desc;}

	public String getLsDescription(){return lsDescription;}

	//... Foreign key Projects
	public void setLsProjects(Projects proj){
		lsProjects = proj;
	}
	
	public Projects getLsProjects(){
		return lsProjects;
	}
	
	//... Foreign key OS
	public void setLsOs(OS os){
		lsOsTable = os;
	}
	
	public OS getLsOs(){
		return lsOsTable;
	}

	public void recalcLSTotal(){
		float total = 0;
		for(Works w: worksList){
			total += w.getWTotal();
		}
		lsTotal = total;
	}

	//   FOR OS LIST
	public ArrayList<Works> getAllWorks(){
		return worksList;
	}
	public Works getCurrentWork(int position){
		return worksList.get(position);
	}
	public void setAllWorks(ArrayList<Works> works){worksList = works;}

	public void setCurrentWork(Works work){worksList.add(work);}

	public int findCurrentWorkPosition(Works work){
		for(Works tmp: worksList){
			if(tmp.getWorkId() == work.getWorkId()){
				return worksList.indexOf(tmp);
			}
		}
		return -1;
	}

	public int findWorkPositionByGuid(Works work){
		for(Works tmp: worksList){
			if(tmp.getWGuid().equals(work.getWGuid())){
				return worksList.indexOf(tmp);
			}
		}
		return -1;
	}

	public void removeWork(Works work){
		for(Works tmp: worksList){
			if(tmp.getWorkId() == work.getWorkId()){
				worksList.remove(tmp);
				break;
			}
		}
	}

	public void replaceWorks(Works oldWork, Works newWork){
		int position = findWorkPositionByGuid(oldWork);
		if(position != -1){
			worksList.set(position, newWork);
		}
	}

	public void setCurrentWork(int position, Works work){worksList.set(position,work);}

	public LS() {
		// TODO Auto-generated constructor stub
		worksList = new ArrayList<Works>();
	}

}

