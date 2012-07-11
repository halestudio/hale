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

package eu.esdihumboldt.hale.ui.io.instance;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.eclipse.ui.PlatformUI;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.Locatable;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.common.instance.io.InstanceValidator;
import eu.esdihumboldt.hale.common.instance.io.InstanceWriter;
import eu.esdihumboldt.hale.ui.io.ExportSelectTargetPage;
import eu.esdihumboldt.hale.ui.io.ExportWizard;
import eu.esdihumboldt.hale.ui.io.IOWizard;
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

	/**
	 * Default constructor
	 */
	public InstanceExportWizard() {
		super(InstanceWriter.class);
		
		setWindowTitle("Export instances");
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
			String fileName = getSelectTargetPage().getTargetFileName(); //XXX will only work for files!
			LocatableInputSupplier<? extends InputStream> source = new FileIOSupplier(new File(fileName));
			validator.setSource(source);
			
			//XXX configuration pages for validator?
			
			IOReporter defReport = validator.createReporter();
			
			// validate and execute provider
			try {
				IOReport report = validateAndExecute(validator, defReport);
				// add report to report server
				ReportService repService = (ReportService) PlatformUI.getWorkbench().getService(ReportService.class);
				repService.addReport(report);
				// show message to user
				if (report.isSuccess()) {
					// info message
					log.userInfo(report.getSummary());
				}
				else {
					// error message
					log.userError(report.getSummary());
				}
			} catch (IOProviderConfigurationException e) {
				log.userError("The validator could not be executed", e);
				return false;
			}
		}
		
		return success;
	}

	/**
	 * @see ExportWizard#createSelectTargetPage()
	 */
	@Override
	protected ExportSelectTargetPage<InstanceWriter, ? extends ExportWizard<InstanceWriter>> createSelectTargetPage() {
		return new InstanceSelectTargetPage();
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

}
