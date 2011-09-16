/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.rcp.wizards.functions.core.inspire.geographicname;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.wizard.Wizard;

import eu.esdihumboldt.commons.goml.align.Cell;
import eu.esdihumboldt.commons.goml.oml.ext.Parameter;
import eu.esdihumboldt.commons.goml.oml.ext.Transformation;
import eu.esdihumboldt.commons.goml.omwg.ComposedProperty;
import eu.esdihumboldt.commons.goml.omwg.Property;
import eu.esdihumboldt.commons.goml.rdf.Resource;
import eu.esdihumboldt.cst.corefunctions.inspire.GeographicalNameFunction;
import eu.esdihumboldt.hale.rcp.wizards.functions.core.Messages;
import eu.esdihumboldt.hale.rcp.wizards.functions.core.inspire.geographicname.GeographicNamePage.SpellingType;
import eu.esdihumboldt.hale.ui.model.functions.AbstractSingleComposedCellWizard;
import eu.esdihumboldt.hale.ui.model.functions.AlignmentInfo;
import eu.esdihumboldt.specification.cst.align.ICell;
import eu.esdihumboldt.specification.cst.align.ext.IParameter;

/**
 * Wizard for the {@link GeographicNameFunction}.
 * 
 * @author Anna Pitaev
 * @partner 04 / Logica
 */
