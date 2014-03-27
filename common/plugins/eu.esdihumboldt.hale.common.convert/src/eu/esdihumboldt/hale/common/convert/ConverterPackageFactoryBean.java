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

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.List;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.core.convert.converter.GenericConverter;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.util.reflection.ReflectionHelper;

/**
 * Factory bean for adding a converter to a converter registry
 * 
 * @author Simon Templer
 */
public class ConverterPackageFactoryBean implements FactoryBean<Object>, BeanClassLoaderAware,
		InitializingBean {

	private static final ALogger log = ALoggerFactory.getLogger(ConverterPackageFactoryBean.class);

	private ConverterRegistry registry;

	private String packageName;

	private ClassLoader classLoader;

	/**
	 * Set the converter registry to add the converter to.
	 * 
	 * @param registry the converter registry
	 */
	public void setRegistry(ConverterRegistry registry) {
		this.registry = registry;
	}

	/**
	 * Set the package name
	 * 
	 * @param packageName the package name
	 */
	public void setPackage(String packageName) {
		this.packageName = packageName;
	}

	/**
	 * @see BeanClassLoaderAware#setBeanClassLoader(ClassLoader)
	 */
	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	/**
	 * @see FactoryBean#getObject()
	 */
	@Override
	public Object getObject() throws Exception {
		return null;
	}

	/**
	 * @see InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		registerConverters(registry, classLoader, packageName);
	}

	/**
	 * Register all converters and converter factories in a given package with
	 * the given registry.
	 * 
	 * @param registry the converter registry
	 * @param classLoader the class loader
	 * @param packageName the package name
	 */
	public static void registerConverters(ConverterRegistry registry, ClassLoader classLoader,
			String packageName) {
		try {
			List<Class<?>> classes = ReflectionHelper.getClassesFromPackage(packageName,
					classLoader);

			for (Class<?> clazz : classes) {
				try {
					if (!Modifier.isAbstract(clazz.getModifiers())) {
						registerConverter(registry, clazz);
					}
				} catch (Exception e) {
					log.error("Error registering converter", e);
				}
			}
		} catch (IOException e) {
			throw new IllegalStateException("Failed to retrieve classes from package", e);
		}
	}

	private static void registerConverter(ConverterRegistry registry, Class<?> clazz)
			throws InstantiationException, IllegalAccessException {
		if (Converter.class.isAssignableFrom(clazz)) {
			Converter<?, ?> converter = (Converter<?, ?>) clazz.newInstance();
			registry.addConverter(converter);
		}
		else if (GenericConverter.class.isAssignableFrom(clazz)) {
			GenericConverter converter = (GenericConverter) clazz.newInstance();
			registry.addConverter(converter);
		}
		else if (ConverterFactory.class.isAssignableFrom(clazz)) {
			ConverterFactory<?, ?> converterFactory = (ConverterFactory<?, ?>) clazz.newInstance();
			registry.addConverterFactory(converterFactory);
		}
	}

	/**
	 * @see FactoryBean#getObjectType()
	 */
	@Override
	public Class<? extends Object> getObjectType() {
		return null;
	}

	/**
	 * @see FactoryBean#isSingleton()
	 */
	@Override
	public boolean isSingleton() {
		return true;
	}

}
