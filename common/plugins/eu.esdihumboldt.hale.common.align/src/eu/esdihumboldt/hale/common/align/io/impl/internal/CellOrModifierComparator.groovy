/*
 * Copyright (c) 2016 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.common.align.io.impl.internal

import javax.xml.namespace.QName

import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.AbstractEntityType
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.CellType
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ChildContextType
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ClassType
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ConditionType
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ModifierType
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.NamedEntityType
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.PropertyType
import groovy.transform.CompileStatic


/**
 * Comparator for sorting cells and modifiers.
 * 
 * Goal of this comparator is to have a reproducable order that makes
 * comparison in a diff of the resulting XML file easier.
 * 
 * @author Simon Templer
 */
@Singleton
@CompileStatic
class CellOrModifierComparator implements Comparator<Object> {

	@Override
	int compare(Object o1, Object o2) {
		// handle generic cases
		if (o1 == o2) {
			return 0
		}
		if (o1 == null) {
			// null last
			return 1
		}
		if (o2 == null) {
			// null last
			return -1
		}

		// type specific comparisons

		if (o1 instanceof CellType && o2 instanceof CellType) {
			return compareCell(o1, o2)
		}

		if (o1 instanceof ModifierType && o2 instanceof ModifierType) {
			return compareModifier(o1, o2)
		}

		if (o1 instanceof CellType) {
			// cells first
			return -1
		}

		if (o2 instanceof CellType) {
			// cells first
			return 1
		}

		// fall-back: order by class name
		return o1.class.name <=> o2.class.name
	}

	int compareCell(CellType o1, CellType o2) {
		/*
		 * 1. compare targets
		 * 
		 * The reason behind this is the assumption that most changes will be:
		 * - Changing the configuration of an existing cell (w/o changing source or target)
		 * - Determine the target value in a different way (different function and/or different sources)
		 */
		int cmp = compareEntities(o1.target, o2.target)

		if (cmp == 0) {
			/*
			 * 2. compare used transformation functions
			 * 
			 * The reason behind this is that it is most critical to have the
			 * same position for a cell if the transformation function is not
			 * changed (to have a proper diff of the changes inside).
			 */
			cmp = o1.relation <=> o2.relation
		}

		if (cmp == 0) {
			// 3. compare cell ID -> reproducible order if there are no changes
			cmp = o1.id <=> o2.id
		}

		return cmp
	}

	private int compareEntities(List<NamedEntityType> o1, List<NamedEntityType> o2) {
		// handle generic cases
		if (o1 == o2) {
			return 0
		}
		if (o1 == null) {
			// null last
			return 1
		}
		if (o2 == null) {
			// null last
			return -1
		}

		// sort lists
		def list1 = o1.sort(false) { NamedEntityType a, NamedEntityType b ->
			compareEntity(a, b)
		}
		def list2 = o2.sort(false) { NamedEntityType a, NamedEntityType b ->
			compareEntity(a, b)
		}

		int commonLength = Math.min(list1.size(), list2.size())

		for (i in 0..(commonLength - 1)) {
			int cmp = compareEntity(list1[i], list2[i])
			if (cmp != 0) {
				return cmp
			}
		}

		// equal up to common length
		if (list1.size() > commonLength) {
			return 1
		}
		else if (list2.size() > commonLength) {
			return -1
		}
		else {
			// same length -> same entities
			return 0
		}
	}

	private int compareEntity(NamedEntityType o1, NamedEntityType o2) {
		AbstractEntityType e1 = o1.abstractEntity?.value
		AbstractEntityType e2 = o1.abstractEntity?.value

		// handle generic cases
		if (e1 == e2) {
			return 0
		}
		if (e1 == null) {
			// null last
			return 1
		}
		if (e2 == null) {
			// null last
			return -1
		}

		// can't compare anything that is not a ClassType (or PropertyType)
		if (!(e1 instanceof ClassType) && !(e2 instanceof ClassType)) {
			return 0
		}
		if (!(e1 instanceof ClassType)) {
			return 1
		}
		if (!(e2 instanceof ClassType)) {
			return -1
		}

		// compare class
		int cmp = compareClass((ClassType) e1, (ClassType) e2)

		if (cmp == 0) {
			if (e1 instanceof PropertyType && e2 instanceof PropertyType) {
				// compare properties
				cmp = compareProperties(e1.getChild(), e2.getChild())
			}
			else if (e1 instanceof PropertyType) {
				// type only first
				cmp = 1
			}
			else if (e2 instanceof PropertyType) {
				// type only first
				cmp = -1
			}
		}

		return cmp
	}

	private int compareProperties(List<ChildContextType> list1, List<ChildContextType> list2) {
		int commonLength = Math.min(list1.size(), list2.size())

		for (i in 0..(commonLength - 1)) {
			int cmp = compareChildContext(list1[i], list2[i])
			if (cmp != 0) {
				return cmp
			}
		}

		// equal up to common length
		if (list1.size() > commonLength) {
			return 1
		}
		else if (list2.size() > commonLength) {
			return -1
		}
		else {
			// same length -> same path
			return 0
		}

		return 0
	}

	private int compareChildContext(ChildContextType o1, ChildContextType o2) {
		QName n1 = new QName(o1.ns, o1.name?:'')
		QName n2 = new QName(o2.ns, o2.name?:'')

		// compare name
		int cmp = compareQName(n1, n2)

		// compare index
		if (cmp == 0) {
			cmp = compareBigInt(o1.index, o2.index)
		}

		// compare context
		if (cmp == 0) {
			cmp = compareBigInt(o1.context, o2.context)
		}

		// compare condition
		if (cmp == 0) {
			cmp = compareCondition(o1.condition, o2.condition)
		}

		cmp
	}

	private int compareBigInt(BigInteger i1, BigInteger i2) {
		// handle generic cases
		if (i1 == i2) {
			return 0
		}
		if (i1 == null) {
			// null last
			return 1
		}
		if (i2 == null) {
			// null last
			return -1
		}

		return i1 <=> i2
	}

	private int compareClass(ClassType o1, ClassType o2) {
		QName n1 = new QName(o1.type?.ns, o1.type?.name?:'')
		QName n2 = new QName(o2.type?.ns, o2.type?.name?:'')

		// compare name
		int cmp = compareQName(n1, n2)

		if (cmp == 0) {
			// compare condition
			cmp = compareCondition(o1.type.condition, o2.type.condition)
		}

		cmp
	}

	private int compareCondition(ConditionType c1, ConditionType c2) {
		int cmp = (c1?.value?:'') <=> (c2?.value?:'')
		if (cmp == 0) {
			cmp = (c1?.lang?:'') <=> (c2?.lang?:'')
		}
		cmp
	}

	private int compareQName(QName n1, QName n2) {
		// first compare local part (assuming a namespace is more prone to change than a name)
		int cmp = n1.localPart <=> n2.localPart
		if (cmp == 0) {
			// then the namespace
			cmp = n1.namespaceURI <=> n2.namespaceURI
		}

		cmp
	}

	int compareModifier(ModifierType o1, ModifierType o2) {
		// sort modifiers by cell ID
		return o1.cell <=> o2.cell
	}
}
