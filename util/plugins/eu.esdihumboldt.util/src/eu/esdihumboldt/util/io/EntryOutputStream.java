package eu.esdihumboldt.util.io;

import java.io.IOException;
import java.util.zip.ZipOutputStream;

/**
 * Output stream for a ZIP entry.
 */
public class EntryOutputStream extends OutputStreamDecorator {

	private final ZipOutputStream zip;

	/**
	 * Create an output stream for a ZIP entry
	 * 
	 * @param zip the ZIP output stream
	 */
	public EntryOutputStream(ZipOutputStream zip) {
		super(zip);

		this.zip = zip;
	}

	/**
	 * @see OutputStreamDecorator#close()
	 */
	@Override
	public void close() throws IOException {
		// instead of closing the stream close the entry
		zip.closeEntry();
	}

}