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

package eu.esdihumboldt.hale.ui.service.population;

import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.ui.common.service.population.Population;
import eu.esdihumboldt.hale.ui.common.service.population.PopulationService;

/**
 * Filter that hides unpopulated properties (Only works for
 * {@link EntityDefinition} elements).
 * 
 * @author Simon Templer
 */
public class UnpopulatedPropertiesFilter extends ViewerFilter {

	private final PopulationService ps;

	private final boolean filterOnlyPopulatedTypes;

	/**
	 * Default constructor. Filters also properties of unpopulated types.
	 */
	public UnpopulatedPropertiesFilter() {
		this(false);
	}

	/**
	 * Create a filter for unpopulated properties.
	 * 
	 * @param filterOnlyPopulatedTypes if the filter should be applied only for
	 *            populated types
	 */
	public UnpopulatedPropertiesFilter(boolean filterOnlyPopulatedTypes) {
		super();

		this.filterOnlyPopulatedTypes = filterOnlyPopulatedTypes;
		ps = PlatformUI.getWorkbench().getService(PopulationService.class);
	}

	/**
	 * @see ViewerFilter#select(Viewer, Object, Object)
	 */
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (ps != null) {
			if (element instanceof TreePath) {
				element = ((TreePath) element).getLastSegment();
			}

			if (element instanceof EntityDefinition) {
				EntityDefinition entityDef = (EntityDefinition) element;

				if (filterOnlyPopulatedTypes) {
					TypeEntityDefinition type = AlignmentUtil.getTypeEntity(entityDef);
					int typeCount = ps.getPopulation(type).getOverallCount();
					if (typeCount == 0 || typeCount == Population.UNKNOWN) {
						// for types where there is no population counted, show
						// all
						return true;
					}
				}

				if (!entityDef.getPropertyPath().isEmpty()
						// only filter properties
						&& ps.hasPopulation(entityDef.getSchemaSpace())) {
					// only filter if there is a population
					Population pop = ps.getPopulation(entityDef);
					return pop != null && pop.getOverallCount() != 0;
				}
			}
		}

		return true;
	}

}
