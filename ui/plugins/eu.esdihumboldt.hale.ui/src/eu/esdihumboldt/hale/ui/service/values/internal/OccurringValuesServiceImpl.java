/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.service.values.internal;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.PlatformUI;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.TreeMultiset;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.common.core.io.project.model.Resource;
import eu.esdihumboldt.hale.common.instance.io.InstanceIO;
import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.model.TypeFilter;
import eu.esdihumboldt.hale.common.instance.model.impl.MultiInstanceCollection;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;
import eu.esdihumboldt.hale.ui.service.instance.InstanceServiceAdapter;
import eu.esdihumboldt.hale.ui.service.instance.sample.internal.InstanceViewPreferences;
import eu.esdihumboldt.hale.ui.service.project.ProjectResourcesUtil;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.hale.ui.service.project.ProjectServiceAdapter;
import eu.esdihumboldt.hale.ui.service.values.OccurringValues;
import eu.esdihumboldt.hale.ui.service.values.OccurringValuesUtil;
import eu.esdihumboldt.hale.ui.transformation.TransformDataImportAdvisor;
import eu.esdihumboldt.hale.ui.util.io.ThreadProgressMonitor;

/**
 * Service that determines what different values occur for specific
 * {@link PropertyEntityDefinition}s.
 * 
 * @author Simon Templer
 */
public class OccurringValuesServiceImpl extends AbstractOccurringValuesService {

	private static final ALogger log = ALoggerFactory.getLogger(OccurringValuesServiceImpl.class);

	/**
	 * Job determining the occurring values for a specific property entity.
	 */
	public class OccurringValuesJob extends Job {

		private final PropertyEntityDefinition property;
		private final Map<PropertyEntityDefinition, OccurringValuesImpl> values;
		private final InstanceCollection instances;

		/**
		 * Create a Job to update the occurring values for the given property
		 * entity.
		 * 
		 * @param property the property entity definition
		 * @param values the map to store the updated information in
		 * @param instances the instances to test
		 */
		public OccurringValuesJob(PropertyEntityDefinition property,
				Map<PropertyEntityDefinition, OccurringValuesImpl> values,
				InstanceCollection instances) {
			super("Determine occurring values");
			this.property = property;
			this.values = values;
			this.instances = instances;

			setUser(true);
		}

		@SuppressWarnings("unchecked")
		@Override
		protected IStatus run(IProgressMonitor monitor) {
			// only select instances of the correct type
			InstanceCollection instances = this.instances
					.select(new TypeFilter(property.getType()));
			// and apply an eventual filter
			if (property.getFilter() != null) {
				instances = instances.select(property.getFilter());
			}

			boolean instanceProgress = instances.hasSize();
			String taskName = "Check instances for occuring values";
			if (instanceProgress) {
				monitor.beginTask(taskName, instances.size());
			}
			else {
				monitor.beginTask(taskName, IProgressMonitor.UNKNOWN);
			}

			// create set to store values
			@SuppressWarnings("rawtypes")
			Multiset collectedValues;
			Class<?> binding = property.getDefinition().getPropertyType()
					.getConstraint(Binding.class).getBinding();
			if (Comparable.class.isAssignableFrom(binding)) {
				// tree set for sorted values
				collectedValues = TreeMultiset.create();
			}
			else {
				// unsorted values
				collectedValues = HashMultiset.create();
			}

			ResourceIterator<Instance> it = instances.iterator();
			try {
				while (it.hasNext()) {
					Instance instance = it.next();
					AlignmentUtil.addValues(instance, property.getPropertyPath(), collectedValues,
							true);
					if (instanceProgress) {
						// TODO improved monitor update?!
						monitor.worked(1);
					}
				}
			} finally {
				it.close();
			}

			synchronized (values) {
				OccurringValuesImpl ov = new OccurringValuesImpl(collectedValues, property);
				values.put(property, ov);
			}

			notifyOccurringValuesUpdated(property);

			monitor.done();

			return Status.OK_STATUS;
		}
	}

	/**
	 * Values that occur in the source data.
	 */
	private final Map<PropertyEntityDefinition, OccurringValuesImpl> sourceValues = new HashMap<PropertyEntityDefinition, OccurringValuesImpl>();

	/**
	 * Values that occur in the transformed data.
	 */
	private final Map<PropertyEntityDefinition, OccurringValuesImpl> transformedValues = new HashMap<PropertyEntityDefinition, OccurringValuesImpl>();

	/**
	 * The service for accessing instances.
	 */
	private final InstanceService instances;

	/**
	 * Create a service instance.
	 * 
	 * @param instances the instance service
	 * @param projectService the project service
	 */
	public OccurringValuesServiceImpl(InstanceService instances, ProjectService projectService) {
		super();

		// add instance service listener
		instances.addListener(new InstanceServiceAdapter() {

			@Override
			public void datasetChanged(DataSet type) {
				SchemaSpaceID schemaSpace;
				switch (type) {
				case TRANSFORMED:
					schemaSpace = SchemaSpaceID.TARGET;
					break;
				default:
					schemaSpace = SchemaSpaceID.SOURCE;
				}

				invalidateValues(schemaSpace, type);
			}

		});

		// add project service listener
		projectService.addListener(new ProjectServiceAdapter() {

			@Override
			public void projectSettingChanged(String name, Value value) {
				if (InstanceViewPreferences.KEY_OCCURRING_VALUES_USE_EXTERNAL.equals(name)) {
					// invalidate values on setting change
					invalidateValues(SchemaSpaceID.SOURCE, DataSet.SOURCE);
				}
			}

		});

		this.instances = instances;
	}

