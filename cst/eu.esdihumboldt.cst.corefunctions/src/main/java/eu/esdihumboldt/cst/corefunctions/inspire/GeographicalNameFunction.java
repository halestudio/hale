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

package eu.esdihumboldt.cst.corefunctions.inspire;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureImpl;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.PropertyDescriptor;

import eu.esdihumboldt.cst.AbstractCstFunction;
import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.ext.IParameter;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.omwg.ComposedProperty;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.goml.rdf.About;
import eu.esdihumboldt.inspire.data.GeographicalName;
import eu.esdihumboldt.inspire.data.GrammaticalGenderValue;
import eu.esdihumboldt.inspire.data.GrammaticalNumberValue;
import eu.esdihumboldt.inspire.data.NameStatusValue;
import eu.esdihumboldt.inspire.data.NativenessValue;

/**
 * This function enables the creation of an INPSIRE GeographicalName object from
 * a set of simple string parameters.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @author Jose Ignacio Gisbert, Ana Belen Anton
 * @partner 02 / ETRA Research and Development
 * @version $Id$
 */
public class GeographicalNameFunction extends AbstractCstFunction {

	public static final String PROPERTY_TEXT = "text";
	public static final String PROPERTY_SCRIPT = "script";
	public static final String PROPERTY_TRANSLITERATION = "transliterationScheme";
	public static final String PROPERTY_NAMESTATUS = "nameStatus";
	public static final String PROPERTY_LANGUAGE = "language";
	public static final String PROPERTY_NATIVENESS = "nativeness";
	public static final String PROPERTY_SOURCEOFNAME = "sourceOfName";
	public static final String PROPERTY_PRONUNCIATIONIPA = "pronunciationIPA";
	public static final String PROPERTY_PRONUNCIATIONSOUNDLINK = "pronunciationSoundLink";
	public static final String PROPERTY_GRAMMA_GENDER = "grammaticalGender";
	public static final String PROPERTY_GRAMMA_NUMBER = "grammaticalNumber";

	private ArrayList<ArrayList<Property>> variable = new ArrayList<ArrayList<Property>>();
	private Property targetProperty = null;

