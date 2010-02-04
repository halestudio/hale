/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.models.instance;

import eu.esdihumboldt.hale.models.AbstractUpdateService;
import eu.esdihumboldt.hale.models.HaleServiceListener;
import eu.esdihumboldt.hale.models.InstanceService;
import eu.esdihumboldt.hale.models.UpdateMessage;

/**
 * Notification handling for {@link InstanceService}s that support
 * {@link InstanceServiceListener}s
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public abstract class AbstractInstanceService extends AbstractUpdateService
		implements InstanceService {

	/**
	 * The default update message
	 */
	private static final UpdateMessage<?> DEF_MESSAGE = new UpdateMessage<Object>(InstanceService.class, null);
	
	/**
	 * @see AbstractUpdateService#notifyListeners(UpdateMessage)
	 * @deprecated use {@link #notifyDatasetChanged(DatasetType)} instead
	 */
	@Deprecated
	@Override
	protected void notifyListeners(UpdateMessage<?> message) {
		notifyDatasetChanged(null);
	}

	/**
	 * Call when a data set has changed
	 * 
	 * @param type the data set type, <code>null</code> if both sets have changed
	 */
	protected void notifyDatasetChanged(DatasetType type) {
		for (HaleServiceListener listener : getListeners()) {
			if (listener instanceof InstanceServiceListener) {
				if (type == null) {
					((InstanceServiceListener) listener).datasetChanged(DatasetType.reference);
					((InstanceServiceListener) listener).datasetChanged(DatasetType.transformed);
				} else {
					((InstanceServiceListener) listener).datasetChanged(type);
				}
			}
			
			listener.update(DEF_MESSAGE);
		}
	}

}
