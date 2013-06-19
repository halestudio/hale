/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.cst.functions.inspire;

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
 * Class for the identifier function
 * 
 * @author Kevin Mais
 * 
 */
public class Identifier extends AbstractSingleTargetPropertyTransformation<TransformationEngine>
		implements IdentifierFunction {

	@Override
	protected Object evaluate(String transformationIdentifier, TransformationEngine engine,
			ListMultimap<String, PropertyValue> variables, String resultName,
			PropertyEntityDefinition resultProperty, Map<String, String> executionParameters,
			TransformationLog log) throws TransformationException, NoResultException {

		// get input
		PropertyValue input = variables.get(null).get(0);
		// source value as string (will be written into localid)
		String source = input.getValueAs(String.class);

		// get all values for the parameters set by the parameter page
		String countryName = getParameterChecked(COUNTRY_PARAMETER_NAME).as(String.class);
		String providerName = getParameterChecked(DATA_PROVIDER_PARAMETER_NAME).as(String.class);
		String productName = getParameterChecked(PRODUCT_PARAMETER_NAME).as(String.class);
		String version = getParameterChecked(VERSION).as(String.class);
		String versionNilReason = getParameterChecked(VERSION_NIL_REASON).as(String.class);

		// definition of the target property (inspireId in this case)
		TypeDefinition targetType = resultProperty.getDefinition().getPropertyType();

		// instance that can be changed (add property/instance as child)
		DefaultInstance inspireInstance = new DefaultInstance(targetType, null);

		// search for the child named "Identifier"
		PropertyDefinition inspireChildPropDef = Util.getChild("Identifier", targetType);

		// get type definition to create the "Identifier" instance
		TypeDefinition identType = inspireChildPropDef.getPropertyType();

		DefaultInstance identInstance = new DefaultInstance(identType, null);

		PropertyDefinition identChildLocal = Util.getChild("localId", identType);

		PropertyDefinition identChildNamespace = Util.getChild("namespace", identType);

		PropertyDefinition identChildVersion = Util.getChild("versionId", identType);

		TypeDefinition versionType = identChildVersion.getPropertyType();

		// 1.)
		// add the "localId" and "namespace" properties to the identifier
		// instance
		identInstance.addProperty(identChildLocal.getName(), source);
		identInstance.addProperty(identChildNamespace.getName(),
				getNamespace(countryName, providerName, productName, getTargetType()));

		DefaultInstance versionInstance = null;
		// 2.)
		// add the "nilReason" property to the version instance
		if (version == null || version.isEmpty()) {
			if (!versionNilReason.isEmpty()) {
				versionInstance = new DefaultInstance(versionType, null);
				PropertyDefinition versionIdChildVersion = Util.getChild("nilReason", versionType);
				versionInstance.addProperty(versionIdChildVersion.getName(), versionNilReason);
			}
		}
		else {
			versionInstance = new DefaultInstance(versionType, null);
			versionInstance.setValue(version);
		}

		// 3.)
		// add the "versionId" instance to the identifier instance
		if (versionInstance != null) {
			identInstance.addProperty(identChildVersion.getName(), versionInstance);
		}

		// 4.)
		// add the "identifier" instance to the inspireId instance
		inspireInstance.addProperty(inspireChildPropDef.getName(), identInstance);

		return inspireInstance;
	}

	/**
	 * Determine an Inspire Identifier namespace from the given information.
	 * 
	 * @param countryName the country
	 * @param providerName the provider name
	 * @param productName the product name
	 * @param targetType the target type
	 * @return the namespace
	 */
	public static String getNamespace(String countryName, String providerName, String productName,
			TypeDefinition targetType) {
		StringBuilder result = new StringBuilder();
		if (countryName == null || countryName.isEmpty()) {
			countryName = "_"; // default
		}

		result.append(countryName);
		result.append(':');
		if (providerName != null && !providerName.isEmpty()) {
			result.append(providerName);
			result.append(':');
		}
		if (productName != null && !productName.isEmpty()) {
			result.append(productName);
			result.append(':');
		}
		result.append(targetType.getDisplayName());

		return result.toString();
	}

}
