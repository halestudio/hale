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
