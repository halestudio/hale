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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.scripting;

import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.common.scripting.Script;
import eu.esdihumboldt.hale.ui.common.AttributeEditor;

/**
 * Interface for script input UIs / editors.
 * 
 * @author Kai Schwierczek
 */
public interface ScriptUI {

//	/**
//	 * Returns the script id this UI is for.
//	 *
//	 * @return the script id
//	 */
//	public String getScriptId();

	/**
	 * Sets the script this UI is for. It can than be used for example for the
	 * editor creation.
	 * 
	 * @param script the script
	 */
	public void setScript(Script script);

	/**
	 * Creates an editor to edit a script for the given binding.
	 * 
	 * @param parent the parent composite
	 * @param binding the binding class
	 * @return an editor for the script this UI is for, for the given binding if
	 *         supported
	 */
	public AttributeEditor<String> createEditor(Composite parent, Class<?> binding);

// How to set variables? Add them to editor interface or add them here?
}
