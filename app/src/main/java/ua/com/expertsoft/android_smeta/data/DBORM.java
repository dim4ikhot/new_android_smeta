package ua.com.expertsoft.android_smeta.data;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

public class DBORM implements Serializable {

	ORMDatabaseHelper databaseHelper = null;
	Context context;
	
	private static final long serialVersionUID = -222864131214757024L;
	
	public DBORM(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		getHelper();
	}
	
	public ORMDatabaseHelper getHelper(){
		if(databaseHelper == null)
		{
			databaseHelper = OpenHelperManager.getHelper(context, ORMDatabaseHelper.class);
		}		
		return databaseHelper;			
	}
	
	public void destroyHelper(){
		if (databaseHelper != null) {
			OpenHelperManager.releaseHelper();
			databaseHelper = null;
		}
	}

	public List<ProjectExp> getAllProjectsKind(){
		try{
			return getHelper().getProjectExpDao().queryForAll();
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}
	}

	public int getAllUserProjectsCount() {
		try {
			return getHelper().getUserProjectsDao().queryForAll().size();
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public ArrayList<UserProjects> getAllUserProjects() {
		try {
			return new ArrayList<>(getHelper().getUserProjectsDao().queryForAll());
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public ArrayList<UserTask> getUsersTasks(UserProjects proj){
		ArrayList<UserTask> userTasks = new ArrayList<>();
		try{
			// This is how, a reference of DAO object can be done
			Dao<UserTask,Integer> userTasksDao =  getHelper().getUseTasksDao();

			// Get our query builder from the DAO
			final QueryBuilder<UserTask, Integer> queryBuilder = userTasksDao.queryBuilder();

			// We need only Tasks by selected project
			queryBuilder.where().eq(UserTask.USER_TASK_FIELD_UPROJ_ID,  proj.getUserProjId());

			// Prepare our SQL statement
			final PreparedQuery<UserTask> preparedQuery = queryBuilder.prepare();

			// Fetch the list from Database by queryingit
			final Iterator<UserTask>  studentsIt = userTasksDao.query(preparedQuery).iterator();

			// Iterate through the UserTask object iterator and populate the comma separated String
			while (studentsIt.hasNext()) {
				final UserTask tasks = studentsIt.next();
				userTasks.add(tasks);
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		return userTasks;
	}

	public ArrayList<UserTask> getUsersTasksByDate(Date date){
		ArrayList<UserTask> userTasks = new ArrayList<>();
		try{
			// This is how, a reference of DAO object can be done
			Dao<UserTask,Integer> userTasksDao =  getHelper().getUseTasksDao();
			// Get our query builder from the DAO
			final QueryBuilder<UserTask, Integer> queryBuilder = userTasksDao.queryBuilder();
			// We need only Tasks by selected project
			queryBuilder.where().eq(UserTask.USER_TASK_FIELD_DATE,  date);
			// Prepare our SQL statement
			final PreparedQuery<UserTask> preparedQuery = queryBuilder.prepare();
			// Fetch the list from Database by queryingit
			final Iterator<UserTask>  studentsIt = userTasksDao.query(preparedQuery).iterator();
			// Iterate through the UserTask object iterator and populate the comma separated String
			while (studentsIt.hasNext()) {
				final UserTask tasks = studentsIt.next();
				userTasks.add(tasks);
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		return userTasks;
	}

	public ArrayList<UserTask> getUsersTasksByDateTime(Date date, Date time){
		ArrayList<UserTask> userTasks = new ArrayList<>();
		try{
			// This is how, a reference of DAO object can be done
			Dao<UserTask,Integer> userTasksDao =  getHelper().getUseTasksDao();
			// Get our query builder from the DAO
			final QueryBuilder<UserTask, Integer> queryBuilder = userTasksDao.queryBuilder();
			// We need only Tasks by selected project
			Where<UserTask,Integer> where = queryBuilder.where();
			where.and(
					where.eq(UserTask.USER_TASK_FIELD_DATE, date),
					where.eq(UserTask.USER_TASK_FIELD_TIME, time)
			);
			// Prepare our SQL statement
			final PreparedQuery<UserTask> preparedQuery = queryBuilder.prepare();
			// Fetch the list from Database by queryingit
			final Iterator<UserTask>  studentsIt = userTasksDao.query(preparedQuery).iterator();
			// Iterate through the UserTask object iterator and populate the comma separated String
			while (studentsIt.hasNext()) {
				final UserTask tasks = studentsIt.next();
				userTasks.add(tasks);
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		return userTasks;
	}

	public ArrayList<UserSubTask> getUsersSubTasks(UserTask task){
		ArrayList<UserSubTask> userSubTasks = new ArrayList<>();
		try{
			// This is how, a reference of DAO object can be done
			Dao<UserSubTask,Integer> userSubTasksDao =  getHelper().getUserSubTaskDao();

			// Get our query builder from the DAO
			final QueryBuilder<UserSubTask, Integer> queryBuilder = userSubTasksDao.queryBuilder();

			// We need only Tasks by selected project
			queryBuilder.where().eq(UserSubTask.USER_SUBTASK_FIELD_UTASK_ID, task.getUserTaskId());

			// Prepare our SQL statement
			final PreparedQuery<UserSubTask> preparedQuery = queryBuilder.prepare();

			// Fetch the list from Database by queryingit
			final Iterator<UserSubTask>  studentsIt = userSubTasksDao.query(preparedQuery).iterator();

			// Iterate through the UserTask object iterator and populate the comma separated String
			while (studentsIt.hasNext()) {
				final UserSubTask tasks = studentsIt.next();
				userSubTasks.add(tasks);
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		return userSubTasks;
	}

		public ArrayList<Projects> getAllProjectsData(int projectTypeId){
			ArrayList<Projects> projList = new ArrayList<>();
		try{
			// This is how, a reference of DAO object can be done
			Dao<Projects,Integer> projDao =  getHelper().getProjectsDao();

			// Get our query builder from the DAO
			final QueryBuilder<Projects, Integer> queryBuilder = projDao.queryBuilder();

			// We need only Students who are associated with the selected Teacher, so build the query by "Where" clause
			queryBuilder.where().eq(Projects.TP_FIELD_TYPE_EXP_ID,  projectTypeId);

			// Prepare our SQL statement
			final PreparedQuery<Projects> preparedQuery = queryBuilder.prepare();

			// Fetch the list from Database by queryingit
			final Iterator<Projects>  studentsIt = projDao.query(preparedQuery).iterator();

			// Iterate through the StudentDetails object iterator and populate the comma separated String
			while (studentsIt.hasNext()) {
				final Projects oss = studentsIt.next();
				oss.setAllEstimates(getObjectEstimate(oss));
				projList.add(oss);
			}
			return projList;
		}catch(SQLException e)
		{
			Log.d("myLogs", e.getMessage().toString());
			return new ArrayList<>();
		}
		
	}
	
	public List<OS> getAllOEstimateData(){
		try{
			return getHelper().getOSDao().queryForAll();
		}catch(SQLException e)
		{
			Log.d("muLogs", e.getMessage().toString());
			return new ArrayList<>();
		}
		
	}
	
	public ArrayList<OS> getObjectEstimate(Projects proj){
		ArrayList<OS> osList = new ArrayList<>();
		try {
			// This is how, a reference of DAO object can be done
			Dao<OS,Integer> osDao =  getHelper().getOSDao();
			
			// Get our query builder from the DAO
			final QueryBuilder<OS, Integer> queryBuilder = osDao.queryBuilder();
			
			// We need only Students who are associated with the selected Teacher, so build the query by "Where" clause
			queryBuilder.where().eq(OS.TOS_FIELD_PROJECT_ID,  proj.getProjectId());
			
			// Prepare our SQL statement
			final PreparedQuery<OS> preparedQuery = queryBuilder.prepare();
			
			// Fetch the list from Database by queryingit 
			final Iterator<OS>  studentsIt = osDao.query(preparedQuery).iterator();
			
			// Iterate through the StudentDetails object iterator and populate the comma separated String
			while (studentsIt.hasNext()) {
				final OS oss = studentsIt.next();
				oss.setOsProjects(proj);
				oss.setAllEstimates(getLocalEstimate(proj, oss));
				osList.add(oss);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return osList;
	}
	
	public ArrayList<LS> getLocalEstimate(Projects proj, OS os){
		ArrayList<LS> lsList = new ArrayList<>();
		try {
			// This is how, a reference of DAO object can be done
			Dao<LS,Integer> lsDao =  getHelper().getLSDao();
			
			// Get our query builder from the DAO
			final QueryBuilder<LS, Integer> queryBuilder = lsDao.queryBuilder();
			
			// We need only Students who are associated with the selected Teacher, so build the query by "Where" clause
			queryBuilder.where().eq(LS.TLS_FIELD_PROJECT_ID,  proj.getProjectId());
			queryBuilder.where().eq(LS.TLS_FIELD_OS_ID,  os.getOsId());
			
			// Prepare our SQL statement
			final PreparedQuery<LS> preparedQuery = queryBuilder.prepare();
			
			// Fetch the list from Database by queryingit 
			final Iterator<LS>  studentsIt = lsDao.query(preparedQuery).iterator();
			
			// Iterate through the StudentDetails object iterator and populate the comma separated String
			while (studentsIt.hasNext()) {
				final LS oss = studentsIt.next();
				oss.setLsOs(os);
				oss.setLsProjects(proj);
			//	oss.setAllWorks(getWorks(proj,os,oss));
				lsList.add(oss);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lsList;
	}
	
	public ArrayList<Works> getWorks(Projects proj, OS os, LS ls, boolean isLoadWorkParams){
		ArrayList<Works> worksList = new ArrayList<>();
		try {
			// This is how, a reference of DAO object can be done
			Dao<Works,Integer> worksGroupDao =  getHelper().getWorksDao();
			
			// Get our query builder from the DAO
			final QueryBuilder<Works, Integer> queryBuilder = worksGroupDao.queryBuilder();
			
			// We need only Students who are associated with the selected Teacher, so build the query by "Where" clause
			Where<Works,Integer> where = queryBuilder.where();
			where.and(
					where.eq(Works.TW_FIELD_PROJECT_ID, proj.getProjectId()),
					where.eq(Works.TW_FIELD_OS_ID, os.getOsId()),
					where.eq(Works.TW_FIELD_LS_ID, ls.getLsId()),
					where.ne(Works.TW_FIELD_REC, "koef")/*
					where.or(
							where.and(
									where.ne(Works.TW_FIELD_REC, "machine"),
									where.ne(Works.TW_FIELD_REC, "resource")),
							where.eq(Works.TW_FIELD_PARENT_NORM_ID, 0))*/);
				 
			
			// Prepare our SQL statement
			final PreparedQuery<Works> preparedQuery = queryBuilder.prepare();
			
			// Fetch the list from Database by queryingit 
			final Iterator<Works>  studentsIt = worksGroupDao.query(preparedQuery).iterator();
			
			// Iterate through the StudentDetails object iterator and populate the comma separated String
			Works oss;
			while (studentsIt.hasNext()) {
				oss = studentsIt.next();
				oss.setWLSFK(ls);
				oss.setWOSFK(os);
				oss.setWProjectFK(proj);
				if(isLoadWorkParams) {
					oss.setAllFactss(getWorksFacts(oss));
					oss.setAllResources(getWorksResource(oss));
				}
				worksList.add(oss);

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return worksList;
	}

	public ArrayList<Works> getWorksFilter(Projects proj, int id, String columnName){
		ArrayList<Works> worksList = new ArrayList<>();
		try {
			// This is how, a reference of DAO object can be done
			Dao<Works,Integer> worksGroupDao =  getHelper().getWorksDao();
			QueryBuilder<Works, Integer> queryBuilder = worksGroupDao.queryBuilder();
			queryBuilder = queryBuilder.distinct().selectColumns(columnName);
			Where<Works,Integer> where = queryBuilder.where();
			where.and(
					where.eq(Works.TW_FIELD_PROJECT_ID, proj.getProjectId()),
					//where.eq(Works.TW_FIELD_LS_ID, id),
					where.or(where.eq(Works.TW_FIELD_LS_ID, id),
							where.eq(Works.TW_FIELD_OS_ID, id)
					),
					where.ne(columnName, ""),
					where.ne(Works.TW_FIELD_REC, "koef"));
			// Prepare our SQL statement
			final PreparedQuery<Works> preparedQuery = queryBuilder.prepare();
			// Fetch the list from Database by queryingit
			final Iterator<Works>  studentsIt = worksGroupDao.query(preparedQuery).iterator();
			// Iterate through the StudentDetails object iterator and populate the comma separated String
			while (studentsIt.hasNext()) {
				final Works oss = studentsIt.next();
				worksList.add(oss);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return worksList;
	}

	public ArrayList<LS> getWorksFilterByOs(Projects proj, String columnName){
		ArrayList<LS> worksList = new ArrayList<>();
		try {
			// This is how, a reference of DAO object can be done
			Dao<LS,Integer> lsDao =  getHelper().getLSDao();
			QueryBuilder<LS, Integer> queryBuilder = lsDao.queryBuilder();
			queryBuilder = queryBuilder.distinct().selectColumns(columnName);
			Where<LS,Integer> where = queryBuilder.where();
			where.and(
					where.eq(LS.TLS_FIELD_PROJECT_ID, proj.getProjectId()),
					where.ne(columnName, ""));
			// Prepare our SQL statement
			final PreparedQuery<LS> preparedQuery = queryBuilder.prepare();
			// Fetch the list from Database by queryingit
			final Iterator<LS>  studentsIt = lsDao.query(preparedQuery).iterator();
			// Iterate through the StudentDetails object iterator and populate the comma separated String
			while (studentsIt.hasNext()) {
				final LS oss = studentsIt.next();
				worksList.add(oss);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return worksList;
	}

	public ArrayList<Works> getWorksByFilter(Projects proj,String columnName, String filter){
		ArrayList<Works> worksList = new ArrayList<>();
		try {
			// This is how, a reference of DAO object can be done
			Dao<Works,Integer> worksGroupDao =  getHelper().getWorksDao();
			
			// Get our query builder from the DAO
			final QueryBuilder<Works, Integer> queryBuilder = worksGroupDao.queryBuilder();
			// We need only Students who are associated with the selected Teacher, so build the query by "Where" clause
			Where<Works,Integer> where = queryBuilder.where();
			where.and(
					where.eq(Works.TW_FIELD_PROJECT_ID, proj.getProjectId()),
					where.eq(columnName, filter));
			// Prepare our SQL statement
			final PreparedQuery<Works> preparedQuery = queryBuilder.prepare();
			// Fetch the list from Database by queryingit 
			final Iterator<Works>  studentsIt = worksGroupDao.query(preparedQuery).iterator();
			// Iterate through the StudentDetails object iterator and populate the comma separated String
			while (studentsIt.hasNext()) {
				final Works oss = studentsIt.next();
				worksList.add(oss);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return worksList;
	}
	
	public List<Works> getWorksChild(Projects proj, OS os, LS ls, Works work){
		List<Works> worksList = new ArrayList<>();
		try {
			// This is how, a reference of DAO object can be done
			Dao<Works,Integer> workChildDao =  getHelper().getWorksDao();
			
			// Get our query builder from the DAO
			final QueryBuilder<Works, Integer> queryBuilder = workChildDao.queryBuilder();
			
			// We need only Students who are associated with the selected Teacher, so build the query by "Where" clause
			Where<Works,Integer> where = queryBuilder.where();
			where.and(
					where.eq(Works.TW_FIELD_PROJECT_ID, proj.getProjectId()),
					where.eq(Works.TW_FIELD_OS_ID, os.getOsId()),
					where.eq(Works.TW_FIELD_LS_ID, ls.getLsId()),
					where.eq(Works.TW_FIELD_PARENT_NORM_ID, work.getWorkId()),
					where.or(where.eq(Works.TW_FIELD_REC, "machine"),
							where.eq(Works.TW_FIELD_REC, "resource"))
			);
			// Prepare our SQL statement
			final PreparedQuery<Works> preparedQuery = queryBuilder.prepare();
			
			// Fetch the list from Database by queryingit 
			final Iterator<Works>  studentsIt = workChildDao.query(preparedQuery).iterator();
			
			// Iterate through the StudentDetails object iterator and populate the comma separated String
			while (studentsIt.hasNext()) {
				final Works oss = studentsIt.next();
				worksList.add(oss);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return worksList;
	}
	
	public ArrayList<WorksResources> getWorksResource(Works work){
		ArrayList<WorksResources> worksList = new ArrayList<>();
		try {
			// This is how, a reference of DAO object can be done
			Dao<WorksResources,Integer> workResDao =  getHelper().getWorksResDao();			
			// Get our query builder from the DAO
			final QueryBuilder<WorksResources, Integer> queryBuilder = workResDao.queryBuilder();			
			// We need only Students who are associated with the selected Teacher, so build the query by "Where" clause
			Where<WorksResources,Integer> where = queryBuilder.where();
			where.eq(WorksResources.TWS_FIELD_WORK_ID, work.getWorkId());
			// Prepare our SQL statement
			final PreparedQuery<WorksResources> preparedQuery = queryBuilder.prepare();			
			// Fetch the list from Database by queryingit 
			final Iterator<WorksResources>  studentsIt = workResDao.query(preparedQuery).iterator();			
			// Iterate through the StudentDetails object iterator and populate the comma separated String
			while (studentsIt.hasNext()) {
				final WorksResources oss = studentsIt.next();
				oss.setWrWork(work);
				worksList.add(oss);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return worksList;
	}

	public ArrayList<Facts> getWorksFacts(Works work){
		ArrayList<Facts> factsList = new ArrayList<>();
		try {
			// This is how, a reference of DAO object can be done
			Dao<Facts,Integer> workResDao =  getHelper().getFactsDao();
			// Get our query builder from the DAO
			final QueryBuilder<Facts, Integer> queryBuilder = workResDao.queryBuilder();
			// We need only Students who are associated with the selected Teacher, so build the query by "Where" clause
			Where<Facts,Integer> where = queryBuilder.where();
			where.eq(Facts.FACTS_FIELD_WORK_ID, work.getWorkId());
			// Prepare our SQL statement
			final PreparedQuery<Facts> preparedQuery = queryBuilder.prepare();
			// Fetch the list from Database by queryingit
			final Iterator<Facts>  studentsIt = workResDao.query(preparedQuery).iterator();
			// Iterate through the StudentDetails object iterator and populate the comma separated String
			while (studentsIt.hasNext()) {
				final Facts fact = studentsIt.next();
				fact.setFactsParent(work);
				factsList.add(fact);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return factsList;
	}



	/**********************************   DELETING PART   ***********************************/
	public void deleteWorksInSide(LS ls){
		try {
			// This is how, a reference of DAO object can be done
			Dao<Works,Integer> worksGroupDao =  getHelper().getWorksDao();

			// Get our query builder from the DAO
			final QueryBuilder<Works, Integer> queryBuilder = worksGroupDao.queryBuilder();
			// We need only Students who are associated with the selected Teacher, so build the query by "Where" clause
			Where<Works,Integer> where = queryBuilder.where();
			where.eq(Works.TW_FIELD_LS_ID, ls.getLsId());
			// Prepare our SQL statement
			final PreparedQuery<Works> preparedQuery = queryBuilder.prepare();
			// Fetch the list from Database by queryingit
			final Iterator<Works>  studentsIt = worksGroupDao.query(preparedQuery).iterator();
			// Iterate through the StudentDetails object iterator and populate the comma separated String
			while (studentsIt.hasNext()) {
				final Works w = studentsIt.next();
				deleteWorksFacts(w);
				deleteWorksResource(w);
			}
			deleteWorks(ls);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public void deleteWorks(LS ls){
		try {
			// This is how, a reference of DAO object can be done
			Dao<Works,Integer> worksGroupDao =  getHelper().getWorksDao();
			// Get our query builder from the DAO
			final DeleteBuilder<Works, Integer> deleteBuilder = worksGroupDao.deleteBuilder();
			// We need only Students who are associated with the selected Teacher, so build the query by "Where" clause
			Where<Works,Integer> where = deleteBuilder.where();
			where.eq(Works.TW_FIELD_LS_ID, ls.getLsId());
			deleteBuilder.delete();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void deleteWorksResource(Works work){
		try {
			// This is how, a reference of DAO object can be done
			Dao<WorksResources,Integer> workResDao =  getHelper().getWorksResDao();
			// Get our query builder from the DAO
			final DeleteBuilder<WorksResources, Integer> deleteBuilder = workResDao.deleteBuilder();
			// We need only Students who are associated with the selected Teacher, so build the query by "Where" clause
			Where<WorksResources,Integer> where = deleteBuilder.where();
			where.eq(WorksResources.TWS_FIELD_WORK_ID, work.getWorkId());
			// Prepare our SQL statement
			final PreparedDelete<WorksResources> preparedQuery = deleteBuilder.prepare();
			// Fetch the list from Database by queryingit
			workResDao.delete(preparedQuery);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void deleteWorksFacts(Works work){
		try {
			// This is how, a reference of DAO object can be done
			Dao<Facts,Integer> workResDao =  getHelper().getFactsDao();
			// Get our query builder from the DAO
			final DeleteBuilder<Facts, Integer> deleteBuilder = workResDao.deleteBuilder();
			// We need only Students who are associated with the selected Teacher, so build the query by "Where" clause
			Where<Facts,Integer> where = deleteBuilder.where();
			where.eq(Facts.FACTS_FIELD_WORK_ID,  work.getWorkId());
			deleteBuilder.delete();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/****************************************   END   ****************************************/
}
