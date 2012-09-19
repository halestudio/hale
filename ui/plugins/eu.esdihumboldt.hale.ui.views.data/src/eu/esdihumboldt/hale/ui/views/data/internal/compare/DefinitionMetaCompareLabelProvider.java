/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.views.data.internal.compare;

import java.util.Set;

import org.eclipse.swt.graphics.Image;

import eu.esdihumboldt.hale.common.instance.extension.metadata.MetadataInfo;
import eu.esdihumboldt.hale.common.instance.extension.metadata.MetadataInfoExtension;
import eu.esdihumboldt.hale.ui.common.CommonSharedImages;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionLabelProvider;
import eu.esdihumboldt.hale.ui.views.data.internal.Messages;

/**
 * Subclass of basic label provider {@link DefinitionLabelProvider}, which can
 * handel instance metadatas
 * 
 * @author Sebastian Reinhardt
 */
public class DefinitionMetaCompareLabelProvider extends DefinitionLabelProvider {

	/**
	 * @see eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionLabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {
		if (element instanceof Set<?>) {
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
		else
			return super.getText(element);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionLabelProvider#getImage(java.lang.Object)
	 */
	@Override
	public Image getImage(Object element) {
		if (element instanceof Set<?>) {
			return CommonSharedImages.getImageRegistry().get(CommonSharedImages.IMG_META);
		}

		return super.getImage(element);
	}

}
