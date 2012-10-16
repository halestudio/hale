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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.views.data.internal.compare;

import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.ui.service.population.UnpopulatedPropertiesFilter;

/**
 * Instance tree viewer that only show populated properties.
 * 
 * @author Simon Templer
 */
public class PopulatedInstanceTreeViewer extends DefinitionInstanceTreeViewer {

	/**
	 * @see DefinitionInstanceTreeViewer#createControls(Composite,
	 *      SchemaSpaceID)
	 */
	@Override
	public void createControls(Composite parent, SchemaSpaceID schemaSpace) {
		super.createControls(parent, schemaSpace);

		getViewer().setFilters(new ViewerFilter[] { new UnpopulatedPropertiesFilter(true) });
	}

}
