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

import java.net.URI;

/**
 * This is the INPSIRE PronunciationOfName object implementation
 * 
 * @author Jose Ignacio Gisbert
 * @partner 02 / ETRA Research and Development
 */
@SuppressWarnings("javadoc")
public class PronunciationOfName implements Cloneable {

	private URI pronunciationSoundLink = null;
	private String pronunciationIPA = null;

	public PronunciationOfName() {
	}

	public URI getPronunciationSoundLink() {
		return pronunciationSoundLink;
	}

	public void setPronunciationSoundLink(URI ur) {
		pronunciationSoundLink = ur;
	}

	public String getPronunciationIPA() {
		return pronunciationIPA;
	}

	public void setPronunciationIPA(String p) {
		pronunciationIPA = p;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public boolean equals(PronunciationOfName target) {
		if (target == null)
			return false;
		if (pronunciationSoundLink != null
				&& pronunciationSoundLink.equals(target.getPronunciationSoundLink()) == false)
			return false;
		else if (pronunciationSoundLink == null && target.getPronunciationSoundLink() != null)
			return false;

		if (pronunciationIPA != null
				&& pronunciationIPA.equals(target.getPronunciationIPA()) == false)
			return false;
		else if (pronunciationIPA == null && target.getPronunciationIPA() != null)
			return false;

		return true;
	}
}