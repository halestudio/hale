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

package eu.esdihumboldt.hale.ui.io;

import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.core.io.IOAdvisor;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOAdvisor;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfoAware;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;

/**
 * Base class for UI related {@link IOAdvisor}s.
 * 
 * @author Simon Templer
 * @param <T> the I/O provider type
 */
public abstract class DefaultIOAdvisor<T extends IOProvider> extends AbstractIOAdvisor<T> {

	/**
	 * @see AbstractIOAdvisor#prepareProvider(IOProvider)
	 */
	@Override
	public void prepareProvider(T provider) {
		super.prepareProvider(provider);

		if (provider instanceof ProjectInfoAware) {
			ProjectService ps = (ProjectService) PlatformUI.getWorkbench().getService(
					ProjectService.class);
			if (ps != null) {
				((ProjectInfoAware) provider).setProjectInfo(ps.getProjectInfo());
			}
		}
	}

}
