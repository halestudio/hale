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

import java.util.ArrayList;

/**
 * This is the INPSIRE GeographicalName object implementation
 * 
 * @author Ana Belen Anton & Jose Ignacio Gisbert
 * @partner 02 / ETRA Research and Development
 */
@SuppressWarnings("javadoc")
public class GeographicalName {

	private final ArrayList<SpellingOfName> spelling = new ArrayList<SpellingOfName>();
	private String language = null;
	private NativenessValue nativeness = null;
	private NameStatusValue nameStatus = null;
	private String sourceOfName = null;
	private PronunciationOfName pronunciation = null;
	private GrammaticalGenderValue grammaticalGender = null;
	private GrammaticalNumberValue grammaticalNumber = null;
	public final static String sourceOfNameDefaultValue = "Unknown";

	public GeographicalName() {
	}

	public ArrayList<SpellingOfName> getSpellingList() {
		return spelling;
	}

	public int getSpellingCount() {
		return spelling.size();
	}

	public SpellingOfName getSpelling(int n) {
		return spelling.get(n);
	}

	public void setSpelling(SpellingOfName sp) {
		try {
			spelling.clear();
			spelling.add(0, sp.clone());
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}

	public void addSpelling(SpellingOfName sp) {
		try {
			spelling.add(0, sp.clone());
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String lg) {
		language = lg;
	}

	public NativenessValue getNativeness() {
		return nativeness;
	}

	public void setNativeness(NativenessValue nt) {
		nativeness = nt;
	}

	public NameStatusValue getNameStatus() {
		return nameStatus;
	}

	public void setNameStatus(NameStatusValue nm) {
		nameStatus = nm;
	}

	public String getSourceOfName() {
		return sourceOfName;
	}

	public void setSourceOfName(String sn) {
		sourceOfName = sn;
	}

	public PronunciationOfName getPronunciation() {
		return pronunciation;
	}

	public void setPronunciation(PronunciationOfName pr) {
		try {
			pronunciation = pr.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}

	public GrammaticalGenderValue getGrammaticalGender() {
		return grammaticalGender;
	}

	public void setGrammaticalGender(GrammaticalGenderValue gv) {
		grammaticalGender = gv;
	}

	public GrammaticalNumberValue getGrammaticalNumber() {
		return grammaticalNumber;
	}

	public void setGrammaticalNumber(GrammaticalNumberValue gn) {
		grammaticalNumber = gn;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((grammaticalGender == null) ? 0 : grammaticalGender.hashCode());
		result = prime * result + ((grammaticalNumber == null) ? 0 : grammaticalNumber.hashCode());
		result = prime * result + ((language == null) ? 0 : language.hashCode());
		result = prime * result + ((nameStatus == null) ? 0 : nameStatus.hashCode());
		result = prime * result + ((nativeness == null) ? 0 : nativeness.hashCode());
		result = prime * result + ((pronunciation == null) ? 0 : pronunciation.hashCode());
		result = prime * result + ((sourceOfName == null) ? 0 : sourceOfName.hashCode());
		result = prime * result + ((spelling == null) ? 0 : spelling.hashCode());
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
		GeographicalName other = (GeographicalName) obj;
		if (grammaticalGender != other.grammaticalGender)
			return false;
		if (grammaticalNumber != other.grammaticalNumber)
			return false;
		if (language == null) {
			if (other.language != null)
				return false;
		}
		else if (!language.equals(other.language))
			return false;
		if (nameStatus != other.nameStatus)
			return false;
		if (nativeness != other.nativeness)
			return false;
		if (pronunciation == null) {
			if (other.pronunciation != null)
				return false;
		}
		else if (!pronunciation.equals(other.pronunciation))
			return false;
		if (sourceOfName == null) {
			if (other.sourceOfName != null)
				return false;
		}
		else if (!sourceOfName.equals(other.sourceOfName))
			return false;
		if (spelling == null) {
			if (other.spelling != null)
				return false;
		}
		else if (!spelling.equals(other.spelling))
			return false;
		return true;
	}

}
