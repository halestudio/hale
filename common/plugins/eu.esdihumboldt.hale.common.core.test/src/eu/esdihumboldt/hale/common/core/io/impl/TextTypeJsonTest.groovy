/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.core.io.impl

import eu.esdihumboldt.hale.common.core.io.Text


/**
 * Tests {@link Text} / JSON conversion.
 * 
 * @author Simon Templer
 */
class TextTypeJsonTest extends GroovyTestCase {

	void testToFrom() {
		Text text = new Text('My \"awesome\" text')
		TextTypeJson conv = new TextTypeJson()

		// convert to json
		StringWriter writer = new StringWriter()
		writer.withWriter {
			conv.toJson(text, it)
		}
		String json = writer.toString()

		assertEquals('\"My \\\"awesome\\\" text\"', json)

		// convert back
		StringReader reader = new StringReader(json)
		Text newText
		reader.withReader {
			newText = conv.fromJson(reader, null)
		}

		assertEquals(text, newText)
	}
}
