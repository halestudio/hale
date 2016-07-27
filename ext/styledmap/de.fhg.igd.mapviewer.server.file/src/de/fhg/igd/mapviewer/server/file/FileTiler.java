/*
 * Copyright (c) 2016 Fraunhofer IGD
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
 *     Fraunhofer IGD <http://www.igd.fraunhofer.de/>
 */
package de.fhg.igd.mapviewer.server.file;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * FileTiler - creates map files
 *
 * @author <a href="mailto:simon.templer@igd.fhg.de">Simon Templer</a>
 *
 * @version $Id$
 */
public class FileTiler {

	/**
	 * A file filter that accepts file names that contain a certain string
	 */
	public static class ContainsFileFilter extends FileFilter {

		private final String contains;

		/**
		 * Creates a file filter that accepts file names that contain the given
		 * string
		 * 
		 * @param contains the string that must be contained in accepted file
		 *            names
		 */
		public ContainsFileFilter(String contains) {
			this.contains = contains;
		}

		/**
		 * @see FileFilter#accept(File)
		 */
		@Override
		public boolean accept(File f) {
			if (f.isDirectory())
				return true;
			else
				return f.getName().indexOf(contains) >= 0;
		}

		/**
		 * @see FileFilter#getDescription()
		 */
		@Override
		public String getDescription() {
			return contains + " file";
		}

	}

	/**
	 * A file filter that accepts a certain file name
	 */
	public static class ExactFileFilter extends FileFilter {

		private final String fileName;

		/**
		 * Creates a file filter that accepts the given file name
		 * 
		 * @param fileName the file name
		 */
		public ExactFileFilter(String fileName) {
			this.fileName = fileName;
		}

		/**
		 * @see FileFilter#accept(java.io.File)
		 */
		@Override
		public boolean accept(File f) {
			if (f.isDirectory())
				return true;
			else
				return f.getName().equals(fileName);
		}

		/**
		 * @see FileFilter#getDescription()
		 */
		@Override
		public String getDescription() {
			return fileName;
		}

	}

	private static final Log log = LogFactory.getLog(FileTiler.class);

	// preferences keys
	private static final String PREF_DIR = "dir";
	private static final String PREF_CONVERT = "convert";
	private static final String PREF_IDENTIFY = "identify";

	// default values
	private static final int DEF_MIN_TILE_SIZE = 200;
	private static final int DEF_MIN_MAP_SIZE = 600;

	// map properties keys
	/** tile width (pixel) property name */
	public static final String PROP_TILE_WIDTH = "tileWidth";
	/** tile height (pixel) property name */
	public static final String PROP_TILE_HEIGHT = "tileHeight";
	/** number of zoom levels property name */
	public static final String PROP_ZOOM_LEVELS = "zoomLevels";
	/** map width (tiles) property name */
	public static final String PROP_MAP_WIDTH = "mapWidthAtZoom";
	/** map height (tiles) property name */
	public static final String PROP_MAP_HEIGHT = "mapHeightAtZoom";

	/** map properties file name */
	public static final String MAP_PROPERTIES_FILE = "map.properties";
	/** map file file-extension */
	public static final String MAP_ARCHIVE_EXTENSION = ".map";
	/** converter properties file name */
	public static final String CONVERTER_PROPERTIES_FILE = "converter.properties";

	// tile file
	/** tile file name prefix */
	public static final String TILE_FILE_PREFIX = "tile_z";
	/** tile file name separator */
	public static final String TILE_FILE_SEPARATOR = "_n";
	/** tile file file-extension */
	public static final String TILE_FILE_EXTENSION = ".jpg";

	/**
	 * Buffer size for writing files into jar archive
	 */
	public static int BUFFER_SIZE = 10240;

	/**
	 * FileTiler preferences node
	 */
	private final Preferences pref = Preferences.userNodeForPackage(FileTiler.class)
			.node(FileTiler.class.getSimpleName());

	/**
	 * Path to convert executable
	 */
	private String convertPath;

