package ua.com.expertsoft.android_smeta.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import com.j256.ormlite.field.DatabaseField;

public class Projects implements Serializable {

	private static final long serialVersionUID = -222864131214757024L;

	public static final String TP_FIELD_ID = "proj_id";
	public static final String TP_FIELD_ORIGIN_GUID = "proj_orig_guid";
	public static final String TP_FIELD_TYPE_EXP_ID = "proj_type_exp_Id";
	public static final String TP_FIELD_NAME_RUS = "proj_name_rus";
	public static final String TP_FIELD_NAME_UKR = "proj_name_ukr";
	public static final String TP_FIELD_CIPHER = "proj_cipher";
	public static final String TP_FIELD_CREATEDATE="proj_create_date";
	public static final String TP_FIELD_CUSTOMER="proj_customer";
	public static final String TP_FIELD_CONTRACTOR = "proj_contractor";
	public static final String TP_FIELD_SORTID = "proj_sort_id";
	public static final String TP_FIELD_TOTAL = "proj_total";
	public static final String TP_FIELD_TYPE = "proj_type"; //0 - Online; 1 - offline
	public static final String TP_FIELD_ISDONE = "proj_done";
	public static final String TP_FIELD_DATA_UPDATE = "proj_data_wpdate";
	
	
	@DatabaseField(canBeNull = false, generatedId = true, columnName = TP_FIELD_ID)
	private int projId;

	@DatabaseField(canBeNull = false, columnName = TP_FIELD_ORIGIN_GUID)
	private String projOriginGuid;

	@DatabaseField(canBeNull = false, columnName = TP_FIELD_TYPE_EXP_ID, index = true)
	private int projTypeExpId;

	@DatabaseField(canBeNull = false, columnName = TP_FIELD_TYPE, index = true)
	private int projType;
	
	@DatabaseField(columnName = TP_FIELD_NAME_RUS)
	private String projNameRus;

	@DatabaseField(columnName = TP_FIELD_NAME_UKR)
	private String projNameUkr;
	
	@DatabaseField(columnName = TP_FIELD_CIPHER)
	private String projCipher;
	
	@DatabaseField(columnName = TP_FIELD_CREATEDATE)
	private Date projCreateDate;

	@DatabaseField(columnName = TP_FIELD_DATA_UPDATE)
	private Date projDataUpdate;

	@DatabaseField(columnName = TP_FIELD_CUSTOMER)
	private String projCustomer;
	
	@DatabaseField(columnName = TP_FIELD_CONTRACTOR)
	private String projContractor;
	
	@DatabaseField(columnName = TP_FIELD_SORTID)
	private int projSortId;
	
	@DatabaseField(columnName = TP_FIELD_TOTAL)
	private double projTotal;

	@DatabaseField(columnName = TP_FIELD_ISDONE)
	private boolean projIsDone;

	private ArrayList<OS> projectsOS;

	 private boolean isOpenBranch = false;




	public int getProjectId(){
		return projId;
	}

	//...Origin Guid
	public void setProjectGuid(String guid){
		projOriginGuid = guid;
	}

	public String getProjectGuid(){
		return projOriginGuid;
	}

	//...Type expantion id
	public void setProjectExpId(int expId){
		projTypeExpId = expId;
	}

	public int getProjectExpId(){
		return projTypeExpId;
	}

	//...Type
	public void setProjectType(int typeId){
		projType = typeId;
	}

	public int getProjectType(){
		return projType;
	}

	//Setter&Getter of project name
	public void setProjectNameRus(String name){
		projNameRus = name;
	}
	
	public String getProjectNameRus(){
		return projNameRus;
	}

	public void setProjectNameUkr(String name){
		projNameUkr = name;
	}

	public String getProjectNameUkr(){
		return projNameUkr;
	}
	
	//Setter&Getter cipher
	public void setProjectCipher(String cipher){
		projCipher = cipher;
	}
	
	public String getProjectCipher(){
		return projCipher;
	}
	
	//...Created Date
	public void setProjectCreatedDate(Date date){
		projCreateDate = date;
	}
	
	public Date getProjectCreatedDate(){
		return projCreateDate;
	}
	
	//... Customer
	public void setProjectCustomer(String customer){
		projCustomer = customer;
	}
	
	public String getProjectCustomer(){
		return projCustomer;
	}
	
	//... Contractor
	public void setProjectContractor(String contractorName){
		projContractor = contractorName;
	}
	
	public String getProjectContractor(){
		return projContractor;
	}
	
	//... SortId
	public void setProjectSortId(int  sortId){
		projSortId = sortId;
	}
	
	public int getProjectSortId(){
		return projSortId;
	}
	
	//... Total
	public void setProjectTotal(double total){
		projTotal = total;
	}
	
	public double getProjectTotal(){
		return projTotal;
	}


	//... IsDone
	public void setProjectDone(boolean isDone){projIsDone = isDone;}

	public boolean getProjectIsDone(){return projIsDone;}

	//... Update Date

	public void setProjectDataUpdate(Date date){projDataUpdate = date;}

	public Date getProjectDataUpdate(){return projDataUpdate;}


	public void setIsOpen(boolean isOpen){isOpenBranch = isOpen;}
	public boolean getIsOpen(){return isOpenBranch;}

	public void recalcProjectTotal(){
		float total = 0;
		for(OS os: projectsOS){
			total += os.getOsTotal();
		}
		projTotal = total;
	}

	//   FOR OS LIST
	public ArrayList<OS> getAllObjectEstimates(){
		return projectsOS;
	}
	public OS getCurrentEstimate(int position){
		return projectsOS.get(position);
	}
	public void setAllEstimates(ArrayList<OS> objEstim){projectsOS = objEstim;}

	public void setCurrentEstimate(OS estimate){projectsOS.add(estimate);}

	public void setCurrentEstimate(int position, OS estimate){projectsOS.set(position, estimate);}

	public int getCurrentEstimatePosition(OS os){
		int position = -1;
		for(OS iterOs:projectsOS) {
			if(iterOs.getOsId() == os.getOsId()){
				position = projectsOS.indexOf(iterOs);
				break;
			}
		}
		return position;
	}

	public int getEstimatePositionByGuid(OS os){
		for(OS iterOs:projectsOS) {
			if(iterOs.getOsGuid().equals(os.getOsGuid())){
				return projectsOS.indexOf(iterOs);
			}
		}
		return -1;
	}
	public void removeObjectEstimate(OS os){
		for(OS iterOs:projectsOS) {
			if(iterOs.getOsId() == os.getOsId()){
				projectsOS.remove(iterOs);
				break;
			}
		}
	}

	//default constructor
	public Projects() {
		// TODO Auto-generated constructor stub
		projectsOS = new ArrayList<OS>();
		projContractor = "";
		projCustomer = "";
		projCipher = "";
	}

}
