/*
 * Copyright (c) 2014 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.app.transform;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.eclipse.core.runtime.content.IContentType;

import com.google.common.util.concurrent.ListenableFuture;

import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.common.core.report.Report;
import eu.esdihumboldt.hale.common.core.report.ReportHandler;
import eu.esdihumboldt.hale.common.headless.impl.ProjectTransformationEnvironment;
import eu.esdihumboldt.hale.common.headless.report.ReportFile;
import eu.esdihumboldt.hale.common.headless.transform.Transformation;
import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.common.instance.io.InstanceWriter;

/**
 * Executes a transformation.
 * 
 * @author Simon Templer
 */
public class ExecTransformation implements ConsoleConstants {
	
	/**
	 * ID for the transformation
	 */
	private final String id = UUID.randomUUID().toString();
	
	private ReportHandler reportHandler = null;
	
	private ProjectTransformationEnvironment env;

	private ExecContext context;

	private InstanceWriter target;

	private InstanceReader source;
	
//	private InstanceValidator validator;
	
	public void run(ExecContext context) throws Exception {
		this.context = context;
		
		new ConsoleProgressManager();
		
		// set up report handler
		setupReportHandler();
		
		// set up transformation environment
		loadProject();
		
		// set up reader for source
		setupReader();
		
		// set up writer for target
		setupWriter();
		
		if (target == null) {
			// writer could not be created
			return;
		}
		
		// set up validator
//		setupValidator();
		
		// trigger transformation
		transform();
	}

	private void setupReportHandler() {
		final ReportHandler delegateTo;
		if (context.getReportsOut() != null) {
			delegateTo = new ReportFile(context.getReportsOut());
		}
		else {
			delegateTo = null;
		}
		
		/*
		 * The report handler writes a summary to std out
		 */
		reportHandler = new ReportHandler() {
			
			@Override
			public void publishReport(Report<?> report) {
				ExecUtil.printSummary(report);
				if (delegateTo != null) {
					delegateTo.publishReport(report);
				}
			}
		};
	}

	private void loadProject() throws IOException {
		System.out.println(STATUS_PREFIX + "Loading HALE project...");
		
		env = new ProjectTransformationEnvironment(id, new DefaultInputSupplier(context.getProject()), reportHandler);
	}
	
	private void setupReader() {
		LocatableInputSupplier<? extends InputStream> sourceIn = new DefaultInputSupplier(
				context.getSource());

		// find applicable reader
		source = HaleIO.findIOProvider(InstanceReader.class, sourceIn, context
				.getSource().getPath());
		//TODO allow custom configuration of reader
		
		source.setSource(sourceIn);
//		source.setCRSProvider(crsProvider)
		// source schema is set in Transformation.transform
	}
	
	private void setupWriter() {
		String preset = context.getPreset();
		if (preset == null) {
			System.out.println(ERROR_PREFIX + "Please specify the name of a data export configuration preset");
			return;
		}
		IOConfiguration conf = env.getExportPresets().get(preset);
		if (conf == null) {
			System.out.println(ERROR_PREFIX + "Data export configration preset not found: " + preset);
			return;
		}
		
		String writerId = conf.getProviderId();
		target = HaleIO.createIOProvider(InstanceWriter.class, null, writerId);
		target.setTarget(new FileIOSupplier(context.getOut()));
		target.setTargetSchema(env.getTargetSchema());

		// determine content type to use based on file extension
		IOProviderDescriptor factory = HaleIO.findIOProviderFactory(InstanceWriter.class, null, writerId);
		List<IContentType> cts = HaleIO.findContentTypesFor(factory.getSupportedTypes(), null, context.getOut().getPath());
		if (!cts.isEmpty()) {
			target.setContentType(cts.get(0));
		}
	}

//	private void setupValidator() {
//		if (context.isValidate()) {
//			validator = HaleIO.createIOProvider(InstanceValidator.class, null, XML_VALIDATOR_ID);
//			// set validation schemas
//			List<? extends Locatable> schemas = target.getValidationSchemas();
//			validator.setSchemas(schemas.toArray(new Locatable[schemas.size()]));
//			// set source
//			validator.setSource(new FileIOSupplier(context.getOut()));
//			
//			// determine content type to use base on file extension
//			IOProviderDescriptor factory = HaleIO.findIOProviderFactory(InstanceValidator.class, null, XML_VALIDATOR_ID);
//			List<IContentType> cts = HaleIO.findContentTypesFor(factory.getSupportedTypes(), null, context.getOut().getPath());
//			if (!cts.isEmpty()) {
//				validator.setContentType(cts.get(0));
//			}
//		}
//	}
	
	private void transform() throws InterruptedException, ExecutionException {
		System.out.println(STATUS_PREFIX + "Running HALE transformation...");
		
		List<InstanceReader> sources = new ArrayList<InstanceReader>();
		sources.add(source);
		ListenableFuture<Boolean> res = Transformation.transform(sources, target, env, reportHandler, id, null);
		
		//TODO success/failure message
		res.get();
	}

}
