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
