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

package eu.esdihumboldt.hale.ui.views.typehierarchy;

import org.eclipse.jface.viewers.IColorProvider;
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
