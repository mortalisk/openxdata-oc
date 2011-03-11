package com.test;


import java.util.Date;

/**
 * author@ gbro
 * **/

public class ServiceDetails {
	
	Integer stdNumber = null;
    Date crtDate = null;
    String by = null;
    String std_identity = null;
    Integer noOfSubj = null;
    String name = null;
    Integer age = null;
    String sex = null;
    
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	public String getBy() {
		return by;
	}
	public void setBy(String by) {
		this.by = by;
	}
	public Date getCreationDate() {
		return crtDate;
	}
	public void setCreationDate(Date crtDate) {
		this.crtDate = crtDate;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getNoOfSubj() {
		return noOfSubj;
	}
	public void setNoOfSubj(Integer noOfSubj) {
		this.noOfSubj = noOfSubj;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getStd_identity() {
		return std_identity;
	}
	public void setStd_identity(String std_identity) {
		this.std_identity = std_identity;
	}
	public Integer getStudyNumber() {
		return stdNumber;
	}
	public void setStudyNumber(Integer stdNumber) {
		this.stdNumber = stdNumber;
	}
    
}
