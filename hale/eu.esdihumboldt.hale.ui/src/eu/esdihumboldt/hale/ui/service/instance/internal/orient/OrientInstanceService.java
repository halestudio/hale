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
import java.net.URISyntaxException;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;

import eu.esdihumboldt.hale.instance.model.Instance;
import eu.esdihumboldt.hale.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.instance.model.impl.OInstance;
import eu.esdihumboldt.hale.ui.service.instance.DataSet;
import eu.esdihumboldt.hale.ui.service.instance.InstanceReference;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;
import eu.esdihumboldt.hale.ui.service.instance.internal.AbstractInstanceService;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;
import eu.esdihumboldt.hale.ui.service.schema.SchemaSpaceID;

/**
 * {@link InstanceService} implementation based on OrientDB. This must be a
 * singleton as the references to the databases may only exist once.
 * @author Simon Templer
 */
public class OrientInstanceService extends AbstractInstanceService {
	
	private static OrientInstanceService instance;
	
	/**
	 * Get the service instance
	 * @param schemaService the schema service
	 * @param projectService the project service
	 * @return the service instance
	 */
	public static final OrientInstanceService getInstance(
			SchemaService schemaService, ProjectService projectService) {
		if (instance == null) {
			instance = new OrientInstanceService(schemaService, projectService);
		}
		return instance;
	}
	
	private final SchemaService schemaService;
	
	private final LocalOrientDB source;
	private final LocalOrientDB transformed;

	/**
	 * Default constructor 
	 * @param schemaService the schema service
	 * @param projectService the project service 
	 */
	private OrientInstanceService(SchemaService schemaService, ProjectService projectService) {
		super(projectService);
		
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
			return new BrowseOrientInstanceCollection(source, schemaService.getSchemas(SchemaSpaceID.SOURCE));
		case TRANSFORMED:
			return new BrowseOrientInstanceCollection(transformed, schemaService.getSchemas(SchemaSpaceID.TARGET));
		}
		
		throw new IllegalArgumentException("Illegal data set requested: " + dataset);
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
		
		notifyDatasetChanged(null);
	}



	/**
	 * @see InstanceService#getReference(Instance, DataSet)
	 */
	@Override
	public InstanceReference getReference(Instance instance, DataSet dataSet) {
		OInstance inst = (OInstance) instance;
		ORID id = inst.getDocument().getIdentity();
		
		return new OrientInstanceReference(id, dataSet, inst.getDefinition());
	}



	/**
	 * @see InstanceService#getInstance(InstanceReference)
	 */
	@Override
	public Instance getInstance(InstanceReference reference) {
		OrientInstanceReference ref = (OrientInstanceReference) reference;
		
		LocalOrientDB lodb = (ref.getDataSet().equals(DataSet.SOURCE))?(source):(transformed);
		
		DatabaseReference<ODatabaseDocumentTx> db = lodb.openRead();
		DatabaseHandle handle = new DatabaseHandle(db.getDatabase());
		try {
			ODocument document = db.getDatabase().load(ref.getId());
			if (document != null) {
				OInstance instance = new OInstance(document, ref.getTypeDefinition());
				handle.addReference(instance);
				return instance;
			}
		}
		finally {
			db.dispose(false);
			handle.tryClose();
		}
		
		return null;
	}

}
