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

package eu.esdihumboldt.hale.ui.instancevalidation.report;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.eclipse.jface.viewers.ITreePathContentProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.PlatformUI;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import eu.esdihumboldt.hale.common.instancevalidator.report.InstanceValidationMessage;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;

/**
 * Content provider for the instance validation report details page.
 * 
 * @author Kai Schwierczek
 */
public class InstanceValidationReportDetailsContentProvider implements ITreePathContentProvider {

	/**
	 * Maximum number of messages shown for one path/category.
	 */
	public static final int LIMIT = 5;

	private final Map<TreePath, Set<Object>> childCache = new HashMap<TreePath, Set<Object>>();
	private final Multimap<TreePath, InstanceValidationMessage> messages = ArrayListMultimap
			.create();
	private final Set<TreePath> limitedPaths = new HashSet<TreePath>();

	/**
	 * @see ITreePathContentProvider#dispose()
	 */
	@Override
	public void dispose() {
		childCache.clear();
		messages.clear();
		limitedPaths.clear();
	}

	/**
	 * @see ITreePathContentProvider#inputChanged(Viewer, Object, Object)
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		childCache.clear();
		messages.clear();
		limitedPaths.clear();
		if (newInput instanceof Collection<?>) {
			SchemaService ss = PlatformUI.getWorkbench().getService(SchemaService.class);
			TreePath emptyPath = new TreePath(new Object[0]);
			for (Object o : (Collection<?>) newInput) {
				if (o instanceof InstanceValidationMessage) {
					InstanceValidationMessage message = ((InstanceValidationMessage) o);
					Set<Object> baseTypes = childCache.get(emptyPath);
					if (baseTypes == null) {
						baseTypes = new HashSet<Object>();
						childCache.put(emptyPath, baseTypes);
					}
					// XXX maybe expand messages with SSID?
					TypeDefinition typeDef = ss.getSchemas(SchemaSpaceID.TARGET)
							.getType(message.getType());
					// use typeDef if available, QName otherwise
					Object use = typeDef == null ? message.getType() : typeDef;
					if (use == null) {
						// fall-back to generic category
						use = "General";
					}
					baseTypes.add(use);
					messages.put(new TreePath(new Object[] { use }), message);
				}
			}
		}
	}

	/**
	 * @see ITreePathContentProvider#getElements(Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		Set<Object> baseTypes = childCache.get(new TreePath(new Object[0]));
		if (baseTypes == null)
			return new Object[0];
		return baseTypes.toArray();
	}

	/**
	 * @see ITreePathContentProvider#getChildren(TreePath)
	 */
	@Override
	public Object[] getChildren(TreePath parentPath) {
		Set<Object> children = childCache.get(parentPath);
		if (children == null) {
			Collection<InstanceValidationMessage> ivms = messages.get(parentPath);
			if (!ivms.isEmpty()) {
				children = new HashSet<Object>();

				// count of added messages
				int messageCount = 0;

				for (InstanceValidationMessage message : ivms) {
					if (message.getPath().size() > parentPath.getSegmentCount() - 1) {
						// path not done, add next segment
						QName name = message.getPath().get(parentPath.getSegmentCount() - 1);
						Object child = name;
						Object parent = parentPath.getLastSegment();
						if (parent instanceof Definition<?>) {
							ChildDefinition<?> childDef = DefinitionUtil
									.getChild((Definition<?>) parent, name);
							if (childDef != null)
								child = childDef;
						}
						children.add(child);
						messages.put(parentPath.createChildPath(child), message);
					}
					else if (message.getPath().size() == parentPath.getSegmentCount() - 1) {
						// path done, go by category
						String category = message.getCategory();
						children.add(category);
						messages.put(parentPath.createChildPath(category), message);
					}
					else {
						// all done, add as child
						if (messageCount < LIMIT) {
							children.add(message);
							messageCount++;
						}
						else {
							limitedPaths.add(parentPath);
						}
					}
				}
			}
			else
				children = Collections.emptySet();

			childCache.put(parentPath, children);
		}

		return children.toArray();
	}

	/**
	 * Returns the number of messages that are children of the given path.
	 * 
	 * @param path the path
	 * @return the number of messages that are children of the given path
	 */
	public int getMessageCount(TreePath path) {
		return messages.get(path).size();
	}

	/**
	 * Returns whether the given path has more instances available than those
	 * which are shown.
	 * 
	 * @see #LIMIT
	 * @param path the path
	 * @return whether the given path has more instances available than those
	 *         which are shown
	 */
	public boolean isLimited(TreePath path) {
		getChildren(path); // get children so limitedPaths is updated
		return limitedPaths.contains(path);
	}

	/**
	 * Returns all messages that are children of the given path.
	 * 
	 * @param path the path
	 * @return all messages that are children of the given path
	 */
	public Collection<InstanceValidationMessage> getMessages(TreePath path) {
		return messages.get(path);
	}

	/**
	 * @see ITreePathContentProvider#hasChildren(TreePath)
	 */
	@Override
	public boolean hasChildren(TreePath path) {
		return !messages.get(path).isEmpty();
	}

	/**
	 * @see ITreePathContentProvider#getParents(Object)
	 */
	@Override
	public TreePath[] getParents(Object element) {
		return new TreePath[0]; // only possible for messages
	}
}
