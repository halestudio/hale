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

package eu.esdihumboldt.hale.ui.service.instance.internal.orient;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.xml.namespace.QName;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.operations.IWorkbenchOperationSupport;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import de.cs3d.util.logging.ATransaction;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationReport;
import eu.esdihumboldt.hale.common.align.transformation.service.TransformationService;
import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.instance.model.impl.OGroup;
import eu.esdihumboldt.hale.common.instance.model.impl.ONameUtil;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.common.service.population.PopulationService;
import eu.esdihumboldt.hale.ui.io.util.ProgressMonitorIndicator;
import eu.esdihumboldt.hale.ui.io.util.ThreadProgressMonitor;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;
import eu.esdihumboldt.hale.ui.service.instance.internal.AbstractInstanceService;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.hale.ui.service.project.internal.AbstractRemoveResourcesOperation;
import eu.esdihumboldt.hale.ui.service.report.ReportService;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;

/**
 * {@link InstanceService} implementation based on OrientDB. This must be a
 * singleton as the references to the databases may only exist once.
 * 
 * @author Simon Templer
 */
public class OrientInstanceService extends AbstractInstanceService {

	private static final ALogger log = ALoggerFactory.getLogger(OrientInstanceService.class);

	private static volatile OrientInstanceService instance;

	/**
	 * Get the service instance
	 * 
	 * @param schemaService the schema service
	 * @param projectService the project service
	 * @param alignmentService the alignment service
	 * @return the service instance
	 */
	public static final OrientInstanceService getInstance(SchemaService schemaService,
			ProjectService projectService, AlignmentService alignmentService) {
		if (instance == null) {
			instance = new OrientInstanceService(schemaService, projectService, alignmentService);
		}
		return instance;
	}

	/**
	 * Get the existing service instance.
	 * 
	 * @return the existing service instance or <code>null</code> if none was
	 *         created
	 */
	public static OrientInstanceService getExistingInstance() {
		return instance;
	}

	private final SchemaService schemaService;

	private final LocalOrientDB source;
	private final LocalOrientDB transformed;

	private final File databasesFolder;

	/**
	 * Default constructor
	 * 
	 * @param schemaService the schema service
	 * @param projectService the project service
	 * @param alignmentService the alignment service
	 */
	private OrientInstanceService(SchemaService schemaService, ProjectService projectService,
			AlignmentService alignmentService) {
		super(projectService, alignmentService);

		this.schemaService = schemaService;

		// setup databases
		File instanceLoc;
		try {
			instanceLoc = new File(Platform.getInstanceLocation().getURL().toURI());
		} catch (URISyntaxException e) {
			instanceLoc = new File(System.getProperty("java.io.tmpdir"));
		}
		instanceLoc = new File(instanceLoc, "instances");

		File tmpInstanceLoc = null;
		while (tmpInstanceLoc == null || tmpInstanceLoc.exists()) {
			tmpInstanceLoc = new File(instanceLoc, UUID.randomUUID().toString());
		}
		databasesFolder = tmpInstanceLoc;

		source = new LocalOrientDB(new File(databasesFolder, "source"));
		transformed = new LocalOrientDB(new File(databasesFolder, "transformed"));
	}

	/**
	 * @see InstanceService#getInstances(DataSet)
	 */
	@Override
	public InstanceCollection getInstances(DataSet dataset) {
		switch (dataset) {
		case SOURCE:
			return new BrowseOrientInstanceCollection(source,
					schemaService.getSchemas(SchemaSpaceID.SOURCE), DataSet.SOURCE);
		case TRANSFORMED:
			return new BrowseOrientInstanceCollection(transformed,
					schemaService.getSchemas(SchemaSpaceID.TARGET), DataSet.TRANSFORMED);
		}

		throw new IllegalArgumentException("Illegal data set requested: " + dataset);
	}

	/**
	 * @see InstanceService#getInstanceTypes(DataSet)
	 */
	@Override
	public Set<TypeDefinition> getInstanceTypes(DataSet dataset) {
		switch (dataset) {
		case SOURCE:
			return getInstanceTypes(source, schemaService.getSchemas(SchemaSpaceID.SOURCE));
		case TRANSFORMED:
			return getInstanceTypes(transformed, schemaService.getSchemas(SchemaSpaceID.TARGET));
		}

		throw new IllegalArgumentException("Illegal data set requested: " + dataset);
	}

