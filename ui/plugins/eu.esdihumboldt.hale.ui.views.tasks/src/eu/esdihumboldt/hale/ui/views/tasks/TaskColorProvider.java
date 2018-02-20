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

package eu.esdihumboldt.hale.ui.views.tasks;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import eu.esdihumboldt.hale.ui.util.tree.DefaultTreeNode;
import eu.esdihumboldt.hale.ui.views.tasks.model.ResolvedTask;

/**
 * Task color provider
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class TaskColorProvider implements IColorProvider {
	
	private final Display display;
	
	/**
	 * Constructor
	 * 
	 * @param display the display
	 */
	public TaskColorProvider(Display display) {
		super();
		this.display = display;
	}

	/**
	 * Get the default font
	 * 
	 * @return the default font
	 */
	private Color getDefaultColor() {
		return null; //display.getSystemColor(SWT.COLOR_LIST_FOREGROUND);
	}

	/**
	 * Get the light font
	 * 
	 * @return the light font;
	 */
	private Color getLightColor() {
		return display.getSystemColor(SWT.COLOR_DARK_GRAY);
	}

	@Override
	public Color getBackground(Object element) {
		return null;
	}

	/**
	 * @see IColorProvider#getForeground(Object)
	 */
	@Override
	public Color getForeground(Object element) {
		if (element instanceof DefaultTreeNode) {
			DefaultTreeNode node = (DefaultTreeNode) element;
			Object value = node.getFirstValue();
			if (value instanceof ResolvedTask) {
				ResolvedTask task = (ResolvedTask) value;
				if (task.isOpen()) {
					return getDefaultColor();
				}
				else {
					return getLightColor();
				}
			}
		}
		
		return getDefaultColor();
	}

}
