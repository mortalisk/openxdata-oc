package edu.wustl.mobilab.sliver.bpel;

import org.ksoap2.*;
import org.kxml2.kdom.*;

import edu.wustl.mobilab.sliver.soap.*;


/**
 * Represents <i>Faulted</t> {@link Signal}s.
 * This signal includes a payload that describes the fault that was thrown.
 * 
 * @author Greg Hackmann
 */
public class FaultedSignal extends Exception implements Signal
{
	/**
	 * The fault that was thrown.
	 */
	private transient final Fault fault;
	
	/**
	 * The fault's namespace.
	 */
	private final String namespace;
	/**
	 * The fault's type.
	 */
	private final String type;
	
	/**
	 * Creates a new FaultedSignal from a {@link Throwable} exception.
	 * 
	 * @param namespace the fault's namespace
	 * @param type the fault's type
	 * @param payload the {@link Throwable} that created the fault
	 */
	public FaultedSignal(String namespace, String type, Throwable payload)
	{
		super(payload.toString());
		this.namespace = namespace;
		this.type = type;
		
		fault = new Fault("Server", payload.toString());
		fault.addDetail(payload.getMessage());
		// Create a Server fault and add the payload's message as the detail
	}
	
	/**
	 * Creates a new FaultedSignal from a {@link SoapFault}.
	 * 
	 * @param cause the {@link SoapFault} that represents the fault
	 */
	public FaultedSignal(SoapFault cause)
	{
		fault = new Fault(cause.faultcode, cause.faultstring);
		fault.addFaultActor(cause.faultactor);
		// Copy the SOAP fault's code, string, and actor

		Element detail = (Element)cause.detail.getChild(0);
		this.namespace = detail.getNamespace();
		this.type = detail.getName();
		// Extract the namespace and type from the fault's detail
		
		fault.addDetail(detail.toString());
		// Copy the fault's detail
	}
	
	/**
	 * Gets the fault that was thrown.
	 * 
	 * @return the fault that was thrown
	 */
	Fault getFault()
	{
		return fault;
	}
	
	/**
	 * Gets the fault's namespace.
	 * 
	 * @return the fault's namespace
	 */
	String getNamespace()
	{
		return namespace;
	}
	
	/**
	 * Gets the fault's type.
	 * 
	 * @return the fault's type
	 */
	String getType()
	{
		return type;
	}
	
	public String toString()
	{
		return "Faulted";
	}
}
