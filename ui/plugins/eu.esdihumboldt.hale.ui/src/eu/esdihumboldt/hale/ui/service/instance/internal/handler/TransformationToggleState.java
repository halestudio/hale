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

package eu.esdihumboldt.hale.ui.service.instance.internal.handler;

import org.eclipse.core.commands.State;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.ui.service.instance.InstanceService;
import eu.esdihumboldt.hale.ui.service.instance.InstanceServiceAdapter;
import eu.esdihumboldt.hale.ui.service.instance.InstanceServiceListener;

/**
 * Command state that represents if the {@link InstanceService} transformation
 * is enabled.
 * 
 * @author Simon Templer
 */
public class TransformationToggleState extends State {

	private final InstanceServiceListener instanceListener;

	/**
	 * Default constructor
	 */
	public TransformationToggleState() {
		super();

		InstanceService is = (InstanceService) PlatformUI.getWorkbench().getService(
				InstanceService.class);
		is.addListener(instanceListener = new InstanceServiceAdapter() {

			@Override
			public void transformationToggled(boolean enabled) {
				// when the transformation is toggled through any means, update
				// the state
				setValue(enabled);
			}

		});
		setValue(is.isTransformationEnabled());
	}

	/**
	 * @see org.eclipse.core.commands.State#dispose()
	 */
	@Override
	public void dispose() {
		InstanceService is = (InstanceService) PlatformUI.getWorkbench().getService(
				InstanceService.class);
		is.removeListener(instanceListener);
	}

}
