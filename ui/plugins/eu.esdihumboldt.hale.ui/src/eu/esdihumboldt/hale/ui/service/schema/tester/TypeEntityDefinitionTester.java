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

package eu.esdihumboldt.hale.ui.service.schema.tester;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;

/**
 * Tests on {@link TypeEntityDefinition}s based on the {@link AlignmentService}.
 * 
 * @author Kai Schwierczek
 */
public class TypeEntityDefinitionTester extends PropertyTester {

	/**
	 * The property namespace for this tester.
	 */
	public static final String NAMESPACE = "eu.esdihumboldt.hale.ui.service.shema.type";

	/**
	 * The property that specifies if a cell may be removed.
	 */
	public static final String PROPERTY_TYPE_ALLOW_MARK_UNMAPPABLE = "allow_mark_unmappable";

	/**
	 * @see org.eclipse.core.expressions.IPropertyTester#test(java.lang.Object,
	 *      java.lang.String, java.lang.Object[], java.lang.Object)
	 */
	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (receiver == null)
			return false;

		if (property.equals(PROPERTY_TYPE_ALLOW_MARK_UNMAPPABLE)
				&& receiver instanceof TypeEntityDefinition) {
			AlignmentService as = PlatformUI.getWorkbench().getService(AlignmentService.class);
			TypeEntityDefinition entityDef = (TypeEntityDefinition) receiver;
			return as.getAlignment().getCells(entityDef.getType(), entityDef.getSchemaSpace())
					.isEmpty();
		}

		return false;
	}
}