	/**
	 * Path to identify executable
	 */
	private String identifyPath;

	/**
	 * Get the path to the convert executable
	 * 
	 * @return the path to the convert executable
	 */
	private String getConvertPath() {
		if (convertPath == null)
			loadCommandPaths();

		return convertPath;
	}

	/**
	 * Get the path to the identify executable
	 * 
	 * @return the path to the convert executable
	 */
	private String getIdentifyPath() {
		if (identifyPath == null)
			loadCommandPaths();

		return identifyPath;
	}

	/**
	 * Load the command paths from the preferences or ask the user for them
	 */
	private void loadCommandPaths() {
		String convert = pref.get(PREF_CONVERT, null);
		String identify = pref.get(PREF_IDENTIFY, null);

		JFileChooser commandChooser = new JFileChooser();

		if (convert != null && identify != null) {
			if (JOptionPane.showConfirmDialog(null,
					"<html>Found paths to executables:<br/><b>" + convert + "<br/>" + identify
							+ "</b><br/>Do you want to use this settings?</html>",
					"Paths to executables", JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION) {
				convert = null;
				identify = null;
			}
		}

		if (convert == null) {
			// ask for convert path
			convert = askForPath(commandChooser, new ContainsFileFilter("convert"),
					"Please select your convert executable");
		}

		if (convert != null && identify == null) {
			// ask for identify path
			identify = askForPath(commandChooser, new ContainsFileFilter("identify"),
					"Please select your identify executable");
		}

		if (convert == null)
			pref.remove(PREF_CONVERT);
		else
			pref.put(PREF_CONVERT, convert);

		if (identify == null)
			pref.remove(PREF_IDENTIFY);
		else
			pref.put(PREF_IDENTIFY, identify);

		convertPath = convert;
		identifyPath = identify;
	}

	/**
	 * Ask the user for a certain file path
	 * 
	 * @param chooser the {@link JFileChooser} to use
	 * @param filter the file filter
	 * @param title the title of the dialog
	 * 
	 * @return the selected file or null
	 */
	private String askForPath(JFileChooser chooser, FileFilter filter, String title) {
		chooser.setDialogTitle(title);
		chooser.setFileFilter(filter);
		int returnVal = chooser.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile().getAbsolutePath();
		}

		return null;
	}

	/**
	 * Uses a Runtime.exec() to use imagemagick to perform the given conversion
	 * operation. Returns true on success, false on failure. Does not check if
	 * either file exists.
	 *
	 * @param in Description of the Parameter
	 * @param out Description of the Parameter
	 * @param width the new width
	 * @param height the new height
	 * @param quality Description of the Parameter
	 * @return Description of the Return Value
	 */
	public boolean convert(File in, File out, int width, int height, int quality) {
		if (quality < 0 || quality > 100) {
			quality = 75;
		}

		// note: CONVERT_PROG is a class variable that stores the location of
		// ImageMagick's convert command
		// it might be something like "/usr/local/magick/bin/convert" or
		// something else, depending on where you installed it.
		String[] command = { getConvertPath(), "-geometry", width + "x" + height, "-quality",
				String.valueOf(quality), in.getAbsolutePath(), out.getAbsolutePath() };

		return exec(command, null);
	}

	/**
	 * Split an image file into tiles
	 * 
	 * @param in the image file
	 * @param outPattern the name pattern for the tiles (e.g. tiles_%d)
	 * @param extension the file extension for the tile image files
	 * @param tileWidth the desired tile width
	 * @param tileHeight the desired tile height
	 * 
	 * @return if the operation succeded
	 */
	public boolean tile(File in, String outPattern, String extension, int tileWidth,
			int tileHeight) {
		File dir = in.getParentFile();

		String[] command = { getConvertPath(), in.getAbsolutePath(), "-crop",
				tileWidth + "x" + tileHeight, "+repage",
				dir.getAbsolutePath() + File.separator + outPattern + extension };

		return exec(command, null);
	}

