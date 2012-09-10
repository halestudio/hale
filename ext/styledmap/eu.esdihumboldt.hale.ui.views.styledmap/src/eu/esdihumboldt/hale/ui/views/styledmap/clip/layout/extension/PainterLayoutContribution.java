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

package eu.esdihumboldt.hale.ui.views.styledmap.clip.layout.extension;

import org.eclipse.ui.PlatformUI;

import de.cs3d.ui.util.eclipse.extension.AbstractExtensionContribution;
import de.cs3d.ui.util.eclipse.extension.exclusive.ExclusiveExtensionContribution;
import de.cs3d.util.eclipse.extension.exclusive.ExclusiveExtension;
import eu.esdihumboldt.hale.ui.views.styledmap.clip.layout.PainterLayout;

/**
 * {@link PainterLayout} contribution
 * 
 * @author Simon Templer
 */
public class PainterLayoutContribution extends
		ExclusiveExtensionContribution<PainterLayout, PainterLayoutFactory> {

	/**
	 * @see AbstractExtensionContribution#initExtension()
	 */
	@Override
	protected ExclusiveExtension<PainterLayout, PainterLayoutFactory> initExtension() {
		return (PainterLayoutService) PlatformUI.getWorkbench().getService(
				PainterLayoutService.class);
	}

}
