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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.convert;

import java.util.HashMap;
import java.util.Map;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.core.convert.support.GenericConversionService;

/**
 * Conversion service that caches converters retrieved for type descriptors.
 * 
 * @author Simon Templer
 */
public class CachingConversionService extends GenericConversionService {

	/**
	 * Target type mapped to source type mapped to converter
	 */
	private final Map<TypeDescriptor, Map<TypeDescriptor, GenericConverter>> converters = new HashMap<TypeDescriptor, Map<TypeDescriptor, GenericConverter>>();

	@Override
	public void addConverter(GenericConverter converter) {
		super.addConverter(converter);
		reset();
	}

	@Override
	public void addConverter(Converter<?, ?> converter) {
		super.addConverter(converter);
		reset();
	}

	@Override
	public void addConverterFactory(ConverterFactory<?, ?> converterFactory) {
		super.addConverterFactory(converterFactory);
		reset();
	}

	@Override
	public void removeConvertible(Class<?> sourceType, Class<?> targetType) {
		super.removeConvertible(sourceType, targetType);
		reset(); // TODO remove specific converters instead?
	}

	private void reset() {
		synchronized (converters) {
			converters.clear();
		}
	}

	@Override
	protected GenericConverter getConverter(TypeDescriptor sourceType, TypeDescriptor targetType) {
		synchronized (converters) {
			Map<TypeDescriptor, GenericConverter> targetConverters = converters.get(targetType);

			if (targetConverters == null) {
				targetConverters = new HashMap<TypeDescriptor, GenericConverter>();
				converters.put(targetType, targetConverters);

				GenericConverter sourceConverter = super.getConverter(sourceType, targetType);
				targetConverters.put(sourceType, sourceConverter);
				return sourceConverter;
			}

			GenericConverter sourceConverter = targetConverters.get(sourceType);
			if (sourceConverter == null) {
				sourceConverter = super.getConverter(sourceType, targetType);
				targetConverters.put(sourceType, sourceConverter);
			}
			return sourceConverter;
		}
	}

}
