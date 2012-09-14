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
package eu.esdihumboldt.hale.io.oml.internal.goml.align;

import java.util.ArrayList;
import java.util.List;

import eu.esdihumboldt.hale.io.oml.internal.goml.rdf.About;
import eu.esdihumboldt.hale.io.oml.internal.model.align.IAlignment;
import eu.esdihumboldt.hale.io.oml.internal.model.align.ICell;
import eu.esdihumboldt.hale.io.oml.internal.model.align.ISchema;
import eu.esdihumboldt.hale.io.oml.internal.model.align.ext.IValueClass;
import eu.esdihumboldt.hale.io.oml.internal.model.rdf.IAbout;

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
	 * A {@link List} of all values classes (attribute values grouped into
	 * categories for efficient re-use)
	 */

	private List<IValueClass> valueClasses;

	/**
	 * Create a deep copy of the alignment.
	 * 
	 * @return a copy of the alignment
	 */
	public Alignment deepCopy() {
		Alignment result = new Alignment();
		result.setAbout(new About(this.getAbout().getAbout()));
		result.setLevel(this.getLevel());
		Schema schema1 = new Schema(this.level, (Formalism) this.getSchema1().getFormalism());
		schema1.setAbout(new About(this.getSchema1().getAbout().getAbout()));
		result.setSchema1(schema1);

		Schema schema2 = new Schema(this.level, (Formalism) this.getSchema2().getFormalism());
		schema2.setAbout(new About(this.getSchema2().getAbout().getAbout()));
		result.setSchema2(schema2);

		List<ICell> cells = new ArrayList<ICell>();
		for (ICell cell : this.getMap()) {
			cells.add(((Cell) cell).deepCopy());
		}
		result.setMap(cells);
		return result;
	}

	// getters / setters .......................................................

	/**
	 * @return the schema1
	 */
	@Override
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
	@Override
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
	@Override
	public List<ICell> getMap() {
		if (this.map == null) {
			this.map = new ArrayList<ICell>();
		}
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
	@Override
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
	@Override
	public IAbout getAbout() {
		return about;
	}

	/**
	 * @param about the about to set
	 */
	public void setAbout(IAbout about) {
		this.about = about;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.esdihumboldt.cst.align.IAlignment#getValueClasses()
	 */
	@Override
	public List<IValueClass> getValueClasses() {
		return this.valueClasses;
	}

	/**
	 * @param valueClass list of value class
	 */
	public void setValueClass(List<IValueClass> valueClass) {
		this.valueClasses = valueClass;
	}

}
