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
package eu.esdihumboldt.cst.iobridge.impl;

import eu.esdihumboldt.cst.iobridge.CstServiceBridge;
import eu.esdihumboldt.hale.gmlparser.GmlHelper.ConfigurationType;

/**
 * This type implements the {@link CstServiceBridge} interface in such a way
 * that instead of passing a local filename to already loaded GML, a WFS 
 * GetFeature request string is provided. the CST can use this to dynamically 
 * load the data that it needs during transformation. This is especially helpful
 * when a large data set has to be processed.
 * 
 * @author Thorsten Reitz
 * @version $Id$
 */
public class DynamicWfsCstServiceBridge 
	implements CstServiceBridge {

	/* (non-Javadoc)
	 * @see eu.esdihumboldt.cst.iobridge.CstServiceBridge#transform(java.lang.String, java.lang.String, java.lang.String)
	 */
	public String transform(String schemaFilename, String omlFilename,
			String gmlFilename) {
		throw new UnsupportedOperationException("This operation is not yet " + //$NON-NLS-1$
			"implemented"); //$NON-NLS-1$
	}

	/**
	 * @see CstServiceBridge#transform(String, String, String, String, ConfigurationType)
	 */
	@Override
	public String transform(String schemaFilename, String omlFilename,
			String gmlFilename, String sourceSchema,
			ConfigurationType sourceVersion) {
		throw new UnsupportedOperationException("This operation is not yet " + //$NON-NLS-1$
			"implemented"); //$NON-NLS-1$
	}

}
