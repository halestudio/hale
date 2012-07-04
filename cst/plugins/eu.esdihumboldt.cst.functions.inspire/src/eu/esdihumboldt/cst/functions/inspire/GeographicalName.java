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

		// list of all source properties (
		List<PropertyValue> inputs = variables.get(null);

		// get all parameters defined by the wizard page
		String ipa = getParameterChecked(PROPERTY_PRONUNCIATIONIPA);
		String language = getParameterChecked(PROPERTY_LANGUAGE);
		String sourceOfName = getParameterChecked(PROPERTY_SOURCEOFNAME);
		String nameStatus = getParameterChecked(PROPERTY_NAMESTATUS);
		String nativeness = getParameterChecked(PROPERTY_NATIVENESS);
		String gender = getParameterChecked(PROPERTY_GRAMMA_GENDER);
		String number = getParameterChecked(PROPERTY_GRAMMA_NUMBER);

		// definition of the target property (name in this case ->
		// mapping_dkm_inspire.xml)
		TypeDefinition targetType = resultProperty.getDefinition()
				.getPropertyType();

		// instance that can be changed (add property/instance as child)
		DefaultInstance targetInstance = new DefaultInstance(targetType, null);

		// search for the child named "Identifier"
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
		// ----------------- childs for grammaticalGender here ----------------
		// name/GeographicalName/grammaticalGender/codeSpace
		PropertyDefinition grammarGenderChildCodeSpace = Util.getChild(
				"codeSpace", grammarGenderType);

		// name/GeographicalName/grammaticalGender/nilReason
		PropertyDefinition grammarGenderChildNilReason = Util.getChild(
				"nilReason", grammarGenderType);
		// --------------------------------------------------------------------
		
		// name/GeographicalName/grammaticalNumber
		PropertyDefinition geoChildGramNumber = Util.getChild("grammaticalNumber", geoType);
		TypeDefinition grammarNumberType = geoChildGramNumber.getPropertyType();
		DefaultInstance grammarNumberInst = new DefaultInstance(grammarNumberType, null);
		// ------------------ childs of grammaticalNumber here ---------------
		// name/GeographicalName/grammaticalNumber/codeSpace
		PropertyDefinition grammarNumberChildCodeSpace = Util.getChild("codeSpace", grammarNumberType);
		
		// name/GeographicalName/grammaticalNumber/nilReason
		PropertyDefinition grammarNumberChildNilReason = Util.getChild("nilReason", grammarNumberType);
		// -------------------------------------------------------------------
		
		// name/GeographicalName/language
		PropertyDefinition geoChildLanguage = Util.getChild("language", geoType);
		TypeDefinition languageType = geoChildLanguage.getPropertyType();
		DefaultInstance languageInstance = new DefaultInstance(languageType, null);
		// ----------------- childs of language here ---------------------------
		// name/GeographicalName/language/nilReason
		PropertyDefinition languageChildNilReason = Util.getChild("nilReason", languageType);
		// ---------------------------------------------------------------------
		
		// name/GeographicalName/nameStatus
		PropertyDefinition geoChildNameStatus = Util.getChild("nameStatus", geoType);
		TypeDefinition nameStatusType = geoChildNameStatus.getPropertyType();
		DefaultInstance nameStatusInstance = new DefaultInstance(nameStatusType, null);
		// ----------------- childs of nameStatus here --------------------------
		// name/GeographicalName/nameStatus/codeSpace
		PropertyDefinition nameStatusChildCodeSpace = Util.getChild("codeSpace", nameStatusType);
		// name/GeographicalName/nameStatus/nilReason
		PropertyDefinition nameStatusChildNilReason = Util.getChild("nilReason", nameStatusType);
		// ----------------------------------------------------------------------
		
		// name/GeographicalName/nativeness
		PropertyDefinition geoChildNativeness = Util.getChild("nativeness", geoType);
		TypeDefinition nativenessType = geoChildNativeness.getPropertyType();
		DefaultInstance nativenessInstance = new DefaultInstance(nativenessType, null);
		// ----------------- childs for nativeness here --------------------------
		// name/GeographicalName/nativeness/codeSpace
		PropertyDefinition nativeChildCodeSpace = Util.getChild("codeSpace", nativenessType);
		// name/GeographicalName/nativeness/nilReason	
		PropertyDefinition nativeChildNilReason = Util.getChild("nilReason", nativenessType);
		// -----------------------------------------------------------------------
		
		// name/GeographicalName/pronunciation
		PropertyDefinition geoChildPronun = Util.getChild("pronunciation", geoType);
		TypeDefinition pronunType = geoChildPronun.getPropertyType();
		DefaultInstance pronunInstance = new DefaultInstance(pronunType, null);
		// ----------------- childs for pronunciation here ------------------------
		// name/GeographicalName/pronunciation/nilReason
		PropertyDefinition pronunChildNilReason = Util.getChild("nilReason", pronunType);
		// name/GeographicalName/pronunciation/PronunciationOfName
		PropertyDefinition pronunChildPronOfName = Util.getChild("PronunciationOfName", pronunType);
		TypeDefinition pronOfNameType = pronunChildPronOfName.getPropertyType();
		DefaultInstance pronOfNameInst = new DefaultInstance(pronOfNameType, null);
		
		// name/GeographicalName/pronunciation/PronunciationOfName/pronunciationIPA
		PropertyDefinition pronOfNameChildIPA = Util.getChild("pronunciationIPA", pronOfNameType);
		TypeDefinition pronunIpaType = pronOfNameChildIPA.getPropertyType();
		DefaultInstance pronunIpaInstance = new DefaultInstance(pronunIpaType, null);
		
		// name/GeographicalName/pronunciation/PronunciationOfName/pronunciationIPA/nilReason
		PropertyDefinition pronunIpaChildNilReason = Util.getChild("nilReason", pronunIpaType);
		
		// name/GeographicalName/pronunciation/PronunciationOfName/pronunciationSoundLink
		PropertyDefinition pronOfNameChildSound = Util.getChild("pronunciationSoundLink", pronOfNameType);
		TypeDefinition pronunSoundType = pronOfNameChildSound.getPropertyType();
		DefaultInstance pronunSoundInstance = new DefaultInstance(pronunSoundType, null);

		// name/GeographicalName/pronunciation/PronunciationOfName/pronunciationSoundLink/nilReason
		PropertyDefinition pronunSoundChildNilReason = Util.getChild("nilReason", pronunSoundType);
		// ------------------------------------------------------------------------
		
		// name/GeographicalName/sourceOfName
		PropertyDefinition geoChildSource = Util.getChild("sourceOfName", geoType);
		TypeDefinition sourceType = geoChildSource.getPropertyType();
		DefaultInstance sourceInstance = new DefaultInstance(sourceType, null);
		// ----------------- childs for sourceOfName here -------------------------
		// name/GeographicalName/sourceOfName/nilReason
		PropertyDefinition sourceChildNilReason = Util.getChild("nilReason", sourceType);
		// ------------------------------------------------------------------------
		
		// name/GeographicalName/spelling
		PropertyDefinition geoChildSpelling = Util.getChild("spelling", geoType);
		TypeDefinition spellingType = geoChildSpelling.getPropertyType();
		DefaultInstance spellingInstance = new DefaultInstance(spellingType, null);
		// ------------------ childs for spelling here -----------------------------
		// name/GeographicalName/spelling/SpellingOfName
		PropertyDefinition spellingChildSpellOfName = Util.getChild("SpellingOfName", spellingType);
		TypeDefinition spellOfNameType = spellingChildSpellOfName.getPropertyType();
		DefaultInstance spellOfNameInst = new DefaultInstance(spellOfNameType, null);
		
		// name/GeographicalName/spelling/SpellingOfName/script
		PropertyDefinition spellOfNameChildScript = Util.getChild("script", spellOfNameType);
		TypeDefinition scriptType = spellOfNameChildScript.getPropertyType();
		DefaultInstance scriptInstance = new DefaultInstance(scriptType, null);
		
		// name/GeographicalName/spelling/SpellingOfName/script/nilReason
		PropertyDefinition scriptChildNilReason = Util.getChild("nilReason", scriptType);
		
		// name/GeographicalName/spelling/SpellingOfName/text
		PropertyDefinition spellOfNameChildText = Util.getChild("text", spellOfNameType);
		
		// name/GeographicalName/spelling/SpellingOfName/transliterationScheme
		PropertyDefinition spellOfNameChildTransliteration = Util.getChild("transliterationScheme", spellOfNameType);
		TypeDefinition transliterationType = spellOfNameChildTransliteration.getPropertyType();
		DefaultInstance transliterationInstance = new DefaultInstance(transliterationType, null);
		
		// name/GeographicalName/spelling/SpellingOfName/transliterationScheme/nilReason
		PropertyDefinition transliterationChildNilReason = Util.getChild("nilReason", transliterationType);
		// -------------------------------------------------------------------------
		
		// name/nilReason
		PropertyDefinition targetChildNilReason = Util.getChild("nilReason", targetType);
		
		
		// -------------------------------------------------------------------------------
		// ------------------------------- Build Instance --------------------------------
		// -------------------------------------------------------------------------------
		
		// build the grammaticalGender instance
		grammarGenderInst.addProperty(grammarGenderChildCodeSpace.getName(), grammarGenderChildCodeSpace);
		grammarGenderInst.addProperty(grammarGenderChildNilReason.getName(), grammarNumberChildNilReason);
		grammarGenderInst.setValue(gender);
		
		// build the grammaticalNumber instance
		grammarNumberInst.addProperty(grammarNumberChildCodeSpace.getName(), grammarNumberChildCodeSpace);
		grammarNumberInst.addProperty(grammarNumberChildNilReason.getName(), grammarGenderChildNilReason);
		grammarNumberInst.setValue(number);
		
		// build the language instance
		languageInstance.addProperty(languageChildNilReason.getName(), languageChildNilReason);
		languageInstance.setValue(language);
		
		// build the nameStatus instance
		nameStatusInstance.addProperty(nameStatusChildCodeSpace.getName(), nameStatusChildCodeSpace);
		nameStatusInstance.addProperty(nameStatusChildNilReason.getName(), nameStatusChildNilReason);
		nameStatusInstance.setValue(nameStatus);
		
		// build the nativeness instance
		nativenessInstance.addProperty(nativeChildCodeSpace.getName(), nameStatusChildCodeSpace);
		nativenessInstance.addProperty(nativeChildNilReason.getName(), nativeChildNilReason);
		nativenessInstance.setValue(nativeness);
		
		// build the pronunciation instance
		// first build the inner instances ...
		pronunIpaInstance.addProperty(pronunIpaChildNilReason.getName(), pronunSoundChildNilReason);
		pronunIpaInstance.setValue(ipa);
		
		pronunSoundInstance.addProperty(pronunSoundChildNilReason.getName(), pronunSoundChildNilReason);
		// TODO: pronunSoundInstance.setValue(sound);
		
		pronOfNameInst.addProperty(pronOfNameChildIPA.getName(), pronunIpaInstance);
		pronOfNameInst.addProperty(pronOfNameChildSound.getName(), pronunSoundInstance);
		
		// ... and merge all to the outer instance "pronunciation"
		pronunInstance.addProperty(pronunChildPronOfName.getName(), pronOfNameInst);
		pronunInstance.addProperty(pronunChildNilReason.getName(), pronunChildNilReason);
		
		// build the sourceOfName instance
		sourceInstance.addProperty(sourceChildNilReason.getName(), sourceChildNilReason);
		sourceInstance.setValue(sourceOfName);
		
		// build the spelling instance
		scriptInstance.addProperty(scriptChildNilReason.getName(), scriptChildNilReason);
		// TODO: scriptInstance.setValue(script);
		
		// TODO: each source element should have its own "text" property
		
		transliterationInstance.addProperty(transliterationChildNilReason.getName(), transliterationChildNilReason);
		// TODO: transliterationInstance.setValue(transliteration);
		
		spellOfNameInst.addProperty(spellOfNameChildScript.getName(), scriptInstance);
		// FIXME: text should be handled correct
		spellOfNameInst.addProperty(spellOfNameChildText.getName(), spellOfNameChildText);
		spellOfNameInst.addProperty(spellOfNameChildTransliteration.getName(), transliterationInstance);
		
		spellingInstance.addProperty(spellingChildSpellOfName.getName(), spellOfNameInst);
		
		geoInstance.addProperty(geoChildGramGender.getName(), grammarGenderInst);
		geoInstance.addProperty(geoChildGramNumber.getName(), grammarNumberInst);
		geoInstance.addProperty(geoChildLanguage.getName(), languageInstance);
		geoInstance.addProperty(geoChildNameStatus.getName(), nameStatusInstance);
		geoInstance.addProperty(geoChildNativeness.getName(), nativenessInstance);
		geoInstance.addProperty(geoChildPronun.getName(), pronunInstance);
		geoInstance.addProperty(geoChildSource.getName(), sourceInstance);
		geoInstance.addProperty(geoChildSpelling.getName(), spellingInstance);
		
		targetInstance.addProperty(targetChildGeoName.getName(), geoInstance);
		targetInstance.addProperty(targetChildNilReason.getName(), targetChildNilReason);
		
		return targetInstance;
	}
}
