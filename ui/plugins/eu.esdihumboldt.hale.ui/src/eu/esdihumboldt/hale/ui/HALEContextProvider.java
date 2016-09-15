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

package eu.esdihumboldt.hale.ui;

import org.eclipse.jface.viewers.ISelectionProvider;

import eu.esdihumboldt.cst.doc.functions.FunctionReferenceConstants;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionDefinition;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.CellNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.SourceNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TargetNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTree;
import eu.esdihumboldt.hale.common.instance.orient.ONameUtil;
import eu.esdihumboldt.hale.ui.common.help.SelectionContextProvider;
import eu.esdihumboldt.hale.ui.function.internal.FunctionWizardNode;

/**
 * Selection context provider for selection containing objects from the HALE
 * models, e.g. the schema, instance and alignment models.
 * 
 * @author Simon Templer
 */
public class HALEContextProvider extends SelectionContextProvider {

	/**
	 * @see SelectionContextProvider#SelectionContextProvider(ISelectionProvider,
	 *      String)
	 */
	public HALEContextProvider(ISelectionProvider selectionProvider, String defaultContextId) {
		super(selectionProvider, defaultContextId);
	}

	/**
	 * @see SelectionContextProvider#getContextId(Object)
	 */
	@Override
	protected String getContextId(Object object) {
		object = extractObject(object);

		if (object instanceof Cell) {
			Cell cell = (Cell) object;
			return FunctionReferenceConstants.PLUGIN_ID + "."
					+ ONameUtil.encodeName(cell.getTransformationIdentifier());
		}

		if (object instanceof FunctionDefinition<?>) {
			FunctionDefinition<?> function = (FunctionDefinition<?>) object;
			return FunctionReferenceConstants.PLUGIN_ID + "."
					+ ONameUtil.encodeName(function.getId());
		}

		// TODO for other kinds of selection

		return null;
	}

	private Object extractObject(Object node) {
		if (node instanceof FunctionWizardNode) {
			return ((FunctionWizardNode) node).getFunction();
		}
		if (node instanceof TransformationTree) {
			return ((TransformationTree) node).getType();
		}
		if (node instanceof TargetNode) {
			return ((TargetNode) node).getDefinition();
		}
		if (node instanceof CellNode) {
			return ((CellNode) node).getCell();
		}
		if (node instanceof SourceNode) {
			return ((SourceNode) node).getDefinition();
		}

		return node;
	}

}