public class GeographicNameFunctionWizard extends
		AbstractSingleComposedCellWizard {

	private static final int SPELLING_ATTRIBUTE_NUMBER = 3;
	private GeographicNamePage page;

	/**
	 * @see AbstractSingleComposedCellWizard#AbstractSingleComposedCellWizard(AlignmentInfo)
	 */
	public GeographicNameFunctionWizard(AlignmentInfo selection) {
		super(selection);
	}

	/**
	 * @see AbstractSingleComposedCellWizard#init()
	 */
	@Override
	protected void init() {

		ICell cell = getResultCell();
//		String text = null;
//		String script = null;
		String transliteration = null;
		String ipa = null;
		String language = null;
		String sourceOfName = null;
		String nameStatus = null;
		String nativeness = null;
		String gender = null;
		String number = null;

		// init transformation parameters from cell

		if (cell.getEntity1()instanceof ComposedProperty && ((ComposedProperty)cell.getEntity1()).getCollection()!= null && ((ComposedProperty)cell.getEntity1()).getCollection().size()==1) {
			// edit existing cell
			ComposedProperty outerCP = (ComposedProperty) cell.getEntity1();
			ComposedProperty innerCP = (ComposedProperty) outerCP.getCollection().get(0);
			List<IParameter> parameters = innerCP.getTransformation().getParameters();

			if (parameters != null) {
				Iterator<IParameter> it = parameters.iterator();

				while (it.hasNext()) {
					IParameter param = it.next();
					String paramValue = param.getValue();
					if (param.getName().equals(
							GeographicalNameFunction.PROPERTY_GRAMMA_GENDER)) {
						gender = paramValue;
					} else if (param.getName().equals(
							GeographicalNameFunction.PROPERTY_GRAMMA_NUMBER)) {
						number = paramValue;
					} else if (param.getName().equals(
							GeographicalNameFunction.PROPERTY_LANGUAGE)) {
						language = paramValue;
					} else if (param.getName().equals(
							GeographicalNameFunction.PROPERTY_NAMESTATUS)) {
						nameStatus = paramValue;
					} else if (param.getName().equals(
							GeographicalNameFunction.PROPERTY_NATIVENESS)) {
						nativeness = paramValue;
					} else if (param.getName().equals(
							GeographicalNameFunction.PROPERTY_PRONUNCIATIONIPA)) {
						ipa = paramValue;

					} else if (param.getName().equals(
							GeographicalNameFunction.PROPERTY_TRANSLITERATION)) {
						transliteration = paramValue;
					} else if (param.getName().equals(GeographicalNameFunction.PROPERTY_SOURCEOFNAME)) {
						sourceOfName = paramValue;
					}
				}
			}
		}
		ArrayList<SpellingType> spellings = new ArrayList<SpellingType>();
		String spText = null;
		String spScript = null;
		String spTransliteration = null;
		SpellingType tmpSpelling = null;
		List<IParameter> spellingAttributes = null;
		// GET Spelling Properties from PropertyComposition
		if (cell.getEntity1() instanceof ComposedProperty) {
			ComposedProperty cp = (ComposedProperty) cell.getEntity1();
			List<Property> propCollection = cp.getCollection();
			// check that the list consists of 1 Property which is a
			// ComposedProperty
			if (propCollection.size() == 1
					&& propCollection.get(0) instanceof ComposedProperty) {
				ComposedProperty subCP = (ComposedProperty) propCollection
						.get(0);
				List<Property> spellingProps = subCP.getCollection();
				for (Property property : spellingProps) {
					if (property.getTransformation() != null
							&& property.getTransformation().getParameters() != null
							&& property.getTransformation().getParameters()
									.size() == SPELLING_ATTRIBUTE_NUMBER) {
						spellingAttributes = property.getTransformation()
								.getParameters();
						for (IParameter param : spellingAttributes) {
							if (param.getName().equals(
									GeographicalNameFunction.PROPERTY_TEXT)) {
								spText = param.getValue();
							} else if (param.getName().equals(
									GeographicalNameFunction.PROPERTY_SCRIPT)) {
								spScript = param.getValue();
							} else if (param
									.getName()
									.equals(
											GeographicalNameFunction.PROPERTY_TRANSLITERATION)) {
								spTransliteration = param.getValue();
							}
						}
					}
					// 1.create SpellingType
					tmpSpelling = new SpellingType(property);
					tmpSpelling.setScript(spScript);
					tmpSpelling.setTransliteration(spTransliteration);
					// 2. add SpellingType to the List of Spellings
					spellings.add(tmpSpelling);
				}
			}
		}

		this.page = new GeographicNamePage("main", //$NON-NLS-1$
				Messages.GeographicNameFunctionWizard_1, null);
		super.setWindowTitle(Messages.GeographicNameFunctionWizard_2);
		this.page.setGender(gender);
		this.page.setIpa(ipa);
		this.page.setLanguage(language);
		this.page.setNameStatus(nameStatus);
		this.page.setNativeness(nativeness);
		this.page.setNumber(number);
		this.page.setSourceOfName(sourceOfName);
		this.page.setSpellings(spellings);
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		Cell cell = getResultCell();
		ComposedProperty outerCP;
		ComposedProperty innerCP;
		
		if (cell.getEntity1()instanceof ComposedProperty && ((ComposedProperty)cell.getEntity1()).getCollection()!= null && ((ComposedProperty)cell.getEntity1()).getCollection().size()==1) {
			// edit existing cell
			outerCP = (ComposedProperty) cell.getEntity1();
			innerCP = (ComposedProperty) outerCP.getCollection().get(0);
		}
		else {
			// new cell -> property or inner composed property 
			//check if  Entity1 is ComposedProperty
			if (! (cell.getEntity1() instanceof ComposedProperty) ){
				//create Composed Property
				Property property = (Property) cell.getEntity1();
				innerCP = new ComposedProperty(property.getNamespace());
				innerCP.setCollection(Collections.singletonList(property));
				//A.P. innerCP included in outerCP
				//cell.setEntity1(innerCP);
			} else {
				innerCP = (ComposedProperty)cell.getEntity1();
			}
			
			outerCP = new ComposedProperty(innerCP.getNamespace());
			List<Property> propList = new ArrayList<Property>();
			propList.add(innerCP);
			outerCP.setCollection(propList);
			
			cell.setEntity1(outerCP);
		}

		// 1. configure composed property for the cell
		// 2. set Transformation for the cell
		 //Transformation  cpT = new Transformation(new Resource(GeographicalNameFunction.class.getName()));
		 Transformation cpT = new Transformation();
		 cpT.setService(new Resource(GeographicalNameFunction.class.getName()));
		 //add geographical name common parameters to is transformation
		 List<IParameter> gnParams = new ArrayList<IParameter>();
		 //set language
		 IParameter gnParam = new Parameter (GeographicalNameFunction.PROPERTY_LANGUAGE, this.page.getLanguage());
		 gnParams.add(gnParam);
		 //nativeness
		 gnParam = new Parameter (GeographicalNameFunction.PROPERTY_NATIVENESS, this.page.getNativeness());
		 gnParams.add(gnParam);
		 //nameStatus
		 gnParam = new Parameter (GeographicalNameFunction.PROPERTY_NAMESTATUS, this.page.getNameStatus());
		 gnParams.add(gnParam);
		 //sourceOfName
		 gnParam = new Parameter (GeographicalNameFunction.PROPERTY_SOURCEOFNAME, this.page.getSourceOfName());
		 gnParams.add(gnParam);
		 //pronunciationIPA
		 gnParam = new Parameter (GeographicalNameFunction.PROPERTY_PRONUNCIATIONIPA, this.page.getIpa());
		 gnParams.add(gnParam);
		 //pronunciationSoundLink
		 //gnParam = new Parameter (GeographicalNameFunction.PROPERTY_PRONUNCIATIONSOUNDLINK, this.page.get());
		 //grammaticalGender
		 gnParam = new Parameter (GeographicalNameFunction.PROPERTY_GRAMMA_GENDER, this.page.getGender());
		 gnParams.add(gnParam);
		 //grammaticalNumber
		 gnParam = new Parameter (GeographicalNameFunction.PROPERTY_GRAMMA_NUMBER, this.page.getNumber());
		 gnParams.add(gnParam);
		 cpT.setParameters(gnParams);
		 innerCP.setTransformation(cpT);
		 //3. create collection of the properties and set it to the ComposedProperty
		 ArrayList<SpellingType> spellings = page.getSpellings();  
		 //4. add to the property list a new Composed Property 1 containing a collection of the spelling specific parameters
		 //4.a set transformation for comProp1
		 //4b. create a list of properties for comProp1
		 for (Property property : innerCP.getCollection()) {
			 for (SpellingType spelling : spellings){
				 if (property.getAbout().getAbout().equals(spelling.getProperty().getAbout().getAbout())) {
					 Transformation transformation = new Transformation();
					 //FIXME if I know one thing it is that the next line shouldn't be like that
					 transformation.setService((new Resource("some spelling functionSpellingFunction"))); //$NON-NLS-1$
					 //add spelling parameters to the transformation 
					 List<IParameter> params = new ArrayList<IParameter>();
					 IParameter param = new Parameter(GeographicalNameFunction.PROPERTY_TEXT, spelling.getProperty().getLocalname());
					 params.add(param);
					 param = new Parameter(GeographicalNameFunction.PROPERTY_SCRIPT, spelling.getScript());
					 params.add(param);
					 param = new Parameter(GeographicalNameFunction.PROPERTY_TRANSLITERATION, spelling.getTransliteration());
					 params.add(param);
					 transformation.setParameters(params);
					 property.setTransformation(transformation);
				 }
			 }
		 }
		 //6. update Entity 1 in the cell
		 Transformation cpO = new Transformation();
		 cpO.setService(new Resource(GeographicalNameFunction.class.getName()));
		 outerCP.setTransformation(cpO);
		 
		
		/*ComposedProperty maincp=null;
		DetailedAbout ab = null;
		try{
			maincp= (ComposedProperty) cell.getEntity1();
		}catch(Exception ex){
			try{
			 Property p = (Property)cell.getEntity1();
			 ab=(DetailedAbout)p.getAbout();			 
			}
			catch(Exception ex2){}
		}
		if (maincp==null)
				maincp = new ComposedProperty(new About("http://www.esdi-humboldt.eu", "FT1"));
		ComposedProperty geograf=null;
		if(ab==null) 
			geograf = new ComposedProperty(new About("http://www.esdi-humboldt.eu", "FT1"));
		else
			geograf = new ComposedProperty(ab);
		Transformation t = new Transformation();
		t.setService(new Resource(GeographicalNameFunction.class.getName()));

		ArrayList<SpellingType> spellings = page.getSpellings();
		for(int i=0;i<spellings.size();i++)
		{
			Property p = new Property(new About("urn:x-inspire:specification:gmlas-v31:Hydrography:2.0", "FT1","sourceprop"+i));
			Transformation tp = new Transformation();
			tp.setService(new Resource(GeographicalNameFunction.class.getName()));
			tp.getParameters().add(new Parameter ("script", spellings.get(i).getScript()));
			tp.getParameters().add(new Parameter("text",spellings.get(i).getText()));
			tp.getParameters().add(new Parameter("transliterationScheme",spellings.get(i).getTransliteration()));
			p.setTransformation(tp);
			geograf.getCollection().add(p);
		}

		// add parameters

		/*
		 * ComposedProperty maincp=null; DetailedAbout ab = null; try{ maincp=
		 * (ComposedProperty) cell.getEntity1(); }catch(Exception ex){ try{
		 * Property p = (Property)cell.getEntity1();
		 * ab=(DetailedAbout)p.getAbout(); } catch(Exception ex2){} } if
		 * (maincp==null) maincp = new ComposedProperty(new
		 * About("http://www.esdi-humboldt.eu", "FT1")); ComposedProperty
		 * geograf=null; if(ab==null) geograf = new ComposedProperty(new
		 * About("http://www.esdi-humboldt.eu", "FT1")); else geograf = new
		 * ComposedProperty(ab); Transformation t = new Transformation();
		 * t.setService(new Resource(GeographicalNameFunction.class.getName()));
		 * 
		 * ArrayList<SpellingType> spellings = page.getSpellings(); for(int
		 * i=0;i<spellings.size();i++) { Property p = new Property(new
		 * About("urn:x-inspire:specification:gmlas-v31:Hydrography:2.0",
		 * "FT1","sourceprop"+i)); Transformation tp = new Transformation();
		 * tp.setService(new
		 * Resource(GeographicalNameFunction.class.getName()));
		 * tp.getParameters().add(new Parameter ("script",
		 * spellings.get(i).getScript())); tp.getParameters().add(new
		 * Parameter("text",spellings.get(i).getText()));
		 * tp.getParameters().add(new
		 * Parameter("transliterationScheme",spellings
		 * .get(i).getTransliteration())); p.setTransformation(tp);
		 * geograf.getCollection().add(p); }
		 * 
		 * // add parameters
		 * 
		 * 
		 * // text t.getParameters().add( new
		 * Parameter(GeographicalNameFunction.PROPERTY_TEXT, page .getText()));
		 * // script t.getParameters().add( new
		 * Parameter(GeographicalNameFunction.PROPERTY_SCRIPT, page
		 * .getScript())); // transliteration t.getParameters().add( new
		 * Parameter( GeographicalNameFunction.PROPERTY_TRANSLITERATION, page
		 * .getTransliteration()));
		 * 
		 * // ipa t.getParameters().add( new Parameter(
		 * GeographicalNameFunction.PROPERTY_PRONUNCIATIONIPA,page.getIpa()));
		 * // language t.getParameters().add( new
		 * Parameter(GeographicalNameFunction.PROPERTY_LANGUAGE,
		 * page.getLanguage())); // source of Name t.getParameters().add( new
		 * Parameter
		 * (GeographicalNameFunction.PROPERTY_SOURCEOFNAME,page.getSourceOfName
		 * ())); // name status t.getParameters().add( new
		 * Parameter(GeographicalNameFunction
		 * .PROPERTY_NAMESTATUS,page.getNameStatus())); // nativeness
		 * t.getParameters().add( new
		 * Parameter(GeographicalNameFunction.PROPERTY_NATIVENESS
		 * ,page.getNativeness())); // gender t.getParameters().add( new
		 * Parameter
		 * (GeographicalNameFunction.PROPERTY_GRAMMA_GENDER,page.getGender()));
		 * // number t.getParameters().add( new
		 * Parameter(GeographicalNameFunction
		 * .PROPERTY_GRAMMA_NUMBER,page.getNumber()));
		 * 
		 * Transformation tsp = new Transformation(); tsp.setService(new
		 * Resource(GeographicalNameFunction.class.getName()));
		 * 
		 * 
		 * geograf.setTransformation(t); maincp.getCollection().add(geograf);
		 * cell.setEntity1(maincp); //((Entity)
		 * cell.getEntity1()).setTransformation(t);
		 */
		return true;
	}

	/**
	 * @see Wizard#addPages()
	 */
	@Override
	public void addPages() {
		addPage(page);
	}

	@Override
	public boolean canFinish() {
		return page.isPageComplete();

	}

}
