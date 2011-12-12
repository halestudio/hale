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

import eu.esdihumboldt.util.Pair;

/**
 * TODO Type description
 * @author Patrick
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
			return super.getText(((Pair<?,?>) element).getFirst());
		}
		if (element instanceof EntityConnectionData) {
			return "";
		}
		return super.getText(element);
	}

	
}
