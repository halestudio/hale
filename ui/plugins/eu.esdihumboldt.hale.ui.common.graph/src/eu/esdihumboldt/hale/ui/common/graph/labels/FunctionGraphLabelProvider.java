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

package eu.esdihumboldt.hale.ui.common.graph.labels;

import org.eclipse.swt.graphics.Image;
import org.eclipse.zest.core.viewers.EntityConnectionData;

import eu.esdihumboldt.hale.common.align.extension.function.AbstractParameter;
import eu.esdihumboldt.util.Pair;

/**
 * Label provider for graphs based on function(s).
 * @author Patrick Lieb
 */
public class FunctionGraphLabelProvider extends GraphLabelProvider {

	/**
	 * @see eu.esdihumboldt.hale.ui.common.graph.labels.GraphLabelProvider#getImage(java.lang.Object)
	 */
	@Override
	public Image getImage(Object element) {
		if(element instanceof Pair<?,?>){
			return super.getImage(((Pair<?,?>) element).getFirst());
		}
		
		return super.getImage(element);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.graph.labels.GraphLabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {
		if(element instanceof Pair<?,?>){
			element = ((Pair<?,?>) element).getFirst();
		}
		
		if (element instanceof EntityConnectionData) {
			return "";
		}
		
		if(element instanceof AbstractParameter){
			String result = ((AbstractParameter) element).getDisplayName();
			if(!result.equals(""))
				return result;
			// XXX only for developing use
			return "(not set)";
		}
		
		return super.getText(element);
	}

	
}
