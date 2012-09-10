/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
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
