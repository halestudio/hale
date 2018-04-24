/*
 * Copyright (c) 2013 Simon Templer
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
 *     Simon Templer - initial version
 */

package eu.esdihumboldt.hale.common.schema.groovy.constraints

import java.util.regex.Matcher
import java.util.regex.Pattern

import eu.esdihumboldt.hale.common.schema.model.Definition
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode


/**
 * Factory for cardinality constraints. A cardinality can be defined as a
 * {@link Range}, an integer for a fixed size, <code>+</code>, <code>*</code>,
 * <code>?</code> or through a string with a pattern like <code>from..to</code>,
 * where <code>unbounded</code>, <code>n</code> or <code>*</code> stands for an
 * unbounded maximum occurrence.
 * 
 * @author Simon Templer
 */
@Singleton
@CompileStatic
class CardinalityFactory extends OptionalContextConstraintFactory<Cardinality> {

	/**
	 * Pattern for catching minimal and maximal occurrence.
	 */
	private static final Pattern pattern = ~/^(\d+)[\.\-]{1,3}(\w+|\*)$/

	@Override
	Cardinality createConstraint(Object arg, Definition context) {
		if (arg instanceof Range) {
			// defined as a Range
			return createFromRange((Range) arg)
		}
		else if (arg instanceof Number) {
			// defined with a single number
			return Cardinality.get(arg.longValue(), arg.longValue())
		}
		else {
			// defined as something else
			String text = arg.toString()

			switch (text) {
				case '+':
					return Cardinality.CC_AT_LEAST_ONCE
				case '*':
					return Cardinality.CC_ANY_NUMBER
				case '?':
					return Cardinality.CC_OPTIONAL
			}

			Matcher matcher = pattern.matcher(text)
			if (matcher.find()) {
				long from = matcher.group(1) as long
				String strTo = matcher.group(2)
				long to

				switch (strTo) {
					case 'unbounded':
					case '*':
					case 'n':
						to = Cardinality.UNBOUNDED
						break
					default:
						to = strTo as long
						break
				}

				return Cardinality.get(from, to)
			}
		}

		throw new IllegalArgumentException(
		'Invalid argument to create cardinality constraint from: ' + arg)
	}

	@CompileStatic(TypeCheckingMode.SKIP)
	private Cardinality createFromRange(Range range) {
		return Cardinality.get(range.from, range.to)
	}

}
