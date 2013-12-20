package com.acs.hrs.catalina.valves;

import java.io.IOException;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class CacheHandlerValve extends ValveBase {
	private static Log log = LogFactory.getLog(CacheHandlerValve.class);
	private static final String PRAGMA = "Pragma";

	@Override
	public void invoke(Request request, Response response) throws IOException,
			ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		String url = "/oneportal-static/";
		String expiryTime = "30";

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, Integer.valueOf(expiryTime));

		if (httpRequest.getRequestURI().contains(url)) {
			long timeInMillis = calendar.getTimeInMillis();
			httpResponse.setDateHeader("Expires", timeInMillis);
			httpResponse.setHeader("Cache-Control", "public, max-age="
					+ timeInMillis);
		}

		/*
		 * By default, some servers (e.g. Tomcat) will set headers on any SSL
		 * content to deny caching. Setting the Pragma header to null or to an
		 * empty string takes care of user-agents implementing HTTP 1.0.
		 */
		if (httpResponse.containsHeader("Pragma")) {
			httpResponse.setHeader(PRAGMA, null);
		}

		try {
			getNext().invoke(request, response);
		} catch (Exception ex) {
			log.error("No more valves", ex);
		}
	}

}
