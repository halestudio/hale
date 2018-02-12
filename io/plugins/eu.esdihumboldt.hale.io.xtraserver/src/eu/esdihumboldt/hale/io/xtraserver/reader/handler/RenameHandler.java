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

import de.interactive_instruments.xtraserver.config.util.api.MappingValue;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.functions.RenameFunction;

/**
 * Transforms a {@link MappingValue} to a {@link RenameFunction}
 * 
 * @author zahnen
 */
class RenameHandler extends AbstractPropertyTransformationHandler {

	RenameHandler(final TransformationContext transformationContext) {
		super(transformationContext);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.xtraserver.reader.handler.AbstractPropertyTransformationHandler#doHandle(de.interactive_instruments.xtraserver.config.util.api.MappingValue)
	 */
	@Override
	public String doHandle(final MappingValue mappingValue) {

		transformationContext.nextPropertyTransformation(mappingValue.getTable(),
				mappingValue.getValue(), mappingValue.getTargetQNameList());

		final ListMultimap<String, ParameterValue> parameters = transformationContext
				.getCurrentPropertyParameters();

		parameters.put("ignoreNamespaces", new ParameterValue("false"));
		parameters.put("structuralRename", new ParameterValue("false"));

		return RenameFunction.ID;
	}

}
