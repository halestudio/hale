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

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.filter.FilterGeoCqlImpl;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionService;

/**
 * Add condition contexts on an entity based on a value list.
 * 
 * @author Simon Templer
 */
public class AddConditionAction extends Action {

	private static final ALogger log = ALoggerFactory.getLogger(AddConditionAction.class);

	private final List<String> values;
	private final EntityDefinition entity;
	private final boolean combine;

	/**
	 * Create an action for creating condition contexts
	 * 
	 * @param entity the entity to create contexts for
	 * @param values the values the condition should match
	 * @param combine if the values should be combined to one condition
	 */
	public AddConditionAction(EntityDefinition entity, List<String> values, boolean combine) {
		super("Create condition context"
				+ (combine ? " (combined)" : (values.size() > 1 ? "s" : "")), AS_PUSH_BUTTON);

		this.entity = entity;
		this.values = values;
		this.combine = combine;
	}

	/**
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		EntityDefinitionService eds = PlatformUI.getWorkbench()
				.getService(EntityDefinitionService.class);

		// create filter
		if (combine) {
			StringBuilder filterTerm = new StringBuilder();
			boolean first = true;
			for (String value : values) {
				if (first) {
					first = false;
				}
				else {
					filterTerm.append(" or ");
				}
				addSingleValueExpression(filterTerm, value);
			}

			Filter filter;
			try {
				filter = new FilterGeoCqlImpl(filterTerm.toString());
			} catch (CQLException e) {
				log.userError("Error creating condition", e);
				return;
			}

			// add condition context
			eds.addConditionContext(getContextEntity(), filter);
		}
		else {
			for (String value : values) {
				StringBuilder filterTerm = new StringBuilder();
				addSingleValueExpression(filterTerm, value);

				Filter filter;
				try {
					filter = new FilterGeoCqlImpl(filterTerm.toString());
				} catch (CQLException e) {
					log.userError("Error creating condition", e);
					return;
				}

				// add condition context
				eds.addConditionContext(getContextEntity(), filter);
			}
		}
	}

	/**
	 * @return the entity
	 */
	public EntityDefinition getEntity() {
		return entity;
	}

	/**
	 * Add the filter expression for checking a single value to the filter term
	 * 
	 * @param filterTerm the filter term to add the expression to
	 * @param value the value on which should be checked
	 */
	protected void addSingleValueExpression(StringBuilder filterTerm, String value) {
		filterTerm.append(getPropertyReference());
		filterTerm.append(" = '");
		filterTerm.append(value);
		filterTerm.append('\'');
	}

	/**
	 * @return the name by which to reference the entity in the condition
	 */
	protected String getPropertyReference() {
		return "value";
	}

	/**
	 * @return the entity to attach the condition to
	 */
	protected EntityDefinition getContextEntity() {
		return entity;
	}

}
