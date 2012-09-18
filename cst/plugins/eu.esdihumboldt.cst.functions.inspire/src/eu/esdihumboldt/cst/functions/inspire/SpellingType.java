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
	 * @param transliteration the transliteration to set
	 */
	public void setTransliteration(String transliteration) {
		this.transliteration = transliteration;
	}

	/**
	 * Constructor
	 * 
	 * @param property the property definition to set
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
	 * @param script the script to set
	 */
	public void setScript(String script) {
		this.script = script;
	}

}
