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

package eu.esdihumboldt.hale.ui.util.jobs;

import org.eclipse.core.runtime.jobs.ISchedulingRule;

/**
 * Scheduling rule that is exclusive per equal owner.
 * 
 * @author Simon Templer
 */
public class ExclusiveSchedulingRule implements ISchedulingRule {

	private final Object owner;

	/**
	 * Create a rule for scheduling Jobs exclusively if they have the same
	 * owner.
	 * 
	 * @param owner the rule owner
	 */
	public ExclusiveSchedulingRule(Object owner) {
		super();
		this.owner = owner;
	}

	@Override
	public boolean contains(ISchedulingRule rule) {
		return rule instanceof ExclusiveSchedulingRule
				&& owner == ((ExclusiveSchedulingRule) rule).owner;
	}

	@Override
	public boolean isConflicting(ISchedulingRule rule) {
		return contains(rule);
	}

}