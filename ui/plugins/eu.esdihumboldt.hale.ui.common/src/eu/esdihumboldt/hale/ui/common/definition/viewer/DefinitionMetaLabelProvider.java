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

package eu.esdihumboldt.hale.ui.common.definition.viewer;

import org.eclipse.swt.graphics.Image;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.ui.common.CommonSharedImages;


/**
 * Extends the {@link DefinitionLabelProvider} to support an image associated with meta data
 * @author Sebastian Reinhardt
 */
public class DefinitionMetaLabelProvider extends DefinitionLabelProvider {



	/**
	 * Create a label provider for {@link Definition}s and 
	 * {@link EntityDefinition}, which supports Images for Meta Data
	 * @param longNames if for {@link EntityDefinition}s long names shall
	 *   be used
	 * @param suppressMandatory if the mandatory overlay for properties shall
	 *   be suppressed (defaults to <code>false</code>)
	 */
	public DefinitionMetaLabelProvider(boolean longNames, boolean suppressMandatory) {
		super(longNames, suppressMandatory);
	}

	/**
	 * Returns an adjusted image depending on the type of the object passed in.
	 * @return an Image
	 */
	@Override
	public Image getImage(Object element){
		if (element.equals("metadata")){
			return CommonSharedImages.getImageRegistry().get(CommonSharedImages.IMG_META);
		}
		
		return super.getImage(element);
	}
	
}
