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
import eu.esdihumboldt.hale.common.instance.io.InstanceValidator;
import eu.esdihumboldt.hale.common.instance.io.InstanceWriter;
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

	private IOProviderDescriptor validatorFactory;

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
	 * @see IOWizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		boolean success = super.performFinish();

		if (success && validatorFactory != null) {
			// validate the written output

			// create validator
			InstanceValidator validator;
			try {
				validator = (InstanceValidator) validatorFactory.createExtensionObject();
			} catch (Exception e) {
				log.userError("The validator could not be instantiated", e);
				return false;
			}

			// configure validator
			List<? extends Locatable> schemas = getProvider().getValidationSchemas();
			validator.setSchemas(schemas.toArray(new Locatable[schemas.size()]));
			ExportTarget<?> exportTarget = getSelectTargetPage().getExportTarget();
			if (exportTarget instanceof FileTarget) {
				String fileName = ((FileTarget<?>) exportTarget).getTargetFileName();
				LocatableInputSupplier<? extends InputStream> source = new FileIOSupplier(
						new File(fileName));
				validator.setSource(source);
				validator.setContentType(getContentType());

				// XXX configuration pages for validator?

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
						// show message to user
						if (report.isSuccess()) {
							// info message
							log.userInfo(report.getSummary());
						}
						else {
							// error message
							log.userError(report.getSummary()
									+ "\nPlease see the report for more details.");
						}
					}
				} catch (IOProviderConfigurationException e) {
					log.userError("The validator could not be executed", e);
					return false;
				}
			}
			else {
				log.error("No input can be provided for validation (no file target)");
				return false;
			}
		}

		return success;
	}

	/**
	 * @return the validatorFactory
	 */
	public IOProviderDescriptor getValidatorFactory() {
		return validatorFactory;
	}

	/**
	 * @param validatorFactory the validatorFactory to set
	 */
	public void setValidatorFactory(IOProviderDescriptor validatorFactory) {
		this.validatorFactory = validatorFactory;
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
