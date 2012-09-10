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
import java.io.InputStream;

/**
 * Decorator on an {@link InputStream}
 * 
 * @author Simon Templer
 */
public class InputStreamDecorator extends InputStream {

	private final InputStream in;

	/**
	 * Creates a decorator for the given input stream
	 * 
	 * @param in the input stream to decorate
	 */
	public InputStreamDecorator(InputStream in) {
		super();
		this.in = in;
	}

	/**
	 * @see java.io.InputStream#available()
	 */
	@Override
	public int available() throws IOException {
		return in.available();
	}

	/**
	 * @see java.io.InputStream#close()
	 */
	@Override
	public void close() throws IOException {
		in.close();
	}

	/**
	 * @see java.io.InputStream#mark(int)
	 */
	@Override
	public void mark(int arg0) {
		in.mark(arg0);
	}

	/**
	 * @see java.io.InputStream#markSupported()
	 */
	@Override
	public boolean markSupported() {
		return in.markSupported();
	}

	/**
	 * @see java.io.InputStream#read()
	 */
	@Override
	public int read() throws IOException {
		return in.read();
	}

	/**
	 * @see java.io.InputStream#read(byte[], int, int)
	 */
	@Override
	public int read(byte[] arg0, int arg1, int arg2) throws IOException {
		return in.read(arg0, arg1, arg2);
	}

	/**
	 * @see java.io.InputStream#read(byte[])
	 */
	@Override
	public int read(byte[] arg0) throws IOException {
		return in.read(arg0);
	}

	/**
	 * @see java.io.InputStream#reset()
	 */
	@Override
	public void reset() throws IOException {
		in.reset();
	}

	/**
	 * @see java.io.InputStream#skip(long)
	 */
	@Override
	public long skip(long arg0) throws IOException {
		return in.skip(arg0);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return in.toString();
	}

}
