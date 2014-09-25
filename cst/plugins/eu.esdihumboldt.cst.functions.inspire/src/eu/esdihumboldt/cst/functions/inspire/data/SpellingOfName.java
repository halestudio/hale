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

package eu.esdihumboldt.cst.functions.inspire.data;

/**
 * This is the INPSIRE SpellingOfName object implementation
 * 
 * @author Jose Ignacio Gisbert
 * @partner 02 / ETRA Research and Development
 */
@SuppressWarnings("javadoc")
public class SpellingOfName implements Cloneable {

	private String text = null;
	private String script = null;
	private String transliterationScheme = null;

	public SpellingOfName() {
	}

	public String getText() {
		return text;
	}

	public void setText(String t) {
		text = t;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String s) {
		script = s;
	}

	public String getTransliterationScheme() {
		return transliterationScheme;
	}

	public void setTransliterationScheme(String t) {
		transliterationScheme = t;
	}

	@Override
	public SpellingOfName clone() throws CloneNotSupportedException {
		return (SpellingOfName) super.clone();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((script == null) ? 0 : script.hashCode());
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		result = prime * result
				+ ((transliterationScheme == null) ? 0 : transliterationScheme.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SpellingOfName other = (SpellingOfName) obj;
		if (script == null) {
			if (other.script != null)
				return false;
		}
		else if (!script.equals(other.script))
			return false;
		if (text == null) {
			if (other.text != null)
				return false;
		}
		else if (!text.equals(other.text))
			return false;
		if (transliterationScheme == null) {
			if (other.transliterationScheme != null)
				return false;
		}
		else if (!transliterationScheme.equals(other.transliterationScheme))
			return false;
		return true;
	}

}
