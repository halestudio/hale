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

package eu.esdihumboldt.hale.ui.instancevalidation.status;

import org.eclipse.core.commands.State;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.instancevalidator.report.InstanceValidationReport;
import eu.esdihumboldt.hale.ui.service.instance.validation.InstanceValidationListener;
import eu.esdihumboldt.hale.ui.service.instance.validation.InstanceValidationService;

/**
 * Toggle state for the instance validation, backed by the
 * {@link InstanceValidationService}.
 * 
 * @author Kai Schwierczek
 */
public class ToggleInstanceValidationState extends State {
	private final InstanceValidationListener listener;

	/**
	 * Constructor.
	 */
	public ToggleInstanceValidationState() {
		final InstanceValidationService ivs = (InstanceValidationService) PlatformUI.getWorkbench().getService(InstanceValidationService.class);

		listener = new InstanceValidationListener() {
			@Override
			public void validationEnabledChange() {
				setValue(ivs.isValidationEnabled());
			}
			
			@Override
			public void instancesValidated(InstanceValidationReport report) {
				// don't care
			}
		};
		ivs.addListener(listener);

		setValue(ivs.isValidationEnabled());
	}

	@Override
	public void dispose() {
		InstanceValidationService ivs = (InstanceValidationService) PlatformUI.getWorkbench().getService(InstanceValidationService.class);
		ivs.removeListener(listener);
		super.dispose();
	}
}
