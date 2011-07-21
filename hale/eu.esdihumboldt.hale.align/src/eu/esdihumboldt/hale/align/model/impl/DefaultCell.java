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

import java.util.Collection;
import java.util.Map;

import org.exolab.castor.mapping.MapItem;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;

import eu.esdihumboldt.hale.align.model.Cell;
import eu.esdihumboldt.hale.align.model.Entity;

/**
 * Default implementation of an alignment cell
 * @author Simon Templer
 */
public class DefaultCell implements Cell {

	private ListMultimap<String, ? extends Entity> source;
	private ListMultimap<String, ? extends Entity> target;
	private ListMultimap<String, String> parameters;
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
	public void setTransformationParameters(ListMultimap<String, String> parameters) {
		this.parameters = parameters;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(ListMultimap<String, ? extends Entity> source) {
		this.source = source;
	}

	/**
	 * @param target the target to set
	 */
	public void setTarget(ListMultimap<String, ? extends Entity> target) {
		this.target = target;
	}

	/**
	 * @see Cell#getSource()
	 */
	@Override
	public ListMultimap<String, ? extends Entity> getSource() {
		if (source == null) {
			return null;
		}
		return Multimaps.unmodifiableListMultimap(source);
	}

	/**
	 * @see Cell#getTarget()
	 */
	@Override
	public ListMultimap<String, ? extends Entity> getTarget() {
		if (target == null) {
			return null;
		}
		return Multimaps.unmodifiableListMultimap(target);
	}

	/**
	 * @see Cell#getTransformationParameters()
	 */
	@Override
	public ListMultimap<String, String> getTransformationParameters() {
		if (parameters == null) {
			return null;
		}
		return Multimaps.unmodifiableListMultimap(parameters);
	}

	/**
	 * @see Cell#getTransformationIdentifier()
	 */
	@Override
	public String getTransformationIdentifier() {
		return transformation;
	}

	public Map<String, Collection<String>> getParametersMap() {
		if (parameters == null) {
			return null;
		}
		return parameters.asMap();
	}
	
	public void addParameters(String name, Collection<String> params) {
		if (parameters == null) {
			parameters = LinkedListMultimap.create();
		}
		parameters.putAll(name, params);
	}
	
	public void addParameters(MapItem item) {
		addParameters((String) item.getKey(), (Collection<String>) item.getValue());
	}
	
}
