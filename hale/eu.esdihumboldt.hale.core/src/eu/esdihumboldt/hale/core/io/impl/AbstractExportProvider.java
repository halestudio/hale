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

package eu.esdihumboldt.hale.core.io.impl;

import java.io.OutputStream;

import eu.esdihumboldt.hale.core.io.ExportProvider;
import eu.esdihumboldt.hale.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.core.io.supplier.LocatableOutputSupplier;

/**
 * Abstract {@link ExportProvider} implementation
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.2 
 */
public abstract class AbstractExportProvider extends AbstractIOProvider implements
		ExportProvider {
	
	private LocatableOutputSupplier<? extends OutputStream> target;

	/**
	 * @see ExportProvider#setTarget(LocatableOutputSupplier)
	 */
	@Override
	public void setTarget(LocatableOutputSupplier<? extends OutputStream> target) {
		this.target = target;
	}

	/**
	 * Get the export target
	 * 
	 * @return the target output supplier
	 */
	protected LocatableOutputSupplier<? extends OutputStream> getTarget() {
		return target;
	}

	/**
	 * @see AbstractIOProvider#validate()
	 */
	@Override
	public void validate() throws IOProviderConfigurationException {
		super.validate();
		
		if (target == null) {
			fail("No target specified");
		}
	}

}
