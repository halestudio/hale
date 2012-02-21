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
import java.util.concurrent.atomic.AtomicBoolean;

import javax.xml.namespace.QName;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
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
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;
import eu.esdihumboldt.hale.ui.service.instance.internal.AbstractInstanceService;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.hale.ui.service.report.ReportService;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;

/**
 * {@link InstanceService} implementation based on OrientDB. This must be a
 * singleton as the references to the databases may only exist once.
 * @author Simon Templer
 */
public class OrientInstanceService extends AbstractInstanceService {
	
	private static final ALogger log = ALoggerFactory.getLogger(OrientInstanceService.class);
	
	private static OrientInstanceService instance;
	
	/**
	 * Get the service instance
	 * @param schemaService the schema service
	 * @param projectService the project service
	 * @param alignmentService the alignment service 
	 * @return the service instance
	 */
	public static final OrientInstanceService getInstance(
			SchemaService schemaService, ProjectService projectService, 
			AlignmentService alignmentService) {
		if (instance == null) {
			instance = new OrientInstanceService(schemaService, projectService,
					alignmentService);
		}
		return instance;
	}
	
	private final SchemaService schemaService;
	
	private final LocalOrientDB source;
	private final LocalOrientDB transformed;
	
	private final AtomicBoolean outstandingTransform = new AtomicBoolean(true);

	/**
	 * Default constructor 
	 * @param schemaService the schema service
	 * @param projectService the project service 
	 * @param alignmentService the alignment service 
	 */
	private OrientInstanceService(SchemaService schemaService, 
			ProjectService projectService, AlignmentService alignmentService) {
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
		
		source = new LocalOrientDB(new File(instanceLoc, "source"));
		transformed = new LocalOrientDB(new File(instanceLoc, "transformed"));
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
			updateTransformed();
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
			updateTransformed();
			return getInstanceTypes(transformed, schemaService.getSchemas(SchemaSpaceID.TARGET));
		}
		
		throw new IllegalArgumentException("Illegal data set requested: " + dataset);
	}

	/**
	 * Get the instance types available in the given database
	 * @param lodb the database
	 * @param schemas the type definitions
	 * @return the set of type definitions for which instances are present in 
	 * the database
	 */
	private Set<TypeDefinition> getInstanceTypes(LocalOrientDB lodb,
			SchemaSpace schemas) {
		Set<TypeDefinition> result = new HashSet<TypeDefinition>();
		
		DatabaseReference<ODatabaseDocumentTx> dbref = lodb.openRead();
		try {
			ODatabaseDocumentTx db = dbref.getDatabase();
			
			// get schema and classes
			OSchema schema = db.getMetadata().getSchema();
			Collection<OClass> classes = schema.getClasses();
			
			Collection<? extends TypeDefinition> mappableTypes = schemas.getMappableTypes();
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
					if (allowedIdentifiers.contains(identifier) && 
							db.countClass(clazz.getName()) > 0) {
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
										"Could not resolve type with identifier {0}", 
										identifier));
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
		Job storeInstances = new StoreInstancesJob(
				"Load source instances into database", source, sourceInstances) {
					@Override
					protected void onComplete() {
						notifyDatasetChanged(DataSet.SOURCE);
						retransform();
					}
		};
		storeInstances.schedule();
	}

	/**
	 * @see InstanceService#clearInstances()
	 */
	@Override
	public void clearInstances() {
		source.clear();
		transformed.clear();
		outstandingTransform.set(false);
		
		notifyDatasetChanged(null);
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
		if (ref.getDataSet().equals(DataSet.TRANSFORMED)) {
			updateTransformed();
		}
		LocalOrientDB lodb = (ref.getDataSet().equals(DataSet.SOURCE))?(source):(transformed);
		
		return ref.load(lodb);
	}

	/**
	 * Update the transformed instances
	 */
	private synchronized void updateTransformed() {
		if (outstandingTransform.compareAndSet(true, false)) {
			performTransformation();
		}
	}

	/**
	 * @see AbstractInstanceService#retransform()
	 */
	@Override
	protected void retransform() {
		transformed.clear();
		
		outstandingTransform.set(true);
		
		notifyDatasetChanged(DataSet.TRANSFORMED);
	}
	
	/**
	 * Perform the transformation
	 */
	protected void performTransformation() {
		final AtomicBoolean transformationFinished = new AtomicBoolean(false);
		final Display display = PlatformUI.getWorkbench().getDisplay();
		display.syncExec(new Runnable() {
			
			@Override
			public void run() {
				ProgressMonitorDialog pm = new ProgressMonitorDialog(display.getActiveShell());
				try {
					pm.run(true, false, new IRunnableWithProgress() {
						
						@Override
						public void run(IProgressMonitor monitor) throws InvocationTargetException,
								InterruptedException {
							try {
								monitor.beginTask("Transform source instances", IProgressMonitor.UNKNOWN);
								
								TransformationService ts = getTransformationService();
								if (ts == null) {
									log.userError("No transformation service available");
									return;
								}
								
								OrientInstanceSink sink = new OrientInstanceSink(transformed);
								TransformationReport report;
								try {
									//TODO transformation should be done in a job!
									report = ts.transform(
											getAlignmentService().getAlignment(), 
											getInstances(DataSet.SOURCE), 
											sink);
									
									// publish report
									ReportService rs = (ReportService) PlatformUI.getWorkbench().getService(ReportService.class);
									rs.addReport(report);
								} finally {
									try {
										sink.close();
									} catch (IOException e) {
										// ignore
									}
								}
							} finally {
								monitor.done();
								transformationFinished.set(true);
							}
						}
					});
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		// wait for transformation to complete
		HaleUI.waitFor(transformationFinished);
	}

	/**
	 * @see AbstractInstanceService#clearTransformedInstances()
	 */
	@Override
	protected void clearTransformedInstances() {
		transformed.clear();
		notifyDatasetChanged(DataSet.TRANSFORMED);
	}

}
