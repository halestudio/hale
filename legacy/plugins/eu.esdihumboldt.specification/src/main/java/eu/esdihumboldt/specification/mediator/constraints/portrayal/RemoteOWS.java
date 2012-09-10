/*
 * HUMBOLDT: A Framework for Data Harmonistation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this website:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to : http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.specification.mediator.constraints.portrayal;

/**
 * A RemoteOWS gives a referense to a remote WFS/WCS/other-OWS server.
 * 
 * 
 * 
 * @author Anna Pitaev, Logica CMG
 * @version $Id: RemoteOWS.java,v 1.2 2007-11-06 09:32:37 pitaeva Exp $
 * 
 */
public interface RemoteOWS {

	/**
	 * 
	 * @return a Service Component of RemoteOWS.
	 */
	public Service getService();

	/**
	 * 
	 * @return an OnlineResource of RemoteOWS.
	 */
	public OnlineResource getOnlineResource();

	/**
	 * 
	 * A Service refers to the type of a remote OWS server.
	 */
	public enum Service {
		WFS, WCS
	}

	/**
	 * 
	 * An OnlineResource is typically used to refer to an HTTP URL.
	 * 
	 */
	public enum OnlineResource {
		URL
	}

}
