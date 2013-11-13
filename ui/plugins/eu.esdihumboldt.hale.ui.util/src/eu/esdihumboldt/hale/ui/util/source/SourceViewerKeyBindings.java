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
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Point;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;

/**
 * Utility class for source viewer key bindings if used outside an editor.
 * 
 * @author Simon Templer
 */
public abstract class SourceViewerKeyBindings {

	private static final ALogger log = ALoggerFactory.getLogger(SourceViewerKeyBindings.class);

	/**
	 * Install the default bindings for undo, redo and shift.
	 * 
	 * @param viewer the source viewer
	 */
	public static void installDefault(SourceViewer viewer) {
		SourceViewerUndoSupport.install(viewer);
		TextViewerOperationSupport.installShift(viewer);
		installDeleteLine(viewer);
	}

	/**
	 * Install the ability to delete a line through <code>CTRL+D</code> on a
	 * text viewer.
	 * 
	 * @param viewer the text viewer
	 */
	public static void installDeleteLine(final TextViewer viewer) {
		try {
			final TriggerSequence trigger = KeySequence.getInstance("CTRL+D");
			viewer.appendVerifyKeyListener(new VerifyKeyListener() {

				@Override
				public void verifyKey(VerifyEvent event) {
					int accelerator = SWTKeySupport.convertEventToUnmodifiedAccelerator(event);
					KeySequence sequence = KeySequence.getInstance(SWTKeySupport
							.convertAcceleratorToKeyStroke(accelerator));
					if (trigger.equals(sequence)) {
						// determine the current selection
						int startOffset;
						int endOffset;
						Point sel = viewer.getTextWidget().getSelectionRange();
						if (sel != null) {
							startOffset = sel.x;
							endOffset = startOffset + sel.y;
						}
						else {
							startOffset = viewer.getTextWidget().getCaretOffset();
							endOffset = startOffset;
						}

						try {
							// determine the involved lines
							IDocument doc = viewer.getDocument();
							int startLine = doc.getLineOfOffset(startOffset);
							int endLine = doc.getLineOfOffset(endOffset);
							// derive start and end offset
							startOffset = doc.getLineOffset(startLine);
							if (startLine != endLine) {
								// delete multiple lines
								endOffset = doc.getLineOffset(endLine) + doc.getLineLength(endLine);
							}
							else {
								// delete one line
								endOffset = startOffset + doc.getLineLength(endLine);
							}

							// delete the line
							doc.replace(startOffset, endOffset - startOffset, "");

							event.doit = false;
						} catch (Exception e) {
							log.warn("Failed to delete line in document", e);
						}
					}
				}
			});
		} catch (ParseException e) {
			log.error("Failed to install delete line listener on source viewer", e);
		}
	}

}
