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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.MessageFormat;
import java.util.Map;
import java.util.UUID;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
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
	 * The resource identifier
	 */
	private String resourceIdentifier;

	/**
	 * Default constructor
	 */
	public AbstractImportProvider() {
		super();

		addSupportedParameter(PARAM_SOURCE);
	}

	/**
	 * @see AbstractIOProvider#execute(ProgressIndicator)
	 */
	@Override
	public IOReport execute(ProgressIndicator progress)
			throws IOProviderConfigurationException, IOException {
		if (resourceIdentifier == null) {
			resourceIdentifier = generateResourceId();
		}
		return super.execute(progress);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider#execute(eu.esdihumboldt.hale.common.core.io.ProgressIndicator,
	 *      java.lang.String)
	 */
	@SuppressWarnings("javadoc")
	@Override
	public IOReport execute(ProgressIndicator progress, String resourceIdentifier)
			throws IOProviderConfigurationException, IOException, UnsupportedOperationException {

		if (resourceIdentifier == null) {
			this.resourceIdentifier = generateResourceId();
		}
		return super.execute(progress);
	}

	/**
	 * Generate the unique resource identifier.
	 * 
	 * @return the generated resource identifier
	 */
	protected String generateResourceId() {
		return UUID.randomUUID().toString();
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
	public void storeConfiguration(Map<String, Value> configuration) {
		// store source if possible
		if (source != null) {
			URI location = source.getUsedLocation();
			if (location != null) {
				configuration.put(PARAM_SOURCE, Value.of(location.toString()));
			}
		}

		// store resource identifier (if set)
		if (resourceIdentifier != null) {
			configuration.put(PARAM_RESOURCE_ID, Value.of(resourceIdentifier));
		}

		super.storeConfiguration(configuration);
	}

	@Override
	public void setParameter(String name, Value value) {
		if (name.equals(PARAM_SOURCE)) {
			setSource(new DefaultInputSupplier(URI.create(value.as(String.class))));
		}
		if (name.equals(PARAM_RESOURCE_ID)) {
			// set resource id
			this.resourceIdentifier = value.as(String.class);
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
		return new DefaultIOReporter(getSource(), MessageFormat.format("{0} import", getTypeName()),
				getActionId(), true);
	}

	/**
	 * @see ImportProvider#getResourceIdentifier()
	 */
	@Override
	public String getResourceIdentifier() {
		return resourceIdentifier;
	}

}
