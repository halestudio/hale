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

package eu.esdihumboldt.hale.ui.views.styledmap.clip.layout.extension;

import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactoryCollection;
import de.fhg.igd.eclipse.util.extension.FactoryFilter;
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
