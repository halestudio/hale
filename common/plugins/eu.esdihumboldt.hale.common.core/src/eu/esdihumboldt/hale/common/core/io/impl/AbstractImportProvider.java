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

package eu.esdihumboldt.hale.common.core.io.impl;

import java.io.InputStream;
import java.net.URI;
import java.text.MessageFormat;
import java.util.Map;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.DefaultIOReporter;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;

/**
 * Abstract {@link ImportProvider} implementation
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.2
 */
public abstract class AbstractImportProvider extends AbstractIOProvider implements ImportProvider {

	/**
	 * The source
	 */
	private LocatableInputSupplier<? extends InputStream> source;

	/**
	 * Default constructor
	 */
	public AbstractImportProvider() {
		super();

		addSupportedParameter(PARAM_SOURCE);
	}

	/**
	 * @see ImportProvider#setSource(LocatableInputSupplier)
	 */
	@Override
	public void setSource(LocatableInputSupplier<? extends InputStream> source) {
		this.source = source;
	}

	/**
	 * @see ImportProvider#getSource()
	 */
	@Override
	public LocatableInputSupplier<? extends InputStream> getSource() {
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

	/**
	 * @see AbstractIOProvider#storeConfiguration(Map)
	 */
	@Override
	public void storeConfiguration(Map<String, String> configuration) {
		// store source if possible
		if (source != null) {
			URI location = source.getLocation();
			if (location != null) {
				configuration.put(PARAM_SOURCE, location.toString());
			}
		}

		super.storeConfiguration(configuration);
	}

	/**
	 * @see AbstractIOProvider#setParameter(String, String)
	 */
	@Override
	public void setParameter(String name, String value) {
		if (name.equals(PARAM_SOURCE)) {
			setSource(new DefaultInputSupplier(URI.create(value)));
		}
		else {
			super.setParameter(name, value);
		}
	}

	/**
	 * @see IOProvider#createReporter()
	 */
	@Override
	public IOReporter createReporter() {
		return new DefaultIOReporter(getSource(),
				MessageFormat.format("{0} import", getTypeName()), true);
	}

}
