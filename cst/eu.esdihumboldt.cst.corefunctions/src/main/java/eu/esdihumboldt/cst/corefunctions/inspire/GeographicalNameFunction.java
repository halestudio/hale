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
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.geotools.feature.AttributeImpl;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.PropertyImpl;
import org.opengis.feature.Feature;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.PropertyDescriptor;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.ext.IParameter;
import eu.esdihumboldt.cst.transformer.AbstractCstFunction;
import eu.esdihumboldt.goml.align.Cell;
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
 * This function enables the creation of an INPSIRE GeographicalName object from a set of simple string parameters.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @author Jose Ignacio Gisbert, Ana Belen Anton 
 * @partner 02 / ETRA Research and Development
 * @version $Id$ 
 */
public class GeographicalNameFunction 
	extends AbstractCstFunction {

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
	
	private Property variable = null;
	private Property targetProperty = null;
	
	private String _script = null;
	private String _transliteration = null;
	private NameStatusValue _nameStatus = null;
	private String _language = null;
	private NativenessValue _nativeness = null;
	private String _sourceOfName = null;
	private String _pronunciationIPA = null;
	private URI _pronunciationSoundLink = null;
	private GrammaticalGenderValue _grammaticalGender = null;
	private GrammaticalNumberValue _grammaticalNumber = null;
	
	/* (non-Javadoc)
	 * @see eu.esdihumboldt.cst.transformer.CstFunction#configure(eu.esdihumboldt.cst.align.ICell)
	 */
	public boolean configure(ICell cell) {
		for (IParameter ip : cell.getEntity1().getTransformation().getParameters()) {
			if(ip.getName().equals(GeographicalNameFunction.PROPERTY_SCRIPT))
			{
				this._script=ip.getValue();
			}
			else if(ip.getName().equals(GeographicalNameFunction.PROPERTY_TRANSLITERATION))
			{
				this._transliteration=ip.getValue();
			}
			else if(ip.getName().equals(GeographicalNameFunction.PROPERTY_NAMESTATUS))
			{
				this._nameStatus=NameStatusValue.valueOf(ip.getValue());
			}
			else if(ip.getName().equals(GeographicalNameFunction.PROPERTY_LANGUAGE))
			{
				this._language=ip.getValue();
			}
			else if(ip.getName().equals(GeographicalNameFunction.PROPERTY_NATIVENESS))
			{
				this._nativeness=NativenessValue.valueOf(ip.getValue());
			}
			else if(ip.getName().equals(GeographicalNameFunction.PROPERTY_SOURCEOFNAME))
			{
				this._sourceOfName=ip.getValue();
			}
			else if(ip.getName().equals(GeographicalNameFunction.PROPERTY_PRONUNCIATIONIPA))
			{
				this._pronunciationIPA=ip.getValue();
			}
			else if(ip.getName().equals(GeographicalNameFunction.PROPERTY_PRONUNCIATIONSOUNDLINK))
			{
				this._pronunciationSoundLink=URI.create(ip.getValue());
			}
			else if(ip.getName().equals(GeographicalNameFunction.PROPERTY_GRAMMA_GENDER))
			{
				this._grammaticalGender=GrammaticalGenderValue.valueOf(ip.getValue());
			}
			else if(ip.getName().equals(GeographicalNameFunction.PROPERTY_GRAMMA_NUMBER))
			{
				this._grammaticalNumber=GrammaticalNumberValue.valueOf(ip.getValue());
			}
			
		}
		for (Property p : ((ComposedProperty)cell.getEntity1()).getCollection()) {
			this.variable=p;
		}
		this.targetProperty = (Property) cell.getEntity2();
		
		if (this.variable == null) {
			throw new RuntimeException("The Source property must be defined.");
		}
		if (this.targetProperty == null) {
			throw new RuntimeException("The Target property must be defined.");
		}
		
		return true;
	}


	/* (non-Javadoc)
	 * @see eu.esdihumboldt.cst.transformer.CstFunction#transform(org.geotools.feature.FeatureCollection)
	 */
	public FeatureCollection<? extends FeatureType, ? extends Feature> transform(
			FeatureCollection<? extends FeatureType, ? extends Feature> fc) {
		// TODO Auto-generated method stub
		return null;
	} 

	/**
	 * @see eu.esdihumboldt.cst.transformer.CstFunction#transform(org.opengis.feature.Feature, org.opengis.feature.Feature)
	 */
	public Feature transform(Feature source, Feature target) {
		
		//check if the input features have the expected property name
		if (target.getProperties(targetProperty.getLocalname()).size()==0) return null;
		if (source.getProperties(variable.getLocalname()).size()==0) return null;
		
		try {
			Object result = source.getProperty(this.variable.getLocalname()).getValue();

			// inject result into target object
			PropertyDescriptor pd = target.getProperty(
					this.targetProperty.getLocalname()).getDescriptor();
			PropertyImpl p = null;
			if (pd.getType().getBinding().equals(GeographicalName.class)) {
				GeographicalName gn=new GeographicalName();
				gn.setGrammaticalGender(_grammaticalGender);
				gn.setGrammaticalNumber(_grammaticalNumber);
				gn.setLanguage(_language);
				gn.setNameStatus(_nameStatus);
				gn.setNativeness(_nativeness);
				gn.setSourceOfName(_sourceOfName);
				
				PronunciationOfName pn = new PronunciationOfName();
				pn.setPronunciationIPA(_pronunciationIPA);
				pn.setPronunciationSoundLink(_pronunciationSoundLink);
				gn.setPronunciation(pn);
				
				SpellingOfName sp = new SpellingOfName();
				sp.setScript(_script);
				sp.setText(result.toString());
				sp.setTransliterationScheme(_transliteration);
				gn.setSpelling(sp);
				
				p = new AttributeImpl(gn, (AttributeDescriptor) pd, null);
			}
			else {
				return null;
			}
			
			Collection<org.opengis.feature.Property> c = new HashSet<org.opengis.feature.Property>();
			c.add(p);
			target.setValue(c);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		
		return target;
	}

	@Override
	protected void setParametersTypes(Map<String, Class<?>> parametersTypes) {
		parameterTypes.put(GeographicalNameFunction.PROPERTY_TEXT, String.class);
		parameterTypes.put(GeographicalNameFunction.PROPERTY_SCRIPT, String.class);
		parameterTypes.put(GeographicalNameFunction.PROPERTY_TRANSLITERATION, String.class);
		parameterTypes.put(GeographicalNameFunction.PROPERTY_NAMESTATUS, String.class);
		parameterTypes.put(GeographicalNameFunction.PROPERTY_LANGUAGE, String.class);
		parameterTypes.put(GeographicalNameFunction.PROPERTY_NATIVENESS, String.class);
		parameterTypes.put(GeographicalNameFunction.PROPERTY_SOURCEOFNAME, String.class);
		parameterTypes.put(GeographicalNameFunction.PROPERTY_PRONUNCIATIONIPA, String.class);
		parameterTypes.put(GeographicalNameFunction.PROPERTY_PRONUNCIATIONSOUNDLINK, String.class);
		parameterTypes.put(GeographicalNameFunction.PROPERTY_GRAMMA_GENDER, String.class);
		parameterTypes.put(GeographicalNameFunction.PROPERTY_GRAMMA_NUMBER, String.class);
	}
	
	public Cell getParameters() {
		Cell parameterCell = new Cell();
		Property entity1 = new Property(new About(""));
		Property entity2 = new Property(new About(""));
	
		parameterCell.setEntity1(entity1);
		parameterCell.setEntity2(entity2);
		return parameterCell;
	}

}
