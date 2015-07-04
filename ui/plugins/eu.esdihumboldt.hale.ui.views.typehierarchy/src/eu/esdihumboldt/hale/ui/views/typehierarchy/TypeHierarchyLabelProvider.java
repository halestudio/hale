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

package eu.esdihumboldt.hale.ui.views.typehierarchy;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionLabelProvider;
import eu.esdihumboldt.hale.ui.views.typehierarchy.TypeHierarchyContentProvider.ParentPath;

/**
 * Label provider for type hierarchies
 * 
 * @author Simon Templer
 */
public class TypeHierarchyLabelProvider extends DefinitionLabelProvider implements IColorProvider {

	private Color mainColor;

	/**
	 * @param associatedViewer the associated viewer (needed for style legend
	 *            support) or <code>null</code>
	 */
	public TypeHierarchyLabelProvider(Viewer associatedViewer) {
		super(associatedViewer);
	}

	/**
	 * @see DefinitionLabelProvider#getText(Object)
	 */
	@Override
	public String getText(Object element) {
		if (element instanceof ParentPath) {
			element = ((ParentPath) element).getHead();
		}

		if (element instanceof TypeDefinition) {
			// return the local name of the type instead of the display name
			// XXX as it may be masked by an XML element name
			return ((TypeDefinition) element).getName().getLocalPart();
		}

		return super.getText(element);
	}

	/**
	 * @see DefinitionLabelProvider#getImage(Object)
	 */
	@Override
	public Image getImage(Object element) {
		if (element instanceof ParentPath) {
			return super.getImage(((ParentPath) element).getHead());
		}
		return super.getImage(element);
	}

	/**
	 * @see IColorProvider#getForeground(Object)
	 */
	@Override
	public Color getForeground(Object element) {
		return null;
	}

	/**
	 * @see IColorProvider#getBackground(Object)
	 */
	@Override
	public Color getBackground(Object element) {
		if (element instanceof ParentPath && ((ParentPath) element).isMainType()) {
			return getMainColor();
		}
		return null;
	}

	/**
	 * @return the color for the main type
	 */
	private Color getMainColor() {
		if (mainColor == null) {
			mainColor = new Color(Display.getDefault(), 220, 220, 255);
		}
		return mainColor;
	}

	/**
	 * @see DefinitionLabelProvider#dispose()
	 */
	@Override
	public void dispose() {
		if (mainColor != null) {
			mainColor.dispose();
		}

		super.dispose();
	}

}
