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

package eu.esdihumboldt.hale.ui.views.data;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
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
	 * @param schemaSpace the represented schema space
	 */
	public void createControls(Composite parent, SchemaSpaceID schemaSpace);

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
