/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.align.model.functions;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.Multimap;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;
import eu.esdihumboldt.hale.common.lookup.LookupService;
import eu.esdihumboldt.hale.common.lookup.LookupTable;
import eu.esdihumboldt.hale.common.lookup.LookupTableInfo;
import eu.esdihumboldt.hale.common.lookup.impl.LookupTableImpl;

/**
 * Common utility methods for dealing with the classification mapping function.
 * 
 * @author Simon Templer
 */
public class ClassificationMappingUtil implements ClassificationMappingFunction {

	private static final ALogger log = ALoggerFactory.getLogger(ClassificationMappingUtil.class);

	/**
	 * Get the classification lookup table from the transformation parameters.
	 * 
	 * @param parameters the transformation parameters
	 * @param serviceProvider service provider in case a lookup table has to be
	 *            retrieved through a service
	 * @return the classification lookup table
	 */
	public static LookupTable getClassificationLookup(Multimap<String, ? extends Value> parameters,
			ServiceProvider serviceProvider) {

		try {
			if (!(parameters.get(PARAMETER_LOOKUPTABLE).isEmpty())) {
				Collection<? extends Value> tmpMap = parameters.get(PARAMETER_LOOKUPTABLE);
				return tmpMap.iterator().next().as(LookupTable.class);
			}
			if (!(parameters.get(PARAMETER_LOOKUPTABLE_ID).isEmpty())) {
				LookupService lookupServiceImpl = serviceProvider.getService(LookupService.class);
				Collection<? extends Value> tmpMap = parameters.get(PARAMETER_LOOKUPTABLE_ID);
				LookupTableInfo lookupTableInfo = lookupServiceImpl.getTable(tmpMap.iterator()
						.next().as(String.class));
				return lookupTableInfo.getTable();
			}
		} catch (NullPointerException e) {
			log.error("Service provider not accessible for retrieving lookup table", e);
		}

		// For reason of compatibility we need the following code
		// lookup table in strangely encoded string parameter
		Collection<? extends Value> mappings = parameters.get(PARAMETER_CLASSIFICATIONS);
		try {
			Map<Value, Value> lookupMap = new HashMap<Value, Value>();
			for (Value mapping : mappings) {
				String[] parts = mapping.as(String.class).split(" ");
				if (parts.length > 0) {
					Value target = Value.of(URLDecoder.decode(parts[0], "UTF-8"));
					for (int i = 1; i < parts.length; i++) {
						lookupMap.put(Value.of(URLDecoder.decode(parts[i], "UTF-8")), target);
					}
				}
			}
			return new LookupTableImpl(lookupMap);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("Failed to decode classification mapping.");
		}
	}
}
