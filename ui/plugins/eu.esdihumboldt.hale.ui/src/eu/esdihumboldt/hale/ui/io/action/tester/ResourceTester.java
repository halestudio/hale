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

package eu.esdihumboldt.hale.ui.io.action.tester;

import org.eclipse.core.expressions.PropertyTester;

import eu.esdihumboldt.hale.common.core.io.project.model.Resource;
import eu.esdihumboldt.hale.ui.io.action.ActionUI;
import eu.esdihumboldt.hale.ui.io.action.ActionUIAdvisor;
import eu.esdihumboldt.hale.ui.io.action.ActionUIExtension;

/**
 * Tests on {@link Resource}s based on the associated {@link ActionUIAdvisor}.
 * 
 * @author Simon Templer
 */
public class ResourceTester extends PropertyTester {

	/**
	 * The property namespace for this tester.
	 */
	public static final String NAMESPACE = "eu.esdihumboldt.hale.ui.resource";

	/**
	 * The property that specifies if a cell may be removed.
	 */
	public static final String PROPERTY_CELL_ALLOW_REMOVE = "allow_remove";

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (receiver == null) {
			return false;
		}

		if (property.equals(PROPERTY_CELL_ALLOW_REMOVE) && receiver instanceof Resource) {
			return testAllowRemove((Resource) receiver);
		}

		return false;
	}

	/**
	 * Test if removing the given resource is allowed.
	 * 
	 * @param resource the resource to remove
	 * @return if removing the resource is allowed
	 */
	private boolean testAllowRemove(Resource resource) {
		ActionUIAdvisor<?> advisor = findAdvisor(resource.getActionId());
		if (advisor != null) {
			return advisor.supportsRemoval(resource.getResourceId());
		}
		return false;
	}

	private ActionUIAdvisor<?> findAdvisor(String actionId) {
		ActionUI actionUI = ActionUIExtension.getInstance().findActionUI(actionId);
		if (actionUI != null) {
			return actionUI.getUIAdvisor();
		}
		return null;
	}

}
