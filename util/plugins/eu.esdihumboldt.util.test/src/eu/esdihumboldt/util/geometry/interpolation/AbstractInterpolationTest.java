/*
 * Copyright (c) 2016 wetransform GmbH
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

package eu.esdihumboldt.util.geometry.interpolation;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.io.IOException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;

import eu.esdihumboldt.util.svg.test.AbstractSVGPainterTest;
import eu.esdihumboldt.util.svg.test.PaintSettings;
import eu.esdihumboldt.util.svg.test.SVGTempFilePainter;

/**
 * Abstract interpolation test
 * 
 * @author Arun
 */
public abstract class AbstractInterpolationTest extends AbstractSVGPainterTest {

	private int skipCount = 0;

	/**
	 * validate neighbor coordinates must not same
	 * 
	 * @param geom geometry
	 */
	protected void checkNeighbourCoordinates(Geometry geom) {
		Coordinate[] coordinates = geom.getCoordinates();
		for (int i = 1; i < coordinates.length; i++) {
			assertNotEquals("should not match neighbour coordinates", coordinates[i],
					coordinates[i - 1]);
		}
	}

	/**
	 * print geometry coordinates
	 * 
	 * @param geom geometry
	 */
	protected void printCoordinates(Geometry geom) {
		System.out.println(geom.getCoordinates().length);
		System.out.println("");
		for (Coordinate coordinate : geom.getCoordinates())
			System.out.print("new Coordinate(" + coordinate.x + "," + coordinate.y + "), ");
		System.out.println("");
	}

	/**
	 * validate coordinate should be on grid
	 *
	 * @param generatedGeom geometry
	 * @param rawCoordinatesLength no of raw coordinates
	 * @param maxPosError max positional error
	 * @param keepOriginal keep original
	 *
	 */
	protected void validateCoordinatesOnGrid(Geometry generatedGeom, int rawCoordinatesLength,
			double maxPosError, boolean keepOriginal) {

		Coordinate[] coordinates = generatedGeom.getCoordinates();

		for (Coordinate testCoordinate : coordinates) {
			Coordinate actualGridPoint = Interpolation.pointToGrid(testCoordinate, maxPosError);

			assertTrue(checkOnGrid(testCoordinate, actualGridPoint, rawCoordinatesLength,
					keepOriginal));
		}
	}

	private boolean checkOnGrid(Coordinate expected, Coordinate actual, int noOfSkip,
			boolean keepOriginal) {

		boolean check = actual.equals(expected);
		if (!check) {
			if (keepOriginal)
				skipCount++;
		}
		return (skipCount <= noOfSkip);
	}

	/**
	 * Draw image
	 * 
	 * @param geometry interpolated LineString geometry
	 * @param originalCoordinates original coordinates
	 * @param imageIndex image index
	 * @throws IOException if drawing the image fails
	 */
	protected void drawImage(LineString geometry, Coordinate[] originalCoordinates, int imageIndex)
			throws IOException {

		Envelope envelope = geometry.getEnvelopeInternal();
		for (Coordinate c : originalCoordinates) {
			envelope.expandToInclude(c);
		}
		PaintSettings settings = new PaintSettings(envelope, 1000, 10);
		SVGTempFilePainter svg = new SVGTempFilePainter(settings, "interpolation");

		svg.setColor(Color.DARK_GRAY);
		svg.getGraphics2D().setFont(svg.getGraphics2D().getFont()
				.deriveFont(40.0f)/* .deriveFont(Font.BOLD) */);
		svg.getGraphics2D().drawString(getClass().getSimpleName() + " - " + imageIndex, 30,
				Math.round(envelope.getHeight() * settings.getScaleFactor()) - 30);

		// draw LineString
		svg.setColor(Color.BLACK);
		svg.setStroke(2.0f);
		svg.drawGeometry(geometry);

		// draw arc Coordinates
		for (int i = 0; i < originalCoordinates.length; i++) {
			if (i % 2 == 0) {
				svg.setColor(Color.RED);
			}
			else {
				svg.setColor(Color.BLUE);
			}
			svg.drawPoint(originalCoordinates[i]);
		}

		saveDrawing(svg);
	}

}
