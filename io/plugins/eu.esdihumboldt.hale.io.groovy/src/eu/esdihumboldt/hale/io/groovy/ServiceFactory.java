/*
 * Copyright (c) 2018 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.io.groovy;

import eu.esdihumboldt.hale.common.core.service.ServiceProvider;
import eu.esdihumboldt.hale.io.groovy.snippets.SnippetService;
import eu.esdihumboldt.hale.io.groovy.snippets.impl.SnippetServiceImpl;
import eu.esdihumboldt.util.groovy.sandbox.GroovyService;

/**
 * Service factory.
 * 
 * @author Simon Templer
 */
public class ServiceFactory implements eu.esdihumboldt.hale.common.core.service.ServiceFactory {

	@SuppressWarnings("unchecked")
	@Override
	public <T> T createService(Class<T> serviceInterface, ServiceProvider serviceLocator) {
		if (SnippetService.class.equals(serviceInterface)) {
			return (T) new SnippetServiceImpl(serviceLocator.getService(GroovyService.class));
		}

		return null;
	}

}
