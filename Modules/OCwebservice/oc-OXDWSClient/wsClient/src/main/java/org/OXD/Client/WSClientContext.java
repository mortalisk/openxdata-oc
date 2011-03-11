 package org.OXD.Client;
 
 import java.math.BigInteger;  
 import java.util.Date;  
 import java.util.GregorianCalendar;  
   
 import javax.xml.datatype.DatatypeConfigurationException;  
 import javax.xml.datatype.DatatypeFactory;  
 import javax.xml.datatype.XMLGregorianCalendar;  
   
 import org.springframework.context.ApplicationContext; 
 import org.springframework.context.support.FileSystemXmlApplicationContext;
 //import org.springframework.context.support.ClassPathXmlApplicationContext;  
 import org.springframework.ws.client.core.WebServiceTemplate;  
   
 import com.sample.tr.schemas.MasterSubject;  
 import com.sample.tr.schemas.SubjectDetails;  
 import com.sample.tr.schemas.StudyDetails;  
 import com.sample.tr.schemas.StudyRequest;  
import com.sample.tr.schemas.StudyResponse;  
   
 /**
  * author@ gbro
  * **/  
 
 public class WSClientContext {  
     public static void callMe() throws Exception{  
         //ApplicationContext ctx = new ClassPathXmlApplicationContext("spring-context.xml");
    	// ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath:context.xml");
    	ApplicationContext ctx = new FileSystemXmlApplicationContext("context.xml");
         WebServiceTemplate template = (WebServiceTemplate)ctx.getBean("webServiceTemplate");  
           
           
         StudyResponse resp = (StudyResponse)template.marshalSendAndReceive(retrieveRequest());  
         System.out.println(resp.getCode());  
     }  
       
     public static void main(String[] args) throws Exception {  
         callMe();  
     }  
       
     public static StudyRequest retrieveRequest(){  
    	 StudyRequest request = new StudyRequest();  
    	 SubjectDetails subdetails = new SubjectDetails();  
    	 StudyDetails stDetails = new StudyDetails();  
    	 ///stDetails.setStudyIdentity("Study_Subjects");  
    	 stDetails.setStudyIdentity("Default_Study");
    	 stDetails.setCreationDate(prepareXMLGregorianCalendar(new Date(2009,12,12)).normalize());  
    	 stDetails.setBy("Mumbai");  
    	 stDetails.setStudyNumber(new BigInteger("1333"));  
           
         MasterSubject masterSub = new MasterSubject();  
         masterSub.setAge(new BigInteger("15"));;  
         masterSub.setName("Chirag");  
         masterSub.setSex("Male");  
           
         subdetails.setMasterSubject(masterSub);  
         subdetails.setNumberOfSubjects(new BigInteger("1"));  
           
         request.setSubjectDetails(subdetails);  
         request.setStudyDetails(stDetails);  
         return request;  
     }  
       
     public static XMLGregorianCalendar prepareXMLGregorianCalendar(Date date)  
     {  
         GregorianCalendar cal = (GregorianCalendar)GregorianCalendar.getInstance();  
         cal.setTime(date);  
           
         XMLGregorianCalendar dateTimeXML = null;  
         try   
         {  
             dateTimeXML = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);  
         }   
         catch (DatatypeConfigurationException e)   
         {  
         }  
         return dateTimeXML;  
     }  
       
 }  