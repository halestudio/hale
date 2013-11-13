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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.bindings.TriggerSequence;
import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.jface.bindings.keys.SWTKeySupport;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.VerifyEvent;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;

/**
 * Adds key triggered operation support to a text viewer.
 * 
 * @author Simon Templer
 */
public class TextViewerOperationSupport {

	private static final ALogger log = ALoggerFactory.getLogger(TextViewerOperationSupport.class);

	/**
	 * Adds support for the given operations to be triggered through a key
	 * sequence to a text viewer.
	 * 
	 * @param viewer the text viewer
	 * @param operations key sequences mapped to operations
	 */
	public static void install(final TextViewer viewer,
			final Map<TriggerSequence, Integer> operations) {
		viewer.appendVerifyKeyListener(new VerifyKeyListener() {

			@Override
			public void verifyKey(VerifyEvent e) {
				int accelerator = SWTKeySupport.convertEventToUnmodifiedAccelerator(e);
				KeySequence sequence = KeySequence.getInstance(SWTKeySupport
						.convertAcceleratorToKeyStroke(accelerator));
				Integer op = operations.get(sequence);
				if (op != null) {
					if (viewer.canDoOperation(op)) {
						e.doit = false;
						viewer.doOperation(op);
					}
				}
			}
		});
	}

	/**
	 * Install the default operations on the given text viewer with default
	 * bindings. Always includes the shift operations.
	 * 
	 * @param viewer the text viewer
	 * @param contentAssist if content assist operations should be added
	 */
	public static void installDefaults(TextViewer viewer, boolean contentAssist) {
		Map<TriggerSequence, Integer> operations = new HashMap<>();

		try {
			operations.put(KeySequence.getInstance("SHIFT+TAB"), ITextOperationTarget.SHIFT_LEFT);
		} catch (ParseException e) {
			log.error("Failed to create key sequence for shift left operation", e);
		}
		try {
			operations.put(KeySequence.getInstance("TAB"), ITextOperationTarget.SHIFT_RIGHT);
		} catch (ParseException e) {
			log.error("Failed to create key sequence for shift right operation", e);
		}

		if (contentAssist) {
			try {
				operations.put(KeySequence.getInstance("M1+SPACE"),
						ISourceViewer.CONTENTASSIST_PROPOSALS);
			} catch (ParseException e) {
				log.error("Failed to create key sequence for content assist proposal operation", e);
			}
		}

		install(viewer, operations);
	}

}
