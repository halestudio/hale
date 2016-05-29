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

import org.eclipse.jface.bindings.TriggerSequence;
import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.jface.bindings.keys.SWTKeySupport;
import org.eclipse.jface.text.IUndoManager;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.keys.IBindingService;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;

/**
 * Adds undo support to source viewers not associated to a view or editor.
 * 
 * @author Simon Templer
 */
public class SourceViewerUndoSupport {

	private static final ALogger log = ALoggerFactory.getLogger(SourceViewerUndoSupport.class);

	/**
	 * Adds undo support to the given source viewer. Should only be called if
	 * for this viewer the undo/redo support is not provided through the
	 * workbench.
	 * 
	 * @param viewer the source viewer
	 */
	public static void install(final SourceViewer viewer) {
		IBindingService bs = PlatformUI.getWorkbench().getService(IBindingService.class);
		TriggerSequence undo = null;
		TriggerSequence redo = null;
		if (bs != null) {
			undo = bs.getBestActiveBindingFor(IWorkbenchCommandConstants.EDIT_UNDO);
			redo = bs.getBestActiveBindingFor(IWorkbenchCommandConstants.EDIT_REDO);
			/*
			 * Note: Curiously this need not be the same as what is displayed in
			 * the main menu. When testing on Linux, CTRL+SHIT+Z was the binding
			 * in the main menu, but here CTRL+Y was returned.
			 */
		}
		try {
			// fall-back bindings
			if (undo == null) {
				undo = KeySequence.getInstance("M1+Z");
			}
			if (redo == null) {
				redo = KeySequence.getInstance("M1+Y");
			}

			final TriggerSequence undoSeq = undo;
			final TriggerSequence redoSeq = redo;
			viewer.getTextWidget().addKeyListener(new KeyAdapter() {

				@Override
				public void keyPressed(KeyEvent e) {
					int accelerator = SWTKeySupport.convertEventToUnmodifiedAccelerator(e);
					KeySequence sequence = KeySequence
							.getInstance(SWTKeySupport.convertAcceleratorToKeyStroke(accelerator));
					if (sequence.equals(undoSeq)) {
						IUndoManager um = viewer.getUndoManager();
						if (um.undoable()) {
							um.undo();
						}
					}
					else if (sequence.equals(redoSeq)) {
						IUndoManager um = viewer.getUndoManager();
						if (um.redoable()) {
							um.redo();
						}
					}
				}
			});
		} catch (ParseException e) {
			log.error("Could not created key sequences for source viewer undo/redo support", e);
		}
	}

}
