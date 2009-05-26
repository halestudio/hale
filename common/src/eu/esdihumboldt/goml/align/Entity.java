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

package eu.esdihumboldt.goml.align;

import java.util.List;

import eu.esdihumboldt.goml.oml.ext.Transformation;

/**
 * {@link Entity} is the supertype for all objects that can be mapped in a 
 * {@link Cell}.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class Entity {
	
	/**
	 * Note: Interior element omwg:label collapsed. <xs:element ref="omwg:label"
	 * minOccurs="0" maxOccurs="unbounded" />
	 */
	private List<String> label;
	
	/**
	 * Note: this can be a single Function (transf) or a Service (service) or a pipe of transformations
	 * <xs:group ref="omwg:transformation" minOccurs="0" maxOccurs="1" />
	 */
	private Transformation transformation;
	
	// constructors ............................................................

	/**
	 * @param label
	 */
	public Entity(List<String> label) {
		super();
		this.label = label;
	}

	// getters / setters .......................................................
	
	/**
	 * @return the label
	 */
	public List<String> getLabel() {
		return label;
	}

	/**
	 * @return the transformation
	 */
	public Transformation getTransformation() {
		return transformation;
	}

	/**
	 * @param transformation the transformation to set
	 */
	public void setTransformation(Transformation transformation) {
		this.transformation = transformation;
	}


	/**
	 * @param label the label to set
	 */
	public void setLabel(List<String> label) {
		this.label = label;
	}

}
