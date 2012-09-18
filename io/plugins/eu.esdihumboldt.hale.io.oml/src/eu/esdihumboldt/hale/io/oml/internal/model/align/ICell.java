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
 * A {@link ICell} contains a mapping between two {@link IEntity}s.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
@SuppressWarnings("javadoc")
public interface ICell {

	/**
	 * @return the first {@link IEntity} of the {@link ICell}.
	 */
	public IEntity getEntity1();

	/**
	 * @return the second {@link IEntity} of the {@link ICell}.
	 */
	public IEntity getEntity2();

	/**
	 * @return the relation
	 */
	public RelationType getRelation();

	/**
	 * @return the measure
	 */
	public double getMeasure();

	/**
	 * @return the about
	 */
	public IAbout getAbout();

	/**
	 * Returns a list of labels of the cell. The first label is currently used
	 * to retrieve the operation name which indicates the transformer.
	 * 
	 * @return List of labels.
	 */
	public List<String> getLabel();

	public enum RelationType {
		Equivalence, Subsumes, SubsumedBy, InstanceOf, HasInstance, Disjoint, PartOf, // TODO,
																						// might
																						// have
																						// to
																						// go
																						// elsewhere.
																						// added
																						// by
																						// MdV
		Extra, // TODO, might have to go elsewhere. added by MdV
		Missing // TODO, might have to go elsewhere. added by MdV
	}

}
