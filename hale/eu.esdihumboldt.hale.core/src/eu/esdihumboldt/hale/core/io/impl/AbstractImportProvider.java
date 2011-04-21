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

import java.io.InputStream;

import com.google.common.io.InputSupplier;

import eu.esdihumboldt.hale.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.core.io.ImportProvider;

/**
 * Abstract {@link ImportProvider} implementation
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.2 
 */
public abstract class AbstractImportProvider extends AbstractIOProvider implements
		ImportProvider {
	
	private InputSupplier<? extends InputStream> source;

	/**
	 * @see ImportProvider#setSource(InputSupplier)
	 */
	@Override
	public void setSource(InputSupplier<? extends InputStream> source) {
		this.source = source;
	}

	/**
	 * Get the import source
	 * 
	 * @return the source input supplier
	 */
	protected InputSupplier<? extends InputStream> getSource() {
		return source;
	}

	/**
	 * @see AbstractIOProvider#validate()
	 */
	@Override
	public void validate() throws IOProviderConfigurationException {
		super.validate();
		
		if (source == null) {
			fail("No source specified");
		}
	}

}
