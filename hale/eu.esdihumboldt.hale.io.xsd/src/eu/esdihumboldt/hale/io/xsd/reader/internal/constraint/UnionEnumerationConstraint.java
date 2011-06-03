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

package eu.esdihumboldt.hale.io.xsd.reader.internal.constraint;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import eu.esdihumboldt.hale.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.schema.model.constraints.type.EnumerationConstraint;

/**
 * Enumeration constraint for type unions
 * @author Simon Templer
 */
public class UnionEnumerationConstraint extends EnumerationConstraint<Object> {

	private Collection<? extends TypeDefinition> unionTypes;
	
	private boolean initialized = false;
	
	private Set<Object> values = null;
	
	private boolean allowOthers = false;

	/**
	 * Create a type union binding constraint
	 * 
	 * @param unionTypes the definitions of the types contained in the union
	 */
	public UnionEnumerationConstraint(Collection<? extends TypeDefinition> unionTypes) {
		this.unionTypes = unionTypes;
	}
	
	private void init() {
		if (!initialized) {
			values = null;
			allowOthers = false;
			
			for (TypeDefinition type : unionTypes) {
				EnumerationConstraint<?> enumeration = type.getConstraint(EnumerationConstraint.class);
				if (enumeration.getValues() == null || enumeration.isAllowOthers()) {
					allowOthers = true;
				}
				
				if (enumeration.getValues() != null) {
					// collect allowed values
					if (values == null) {
						values = new HashSet<Object>();
					}
					values.addAll(enumeration.getValues());
				}
			}
			
			if (values == null) {
				allowOthers = true;
			}
			
			initialized = true;
		}
	}

	/**
	 * @see EnumerationConstraint#getValues()
	 */
	@Override
	public Collection<? extends Object> getValues() {
		init();
		
		return values;
	}

	/**
	 * @see EnumerationConstraint#isAllowOthers()
	 */
	@Override
	public boolean isAllowOthers() {
		init();
		
		return allowOthers;
	}

}
