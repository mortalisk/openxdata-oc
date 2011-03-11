package org.openxdata.server.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author maimoona kausar
 *
 */
class ServletUtils {

    public static void setBadRequest(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("text/plain");
        response.getOutputStream().println(message);
    }
}
