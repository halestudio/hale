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

package eu.esdihumboldt.hale.align.model.condition.impl;

import net.jcip.annotations.Immutable;
import eu.esdihumboldt.hale.align.model.Entity;
import eu.esdihumboldt.hale.align.model.Type;
import eu.esdihumboldt.hale.align.model.condition.EntityCondition;
import eu.esdihumboldt.hale.align.model.condition.TypeCondition;

/**
 * Type condition that checks its binding and element type 
 * @author Simon Templer
 */
@Immutable
public final class BindingCondition implements TypeCondition {

	private final boolean allowCollection;
	private final boolean allowConversion;
	private final Class<?> compatibleClass;

	/**
	 * Create a binding condition
	 * 
	 * @param compatibleClass the class the binding should be compatible to
	 * @param allowConversion specifies if a binding is classified as compatible 
	 *   if conversion to the compatible class is possible
	 * @param allowCollection specifies if a binding is classified as compatible 
	 *   if it is a collection of the compatible class
	 */
	public BindingCondition(Class<?> compatibleClass, boolean allowConversion,
			boolean allowCollection) {
		this.compatibleClass = compatibleClass;
		this.allowConversion = allowConversion;
		this.allowCollection = allowCollection;
	}

	/**
	 * @see EntityCondition#accept(Entity)
	 */
	@Override
	public boolean accept(Type entity) {
		// TODO Auto-generated method stub
		return false;
	}

}
