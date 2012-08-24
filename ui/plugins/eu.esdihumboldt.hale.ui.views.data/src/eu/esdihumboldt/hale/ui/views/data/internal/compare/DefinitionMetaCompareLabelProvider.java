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

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.instance.extension.metadata.MetadataInfo;
import eu.esdihumboldt.hale.common.instance.extension.metadata.MetadataInfoExtension;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.ui.common.CommonSharedImages;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionLabelProvider;
import eu.esdihumboldt.hale.ui.views.data.internal.Messages;

/**
 * Subclass of basic label provider {@link DefinitionLabelProvider}, 
 * which can handel instance metadatas
 * @author Sebastian Reinhardt
 */
public class DefinitionMetaCompareLabelProvider extends DefinitionLabelProvider {

	private final Map<String, Image> metaimages;
	
	
	/**
	 * Create a label provider that will use short names for 
	 * {@link EntityDefinition}s. 
	 */
	public DefinitionMetaCompareLabelProvider(){
		super();
		metaimages = new HashMap<String, Image>();
	}
	

	/**
	 * Create a label provider for {@link Definition}s and 
	 * {@link EntityDefinition}.
	 * @param longNames if for {@link EntityDefinition}s long names shall
	 *   be used
	 */
	public DefinitionMetaCompareLabelProvider(boolean longNames) {
		super(longNames, false);
		metaimages = new HashMap<String, Image>();
	}
	
	/**
	 * Create a label provider for {@link Definition}s and 
	 * {@link EntityDefinition}.
	 * @param longNames if for {@link EntityDefinition}s long names shall
	 *   be used
	 * @param suppressMandatory if the mandatory overlay for properties shall
	 *   be suppressed (defaults to <code>false</code>)
	 */
	public DefinitionMetaCompareLabelProvider(boolean longNames, boolean suppressMandatory) {
		super(longNames, suppressMandatory);
		metaimages = new HashMap<String, Image>();
	}
	
	/**
	 * @see eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionLabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {
		if(element instanceof Set<?>){
			return Messages.InstanceContentProvider_metadata;
		}
		if(element instanceof String){
			//get the correct label from the extension point
			MetadataInfo meta = MetadataInfoExtension.getInstance().get((String) element);
			if(meta != null){
				return meta.getLabel();			
			}
			else return super.getText(element);
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
		if (element instanceof String){
			MetadataInfo meta = MetadataInfoExtension.getInstance().get((String) element);
			if(meta != null){
				if(metaimages.containsKey(element)){
					return metaimages.get(element);
				}
				else{
					URL icon = meta.getIconURL();
					if(icon != null){
						return ImageDescriptor.createFromURL(icon).createImage();
					}
				}
				
			}				
		}		
		return super.getImage(element);
	}
	
	/**
	 * @see eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionLabelProvider#dispose()
	 */
	@Override
	public void dispose() {
		for(Image img : metaimages.values()){
			img.dispose();
		}
		
		super.dispose();
	}
	
}