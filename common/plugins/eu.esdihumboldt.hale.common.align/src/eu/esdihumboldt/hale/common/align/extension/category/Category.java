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

package eu.esdihumboldt.hale.common.align.extension.category;

import net.jcip.annotations.Immutable;
import de.cs3d.util.eclipse.extension.simple.IdentifiableExtension.Identifiable;

/**
 * Represents a function category. Usually provided through the corresponding
 * extension point.
 * 
 * @author Simon Templer
 */
@Immutable
public final class Category implements Identifiable {

	private final String id;
	private final String name;
	private final String description;

	/**
	 * Create a function category
	 * 
	 * @param id the category id
	 * @param name the category name
	 * @param description the category description
	 */
	public Category(String id, String name, String description) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
	}

	/**
	 * @return the category id
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * @return the category name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the category description
	 */
	public String getDescription() {
		return description;
	}

}
