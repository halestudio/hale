/*
 * Copyright (c) 2018 wetransform GmbH
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

package eu.esdihumboldt.hale.ui.views.properties.definition.typedefinition.geometry;

import org.eclipse.swt.widgets.Text;

import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTreeUtil;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.GeometryMetadata;
import eu.esdihumboldt.hale.ui.views.properties.AbstractSimpleTextSection;

/**
 * Property type SRS code from {@link GeometryMetadata}.
 * 
 * @author Simon Templer
 */
public class TypeSrsCodeSection extends AbstractSimpleTextSection {

	@Override
	protected void updateControl(Object input, Text control) {
		input = TransformationTreeUtil.extractObject(input);

		if (input instanceof Entity) {
			input = ((Entity) input).getDefinition();
		}

		if (input instanceof EntityDefinition) {
			input = ((EntityDefinition) input).getDefinition();
		}

		TypeDefinition def;
		if (input instanceof PropertyDefinition) {
			def = ((PropertyDefinition) input).getPropertyType();
		}
		else {
			def = (TypeDefinition) input;
		}
		if (def == null) {
			control.setText("");
			return;
		}

		GeometryMetadata gm = def.getConstraint(GeometryMetadata.class);

		StringBuffer srs = new StringBuffer();
		if (gm.getAuthName() != null) {
			srs.append(gm.getAuthName());
			srs.append(":");
		}
		if (gm.getSrs() != null) {
			srs.append(gm.getSrs());
		}
		control.setText(srs.toString());
	}

	@Override
	protected String getPropertyName() {
		return "SRS Code";
	}

}
