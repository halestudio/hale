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
	public PronunciationOfName clone() throws CloneNotSupportedException {
		return (PronunciationOfName) super.clone();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pronunciationIPA == null) ? 0 : pronunciationIPA.hashCode());
		result = prime * result
				+ ((pronunciationSoundLink == null) ? 0 : pronunciationSoundLink.hashCode());
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
		PronunciationOfName other = (PronunciationOfName) obj;
		if (pronunciationIPA == null) {
			if (other.pronunciationIPA != null)
				return false;
		}
		else if (!pronunciationIPA.equals(other.pronunciationIPA))
			return false;
		if (pronunciationSoundLink == null) {
			if (other.pronunciationSoundLink != null)
				return false;
		}
		else if (!pronunciationSoundLink.equals(other.pronunciationSoundLink))
			return false;
		return true;
	}

}
