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

package eu.esdihumboldt.hale.common.core.io.util;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Decorator for an {@link OutputStream}
 * 
 * @author Simon Templer
 */
public class OutputStreamDecorator extends OutputStream {

	private final OutputStream out;

	/**
	 * Creates a decorator for an output stream
	 * 
	 * @param out the output stream
	 */
	public OutputStreamDecorator(OutputStream out) {
		super();
		this.out = out;
	}

	/**
	 * @see java.io.OutputStream#close()
	 */
	@Override
	public void close() throws IOException {
		out.close();
	}

	/**
	 * @see java.io.OutputStream#flush()
	 */
	@Override
	public void flush() throws IOException {
		out.flush();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return out.toString();
	}

	/**
	 * @see java.io.OutputStream#write(byte[], int, int)
	 */
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		out.write(b, off, len);
	}

	/**
	 * @see java.io.OutputStream#write(byte[])
	 */
	@Override
	public void write(byte[] b) throws IOException {
		out.write(b);
	}

	/**
	 * @see java.io.OutputStream#write(int)
	 */
	@Override
	public void write(int b) throws IOException {
		out.write(b);
	}

}
