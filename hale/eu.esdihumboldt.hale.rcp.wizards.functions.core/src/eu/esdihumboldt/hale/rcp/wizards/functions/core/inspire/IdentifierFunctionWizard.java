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
package eu.esdihumboldt.hale.rcp.wizards.functions.core.inspire;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.jface.wizard.Wizard;

import eu.esdihumboldt.commons.goml.align.Cell;
import eu.esdihumboldt.commons.goml.align.Entity;
import eu.esdihumboldt.commons.goml.oml.ext.Parameter;
import eu.esdihumboldt.commons.goml.oml.ext.Transformation;
import eu.esdihumboldt.commons.goml.rdf.Resource;
import eu.esdihumboldt.cst.corefunctions.inspire.IdentifierFunction;
import eu.esdihumboldt.hale.rcp.wizards.functions.AbstractSingleCellWizard;
import eu.esdihumboldt.hale.rcp.wizards.functions.AlignmentInfo;
import eu.esdihumboldt.hale.rcp.wizards.functions.core.Messages;
import eu.esdihumboldt.specification.cst.align.ICell;
import eu.esdihumboldt.specification.cst.align.ext.IParameter;

/**
 * FIXME Add Type description.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class IdentifierFunctionWizard 
	extends AbstractSingleCellWizard {
	
	private IdentifierFunctionWizardPage mainPage;

	/**
	 * @see AbstractSingleCellWizard#AbstractSingleCellWizard(AlignmentInfo)
	 */
	public IdentifierFunctionWizard(AlignmentInfo selection) {
		super(selection);
	}

	/**
	 * @see AbstractSingleCellWizard#init()
	 */
	@Override
	protected void init() {
		Cell cell = getResultCell();
		
		Map<String,String> params = new HashMap<String,String>();
		
		if (cell.getEntity1	().getTransformation() != null) {
          List<IParameter> parameters = cell.getEntity1().getTransformation().getParameters();
			
			if (parameters != null) {
				Iterator<IParameter> it = parameters.iterator();
				while (it.hasNext()) {
					IParameter param = it.next();
					params.put(param.getName(),param.getValue());
				}
			}
		}
		String country = params.get(IdentifierFunction.COUNTRY_PARAMETER_NAME);
		if (country == null) {
			country = Locale.getDefault().getLanguage();		
		}
		String provider = params.get(IdentifierFunction.DATA_PROVIDER_PARAMETER_NAME);
		if (provider == null) {
			provider = Locale.getDefault().getLanguage();		
		}
		String product = params.get(IdentifierFunction.PRODUCT_PARAMETER_NAME);
		if (product == null) {
			product = Locale.getDefault().getLanguage();		
		}
		String version = params.get(IdentifierFunction.VERSION);
		if (version == null) {
			version = Locale.getDefault().getLanguage();		
		}
		String versionNil = params.get(IdentifierFunction.VERSION_NIL_REASON);
		if (versionNil == null) {
			versionNil = Locale.getDefault().getLanguage();		
		}
		
		this.mainPage = new IdentifierFunctionWizardPage(Messages.IdentifierFunctionWizard_0,
				country, provider, product, version, versionNil);
	}

	/**
	 * @see Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		ICell cell = getResultCell();
		
		Transformation t = new Transformation();
		t.setService(new Resource(IdentifierFunction.class.getName()));
		t.getParameters().add(
				new Parameter(
						IdentifierFunction.COUNTRY_PARAMETER_NAME, 
						mainPage.getCountryCode()));
		t.getParameters().add(
				new Parameter(
						IdentifierFunction.DATA_PROVIDER_PARAMETER_NAME, 
						mainPage.getProviderName()));
		t.getParameters().add(
				new Parameter(
						IdentifierFunction.PRODUCT_PARAMETER_NAME, 
						mainPage.getProductName()));
		t.getParameters().add(
				new Parameter(
						IdentifierFunction.VERSION, 
						mainPage.getVersion()));
		t.getParameters().add(
				new Parameter(
						IdentifierFunction.VERSION_NIL_REASON, 
						mainPage.getVersionNilReason()));
		
		((Entity) cell.getEntity1()).setTransformation(t);
		
		return true;
	}
	
	/**
	 * @see Wizard#addPages()
	 */
	@Override
	public void addPages() {
		addPage(mainPage);
	}

}
