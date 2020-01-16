/*
 * Copyright (c) 2020 wetransform GmbH
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

package eu.esdihumboldt.hale.io.geopackage;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.function.Consumer;

import org.junit.Test;

import mil.nga.geopackage.GeoPackage;
import mil.nga.geopackage.features.user.FeatureDao;
import mil.nga.geopackage.manager.GeoPackageManager;

/**
 * Tests using the Geopackage API. These are mostly intended to verify that the
 * geopackage library is properly integrated into the target platform.
 * 
 * @author Simon Templer
 */
public class GeopackageApiTest {

	/**
	 * Run a handler on the wastewater geopackage test file.
	 * 
	 * @param handler the file handler
	 */
	public static void withWastewaterTestFile(Consumer<File> handler) {
		withTestFile("testdata/util_wastewater_discharge.gpkg", handler);
	}

	/**
	 * Run a handler on a test file from the classpath.
	 * 
	 * @param name the name in the classpath
	 * @param handler the file handler
	 */
	public static void withTestFile(String name, Consumer<File> handler) {
		try {
			Path tempFile = Files.createTempFile("tmp", ".gpkg");
			try (InputStream in = GeopackageApiTest.class.getClassLoader()
					.getResourceAsStream(name)) {
				Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
			}

			try {
				handler.accept(tempFile.toFile());
			} finally {
				Files.delete(tempFile);
			}
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Test using the Geopackage API to load a file.
	 */
	@Test
	public void testLoadFile() {
		withTestFile("testdata/util_wastewater_discharge.gpkg", file -> {
			GeoPackage gp = GeoPackageManager.open(file);

			List<String> tables = gp.getFeatureTables();
			assertEquals(1, tables.size());

			String tableName = tables.get(0);
			FeatureDao features = gp.getFeatureDao(tableName);

//			FeatureTable table = features.getTable();

			assertEquals(3193, features.count());
		});
	}

}
