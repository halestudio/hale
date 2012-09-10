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

import java.io.OutputStream;

import eu.esdihumboldt.hale.common.core.io.supplier.LocatableOutputSupplier;

/**
 * Base interface for export providers
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.2
 */
public interface ExportProvider extends IOProvider {

	/**
	 * The configuration parameter name for the target URI
	 */
	public static final String PARAM_TARGET = "target";

	/**
	 * Set the export target
	 * 
	 * @param target the target output supplier
	 */
	public void setTarget(LocatableOutputSupplier<? extends OutputStream> target);

	/**
	 * Get the export target
	 * 
	 * @return the target output supplier
	 */
	public LocatableOutputSupplier<? extends OutputStream> getTarget();

}
