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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.core.io.supplier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

/**
 * {@link LookupStreamResource} tests.
 * 
 * @author Simon Templer
 */
public class LookupStreamResourceTest {

	private static byte[] data = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };
	private static short previewLimit = 6;

	/**
	 * Tests if the lookup stream retrieved is in fact limited.
	 * 
	 * @throws IOException if reading the stream fails
	 */
	@Test
	public void testLimit() throws IOException {
		LookupStreamResource resource = new LookupStreamResource(new ByteArrayInputStream(data),
				null, previewLimit);

		InputStream in = resource.getLookupSupplier().getInput();
		readLimit(in);
		in.close();
	}

	/**
	 * Tests if the lookup stream retrieved is in fact limited and if the lookup
	 * stream can be read twice with the same result.
	 * 
	 * @throws IOException if reading the stream fails
	 */
	@Test
	public void testLimit2() throws IOException {
		LookupStreamResource resource = new LookupStreamResource(new ByteArrayInputStream(data),
				null, previewLimit);

		LocatableInputSupplier<? extends InputStream> lookup = resource.getLookupSupplier();

		InputStream in = lookup.getInput();
		readLimit(in);
		in.close();

		in = lookup.getInput();
		readLimit(in);
		in.close();
	}

	/**
	 * Read the input stream including the byte after {@link #previewLimit}.
	 * 
	 * @param in the limited lookup input stream
	 * @throws IOException if reading the stream fails
	 */
	private void readLimit(InputStream in) throws IOException {
		for (int i = 0; i <= previewLimit; i++) {
			int b = in.read();
			if (i == previewLimit) {
				assertEquals("Stream not limited", -1, b);
			}
			else {
				assertEquals("Wrong value at stream position" + (i + 1), i + 1, b);
			}
		}
	}

	/**
	 * Test doing lookup and consume in concession.
	 * 
	 * @throws IOException if reading the input stream fails
	 */
	@Test
	public void testLookupAndConsume() throws IOException {
		LookupStreamResource resource = new LookupStreamResource(new ByteArrayInputStream(data),
				null, previewLimit);

		LocatableInputSupplier<? extends InputStream> lookup = resource.getLookupSupplier();

		InputStream in = lookup.getInput();
		readLimit(in);
		in.close();

		LocatableInputSupplier<? extends InputStream> input = resource.getInputSupplier();
		in = input.getInput();
		consume(in);
		in.close();
	}

	/**
	 * Test if mark is correctly not supported by the input streams.
	 * 
	 * @throws IOException if reading the input stream fails
	 */
	@Test
	public void testLookupAndConsumeMark() throws IOException {
		LookupStreamResource resource = new LookupStreamResource(new ByteArrayInputStream(data),
				null, previewLimit);

		LocatableInputSupplier<? extends InputStream> lookup = resource.getLookupSupplier();

		InputStream in = lookup.getInput();
		assertFalse("Mark may not be supported", in.markSupported());
		readLimit(in);
		in.close();

		LocatableInputSupplier<? extends InputStream> input = resource.getInputSupplier();
		assertFalse("Mark may not be supported", in.markSupported());
		in = input.getInput();
		consume(in);
		in.close();
	}

	/**
	 * Completely consume the given input stream.
	 * 
	 * @param in the input stream reading the {@link #data} array
	 * @throws IOException if reading the stream fails
	 */
	private void consume(InputStream in) throws IOException {
		int b;
		int count = 0;
		while ((b = in.read()) != -1) {
			count++;

			assertEquals("Wrong value at stream position" + count, count, b);
		}

		assertEquals("Could not consume whole stream", data.length, count);
	}

	/**
	 * Test doing lookup and consume in concession on the not limited input
	 * supplier.
	 * 
	 * @throws IOException if reading the input stream fails
	 */
	@Test
	public void testInputLookupAndConsume() throws IOException {
		LookupStreamResource resource = new LookupStreamResource(new ByteArrayInputStream(data),
				null, previewLimit);

		LocatableInputSupplier<? extends InputStream> lookup = resource.getLookupSupplier();

		InputStream in = lookup.getInput();
		readLimit(in);
		in.close();

		LocatableInputSupplier<? extends InputStream> input = resource.getInputSupplier();
		in = input.getInput();
		preview(in);
		in.close();

		in = input.getInput();
		preview(in);
		in.close();

		in = input.getInput();
		consume(in);
		in.close();
	}

	/**
	 * Read the input stream up to the preview limit {@link #previewLimit}.
	 * 
	 * @param in the input stream where the first {@link #data} bytes can be
	 *            read from
	 * @throws IOException if reading the stream fails
	 */
	private void preview(InputStream in) throws IOException {
		for (int i = 0; i < previewLimit; i++) {
			int b = in.read();
			assertEquals("Wrong value at stream position" + (i + 1), i + 1, b);
		}
	}

	/**
	 * Test doing lookup and consume in concession on the not limited input
	 * supplier.
	 * 
	 * @throws IOException if reading the input stream fails
	 */
	@Test
	public void testDoubleConsume() throws IOException {
		LookupStreamResource resource = new LookupStreamResource(new ByteArrayInputStream(data),
				null, previewLimit);

		LocatableInputSupplier<? extends InputStream> lookup = resource.getLookupSupplier();

		InputStream in = lookup.getInput();
		readLimit(in);
		in.close();

		LocatableInputSupplier<? extends InputStream> input = resource.getInputSupplier();
		in = input.getInput();
		preview(in);
		in.close();

		in = input.getInput();
		consume(in);
		in.close();

		try {
			in = input.getInput();
			fail("Getting the already consumed input stream should fail.");
		} catch (Exception e) {
			// expected
		}
	}
}
