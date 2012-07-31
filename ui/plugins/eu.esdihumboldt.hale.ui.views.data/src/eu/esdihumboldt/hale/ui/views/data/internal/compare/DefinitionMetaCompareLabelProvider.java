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

package eu.esdihumboldt.hale.ui.views.data.internal.compare;

import java.util.Set;

import org.eclipse.swt.graphics.Image;

import eu.esdihumboldt.hale.ui.common.CommonSharedImages;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionLabelProvider;
import eu.esdihumboldt.hale.ui.views.data.internal.Messages;

/**
 * Subclass of basic label provider {@link DefinitionLabelProvider}, 
 * which can handel instance metadatas
 * @author Sebastian Reinhardt
 */
public class DefinitionMetaCompareLabelProvider extends DefinitionLabelProvider {

	
	
	/**
	 * @see eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionLabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {
		if(element instanceof Set<?>){
			return Messages.InstanceContentProvider_metadata;
		}
		
		else return super.getText(element);
	}


	/**
	 * @see eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionLabelProvider#getImage(java.lang.Object)
	 */
	@Override
	public Image getImage(Object element){
		if (element instanceof Set<?>){
			return CommonSharedImages.getImageRegistry().get(CommonSharedImages.IMG_META);
		}
		
		return super.getImage(element);
	}
	
}