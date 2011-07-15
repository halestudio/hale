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

package eu.esdihumboldt.hale.align.model.impl;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import eu.esdihumboldt.hale.align.model.Cell;
import eu.esdihumboldt.hale.align.model.Entity;

/**
 * Default implementation of an alignment cell
 * @author Simon Templer
 */
public class DefaultCell implements Cell {

	private Multimap<String, ? extends Entity> source;
	private Multimap<String, ? extends Entity> target;
	private Multimap<String, String> parameters;
	private String transformation;

	/**
	 * Set the identifier for the transformation referenced by the cell.
	 * @param transformation the transformation identifier
	 */
	public void setTransformationIdentifier(String transformation) {
		this.transformation = transformation;
	}

	/**
	 * @param parameters the parameters to set
	 */
	public void setTransformationParameters(Multimap<String, String> parameters) {
		this.parameters = parameters;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(Multimap<String, ? extends Entity> source) {
		this.source = source;
	}

	/**
	 * @param target the target to set
	 */
	public void setTarget(Multimap<String, ? extends Entity> target) {
		this.target = target;
	}

	/**
	 * @see Cell#getSource()
	 */
	@Override
	public Multimap<String, ? extends Entity> getSource() {
		return Multimaps.unmodifiableMultimap(source);
	}

	/**
	 * @see Cell#getTarget()
	 */
	@Override
	public Multimap<String, ? extends Entity> getTarget() {
		return Multimaps.unmodifiableMultimap(target);
	}

	/**
	 * @see Cell#getTransformationParameters()
	 */
	@Override
	public Multimap<String, String> getTransformationParameters() {
		return Multimaps.unmodifiableMultimap(parameters);
	}

	/**
	 * @see Cell#getTransformationIdentifier()
	 */
	@Override
	public String getTransformationIdentifier() {
		return transformation;
	}

}
