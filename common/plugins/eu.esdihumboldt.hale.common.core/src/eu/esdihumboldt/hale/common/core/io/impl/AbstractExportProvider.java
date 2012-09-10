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

import java.io.File;
import java.io.OutputStream;
import java.net.URI;
import java.text.MessageFormat;
import java.util.Map;

import eu.esdihumboldt.hale.common.core.io.ExportProvider;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.DefaultIOReporter;
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableOutputSupplier;

/**
 * Abstract {@link ExportProvider} implementation
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.2
 */
public abstract class AbstractExportProvider extends AbstractIOProvider implements ExportProvider {

	private LocatableOutputSupplier<? extends OutputStream> target;

	/**
	 * @see ExportProvider#setTarget(LocatableOutputSupplier)
	 */
	@Override
	public void setTarget(LocatableOutputSupplier<? extends OutputStream> target) {
		this.target = target;
	}

	/**
	 * @see ExportProvider#getTarget()
	 */
	@Override
	public LocatableOutputSupplier<? extends OutputStream> getTarget() {
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

	/**
	 * @see AbstractIOProvider#storeConfiguration(Map)
	 */
	@Override
	public void storeConfiguration(Map<String, String> configuration) {
		// store target if possible
		if (target != null) {
			URI location = target.getLocation();
			if (location != null) {
				configuration.put(PARAM_TARGET, location.toString());
			}
		}

		super.storeConfiguration(configuration);
	}

	/**
	 * @see AbstractIOProvider#setParameter(String, String)
	 */
	@Override
	public void setParameter(String name, String value) {
		if (name.equals(PARAM_TARGET)) {
			try {
				File file = new File(URI.create(value));
				setTarget(new FileIOSupplier(file));
			} catch (IllegalArgumentException e) {
				// ignore, can't set target
				// XXX extend with support for other URIs?
			}
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
		return new DefaultIOReporter(getTarget(),
				MessageFormat.format("{0} export", getTypeName()), true);
	}

}
