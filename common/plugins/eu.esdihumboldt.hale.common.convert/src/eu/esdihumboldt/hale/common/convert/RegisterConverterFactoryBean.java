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

package eu.esdihumboldt.hale.common.convert;

import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.core.convert.converter.GenericConverter;

/**
 * Factory bean for adding a converter to a converter registry
 * 
 * @author Simon Templer
 */
public class RegisterConverterFactoryBean extends MethodInvokingFactoryBean {

	/**
	 * Set the converter registry to add the converter to.
	 * 
	 * @param registry the converter registry
	 */
	public void setRegistry(ConverterRegistry registry) {
		setTargetObject(registry);
	}

	/**
	 * Add a plain converter to the registry.
	 * 
	 * @param converter the converter
	 */
	public void setConverter(Converter<?, ?> converter) {
		setTargetMethod("addConverter");
		setArguments(new Object[] { converter });
	}

	/**
	 * Add a generic converter to the registry.
	 * 
	 * @param converter the generic converter
	 */
	public void setGenericConverter(GenericConverter converter) {
		setTargetMethod("addConverter");
		setArguments(new Object[] { converter });
	}

	/**
	 * Add a ranged converter factory to the registry.
	 * 
	 * @param converterFactory the converter factory
	 */
	public void setConverterFactory(ConverterFactory<?, ?> converterFactory) {
		setTargetMethod("addConverterFactory");
		setArguments(new Object[] { converterFactory });
	}

}
