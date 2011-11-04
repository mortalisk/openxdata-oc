package org.openxdata.server.admin.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.sf.gilead.pojo.gwt.LightEntity;


public class OpenclinicaStudy extends LightEntity {
	
	private static final long serialVersionUID = 1812048393826464036L;
	
	String OID;
	String name;
	String identifier;

	private Set<String> subjects = new HashSet<String>();
	
	public String getOID(){
		return OID;
	}
	
	public void setOID(String OID){
		this.OID = OID;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getIdentifier(){
		return identifier;
	}
	
	public void setIdentifier(String identifier){
		this.identifier = identifier;
	}
	
	public void setSubjects(Collection<String> subjectKeys) {
		for(String x : subjectKeys){
			this.subjects.add(x);
		}
	}
	
	public Set<String> getSubjects(){
		return this.subjects;
	}
	
	@Override
	public boolean equals(Object otherStudy) {
		if (otherStudy == null)
			return false;
		
		if (otherStudy == this)
			return true;
		
		if (this.getClass() != otherStudy.getClass())
			return false;

		OpenclinicaStudy x = (OpenclinicaStudy) otherStudy;
		if (this.OID == x.getOID() && this.name == x.getName()
				&& this.identifier == x.getIdentifier())
			return true;
		
		return false;
	}
}
