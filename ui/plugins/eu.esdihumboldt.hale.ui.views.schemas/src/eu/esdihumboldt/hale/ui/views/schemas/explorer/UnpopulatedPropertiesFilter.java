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

package eu.esdihumboldt.hale.ui.views.schemas.explorer;

import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
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

	/**
	 * Default constructor
	 */
	public UnpopulatedPropertiesFilter() {
		super();

		ps = (PopulationService) PlatformUI.getWorkbench().getService(PopulationService.class);
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