	private ArrayList<ArrayList<String>> _script = new ArrayList<ArrayList<String>>();
	private ArrayList<ArrayList<String>> _transliteration = new ArrayList<ArrayList<String>>();
	private ArrayList<NameStatusValue> _nameStatus = new ArrayList<NameStatusValue>();
	private ArrayList<String> _language = new ArrayList<String>();
	private ArrayList<NativenessValue> _nativeness = new ArrayList<NativenessValue>();
	private ArrayList<String> _sourceOfName = new ArrayList<String>();
	private ArrayList<String> _pronunciationIPA = new ArrayList<String>();
	private ArrayList<URI> _pronunciationSoundLink = new ArrayList<URI>();
	private ArrayList<GrammaticalGenderValue> _grammaticalGender = new ArrayList<GrammaticalGenderValue>();
	private ArrayList<GrammaticalNumberValue> _grammaticalNumber = new ArrayList<GrammaticalNumberValue>();
	private int cellcount = 0;
	private int spellcount = 0;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.esdihumboldt.cst.transformer.CstFunction#configure(eu.esdihumboldt
	 * .cst.align.ICell)
	 */
	public boolean configure(ICell cell) {

		// ************* STORE ALL PARAMETERS IN ARRAYLISTS TO FACILITATE
		// ************* CONSTRUCTION OF GEOGRAPHICALNAMES IN TRANSFORM FUNCTION
		for (Property p : ((ComposedProperty) cell.getEntity1())
				.getCollection()) {
			spellcount = 0;
			this._script.add(cellcount, new ArrayList<String>());
			this._transliteration.add(cellcount, new ArrayList<String>());
			this.variable.add(cellcount, new ArrayList<Property>());
			for (Property prop : ((ComposedProperty) p).getCollection()) {
				this.variable.get(cellcount).add(spellcount, prop);
				for (IParameter ip : prop.getTransformation().getParameters()) {
					if (ip.getName().equals(
							GeographicalNameFunction.PROPERTY_SCRIPT)) {
						this._script.get(cellcount).add(spellcount,
								ip.getValue());
					} else if (ip.getName().equals(
							GeographicalNameFunction.PROPERTY_TRANSLITERATION)) {
						this._transliteration.get(cellcount).add(spellcount,
								ip.getValue());
					}
					if (this._script.get(cellcount).size() < spellcount + 1)
						this._script.get(cellcount).add(spellcount, null);
					if (this._transliteration.get(cellcount).size() < spellcount + 1)
						this._transliteration.get(cellcount).add(spellcount,
								null);
				}
				spellcount++;
			}
			for (IParameter ip : p.getTransformation().getParameters()) {
				if (ip.getName().equals(
						GeographicalNameFunction.PROPERTY_NAMESTATUS)) {
					this._nameStatus.add(cellcount, NameStatusValue.valueOf(ip
							.getValue()));
				} else if (ip.getName().equals(
						GeographicalNameFunction.PROPERTY_LANGUAGE)) {
					this._language.add(cellcount, ip.getValue());
				} else if (ip.getName().equals(
						GeographicalNameFunction.PROPERTY_NATIVENESS)) {
					this._nativeness.add(cellcount, NativenessValue.valueOf(ip
							.getValue()));
				} else if (ip.getName().equals(
						GeographicalNameFunction.PROPERTY_SOURCEOFNAME)) {
					this._sourceOfName.add(cellcount, ip.getValue());
				} else if (ip.getName().equals(
						GeographicalNameFunction.PROPERTY_PRONUNCIATIONIPA)) {
					this._pronunciationIPA.add(cellcount, ip.getValue());
				} else if (ip
						.getName()
						.equals(
								GeographicalNameFunction.PROPERTY_PRONUNCIATIONSOUNDLINK)) {
					this._pronunciationSoundLink.add(cellcount, URI.create(ip
							.getValue()));
				} else if (ip.getName().equals(
						GeographicalNameFunction.PROPERTY_GRAMMA_GENDER)) {
					this._grammaticalGender.add(cellcount,
							GrammaticalGenderValue.valueOf(ip.getValue()));
				} else if (ip.getName().equals(
						GeographicalNameFunction.PROPERTY_GRAMMA_NUMBER)) {
					this._grammaticalNumber.add(cellcount,
							GrammaticalNumberValue.valueOf(ip.getValue()));
				}
			}
			// ************* FORCE TO COMPLETE INDEX FOR ALL ARRAYLISTS
			if (this._nameStatus.size() < cellcount + 1)
				this._nameStatus.add(cellcount, null);
			if (this._language.size() < cellcount + 1)
				this._language.add(cellcount, null);
			if (this._nativeness.size() < cellcount + 1)
				this._nativeness.add(cellcount, null);
			if (this._sourceOfName.size() < cellcount + 1)
				this._sourceOfName.add(cellcount, null);
			if (this._pronunciationIPA.size() < cellcount + 1)
				this._pronunciationIPA.add(cellcount, null);
			if (this._pronunciationSoundLink.size() < cellcount + 1)
				this._pronunciationSoundLink.add(cellcount, null);
			if (this._grammaticalGender.size() < cellcount + 1)
				this._grammaticalGender.add(cellcount, null);
			if (this._grammaticalNumber.size() < cellcount + 1)
				this._grammaticalNumber.add(cellcount, null);
			cellcount++;
		}
		this.targetProperty = (Property) cell.getEntity2();

		if (this.variable.size() == 0) {
			throw new RuntimeException("The Source property must be defined.");
		}
		if (this.targetProperty == null) {
			throw new RuntimeException("The Target property must be defined.");
		}
		return true;
	}

	/**
	 * @see eu.esdihumboldt.cst.transformer.CstFunction#transform(org.opengis.feature.Feature,
	 *      org.opengis.feature.Feature)
	 */
	public Feature transform(Feature source, Feature target) {

		// ************* CHECK IF INPUT FEATURES HAVE THE EXPECTED PROPERTY NAME
		if (target.getProperties(targetProperty.getLocalname()).size() == 0)
			return null;

		for (int i = 0; i < variable.size(); i++)
			for (int j = 0; j < variable.get(i).size(); j++)
				if (source.getProperties(variable.get(i).get(j).getLocalname())
						.size() == 0)
					return null;

		PropertyDescriptor pd = target.getProperty(
				targetProperty.getLocalname()).getDescriptor();
		SimpleFeatureType spellingOfNamePropertyType = 
			(SimpleFeatureType) ((SimpleFeatureType) pd
				.getType()).getDescriptor("spelling").getType();
		SimpleFeatureType spellingOfNameType = (SimpleFeatureType) (spellingOfNamePropertyType
				.getDescriptor("SpellingOfName")).getType();
		SimpleFeatureType pronunciationOfNameType = (SimpleFeatureType) ((SimpleFeatureType) pd
				.getType()).getDescriptor("pronunciation").getType();

		Collection<SimpleFeatureImpl> geographicalnames = new HashSet<SimpleFeatureImpl>();

		// ************* CREATION OF A COLLECTION OF GEOGRAPHICALNAMES WITH
		// CONFIGURED VALUES ****************
		for (int i = 0; i < cellcount; i++) {
			Collection<SimpleFeatureImpl> colecc = new HashSet<SimpleFeatureImpl>();
			for (int j = 0; j < _script.get(i).size(); j++) {
				Object result = source.getProperty(
						this.variable.get(i).get(j).getLocalname()).getValue();
				SimpleFeatureImpl spellingofname = (SimpleFeatureImpl) SimpleFeatureBuilder
						.build(spellingOfNameType, new Object[] {},
								"SpellingOfName");
				spellingofname.setAttribute("script", _script.get(i).get(j));
				spellingofname.setAttribute("text", result.toString());
				spellingofname.setAttribute("transliterationScheme",
						_transliteration.get(i).get(j));

				SimpleFeatureImpl spellingofnameproperty = (SimpleFeatureImpl) SimpleFeatureBuilder
						.build(spellingOfNamePropertyType, new Object[] {},
								"SpellingOfNameProperty");
				spellingofnameproperty.setAttribute("SpellingOfName",
						Collections.singleton(spellingofname));

				colecc.add(spellingofnameproperty);
			}
			SimpleFeatureImpl geographicalname = (SimpleFeatureImpl) SimpleFeatureBuilder
					.build((SimpleFeatureType) pd.getType(), new Object[] {},
							"GeographicalName");
			geographicalname.setAttribute("spelling", colecc);
			geographicalname.setAttribute("language", _language.get(i));
			geographicalname.setAttribute("sourceOfName", _sourceOfName.get(i));
			if (_nativeness.get(i) != null)
				geographicalname.setAttribute("nativeness", Collections
						.singleton(_nativeness.get(i).toString()));
			else
				geographicalname.setAttribute("nativeness", null);
			if (_grammaticalGender.get(i) != null)
				geographicalname.setAttribute("grammaticalGender", Collections
						.singleton(_grammaticalGender.get(i).toString()));
			else
				geographicalname.setAttribute("grammaticalGender", null);
			if (_grammaticalNumber.get(i) != null)
				geographicalname.setAttribute("grammaticalNumber", Collections
						.singleton(_grammaticalNumber.get(i).toString()));
			else
				geographicalname.setAttribute("grammaticalNumber", null);
			if (_nameStatus.get(i) != null)
				geographicalname.setAttribute("nameStatus", Collections
						.singleton(_nameStatus.get(i).toString()));
			else
				geographicalname.setAttribute("nameStatus", null);
			SimpleFeatureImpl pronunciation = (SimpleFeatureImpl) SimpleFeatureBuilder
					.build(pronunciationOfNameType, new Object[] {},
							"PronunctiationOfName");
			pronunciation.setAttribute("pronunciationIPA", _pronunciationIPA
					.get(i));
			pronunciation.setAttribute("pronunciationSoundLink",
					_pronunciationSoundLink.get(i));
			geographicalname.setAttribute("pronunciation", Collections
					.singleton(pronunciation));
			geographicalnames.add(geographicalname);
		}
		// ************* SET COLLECTION OF GEOGRAPHICALNAMES AS TARGET INPUT
		// PARAMETER ****************
		((SimpleFeature) target).setAttribute(targetProperty.getLocalname(),
				geographicalnames);
		return target;
	}

	public Cell getParameters() {
		Cell parameterCell = new Cell();
		Property entity1 = new Property(new About(""));

		// ************* SETTING OF TYPE CONDITION FOR ENTITY1 ****************
		List<String> entityTypes = new ArrayList<String>();
		entityTypes.add(GeographicalName.class.getName());
		entityTypes.add(String.class.getName());
		entity1.setTypeCondition(entityTypes);

		// ************* SETTING OF TYPE CONDITION FOR ENTITY2 ****************
		Property entity2 = new Property(new About(""));
		entity2.setTypeCondition(entityTypes);

		parameterCell.setEntity1(entity1);
		parameterCell.setEntity2(entity2);
		return parameterCell;
	}

}
