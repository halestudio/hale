/*
 * Copyright (c) 2013 Fraunhofer IGD
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
 *     Fraunhofer IGD
 */

package eu.esdihumboldt.hale.io.xslt.citygml;

import java.text.MessageFormat;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.io.gml.CityGMLConstants;
import eu.esdihumboldt.hale.io.xsd.model.XmlElement;
import eu.esdihumboldt.hale.io.xsd.model.XmlIndex;
import eu.esdihumboldt.hale.io.xslt.SourceContextProvider;
import eu.esdihumboldt.hale.io.xslt.XsltExport;

/**
 * Specific XSLT export for mappings with a CityGML based schema as target
 * model.
 * 
 * @author Simon Templer
 */
public class CityGMLXsltExport extends XsltExport implements CityGMLConstants {

	private SourceContextProvider sourceContext;

	@Override
	protected void init(XmlIndex sourceIndex, XmlIndex targetIndex)
			throws IOProviderConfigurationException {
		super.init(sourceIndex, targetIndex);

		// scan target schema for CityModel
		XmlElement targetCityModel = findCityModel(targetIndex);
		if (targetCityModel != null) {
			QName name = targetCityModel.getName();
			setParameter(PARAM_ROOT_ELEMENT_NAMESPACE, new ParameterValue(name.getNamespaceURI()));
			setParameter(PARAM_ROOT_ELEMENT_NAME, new ParameterValue(name.getLocalPart()));
		}
		else {
			throw new IOProviderConfigurationException(MessageFormat.format(
					"Element {0} not found in the target schema.", CITY_MODEL_ELEMENT));
		}

		// scan source schema for CityModel
		XmlElement sourceCityModel = findCityModel(sourceIndex);
		if (sourceCityModel != null) {
			// create a custom source context
			sourceContext = new CityGMLSourceContext(sourceCityModel);

			// TODO copy envelope?
		}
	}

	private XmlElement findCityModel(XmlIndex schema) {
		for (XmlElement element : schema.getElements().values()) {
			QName name = element.getName();

			if (CITY_MODEL_ELEMENT.equals(name.getLocalPart())
					&& name.getNamespaceURI().startsWith(CITYGML_NAMESPACE_CORE)) {
				return element;
			}
		}

		return null;
	}

	@Override
	protected SourceContextProvider getSourceContext() {
		return sourceContext;
	}

}
