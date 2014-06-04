/*
 * Copyright (c) 2014 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.common.graph.labels;

import org.eclipse.draw2d.IFigure;
import org.eclipse.zest.core.viewers.IFigureProvider;

import eu.esdihumboldt.hale.common.core.service.ServiceProvider;
import eu.esdihumboldt.hale.ui.common.graph.figures.EntityFigure;
import eu.esdihumboldt.hale.ui.util.graph.shapes.StretchedHexagon;

/**
 * This label provider is used to show String Elements as a Hexagon shaped
 * Figure.
 * 
 * @author Yasmina Kammeyer
 */
public class StringGraphLabelProvider extends GraphLabelProvider {

	/**
	 * Default constructor
	 * 
	 * @param provider the service provider that may be needed to obtain cell
	 *            explanations, may be <code>null</code>
	 */
	public StringGraphLabelProvider(ServiceProvider provider) {
		super(provider);
	}

	/**
	 * @see IFigureProvider#getFigure(Object)
	 */
	@Override
	public IFigure getFigure(Object element) {
		if (element instanceof String) {
			return new EntityFigure(new StretchedHexagon(10), null, null, getCustomFigureFont());
		}
		return super.getFigure(element);
	}

}
