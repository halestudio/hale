/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.util.source;

import org.eclipse.jface.text.source.SourceViewer;

/**
 * Utility class for source viewer key bindings if used outside an editor.
 * 
 * @author Simon Templer
 */
public abstract class SourceViewerKeyBindings {

	/**
	 * Install the default bindings for undo, redo and shift.
	 * 
	 * @param viewer the source viewer
	 */
	public static void installDefault(SourceViewer viewer) {
		SourceViewerUndoSupport.install(viewer);
		TextViewerOperationSupport.installShift(viewer);
	}

}
