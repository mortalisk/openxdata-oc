package com.test;

import java.util.Date;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.io.*;
import java.util.*;

/**
 * author@ gbro
 * **/

public class DbServices
{

	private Statement state = null;
	private String datas = null;
	private Connection c = null;
	private String pswrd= null;
	private String user= null;
	private String driver= null;
	private String url= null;
	private List <Study> studyList;
	private List <String> dbdataList;
	public List<Study> studies;
	public List<Subject> subjects;

	 public DbServices( )
	 {
		 studyList = new ArrayList<Study>();
		 InnitialiseProp();
		// dbdataList = new List<String>();
	 }

	 public void InnitialiseProp()
     {
     //create an instance of properties class

     Properties props = new Properties();

     //try retrieve data from file

               try 
               {
               props.load(new FileInputStream("C:\\Program Files\\Apache Software Foundation\\Tomcat 5.5\\webapps\\StudySubjService\\properties\\OC_WS_Settings.properties"));

               // assign value to message variable only if it is not null

                  if(props.getProperty("connection.password") !=null || props.getProperty("connection.driver_class") !=null || props.getProperty("connection.username") !=null || props.getProperty("connection.url") !=null )
                    {
                    user = props.getProperty("connection.username");
                    pswrd = props.getProperty("connection.password");
                    driver = props.getProperty("connection.driver_class");
                    url = props.getProperty("connection.url");
                    }         
                  System.out.println(user + pswrd + driver + url);
               }
               //catch exception in case properties file does not exist

               catch(IOException e)
               {
            	   e.printStackTrace();
            	  // System.out.println(message);
                   }
     }
	 
	 public Connection establishConn()
	  { 
		  Connection inner= null;
		  try {
			    Class.forName(driver);
			  } 
			catch (ClassNotFoundException cnfe) 
			  {
			    System.out.println("Couldn't find the driver-class!");
			    cnfe.printStackTrace();
			    System.exit(1);
			  }
			  
			  try 
			  {
				  inner = DriverManager.getConnection(url,user,pswrd);
			  } 
			  catch (SQLException se)
				  {
				  System.out.println("Couldn't connect: print out a stack trace and exit.");
				  se.printStackTrace();
				  System.exit(1);

				  }
 
			  	return inner;
	  }
	 
	 public List SubjstudyData()
	  {
		  List resuldata = null;
		  c = establishConn();
		 
		  if (c != null)
		  {
			  try{
			  state = c.createStatement();

			    ResultSet resultSet = state.executeQuery("select study_id, name, summary, principal_investigator,date_created from study;");
			    ResultSetMetaData metaData = resultSet.getMetaData();
			    int numberOfColumns = metaData.getColumnCount();     
			    
			   resultsdisp(numberOfColumns,metaData,resultSet,"studies"); 
			    }catch(SQLException se)
			    {
			    	System.out.println("Couldn't connect: print out a stack trace and exit.");
					  se.printStackTrace();
					  System.exit(1);
			    }
		  }
			  return studies;  
	  }

	 public List Subjstudylist()
	  {
		  List resuldata= null ;
		  c = establishConn();
		 
		  if (c != null)
		  {
			  try{
			  state = c.createStatement();

			    ResultSet resultSet = state.executeQuery("select subject.subject_id, subject.gender, subject.unique_identifier,study_subject.study_Id from subject, study_subject where study_subject.study_id = 1;");
			    ResultSetMetaData metaData = resultSet.getMetaData();
			    int numberOfColumns = metaData.getColumnCount();     
			    
			    //resuldata= 
			    resultsdisp(numberOfColumns,metaData,resultSet,"subjects"); 
			    }catch(SQLException se)
			    {
			    	System.out.println("Couldn't connect: print out a stack trace and exit.");
					  se.printStackTrace();
					  System.exit(1);
			    }
		  }
			  return subjects;  
	  }
  
	 public void resultsdisp(int colNum, ResultSetMetaData metaData,ResultSet resultSet,String st )
	  {
		 				
		  System.out.println( "Welcome to OpenXdata to openclinica webservice:\n" );
		    datas ="Your request reponse is as follows:\n";
		    try{
		    for ( int i = 1; i <= colNum; i++ )
		    {
		    	datas= datas + "%-8s\t" + metaData.getColumnName( i ).toString();
		    	
		    }
		    
		    if(st.equals("studies"))
		    {
		    studies = new ArrayList<Study>();
		    while ( resultSet.next() )
		    {
		    			    	
		    	Study s  = new Study();
		    
		    	for ( int i = 1; i <= colNum; i++ )
		    	{
		    		
		    		switch(i)
					{
					case 1:
						s.setStudyId( resultSet.getObject( i ).toString());
						break;
					case 2:
						s.setStudyName(resultSet.getObject( i ).toString());
						break;
					case 3:
						s.setDescription (resultSet.getObject( i ).toString());
						break;
					case 4:
						s.setPrincipalInvestigator(resultSet.getObject( i ).toString());
						break;
					case 5:
						s.setDateCreated ( resultSet.getObject( i ).toString());
						break;
					default:
							System.out.println("The passed data has aproblem check: ");
						break;
							
					}
		    		
		    			    				    		
		    	}
		    	
		    	studies.add(s);
		    	 
		    } // end while  
		    }else if(st.equals("subjects"))
		    	{
		    	subjects = new ArrayList<Subject>();
			    while ( resultSet.next() )
			    {
			    			    	
			    	Subject s  = new Subject();
			    
			    	for ( int i = 1; i <= colNum; i++ )
			    	{
			    		
			    		switch(i)
						{
						case 1:
							s.setSubjectId( resultSet.getObject( i ).toString());
							break;
						case 2:
							s.setGender(resultSet.getObject( i ).toString());
							break;
						case 3:
							s.setUnique_identifier (resultSet.getObject( i ).toString());
							break;
						case 4:
							s.setStudyId(resultSet.getObject( i ).toString());
							break;
						case 5:
							s.setDateCreated ( resultSet.getObject( i ).toString());
							break;
						default:
								System.out.println("The passed data has aproblem check: ");
							break;
								
						}
			    				    				    		
			    	}
			    	
			    	subjects.add(s);
			    	 
			    } // end while 
		    	
		    	}
		    
		    }catch(SQLException se)
		    {
		    	System.out.println("Couldn't connect: print out a stack trace and exit.");
				  se.printStackTrace();
				  System.exit(1);
		    }

		 
	  }
	 
	 public List<Study> getstudylst()
	 {
		return  studyList;
	 }
	 public void setStudylist(List<Study> stl)
	 {
		 studyList = stl;
		 
	 }
	
}