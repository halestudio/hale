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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.IOAction;
import eu.esdihumboldt.hale.common.core.io.extension.IOActionExtension;
import eu.esdihumboldt.hale.common.core.io.project.model.Resource;

/**
 * Content provider for project resources organized by action.
 * 
 * @author Simon Templer
 */
public class ResourcesContentProvider implements ITreeContentProvider {

	private static final ALogger log = ALoggerFactory.getLogger(ResourcesContentProvider.class);

	private final ListMultimap<IOAction, Resource> resources = ArrayListMultimap.create();

	@Override
	public void dispose() {
		resources.clear();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		resources.clear();
		if (newInput instanceof Iterable<?>) {
			for (Resource resource : (Iterable<Resource>) newInput) {
				String actionId = resource.getActionId();
				IOAction action = IOActionExtension.getInstance().get(actionId);
				if (action != null) {
					resources.put(action, resource);
				}
				else {
					log.warn("Resource is not displayed because action {} was not found", actionId);
				}
			}
		}
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return resources.keySet().toArray();
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IOAction) {
			return resources.get((IOAction) parentElement).toArray();
		}

		if (parentElement instanceof Resource) {
			Resource resource = (Resource) parentElement;
			List<Object> children = new ArrayList<>();

			// location
			if (resource.getSource() != null) {
				children.add(resource.getSource());
			}

			// content type
//			if (resource.getContentType() != null) {
//				children.add(resource.getContentType());
//			}

			return children.toArray();
		}

		return null;
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof Resource) {
			Resource resource = (Resource) element;
			return IOActionExtension.getInstance().get(resource.getActionId());
		}

		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof IOAction) {
			return resources.containsKey(element);
		}
		return element instanceof Resource;
	}

}
