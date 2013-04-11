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

package eu.esdihumboldt.hale.io.xslt;

import java.text.MessageFormat;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.io.gml.CityGMLConstants;
import eu.esdihumboldt.hale.io.xsd.model.XmlElement;
import eu.esdihumboldt.hale.io.xsd.model.XmlIndex;

/**
 * Specific XSLT export for mappings with a CityGML based schema as target
 * model.
 * 
 * @author Simon Templer
 */
public class CityGMLXsltExport extends XsltExport implements CityGMLConstants {

	@Override
	protected void init(XmlIndex sourceIndex, XmlIndex targetIndex)
			throws IOProviderConfigurationException {
		super.init(sourceIndex, targetIndex);

		boolean found = false;
		for (XmlElement element : targetIndex.getElements().values()) {
			QName name = element.getName();

			if (CITY_MODEL_ELEMENT.equals(name.getLocalPart())
					&& name.getNamespaceURI().startsWith(CITYGML_NAMESPACE_CORE)) {
				setParameter(PARAM_ROOT_ELEMENT_NAMESPACE,
						new ParameterValue(name.getNamespaceURI()));
				setParameter(PARAM_ROOT_ELEMENT_NAME, new ParameterValue(name.getLocalPart()));
				found = true;
			}
		}

		if (!found) {
			throw new IOProviderConfigurationException(MessageFormat.format(
					"Element {0} not found in the target schema.", CITY_MODEL_ELEMENT));
		}
	}

}
