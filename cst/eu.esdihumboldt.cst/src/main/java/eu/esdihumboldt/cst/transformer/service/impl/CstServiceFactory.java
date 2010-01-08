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
 *
 * Componet     : CST
 * 	 
 * Classname    : eu.esdihumboldt.cst.transformer/CstServiceFactory.java 
 * 
 * Author       :  Bernd Schneiders, Logica
 * 
 * Created on   : Aug 26, 2009 -- 11:50:35 AM
 *
 */
package eu.esdihumboldt.cst.transformer.service.impl;

import eu.esdihumboldt.cst.transformer.CstService;

/**
 * Factory to get an instance of an {@link CstService}.
 * 
 * @author Thorsten Reitz
 */
public class CstServiceFactory {

	/** Reference to a transformation service */
	private static CstServiceImpl service = null;
	
	/**
	 * Returns an instance of an {@link CstService}.
	 * @return CstService
	 */
	public static CstServiceImpl getInstance() {
		if (service == null) {
			service = new CstServiceImpl();
		}
		return service;
	}
}
