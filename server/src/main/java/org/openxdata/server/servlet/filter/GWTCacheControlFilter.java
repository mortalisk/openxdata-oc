package org.openxdata.server.servlet.filter;

import java.io.IOException;
import java.util.Date;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * {@link Filter} to add "no cache" cache control headers for the GWT 
 * "nocache" javascript file. 
 * 
 * This means that the nocache file will not have caching issues when 
 * upgrading the GWT web application (which requires users to clear
 * cache and history).
 */
public class GWTCacheControlFilter implements Filter {
	
	private static final long ONE_DAY_IN_MILISECONDS = (1000L * 60L * 60L * 24L);

	public void destroy() {
	}

	public void init(FilterConfig config) throws ServletException {
	}

	public void doFilter(ServletRequest request, 
			ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String requestURI = httpRequest.getRequestURI();

		if (requestURI.contains(".nocache.")) {
			Date now = new Date();
			HttpServletResponse httpResponse = (HttpServletResponse) response;
			httpResponse.setDateHeader("Date", now.getTime());
			httpResponse.setDateHeader("Expires", now.getTime() - ONE_DAY_IN_MILISECONDS); // HTTP 1.0
			httpResponse.setHeader("Pragma", "no-cache"); // HTTP 1.0
			httpResponse.setHeader("Cache-control", "no-cache, no-store, must-revalidate, private, max-stale=0, post-check=0, pre-check=0, max-age=0"); // HTTP 1.1
		}

		filterChain.doFilter(request, response);
	}
}