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

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor;
import eu.esdihumboldt.hale.common.instance.io.InstanceWriter;
import eu.esdihumboldt.hale.schemaprovider.Schema;
import eu.esdihumboldt.hale.ui.io.ExportSelectTargetPage;
import eu.esdihumboldt.hale.ui.io.ExportWizard;
import eu.esdihumboldt.hale.ui.io.IOWizard;

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
	 * @see ExportWizard#addPages()
	 */
	@Override
	public void addPages() {
		super.addPages();
		
		//TODO add configuration pages?!!
	}

	/**
	 * @see IOWizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		boolean success = super.performFinish();
		
//		if (success && validatorFactory != null) {
//			// validate the written output
//			
//			// create validator
//			InstanceValidator validator;
//			try {
//				validator = (InstanceValidator) validatorFactory.createExtensionObject();
//			} catch (Exception e) {
//				log.userError("The validator could not be instantiated", e);
//				return false;
//			}
//			
//			// configure validator
//			List<Schema> schemas = getProvider().getValidationSchemas();
//			validator.setSchemas(schemas.toArray(new Schema[schemas.size()]));
//			String fileName = getSelectTargetPage().getTargetFileName();
//			LocatableInputSupplier<? extends InputStream> source = new FileIOSupplier(new File(fileName));
//			validator.setSource(source);
//			
//			//XXX configuration pages for validator?
//			
//			IOReporter defReport = validator.createReporter();
//			
//			// validate and execute provider
//			try {
//				IOReport report = validateAndExecute(validator, defReport);
//				// add report to report server
//				ReportService repService = (ReportService) PlatformUI.getWorkbench().getService(ReportService.class);
//				repService.addReport(report);
//				// show message to user
//				if (report.isSuccess()) {
//					// info message
//					log.userInfo(report.getSummary());
//				}
//				else {
//					// error message
//					log.userError(report.getSummary());
//				}
//			} catch (IOProviderConfigurationException e) {
//				log.userError("The validator could not be executed", e);
//				return false;
//			}
//		}
		
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
	 * Get the target schema
	 * 
	 * @return the target schema
	 */
	public Schema getTargetSchema() {
		//FIXME update
//		SchemaService ss = (SchemaService) PlatformUI.getWorkbench().getService(SchemaService.class);
//		return ss.getTargetSchema();
		return null;
	}

	/**
	 * @see IOWizard#updateConfiguration(IOProvider)
	 */
	@Override
	protected void updateConfiguration(InstanceWriter provider) {
		super.updateConfiguration(provider);
		
		//FIXME update
//		// configure with instances, common SRS, target schema
//		InstanceService is = (InstanceService) PlatformUI.getWorkbench().getService(InstanceService.class);
//		
//		FeatureCollection<FeatureType, Feature> features = is.getFeatures(DataSet.TRANSFORMED);
//		Schema targetSchema = getTargetSchema();
//		
//		// determine SRS
//		String commonSRSName;
//		try {
//			commonSRSName = is.getCRS().getCRS().getIdentifiers().iterator().next().toString();
//		} catch (Exception e) {
//			// ignore
//			commonSRSName = null;
//		}
//		
//		provider.setInstances(features);
//		provider.setTargetSchema(targetSchema);
//		provider.setCommonSRSName(commonSRSName);
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
