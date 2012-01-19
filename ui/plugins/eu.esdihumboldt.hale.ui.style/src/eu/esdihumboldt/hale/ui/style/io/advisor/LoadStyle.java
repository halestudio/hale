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

package eu.esdihumboldt.hale.ui.style.io.advisor;

import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOAdvisor;
import eu.esdihumboldt.hale.ui.io.DefaultIOAdvisor;
import eu.esdihumboldt.hale.ui.style.io.StyleReader;
import eu.esdihumboldt.hale.ui.style.service.StyleService;

/**
 * Stores loaded styles in the {@link StyleService}.
 * @author Simon Templer
 */
public class LoadStyle extends DefaultIOAdvisor<StyleReader> {

	/**
	 * @see AbstractIOAdvisor#handleResults(IOProvider)
	 */
	@Override
	public void handleResults(StyleReader provider) {
		StyleService ss = (StyleService) PlatformUI.getWorkbench().getService(StyleService.class);
		
		ss.addStyles(provider.getStyles());
		
		super.handleResults(provider);
	}

}
