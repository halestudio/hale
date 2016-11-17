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

package eu.esdihumboldt.hale.common.align.helper;

import java.util.Comparator;
import java.util.Iterator;

import com.google.common.base.Strings;

import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.instance.extension.filter.FilterDefinitionManager;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;

/**
 * Comparator for entity definitions.
 */
public class EntityDefinitionComparator implements Comparator<EntityDefinition> {

	@Override
	public int compare(EntityDefinition o1, EntityDefinition o2) {
		Iterator<ChildContext> o1iterator = o1.getPropertyPath().iterator();
		Iterator<ChildContext> o2iterator = o2.getPropertyPath().iterator();

		while (o1iterator.hasNext() && o2iterator.hasNext()) {
			ChildContext o1c = null;
			PropertyDefinition o1def = null;
			// get first property definition (skip groups)
			while (o1def == null && o1iterator.hasNext()) {
				o1c = o1iterator.next();
				o1def = o1c.getChild().asProperty();
			}
			ChildContext o2c = null;
			PropertyDefinition o2def = null;
			// get first property definition (skip groups)
			while (o2def == null && o2iterator.hasNext()) {
				o2c = o2iterator.next();
				o2def = o2c.getChild().asProperty();
			}

			if (o1def != null && o2def != null) {
				// compare local name
				int comp = o1def.getName().getLocalPart().compareTo(o2def.getName().getLocalPart());
				if (comp == 0) {
					// compare namespace
					comp = o1def.getName().getNamespaceURI()
							.compareTo(o2def.getName().getNamespaceURI());
				}
				if (comp == 0) {
					// compare context
					comp = compareContext(o1c, o2c);
				}
				if (comp != 0) {
					// properties are different
					return comp;
				}
				// properties are the same, continue
			}
			else if (o1def != null && o2def == null) {
				// o2 after o1
				return -1;
			}
			else if (o1def == null && o2def != null) {
				// o1 after o2
				return 1;
			}
		}

		// different path length of properties
		// shorter paths first
		if (o1iterator.hasNext() && !o2iterator.hasNext()) {
			// o1 after o2
			return 1;
		}
		else if (!o1iterator.hasNext() && o2iterator.hasNext()) {
			// o2 after o1
			return -1;
		}

		return 0;
	}

	private int compareContext(ChildContext o1c, ChildContext o2c) {
		boolean hasContext1 = o1c.getCondition() != null || o1c.getContextName() != null
				|| o1c.getIndex() != null;
		boolean hasContext2 = o2c.getCondition() != null || o2c.getContextName() != null
				|| o2c.getIndex() != null;
		if (hasContext1 && !hasContext2) {
			// o1 after o2
			return 1;
		}
		else if (!hasContext1 && hasContext2) {
			// o2 after o1
			return -1;
		}
		else if (!hasContext1 && !hasContext2) {
			return 0;
		}

		// compare index context
		if (o1c.getIndex() != null && o2c.getIndex() != null) {
			if (o1c.getIndex() < o2c.getIndex()) {
				// o2 after o1
				return -1;
			}
			else if (o1c.getIndex() > o2c.getIndex()) {
				// o1 after o2
				return 1;
			}
			else
				return 0;
		}

		// compare named context
		if (o1c.getContextName() != null && o2c.getContextName() != null) {
			if (o1c.getContextName() < o2c.getContextName()) {
				// o2 after o1
				return -1;
			}
			else if (o1c.getContextName() > o2c.getContextName()) {
				// o1 after o2
				return 1;
			}
			else
				return 0;
		}

		// compare condition
		if (o1c.getCondition() != null && o2c.getCondition() != null) {
			if (o1c.getCondition().getFilter() != null && o2c.getCondition().getFilter() != null) {
				String f1 = Strings.nullToEmpty(FilterDefinitionManager.getInstance()
						.asString(o1c.getCondition().getFilter()));
				String f2 = Strings.nullToEmpty(FilterDefinitionManager.getInstance()
						.asString(o2c.getCondition().getFilter()));

				return f1.compareTo(f2);
			}
			else
				return 0;
		}

		// index before condition
		if (o1c.getIndex() != null && o2c.getCondition() != null) {
			// o2 after o1
			return -1;
		}
		else if (o2c.getIndex() != null && o1c.getCondition() != null) {
			// o1 after o2
			return 1;
		}

		return 0;
	}

}