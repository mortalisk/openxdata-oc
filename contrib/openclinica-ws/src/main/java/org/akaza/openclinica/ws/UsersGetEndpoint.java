package org.akaza.openclinica.ws;

import java.util.List;
import java.util.Locale;

import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * Gets a list of users as registered in openclinica.
 * 
 * @author daniel
 *
 */
@Endpoint
public class UsersGetEndpoint {

	protected final Logger logger = LoggerFactory.getLogger(getClass().getName());
	private final String NAMESPACE_URI_V1 = "http://openclinica.org/ws/usersGet/v1";
	private final String SUCCESS_MESSAGE = "success";
	private final String FAIL_MESSAGE = "fail";

	private final DataSource dataSource;

	/**
	 * Constructor
	 * 
	 * @param cctsService
	 */
	public UsersGetEndpoint(DataSource dataSource) {
		this.dataSource = dataSource;
	}


	/**
	 * if NAMESPACE_URI_V1:getUsersRequest execute this method
	 * 
	 * @return
	 * @throws Exception
	 */
	@PayloadRoot(localPart = "getUsersRequest", namespace = NAMESPACE_URI_V1)
	public Source getUsers() throws Exception {  
		
		ResourceBundleProvider.updateLocale(new Locale("en_US"));
		
		if (true) {
			return new DOMSource(mapConfirmation(SUCCESS_MESSAGE, ""));
		} else {
			return new DOMSource(mapConfirmation(FAIL_MESSAGE, null));
		}
	}


	/**
	 * Create Response
	 * 
	 * @param confirmation
	 * @return
	 * @throws Exception
	 */
	private Element mapConfirmation(String confirmation, String theLabel) throws Exception {
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		Document document = docBuilder.newDocument();

		Element responseElement = document.createElementNS(NAMESPACE_URI_V1, "getUsersResponse");
		Element resultElement = document.createElementNS(NAMESPACE_URI_V1, "result");
		Element label = document.createElementNS(NAMESPACE_URI_V1, "label");
		resultElement.setTextContent(confirmation);
		label.setTextContent(theLabel);
		responseElement.appendChild(resultElement);
		responseElement.appendChild(label);


		Element usersElement = document.createElementNS(NAMESPACE_URI_V1, "users");
		responseElement.appendChild(usersElement);

		try{
			UserAccountDAO userDao = new UserAccountDAO(dataSource);
			List<UserAccountBean> users = (List<UserAccountBean>)userDao.findAll();
			for(int index = 0; index < users.size(); index++){
				UserAccountBean user = users.get(index);

				Element userElement = document.createElementNS(NAMESPACE_URI_V1, "user");

				Element element = document.createElementNS(NAMESPACE_URI_V1, "userId");
				element.setTextContent(user.getId()+"");
				userElement.appendChild(element);

				element = document.createElementNS(NAMESPACE_URI_V1, "name");
				element.setTextContent(user.getName());
				userElement.appendChild(element);

				element = document.createElementNS(NAMESPACE_URI_V1, "password");
				element.setTextContent(user.getPasswd());
				userElement.appendChild(element);

				usersElement.appendChild(userElement);
			}
		}
		catch(Exception ex){
			logger.error(ex.getMessage(), ex);
			ex.printStackTrace();
		}

		return responseElement;
	}

}
