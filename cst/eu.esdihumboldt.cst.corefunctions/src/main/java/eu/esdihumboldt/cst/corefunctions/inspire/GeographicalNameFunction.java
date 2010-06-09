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
import org.geotools.feature.AttributeImpl;
import org.geotools.feature.FeatureImpl;
import org.geotools.feature.PropertyImpl;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureImpl;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.feature.type.PropertyType;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.ext.IParameter;
import eu.esdihumboldt.cst.transformer.service.rename.FeatureBuilder;
import eu.esdihumboldt.cst.AbstractCstFunction;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.goml.omwg.ComposedProperty;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.goml.rdf.About;
import eu.esdihumboldt.inspire.data.GeographicalName;
import eu.esdihumboldt.inspire.data.GrammaticalGenderValue;
import eu.esdihumboldt.inspire.data.GrammaticalNumberValue;
import eu.esdihumboldt.inspire.data.NameStatusValue;
import eu.esdihumboldt.inspire.data.NativenessValue;
import eu.esdihumboldt.inspire.data.PronunciationOfName;
import eu.esdihumboldt.inspire.data.SpellingOfName;

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
	public static final String INSPIRE_IDENTIFIER_PREFIX = "urn:x-inspire:object:id";

	private ArrayList<ArrayList<Property>> sourceattributes = new ArrayList<ArrayList<Property>>();
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
	private int spellingcount = 0;

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
			spellingcount = 0;
			this._script.add(cellcount, new ArrayList<String>());
			this._transliteration.add(cellcount, new ArrayList<String>());
			this.sourceattributes.add(cellcount, new ArrayList<Property>());
			for (Property prop : ((ComposedProperty) p).getCollection()) {
				this.sourceattributes.get(cellcount).add(spellingcount, prop);
				for (IParameter ip : prop.getTransformation().getParameters()) {
					if (ip.getName().equals(
							GeographicalNameFunction.PROPERTY_SCRIPT)) {
						this._script.get(cellcount).add(spellingcount,
								ip.getValue());
					} else if (ip.getName().equals(
							GeographicalNameFunction.PROPERTY_TRANSLITERATION)) {
						this._transliteration.get(cellcount).add(spellingcount,
								ip.getValue());
					}
					if (this._script.get(cellcount).size() < spellingcount + 1)
						this._script.get(cellcount).add(spellingcount, null);
					if (this._transliteration.get(cellcount).size() < spellingcount + 1)
						this._transliteration.get(cellcount).add(spellingcount,
								null);
				}
				spellingcount++;
			}
			for (IParameter ip : p.getTransformation().getParameters()) {
				if (ip.getName().equals(
						GeographicalNameFunction.PROPERTY_NAMESTATUS)) {
					this._nameStatus.add(cellcount, NameStatusValue.valueOf(ip.getValue()));
				} else if (ip.getName().equals(GeographicalNameFunction.PROPERTY_LANGUAGE)) {
					this._language.add(cellcount, ip.getValue());
				} else if (ip.getName().equals(GeographicalNameFunction.PROPERTY_NATIVENESS)) {
					this._nativeness.add(cellcount, NativenessValue.valueOf(ip.getValue()));
				} else if (ip.getName().equals(GeographicalNameFunction.PROPERTY_SOURCEOFNAME)) {
					this._sourceOfName.add(cellcount, ip.getValue());
				} else if (ip.getName().equals(GeographicalNameFunction.PROPERTY_PRONUNCIATIONIPA)) {
					this._pronunciationIPA.add(cellcount, ip.getValue());
				} else if (ip.getName().equals(GeographicalNameFunction.PROPERTY_PRONUNCIATIONSOUNDLINK)) {
					this._pronunciationSoundLink.add(cellcount, URI.create(ip.getValue()));
				} else if (ip.getName().equals(GeographicalNameFunction.PROPERTY_GRAMMA_GENDER)) {
					this._grammaticalGender.add(cellcount,GrammaticalGenderValue.valueOf(ip.getValue()));
				} else if (ip.getName().equals(GeographicalNameFunction.PROPERTY_GRAMMA_NUMBER)) {
					this._grammaticalNumber.add(cellcount,GrammaticalNumberValue.valueOf(ip.getValue()));
				}
			}
			// ************* FORCE TO COMPLETE INDEX FOR ALL ARRAYLISTS
			// ****************
			if (this._nameStatus.size() < cellcount + 1)
				this._nameStatus.add(cellcount, null);
			if (this._language.size() < cellcount + 1)
				this._language.add(cellcount, null);
			if (this._nativeness.size() < cellcount + 1)
				this._nativeness.add(cellcount, null);
			if (this._sourceOfName.size() < cellcount + 1)
				this._sourceOfName.add(cellcount, "");
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

		if (this.sourceattributes.size() == 0) {
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

		// check whether target Feature has the expected property
		if (target.getProperties(targetProperty.getLocalname()).size() == 0) {
			return null;
		}

		// check whether source Feature has the expected properties
		for (int i = 0; i < sourceattributes.size(); i++) {
			for (int j = 0; j < sourceattributes.get(i).size(); j++) {
				if (source.getProperties(
						sourceattributes.get(i).get(j).getLocalname()).size() == 0) {
					return null;
				}
			}
		}

		PropertyType pt = target.getProperty(
				targetProperty.getLocalname()).getType();
		SimpleFeatureType geoNameType = (SimpleFeatureType)
						((SimpleFeatureType) pt).getDescriptor("GeographicalName").getType();
		SimpleFeatureType spellingofnamepropertytype = (SimpleFeatureType) 
						geoNameType.getDescriptor("spelling").getType();
		SimpleFeatureType spellingofnametype = (SimpleFeatureType) 
						(spellingofnamepropertytype.getDescriptor("SpellingOfName")).getType();
		SimpleFeatureType pronunciationofnametype = (SimpleFeatureType) ((SimpleFeatureType) 
						(geoNameType).getDescriptor("pronunciation").getType()).getDescriptor("PronunciationOfName").getType();

		Collection<FeatureImpl> geographicalnames = new HashSet<FeatureImpl>();

		// CREATION OF A COLLECTION OF GEOGRAPHICALNAMES WITH CONFIGURED VALUES
		for (int i = 0; i < cellcount; i++) {
			Collection<FeatureImpl> spellingofnamepropertiescollection = new HashSet<FeatureImpl>();
			for (int j = 0; j < _script.get(i).size(); j++) {
				Object sourcepropertyvalue = source.getProperty(
						this.sourceattributes.get(i).get(j).getLocalname())
						.getValue();
				/*SimpleFeatureImpl spellingofname = (SimpleFeatureImpl) SimpleFeatureBuilder
						.build(spellingofnametype, new Object[] {},
								"SpellingOfName");*/
				FeatureImpl spellingofname = (FeatureImpl)FeatureBuilder.buildFeature(spellingofnametype, null,false);
				spellingofname.getProperty("script").setValue(_script.get(i).get(j));
				spellingofname.getProperty("text").setValue(sourcepropertyvalue
						.toString());
				spellingofname.getProperty("transliterationScheme").setValue(_transliteration.get(i).get(j));
				
				/*spellingofname.setAttribute("script", _script.get(i).get(j));
				spellingofname.setAttribute("text", sourcepropertyvalue
						.toString());
				spellingofname.setAttribute("transliterationScheme",
						_transliteration.get(i).get(j));*/

				FeatureImpl spellingofnameproperty = (FeatureImpl)FeatureBuilder.buildFeature(spellingofnamepropertytype, null,false);
				spellingofnameproperty.getProperty("SpellingOfName").setValue(Collections.singleton(spellingofname));
				/*SimpleFeatureImpl spellingofnameproperty = (SimpleFeatureImpl) SimpleFeatureBuilder
						.build(spellingofnamepropertytype, new Object[] {},
								"SpellingOfNameProperty");
				spellingofnameproperty.setAttribute("SpellingOfName",
						Collections.singleton(spellingofname));*/

				spellingofnamepropertiescollection.add(spellingofnameproperty);
			}
			
			FeatureImpl geographicalname = (FeatureImpl)FeatureBuilder.buildFeature(geoNameType, null,false);
			geographicalname.getProperty("spelling").setValue(spellingofnamepropertiescollection);
			geographicalname.getProperty("language").setValue(_language.get(i));
			geographicalname.getProperty("sourceOfName").setValue(_sourceOfName.get(i));
			/*SimpleFeatureImpl geographicalname = (SimpleFeatureImpl) SimpleFeatureBuilder
					.build((SimpleFeatureType) geoNameType, new Object[] {},
							"GeographicalName");
			geographicalname.setAttribute("spelling",
					spellingofnamepropertiescollection);
			geographicalname.setAttribute("language", _language.get(i));
			geographicalname.setAttribute("sourceOfName", _sourceOfName.get(i));*/
			
			if (_nativeness.get(i) != null)
				geographicalname.getProperty("nativeness").setValue(Collections.singleton(_nativeness.get(i).toString()));
			else
				geographicalname.getProperty("nativeness").setValue(null);
			if (_nameStatus.get(i) != null)
				geographicalname.getProperty("nameStatus").setValue(Collections
						.singleton(_nameStatus.get(i).toString()));
			else
				geographicalname.getProperty("nameStatus").setValue(null);
			if (_grammaticalGender.get(i) != null)
				geographicalname.getProperty("grammaticalGender").setValue(Collections
						.singleton(_grammaticalGender.get(i).toString()));
			else
				geographicalname.getProperty("grammaticalGender").setValue(null);
			if (_grammaticalNumber.get(i) != null)
				geographicalname.getProperty("grammaticalNumber").setValue(Collections
						.singleton(_grammaticalNumber.get(i).toString()));
			else
				geographicalname.getProperty("grammaticalNumber").setValue(null);

			
			/*if (_nativeness.get(i) != null)
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
				geographicalname.setAttribute("nameStatus", null);*/
			
			FeatureImpl pronunciation = (FeatureImpl) FeatureBuilder.buildFeature(pronunciationofnametype, null,false);
			pronunciation.getProperty("pronunciationIPA").setValue(_pronunciationIPA.get(i));
			pronunciation.getProperty("pronunciationSoundLink").setValue(_pronunciationSoundLink.get(i));
			geographicalname.getProperty("pronunciation").setValue(Collections.singleton(pronunciation));
			/*SimpleFeatureImpl pronunciation = (SimpleFeatureImpl) SimpleFeatureBuilder
					.build(pronunciationofnametype, new Object[] {},
							"PronunctiationOfName");
			pronunciation.setAttribute("pronunciationIPA", _pronunciationIPA
					.get(i));
			pronunciation.setAttribute("pronunciationSoundLink",
					_pronunciationSoundLink.get(i));
			geographicalname.setAttribute("pronunciation", Collections
					.singleton(pronunciation));*/
			geographicalnames.add(geographicalname);
		}
		// SET COLLECTION OF GEOGRAPHICALNAMES AS TARGET INPUT PARAMETER
		target.getProperty(targetProperty.getLocalname()).setValue(geographicalnames);
		/*((SimpleFeature) target).setAttribute(targetProperty.getLocalname(),
				geographicalnames);*/
		return target;
	}

	public Cell getParameters() {
		Cell parameterCell = new Cell();
		ComposedProperty entity1 = new ComposedProperty(new About(""));

		// ************* SETTING OF TYPE CONDITION FOR ENTITY1 ****************
		List<String> entityTypes = new ArrayList<String>();
		entityTypes.add(GeographicalName.class.getName());
		entityTypes.add(String.class.getName());
		entity1.setTypeCondition(entityTypes);

		ComposedProperty cpsp1 = new ComposedProperty(new About(""));

		Transformation transform = new Transformation();
		cpsp1.setTransformation(transform);
		entity1.setTransformation(transform);

		entity1.getCollection().add(cpsp1);

		// ************* SETTING OF TYPE CONDITION FOR ENTITY2 ****************
		Property entity2 = new Property(new About(""));
		entity2.setTypeCondition(entityTypes);

		parameterCell.setEntity1(entity1);
		parameterCell.setEntity2(entity2);
		return parameterCell;
	}

}
