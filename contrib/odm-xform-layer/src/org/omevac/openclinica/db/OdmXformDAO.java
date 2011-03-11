package org.omevac.openclinica.db;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import org.omevac.openclinica.CrfMetaData;
import org.omevac.openclinica.ItemMetaData;

/**
 * Provides database access services to the ODM to XForms converter.
 * 
 * @author daniel
 *
 */
public class OdmXformDAO {

	private static final String DRIVER_CLASS_NAME = "org.postgresql.Driver";
	private static final String KEY_CON_URL = "connection.url";
	private static final String DEFAULT_CON_URL = "jdbc:postgresql://localhost:5432/openclinica";
	private static final String PROPERTIES_FILE_NAME = "OPENCLINICA-SETTINGS.PROPERTIES";

	private String conUrl;
	private Connection con;

	
	public OdmXformDAO(){
		connect2Database();
	}
	
	private void connect2Database(){
		try{
			Class.forName(DRIVER_CLASS_NAME);
			Properties props=new Properties();
			String propertiesPath= getClass().getProtectionDomain().getCodeSource().getLocation().toString().substring(6)+"//..//"+PROPERTIES_FILE_NAME;			
			
//			If path contains spaces, java will replace them with
			//%20 hence creating a non existing path.
			propertiesPath.replace("%20", " ");
			
			props.clear();
			
			//This will work (File will be found) only if the path has no names with spaces.
			props.load(new FileInputStream(propertiesPath));
			conUrl = props.getProperty(KEY_CON_URL, DEFAULT_CON_URL);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets metadata(text,type, etc) of an openclinica form question item given its oc_oid
	 * 
	 * @param ocOid the oc_oid
	 * @return the metadata object
	 */
	public ItemMetaData getItemMetaData(String ocOid){
		ItemMetaData metaData = null;
		
		if(conUrl != null){
			try{
				String sql = "select left_item_text,item_data_type_id,options_text,repeat_number,repeat_max, " + 
							"options_values,response_type_id,regexp_error_msg,required, " +
							"s.ordinal as page_no,s.title as page_title,default_value from item a " +
							"inner join item_form_metadata b on a.item_id=b.item_id " +
							"inner join response_set c on c.response_set_id=b.response_set_id " +
							"inner join item_group_metadata d on d.item_id=a.item_id " + 
							"inner join section s on s.section_id=b.section_id " +
							"where oc_oid='" + ocOid + "' order by page_title";
				
				con = DriverManager.getConnection(conUrl,"clinica","clinica");
				PreparedStatement statement = con.prepareStatement(sql);		
				ResultSet res = statement.executeQuery();
				
				if(res != null && res.next()){
					metaData = new ItemMetaData();
					metaData.setItemDataTypeId(res.getInt("item_data_type_id"));
					metaData.setLeftItemText(res.getString("left_item_text"));
					metaData.setOptionsText(res.getString("options_text"));
					metaData.setOptionsValues(res.getString("options_values"));
					metaData.setResponseTypeId(res.getInt("response_type_id"));
					metaData.setRegExpErrorMsg(res.getString("regexp_error_msg"));
					metaData.setPageNo(res.getInt("page_no"));
					metaData.setPageTitle(res.getString("page_title"));
					metaData.setRequired(res.getBoolean("required"));
					metaData.setDefaultValue(res.getString("default_value"));
				}
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
			finally{
				try{
					if(con != null)
						con.close();
				    con = null;
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		
		return metaData;
	}
	
	/**
	 * Gets the meta data of a CRF given its OC ID.
	 * 
	 * @param ocOid the OC ID.
	 * @return the CRF name.
	 */
	public CrfMetaData getCrfMetaData(String ocOid){
		CrfMetaData crfMetaData = null;
		
		if(conUrl != null){
			try{
				String sql = "select a.name,b.crf_version_id from crf a inner join crf_version b on a.crf_id=b.crf_id " +
							 "where b.oc_oid='" + ocOid + "'";
				
				con = DriverManager.getConnection(conUrl,"clinica","clinica");
				PreparedStatement statement = con.prepareStatement(sql);		
				ResultSet res = statement.executeQuery();
				
				if(res != null && res.next())
					crfMetaData = new  CrfMetaData(res.getString("name"),res.getInt("crf_version_id"));
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
			finally{
				try{
					if(con != null)
						con.close();
				    con = null;
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		
		return crfMetaData;
	}
}