	/**
	 * Invalidate occurring values in the given schema space.
	 * 
	 * @param schemaSpace the schema space
	 * @param dataSet the data set
	 */
	protected void invalidateValues(SchemaSpaceID schemaSpace, DataSet dataSet) {
		Map<PropertyEntityDefinition, OccurringValuesImpl> values = selectValues(schemaSpace);

		boolean empty = instances.getInstances(dataSet).isEmpty();

		synchronized (values) {
			if (empty) {
				// remove all values
				values.clear();
			}
			else {
				// invalidate all values
				for (OccurringValuesImpl ov : values.values()) {
					ov.invalidate();
				}
			}
		}

		notifyOccurringValuesInvalidated(schemaSpace);
	}

	@Override
	public OccurringValues getOccurringValues(PropertyEntityDefinition property) {
		return getOccurringValues(property, selectValues(property.getSchemaSpace()));
	}

	private Map<PropertyEntityDefinition, OccurringValuesImpl> selectValues(
			SchemaSpaceID schemaSpace) {
		switch (schemaSpace) {
		case SOURCE:
			return sourceValues;
		case TARGET:
			return transformedValues;
		default:
			throw new IllegalArgumentException("Illegal schema space specified");
		}
	}

	/**
	 * Get the values occurring in the data for the given property entity.
	 * 
	 * @param property the property entity definition
	 * @param values the map containing the current occurring values
	 * @return the occurring values for the property or <code>null</code>
	 */
	private OccurringValues getOccurringValues(PropertyEntityDefinition property,
			Map<PropertyEntityDefinition, ? extends OccurringValues> values) {
		synchronized (values) {
			OccurringValues ov = values.get(property);
			return ov;
		}
	}

	@Override
	public boolean updateOccurringValues(PropertyEntityDefinition property) {
		// sanity check on property
		if (!OccurringValuesUtil.supportsOccurringValues(property)) {
			throw new IllegalArgumentException(
					"Determinining occurring values not supported for given property");
		}

		return updateOccuringValues(property, selectValues(property.getSchemaSpace()));
	}

	/**
	 * Update the occurring values for the given property entity.
	 * 
	 * @param property the property entity definition
	 * @param values the map containing the current occurring values
	 * @return <code>true</code> if the task to update the information has been
	 *         started, <code>false</code> if the information was up-to-date
	 */
	private boolean updateOccuringValues(PropertyEntityDefinition property,
			Map<PropertyEntityDefinition, OccurringValuesImpl> values) {
		synchronized (values) {
			OccurringValues ov = values.get(property);
			if (ov != null && ov.isUpToDate()) {
				return false;
			}
		}

		// determine occurring values

		// determine data set
		DataSet dataSet;
		switch (property.getSchemaSpace()) {
		case TARGET:
			dataSet = DataSet.TRANSFORMED;
			break;
		default:
			dataSet = DataSet.SOURCE;
		}

		// determine if external data should be used
		boolean useExternalData = false;
		if (dataSet.equals(DataSet.SOURCE)) {
			ProjectService ps = PlatformUI.getWorkbench().getService(ProjectService.class);
			useExternalData = InstanceViewPreferences
					.occurringValuesUseExternalData(ps.getConfigurationService());
		}

		InstanceCollection collection;
		if (!useExternalData) {
			collection = instances.getInstances(dataSet);
		}
		else {
			// use complete project data sources
			final AtomicReference<InstanceCollection> source = new AtomicReference<>();

			IRunnableWithProgress op = new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					ProjectService ps = PlatformUI.getWorkbench().getService(ProjectService.class);

					List<InstanceCollection> sources = new ArrayList<>();
					for (Resource resource : ps.getResources()) {
						if (InstanceIO.ACTION_LOAD_SOURCE_DATA.equals(resource.getActionId())) {
							// resource is source data

							IOConfiguration conf = resource.copyConfiguration(true);

							TransformDataImportAdvisor advisor = new TransformDataImportAdvisor();
							ProjectResourcesUtil.executeConfiguration(conf, advisor, false, null);

							if (advisor.getInstances() != null) {
								sources.add(advisor.getInstances());
							}
						}
					}

					source.set(new MultiInstanceCollection(sources));
				}
			};
			try {
				ThreadProgressMonitor.runWithProgressDialog(op, false);
				collection = source.get();
			} catch (Exception e) {
				log.error("Error initializing data sources", e);
				return true;
			}
		}

		// go through instances to determine occurring values
		Job job = new OccurringValuesJob(property, values, collection);
		job.schedule();

		return true;
	}

}
