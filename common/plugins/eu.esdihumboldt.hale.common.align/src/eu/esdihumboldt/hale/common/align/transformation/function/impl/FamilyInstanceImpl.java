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

package eu.esdihumboldt.hale.common.align.transformation.function.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import eu.esdihumboldt.hale.common.instance.model.FamilyInstance;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.impl.InstanceDecorator;

/**
 * Decorate a given instance with family function.
 *
 * @author Kai Schwierczek
 */
public class FamilyInstanceImpl extends InstanceDecorator implements FamilyInstance {
	private Collection<FamilyInstance> children;

	/**
	 * Decorate the given instance with family function.
	 * 
	 * @param instance the instance to decorate
	 */
	public FamilyInstanceImpl(Instance instance) {
		super(instance);
		children = new ArrayList<FamilyInstance>();
	}

	/**
	 * @see FamilyInstance#getChildren()
	 */
	@Override
	public Collection<FamilyInstance> getChildren() {
		return Collections.unmodifiableCollection(children);
	}

	/**
	 * 
	 * @see FamilyInstance#addChild(FamilyInstance)
	 */
	@Override
	public void addChild(FamilyInstance child) {
		children.add(child);
	}
}
