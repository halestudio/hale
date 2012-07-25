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
		AbstractSingleTargetPropertyTransformation<TransformationEngine>
		implements GeographicalNameFunction {

	/**
	 * @see eu.esdihumboldt.hale.common.align.transformation.function.impl.AbstractSingleTargetPropertyTransformation#evaluate(java.lang.String,
	 *      eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine,
	 *      com.google.common.collect.ListMultimap, java.lang.String,
	 *      eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition,
	 *      java.util.Map,
	 *      eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog)
	 */
	@Override
	protected Object evaluate(String transformationIdentifier,
			TransformationEngine engine,
			ListMultimap<String, PropertyValue> variables, String resultName,
			PropertyEntityDefinition resultProperty,
			Map<String, String> executionParameters, TransformationLog log)
			throws TransformationException, NoResultException {

		// list of all source properties
		List<PropertyValue> inputs = variables.get(null);

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

		// definition of the target property
		TypeDefinition targetType = resultProperty.getDefinition()
				.getPropertyType();

		// instance that can be changed (add property/instance as child)
		DefaultInstance targetInstance = new DefaultInstance(targetType, null);

		// search for the child named "GeographicalName"
		PropertyDefinition targetChildGeoName = Util.getChild(
				"GeographicalName", targetType);

		// get type definition to create the "GeographicalName" instance
		TypeDefinition geoType = targetChildGeoName.getPropertyType();

		// name/GeographicalName/
		DefaultInstance geoInstance = new DefaultInstance(geoType, null);

		// name/GeographicalName/grammaticalGender/
		PropertyDefinition geoChildGramGender = Util.getChild(
				"grammaticalGender", geoType);

		TypeDefinition grammarGenderType = geoChildGramGender.getPropertyType();

		// name/GeographicalName/grammaticalGender/
		DefaultInstance grammarGenderInst = new DefaultInstance(
				grammarGenderType, null);

		// name/GeographicalName/grammaticalNumber
		PropertyDefinition geoChildGramNumber = Util.getChild(
				"grammaticalNumber", geoType);
		TypeDefinition grammarNumberType = geoChildGramNumber.getPropertyType();
		DefaultInstance grammarNumberInst = new DefaultInstance(
				grammarNumberType, null);

		// name/GeographicalName/language
		PropertyDefinition geoChildLanguage = Util
				.getChild("language", geoType);
		TypeDefinition languageType = geoChildLanguage.getPropertyType();
		DefaultInstance languageInstance = new DefaultInstance(languageType,
				null);

		// name/GeographicalName/nameStatus
		PropertyDefinition geoChildNameStatus = Util.getChild("nameStatus",
				geoType);
		TypeDefinition nameStatusType = geoChildNameStatus.getPropertyType();
		DefaultInstance nameStatusInstance = new DefaultInstance(
				nameStatusType, null);

		// name/GeographicalName/nativeness
		PropertyDefinition geoChildNativeness = Util.getChild("nativeness",
				geoType);
		TypeDefinition nativenessType = geoChildNativeness.getPropertyType();
		DefaultInstance nativenessInstance = new DefaultInstance(
				nativenessType, null);

		// name/GeographicalName/pronunciation
		PropertyDefinition geoChildPronun = Util.getChild("pronunciation",
				geoType);
		TypeDefinition pronunType = geoChildPronun.getPropertyType();
		DefaultInstance pronunInstance = new DefaultInstance(pronunType, null);

		// name/GeographicalName/pronunciation/PronunciationOfName
		PropertyDefinition pronunChildPronOfName = Util.getChild(
				"PronunciationOfName", pronunType);
		TypeDefinition pronOfNameType = pronunChildPronOfName.getPropertyType();
		DefaultInstance pronOfNameInst = new DefaultInstance(pronOfNameType,
				null);

		// name/GeographicalName/pronunciation/PronunciationOfName/pronunciationIPA
		PropertyDefinition pronOfNameChildIPA = Util.getChild(
				"pronunciationIPA", pronOfNameType);
		TypeDefinition pronunIpaType = pronOfNameChildIPA.getPropertyType();
		DefaultInstance pronunIpaInstance = new DefaultInstance(pronunIpaType,
				null);

		// name/GeographicalName/pronunciation/PronunciationOfName/pronunciationSoundLink
		PropertyDefinition pronOfNameChildSound = Util.getChild(
				"pronunciationSoundLink", pronOfNameType);
		TypeDefinition pronunSoundType = pronOfNameChildSound.getPropertyType();
		DefaultInstance pronunSoundInstance = new DefaultInstance(
				pronunSoundType, null);

		// name/GeographicalName/sourceOfName
		PropertyDefinition geoChildSource = Util.getChild("sourceOfName",
				geoType);
		TypeDefinition sourceType = geoChildSource.getPropertyType();
		DefaultInstance sourceInstance = new DefaultInstance(sourceType, null);

		// name/GeographicalName/spelling
		PropertyDefinition geoChildSpelling = Util
				.getChild("spelling", geoType);
		TypeDefinition spellingType = geoChildSpelling.getPropertyType();
		DefaultInstance spellingInstance = new DefaultInstance(spellingType,
				null);

		// name/GeographicalName/spelling/SpellingOfName
		PropertyDefinition spellingChildSpellOfName = Util.getChild(
				"SpellingOfName", spellingType);
		TypeDefinition spellOfNameType = spellingChildSpellOfName
				.getPropertyType();

		// create a "SpellingOfName" instance for each spelling
		if (scripts != null) {
			for (int i = 0; i < scripts.size(); i++) {

				DefaultInstance spellOfNameInst = new DefaultInstance(
						spellOfNameType, null);

				// name/GeographicalName/spelling/SpellingOfName/script
				PropertyDefinition spellOfNameChildScript = Util.getChild(
						"script", spellOfNameType);
				TypeDefinition scriptType = spellOfNameChildScript
						.getPropertyType();
				DefaultInstance scriptInstance = new DefaultInstance(
						scriptType, null);

				// name/GeographicalName/spelling/SpellingOfName/text
				PropertyDefinition spellOfNameChildText = Util.getChild("text",
						spellOfNameType);

				// name/GeographicalName/spelling/SpellingOfName/transliterationScheme
				PropertyDefinition spellOfNameChildTransliteration = Util
						.getChild("transliterationScheme", spellOfNameType);
				TypeDefinition transliterationType = spellOfNameChildTransliteration
						.getPropertyType();
				DefaultInstance transliterationInstance = new DefaultInstance(
						transliterationType, null);

				// build the spelling instance
				scriptInstance.setValue(scripts.get(i));

				transliterationInstance.setValue(trans.get(i));

				spellOfNameInst.addProperty(spellOfNameChildScript.getName(),
						scriptInstance);
				// set text value from inputs
				spellOfNameInst.addProperty(spellOfNameChildText.getName(),
						inputs.get(i).getValue());
				spellOfNameInst.addProperty(
						spellOfNameChildTransliteration.getName(),
						transliterationInstance);

				spellingInstance.addProperty(
						spellingChildSpellOfName.getName(), spellOfNameInst);

			}
		}

		// set value of the grammaticalGender instance
		grammarGenderInst.setValue(gender);

		// set value of the grammaticalNumber instance
		grammarNumberInst.setValue(number);

		// set value of the language instance
		languageInstance.setValue(language);

		// set value of the nameStatus instance
		nameStatusInstance.setValue(nameStatus);

		// set value of the nativeness instance
		nativenessInstance.setValue(nativeness);

		// build the pronunciation instance
		// first build the inner instances ...
		pronunIpaInstance.setValue(ipa);

		pronunSoundInstance.setValue(sound);

		pronOfNameInst.addProperty(pronOfNameChildIPA.getName(),
				pronunIpaInstance);
		pronOfNameInst.addProperty(pronOfNameChildSound.getName(),
				pronunSoundInstance);

		// ... and merge all to the outer instance "pronunciation"
		pronunInstance.addProperty(pronunChildPronOfName.getName(),
				pronOfNameInst);

		// set value of the sourceOfName instance
		sourceInstance.setValue(sourceOfName);

		geoInstance
				.addProperty(geoChildGramGender.getName(), grammarGenderInst);
		geoInstance
				.addProperty(geoChildGramNumber.getName(), grammarNumberInst);
		geoInstance.addProperty(geoChildLanguage.getName(), languageInstance);
		geoInstance.addProperty(geoChildNameStatus.getName(),
				nameStatusInstance);
		geoInstance.addProperty(geoChildNativeness.getName(),
				nativenessInstance);
		geoInstance.addProperty(geoChildPronun.getName(), pronunInstance);
		geoInstance.addProperty(geoChildSource.getName(), sourceInstance);
		geoInstance.addProperty(geoChildSpelling.getName(), spellingInstance);

		targetInstance.addProperty(targetChildGeoName.getName(), geoInstance);

		return targetInstance;
	}
}
