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
