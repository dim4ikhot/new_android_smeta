package ua.com.expertsoft.android_smeta.data;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

public class WorksResources implements Serializable {

	public static final String TWS_FIELD_ID = "workres_id";
	public static final String TWS_FIELD_WORK_ID = "workres_work_id";
	public static final String TWS_FIELD_GUID = "orig_guid";
	public static final String TWS_FIELD_NAME_RUS = "workres_name_rus";
	public static final String TWS_FIELD_NAME_UKR = "workres_name_ukr";
	public static final String TWS_FIELD_CIPHER = "workres_cipher";
	public static final String TWS_FIELD_MEASURED_RUS = "workres_measured_rus";
	public static final String TWS_FIELD_MEASURED_UKR = "workres_measured_ukr";
	public static final String TWS_FIELD_COUNT = "workres_count";
	public static final String TWS_FIELD_COST = "workres_cost";
	public static final String TWS_FIELD_ONOFF = "workres_onoff";
	public static final String TWS_FIELD_TOTALCOST = "workres_totalcost";
	public static final String TWS_FIELD_PART = "workres_part";
	public static final String TWS_FIELD_NPP = "workres_npp";
	public static final String TWS_FIELD_DESCRIPTION = "workres_description";
	public static final String TWS_FIELD_RES_GROUP_TAG = "workres_group_tag";
	// 20.05.2016 Added new fields
	public static final String TWS_FIELD_DISTRIBUTOR = "distributor";
	public static final String TWS_FIELD_VENDOR = "vendor";
	public static final String TWS_FIELD_PARENT_GUID = "parent_guid";
	
	@DatabaseField(canBeNull = false, generatedId = true, columnName = TWS_FIELD_ID)
	private int workResId;
	
	@DatabaseField(columnName = TWS_FIELD_WORK_ID, index = true)
	private int wrWorkId;

	@DatabaseField(columnName = TWS_FIELD_GUID)
	private String wrGuid;
	
	@DatabaseField(columnName = TWS_FIELD_NAME_RUS)
	private String wrNameRus;

	@DatabaseField(columnName = TWS_FIELD_NAME_UKR)
	private String wrNameUkr;
	
	@DatabaseField(columnName = TWS_FIELD_CIPHER)
	private String wrCipher;
	
	@DatabaseField(columnName = TWS_FIELD_MEASURED_RUS)
	private String wrMeasuredRus;

	@DatabaseField(columnName = TWS_FIELD_MEASURED_UKR)
	private String wrMeasuredUkr;

	@DatabaseField(columnName = TWS_FIELD_DESCRIPTION)
	private String wrDescription;
	
	@DatabaseField(columnName = TWS_FIELD_COUNT)
	private float wrCount;
	
	@DatabaseField(columnName = TWS_FIELD_COST)
	private float wrCost;
	
	@DatabaseField(columnName = TWS_FIELD_TOTALCOST)
	private float wrTotalCost;
	
	@DatabaseField(columnName = TWS_FIELD_ONOFF)
	private int wrOnOff;
	
	@DatabaseField(columnName = TWS_FIELD_PART)
	private int wrPart;

	@DatabaseField(columnName = TWS_FIELD_NPP)
	private int wrNpp;

	@DatabaseField(columnName = TWS_FIELD_RES_GROUP_TAG)
	private String wrResGroupTag;

	@DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true, index = true)
	private Works wrWFK;

	//20.05.2016 Added new fields

	@DatabaseField(columnName = TWS_FIELD_DISTRIBUTOR)
	private String wrDistributor;

	@DatabaseField(columnName = TWS_FIELD_VENDOR)
	private String wrVendor;

	@DatabaseField(columnName = TWS_FIELD_PARENT_GUID)
	private String wrParentGuid;


	public int getWrId(){
		return workResId;
	}
	
	// Setter & Getter of works resource Work id
	public void setWrWorkId(int workId){
		wrWorkId = workId;
	}
	
	public int getWrWorkId(){
		return wrWorkId;
	}

	//... GUID
	public void setWrGuid(String workGuid){
		wrGuid = workGuid;
	}

	public String getWrGuid(){
		return wrGuid;
	}
	
	//... Name
	public void setWrNameRus(String name){
		wrNameRus = name;
	}
	
	public String getWrNameRus(){
		return wrNameRus;
	}

	//... NameUrk
	public void setWrNameUkr(String name){
		wrNameUkr = name;
	}

	public String getWrNameUkr(){
		return wrNameUkr;
	}

	//... Cipher
	public void setWrCipher(String cipher){
		wrCipher = cipher;
	}
	
	public String getWrCipher(){
		return wrCipher;
	}
	//... Measured
	public void setWrMeasuredRus(String measured){
		wrMeasuredRus= measured;
	}
	
	public String getWrMeasuredRus(){
		return wrMeasuredRus;
	}

	//... MeasuredUkr
	public void setWrMeasuredUkr(String measured){
		wrMeasuredUkr = measured;
	}

	public String getWrMeasuredUkr(){
		return wrMeasuredUkr;
	}

	//... description
	public void setWrDescription(String desc){
		wrDescription = desc;
	}

	public String getWrDescription(){
		return wrDescription;
	}

	// res group tag
	public void setWrResGroupTag(String tag){
		wrResGroupTag = tag;
	}

	public String getWrResGroupTag(){
		return wrResGroupTag;
	}

	//... Count
	public void setWrCount(float count){
		wrCount = count;
	}
	
	public float getWrCount(){
		return wrCount;
	}
	//... Cost
	public void setWrCost(float cost){
		wrCost = cost;
	}
	
	public float getWrCost(){
		return wrCost;
	}
	//... Total Cost
	public void setWrTotalCost(float totalCost){
		wrTotalCost = totalCost;
	}
	
	public float getWrTotalCost(){
		return wrTotalCost;
	}
	
	//... onOff
	public void setWrOnOff(int onOff){
		wrOnOff = onOff;
	}
	
	public int getWrOnOff(){
		return wrOnOff;
	}
	
	//... Part
	public void setWrPart(int part){
		wrPart = part;
	}
	
	public int getWrPart(){
		return wrPart;
	}

	//... NPP
	public void setWrNpp(int npp){
		wrNpp = npp;
	}

	public int getWrNpp(){
		return wrNpp;
	}

	//... Foreign Works
	public void setWrWork(Works work){
		wrWFK = work;
	}
	
	public int getWrWork(){
		return wrWorkId;
	}

	//DISTRIBUTOR
	public void setWrDistributor(String distributor) {
		wrDistributor = distributor;
	}
	public String getWrDistributor(){return wrDistributor;}

	//VENDOR
	public void setWrVendor(String vendor) {
		wrVendor = vendor;
	}
	public String getWrVendor(){return wrVendor;}

	//PARENT_GUID
	public void setWrParentGuid(String parent_guid) {
		wrParentGuid = parent_guid;
	}
	public String getWrParentGuid(){return wrParentGuid;}

	public WorksResources() {
		// TODO Auto-generated constructor stub
	}

	public WorksResources(String wrname, String wrcipher, String wrmeas, float wrcount, 
			              float wrcost, float wrtotalcost,int wrpart, Works worksfk) {
		// TODO Auto-generated constructor stub
		wrNameRus = wrname;
		wrCipher = wrcipher;
		wrMeasuredRus = wrmeas;
		wrCount = wrcount;
		wrCost = wrcost;
		wrTotalCost = wrtotalcost;
		wrPart = wrpart;
		wrWFK = worksfk;
	}
}
