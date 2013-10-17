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

package eu.esdihumboldt.hale.io.xsd.reader.internal.constraint;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Enumeration;

/**
 * Enumeration constraint for type unions
 * 
 * @author Simon Templer
 */
public class UnionEnumeration extends Enumeration<Object> {

	private final Collection<? extends TypeDefinition> unionTypes;

	private boolean initialized = false;

	private Set<Object> values = null;

	private boolean allowOthers = false;

	/**
	 * Create a type union binding constraint
	 * 
	 * @param unionTypes the definitions of the types contained in the union
	 */
	public UnionEnumeration(Collection<? extends TypeDefinition> unionTypes) {
		this.unionTypes = unionTypes;
	}

	private void init() {
		if (!initialized) {
			values = null;
			allowOthers = false;

			for (TypeDefinition type : unionTypes) {
				Enumeration<?> enumeration = type.getConstraint(Enumeration.class);
				if (enumeration.getValues() == null || enumeration.isAllowOthers()) {
					allowOthers = true;
				}

				if (enumeration.getValues() != null) {
					// collect allowed values
					if (values == null) {
						values = new LinkedHashSet<Object>();
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
	 * @see Enumeration#getValues()
	 */
	@Override
	public Collection<? extends Object> getValues() {
		init();

		return values;
	}

	/**
	 * @see Enumeration#isAllowOthers()
	 */
	@Override
	public boolean isAllowOthers() {
		init();

		return allowOthers;
	}

}
