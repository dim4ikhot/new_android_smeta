package ua.com.expertsoft.android_smeta.data;

import java.io.File;
import java.io.Serializable;
import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import ua.com.expertsoft.android_smeta.MainActivity;
import ua.com.expertsoft.android_smeta.R;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class ORMDatabaseHelper extends OrmLiteSqliteOpenHelper implements Serializable{

	public static final String DATABASE_NAME = "DATA.db";
	public static final int DATABASE_VER = 1;
	private static final long serialVersionUID = -222864131214757024L;

	String[] defaultParts = {"Проекты ОКАД","Проекты КЛП","Стройки Э-Смета","Сметы АРПС","ПОЛЬЗОВАТЕЛЬСКИЕ"};
	
	//Our database tables
	private Dao<ProjectExp, Integer> projectExpDao;
	private Dao<UserProjects,Integer> userProjectsDao;
	private Dao<UserTask,Integer> userTaskDao;
	private Dao<UserSubTask,Integer> userSubTaskDao;
	private Dao<Facts,Integer> factsDao;
	private Dao<Projects, Integer> projectsDao;
	private Dao<OS, Integer> objectestDao;
	private Dao<LS, Integer> localestDao;
	private Dao<Works, Integer> worksDao;
	private Dao<WorksResources, Integer> worksrestDao;
	
	public ORMDatabaseHelper(Context context) {
		// TODO Auto-generated constructor stub
		super(context, Environment.getExternalStorageDirectory()+"/Android/data/"+ MainActivity.PACKAGE_NAME+"/database"
			    + File.separator + DATABASE_NAME, null, DATABASE_VER, R.raw.ormlite_config);
	}

	@Override
	public void onCreate(SQLiteDatabase sqlitedatabase, ConnectionSource connectionSource) {
		// TODO Auto-generated method stub
		try{
			TableUtils.createTable(connectionSource, UserProjects.class);
			TableUtils.createTable(connectionSource, ProjectExp.class);
			TableUtils.createTable(connectionSource, UserTask.class);
			TableUtils.createTable(connectionSource, UserSubTask.class);
			TableUtils.createTable(connectionSource, Facts.class);
			TableUtils.createTable(connectionSource, Projects.class);
			TableUtils.createTable(connectionSource, OS.class);
			TableUtils.createTable(connectionSource, LS.class);
			TableUtils.createTable(connectionSource, Works.class);
			TableUtils.createTable(connectionSource, WorksResources.class);

			//Default values
			try{
				Dao<ProjectExp, Integer> projectExpsDao = getProjectExpDao();
				ProjectExp defaults;
				for(int i = 0; i<5; i++){
					defaults = new ProjectExp();
					defaults.setProjExpType(i);
					defaults.setProjExpName(defaultParts[i]);
					projectExpsDao.create(defaults);
				}
			}catch(SQLException e)
			{
				e.printStackTrace();
			}
		}catch(SQLException e)
		{
			Log.d("myLogs", e.getMessage().toString());
		}	
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqlitedatabase, ConnectionSource connectionSource, int oldVer,
			int newVer) {
		// Database update....
		/*
		if (newVer == 2){
			try{
				Dao<WorksResources, Integer> worksresDao = getWorksResDao();
				worksresDao.executeRaw("ALTER TABLE 'worksresources' ADD COLUMN workres_onoff INTEGER");
				worksresDao.updateRaw("UPDATE 'worksresources' SET workres_onoff = -1");
			}catch(SQLException e)
			{
				e.printStackTrace();
			}
		}
		try{
			TableUtils.dropTable(connectionSource, Projects.class, true);
			TableUtils.dropTable(connectionSource, OS.class,true);
			TableUtils.dropTable(connectionSource, LS.class,true);
			TableUtils.dropTable(connectionSource, Works.class, true);
			TableUtils.dropTable(connectionSource, WorksResources.class,true);
			
			onCreate(sqlitedatabase, connectionSource);
			
		}catch(SQLException e)
		{
			e.printStackTrace();
		}	
		*/
	}
	
	public Dao<Projects, Integer> getProjectsDao() throws SQLException {
		if (projectsDao == null) {
			projectsDao = getDao(Projects.class);
		}
		return projectsDao;
	}
	
	public Dao<OS, Integer> getOSDao() throws SQLException {
		if (objectestDao == null) {
			objectestDao = getDao(OS.class);
		}
		return objectestDao;
	}
	
	public Dao<LS, Integer> getLSDao() throws SQLException {
		if (localestDao == null) {
			localestDao = getDao(LS.class);
		}
		return localestDao;
	}
	
	public Dao<Works, Integer> getWorksDao() throws SQLException {
		if (worksDao == null) {
			worksDao = getDao(Works.class);
		}
		return worksDao;
	}
	
	public Dao<WorksResources, Integer> getWorksResDao() throws SQLException {
		if (worksrestDao == null) {
			worksrestDao = getDao(WorksResources.class);
		}
		return worksrestDao;
	}

	public Dao<ProjectExp, Integer> getProjectExpDao() throws SQLException {
		if (projectExpDao == null) {
			projectExpDao = getDao(ProjectExp.class);
		}
		return projectExpDao;
	}

	public Dao<UserProjects, Integer> getUserProjectsDao() throws SQLException {
		if (userProjectsDao == null) {
			userProjectsDao = getDao(UserProjects.class);
		}
		return userProjectsDao;
	}

	public Dao<UserTask, Integer> getUseTasksDao() throws SQLException {
		if (userTaskDao == null) {
			userTaskDao = getDao(UserTask.class);
		}
		return userTaskDao;
	}

	public Dao<UserSubTask, Integer> getUserSubTaskDao() throws SQLException {
		if (userSubTaskDao == null) {
			userSubTaskDao = getDao(UserSubTask.class);
		}
		return userSubTaskDao;
	}

	public Dao<Facts, Integer> getFactsDao() throws SQLException {
		if (factsDao == null) {
			factsDao = getDao(Facts.class);
		}
		return factsDao;
	}
}
