/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.views.properties.entity;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.PlatformUI;
import org.geotools.filter.text.cql2.CQLException;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.filter.FilterGeoCqlImpl;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionService;

/**
 * Add a condition context on an entity based on a value list.
 * 
 * @author Simon Templer
 */
public class AddConditionAction extends Action {

	private static final ALogger log = ALoggerFactory.getLogger(AddConditionAction.class);

	private final List<String> values;
	private final EntityDefinition entity;

	/**
	 * Create an action for creating a condition context.
	 * 
	 * @param entity the entity to create a context for
	 * @param values the values the condition should match
	 */
	public AddConditionAction(EntityDefinition entity, List<String> values) {
		super("Create condition context", AS_PUSH_BUTTON);

		this.entity = entity;
		this.values = values;
	}

	/**
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		EntityDefinitionService eds = (EntityDefinitionService) PlatformUI.getWorkbench()
				.getService(EntityDefinitionService.class);

		// create filter
		StringBuilder filterTerm = new StringBuilder();
		boolean first = true;
		for (String value : values) {
			if (first) {
				first = false;
			}
			else {
				filterTerm.append(" or ");
			}
			filterTerm.append("value = '");
			filterTerm.append(value);
			filterTerm.append('\'');
		}

		Filter filter;
		try {
			filter = new FilterGeoCqlImpl(filterTerm.toString());
		} catch (CQLException e) {
			log.userError("Error creating condition", e);
			return;
		}

		// add condition context
		eds.addConditionContext(entity, filter);
	}

}
