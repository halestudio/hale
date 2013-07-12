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
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.filter.FilterGeoCqlImpl;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyDefinition;
import eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionService;

/**
 * Add a condition context on the parent of an entity based on a value list.
 * 
 * @author Simon Templer
 */
public class AddParentConditionAction extends Action {

	private static final ALogger log = ALoggerFactory.getLogger(AddParentConditionAction.class);

	private final List<String> values;
	private final EntityDefinition entity;

	private final EntityDefinition parent;

	/**
	 * Create an action for creating a condition context.
	 * 
	 * @param entity the entity for which parent to create a context for
	 * @param values the values the condition should match
	 */
	public AddParentConditionAction(EntityDefinition entity, List<String> values) {
		super("Create condition context on parent", AS_PUSH_BUTTON);

		// determine parent
		EntityDefinition parent = AlignmentUtil.getParent(entity);
		// ignore groups
		while (parent.getDefinition() instanceof GroupPropertyDefinition) {
			parent = AlignmentUtil.getParent(parent);
		}

		setText("Create condition on parent " + parent.getDefinition().getDisplayName());

		this.parent = parent;
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

		// XXX include namespace?
		String entityName = entity.getDefinition().getName().getLocalPart();

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
			filterTerm.append("value.");
			filterTerm.append(entityName);
			filterTerm.append(" = '");
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

		// add parent condition context
		eds.addConditionContext(parent, filter);
	}
}
