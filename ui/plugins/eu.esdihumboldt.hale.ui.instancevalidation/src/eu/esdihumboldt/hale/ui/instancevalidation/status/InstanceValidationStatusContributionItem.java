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

package eu.esdihumboldt.hale.ui.instancevalidation.status;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;

/**
 * Contribution item only delegating it's work to an
 * {@link ActionContributionItem} with the
 * {@link InstanceValidationStatusAction}.
 * 
 * @author Kai Schwierczek
 */
public class InstanceValidationStatusContributionItem extends ContributionItem {

	InstanceValidationStatusAction action;
	ActionContributionItem actionContribution;

	/**
	 * Constructor. Sets id to <code>null</code>.
	 */
	public InstanceValidationStatusContributionItem() {
		super();
		createActionContributionItem();
	}

	/**
	 * Constructor with specified id.
	 * 
	 * @param id the contribution item identifier, or <code>null</code>
	 */
	public InstanceValidationStatusContributionItem(String id) {
		super(id);
		createActionContributionItem();
	}

	@Override
	public void dispose() {
		actionContribution.dispose();
		super.dispose();
	}

	/**
	 * Create the action contribution item, with the action for the instance
	 * validation status.
	 */
	private void createActionContributionItem() {
		action = new InstanceValidationStatusAction();
		actionContribution = new ActionContributionItem(action);
	}

	@Override
	public void fill(Composite parent) {
		actionContribution.fill(parent);
	}

	@Override
	public void fill(CoolBar parent, int index) {
		actionContribution.fill(parent, index);
	}

	@Override
	public void fill(Menu parent, int index) {
		actionContribution.fill(parent, index);
	}

	@Override
	public void fill(ToolBar parent, int index) {
		actionContribution.fill(parent, index);
	}
}