	/**
	 * Get the instance types available in the given database
	 * 
	 * @param lodb the database
	 * @param schemas the type definitions
	 * @return the set of type definitions for which instances are present in
	 *         the database
	 */
	private Set<TypeDefinition> getInstanceTypes(LocalOrientDB lodb, SchemaSpace schemas) {
		Set<TypeDefinition> result = new HashSet<TypeDefinition>();

		DatabaseReference<ODatabaseDocumentTx> dbref = lodb.openRead();
		try {
			ODatabaseDocumentTx db = dbref.getDatabase();

			// get schema and classes
			OSchema schema = db.getMetadata().getSchema();
			Collection<OClass> classes = schema.getClasses();

			Collection<? extends TypeDefinition> mappableTypes = schemas.getMappingRelevantTypes();
			Set<String> allowedIdentifiers = new HashSet<String>();
			for (TypeDefinition type : mappableTypes) {
				allowedIdentifiers.add(type.getIdentifier());
			}

			for (OClass clazz : classes) {
				try {
					if (clazz.getName().equals(OGroup.BINARY_WRAPPER_CLASSNAME)) {
						// ignore binary wrapper class
						continue;
					}
					String identifier = ONameUtil.decodeName(clazz.getName());
					if (allowedIdentifiers.contains(identifier)
							&& db.countClass(clazz.getName()) > 0) {
						int lastSlash = identifier.lastIndexOf('/');
						if (lastSlash >= 0) {
							String namespace = identifier.substring(0, lastSlash);
							String localname = identifier.substring(lastSlash + 1);
							TypeDefinition type = schemas.getType(new QName(namespace, localname));
							if (type != null) {
								result.add(type);
							}
							else {
								log.error(MessageFormat.format(
										"Could not resolve type with identifier {0}", identifier));
							}
						}
					}
				} catch (Throwable e) {
					log.error("Could not decode class name to type identifier", e);
				}
			}
		} finally {
			dbref.dispose();
		}

		return result;
	}

