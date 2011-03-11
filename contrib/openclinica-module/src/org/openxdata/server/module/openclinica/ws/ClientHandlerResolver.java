package org.openxdata.server.module.openclinica.ws;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;


public class ClientHandlerResolver implements HandlerResolver {  
	public List<Handler> getHandlerChain(PortInfo port_info) {  
		List<Handler> hchain = new ArrayList<Handler>();  
		hchain.add(new HeaderHandler());  
		return hchain;  
	}  
}  
