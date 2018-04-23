/*
 * Copyright (c) 2017 interactive instruments GmbH
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
 *     interactive instruments GmbH <http://www.interactive-instruments.de>
 */

package eu.esdihumboldt.hale.io.xtraserver.reader.handler;

import de.interactive_instruments.xtraserver.config.api.FeatureTypeMapping;
import de.interactive_instruments.xtraserver.config.api.MappingValue;
import eu.esdihumboldt.hale.common.align.model.MutableCell;

/**
 * Transformation Handler
 * 
 * @param <T> output type to transform to
 * 
 * @author zahnen
 */
@FunctionalInterface
public interface TransformationHandler<T> {

	/**
	 * Transforms a {@link FeatureTypeMapping} or a {@link MappingValue} to an
	 * alignment cell
	 * 
	 * @param typeOrProperty the mapping
	 * @param tableName the primary table name
	 * 
	 * @return alignment cell
	 */
	MutableCell handle(final T typeOrProperty, final String tableName);
}
