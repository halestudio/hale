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

package eu.esdihumboldt.hale.ui.function.internal;

import org.eclipse.swt.graphics.Image;

import eu.esdihumboldt.hale.ui.common.function.viewer.FunctionLabelProvider;

/**
 * Label provider for functions that is compatible with
 * {@link FunctionWizardNode}s.
 * 
 * @author Simon Templer
 */
public class FunctionWizardNodeLabelProvider extends FunctionLabelProvider {

	/**
	 * @see FunctionLabelProvider#getText(Object)
	 */
	@Override
	public String getText(Object element) {
		if (element instanceof FunctionWizardNode) {
			element = ((FunctionWizardNode) element).getFunction();
		}

		return super.getText(element);
	}

	/**
	 * @see FunctionLabelProvider#getImage(Object)
	 */
	@Override
	public Image getImage(Object element) {
		if (element instanceof FunctionWizardNode) {
			element = ((FunctionWizardNode) element).getFunction();
		}

		return super.getImage(element);
	}

}
