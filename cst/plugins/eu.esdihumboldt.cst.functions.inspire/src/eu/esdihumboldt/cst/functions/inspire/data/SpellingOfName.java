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
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public boolean equals(SpellingOfName target) {
		if (target == null)
			return false;
		if (text != null && text.equals(target.getText()) == false)
			return false;
		else if (text == null && target.getText() != null)
			return false;

		if (script != null && script.equals(target.getScript()) == false)
			return false;
		else if (script == null && target.getScript() != null)
			return false;

		if (transliterationScheme != null
				&& transliterationScheme.equals(target.getTransliterationScheme()) == false)
			return false;
		else if (transliterationScheme == null && target.getTransliterationScheme() != null)
			return false;
		return true;
	}
}
