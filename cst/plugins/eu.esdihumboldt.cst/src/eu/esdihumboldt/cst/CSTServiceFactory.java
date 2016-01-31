/*
 * Copyright (c) 2016 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.cst;

import eu.esdihumboldt.hale.common.align.transformation.service.TransformationService;
import eu.esdihumboldt.hale.common.core.service.ServiceFactory;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;

/**
 * Service factory for conceptual schema transformer.
 * 
 * @author Simon Templer
 */
public class CSTServiceFactory implements ServiceFactory {

	@SuppressWarnings("unchecked")
	@Override
	public <T> T createService(Class<T> serviceInterface, ServiceProvider serviceLocator) {
		if (TransformationService.class.equals(serviceInterface)) {
			return (T) new ConceptualSchemaTransformer();
		}
		return null;
	}

}
