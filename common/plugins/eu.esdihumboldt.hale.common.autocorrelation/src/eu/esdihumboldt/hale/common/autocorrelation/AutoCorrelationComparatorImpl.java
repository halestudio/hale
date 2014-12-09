/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.autocorrelation;

import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Implementation of the {@link AutoCorrelationComparatorObj} - Interface. This
 * Class implements a basic behavior by only comparing the QName or LocalPart of
 * the QName.
 * 
 * @author Yasmina Kammeyer
 */
public class AutoCorrelationComparatorImpl implements AutoCorrelationComparatorObj {

	/**
	 * @see eu.esdihumboldt.hale.common.autocorrelation.AutoCorrelationComparatorObj#comparator(eu.esdihumboldt.hale.common.schema.model.Definition,
	 *      eu.esdihumboldt.hale.common.schema.model.Definition, boolean)
	 */
	@Override
	public boolean comparator(Definition<?> source, Definition<?> target, boolean ignoreNamespace) {
		if (source == null || target == null) {
			return false;
		}

		if (source instanceof TypeDefinition && target instanceof TypeDefinition) {
			return typeComparator((TypeDefinition) source, (TypeDefinition) target, ignoreNamespace);
		}

		if (source instanceof ChildDefinition<?> && target instanceof ChildDefinition<?>) {
			return childComparator((ChildDefinition<?>) source, (ChildDefinition<?>) target,
					ignoreNamespace);
		}

		return defaultComparator(source, target, ignoreNamespace);
	}

	/**
	 * Comparison of two given {@link Definition}s.
	 * 
	 * @param source The source Definition
	 * @param target The target Definition
	 * @param ignoreNamespace The namespace will be ignored and only the local
	 *            part will be compared
	 * @return true, if the two given Definitions are a match, false otherwise
	 */
	private boolean defaultComparator(Definition<?> source, Definition<?> target,
			boolean ignoreNamespace) {

		if (target.getName().equals(source.getName())) {
			return true;
		}

		if (ignoreNamespace
				&& target.getName().getLocalPart().equals(source.getName().getLocalPart())) {
			return true;
		}

		return false;
	}

	/**
	 * Type Comparator
	 * 
	 * @param source The source TypeDefinition
	 * @param target The target TypeDefinition
	 * @param ignoreNamespace The name space is irrelevant for types to be
	 *            compared
	 * @return true, if the two given TypeDefinitions are a match, false
	 *         otherwise
	 */
	private boolean typeComparator(TypeDefinition source, TypeDefinition target,
			boolean ignoreNamespace) {

		if (target.getName().equals(source.getName())) {
			return true;
		}

		else if (ignoreNamespace
				&& target.getName().getLocalPart().equals(source.getName().getLocalPart())) {
			return true;
		}

		return false;
	}

	/**
	 * Implement this Method to compare two {@link ChildDefinition}s (e.g.
	 * Properties) and return the result.
	 * 
	 * @param sourceChild The source ChildDefinition
	 * @param targetChild The target ChildDefinition
	 * @param ignoreNamespace Indicates if the name space is irrelevant for
	 *            comparison
	 * @return true, if the two given ChildDefinitions are a match, false
	 *         otherwise. A GroupPropertyDefinition is also false.
	 */
	private boolean childComparator(ChildDefinition<?> sourceChild, ChildDefinition<?> targetChild,
			boolean ignoreNamespace) {

		if (!(sourceChild instanceof PropertyDefinition)
				|| !(targetChild instanceof PropertyDefinition)) {
			return false;
		}

		if (targetChild.getName().equals(sourceChild.getName())) {
			return true;
		}

		if (ignoreNamespace
				&& targetChild.getName().getLocalPart()
						.equals(sourceChild.getName().getLocalPart())) {
			return true;
		}

		return false;
	}

}
