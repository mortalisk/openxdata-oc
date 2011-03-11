package com.test;

import java.util.Date;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.List;

/**
 * author@ gbro
 * **/

public class StudySubService {

	private Statement state = null;
	private String datas = null;
	private StudyListCreater createXML;
	private SubjectListCreater SubjcreateXML;
			
	

	public String ImplementTask(ServiceDetails serviceDetail) {
		
		if(serviceDetail.getStudyNumber() >0 && serviceDetail.getStudyNumber()<10000){
			//if(serviceDetail.getCreationDate().getDay() > new Date(System.currentTimeMillis()).getDay()){
			if(serviceDetail.getCreationDate().getDay()!= 0){
				return "---- Connected to OXD-OC WebServer Successfully----\n \n "+ determinService(serviceDetail);
			}else{
				return "Creation Date is not valid";
			}
		}
		else{
			return "Study Number not valid";
		}
	}

	public String operationTask(String ocData)
	{ 
		DbServices dbsrv = new DbServices();
		
	if(ocData.equals("Default_Study"))
		{
		
		List<Study> studyList  = dbsrv.SubjstudyData();
			 createXML = new StudyListCreater( studyList);
			//Get a DOM object
			 createXML.createDocument();
			 datas= createXML.runEngine();
			 	 
		}
	if(ocData.equals("Study_Subjects"))
	{
		List<Subject> SubjectList = dbsrv.Subjstudylist();
		SubjcreateXML = new SubjectListCreater( SubjectList);
			//Get a DOM object
		SubjcreateXML.createDocument();
	    datas= SubjcreateXML.runEngine();	 
	}
			
	return datas;
	}
	public String determinService(ServiceDetails serviceDetail)
	{
		 if(serviceDetail.getStudyNumber()>0 && serviceDetail.getName().length() >0)
		 {
			 datas = operationTask(  serviceDetail.getStd_identity());
		 }
		 return datas;	 
	}

		
		
	
}
