/*
 * HUMBOLDT: A Framework for Data Harmonization and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.models.project;

import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

import org.apache.log4j.Logger;

import eu.esdihumboldt.goml.oml.io.OmlRdfGenerator;
import eu.esdihumboldt.hale.prefixmapper.NamespacePrefixMapperImpl;

/**
 * @author Thorsten Reitz
 * @version $Id$
 */
public class HaleOmlRdfGenerator extends OmlRdfGenerator {
	
	private static Logger _log = Logger.getLogger(HaleOmlRdfGenerator.class);
	
	/**
	 * @param m the {@link Marshaller} to configure.
	 */
	public void configurePrefixMapper(Marshaller m) {
		try {
			m.setProperty("com.sun.xml.internal.bind.namespacePrefixMapper",
					new NamespacePrefixMapperImpl());
		} catch (PropertyException e) {
			_log.error("Setting the PrefixMapper failed: ", e);
		}
	}
	
}
