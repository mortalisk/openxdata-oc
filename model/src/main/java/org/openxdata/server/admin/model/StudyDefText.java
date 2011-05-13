package org.openxdata.server.admin.model;

/**
 * This class contains text for a study in a given locale.
 */
public class StudyDefText extends AbstractEditable{

	/**
	 * Serialisation ID
	 */
	private static final long serialVersionUID = -4438293413015913822L;

	/** The database identifier for the study text. */
	private int studyTextId = 0;
	
	/** The database identifier for the study whose text we contain. */
	private int studyId;
	
	/** The locale key for this study text. */
	private String localeKey;
	
	/** The name of the study in the locale specified by the localKey field. */
	private String name;
	
	/** The description of the study in the locale specified in the localeKey field. */
	private String description;
	
	/**
	 * Constructs a new study text object.
	 */
	public StudyDefText(){
		
	}

	public int getStudyTextId() {
		return studyTextId;
	}

	@Override
	public int getId() {
		return studyTextId;
	}

	public void setStudyTextId(int studyTextId) {
		this.studyTextId = studyTextId;
	}

	public int getStudyId() {
		return studyId;
	}

	public void setStudyId(int studyId) {
		this.studyId = studyId;
	}

	public String getLocaleKey() {
		return localeKey;
	}

	public void setLocaleKey(String localeKey) {
		this.localeKey = localeKey;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public boolean isNew(){
		return studyTextId == 0;
	}
}
