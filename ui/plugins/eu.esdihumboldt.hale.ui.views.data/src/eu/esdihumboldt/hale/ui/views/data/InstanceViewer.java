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

package eu.esdihumboldt.hale.ui.views.data;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Interface for {@link Instance} viewers based on a {@link Viewer}.
 * 
 * @author Simon Templer
 */
public interface InstanceViewer {

	/**
	 * Create the viewer controls.
	 * 
	 * @param parent the parent composite
	 */
	public void createControls(Composite parent);

	/**
	 * Set the input.
	 * 
	 * @param type the type definition
	 * @param instances the instances to display
	 */
	public abstract void setInput(TypeDefinition type, Iterable<Instance> instances);

	/**
	 * Get the internal tree viewer.
	 * 
	 * @return the tree viewer
	 */
	public abstract Viewer getViewer();

	/**
	 * Get the selection provider providing the instance selection.
	 * 
	 * @return the instance selection provider
	 */
	public ISelectionProvider getInstanceSelectionProvider();

	/**
	 * Get the main control
	 * 
	 * @return the main viewer control
	 */
	public Control getControl();

}