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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.wizard.Wizard;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.ext.IParameter;
import eu.esdihumboldt.cst.corefunctions.inspire.GeographicalNameFunction;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.oml.ext.Parameter;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.goml.omwg.ComposedProperty;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.goml.rdf.About;
import eu.esdihumboldt.goml.rdf.Resource;
import eu.esdihumboldt.hale.rcp.wizards.functions.AbstractSingleComposedCellWizard;
import eu.esdihumboldt.hale.rcp.wizards.functions.AlignmentInfo;
import eu.esdihumboldt.hale.rcp.wizards.functions.core.inspire.geographicname.GeographicNamePage.SpellingType;

/**
 * Wizard for the {@link GeographicNameFunction}.
 * 
 * @author Anna Pitaev
 * @partner 04 / Logica
 * @version $Id$
 */
public class GeographicNameFunctionWizard extends
		AbstractSingleComposedCellWizard {

	private GeographicNamePage page;

	/**
	 * @param selection
	 */
	public GeographicNameFunctionWizard(AlignmentInfo selection) {
		super(selection);

	}

	/**
	 * @see eu.esdihumboldt.hale.rcp.wizards.functions.AbstractSingleCellWizard#init()
	 */
	@Override
	protected void init() {

		ICell cell = getResultCell();
		String text = null;
		String script = null;
		String transliteration = null;
		String ipa = null;
		String language = null;
		String sourceOfName = null;
		String nameStatus = null;
		String nativeness = null;
		String gender = null;
		String number = null;
		

		// init transformation parameters from cell
		if (cell.getEntity1().getTransformation() != null) {
			List<IParameter> parameters = cell.getEntity1().getTransformation()
					.getParameters();

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
							GeographicalNameFunction.PROPERTY_SCRIPT)) {
						script = paramValue;
					} else if (param.getName().equals(
							GeographicalNameFunction.PROPERTY_SOURCEOFNAME)) {
						sourceOfName = paramValue;
					} else if (param.getName().equals(
							GeographicalNameFunction.PROPERTY_TEXT)) {
						text = paramValue;
					} else if (param.getName().equals(
							GeographicalNameFunction.PROPERTY_TRANSLITERATION)) {
						transliteration = paramValue;
					}
				}
			}
		}
		this.page = new GeographicNamePage("main",
				"Configure Geographic Name Function", null);
		super.setWindowTitle("INSPIRE Geographic Name Function Wizard");
		this.page.setGender(gender);
		this.page.setIpa(ipa);
		this.page.setLanguage(language);
		this.page.setNameStatus(nameStatus);
		this.page.setNativeness(nativeness);
		this.page.setNumber(number);
		/*
		 * this.page.setTransliteration(transliteration);
		 * this.page.setText(text);
		 */
		this.page.setSourceOfName(sourceOfName);
		/* this.page.setScript(script); */

	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		Cell cell = getResultCell();
		ComposedProperty cp = null;
		//check if  Entity1 is ComposedProperty
		if (! (cell.getEntity1() instanceof ComposedProperty) ){
			//create Composed Property
			Property property = (Property) cell.getEntity1();
			cp = new ComposedProperty(new About(property.getNamespace()));
			
		}else{
			cp = (ComposedProperty)cell.getEntity1();
		}
			
		//TODO handle case if the mapping already exists
		// 1. configure composed property for the cell
		// ComposedProperty cp = (ComposedProperty)cell.getEntity1();
		
		 //2. set Transformation for the cell
		 Transformation  cpT = new Transformation(new Resource(GeographicalNameFunction.class.getName()));
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
		 cp.setTransformation(cpT);
		 //3. create collection of the properties and set it to the ComposedProperty
		 ArrayList<SpellingType> spellings = page.getSpellings();  
		 List<Property> propCollection = new ArrayList<Property>();
		 //4. add to the property list a new Composed Property 1 containing a collection of the spelling specific parameters
		 ComposedProperty compProp1 = new ComposedProperty(cp.getAbout());
		 //4.a set transformation for comProp1
		 compProp1.setTransformation(cpT);
		 //4b. create a list of properties for comProp1
		 List<Property> props = new ArrayList<Property>();
		 for (SpellingType spelling : spellings){
			 Property property = new Property(cp.getAbout());
			 Transformation transformation = new Transformation(new Resource("some spelling functionSpellingFunction"));
			 //add spelling parameters to the transformation 
			 List<IParameter> params = new ArrayList<IParameter>();
			 IParameter param = new Parameter(GeographicalNameFunction.PROPERTY_TEXT, spelling.getText());
			 params.add(param);
			 param = new Parameter(GeographicalNameFunction.PROPERTY_SCRIPT, spelling.getScript());
			 params.add(param);
			 param = new Parameter(GeographicalNameFunction.PROPERTY_TRANSLITERATION, spelling.getTransliteration());
			 transformation.setParameters(params);
			 props.add(property);
			 
		 }
		compProp1.setCollection(props);
		propCollection.add(compProp1);
		cp.setCollection(propCollection);
		 //6. update Entity 1 in the cell
		cell.setEntity1(cp);
		 
		
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

		
		 * // text t.getParameters().add( new
		 * Parameter(GeographicalNameFunction.PROPERTY_TEXT, page .getText()));
		 * // script t.getParameters().add( new
		 * Parameter(GeographicalNameFunction.PROPERTY_SCRIPT, page
		 * .getScript())); // transliteration t.getParameters().add( new
		 * Parameter( GeographicalNameFunction.PROPERTY_TRANSLITERATION, page
		 * .getTransliteration()));
		 
		// ipa
		t.getParameters().add(
				new Parameter(
						GeographicalNameFunction.PROPERTY_PRONUNCIATIONIPA,page.getIpa()));
		// language
		t.getParameters().add(
				new Parameter(GeographicalNameFunction.PROPERTY_LANGUAGE, page.getLanguage()));
		// source of Name
		t.getParameters().add(
				new Parameter(GeographicalNameFunction.PROPERTY_SOURCEOFNAME,page.getSourceOfName()));
		// name status
		t.getParameters().add(
				new Parameter(GeographicalNameFunction.PROPERTY_NAMESTATUS,page.getNameStatus()));
		// nativeness
		t.getParameters().add(
				new Parameter(GeographicalNameFunction.PROPERTY_NATIVENESS,page.getNativeness()));
		// gender
		t.getParameters().add(
				new Parameter(GeographicalNameFunction.PROPERTY_GRAMMA_GENDER,page.getGender()));
		// number
		t.getParameters().add(
				new Parameter(GeographicalNameFunction.PROPERTY_GRAMMA_NUMBER,page.getNumber()));
		
		Transformation tsp = new Transformation();
		tsp.setService(new Resource(GeographicalNameFunction.class.getName()));
		
					
		geograf.setTransformation(t);
		maincp.getCollection().add(geograf);
		cell.setEntity1(maincp);
		//((Entity) cell.getEntity1()).setTransformation(t);
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
