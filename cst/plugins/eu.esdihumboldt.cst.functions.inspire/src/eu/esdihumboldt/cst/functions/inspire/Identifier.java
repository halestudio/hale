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

import java.util.Collection;
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
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
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
		String source = input.getValueAs(String.class);

		// get all values for the parameters set by the parameter page
		String countryName = getParameterChecked(COUNTRY_PARAMETER_NAME);
		String providerName = getParameterChecked(DATA_PROVIDER_PARAMETER_NAME);
		String productName = getParameterChecked(PRODUCT_PARAMETER_NAME);
		String version = getParameterChecked(VERSION);
		String versionNilReason = getParameterChecked(VERSION_NIL_REASON);

		// definition of the target property (inspireId in this case)
		TypeDefinition targetType = resultProperty.getDefinition()
				.getPropertyType();

		// instance that can be changed (add property/instance as child)
		DefaultInstance inspireInstance = new DefaultInstance(targetType, null);

		// search for the child named "Identifier"
		PropertyDefinition targetChildPropDef = getChild("Identifier",
				targetType);

		// get type definition to create the "Identifier" instance
		TypeDefinition identType = targetChildPropDef.getPropertyType();

		DefaultInstance identInstance = new DefaultInstance(identType, null);

		PropertyDefinition identChildLocal = getChild("localId", identType);

		PropertyDefinition identChildNamespace = getChild("namespace",
				identType);

		PropertyDefinition identChildVersion = getChild("versionId", identType);

		TypeDefinition versionType = identChildVersion.getPropertyType();

		DefaultInstance versionInstance = new DefaultInstance(versionType, null);

		PropertyDefinition versionIdChildVersion = getChild("nilReason",
				versionType);

		// 1.)
		// add the "localId" and "namespace" properties to the identifier
		// instance
		identInstance.addProperty(identChildLocal.getName(), source);
		identInstance.addProperty(identChildNamespace.getName(), countryName
				+ ":" + providerName + ":" + productName + ":"
				+ resultProperty.getType().getDisplayName());

		// 2.)
		// add the "nilReason" property to the version instance or set the
		// version if it's not null or empty
		if (version == null || version.isEmpty()) {
			versionInstance.addProperty(versionIdChildVersion.getName(),
					versionNilReason);
		} else {
			versionInstance.setValue(version);
		}

		// 3.)
		// add the "versionId" instance to the identifier instance
		identInstance.addProperty(identChildVersion.getName(), versionInstance);

		// 4.)
		// add the "identifier" instance to the inspireId instance
		inspireInstance
				.addProperty(targetChildPropDef.getName(), identInstance);

		return inspireInstance;
	}

	private PropertyDefinition getChild(String localpart,
			TypeDefinition definition) {

		Collection<? extends ChildDefinition<?>> children = definition
				.getChildren();

		for (ChildDefinition<?> child : children) {
			if (child.asProperty() != null
					&& child.getName().getLocalPart().equals(localpart)) {
				return child.asProperty();
			}
		}

		return null;

	}

}
