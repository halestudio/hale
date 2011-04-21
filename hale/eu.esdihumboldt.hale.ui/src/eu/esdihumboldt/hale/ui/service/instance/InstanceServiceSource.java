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

package eu.esdihumboldt.hale.ui.service.instance;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISourceProvider;
import org.eclipse.ui.ISources;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.models.InstanceService;
import eu.esdihumboldt.hale.models.UpdateMessage;
import eu.esdihumboldt.hale.models.InstanceService.DatasetType;
import eu.esdihumboldt.hale.models.instance.InstanceServiceListener;

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
	 * transformed instances present in the {@link InstanceService} 
	 */
	public static final String HAS_TRANSFORMED_INSTANCES = "hale.instances.has_transformed";
	
	private InstanceServiceListener instanceListener;
	
	/**
	 * Default constructor
	 */
	public InstanceServiceSource() {
		super();
		
		final InstanceService is = (InstanceService) PlatformUI.getWorkbench().getService(InstanceService.class);
		is.addListener(instanceListener = new InstanceServiceListener() {

			@Override
			public void datasetChanged(DatasetType type) {
				switch(type) {
				case transformed:
					fireSourceChanged(ISources.WORKBENCH, HAS_TRANSFORMED_INSTANCES, hasTransformedInstances(is));
					break;
				case reference:
					// do nothing (yet)
					break;
				}
			}

			@Override
			public void update(UpdateMessage<?> message) {
				// ignore
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
		return !is.getFeatures(DatasetType.transformed).isEmpty();
	}

	/**
	 * @see ISourceProvider#getProvidedSourceNames()
	 */
	@Override
	public String[] getProvidedSourceNames() {
		return new String[]{
				HAS_TRANSFORMED_INSTANCES};
	}

}
