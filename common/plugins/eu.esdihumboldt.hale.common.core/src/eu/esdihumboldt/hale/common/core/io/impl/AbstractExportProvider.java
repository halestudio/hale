/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
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
import eu.esdihumboldt.hale.common.core.io.Value;
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
	public void storeConfiguration(Map<String, Value> configuration) {
		// store target if possible
		if (target != null) {
			URI location = target.getLocation();
			if (location != null) {
				configuration.put(PARAM_TARGET, Value.of(location.toString()));
			}
		}

		super.storeConfiguration(configuration);
	}

	@Override
	public void setParameter(String name, Value value) {
		if (name.equals(PARAM_TARGET)) {
			try {
				File file = new File(URI.create(value.as(String.class)));
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
		return new DefaultIOReporter(getTarget(), MessageFormat.format("{0} export", getTypeName()),
				getActionId(), true);
	}

}
