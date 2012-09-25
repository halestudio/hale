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

import java.util.List;
import java.util.Map;

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyValue;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.AbstractSingleTargetPropertyTransformation;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.NoResultException;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultInstance;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Class for the geographical name function
 * 
 * @author Kevin Mais
 */
public class GeographicalName extends
		AbstractSingleTargetPropertyTransformation<TransformationEngine> implements
		GeographicalNameFunction {

	/**
	 * @see AbstractSingleTargetPropertyTransformation#evaluate(String,
	 *      TransformationEngine, ListMultimap, String,
	 *      PropertyEntityDefinition, Map, TransformationLog)
	 */
	@Override
	protected Object evaluate(String transformationIdentifier, TransformationEngine engine,
			ListMultimap<String, PropertyValue> variables, String resultName,
			PropertyEntityDefinition resultProperty, Map<String, String> executionParameters,
			TransformationLog log) throws TransformationException, NoResultException {

		// list of all source properties
		List<PropertyValue> inputs = variables.get(null);
		if (inputs.isEmpty()) {
			// no input, so don't create any structure
			throw new NoResultException();
		}

		// get all parameters defined by the wizard page
		String ipa = getParameterChecked(PROPERTY_PRONUNCIATIONIPA);
		// we need a default value and a try/catch-block because in older
		// version we couldn't edit the pronunciationSoundLink text field
		String sound = "";
		try {
			sound = getParameterChecked(PROPERTY_PRONUNCIATIONSOUNDLINK);
		} catch (Exception e) {
			// do nothing
		}
		String language = getParameterChecked(PROPERTY_LANGUAGE);
		String sourceOfName = getParameterChecked(PROPERTY_SOURCEOFNAME);
		String nameStatus = getParameterChecked(PROPERTY_NAMESTATUS);
		String nativeness = getParameterChecked(PROPERTY_NATIVENESS);
		String gender = getParameterChecked(PROPERTY_GRAMMA_GENDER);
		String number = getParameterChecked(PROPERTY_GRAMMA_NUMBER);

		// get the script and transliteration parameters
		// should have the same order like source properties
		ListMultimap<String, String> params = getParameters();
		List<String> scripts = params.get(PROPERTY_SCRIPT);
		List<String> trans = params.get(PROPERTY_TRANSLITERATION);

		if (inputs.size() != scripts.size() || inputs.size() != trans.size()) {
			throw new TransformationException(
					"Number of inputs does not match number of configured spellings, can't determine script and transliteration of spellings.");
		}

		// definition of the target property
		TypeDefinition targetType = resultProperty.getDefinition().getPropertyType();

		// instance that can be changed (add property/instance as child)
		DefaultInstance targetInstance = new DefaultInstance(targetType, null);

		// search for the child named "GeographicalName"
		PropertyDefinition targetChildGeoName = Util.getChild("GeographicalName", targetType);

		// get type definition to create the "GeographicalName" instance
		TypeDefinition geoType = targetChildGeoName.getPropertyType();

		// name/GeographicalName/
		DefaultInstance geoInstance = new DefaultInstance(geoType, null);
		targetInstance.addProperty(targetChildGeoName.getName(), geoInstance);

		// name/GeographicalName/grammaticalGender/
		if (gender != null && !gender.isEmpty()) {
			PropertyDefinition geoChildGramGender = Util.getChild("grammaticalGender", geoType);
			TypeDefinition grammarGenderType = geoChildGramGender.getPropertyType();
			DefaultInstance grammarGenderInst = new DefaultInstance(grammarGenderType, null);
			grammarGenderInst.setValue(gender);
			geoInstance.addProperty(geoChildGramGender.getName(), grammarGenderInst);
		}

		// name/GeographicalName/grammaticalNumber
		if (number != null && !number.isEmpty()) {
			PropertyDefinition geoChildGramNumber = Util.getChild("grammaticalNumber", geoType);
			TypeDefinition grammarNumberType = geoChildGramNumber.getPropertyType();
			DefaultInstance grammarNumberInst = new DefaultInstance(grammarNumberType, null);
			// set value of the grammaticalNumber instance
			grammarNumberInst.setValue(number);
			geoInstance.addProperty(geoChildGramNumber.getName(), grammarNumberInst);
		}

		// name/GeographicalName/language
		if (language != null && !language.isEmpty()) {
			PropertyDefinition geoChildLanguage = Util.getChild("language", geoType);
			TypeDefinition languageType = geoChildLanguage.getPropertyType();
			DefaultInstance languageInstance = new DefaultInstance(languageType, null);
			// set value of the language instance
			languageInstance.setValue(language);
			geoInstance.addProperty(geoChildLanguage.getName(), languageInstance);
		}

		// name/GeographicalName/nameStatus
		if (nameStatus != null && !nameStatus.isEmpty()) {
			PropertyDefinition geoChildNameStatus = Util.getChild("nameStatus", geoType);
			TypeDefinition nameStatusType = geoChildNameStatus.getPropertyType();
			DefaultInstance nameStatusInstance = new DefaultInstance(nameStatusType, null);
			// set value of the nameStatus instance
			nameStatusInstance.setValue(nameStatus);
			geoInstance.addProperty(geoChildNameStatus.getName(), nameStatusInstance);
		}

		// name/GeographicalName/nativeness
		if (nativeness != null && !nativeness.isEmpty()) {
			PropertyDefinition geoChildNativeness = Util.getChild("nativeness", geoType);
			TypeDefinition nativenessType = geoChildNativeness.getPropertyType();
			DefaultInstance nativenessInstance = new DefaultInstance(nativenessType, null);
			// set value of the nativeness instance
			nativenessInstance.setValue(nativeness);
			geoInstance.addProperty(geoChildNativeness.getName(), nativenessInstance);
		}

		if ((ipa != null && !ipa.isEmpty()) || (sound != null && !sound.isEmpty())) {
			// name/GeographicalName/pronunciation
			PropertyDefinition geoChildPronun = Util.getChild("pronunciation", geoType);
			TypeDefinition pronunType = geoChildPronun.getPropertyType();
			DefaultInstance pronunInstance = new DefaultInstance(pronunType, null);
			geoInstance.addProperty(geoChildPronun.getName(), pronunInstance);

			// name/GeographicalName/pronunciation/PronunciationOfName
			PropertyDefinition pronunChildPronOfName = Util.getChild("PronunciationOfName",
					pronunType);
			TypeDefinition pronOfNameType = pronunChildPronOfName.getPropertyType();
			DefaultInstance pronOfNameInst = new DefaultInstance(pronOfNameType, null);
			pronunInstance.addProperty(pronunChildPronOfName.getName(), pronOfNameInst);

			if (ipa != null && !ipa.isEmpty()) {
				// name/GeographicalName/pronunciation/PronunciationOfName/pronunciationIPA
				PropertyDefinition pronOfNameChildIPA = Util.getChild("pronunciationIPA",
						pronOfNameType);
				TypeDefinition pronunIpaType = pronOfNameChildIPA.getPropertyType();
				DefaultInstance pronunIpaInstance = new DefaultInstance(pronunIpaType, null);
				pronunIpaInstance.setValue(ipa);
				pronOfNameInst.addProperty(pronOfNameChildIPA.getName(), pronunIpaInstance);
			}

			if (sound != null && !sound.isEmpty()) {
				// name/GeographicalName/pronunciation/PronunciationOfName/pronunciationSoundLink
				PropertyDefinition pronOfNameChildSound = Util.getChild("pronunciationSoundLink",
						pronOfNameType);
				TypeDefinition pronunSoundType = pronOfNameChildSound.getPropertyType();
				DefaultInstance pronunSoundInstance = new DefaultInstance(pronunSoundType, null);
				pronunSoundInstance.setValue(sound);
				pronOfNameInst.addProperty(pronOfNameChildSound.getName(), pronunSoundInstance);
			}
		}

		// name/GeographicalName/sourceOfName
		if (sourceOfName != null && !sourceOfName.isEmpty()) {
			PropertyDefinition geoChildSource = Util.getChild("sourceOfName", geoType);
			TypeDefinition sourceType = geoChildSource.getPropertyType();
			DefaultInstance sourceInstance = new DefaultInstance(sourceType, null);
			// set value of the sourceOfName instance
			sourceInstance.setValue(sourceOfName);
			geoInstance.addProperty(geoChildSource.getName(), sourceInstance);
		}

		// name/GeographicalName/spelling
		PropertyDefinition geoChildSpelling = Util.getChild("spelling", geoType);
		TypeDefinition spellingType = geoChildSpelling.getPropertyType();

		// name/GeographicalName/spelling/SpellingOfName
		PropertyDefinition spellingChildSpellOfName = Util.getChild("SpellingOfName", spellingType);
		TypeDefinition spellOfNameType = spellingChildSpellOfName.getPropertyType();

		// create a "spelling" instance for each spelling
		for (int i = 0; i < scripts.size(); i++) {
			DefaultInstance spellingInstance = new DefaultInstance(spellingType, null);
			DefaultInstance spellOfNameInst = new DefaultInstance(spellOfNameType, null);

			// name/GeographicalName/spelling/SpellingOfName/script
			PropertyDefinition spellOfNameChildScript = Util.getChild("script", spellOfNameType);
			TypeDefinition scriptType = spellOfNameChildScript.getPropertyType();
			DefaultInstance scriptInstance = new DefaultInstance(scriptType, null);

			// name/GeographicalName/spelling/SpellingOfName/text
			PropertyDefinition spellOfNameChildText = Util.getChild("text", spellOfNameType);

			// name/GeographicalName/spelling/SpellingOfName/transliterationScheme
			PropertyDefinition spellOfNameChildTransliteration = Util.getChild(
					"transliterationScheme", spellOfNameType);
			TypeDefinition transliterationType = spellOfNameChildTransliteration.getPropertyType();
			DefaultInstance transliterationInstance = new DefaultInstance(transliterationType, null);

			// build the spelling instance
			scriptInstance.setValue(scripts.get(i));

			transliterationInstance.setValue(trans.get(i));

			spellOfNameInst.addProperty(spellOfNameChildScript.getName(), scriptInstance);
			// set text value from inputs
			spellOfNameInst.addProperty(spellOfNameChildText.getName(), inputs.get(i).getValue());
			spellOfNameInst.addProperty(spellOfNameChildTransliteration.getName(),
					transliterationInstance);

			spellingInstance.addProperty(spellingChildSpellOfName.getName(), spellOfNameInst);

			geoInstance.addProperty(geoChildSpelling.getName(), spellingInstance);
		}

		return targetInstance;
	}
}
