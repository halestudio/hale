/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.server;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class CstWps extends HttpServlet{

	/**
	 * SerialVersion
	 */
	private static final long serialVersionUID = -8128494354035680094L;
	
	/**
	 * @see javax.servlet.http.HttpServlet#doGet
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		
	}

	/**
	 * 
	 * @return
	 */
	public String getCapabilities() {
		return null;
	}
	
	/**
	 * 
	 * @return
	 */
	public String describeProcess() {
		return null;
	}
}
