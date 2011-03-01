/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.rcp.wizards.io.gml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PlatformUI;
import org.geotools.feature.FeatureCollection;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import de.cs3d.util.logging.ATransaction;
import eu.esdihumboldt.hale.gmlvalidate.Report;
import eu.esdihumboldt.hale.gmlvalidate.Validator;
import eu.esdihumboldt.hale.gmlvalidate.ValidatorFactory;
import eu.esdihumboldt.hale.gmlwriter.GmlWriter;
import eu.esdihumboldt.hale.models.HaleServiceListener;
import eu.esdihumboldt.hale.models.UpdateMessage;
import eu.esdihumboldt.hale.rcp.views.report.ReportModel;
import eu.esdihumboldt.hale.rcp.views.report.service.ReportService;
import eu.esdihumboldt.hale.rcp.views.report.service.ReportServiceImpl;
import eu.esdihumboldt.hale.schemaprovider.Schema;

/**
 * Wizard for exporting transformed data to a GML file
 * 
 * @author Simon Templer  
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class GmlExportWizard extends Wizard implements IExportWizard {
	
	private static final ALogger log = ALoggerFactory.getLogger(GmlExportWizard.class);
	
	private GmlExportPage exportPage;

	private final String commonSrsName;

	private final Schema schema;

	private final FeatureCollection<FeatureType, Feature> features;

	/**
	 * Constructor
	 * 
	 * @param features the features to export
	 * @param schema the corresponding schema
	 * @param commonSrsName the name of the common SRS, may be <code>null</code> 
	 */
	public GmlExportWizard(FeatureCollection<FeatureType, Feature> features,
			Schema schema, String commonSrsName) {
		super();
		
		setWindowTitle("Save transformation result");
		setNeedsProgressMonitor(true);
		
		this.features = features;
		this.schema = schema;
		this.commonSrsName = commonSrsName;
	}

	/**
	 * @see Wizard#addPages()
	 */
	@Override
	public void addPages() {
		super.addPages();
		
		addPage(exportPage = new GmlExportPage("GML export", "Save transformation result"));
	}

	/**
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// do nothing
	}

	/**
	 * @see Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		final File targetFile = exportPage.getTargetFile();
		final boolean validate = exportPage.getValidate();
		
		try {
			getContainer().run(true, false, new IRunnableWithProgress() {
				
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException,
						InterruptedException {
					monitor.beginTask("Exporting transformed features to GML file", IProgressMonitor.UNKNOWN);
					GmlWriter gmlWriter = (GmlWriter) PlatformUI.getWorkbench().getService(GmlWriter.class);
					OutputStream out;
					
					try {
						out = new FileOutputStream(targetFile);
					} catch (FileNotFoundException e1) {
						log.userError("Could not write output file: " + 
								targetFile.getAbsolutePath(), e1);
						return;
					}
					ATransaction trans = log.begin("Writing transformed features to GML file: " + 
							targetFile.getAbsolutePath());
					List<Schema> addSchemas = null;
					try {
						addSchemas = gmlWriter.writeFeatures(features, schema, out, commonSrsName);
					} catch (Exception e) {
						log.userError("Error saving transformation result to GML file", e);
						return;
					} finally {
						trans.end();
						try {
							out.close();
						} catch (IOException e) {
							// ignore
						}
					}
					
					if (validate) {
						monitor.setTaskName("Validating output file");
						
						// validate output file
						Validator validator;
						if (addSchemas == null || addSchemas.isEmpty()) {
							validator = ValidatorFactory.getInstance().createValidator(schema);
						}
						else {
							Schema[] schemas = new Schema[addSchemas.size() + 1];
							schemas[0] = schema;
							for (int i = 1; i <= addSchemas.size(); i++) {
								schemas[i] = addSchemas.get(i - 1);
							}
							validator = ValidatorFactory.getInstance().createValidator(schemas);
						}
						InputStream xml;
						try {
							xml = new FileInputStream(targetFile);
						} catch (FileNotFoundException e) {
							log.error("File not found for validation: " + targetFile.getAbsolutePath());
							return;
						}
						trans = log.begin("Validating GML file: " + targetFile.getAbsolutePath());
						Report report;
						try {
							report = validator.validate(xml);
						} finally {
							trans.end();
							try {
								xml.close();
							} catch (IOException e) {
								// ignore
							}
						}
						
						if (report.isValid()) {
							log.userInfo("GML file exported, validation successful.");
						}
						else {
							log.userError("Validation of the exported GML file failed, see the error log for more details.");
							//TODO show some report window (with line numbers etc)

							final Report report2 = report;
							Runnable r = new Runnable(){
								public void run(){
									ReportServiceImpl reportService = (ReportServiceImpl)PlatformUI.getWorkbench().getService(ReportService.class);
									reportService.addReport(report2);
								}
							};

							getContainer().getShell().getDisplay().asyncExec(r);
						}
					}
					
					monitor.done();
				}
			});
		} catch (Exception e) {
			log.userError("Error exporting transformed feature to GML file", e);
			return false;
		}
		
		return true;
	}

}
