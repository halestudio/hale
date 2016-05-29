/*
 * Copyright (c) 2013 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.views.resources.internal;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.core.io.IOAction;
import eu.esdihumboldt.hale.common.core.io.project.model.Resource;
import eu.esdihumboldt.hale.ui.io.action.ActionUI;
import eu.esdihumboldt.hale.ui.io.action.ActionUIExtension;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;

/**
 * Resource and action label provider.
 * 
 * @author Simon Templer
 */
public class ResourcesLabelProvider extends StyledCellLabelProvider implements ILabelProvider {

	private final Map<String, Image> actionImages = new HashMap<>();

	private final Image projectImage = ResourcesViewPlugin
			.getImageDescriptor("icons/project_open.gif").createImage();

	@Override
	public void update(ViewerCell cell) {
		Object element = cell.getElement();
		cell.setImage(getImage(element));

		StyledString text = new StyledString(getText(element));

		if (element instanceof Resource) {
			Resource resource = (Resource) element;
			if (resource.getContentType() != null) {
				text.append(" (" + resource.getContentType().getName() + ")",
						StyledString.DECORATIONS_STYLER);
			}
		}

		cell.setText(text.getString());
		cell.setStyleRanges(text.getStyleRanges());

		super.update(cell);
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof IOAction) {
			IOAction action = (IOAction) element;
			Image actionImage = actionImages.get(action.getId());
			if (actionImage == null) {
				ActionUI actionUI = ActionUIExtension.getInstance().findActionUI(action.getId());
				URL iconUrl = actionUI.getIconURL();
				if (iconUrl != null) {
					actionImage = ImageDescriptor.createFromURL(iconUrl).createImage();
					actionImages.put(action.getId(), actionImage);
				}
			}

			if (actionImage != null) {
				return actionImage;
			}

			return PlatformUI.getWorkbench().getSharedImages()
					.getImage(ISharedImages.IMG_OBJ_FOLDER);
		}

		if (element instanceof Resource) {
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
		}

		if (element instanceof ProjectToken) {
			return projectImage;
		}

		return null;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof ProjectToken) {
			ProjectService ps = PlatformUI.getWorkbench().getService(ProjectService.class);
			String name = ps.getProjectInfo().getName();
			if (name == null) {
				return "<Unnamed project>";
			}
			return name;
		}
		if (element instanceof IOAction) {
			IOAction action = (IOAction) element;

			// try names in order of preference

			// resource category name
			if (action.getResourceCategoryName() != null) {
				return action.getResourceCategoryName();
			}

			// action name
			if (action.getName() != null) {
				return action.getName();
			}

			// action ID
			return action.getId();
		}
		if (element instanceof Resource) {
			Resource resource = (Resource) element;
			if (resource.getSource() != null) {
				String location = resource.getSource().toString();
				int index = location.lastIndexOf('/');
				if (index > 0 && index < location.length()) {
					return location.substring(index + 1);
				}
				else {
					return location;
				}
			}
			return resource.getResourceId();
		}
		if (element instanceof IContentType) {
			IContentType ct = (IContentType) element;
			return ct.getName();
		}
		return element.toString();
	}

	@Override
	public void dispose() {
		for (Image image : actionImages.values()) {
			image.dispose();
		}
		actionImages.clear();

		projectImage.dispose();

		super.dispose();
	}
}