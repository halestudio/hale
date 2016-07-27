/*
 * Copyright (c) 2016 Fraunhofer IGD
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
 *     Fraunhofer IGD <http://www.igd.fraunhofer.de/>
 */

package de.fhg.igd.mapviewer.concurrency;

import org.eclipse.core.runtime.jobs.ISchedulingRule;

/**
 * Exclusive execution of jobs with equal name
 * 
 * @author Simon Templer
 */
public class ExclusiveSchedulingRule implements ISchedulingRule {

	private String name;

	/**
	 * Constructor
	 * 
	 * @param name the rule name
	 */
	public ExclusiveSchedulingRule(String name) {
		super();
		this.name = name;
	}

	/**
	 * @see ISchedulingRule#contains(ISchedulingRule)
	 */
	@Override
	public boolean contains(ISchedulingRule rule) {
		return false;
	}

	/**
	 * @see ISchedulingRule#isConflicting(ISchedulingRule)
	 */
	@Override
	public boolean isConflicting(ISchedulingRule rule) {
		if (rule instanceof ExclusiveSchedulingRule) {
			return ((ExclusiveSchedulingRule) rule).name.equals(name);
		}
		else {
			return false;
		}
	}

}
