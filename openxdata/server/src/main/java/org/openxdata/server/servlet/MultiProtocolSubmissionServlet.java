package org.openxdata.server.servlet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.MessageFormat;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.openxdata.proto.ProtocolHandler;
import org.openxdata.proto.ProtocolLoader;
import org.openxdata.proto.SubmissionContext;
import org.openxdata.proto.exception.ProtocolException;
import org.openxdata.proto.exception.ProtocolNotFoundException;
import org.openxdata.server.OpenXDataConstants;
import org.openxdata.server.service.AuthenticationService;
import org.openxdata.server.service.FormDownloadService;
import org.openxdata.server.service.StudyManagerService;
import org.openxdata.server.service.UserService;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.jcraft.jzlib.JZlib;
import com.jcraft.jzlib.ZOutputStream;

/**
 * An example of how to support multiple upload protocols based on similar (or
 * the same) class.
 * 
 * @author batkinson
 * 
 */
public class MultiProtocolSubmissionServlet extends HttpServlet {

	private static final long serialVersionUID = -8555135998272736140L;
	private static final Logger log = Logger
			.getLogger(MultiProtocolSubmissionServlet.class);

	private byte ACTION_NONE = -1;
	public static final byte RESPONSE_STATUS_ERROR = 0;
	public static final byte RESPONSE_ACCESS_DENIED = 2;

	private UserService userService;
	private FormDownloadService formDownloadService;
	private AuthenticationService authenticationService;
	private StudyManagerService studyManagerService;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		ServletContext sctx = this.getServletContext();
		WebApplicationContext ctx = WebApplicationContextUtils
				.getRequiredWebApplicationContext(sctx);

		// Manual Injection
		userService = (UserService) ctx.getBean("userService");
		formDownloadService = (FormDownloadService) ctx
				.getBean("formDownloadService");
		authenticationService = (AuthenticationService) ctx
				.getBean("authenticationService");
		studyManagerService = (StudyManagerService) ctx
				.getBean("studyManagerService");

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		log.info("incoming request");

		InputStream in = req.getInputStream();
		OutputStream out = resp.getOutputStream();

		ZOutputStream zOut = null;

		DataInputStream dataIn = null;
		DataOutputStream dataOut = null;

		resp.setContentType("application/octet-stream");

		zOut = new ZOutputStream(out, JZlib.Z_BEST_COMPRESSION);
		dataIn = new DataInputStream(in);
		dataOut = new DataOutputStream(zOut);

		ClassLoader origCl = Thread.currentThread().getContextClassLoader();

		try {
			Thread.currentThread().setContextClassLoader(
					MultiProtocolSubmissionServlet.class.getClassLoader());

			log.debug("reading request details");
			String username = dataIn.readUTF();
			String password = dataIn.readUTF();
			String serializer = dataIn.readUTF();
			String locale = dataIn.readUTF();
			String action = req
					.getParameter(OpenXDataConstants.REQUEST_PARAMETER_ACTION);

			log.debug("authenticating user");
			if (authenticationService.authenticate(username, password) == null) {
				dataOut.writeByte(RESPONSE_ACCESS_DENIED);
				resp.setStatus(HttpServletResponse.SC_OK);
				return;
			}

			log.debug("initializing protocol loader");
			String protoJarPath = MessageFormat.format(
					"/WEB-INF/protocol-jars/{0}.jar",
					new Object[] { serializer });
			URL protoLocation = getServletContext().getResource(protoJarPath);
			if (protoLocation == null) {
				throw new ProtocolNotFoundException("Could not load protocol jar '"+protoJarPath+"'");
			}
			if (log.isDebugEnabled())
				log.debug("loading protocol plugins from " + protoLocation);
			ProtocolLoader protoLoader = new ProtocolLoader();

			log.debug("loading protocol handler");
			ProtocolHandler handler = protoLoader.loadHandler(protoLocation);

			log.debug("creating submission context");
			SubmissionContext submitCtx = new DefaultSubmissionContext(dataIn,
					dataOut, action == null ? ACTION_NONE : Byte
							.parseByte(action), locale, userService,
					formDownloadService, studyManagerService);

			log.debug("handling request");
			handler.handleRequest(submitCtx);
			resp.setStatus(HttpServletResponse.SC_OK);
		} catch (ProtocolException e) {
			log.error("protocol error while handling request from client", e);
			dataOut.writeByte(RESPONSE_STATUS_ERROR);
			resp.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			log.error("error while handing request to protocol provider",e);
			dataOut.writeByte(RESPONSE_STATUS_ERROR);
			resp.setStatus(HttpServletResponse.SC_OK);
		} finally {
			if (dataOut != null)
				dataOut.flush();
			if (zOut != null)
				zOut.finish();
			resp.flushBuffer();
			Thread.currentThread().setContextClassLoader(origCl);
		}
	}
}
