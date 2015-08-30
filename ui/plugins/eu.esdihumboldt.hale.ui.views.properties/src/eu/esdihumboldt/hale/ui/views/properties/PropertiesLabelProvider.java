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

package eu.esdihumboldt.hale.ui.views.properties;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import eu.esdihumboldt.hale.common.align.extension.function.FunctionDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTreeUtil;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionLabelProvider;
import eu.esdihumboldt.hale.ui.common.function.viewer.FunctionLabelProvider;

/**
 * Label provider for use with a property contributor. Supports
 * {@link IStructuredSelection} as input elements.
 * 
 * @author Simon Templer
 */
public class PropertiesLabelProvider extends LabelProvider {

	private final DefinitionLabelProvider definitionLabels = new DefinitionLabelProvider(null, true);

	private final FunctionLabelProvider functionLabels = new FunctionLabelProvider();

	/**
	 * @see LabelProvider#getImage(Object)
	 */
	@Override
	public Image getImage(Object element) {
		if (element instanceof IStructuredSelection) {
			element = ((IStructuredSelection) element).getFirstElement();
		}

		element = TransformationTreeUtil.extractObject(element);

		if (element instanceof Entity) {
			element = ((Entity) element).getDefinition();
		}

		if (element instanceof EntityDefinition || element instanceof Definition<?>) {
			return definitionLabels.getImage(element);
		}

		if (element instanceof Cell) {
			Cell cell = (Cell) element;
			FunctionDefinition<?> function = FunctionUtil.getFunction(
					cell.getTransformationIdentifier(), HaleUI.getServiceProvider());
			if (function != null) {
				element = function;
			}
		}

		if (element instanceof FunctionDefinition) {
			return functionLabels.getImage(element);
		}

		return super.getImage(element);
	}

	/**
	 * @see LabelProvider#getText(Object)
	 */
	@Override
	public String getText(Object element) {
		if (element instanceof IStructuredSelection) {
			element = ((IStructuredSelection) element).getFirstElement();
		}

		element = TransformationTreeUtil.extractObject(element);

		if (element instanceof Entity) {
			element = ((Entity) element).getDefinition();
		}

		if ((element instanceof EntityDefinition && ((EntityDefinition) element).getDefinition() instanceof TypeDefinition)
				|| element instanceof TypeDefinition) {
			if (element instanceof EntityDefinition) {
				element = ((EntityDefinition) element).getDefinition();
			}

			// return the local name of the type instead of the display name
			// XXX as it may be masked by an XML element name
			return ((TypeDefinition) element).getName().getLocalPart();
		}

		if (element instanceof EntityDefinition || element instanceof Definition<?>) {
			return definitionLabels.getText(element);
		}

		if (element instanceof FunctionDefinition) {
			return functionLabels.getText(element);
		}

		if (element instanceof Cell) {
			return CellUtil.getCellDescription((Cell) element, HaleUI.getServiceProvider());
		}

		return super.getText(element);
	}

}
