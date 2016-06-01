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

package eu.esdihumboldt.hale.common.align.model.functions.explanations;

import java.text.MessageFormat;
import java.util.Locale;

import javax.annotation.Nullable;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.impl.AbstractCellExplanation;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Explanation for the inline transformation function.
 * 
 * @author Simon Templer
 */
public class InlineExplanation extends AbstractCellExplanation {

	@Override
	protected String getExplanation(Cell cell, boolean html, Locale locale) {
		Entity source = CellUtil.getFirstEntity(cell.getSource());
		TypeEntityDefinition sourceType = getPropertyType(source);
		Entity target = CellUtil.getFirstEntity(cell.getTarget());
		TypeEntityDefinition targetType = getPropertyType(target);

		if (sourceType != null && targetType != null) {
			String retypeName = (html) ? ("<i>Retype</i>") : ("Retype");
			String text = "To transform the data contained in {0} to {1}, any {4} transformations defined between the corresponding property types are used.\n"
					+ "Thus a {4} type transformation must be defined between the types {2} (source) and {3} (target), which is utilised for this property transformation.";
			return MessageFormat.format(text, formatEntity(source, html, true),
					formatEntity(target, html, true), formatEntity(sourceType, html, true),
					formatEntity(targetType, html, true), retypeName);
		}

		return null;
	}

	@Nullable
	private TypeEntityDefinition getPropertyType(@Nullable Entity entity) {
		if (entity == null) {
			return null;
		}
		Definition<?> def = entity.getDefinition().getDefinition();
		if (def instanceof PropertyDefinition) {
			TypeDefinition propertyType = ((PropertyDefinition) def).getPropertyType();
			return new TypeEntityDefinition(propertyType, entity.getDefinition().getSchemaSpace(),
					null);
		}
		return null;
	}
}
