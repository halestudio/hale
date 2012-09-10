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

package eu.esdihumboldt.hale.common.core.io;

import java.io.InputStream;

import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;

/**
 * Base interface for import providers
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.2
 */
public interface ImportProvider extends IOProvider {

	/**
	 * The configuration parameter name for the source URI
	 */
	public static final String PARAM_SOURCE = "source";

	/**
	 * Set the import source
	 * 
	 * @param source the source input supplier
	 */
	public void setSource(LocatableInputSupplier<? extends InputStream> source);

	/**
	 * Get the import source
	 * 
	 * @return the source input supplier
	 */
	public LocatableInputSupplier<? extends InputStream> getSource();

}