	/**
	 * Get the size of an image using the identify command
	 * 
	 * @param imageFile the image file
	 * 
	 * @return the dimension stating the size of the image or null
	 */
	public Dimension getSize(File imageFile) {
		String[] command = { getIdentifyPath(), imageFile.getAbsolutePath() };

		List<String> result = new ArrayList<String>();
		boolean success = exec(command, result);

		if (success && !result.isEmpty()) {
			try {
				String[] split = result.get(0).split(" ");
				String name = imageFile.getName();
				log.info("Filename: " + name);
				boolean found = false;
				int fileIndex;
				for (fileIndex = 0; !found && fileIndex < split.length; fileIndex++) {
					if (split[fileIndex].endsWith(name))
						found = true;
				}

				if (found) {
					String geometry = split[fileIndex + 1]; // get geometry part
					String[] geosplit = geometry.split("x");
					return new Dimension(Integer.parseInt(geosplit[0]),
							Integer.parseInt(geosplit[1]));
				}
				else
					throw new IllegalArgumentException();
			} catch (Exception e) {
				log.error("Error getting size info for file " + imageFile.getAbsolutePath()
						+ ", output was: " + result);
			}
		}

		return null;
	}

	/**
	 * Tries to exec the command, waits for it to finsih, logs errors if exit
	 * status is nonzero, and returns true if exit status is 0 (success).
	 *
	 * @param command Description of the Parameter
	 * @param output a list that will be cleared and the output lines added (if
	 *            the list is not null)
	 * @return Description of the Return Value
	 */
	public static boolean exec(String[] command, List<String> output) {
		Process proc;

		try {
			// System.out.println("Trying to execute command " +
			// Arrays.asList(command));
			proc = Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			log.error("IOException while trying to execute " + Arrays.toString(command), e);
			return false;
		}

		if (output == null)
			output = new ArrayList<String>();

		output.clear();
		BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));

		String currentLine;
		try {
			while ((currentLine = reader.readLine()) != null) {
				output.add(currentLine);
			}
		} catch (IOException e) {
			log.error("Error reading process output", e);
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				log.error("Error closing input stream", e);
			}
		}

		int exitStatus;

		while (true) {
			try {
				exitStatus = proc.waitFor();
				break;
			} catch (java.lang.InterruptedException e) {
				log.warn("Interrupted: Ignoring and waiting");
			}
		}

		if (exitStatus != 0) {
			/*
			 * StringBuilder cmdString = new StringBuilder(); for (int i = 0; i
			 * < command.length; i++) cmdString.append(command[i]);
			 */
			log.warn("Error executing command: " + exitStatus + " (" + output + ")");
		}

		return (exitStatus == 0);
	}

	/**
	 * Ask the user for an image file for that a tiled map shall be created
	 */
	public void run() {
		JFileChooser fileChooser = new JFileChooser();

		// load current dir
		fileChooser.setCurrentDirectory(
				new File(pref.get(PREF_DIR, fileChooser.getCurrentDirectory().getAbsolutePath())));

		// open
		int returnVal = fileChooser.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			// save current dir
			pref.put(PREF_DIR, fileChooser.getCurrentDirectory().getAbsolutePath());

			// get file
			File imageFile = fileChooser.getSelectedFile();

			// get image dimension
			Dimension size = getSize(imageFile);
			log.info("Image size: " + size);

			// ask for min tile size
			int minTileSize = 0;
			while (minTileSize <= 0) {
				try {
					minTileSize = Integer.parseInt(JOptionPane.showInputDialog("Minimal tile size",
							String.valueOf(DEF_MIN_TILE_SIZE)));
				} catch (Exception e) {
					minTileSize = 0;
				}
			}

			// determine min map width
			int width = size.width;

			while (width / 2 > minTileSize && width % 2 == 0) {
				width = width / 2;
			}
			int minMapWidth = width; // min map width

			log.info("Minimal map width: " + minMapWidth);

			// determine min map height
			int height = size.height;

			while (height / 2 > minTileSize && height % 2 == 0) {
				height = height / 2; // min map height
			}
			int minMapHeight = height;

			log.info("Minimal map height: " + minMapHeight);

			// ask for min map size
			int minMapSize = 0;
			while (minMapSize <= 0) {
				try {
					minMapSize = Integer.parseInt(JOptionPane.showInputDialog("Minimal map size",
							String.valueOf(DEF_MIN_MAP_SIZE)));
				} catch (Exception e) {
					minMapSize = 0;
				}
			}

			// determine zoom levels
			int zoomLevels = 1;

			width = size.width;
			height = size.height;

			while (width % 2 == 0 && height % 2 == 0
					&& width / 2 >= Math.max(minMapWidth, minMapSize)
					&& height / 2 >= Math.max(minMapHeight, minMapSize)) {
				zoomLevels++;
				width = width / 2;
				height = height / 2;
			}

			log.info("Number of zoom levels: " + zoomLevels);

			// determine tile width
			width = minMapWidth;
			int tileWidth = minMapWidth;
			for (int i = 3; i < Math.sqrt(minMapWidth) && width > minTileSize;) {
				tileWidth = width;
				if (width % i == 0) {
					width = width / i;
				}
				else
					i++;
			}

			// determine tile height
			height = minMapHeight;
			int tileHeight = minMapHeight;
			for (int i = 3; i < Math.sqrt(minMapHeight) && height > minTileSize;) {
				tileHeight = height;
				if (height % i == 0) {
					height = height / i;
				}
				else
					i++;
			}

			// create tiles for each zoom level
			if (JOptionPane.showConfirmDialog(null,
					"Create tiles (" + tileWidth + "x" + tileHeight + ") for " + zoomLevels
							+ " zoom levels?",
					"Create tiles", JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
				int currentWidth = size.width;
				int currentHeight = size.height;
				File currentImage = imageFile;

				Properties properties = new Properties();
				properties.setProperty(PROP_TILE_WIDTH, String.valueOf(tileWidth));
				properties.setProperty(PROP_TILE_HEIGHT, String.valueOf(tileHeight));
				properties.setProperty(PROP_ZOOM_LEVELS, String.valueOf(zoomLevels));

				List<File> files = new ArrayList<File>();

				for (int i = 0; i < zoomLevels; i++) {
					int mapWidth = currentWidth / tileWidth;
					int mapHeight = currentHeight / tileHeight;

					log.info("Creating tiles for zoom level " + i);
					log.info("Map width: " + currentWidth + " pixels, " + mapWidth + " tiles");
					log.info("Map height: " + currentHeight + " pixels, " + mapHeight + " tiles");

					// create tiles
					tile(currentImage, TILE_FILE_PREFIX + i + TILE_FILE_SEPARATOR + "%d",
							TILE_FILE_EXTENSION, tileWidth, tileHeight);

					// add files to list
					for (int num = 0; num < mapWidth * mapHeight; num++) {
						files.add(new File(imageFile.getParentFile().getAbsolutePath()
								+ File.separator + TILE_FILE_PREFIX + i + TILE_FILE_SEPARATOR + num
								+ TILE_FILE_EXTENSION));
					}

					// store map width and height at current zoom
					properties.setProperty(PROP_MAP_WIDTH + i, String.valueOf(mapWidth));
					properties.setProperty(PROP_MAP_HEIGHT + i, String.valueOf(mapHeight));

					// create image for next zoom level
					currentWidth /= 2;
					currentHeight /= 2;
					// create temp image file name
					File nextImage = suffixFile(imageFile, i + 1);
					// resize image
					convert(currentImage, nextImage, currentWidth, currentHeight, 100);
					// delete previous temp file
					if (!currentImage.equals(imageFile)) {
						if (!currentImage.delete()) {
							log.warn("Error deleting " + imageFile.getAbsolutePath());
						}
					}

					currentImage = nextImage;
				}

				// delete previous temp file
				if (!currentImage.equals(imageFile)) {
					if (!currentImage.delete()) {
						log.warn("Error deleting " + imageFile.getAbsolutePath());
					}
				}

				// write properties file
				File propertiesFile = new File(imageFile.getParentFile().getAbsolutePath()
						+ File.separator + MAP_PROPERTIES_FILE);
				try {
					FileWriter propertiesWriter = new FileWriter(propertiesFile);
					try {
						properties.store(propertiesWriter,
								"Map generated from " + imageFile.getName());
						// add properties file to list
						files.add(propertiesFile);
					} finally {
						propertiesWriter.close();
					}
				} catch (IOException e) {
					log.error("Error writing map properties file", e);
				}

				// add a converter properties file
				String convProperties = askForPath(fileChooser,
						new ExactFileFilter(CONVERTER_PROPERTIES_FILE),
						"Select a converter properties file");
				File convFile = null;
				if (convProperties != null) {
					convFile = new File(convProperties);
					files.add(convFile);
				}

				// create jar file
				log.info("Creating jar archive...");
				if (createJarArchive(replaceExtension(imageFile, MAP_ARCHIVE_EXTENSION), files)) {
					log.info("Archive successfully created, deleting tiles...");
					// don't delete converter properties
					if (convFile != null)
						files.remove(files.size() - 1);
					// delete files
					for (File file : files) {
						if (!file.delete()) {
							log.warn("Error deleting " + file.getAbsolutePath());
						}
					}
				}

				log.info("Fin.");
			}
		}
	}

	/**
	 * Inserts an integer suffix after the file name of the given file, just
	 * before the extension and returns the file object with the modified file
	 * name
	 * 
	 * @param file the base file name
	 * @param suffix the suffix that shall be inserted
	 * 
	 * @return the file object with the modified file name
	 */
	private File suffixFile(File file, int suffix) {
		String fileName = file.getAbsolutePath();
		int index = fileName.lastIndexOf('.');
		String newName = fileName.substring(0, index) + String.valueOf(suffix)
				+ fileName.substring(index);
		return new File(newName);
	}

	/**
	 * Returns a file object that equals the given file except for the file
	 * extension
	 * 
	 * @param file the file
	 * @param extension the new extension (with leading dot)
	 * 
	 * @return the file object with the modified file name
	 */
	private File replaceExtension(File file, String extension) {
		String fileName = file.getAbsolutePath();
		int index = fileName.lastIndexOf('.');
		String newName = fileName.substring(0, index) + extension;
		return new File(newName);
	}

	/**
	 * Creates a Jar archive that includes the given list of files
	 * 
	 * @param archiveFile the name of the jar archive file
	 * @param tobeJared the files to be included in the jar file
	 * 
	 * @return if the operation was successful
	 */
	public static boolean createJarArchive(File archiveFile, List<File> tobeJared) {
		try {
			byte buffer[] = new byte[BUFFER_SIZE];
			// Open archive file
			FileOutputStream stream = new FileOutputStream(archiveFile);
			JarOutputStream out = new JarOutputStream(stream, new Manifest());

			for (int i = 0; i < tobeJared.size(); i++) {
				if (tobeJared.get(i) == null || !tobeJared.get(i).exists()
						|| tobeJared.get(i).isDirectory())
					continue; // Just in case...
				log.debug("Adding " + tobeJared.get(i).getName());

				// Add archive entry
				JarEntry jarAdd = new JarEntry(tobeJared.get(i).getName());
				jarAdd.setTime(tobeJared.get(i).lastModified());
				out.putNextEntry(jarAdd);

				// Write file to archive
				FileInputStream in = new FileInputStream(tobeJared.get(i));
				while (true) {
					int nRead = in.read(buffer, 0, buffer.length);
					if (nRead <= 0)
						break;
					out.write(buffer, 0, nRead);
				}
				in.close();
			}

			out.close();
			stream.close();
			log.info("Adding completed OK");
			return true;
		} catch (Exception e) {
			log.error("Creating jar file failed", e);
			return false;
		}
	}

	/**
	 * Executes a FileTiler instance
	 * 
	 * @param args ignored
	 */
	public static void main(String[] args) {
		new FileTiler().run();
	}

}
