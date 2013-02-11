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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import au.com.bytecode.opencsv.CSVReader;
import eu.esdihumboldt.hale.common.core.io.ImportProvider;

/**
 * Utils for the CSVSchemaReader and CSVInstanceReader
 * 
 * @author Kevin Mais
 */
public class CSVUtil implements CSVConstants {

	/**
	 * Reads only the first line of a given CSV file
	 * 
	 * @param provider provider to get the parameters from
	 * @return a reader containing the first line of the CSV file
	 * @throws IOException if an I/O operation fails
	 */
	public static CSVReader readFirst(ImportProvider provider) throws IOException {
		Reader streamReader = new BufferedReader(new InputStreamReader(provider.getSource()
				.getInput(), provider.getCharset()));
		CSVReader reader = new CSVReader(streamReader, getSep(provider), getQuote(provider),
				getEscape(provider));

		return reader;

	}

	/**
	 * Getter for the separating sign
	 * 
	 * @param provider the provider given to the method
	 * @return the separator char
	 */
	public static char getSep(ImportProvider provider) {
		String separator = provider.getParameter(PARAM_SEPARATOR).as(String.class);
		char sep = ((separator == null || separator.isEmpty()) ? (DEFAULT_SEPARATOR) : (separator
				.charAt(0)));

		return sep;
	}

	/**
	 * Getter for the quote sign
	 * 
	 * @param provider the provider given to the method
	 * @return the quote char
	 */
	public static char getQuote(ImportProvider provider) {
		String quote = provider.getParameter(PARAM_QUOTE).as(String.class);
		char qu = (quote == null || quote.isEmpty()) ? (DEFAULT_QUOTE) : (quote.charAt(0));

		return qu;
	}

	/**
	 * Getter for the escape sign
	 * 
	 * @param provider the provider given to the method
	 * @return the escape char
	 */
	public static char getEscape(ImportProvider provider) {
		String escape = provider.getParameter(PARAM_ESCAPE).as(String.class);
		char esc = (escape == null || escape.isEmpty()) ? (DEFAULT_ESCAPE) : (escape.charAt(0));

		return esc;
	}
}
