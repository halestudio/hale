/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.instance.orient.internal;

import java.io.IOException;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.io.ByteOrderValues;
import org.locationtech.jts.io.OutStream;
import org.locationtech.jts.io.WKBConstants;
import org.locationtech.jts.io.WKBWriter;

/**
 * WKB writer that differentiates between {@link LinearRing} and
 * {@link LineString}.
 * 
 * @author Simon Templer
 */
public class ExtendedWKBWriter extends WKBWriter {

	/**
	 * Value for LinearRing geometry type
	 */
	public static final int wkbLinearRing = 8;

	private final int byteOrder;
	private final int outputDimension;
	private final boolean includeSRID = false;
	// holds output data values
	private final byte[] buf = new byte[8];

	/**
	 * @see WKBWriter#WKBWriter(int)
	 */
	public ExtendedWKBWriter(int outputDimension) {
		super(outputDimension);

		this.outputDimension = outputDimension;
		this.byteOrder = ByteOrderValues.BIG_ENDIAN;
	}

	/**
	 * @see org.locationtech.jts.io.WKBWriter#write(org.locationtech.jts.geom.Geometry,
	 *      org.locationtech.jts.io.OutStream)
	 */
	@Override
	public void write(Geometry geom, OutStream os) throws IOException {
		if (geom instanceof LinearRing) {
			writeLinearRing((LinearRing) geom, os);
		}
		else {
			super.write(geom, os);
		}
	}

	private void writeLinearRing(LinearRing ring, OutStream os) throws IOException {
		writeByteOrder(os);
		writeGeometryType(wkbLinearRing, ring, os);
		writeCoordinateSequence(ring.getCoordinateSequence(), true, os);
	}

	private void writeGeometryType(int geometryType, Geometry g, OutStream os) throws IOException {
		int flag3D = (outputDimension == 3) ? 0x80000000 : 0;
		int typeInt = geometryType | flag3D;
		typeInt |= includeSRID ? 0x20000000 : 0;
		writeInt(typeInt, os);
		if (includeSRID) {
			writeInt(g.getSRID(), os);
		}
	}

	private void writeByteOrder(OutStream os) throws IOException {
		if (byteOrder == ByteOrderValues.LITTLE_ENDIAN)
			buf[0] = WKBConstants.wkbNDR;
		else
			buf[0] = WKBConstants.wkbXDR;
		os.write(buf, 1);
	}

	private void writeCoordinateSequence(CoordinateSequence seq, boolean writeSize, OutStream os)
			throws IOException {
		if (writeSize)
			writeInt(seq.size(), os);

		for (int i = 0; i < seq.size(); i++) {
			writeCoordinate(seq, i, os);
		}
	}

	private void writeInt(int intValue, OutStream os) throws IOException {
		ByteOrderValues.putInt(intValue, buf, byteOrder);
		os.write(buf, 4);
	}

	private void writeCoordinate(CoordinateSequence seq, int index, OutStream os)
			throws IOException {
		ByteOrderValues.putDouble(seq.getX(index), buf, byteOrder);
		os.write(buf, 8);
		ByteOrderValues.putDouble(seq.getY(index), buf, byteOrder);
		os.write(buf, 8);

		// only write 3rd dim if caller has requested it for this writer
		if (outputDimension >= 3) {
			// if 3rd dim is requested, only write it if the CoordinateSequence
			// provides it
			double ordVal = Coordinate.NULL_ORDINATE;
			if (seq.getDimension() >= 3)
				ordVal = seq.getOrdinate(index, 2);
			ByteOrderValues.putDouble(ordVal, buf, byteOrder);
			os.write(buf, 8);
		}
	}
}
