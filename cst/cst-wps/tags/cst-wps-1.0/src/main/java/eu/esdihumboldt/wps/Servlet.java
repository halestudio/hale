/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.wps;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.esdihumboldt.cst.iobridge.IoBridgeFactory;
import eu.esdihumboldt.cst.iobridge.IoBridgeFactory.BridgeType;
import eu.esdihumboldt.cst.transformer.service.CstFunctionFactory;

/**
 * 
 * @author jezekjan
 *
 */
public class Servlet extends HttpServlet {
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter out = response.getWriter();

		CstFunctionFactory.getInstance().registerCstPackage(
				"eu.esdihumboldt.cst.corefunctions");

		
	}
}
