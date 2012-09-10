/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.common.align.io;

import eu.esdihumboldt.hale.common.align.model.MutableAlignment;
import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;

/**
 * Provides support for reading an alignment
 * 
 * @author Simon Templer
 */
public interface AlignmentReader extends ImportProvider {

	/**
	 * Set the source schema
	 * 
	 * @param sourceSchema the source schema
	 */
	public void setSourceSchema(TypeIndex sourceSchema);

	/**
	 * Set the target schema
	 * 
	 * @param targetSchema the source schema
	 */
	public void setTargetSchema(TypeIndex targetSchema);

	/**
	 * Get the loaded alignment
	 * 
	 * @return the loaded alignment
	 */
	public MutableAlignment getAlignment();

}
