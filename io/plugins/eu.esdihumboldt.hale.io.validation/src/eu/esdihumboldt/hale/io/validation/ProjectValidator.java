/*
 * Copyright (c) 2017 wetransform GmbH
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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor;
import eu.esdihumboldt.hale.common.core.io.impl.SubtaskProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.instance.io.impl.AbstractInstanceValidator;
import eu.esdihumboldt.hale.io.validation.service.ValidatorConfigurationService;

/**
 * Validator that executes other validators according to the validator
 * configurations stored in the {@link ValidatorConfigurationService}.
 * 
 * @author Florian Esser
 */
public class ProjectValidator extends AbstractInstanceValidator {

	/**
	 * I/O provider ID
	 */
	public static final String PROVIDER_ID = "eu.esdihumboldt.hale.io.validation.projectvalidator";

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.IOProvider#isCancelable()
	 */
	@Override
	public boolean isCancelable() {
		return false;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.io.impl.AbstractInstanceValidator#getReportLabel()
	 */
	@Override
	protected String getReportLabel() {
		return "Project validator";
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.io.impl.AbstractInstanceValidator#getDefaultFailSummary()
	 */
	@Override
	protected String getDefaultFailSummary() {
		return "Validating the XML file against the configured validators failed";
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.io.impl.AbstractInstanceValidator#getDefaultSuccessSummary()
	 */
	@Override
	protected String getDefaultSuccessSummary() {
		return "All configured validations were successful.";
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider#execute(eu.esdihumboldt.hale.common.core.io.ProgressIndicator,
	 *      eu.esdihumboldt.hale.common.core.io.report.IOReporter)
	 */
	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {

		ValidatorConfigurationService service = getServiceProvider()
				.getService(ValidatorConfigurationService.class);
		if (service == null) {
			reporter.setSuccess(false);
			throw new RuntimeException("Unable to find validator configurations");
		}

		Collection<IOProviderDescriptor> validators = new ArrayList<>();
		validators.addAll(HaleIO.getProviderFactories(ConfigurableInstanceValidator.class));

		List<ValidatorConfiguration> configurations = service.getConfigurations();
		progress.begin("Performing project validation", configurations.size());
		reporter.setSuccess(true);

		SubtaskProgressIndicator subProgress = new SubtaskProgressIndicator(progress);
		int i = 0;
		for (ValidatorConfiguration configuration : configurations) {
			for (IOProviderDescriptor validatorFactory : HaleIO.filterFactoriesByConfigurationType(
					validators, configuration.getContentType())) {
				try {
					// Assert that the validator can validate the exported
					// content type, skip otherwise
					boolean compatible = validatorFactory.getSupportedTypes().stream()
							.anyMatch(type -> getContentType().isKindOf(type));
					if (!compatible) {
						reporter.info(new IOMessageImpl(MessageFormat.format(
								"Validator \"{0}\" skipped: cannot validate exported content type \"{1}\"",
								validatorFactory.getIdentifier(), getContentType().getId()), null));
						continue;
					}

					ConfigurableInstanceValidator validator = (ConfigurableInstanceValidator) validatorFactory
							.createExtensionObject();

					subProgress.begin(MessageFormat.format("Executing project validator ({0}/{1})",
							++i, configurations.size()), ProgressIndicator.UNKNOWN);

					validator.setSchemas(getSchemas());
					validator.setSource(getSource());
					validator.setContentType(getContentType());
					validator.setServiceProvider(getServiceProvider());
					validator.configure(configuration);

					validator.validate();
					IOReport result = validator.execute(null);
					if (result != null) {
						reporter.importMessages(result);
						if (!result.isSuccess()) {
							reporter.setSuccess(false);
						}
					}
				} catch (Exception e) {
					reporter.error(new IOMessageImpl("Error running project validator", e));
					reporter.setSuccess(false);
				}

				subProgress.end();
				progress.advance(1);
			}
		}

		progress.end();
		return reporter;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider#getDefaultTypeName()
	 */
	@Override
	protected String getDefaultTypeName() {
		return null;
	}

	@Override
	protected String getReportType() {
		return PROVIDER_ID;
	}

}