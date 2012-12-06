/*
 * Copyright (c) 2012 Fraunhofer IGD
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
 *     Fraunhofer IGD
 */

package eu.esdihumboldt.hale.io.xslt.transformations;

import org.apache.velocity.VelocityContext;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.io.xslt.XslTransformationUtil;
import eu.esdihumboldt.hale.io.xslt.XslTypeTransformation;
import eu.esdihumboldt.hale.io.xslt.transformations.base.AbstractVelocityXslTypeTransformation;

/**
 * XSLT representation of the Retype function.
 * 
 * @author Simon Templer
 */
public class XslRetype extends AbstractVelocityXslTypeTransformation implements
		XslTypeTransformation {

	private static final String CONTEXT_PARAM_SELECT_INSTANCES = "select_instances";

	@Override
	protected void configureTemplate(VelocityContext context, Cell typeCell) {
		Type source = (Type) CellUtil.getFirstEntity(typeCell.getSource());

		TypeEntityDefinition ted = source.getDefinition();
		context.put(CONTEXT_PARAM_SELECT_INSTANCES,
				XslTransformationUtil.selectInstances(ted, "/", context().getNamespaceContext()));
	}

}
