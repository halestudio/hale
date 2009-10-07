/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.rcp.views.mapping;

/**
 * Represents a table row
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class TableItem {
	
	private final String name;
	
	private final String[] values;
	
	/**
	 * Constructor
	 * 
	 * @param name the name
	 * @param values the values
	 */
	public TableItem(String name, String... values) {
		this.name = name;
		this.values = values;
	}
	
	/**
	 * Get the value for the given column. For column <code>0<code>
	 * the value will be the item's name
	 * 
	 * @param column the column number
	 * @return the value for the column or <code>null</code> if there
	 *   is no value for the given column 
	 */
	public String getValue(int column) {
		if (column == 0) {
			return name;
		}
		else if (column <= values.length) {
			return values[column - 1];
		}
		
		return null;
	}

}
