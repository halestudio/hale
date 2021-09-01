/*
 * Copyright (c) 2021 wetransform GmbH
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

package eu.esdihumboldt.hale.ui.io.schema;

import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.Schema;

/**
 * Target Schema action UI advisor.
 * 
 * @author Kapil Agnihotri.
 */
public class TargetSchemaActionUIAdvisor extends AbstractSchemaActionUIAdvisor<Schema> {

	/**
	 * @see eu.esdihumboldt.hale.ui.io.action.ActionUIAdvisor#supportsRemoval(java.lang.String)
	 */
	@Override
	public boolean supportsRemoval(String resourceId) {
		boolean supportRemoval = true;
		Schema schema = schemaService.getSchema(resourceId, SchemaSpaceID.TARGET);

		// check if the target schema is mapped to any alignment, then it cannot
		// be removed.
		// ! is applied because an empty stream will return false and when an
		// alignment is present, the condition will return true. However, when
		// the alignment is mapped then the function should return false.
		supportRemoval = !alignmentService.getAlignment().getCells().stream()
				.anyMatch(alignmentCells -> alignmentCells.getTarget().entries().stream()
						.anyMatch(k -> schema.getType(((Type) k.getValue()).getDefinition()
								.getDefinition().getName()) != null));
		return supportRemoval;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.action.ActionUIAdvisor#removeResource(java.lang.String)
	 */
	@Override
	public boolean removeResource(String resourceId) {
		return removeResource(resourceId, SchemaSpaceID.TARGET);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.action.ActionUIAdvisor#retrieveResource(java.lang.String)
	 */
	@Override
	public Schema retrieveResource(String resourceId) {
		return retrieveResource(resourceId, SchemaSpaceID.TARGET);
	}

}
