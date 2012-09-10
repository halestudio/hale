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

package eu.esdihumboldt.hale.ui.views.data.internal.explore;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.instance.extension.metadata.MetadataInfo;
import eu.esdihumboldt.hale.common.instance.extension.metadata.MetadataInfoExtension;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.ui.common.CommonSharedImages;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionLabelProvider;
import eu.esdihumboldt.hale.ui.views.data.internal.Messages;
import eu.esdihumboldt.hale.ui.views.data.internal.Metadata;

/**
 * Extends the {@link DefinitionLabelProvider} to support an image associated
 * with meta data
 * 
 * @author Sebastian Reinhardt
 */
public class DefinitionMetaLabelProvider extends DefinitionLabelProvider {

	/**
	 * Create a label provider for {@link Definition}s and
	 * {@link EntityDefinition}, which supports Images for Meta Data
	 * 
	 * @param longNames if for {@link EntityDefinition}s long names shall be
	 *            used
	 * @param suppressMandatory if the mandatory overlay for properties shall be
	 *            suppressed (defaults to <code>false</code>)
	 */
	public DefinitionMetaLabelProvider(boolean longNames, boolean suppressMandatory) {
		super(longNames, suppressMandatory);
	}

	/**
	 * @see LabelProvider#getText(Object)
	 */
	@Override
	public String getText(Object element) {

		if (element == Metadata.METADATA) {
			return Messages.InstanceContentProvider_metadata;
		}
		if (element instanceof String) {
			// get the correct label from the extension point
			MetadataInfo meta = MetadataInfoExtension.getInstance().get((String) element);
			if (meta != null) {
				return meta.getLabel();
			}
			else
				return super.getText(element);
		}

		else {
			return super.getText(element);
		}

	}

	/**
	 * Returns an adjusted image depending on the type of the object passed in.
	 * 
	 * @return an Image
	 */
	@Override
	public Image getImage(Object element) {
		if (element == Metadata.METADATA) {
			return CommonSharedImages.getImageRegistry().get(CommonSharedImages.IMG_META);
		}

		return super.getImage(element);
	}

}
