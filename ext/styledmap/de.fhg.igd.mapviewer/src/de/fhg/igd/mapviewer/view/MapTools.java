/*
 * Copyright (c) 2016 Fraunhofer IGD
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
 *     Fraunhofer IGD <http://www.igd.fraunhofer.de/>
 */
package de.fhg.igd.mapviewer.view;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IRegistryEventListener;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistable;
import org.eclipse.ui.IPersistableEditor;
import org.osgi.framework.Bundle;

import de.fhg.igd.mapviewer.BasicMapKit;
import de.fhg.igd.mapviewer.MapTool;
import de.fhg.igd.mapviewer.tools.AbstractMapTool;
import de.fhg.igd.swingrcp.HTMLToolTipProvider;

/**
 * Map tools contribution item.
 * 
 * @author Simon Templer
 */
public class MapTools extends ContributionItem
		implements IRegistryEventListener, IPersistableEditor {

	private static final Log log = LogFactory.getLog(MapTools.class);

	private final HTMLToolTipProvider tips = new HTMLToolTipProvider();

	private final BasicMapKit mapKit;

	private boolean dirty = true;

	private String defTool;

	private final Map<String, MapTool> tools = new HashMap<String, MapTool>();

	/**
	 * Constructor
	 * 
	 * @param mapKit the map kit
	 */
	public MapTools(BasicMapKit mapKit) {
		this.mapKit = mapKit;

		Platform.getExtensionRegistry().addListener(this, MapTool.class.getName());
	}

	/**
	 * Retrieves a tool from the list of registered tools
	 * 
	 * @param id the tool's ID
	 * @return the tool or null if there is no tool with such an ID
	 */
	public MapTool getTool(String id) {
		return tools.get(id);
	}

	/**
	 * @see ContributionItem#fill(ToolBar, int)
	 */
	@Override
	public void fill(ToolBar parent, int index) {
		// get map tool configurations
		IConfigurationElement[] config = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(MapTool.class.getName());

		SortedSet<AbstractMapTool> sortedTools = new TreeSet<AbstractMapTool>();

		boolean defFound = false;
		for (IConfigurationElement element : config) {
			AbstractMapTool tool = createMapTool(element);
			if (tool != null) {
				sortedTools.add(tool);
				if (tool.getId().equals(defTool)) {
					defFound = true;
				}
				tools.put(tool.getId(), tool);
			}
		}

		boolean first = true;
		for (AbstractMapTool tool : sortedTools) {
			boolean def = (first && !defFound) || (defFound && tool.getId().equals(defTool));

			MapToolAction action = new MapToolAction(tool, mapKit, def);
			tips.createItem(action).fill(parent, index++);

			first = false;
		}

		dirty = false;
	}

	/**
	 * Create a map tool from a configuration element
	 * 
	 * @param element the configuration element
	 * @return the map tool
	 */
	private static AbstractMapTool createMapTool(IConfigurationElement element) {
		if (element.getName().equals("tool")) { //$NON-NLS-1$
			try {
				AbstractMapTool tool = (AbstractMapTool) element.createExecutableExtension("class"); //$NON-NLS-1$

				// id
				tool.setId(element.getAttribute("class")); //$NON-NLS-1$

				// priority
				int priority;
				try {
					priority = Integer.parseInt(element.getAttribute("priority")); //$NON-NLS-1$
				} catch (NumberFormatException e) {
					priority = 0;
				}
				tool.setPriority(priority);

				// configure tool
				tool.setName(element.getAttribute("name")); //$NON-NLS-1$
				tool.setDescription(element.getAttribute("description")); //$NON-NLS-1$

				// set icon URL
				String icon = element.getAttribute("icon"); //$NON-NLS-1$
				if (icon != null && !icon.isEmpty()) {
					String contributor = element.getDeclaringExtension().getContributor().getName();
					Bundle bundle = Platform.getBundle(contributor);

					if (bundle != null) {
						tool.setIconURL(bundle.getResource(icon));
					}
				}

				return tool;
			} catch (Exception e) {
				log.error("Error creating map tool", e); //$NON-NLS-1$
				return null;
			}
		}

		return null;
	}

	/**
	 * @see ContributionItem#isDynamic()
	 */
	@Override
	public boolean isDynamic() {
		return true;
	}

	/**
	 * @see org.eclipse.jface.action.ContributionItem#isDirty()
	 */
	@Override
	public boolean isDirty() {
		return dirty;
	}

	/**
	 * @see IRegistryEventListener#added(org.eclipse.core.runtime.IExtension[])
	 */
	@Override
	public void added(IExtension[] extensions) {
		// XXX how to force update???
		dirty = true;
		getParent().markDirty();
		// getParent().update(true);
	}

	/**
	 * @see IRegistryEventListener#added(org.eclipse.core.runtime.IExtensionPoint[])
	 */
	@Override
	public void added(IExtensionPoint[] extensionPoints) {
		// ignore
	}

	/**
	 * @see IRegistryEventListener#removed(org.eclipse.core.runtime.IExtension[])
	 */
	@Override
	public void removed(IExtension[] extensions) {
		// XXX how to force update?
		dirty = true;
		getParent().markDirty();
		// getParent().update(true);
	}

	/**
	 * @see IRegistryEventListener#removed(org.eclipse.core.runtime.IExtensionPoint[])
	 */
	@Override
	public void removed(IExtensionPoint[] extensionPoints) {
		// ignore
	}

	/**
	 * @see IPersistableEditor#restoreState(IMemento)
	 */
	@Override
	public void restoreState(IMemento memento) {
		if (memento == null)
			return;

		defTool = memento.getTextData();
	}

	/**
	 * @see IPersistable#saveState(IMemento)
	 */
	@Override
	public void saveState(IMemento memento) {
		if (mapKit.getMapTool() != null) {
			memento.putTextData(mapKit.getMapTool().getId());
		}
	}

}
