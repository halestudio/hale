/*
 * LICENSE: This program is being made available under the LGPL 3.0 license.
 * For more information on the license, please read the following:
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * 
 * For additional information on the Model behind Mismatches, please refer to
 * the following publication(s):
 * Thorsten Reitz (2010): A Mismatch Description Language for Conceptual Schema 
 * Mapping and Its Cartographic Representation, Geographic Information Science,
 * http://www.springerlink.com/content/um2082120r51232u/
 */
package eu.xsdi.mdlui.views;

import org.eclipse.jface.viewers.LabelProvider;

import eu.xsdi.mdl.model.Reason;
import eu.xsdi.mdl.model.reason.ReasonSet;

/**
 * Provides Text labels for a {@link Reason} object.
 * 
 * @author Thorsten Reitz, thor@xsdi.eu
 * @version $Id$ 
 * @since 0.1.0
 */
public class ReasonLabelProvider extends LabelProvider {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {
		if (element instanceof Reason) {
			return ((Reason)element).getCharacteristic().toString();
		}
		if (element instanceof ReasonSet) {
			return ((ReasonSet)element).getIdentifier() + ":" 
											+ ((ReasonSet)element).getDomain();
		}
		return super.getText(element);
	}
	
}
