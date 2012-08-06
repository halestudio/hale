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

package eu.esdihumboldt.hale.common.instance.helper.population;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.instance.helper.InstanceTraversalCallback;
import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.Population;

/**
 * Counts how often types and properties are populated by increasing the
 * corresponding {@link Population} constraint when applied to an instance. 
 * @author Simon Templer
 */
public class PopulationCounter implements InstanceTraversalCallback {

	/**
	 * @see InstanceTraversalCallback#visit(Instance, QName)
	 */
	@Override
	public boolean visit(Instance instance, QName name) {
		// increase type count
		instance.getDefinition().getConstraint(Population.class).increase();
		
		countProperties(instance);
		
		return true;
	}

	/**
	 * @see InstanceTraversalCallback#visit(Group, QName)
	 */
	@Override
	public boolean visit(Group group, QName name) {
		countProperties(group);
		
		return true;
	}

	/**
	 * Do the population count for a group's properties.
	 * @param group the group
	 */
	private void countProperties(Group group) {
		for (QName propertyName : group.getPropertyNames()) {
			ChildDefinition<?> child = group.getDefinition().getChild(propertyName);
			if (child != null) {
				// property is populated in X instances
				if (child.asProperty() != null) {
					child.asProperty().getConstraint(Population.class).increase();
				}
				else if (child.asGroup() != null) {
					child.asGroup().getConstraint(Population.class).increase();
				}
			}
		}
	}

	/**
	 * @see InstanceTraversalCallback#visit(Object, QName)
	 */
	@Override
	public boolean visit(Object value, QName name) {
		return true;
	}

}
