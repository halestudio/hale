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

package eu.esdihumboldt.hale.ui.views.properties;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionLabelProvider;

/**
 * Label provider for use with a property contributor. Supports 
 * {@link IStructuredSelection} as input elements.
 * @author Simon Templer
 */
public class PropertiesLabelProvider extends LabelProvider {
	
	private final DefinitionLabelProvider definitionLabels = new DefinitionLabelProvider(true);

	/**
	 * @see LabelProvider#getImage(Object)
	 */
	@Override
	public Image getImage(Object element) {
		if (element instanceof IStructuredSelection) {
			element = ((IStructuredSelection) element).getFirstElement();
		}
		
		if (element instanceof Entity) {
			element = ((Entity) element).getDefinition();
		}
		
		if (element instanceof EntityDefinition || element instanceof Definition<?>) {
			return definitionLabels.getImage(element);
		}
		
		return super.getImage(element);
	}

	/**
	 * @see LabelProvider#getText(Object)
	 */
	@Override
	public String getText(Object element) {
		if (element instanceof IStructuredSelection) {
			element = ((IStructuredSelection) element).getFirstElement();
		}
		
		if (element instanceof Entity) {
			element = ((Entity) element).getDefinition();
		}
		
		if ((element instanceof EntityDefinition && ((EntityDefinition) element)
				.getDefinition() instanceof TypeDefinition)
				|| element instanceof TypeDefinition) {
			if (element instanceof EntityDefinition) {
				element = ((EntityDefinition) element).getDefinition();
			}
			
			// return the local name of the type instead of the display name
			// XXX as it may be masked by an XML element name
			return ((TypeDefinition) element).getName().getLocalPart();
		}
		
		if (element instanceof EntityDefinition || element instanceof Definition<?>) {
			return definitionLabels.getText(element);
		}
		
		return super.getText(element);
	}

}
