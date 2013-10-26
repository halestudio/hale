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

package eu.esdihumboldt.hale.ui.util.groovy;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;

import eu.esdihumboldt.hale.ui.util.groovy.internal.GroovyPartitionScanner;

/**
 * Groovy source viewer utility methods.
 * 
 * @author Simon Templer
 */
public class GroovyViewerUtil {

	/**
	 * Setup a Groovy document.
	 * 
	 * @param doc the document
	 */
	public static void setupDocument(IDocument doc) {
		IPartitionTokenScanner scanner = new GroovyPartitionScanner();
		IDocumentPartitioner partitioner = new FastPartitioner(scanner,
				GroovyPartitionScanner.LEGAL_CONTENT_TYPES);
		doc.setDocumentPartitioner(partitioner);
		partitioner.connect(doc);
	}

}
