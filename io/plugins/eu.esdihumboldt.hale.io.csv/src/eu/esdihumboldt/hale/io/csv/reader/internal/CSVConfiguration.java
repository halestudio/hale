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

package eu.esdihumboldt.hale.io.csv.reader.internal;

import eu.esdihumboldt.hale.common.schema.model.Constraint;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;
import eu.esdihumboldt.hale.io.csv.reader.CSVConstants;
import net.jcip.annotations.Immutable;

/**
 * CSVConfiguration represented as TypeConstraints
 * 
 * @author Kevin Mais
 */
@Immutable
@Constraint(mutable = false)
public class CSVConfiguration implements TypeConstraint, CSVConstants {

	private final char separator;

	private final char quote;

	private final char escape;

	private final int skipNlines;

	/**
	 * default constructor
	 */

	public CSVConfiguration() {
		this(DEFAULT_SEPARATOR, DEFAULT_QUOTE, DEFAULT_ESCAPE, 0);
	}

	/**
	 * constructor for CSVConfiguration
	 * 
	 * @param sep the separating sign
	 * @param qu the quote sign
	 * @param esc the escape sign
	 * @param skip the boolean to skip the first line or not
	 */
	public CSVConfiguration(char sep, char qu, char esc, int skipNlines) {
		this.separator = sep;
		this.quote = qu;
		this.escape = esc;
		this.skipNlines = skipNlines;
	}

	/**
	 * @return the separator
	 */
	public char getSeparator() {
		return separator;
	}

	/**
	 * @return the quote
	 */
	public char getQuote() {
		return quote;
	}

	/**
	 * @return the escape
	 */
	public char getEscape() {
		return escape;
	}

	/**
	 * @return the skipNlines
	 */
	public int skipNlines() {
		return skipNlines;
	}

	/**
	 * @see TypeConstraint#isInheritable()
	 */
	@Override
	public boolean isInheritable() {
		// must be set explicitly
		return false;
	}

}
