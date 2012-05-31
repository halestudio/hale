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

package eu.esdihumboldt.cst.functions.inspire;

import java.util.Map;

import javax.xml.namespace.QName;

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyValue;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.AbstractSingleTargetPropertyTransformation;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.NoResultException;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultInstance;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Class for the identifier function
 * 
 * @author Kevin Mais
 * 
 */
public class Identifier extends
		AbstractSingleTargetPropertyTransformation<TransformationEngine>
		implements IdentifierFunction {

	@Override
	protected Object evaluate(String transformationIdentifier,
			TransformationEngine engine,
			ListMultimap<String, PropertyValue> variables, String resultName,
			PropertyEntityDefinition resultProperty,
			Map<String, String> executionParameters, TransformationLog log)
			throws TransformationException, NoResultException {

		// get input
		PropertyValue input = variables.get(null).get(0);
		// source value as string (will be written into localid)
		String inputValue = input.getValueAs(String.class);

		// get all values for the parameters set by the parameter page
		String countryName = getParameterChecked(COUNTRY_PARAMETER_NAME);
		String providerName = getParameterChecked(DATA_PROVIDER_PARAMETER_NAME);
		String productName = getParameterChecked(PRODUCT_PARAMETER_NAME);
		String version = getParameterChecked(VERSION);
		String versionNilReason = getParameterChecked(VERSION_NIL_REASON);

		// definition of the target property (inspireId in this case)
		TypeDefinition def = resultProperty.getDefinition().getParentType();

		// instance that can be changed (add property/instance as child)
		DefaultInstance inst = new DefaultInstance(def, null);

		// child instance of "inst" (Identifier in this case)
		DefaultInstance ident = new DefaultInstance(resultProperty
				.getDefinition().getPropertyType(), null);

		// versionId instance with own value + one property
		DefaultInstance versionId = new DefaultInstance(ident.getDefinition(), null);
		
		// set the child properties of "ident"
		ident.addProperty(new QName("localid"), inputValue);
		ident.addProperty(new QName("namespace"), countryName + ":"
				+ providerName + ":" + productName + ":" + resultName);
		
		// Set value and property of "versionId"
		versionId.setValue(version);
		versionId.addProperty(new QName(""), versionNilReason);
		
		// "add versionId" to "ident"
		ident.addProperty(new QName("versionId"), versionId);
		
		// add the full "ident" to "inst"
		inst.addProperty(new QName("Identifier"), ident);

		return inst;
	}

}
