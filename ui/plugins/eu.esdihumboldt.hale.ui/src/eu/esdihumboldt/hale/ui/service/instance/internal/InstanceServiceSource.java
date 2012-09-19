/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
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

		final InstanceService is = (InstanceService) PlatformUI.getWorkbench().getService(
				InstanceService.class);
		is.addListener(instanceListener = new InstanceServiceAdapter() {

			@Override
			public void datasetChanged(DataSet type) {
				switch (type) {
				case TRANSFORMED:
					fireSourceChanged(ISources.WORKBENCH, HAS_TRANSFORMED_INSTANCES,
							hasTransformedInstances(is));
					break;
				case SOURCE:
					fireSourceChanged(ISources.WORKBENCH, HAS_SOURCE_INSTANCES,
							hasSourceInstances(is));
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
		InstanceService is = (InstanceService) PlatformUI.getWorkbench().getService(
				InstanceService.class);
		is.removeListener(instanceListener);
	}

	/**
	 * @see ISourceProvider#getCurrentState()
	 */
	@Override
	public Map<String, Object> getCurrentState() {
		InstanceService is = (InstanceService) PlatformUI.getWorkbench().getService(
				InstanceService.class);

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
		return new String[] { HAS_TRANSFORMED_INSTANCES, HAS_SOURCE_INSTANCES };
	}

}
