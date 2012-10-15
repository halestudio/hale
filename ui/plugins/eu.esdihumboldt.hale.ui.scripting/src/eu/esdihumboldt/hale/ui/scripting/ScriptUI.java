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

import eu.esdihumboldt.hale.ui.common.Editor;

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
	 * @param <T> the binding type
	 * @param parent the parent composite
	 * @param binding the binding class
	 * @return an editor for the script this UI is for, for the given binding if
	 *         supported
	 */
	public <T> Editor<T> createEditor(Composite parent, Class<T> binding);

// How to set variables? Add them to editor interface or add them here?
}
