/*
 * Copyright (c) 2017 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.common.align.model.annotations.messages

import java.nio.charset.StandardCharsets

import de.fhg.igd.slf4jplus.ALogger
import de.fhg.igd.slf4jplus.ALoggerFactory
import eu.esdihumboldt.hale.common.align.model.Cell
import eu.esdihumboldt.hale.common.core.io.Text
import eu.esdihumboldt.hale.common.core.io.Value
import eu.esdihumboldt.hale.common.core.report.SimpleLog
import groovy.transform.CompileStatic


/**
 * Log wrapper for adding Message annotations to a cell.
 * 
 * @author Simon Templer
 */
@CompileStatic
class CellLog implements SimpleLog {

	private static final ALogger log = ALoggerFactory.getLogger(CellLog)

	private final Cell cell

	private final String fixedCategory

	private final String author

	/**
	 * @param cell
	 * @param fixedCategory
	 */
	CellLog(Cell cell, String fixedCategory = null, String author = null) {
		super()
		this.cell = cell
		this.fixedCategory = fixedCategory
		this.author = author
	}

	private void log(String category, String message, Throwable e) {
		if (fixedCategory) {
			category = fixedCategory
		}

		Message m = (Message) cell.addAnnotation(MessageDescriptor.ID)
		m.setText(message).setCategory(category)

		if (author) {
			m.setAuthor(author)
		}

		if (e != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream()

			try {
				PrintStream ps = new PrintStream(baos, true, StandardCharsets.UTF_8.name())
				e.printStackTrace(ps)
				String trace = new String(baos.toByteArray(), StandardCharsets.UTF_8)
				m.setCustomPayload(Value.of(new Text(trace)))
			} catch (Exception e1) {
				log.error('Error converting stacktrace to String', e1)
			}
		}
	}

	@Override
	void warn(String message, Throwable e) {
		log("warning", message, e)
	}

	@Override
	void error(String message, Throwable e) {
		log("error", message, e)
	}

	@Override
	void info(String message, Throwable e) {
		log("info", message, e)
	}
}
