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
package eu.esdihumboldt.hale.rcp.wizards.io.mappingexport;

import eu.esdihumboldt.goml.align.Alignment;
import eu.esdihumboldt.goml.oml.io.OmlRdfGenerator;
import eu.esdihumboldt.hale.models.project.HaleOmlRdfGenerator;

/**
 * {@link MappingExportProvider} implementation for exporting the Alignment to OML.
 * 
 * @author Thorsten Reitz
 */
public class OmlMappingExportProvider implements MappingExportProvider {

	/* (non-Javadoc)
	 * @see eu.esdihumboldt.hale.rcp.wizards.io.mappingexport.MappingExportFactory#export(eu.esdihumboldt.goml.align.Alignment, java.lang.String)
	 */
	public void export(Alignment al, String path) throws MappingExportException {
		OmlRdfGenerator orgen = new HaleOmlRdfGenerator();
		
		try {
			orgen.write(al, path);
		} catch (Exception e) {
			throw new MappingExportException("During the export of the " +
					"Alignment to the OML format, an error occured.", e);
		}
	}

}
