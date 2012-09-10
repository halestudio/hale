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

package eu.esdihumboldt.hale.io.csv.reader.internal;

import net.jcip.annotations.Immutable;
import eu.esdihumboldt.hale.common.schema.model.Constraint;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;

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

	private final boolean skip_first_line;

	/**
	 * default constructor
	 */

	public CSVConfiguration() {
		this(DEFAULT_SEPARATOR, DEFAULT_QUOTE, DEFAULT_ESCAPE, false);
	}

	/**
	 * constructor for CSVConfiguration
	 * 
	 * @param sep the separating sign
	 * @param qu the quote sign
	 * @param esc the escape sign
	 * @param skip the boolean to skip the first line or not
	 */
	public CSVConfiguration(char sep, char qu, char esc, boolean skip) {
		this.separator = sep;
		this.quote = qu;
		this.escape = esc;
		this.skip_first_line = skip;
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
	 * @return the skip_first_line
	 */
	public boolean skipFirst() {
		return skip_first_line;
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
