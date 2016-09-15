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

package eu.esdihumboldt.hale.ui.function.extension;

import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactory;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionParameterDefinition;
import eu.esdihumboldt.hale.ui.common.EditorFactory;

/**
 * Factory for parameter editors.
 * 
 * @author Simon Templer
 */
public interface ParameterEditorFactory extends ExtensionObjectFactory<EditorFactory> {

	/**
	 * Get the ID of the associated function.
	 * 
	 * @return the function ID
	 */
	public String getFunctionId();

	/**
	 * Get the name of the associated parameter.
	 * 
	 * @return the name of the parameter the editor is associated with
	 */
	public String getParameterName();

	/**
	 * Get the associated parameter.
	 * 
	 * @return the associated parameter
	 */
	public FunctionParameterDefinition getAssociatedParameter();
}
