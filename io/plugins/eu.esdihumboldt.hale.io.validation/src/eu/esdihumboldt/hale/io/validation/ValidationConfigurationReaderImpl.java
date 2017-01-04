/*
 * Copyright (c) 2016 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.io.validation;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;

import org.apache.commons.io.IOUtils;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractImportProvider;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;

/**
 * Simple validation configuration reader
 * 
 * @author Florian Esser
 */
public class ValidationConfigurationReaderImpl extends AbstractImportProvider
		implements ValidatorConfigurationReader {

	/**
	 * The provider ID.
	 */
	public static final String PROVIDER_ID = "eu.esdihumboldt.hale.io.validation.reader";

	private ValidatorConfiguration configuration;

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.IOProvider#isCancelable()
	 */
	@Override
	public boolean isCancelable() {
		return false;
	}

	/**
	 * @see eu.esdihumboldt.hale.io.validation.ValidatorConfigurationReader#getConfiguration()
	 */
	@Override
	public ValidatorConfiguration getConfiguration() {
		return configuration;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider#execute(eu.esdihumboldt.hale.common.core.io.ProgressIndicator,
	 *      eu.esdihumboldt.hale.common.core.io.report.IOReporter)
	 */
	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {

		progress.begin("Loading validator configuration.", ProgressIndicator.UNKNOWN);

		final URI sourceLocation = getSource().getLocation();
		if (sourceLocation == null) {
			throw new IOProviderConfigurationException(
					"No source location provided when trying to read validator configuration.");
		}
		final DefaultInputSupplier validationRuleInputSupplier = new DefaultInputSupplier(
				sourceLocation);
		final InputStream validationRuleInput = validationRuleInputSupplier.getInput();
		if (validationRuleInput == null) {
			throw new IOProviderConfigurationException("Cannot read validator configuration.");
		}
		try {
			// XXX UTF 8 encoding is assumed here. The actual encoding should be
			// detected or be configurable
			configuration = new ValidatorConfiguration(
					IOUtils.toString(validationRuleInput, StandardCharsets.UTF_8), sourceLocation);
			reporter.setSuccess(true);
		} catch (Exception e) {
			throw new IOProviderConfigurationException(
					MessageFormat.format("Could not read validation rule from '{0}': {1}",
							sourceLocation.toString(), e.getMessage()),
					e);
		} finally {
			IOUtils.closeQuietly(validationRuleInput);
		}

		progress.setCurrentTask("Validation rule loaded.");
		return reporter;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider#getDefaultTypeName()
	 */
	@Override
	protected String getDefaultTypeName() {
		return "Validator configuration";
	}
}
