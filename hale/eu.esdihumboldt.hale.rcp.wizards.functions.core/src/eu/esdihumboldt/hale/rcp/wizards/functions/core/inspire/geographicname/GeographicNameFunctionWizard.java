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

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.wizard.Wizard;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.ext.IParameter;
import eu.esdihumboldt.cst.corefunctions.GenericMathFunction;
import eu.esdihumboldt.cst.corefunctions.inspire.GeographicalNameFunction;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.align.Entity;
import eu.esdihumboldt.goml.oml.ext.Parameter;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.goml.rdf.Resource;
import eu.esdihumboldt.hale.rcp.wizards.functions.AbstractSingleCellWizard;
import eu.esdihumboldt.hale.rcp.wizards.functions.AbstractSingleComposedCellWizard;
import eu.esdihumboldt.hale.rcp.wizards.functions.AlignmentInfo;
import eu.esdihumboldt.hale.rcp.wizards.functions.core.math.MathFunctionPage;

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
		this.page.setTransliteration(transliteration);
		this.page.setText(text);
		this.page.setSourceOfName(sourceOfName);
		this.page.setScript(script);

	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		ICell cell = getResultCell();
		Transformation t = new Transformation();
		t.setService(new Resource(GeographicalNameFunction.class.getName()));
		// add parameters

		// text
		t.getParameters().add(
				new Parameter(GeographicalNameFunction.PROPERTY_TEXT, page
						.getText()));
		// script
		t.getParameters().add(
				new Parameter(GeographicalNameFunction.PROPERTY_SCRIPT, page
						.getScript()));
		// transliteration
		t.getParameters().add(
				new Parameter(
						GeographicalNameFunction.PROPERTY_TRANSLITERATION, page
								.getTransliteration()));
		// ipa
		t.getParameters().add(
				new Parameter(
						GeographicalNameFunction.PROPERTY_PRONUNCIATIONIPA,
						page.getIpa()));
		// language
		t.getParameters().add(
				new Parameter(GeographicalNameFunction.PROPERTY_LANGUAGE, page
						.getLanguage()));
		// source of Name
		t.getParameters().add(
				new Parameter(GeographicalNameFunction.PROPERTY_SOURCEOFNAME,
						page.getSourceOfName()));
		// name status
		t.getParameters().add(
				new Parameter(GeographicalNameFunction.PROPERTY_NAMESTATUS,
						page.getNameStatus()));
		// nativeness
		t.getParameters().add(
				new Parameter(GeographicalNameFunction.PROPERTY_NATIVENESS,
						page.getNativeness()));
		// gender
		t.getParameters().add(
				new Parameter(GeographicalNameFunction.PROPERTY_GRAMMA_GENDER,
						page.getGender()));
		// number
		t.getParameters().add(
				new Parameter(GeographicalNameFunction.PROPERTY_GRAMMA_NUMBER,
						page.getNumber()));
		((Entity) cell.getEntity1()).setTransformation(t);

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
