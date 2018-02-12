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

import com.google.common.collect.ListMultimap;

import de.interactive_instruments.xtraserver.config.util.api.FeatureTypeMapping;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.functions.RetypeFunction;

/**
 * Transforms a {@link FeatureTypeMapping} to a {@link RetypeFunction}
 * 
 * @author zahnen
 */
class RetypeHandler extends AbstractTypeTransformationHandler {

	RetypeHandler(final TransformationContext mappingContext) {
		super(mappingContext);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.xtraserver.reader.handler.TypeTransformationHandler#handle(eu.esdihumboldt.hale.common.align.model.Cell,
	 *      String)
	 */
	@Override
	public String doHandle(final FeatureTypeMapping featureTypeMapping,
			final String primaryTableName) {

		final ListMultimap<String, ParameterValue> parameters = transformationContext
				.getCurrentTypeParameters();

		parameters.put("ignoreNamespaces", new ParameterValue("false"));
		parameters.put("structuralRename", new ParameterValue("false"));

		return RetypeFunction.ID;
	}

}
