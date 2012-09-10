/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to this website:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to : http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.specification.mediator;

/**
 * Classes implementing this Interface are the ones actually handling the
 * request from the client. They represent the first step in processing such a
 * request in the Mediator and have the responsibility of transforming the
 * client- encoded request to the internal request format, thereby enriching it
 * with context information by means of the ContextService. Furthermore, these
 * implementations also handle the specifics of Client Response handling. <br/>
 * For many cases, classes implementing this interface will also extend
 * HttpServlet or similar base classes defined by the common APIs.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface InterfaceController {

	/**
	 * @param _hres
	 *            the created {@link MediatorResponse}.
	 * @return true if the {@link InterfaceController} implementation accepted
	 *         and distributed the {@link MediatorResponse}.
	 */
	public boolean notify(MediatorResponse _hres);

}