	/**
	 * @see InstanceService#addSourceInstances(InstanceCollection)
	 */
	@Override
	public void addSourceInstances(InstanceCollection sourceInstances) {
		notifyDatasetAboutToChange(DataSet.SOURCE);
		final StoreInstancesJob storeInstances = new StoreInstancesJob(
				"Load source instances into database", source, sourceInstances) {

			@Override
			protected void onComplete() {
				notifyDatasetChanged(DataSet.SOURCE);
				retransform();
			}
		};
		/*
		 * Doing this in a job may lead to the transformation being run multiple
		 * times on project load, because then it may be that source instances
		 * are added after the alignment was loaded, if multiple source data
		 * sets are loaded then the transformation can be triggered for each.
		 */
//		storeInstances.schedule();
		// so instead, now the data is loaded in a progress dialog
		try {
			ThreadProgressMonitor.runWithProgressDialog(new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException,
						InterruptedException {
					storeInstances.run(monitor);
				}
			}, true);
		} catch (Exception e) {
			log.error("Error starting process to load source data", e);
		}
	}

	/**
	 * @see InstanceService#clearInstances()
	 */
	@Override
	public void clearInstances() {
		IUndoableOperation operation = new AbstractRemoveResourcesOperation("Clear source data",
				InstanceService.ACTION_READ_SOURCEDATA) {

			/**
			 * @see eu.esdihumboldt.hale.ui.service.project.internal.AbstractRemoveResourcesOperation#execute(org.eclipse.core.runtime.IProgressMonitor,
			 *      org.eclipse.core.runtime.IAdaptable)
			 */
			@Override
			public IStatus execute(IProgressMonitor monitor, IAdaptable info)
					throws ExecutionException {
				notifyDatasetAboutToChange(null);
				source.clear();
				transformed.clear();
				notifyDatasetChanged(null);

				return super.execute(monitor, info);
			}
		};
		IWorkbenchOperationSupport operationSupport = PlatformUI.getWorkbench()
				.getOperationSupport();
		operation.addContext(operationSupport.getUndoContext());
		try {
			operationSupport.getOperationHistory().execute(operation, null, null);
		} catch (ExecutionException e) {
			log.error("Error executing operation on instance service", e);
		}
	}

	/**
	 * Delete the databases.
	 */
	public void dispose() {
		source.delete();
		transformed.delete();

		try {
			FileUtils.deleteDirectory(databasesFolder);
		} catch (IOException e) {
			log.warn("Error deleting temporary databases", e);
		}
	}

	/**
	 * @see InstanceService#getReference(Instance)
	 */
	@Override
	public InstanceReference getReference(Instance instance) {
		return OrientInstanceReference.createReference(instance);
	}

	/**
	 * @see InstanceService#getInstance(InstanceReference)
	 */
	@Override
	public Instance getInstance(InstanceReference reference) {
		OrientInstanceReference ref = (OrientInstanceReference) reference;
		LocalOrientDB lodb = (ref.getDataSet().equals(DataSet.SOURCE)) ? (source) : (transformed);

		return ref.load(lodb);
	}

	/**
	 * @see AbstractInstanceService#doRetransform()
	 */
	@Override
	protected void doRetransform() {
		notifyDatasetAboutToChange(DataSet.TRANSFORMED);

		transformed.clear();

		/*
		 * XXX cause population updated are currently coupled to
		 * StoreInstancesJob/OrientInstanceSink and not to events, we have to
		 * clear the transformed population at this point.
		 */
		PopulationService ps = (PopulationService) PlatformUI.getWorkbench().getService(
				PopulationService.class);
		if (ps != null) {
			ps.resetPopulation(DataSet.TRANSFORMED);
		}

		boolean success = performTransformation();

		if (!success) {
			// there may be some (inconsistent) transformed instances from a
			// canceled transformation
			// disable transformation (will clear transformed instances)
			setTransformationEnabled(false);
			log.userInfo("Live transformation has been disabled.\nYou can again enable it in the main toolbar or in the File menu.");
		}
		else {
			// notify about transformed instances
			notifyDatasetChanged(DataSet.TRANSFORMED);
		}
	}

	/**
	 * Perform the transformation
	 * 
	 * @return if the transformation was successful
	 */
	protected boolean performTransformation() {
		final TransformationService ts = getTransformationService();
		if (ts == null) {
			log.userError("No transformation service available");
			return false;
		}

		final AtomicBoolean transformationFinished = new AtomicBoolean(false);
		final AtomicBoolean transformationCanceled = new AtomicBoolean(false);
		IRunnableWithProgress op = new IRunnableWithProgress() {

			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException,
					InterruptedException {
				try {
					InstanceCollection sources = getInstances(DataSet.SOURCE);
					if (sources.isEmpty()) {
						return;
					}

					OrientInstanceSink sink = new OrientInstanceSink(transformed, true);
					TransformationReport report;
					ATransaction trans = log.begin("Instance transformation");
					try {
						report = ts.transform(getAlignmentService().getAlignment(), sources, sink,
								new ProgressMonitorIndicator(monitor));

						// publish report
						ReportService rs = (ReportService) PlatformUI.getWorkbench().getService(
								ReportService.class);
						rs.addReport(report);
					} finally {
						try {
							sink.close();
						} catch (IOException e) {
							// ignore
						}
						trans.end();
					}
				} finally {
					// remember if canceled
					if (monitor.isCanceled()) {
						transformationCanceled.set(true);
					}

					// transformation finished
					transformationFinished.set(true);
				}
			}

		};

		try {
			ThreadProgressMonitor.runWithProgressDialog(op, ts.isCancelable());
		} catch (Throwable e) {
			log.error("Error starting transformation process", e);
		}

		// wait for transformation to complete
		HaleUI.waitFor(transformationFinished);

		return !transformationCanceled.get();
	}

	/**
	 * @see AbstractInstanceService#clearTransformedInstances()
	 */
	@Override
	protected void clearTransformedInstances() {
		notifyDatasetAboutToChange(DataSet.TRANSFORMED);
		transformed.clear();
		notifyDatasetChanged(DataSet.TRANSFORMED);
	}

}
