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

import java.util.List;

import de.cs3d.util.eclipse.extension.ExtensionObjectFactory;
import eu.esdihumboldt.hale.ui.views.styledmap.clip.layout.PainterLayout;

/**
 * Interface for {@link PainterLayout} factories
 * 
 * @author Simon Templer
 */
public interface PainterLayoutFactory extends ExtensionObjectFactory<PainterLayout> {

	/**
	 * Get the painters to be layouted.
	 * 
	 * @return a list with a proxy for each painter
	 */
	public List<PainterProxy> getPaintersToLayout();

}
