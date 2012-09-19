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
