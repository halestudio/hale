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

package eu.esdihumboldt.hale.ui.service.instance.internal;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISourceProvider;
import org.eclipse.ui.ISources;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;
import eu.esdihumboldt.hale.ui.service.instance.InstanceServiceAdapter;
import eu.esdihumboldt.hale.ui.service.instance.InstanceServiceListener;

/**
 * Provides UI variables related to the {@link InstanceService}
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.2 
 */
public class InstanceServiceSource extends AbstractSourceProvider {

	/**
	 * The name of the variable which value is <code>true</code> if there are
	 * transformed instances present in the {@link InstanceService}.
	 */
	public static final String HAS_TRANSFORMED_INSTANCES = "hale.instances.has_transformed";
	/**
	 * The name of the variable which value is <code>true</code> if there are
	 * instances present in the {@link InstanceService}.
	 */
	public static final String HAS_SOURCE_INSTANCES = "hale.instances.has_source";
	
	private InstanceServiceListener instanceListener;
	
	/**
	 * Default constructor
	 */
	public InstanceServiceSource() {
		super();
		
		final InstanceService is = (InstanceService) PlatformUI.getWorkbench().getService(InstanceService.class);
		is.addListener(instanceListener = new InstanceServiceAdapter() {

			@Override
			public void datasetChanged(DataSet type) {
				switch(type) {
				case TRANSFORMED:
					fireSourceChanged(ISources.WORKBENCH, HAS_TRANSFORMED_INSTANCES, hasTransformedInstances(is));
					break;
				case SOURCE:
					fireSourceChanged(ISources.WORKBENCH, HAS_SOURCE_INSTANCES, hasSourceInstances(is));
					break;
				}
			}

		});
	}

	/**
	 * @see ISourceProvider#dispose()
	 */
	@Override
	public void dispose() {
		InstanceService is = (InstanceService) PlatformUI.getWorkbench().getService(InstanceService.class);
		is.removeListener(instanceListener);
	}

	/**
	 * @see ISourceProvider#getCurrentState()
	 */
	@Override
	public Map<String, Object> getCurrentState() {
		InstanceService is = (InstanceService) PlatformUI.getWorkbench().getService(InstanceService.class);
		
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(HAS_TRANSFORMED_INSTANCES, hasTransformedInstances(is));
		
		return result;
	}

	private static boolean hasTransformedInstances(InstanceService is) {
		InstanceCollection instances = is.getInstances(DataSet.TRANSFORMED);
		return instances != null && !instances.isEmpty();
	}

	private static boolean hasSourceInstances(InstanceService is) {
		InstanceCollection instances = is.getInstances(DataSet.SOURCE);
		return instances != null && !instances.isEmpty();
	}

	/**
	 * @see ISourceProvider#getProvidedSourceNames()
	 */
	@Override
	public String[] getProvidedSourceNames() {
		return new String[]{
				HAS_TRANSFORMED_INSTANCES, HAS_SOURCE_INSTANCES};
	}

}
