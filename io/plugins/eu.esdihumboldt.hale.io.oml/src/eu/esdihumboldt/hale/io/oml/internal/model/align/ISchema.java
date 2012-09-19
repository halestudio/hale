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

import eu.esdihumboldt.hale.io.oml.internal.model.rdf.IAbout;

/**
 * This interface provides operations which return metadata on a schema.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface ISchema {

	/**
	 * @return the location
	 */
	public String getLocation();

	/**
	 * @return the formalism
	 */
	public IFormalism getFormalism();

	/**
	 * @return the about
	 */
	public IAbout getAbout();

	/**
	 * @return the labels
	 */
	public List<String> getLabels();
}
