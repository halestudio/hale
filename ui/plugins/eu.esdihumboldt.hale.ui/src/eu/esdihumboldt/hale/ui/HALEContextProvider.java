/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.ui;

import org.eclipse.jface.viewers.ISelectionProvider;

import eu.esdihumboldt.cst.doc.functions.FunctionReferenceConstants;
import eu.esdihumboldt.hale.common.align.extension.function.AbstractFunction;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.CellNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.SourceNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TargetNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTree;
import eu.esdihumboldt.hale.common.instance.model.impl.ONameUtil;
import eu.esdihumboldt.hale.ui.common.help.SelectionContextProvider;

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

		if (object instanceof AbstractFunction<?>) {
			AbstractFunction<?> function = (AbstractFunction<?>) object;
			return FunctionReferenceConstants.PLUGIN_ID + "."
					+ ONameUtil.encodeName(function.getId());
		}

		// TODO for other kinds of selection

		return null;
	}

	private Object extractObject(Object node) {
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
