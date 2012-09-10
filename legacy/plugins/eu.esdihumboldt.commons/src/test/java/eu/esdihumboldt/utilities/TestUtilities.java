package eu.esdihumboldt.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;

public class TestUtilities {
	/**
	 * Logger for this class
	 */
	private static final Logger LOG = Logger.getLogger(TestUtilities.class);

	/**
	 * @param file
	 * @throws FileNotFoundException
	 */
	public static void printFileToConsole(File file)
			throws FileNotFoundException {
		BufferedReader bread = new BufferedReader(new FileReader(file));
		String line = null;
		LOG.trace("File " + file.getName() + " dump: ");
		try {
			while ((line = bread.readLine()) != null) {
				System.out.println(line);
			}
		} catch (IOException e) {
			LOG.error(e);
		}
	}

	public static boolean copyTo(File source, File destination) {
		try {
			FileInputStream fis = new FileInputStream(source);
			FileOutputStream fos = new FileOutputStream(destination);

			byte[] buf = new byte[4096];
			int loaded = 0;
			while ((loaded = fis.read(buf)) > 0) {
				fos.write(buf, 0, loaded);
			}
			fis.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
