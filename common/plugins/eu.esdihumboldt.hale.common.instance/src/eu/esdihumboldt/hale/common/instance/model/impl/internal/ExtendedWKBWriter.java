/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.common.instance.model.impl.internal;

import java.io.IOException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.io.ByteOrderValues;
import com.vividsolutions.jts.io.OutStream;
import com.vividsolutions.jts.io.WKBConstants;
import com.vividsolutions.jts.io.WKBWriter;

/**
 * WKB writer that differentiates between {@link LinearRing} and {@link LineString}.
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
	 * @see com.vividsolutions.jts.io.WKBWriter#write(com.vividsolutions.jts.geom.Geometry,
	 *      com.vividsolutions.jts.io.OutStream)
	 */
	@Override
	public void write(Geometry geom, OutStream os) throws IOException {
		if (geom instanceof LinearRing) {
			writeLinearRing((LinearRing) geom, os);
		} else {
			super.write(geom, os);
		}
	}

	private void writeLinearRing(LinearRing ring, OutStream os)
			throws IOException {
		writeByteOrder(os);
		writeGeometryType(wkbLinearRing, ring, os);
		writeCoordinateSequence(ring.getCoordinateSequence(), true, os);
	}

	private void writeGeometryType(int geometryType, Geometry g, OutStream os)
			throws IOException {
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

	private void writeCoordinateSequence(CoordinateSequence seq,
			boolean writeSize, OutStream os) throws IOException {
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
