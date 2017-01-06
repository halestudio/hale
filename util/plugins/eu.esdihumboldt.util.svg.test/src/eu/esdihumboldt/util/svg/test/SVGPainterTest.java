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

import java.awt.Color;
import java.io.IOException;

import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * Test for {@link SVGPainter}.
 * 
 * @author Simon Templer
 */
public class SVGPainterTest extends AbstractSVGPainterTest {

	@SuppressWarnings("javadoc")
	@Test
	public void testDrawPoint() throws IOException {
		Coordinate point = new Coordinate(1, 1);

		PaintSettings settings = new PaintSettings(5, 0, 2, 10, null);
		SVGPainter painter = new SVGPainter(settings);

		painter.setColor(Color.BLUE);
		painter.drawPoint(point);

		saveDrawing(painter);
	}

}
