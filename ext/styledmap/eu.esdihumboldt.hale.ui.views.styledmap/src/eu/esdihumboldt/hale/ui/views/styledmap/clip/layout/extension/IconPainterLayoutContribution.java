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

import de.cs3d.util.eclipse.extension.ExtensionObjectFactoryCollection;
import de.cs3d.util.eclipse.extension.FactoryFilter;
import eu.esdihumboldt.hale.ui.views.styledmap.clip.layout.PainterLayout;

/**
 * Only lists {@link PainterLayout}s with an associated icon.
 * 
 * @author Simon Templer
 */
public class IconPainterLayoutContribution extends PainterLayoutContribution {

	/**
	 * Default constructor
	 */
	public IconPainterLayoutContribution() {
		super();

		setFilter(new FactoryFilter<PainterLayout, PainterLayoutFactory>() {

			@Override
			public boolean acceptFactory(PainterLayoutFactory factory) {
				return factory.getIconURL() != null;
			}

			@Override
			public boolean acceptCollection(
					ExtensionObjectFactoryCollection<PainterLayout, PainterLayoutFactory> collection) {
				return true;
			}
		});
	}

}
