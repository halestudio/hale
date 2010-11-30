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

import java.util.Collection;

import eu.esdihumboldt.goml.align.Alignment;
import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;

/**
 * Interface for a Factory used to export mappings to any format.
 * 
 * @author Thorsten Reitz
 * @author Simon Templer
 */
public interface MappingExportProvider {
	
	/**
	 * Export an {@link Alignment} to the location indicated by the path.
	 * @param al the {@link Alignment} object to export
	 * @param path the location to export to
	 * @param sourceSchema a {@link Collection} of {@link SchemaElement}s for the source schema
	 * @param targetSchema a {@link Collection} of {@link SchemaElement}s for the target schema
	 * @return a report of the export process, may be
	 *   <code>null</code> if all cells have been exported successfully
	 * @throws MappingExportException when export failed
	 */
	public MappingExportReport export(Alignment al, String path, 
			Collection<SchemaElement> sourceSchema, 
			Collection<SchemaElement> targetSchema) 
		throws MappingExportException;

}
