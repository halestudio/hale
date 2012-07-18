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

package eu.esdihumboldt.cst.functions.inspire;

import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;

/**
 * old inner class of GeographicNamePage
 * 
 * @author Kevin Mais
 */
public class SpellingType {

	/**
	 * name of the source attribute read
	 */
	private final PropertyDefinition property;

	/**
	 * @return the text
	 */
	public PropertyDefinition getProperty() {
		return property;
	}

	/**
	 * script
	 */
	private String script;
	/**
	 * transliteration schema
	 */
	private String transliteration;

	/**
	 * @return the transliteration
	 */
	public String getTransliteration() {
		return transliteration;
	}

	/**
	 * @param transliteration
	 *            the transliteration to set
	 */
	public void setTransliteration(String transliteration) {
		this.transliteration = transliteration;
	}

	/**
	 * Constructor
	 * 
	 * @param property
	 *            the property definition to set
	 */
	public SpellingType(PropertyDefinition property) {
		this.property = property;
	}

	/**
	 * @return the script
	 */
	public String getScript() {
		return script;
	}

	/**
	 * @param script
	 *            the script to set
	 */
	public void setScript(String script) {
		this.script = script;
	}

}
