/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.io.oml.internal.model.align;

import java.util.List;

import eu.esdihumboldt.hale.io.oml.internal.model.align.ext.IValueClass;
import eu.esdihumboldt.hale.io.oml.internal.model.rdf.IAbout;

/**
 * This is the core interface for the description of schema alignments.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface IAlignment {

	/**
	 * @return the schema1
	 */
	public ISchema getSchema1();

	/**
	 * @return the schema2
	 */
	public ISchema getSchema2();

	/**
	 * @return the map
	 */
	public List<ICell> getMap();

	/**
	 * @return the level
	 */
	public String getLevel();

	/**
	 * @return the about
	 */
	public IAbout getAbout();

	/**
	 * @return the list fo ValueClasses
	 */

	public List<IValueClass> getValueClasses();
}
