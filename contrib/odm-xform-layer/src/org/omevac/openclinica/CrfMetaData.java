package org.omevac.openclinica;


/**
 * Contains meta data about a crf (eg name, version_id, etc)
 * 
 * @author daniel
 *
 */
public class CrfMetaData {

	private String name;
	private int versionId;
	
	public CrfMetaData(String name, int versionId) {
		super();
		this.name = name;
		this.versionId = versionId;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getVersionId() {
		return versionId;
	}
	public void setVersionId(int versionId) {
		this.versionId = versionId;
	}
}
