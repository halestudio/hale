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

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.instance.extension.metadata.MetadataInfo;
import eu.esdihumboldt.hale.common.instance.extension.metadata.MetadataInfoExtension;
import eu.esdihumboldt.hale.common.schema.model.Definition;
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

	private final Map<String, Image> metaimages;

	/**
	 * Create a label provider that will use short names for
	 * {@link EntityDefinition}s.
	 * 
	 * @param associatedViewer the associated viewer (needed for style legend
	 *            support) or <code>null</code>
	 */
	public DefinitionMetaCompareLabelProvider(@Nullable Viewer associatedViewer) {
		super(associatedViewer);
		metaimages = new HashMap<String, Image>();
	}

	/**
	 * Create a label provider for {@link Definition}s and
	 * {@link EntityDefinition}.
	 * 
	 * @param longNames if for {@link EntityDefinition}s long names shall be
	 *            used
	 * @param associatedViewer the associated viewer (needed for style legend
	 *            support) or <code>null</code>
	 */
	public DefinitionMetaCompareLabelProvider(@Nullable Viewer associatedViewer, boolean longNames) {
		super(associatedViewer, longNames, false);
		metaimages = new HashMap<String, Image>();
	}

	/**
	 * Create a label provider for {@link Definition}s and
	 * {@link EntityDefinition}.
	 * 
	 * @param longNames if for {@link EntityDefinition}s long names shall be
	 *            used
	 * @param suppressMandatory if the mandatory overlay for properties shall be
	 *            suppressed (defaults to <code>false</code>)
	 * @param associatedViewer the associated viewer (needed for style legend
	 *            support) or <code>null</code>
	 */
	public DefinitionMetaCompareLabelProvider(@Nullable Viewer associatedViewer, boolean longNames,
			boolean suppressMandatory) {
		super(associatedViewer, longNames, suppressMandatory);
		metaimages = new HashMap<String, Image>();
	}

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
		if (element instanceof String) {
			MetadataInfo meta = MetadataInfoExtension.getInstance().get((String) element);
			if (meta != null) {
				if (metaimages.containsKey(element)) {
					return metaimages.get(element);
				}
				else {
					URL icon = meta.getIconURL();
					if (icon != null) {
						Image img = ImageDescriptor.createFromURL(icon).createImage();
						metaimages.put((String) element, img);
						return img;
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
		for (Image img : metaimages.values()) {
			img.dispose();
		}
		metaimages.clear();

		super.dispose();
	}

}