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

package eu.esdihumboldt.util.svg.test;

import java.io.IOException;

import ru.yandex.qatools.allure.annotations.Attachment;

/**
 * Base class for tests with attachments based on {@link SVGPainter}.
 * 
 * @author Simon Templer
 */
public abstract class AbstractSVGPainterTest {

	/**
	 * Call to save the drawing as an attachment.
	 * 
	 * @param painter the SVG painter
	 * @return the SVG string, safely ignored
	 * @throws IOException if encoding the SVG fails
	 */
	@Attachment("Test drawing")
	public String saveDrawing(SVGPainter painter) throws IOException {
		return painter.writeToString();
	}

	/**
	 * Call to save the drawing as an attachment.
	 * 
	 * @param name the drawing name
	 * @param painter the SVG painter
	 * @return the SVG string, safely ignored
	 * @throws IOException if encoding the SVG fails
	 */
	@Attachment("{0}")
	public String saveDrawing(String name, SVGPainter painter) throws IOException {
		return painter.writeToString();
	}

}
