package org.openxdata.server.module.openclinica.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


/**
 * 
 * @author daniel
 *
 */
public class CrfDef {

	private int crfId;
	private String oid;
	private String name;
	
	
	public CrfDef(){
		
	}

	public int getCrfId() {
		return crfId;
	}

	public void setCrfId(int crfId) {
		this.crfId = crfId;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void serialize(DataOutputStream dos) throws IOException {
		dos.writeInt(crfId);
		dos.writeUTF(oid);
		dos.writeUTF(name);
	}
	
	public void deserialize(DataInputStream dis) throws IOException {
		crfId = dis.readInt();
		oid = dis.readUTF();
		name = dis.readUTF();
	}
}
