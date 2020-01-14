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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.impl.LogProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Tests loading a GeoPackage schema.
 * 
 * @author Simon Templer
 */
public class GeopackageSchemaReaderTest {

	/**
	 * Load a schema from a Geopackage file.
	 * 
	 * @param file the file
	 * @return the loaded schema
	 */
	@SuppressWarnings("javadoc")
	public static Schema loadSchema(File file)
			throws IOProviderConfigurationException, IOException {
		GeopackageSchemaReader reader = new GeopackageSchemaReader();

		reader.setSchemaSpace(SchemaSpaceID.SOURCE);
		reader.setSource(new FileIOSupplier(file));

		IOReport report = reader.execute(new LogProgressIndicator());

		assertTrue(report.isSuccess());
		assertTrue(report.getErrors().isEmpty());

		return reader.getSchema();
	}

	/**
	 * Test reading the schema of an example file.
	 */
	@Test
	public void testReadSchema() {
		GeopackageApiTest.withWastewaterTestFile(file -> {
			try {
				Schema schema = loadSchema(file);

				Collection<? extends TypeDefinition> types = schema.getMappingRelevantTypes();
				assertNotNull(types);
				assertEquals(1, types.size());

				TypeDefinition type = types.iterator().next();

				// type name
				assertEquals("wastewater_discharge", type.getName().getLocalPart());

				Set<String> expectedColumns = new HashSet<>();
				expectedColumns.add("geom");

				expectedColumns.add("fid");
				expectedColumns.add("item_id");
				expectedColumns.add("ai_id");
				expectedColumns.add("ai_name");
				expectedColumns.add("ai_program");
				expectedColumns.add("ai_prg_cod");
				expectedColumns.add("si_designa");
				expectedColumns.add("descriptio");
				expectedColumns.add("si_type");
				expectedColumns.add("si_cat");
				expectedColumns.add("si_id");
				expectedColumns.add("si_cat_des");
				expectedColumns.add("si_type_de");
				expectedColumns.add("permit_num");
				expectedColumns.add("permit_sta");
				expectedColumns.add("activity_i");
				expectedColumns.add("int_doc_id");
				expectedColumns.add("ind_vs_dom");
				expectedColumns.add("ownership");
				expectedColumns.add("npdes");
				expectedColumns.add("epa_npdes_");
				expectedColumns.add("fac_design");
				expectedColumns.add("flow_type_");
				expectedColumns.add("avg_annual");
				expectedColumns.add("avg_daily_");
				expectedColumns.add("avg_dry_we");
				expectedColumns.add("avg_wet_we");
				expectedColumns.add("max_daily_");
				expectedColumns.add("which_mont");
				expectedColumns.add("loc_desc");
				expectedColumns.add("address1");
				expectedColumns.add("address2");
				expectedColumns.add("city");
				expectedColumns.add("state");
				expectedColumns.add("zip");
				expectedColumns.add("county_cod");
				expectedColumns.add("county");
				expectedColumns.add("ctu_code");
				expectedColumns.add("ctu_name");
				expectedColumns.add("cong_dist");
				expectedColumns.add("house_dist");
				expectedColumns.add("senate_dis");
				expectedColumns.add("huc8");
				expectedColumns.add("huc8_name");
				expectedColumns.add("huc10");
				expectedColumns.add("huc12");
				expectedColumns.add("huc12_name");
				expectedColumns.add("dwsma_code");
				expectedColumns.add("dwsma_name");
				expectedColumns.add("trdsqq");
				expectedColumns.add("pls_twsp");
				expectedColumns.add("range");
				expectedColumns.add("range_dir");
				expectedColumns.add("section");
				expectedColumns.add("quarters");
				expectedColumns.add("latitude");
				expectedColumns.add("longitude");
				expectedColumns.add("method_cod");
				expectedColumns.add("method_des");
				expectedColumns.add("ref_code");
				expectedColumns.add("ref_desc");
				expectedColumns.add("verified");
				expectedColumns.add("collection");

				for (PropertyDefinition property : DefinitionUtil.getAllProperties(type)) {
					String name = property.getName().getLocalPart();
					assertTrue("Unexpected property: " + name, expectedColumns.contains(name));
					expectedColumns.remove(name);
				}

				assertEquals(
						"Did not find expected columns: "
								+ expectedColumns.stream().collect(Collectors.joining(", ")),
						0, expectedColumns.size());

				// TODO check property types?
			} catch (RuntimeException e) {
				throw e;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}

}
