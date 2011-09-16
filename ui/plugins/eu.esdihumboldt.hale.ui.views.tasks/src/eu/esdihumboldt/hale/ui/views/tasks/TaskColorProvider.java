/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
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
