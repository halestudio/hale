/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
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
