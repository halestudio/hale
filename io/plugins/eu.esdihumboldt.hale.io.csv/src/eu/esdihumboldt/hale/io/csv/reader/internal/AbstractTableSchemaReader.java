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

package eu.esdihumboldt.hale.io.csv.reader.internal;

import eu.esdihumboldt.hale.common.schema.io.impl.AbstractSchemaReader;

/**
 * Abstract schema reader for tables (e.g. csv/xls files)
 * 
 * @author Patrick Lieb
 */
public abstract class AbstractTableSchemaReader extends AbstractSchemaReader {

	/**
	 * Name of the parameter specifying the property name
	 */
	public static final String PARAM_PROPERTY = "properties";

	/**
	 * Name of the parameter specifying the property type
	 */
	public static final String PARAM_PROPERTYTYPE = "types";

	/**
	 * @return content of the header
	 */
	public abstract String[] getHeaderContent();

}
