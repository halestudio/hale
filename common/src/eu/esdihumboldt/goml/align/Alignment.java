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

import eu.esdihumboldt.cst.align.IAlignment;
import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.ISchema;
import eu.esdihumboldt.cst.align.ext.IValueClass;
import eu.esdihumboldt.cst.rdf.IAbout;


/**
 * The {@link Alignment} is the main document containing all mappings and
 * transformations between two schemas.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class Alignment implements IAlignment {

	/**
	 * Information on the first {@link Schema} being mapped.
	 */
	private ISchema schema1;
	
	/**
	 * Information on the second {@link Schema} being mapped.
	 */
	private ISchema schema2;
	
	/**
	 * A {@link List} of all the mappings defined as part of this 
	 * {@link Alignment}.
	 */
	private List<ICell> map;
	
	/**
	 * TODO add description
	 */
	private String level;
	
	/**
	 * Identifier of this {@link Alignment}.
	 */
	private IAbout about;
	
	/**
	 * A {@link List} of all values classes (attribute values grouped into categories for efficient re-use)
	 */
	
	private List<IValueClass> valueClasses;
	// getters / setters .......................................................

	/**
	 * @return the schema1
	 */
	public ISchema getSchema1() {
		return schema1;
	}

	/**
	 * @param schema1 the schema1 to set
	 */
	public void setSchema1(ISchema schema1) {
		this.schema1 = schema1;
	}

	/**
	 * @return the schema2
	 */
	public ISchema getSchema2() {
		return schema2;
	}

	/**
	 * @param schema2 the schema2 to set
	 */
	public void setSchema2(ISchema schema2) {
		this.schema2 = schema2;
	}

	/**
	 * @return the map
	 */
	public List<ICell> getMap() {
		return map;
	}

	/**
	 * @param map the map to set
	 */
	public void setMap(List<ICell> map) {
		this.map = map;
	}

	/**
	 * @return the level
	 */
	public String getLevel() {
		return level;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(String level) {
		this.level = level;
	}

	/**
	 * @return the about
	 */
	public IAbout getAbout() {
		return about;
	}

	/**
	 * @param about the about to set
	 */
	public void setAbout(IAbout about) {
		this.about = about;
	}

	/* (non-Javadoc)
	 * @see eu.esdihumboldt.cst.align.IAlignment#getValueClasses()
	 */
	public List<IValueClass> getValueClasses() {
		return this.valueClasses;
	}
	
	/*
	 * 
	 * @param list of value class 
	 */
	public void setValueClass(List<IValueClass> valueClass){
		this.valueClasses = valueClass;
		
	}
 
}
