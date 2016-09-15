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

package eu.esdihumboldt.hale.common.convert.extension;

import org.eclipse.core.runtime.IConfigurationElement;
import org.springframework.core.convert.converter.Converter;

import de.fhg.igd.eclipse.util.extension.ExtensionUtil;
import de.fhg.igd.eclipse.util.extension.simple.IdentifiableExtension.Identifiable;

/**
 * Converter information object.
 * 
 * @author Simon Templer
 */
public class ConverterInfo implements Identifiable {

	private final String converterClass;
	private final IConfigurationElement conf;

	/**
	 * Create a new converter information object.
	 * 
	 * @param converterClass the converter class name
	 * @param conf the configuration element
	 */
	public ConverterInfo(String converterClass, IConfigurationElement conf) {
		this.converterClass = converterClass;
		this.conf = conf;
	}

	@Override
	public String getId() {
		return converterClass;
	}

	/**
	 * Create a converter instance.
	 * 
	 * @return the converter instance
	 * @throws Exception if instantiating the converter fails
	 */
	@SuppressWarnings("unchecked")
	public <F, T> Converter<F, T> createConverter() throws Exception {
		Class<?> converterClass = ExtensionUtil.loadClass(conf, "class");
		if (converterClass != null) {
			return (Converter<F, T>) converterClass.newInstance();
		}
		else {
			throw new IllegalStateException("Could not load converter class " + converterClass);
		}
	}
}
