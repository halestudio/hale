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

package eu.esdihumboldt.hale.ui.common.help;

import org.eclipse.help.IContext;
import org.eclipse.jface.viewers.ISelectionProvider;

import eu.esdihumboldt.cst.doc.functions.FunctionReferenceConstants;
import eu.esdihumboldt.hale.common.align.extension.function.AbstractFunction;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.instance.model.impl.ONameUtil;

/**
 * TODO Type description
 * @author Simon Templer
 */
public class HALEContextProvider extends SelectionContextProvider {

	/**
	 * 
	 */
	public HALEContextProvider(ISelectionProvider selectionProvider,
			IContext defaultContext) {
		super(selectionProvider, defaultContext);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see SelectionContextProvider#getContextId(Object)
	 */
	@Override
	protected String getContextId(Object object) {
		if (object instanceof Cell) {
			Cell cell = (Cell) object;
			return FunctionReferenceConstants.PLUGIN_ID + "." + 
					ONameUtil.encodeName(cell.getTransformationIdentifier());
		}
		
		if (object instanceof AbstractFunction<?>) {
			AbstractFunction<?> function = (AbstractFunction<?>) object;
			return FunctionReferenceConstants.PLUGIN_ID + "." + 
					ONameUtil.encodeName(function.getId());
		}
		
		//TODO for other kinds of selection
		
		return null;
	}

}
