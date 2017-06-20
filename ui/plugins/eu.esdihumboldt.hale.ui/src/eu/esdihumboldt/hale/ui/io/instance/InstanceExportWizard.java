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

package eu.esdihumboldt.hale.ui.io.instance;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.PlatformUI;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.IOAdvisor;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.Locatable;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableOutputSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.MultiLocationOutputSupplier;
import eu.esdihumboldt.hale.common.instance.io.InstanceValidator;
import eu.esdihumboldt.hale.common.instance.io.InstanceWriter;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.io.ExportTarget;
import eu.esdihumboldt.hale.ui.io.ExportWizard;
import eu.esdihumboldt.hale.ui.io.IOWizard;
import eu.esdihumboldt.hale.ui.io.target.FileTarget;
import eu.esdihumboldt.hale.ui.service.report.ReportService;

/**
 * Wizard for exporting instances
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.5
 */
public class InstanceExportWizard extends ExportWizard<InstanceWriter> {

	private static final ALogger log = ALoggerFactory.getLogger(InstanceExportWizard.class);

	private final List<InstanceValidator> validators = new ArrayList<>();

	private List<IOProviderDescriptor> cachedFactories;

	/**
	 * Default constructor
	 */
	public InstanceExportWizard() {
		super(InstanceWriter.class);

		setWindowTitle("Export instances");
	}

	/**
	 * @see IOWizard#getFactories()
	 */
	@Override
	public List<IOProviderDescriptor> getFactories() {
		if (cachedFactories != null) {
			return cachedFactories;
		}

		List<IOProviderDescriptor> providers = super.getFactories();
		IOAdvisor<InstanceWriter> advisor = getAdvisor();
		if (advisor == null) {
			return providers;
		}

		List<IOProviderDescriptor> result = new ArrayList<IOProviderDescriptor>();

		for (IOProviderDescriptor providerFactory : providers) {
			// create a dummy provider
			InstanceWriter provider;
			try {
				provider = (InstanceWriter) providerFactory.createExtensionObject();
			} catch (Exception e) {
				log.error("Error creating an instance writer: " + providerFactory.getIdentifier(),
						e);
				continue; // ignore this provider as it cannot be created
			}

			// assign the basic configuration
			advisor.prepareProvider(provider);
			advisor.updateConfiguration(provider);

			// and check the compatibility
			try {
				provider.checkCompatibility();
				result.add(providerFactory);
			} catch (IOProviderConfigurationException e) {
				// ignore this export provider, it is not compatible
			}
		}

		cachedFactories = result;
		return cachedFactories;
	}

	/**
	 * Run the configured validators on the exported instance. May be overriden
	 * to customize validation process.
	 * 
	 * @return true if all validations were successful
	 */
	protected boolean performValidation() {
		boolean success = true;
		for (InstanceValidator validator : validators) {
			// set schemas
			List<? extends Locatable> schemas = getProvider().getValidationSchemas();
			validator.setSchemas(schemas.toArray(new Locatable[schemas.size()]));

			// set service provider
			validator.setServiceProvider(HaleUI.getServiceProvider());

			ExportTarget<?> exportTarget = getSelectTargetPage().getExportTarget();
			if (exportTarget instanceof FileTarget) {
				LocatableOutputSupplier<? extends OutputStream> target = getProvider().getTarget();
				List<String> fileNames = new ArrayList<>();
				if (target instanceof MultiLocationOutputSupplier) {
					for (URI location : ((MultiLocationOutputSupplier) target).getLocations()) {
						if (!"file".equals(location.getScheme())) {
							continue;
						}

						File targetFile = new File(location);
						fileNames.add(targetFile.getAbsolutePath());
					}
				}
				else {
					fileNames.add(((FileTarget<?>) exportTarget).getTargetFileName());
				}

				for (String fileName : fileNames) {
					LocatableInputSupplier<? extends InputStream> source = new FileIOSupplier(
							new File(fileName));
					validator.setSource(source);
					validator.setContentType(getContentType());

					IOReporter defReport = validator.createReporter();

					// validate and execute provider
					try {
						// validate configuration
						validator.validate();
						IOReport report = execute(validator, defReport);

						if (report != null) {
							// add report to report server
							ReportService repService = PlatformUI.getWorkbench()
									.getService(ReportService.class);
							repService.addReport(report);
							if (report.isSuccess()) {
								log.info(report.getSummary());
							}
							else {
								log.error(report.getSummary());
								success = false;
							}
						}
					} catch (IOProviderConfigurationException e) {
						log.error(MessageFormat.format("The validator '{0}' could not be executed",
								validator.getClass().getCanonicalName()), e);
						success = false;
					}
				}
			}
			else {
				log.error("No input can be provided for validation (no file target)");
				success = false;
			}
		}

		if (success) {
			log.userInfo("All validations completed successfully.");
		}
		else {
			log.userError(
					"There were validation failures. Please check the report for more details.");
		}

		return success;
	}

	/**
	 * @see IOWizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		boolean success = super.performFinish();

		if (success && !validators.isEmpty()) {
			// validate the written output
			success = performValidation();
		}

		return success;
	}

	/**
	 * @return the list of {@link InstanceValidator}s
	 */
	public List<InstanceValidator> getValidators() {
		return validators;
	}

	/**
	 * @param validator the {@link InstanceValidator} to add
	 */
	public void addValidator(InstanceValidator validator) {
		this.validators.add(validator);
	}

	/**
	 * Removes a validator
	 * 
	 * @param validator {@link InstanceValidator} to remove
	 */
	public void removeValidator(InstanceValidator validator) {
		this.validators.remove(validator);
	}

//	/**
//	 * Get all export configuration saved in the current project
//	 * 
//	 * @return the exportConfigs
//	 */
//	public List<IOConfiguration> getExportConfigurations() {
//		ProjectService ps = (ProjectService) PlatformUI.getWorkbench().getService(
//				ProjectService.class);
//		Project p = (Project) ps.getProjectInfo();
//		return p.getExportConfigurations();
//	}

}
