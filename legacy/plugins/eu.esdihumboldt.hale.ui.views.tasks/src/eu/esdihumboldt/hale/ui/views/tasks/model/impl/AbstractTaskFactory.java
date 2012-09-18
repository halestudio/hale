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

package eu.esdihumboldt.hale.ui.views.tasks.model.impl;

import eu.esdihumboldt.hale.ui.views.tasks.model.TaskFactory;

/**
 * Abstract task factory
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public abstract class AbstractTaskFactory implements TaskFactory {
	
	private String prefix = null;
	
	private final String baseTypeName;

	/**
	 * Creates a new task factory
	 * 
	 * @param baseTypeName the base type name
	 */
	public AbstractTaskFactory(String baseTypeName) {
		super();
		this.baseTypeName = baseTypeName;
	}

	/**
	 * @see TaskFactory#setTypeNamePrefix(String)
	 */
	@Override
	public void setTypeNamePrefix(String prefix) {
		this.prefix = prefix;
	}
	
	/**
	 * @see TaskFactory#getTaskTypeName()
	 */
	@Override
	public String getTaskTypeName() {
		if (prefix == null) {
			return baseTypeName;
		}
		else {
			return prefix + baseTypeName;
		}
	}

}
