/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.io.appschema.writer;

import static eu.esdihumboldt.hale.io.appschema.writer.AppSchemaMappingUtils.GML_NIL_REASON;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Element;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.cst.functions.core.ClassificationMapping;
import eu.esdihumboldt.cst.functions.numeric.MathematicalExpressionFunction;
import eu.esdihumboldt.cst.functions.string.DateExtractionFunction;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.Condition;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.Property;
import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.align.model.functions.AssignFunction;
import eu.esdihumboldt.hale.common.align.model.functions.ClassificationMappingFunction;
import eu.esdihumboldt.hale.common.align.model.functions.FormattedStringFunction;
import eu.esdihumboldt.hale.common.align.model.functions.JoinFunction;
import eu.esdihumboldt.hale.common.align.model.functions.RenameFunction;
import eu.esdihumboldt.hale.common.align.model.functions.RetypeFunction;
import eu.esdihumboldt.hale.common.align.model.functions.join.JoinParameter;
import eu.esdihumboldt.hale.common.align.model.functions.join.JoinParameter.JoinCondition;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultAlignment;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultCell;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultProperty;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultType;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.impl.ComplexValue;
import eu.esdihumboldt.hale.common.core.io.impl.StringValue;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.lookup.LookupTable;
import eu.esdihumboldt.hale.common.lookup.impl.LookupTableImpl;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.io.SchemaReader;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeIndex;
import eu.esdihumboldt.hale.common.schema.persist.hsd.HaleSchemaReader;
import eu.esdihumboldt.hale.common.test.TestUtil;
import eu.esdihumboldt.hale.io.appschema.impl.internal.generated.app_schema.AppSchemaDataAccessType;
import eu.esdihumboldt.hale.io.appschema.impl.internal.generated.app_schema.AttributeMappingType;
import eu.esdihumboldt.hale.io.appschema.impl.internal.generated.app_schema.AttributeMappingType.ClientProperty;
import eu.esdihumboldt.hale.io.appschema.impl.internal.generated.app_schema.TypeMappingsPropertyType.FeatureTypeMapping;
import eu.esdihumboldt.hale.io.appschema.model.FeatureChaining;
import eu.esdihumboldt.hale.io.appschema.writer.internal.AssignHandler;
import eu.esdihumboldt.hale.io.appschema.writer.internal.ClassificationHandler;
import eu.esdihumboldt.hale.io.appschema.writer.internal.DateExtractionHandler;
import eu.esdihumboldt.hale.io.appschema.writer.internal.FormattedStringHandler;
import eu.esdihumboldt.hale.io.appschema.writer.internal.JoinHandler;
import eu.esdihumboldt.hale.io.appschema.writer.internal.MathematicalExpressionHandler;
import eu.esdihumboldt.hale.io.appschema.writer.internal.PropertyTransformationHandler;
import eu.esdihumboldt.hale.io.appschema.writer.internal.PropertyTransformationHandlerFactory;
import eu.esdihumboldt.hale.io.appschema.writer.internal.RenameHandler;
import eu.esdihumboldt.hale.io.appschema.writer.internal.RetypeHandler;
import eu.esdihumboldt.hale.io.appschema.writer.internal.UnsupportedTransformationException;
import eu.esdihumboldt.hale.io.appschema.writer.internal.mapping.AppSchemaMappingContext;
import eu.esdihumboldt.hale.io.appschema.writer.internal.mapping.AppSchemaMappingWrapper;
import eu.esdihumboldt.hale.io.xsd.reader.XmlSchemaReader;

@SuppressWarnings("javadoc")
public class AppSchemaMappingTest {

	private static final ALogger log = ALoggerFactory.getLogger(AppSchemaMappingTest.class);

	private static final String SOURCE_PATH = "/data/landcover_source.hsd";
	private static final String TARGET_PATH = "/data/LandCoverVector.xsd";
	private static final String FEATURE_CHAINING_CONF_PATH = "/data/feature-chaining-landcover.xml";

	private static final String SOURCE_NS = "jdbc:postgresql:lamma:public";
	private static final String LANDCOVER_NS = "http://inspire.ec.europa.eu/schemas/lcv/3.0";
	private static final String LANDCOVER_PREFIX = "lcv";
	private static final String BASE_NS = "http://inspire.ec.europa.eu/schemas/base/3.3";
	private static final String BASE_PREFIX = "base";
	private static final String GML_NS = "http://www.opengis.net/gml/3.2";
	private static final String GML_PREFIX = "gml";
	private static final String GMD_NS = "http://www.isotc211.org/2005/gmd";
	private static final String GMD_PREFIX = "gmd";
	private static final String XLINK_NS = "http://www.w3.org/1999/xlink";
	private static final String XLINK_PREFIX = "xlink";

	private static final String SOURCE_DATASET = "landcover_ds";
	private static final String SOURCE_UNIT = "landcover_lcu";
	private static final String SOURCE_UNIT_DENORM = "landcover_denorm";
	private static final String SOURCE_OBSERVATION = "landcover_obs";
	private static final String SOURCE_DATASET_ID = "dataset_id";
	private static final String SOURCE_UNIT_ID = "unit_id";
	private static final String SOURCE_UUID_V1 = "uuid_v1";
	private static final String SOURCE_UCS2007 = "ucs2007";
	private static final String SOURCE_UCS2013 = "ucs2013";
	private static final String SOURCE_GEOM = "geom";
	private static final String SOURCE_OBS_UNIT_ID = "lcu_id";
	private static final String SOURCE_OBS_CLASS = "ucs";
	private static final String TARGET_LOCAL_ID = "lcv:inspireId/base:Identifier/base:localId";
	private static final String TARGET_FIRST_OBSERVATION_DATE = "lcv:landCoverObservation[1]/lcv:LandCoverObservation/lcv:observationDate";
	private static final String TARGET_DESCRIPTION = "gml:description";
	private static final String TARGET_GEOMETRY_LCD = "lcv:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_BoundingPolygon/gmd:polygon/gml:AbstractGeometry";
	private static final String TARGET_GEOMETRY_LCU = "lcv:geometry";
	private static final String TARGET_METADATA = "gml:metaDataProperty";

	private static Schema source;
	private static Schema target;
	private static TypeDefinition datasetType;
	private static TypeDefinition unitDenormType;
	private static TypeDefinition unitType;
	private static TypeDefinition observationType;
	private static TypeDefinition landCoverUnitType;
	private static TypeDefinition landCoverDatasetType;
	private static TypeDefinition landCoverObservationType;
	private static Set<TypeDefinition> targetTypes = new HashSet<TypeDefinition>();

	private AppSchemaMappingWrapper mappingWrapper;
	private AppSchemaMappingContext mappingContext;

	@BeforeClass
	public static void init() throws Exception {

		TestUtil.startConversionService();

		source = loadSchema(new HaleSchemaReader(), SOURCE_PATH);
		assertNotNull(source);
		target = loadSchema(new XmlSchemaReader(), TARGET_PATH);
		assertNotNull(target);

		datasetType = source.getType(new QName(SOURCE_NS, SOURCE_DATASET));
		assertNotNull(datasetType);
		unitDenormType = source.getType(new QName(SOURCE_NS, SOURCE_UNIT_DENORM));
		assertNotNull(unitDenormType);
		unitType = source.getType(new QName(SOURCE_NS, SOURCE_UNIT));
		assertNotNull(unitType);
		observationType = source.getType(new QName(SOURCE_NS, SOURCE_OBSERVATION));
		assertNotNull(observationType);
		landCoverUnitType = target.getType(new QName(LANDCOVER_NS, "LandCoverUnitType"));
		assertNotNull(landCoverUnitType);
		landCoverDatasetType = target.getType(new QName(LANDCOVER_NS, "LandCoverDatasetType"));
		assertNotNull(landCoverDatasetType);
		landCoverObservationType = target.getType(new QName(LANDCOVER_NS,
				"LandCoverObservationType"));
		assertNotNull(landCoverObservationType);

		targetTypes.add(landCoverDatasetType);
		targetTypes.add(landCoverUnitType);
		targetTypes.add(landCoverObservationType);
	}

	@Before
	public void initMapping() {
		mappingWrapper = new AppSchemaMappingWrapper(new AppSchemaDataAccessType());
		mappingContext = new AppSchemaMappingContext(mappingWrapper);
		// initialize namespaces
		mappingContext.getOrCreateNamespace(LANDCOVER_NS, LANDCOVER_PREFIX);
		mappingContext.getOrCreateNamespace(BASE_NS, BASE_PREFIX);
		mappingContext.getOrCreateNamespace(GML_NS, GML_PREFIX);
		mappingContext.getOrCreateNamespace(GMD_NS, GMD_PREFIX);
		mappingContext.getOrCreateNamespace(XLINK_NS, XLINK_PREFIX);
	}

	private static Schema loadSchema(SchemaReader schemaReader, String resource) throws Exception {
		DefaultInputSupplier input = new DefaultInputSupplier(AppSchemaMappingTest.class
				.getResource(resource).toURI());
		schemaReader.setSharedTypes(new DefaultTypeIndex());
		schemaReader.setSource(input);

		schemaReader.validate();
		IOReport report = schemaReader.execute(null);

		assertTrue(report.isSuccess());
		assertTrue("Errors are contained in the report", report.getErrors().isEmpty());

		return schemaReader.getSchema();
	}

	@Test
	public void testRetypeHandler() {
		DefaultCell cell = new DefaultCell();
		cell.setTransformationIdentifier(RetypeFunction.ID);

		ListMultimap<String, Type> source = ArrayListMultimap.create();
		source.put(null, new DefaultType(new TypeEntityDefinition(unitDenormType,
				SchemaSpaceID.SOURCE, null)));
		ListMultimap<String, Type> target = ArrayListMultimap.create();
		target.put(null, new DefaultType(new TypeEntityDefinition(landCoverUnitType,
				SchemaSpaceID.TARGET, null)));
		cell.setSource(source);
		cell.setTarget(target);

		RetypeHandler handler = new RetypeHandler();
		FeatureTypeMapping ftMapping = handler.handleTypeTransformation(cell,
				new AppSchemaMappingContext(mappingWrapper));
		assertEquals(SOURCE_UNIT_DENORM, ftMapping.getSourceType());
		assertEquals("lcv:LandCoverUnit", ftMapping.getTargetElement());
	}

	@Test
	public void testNestedJoinHandler() {
		DefaultCell joinCell = buildJoinCell(null);

		// create minimal alignment and pass it to JoinHandler
		DefaultCell renameCell = new DefaultCell();
		renameCell.setTransformationIdentifier(RenameFunction.ID);
		renameCell.setSource(getUuidSourceProperty(unitDenormType));
		renameCell.setTarget(getNestedUnitLocalIdTargetProperty());

		DefaultAlignment alignment = new DefaultAlignment();
		alignment.addCell(joinCell);
		alignment.addCell(renameCell);

		processJoinAlignment(alignment, null);

//		logMapping(mappingWrapper.getMainMapping());

		List<FeatureTypeMapping> ftMappings = mappingWrapper.getMainMapping().getTypeMappings()
				.getFeatureTypeMapping();
		assertEquals(2, ftMappings.size());

		FeatureTypeMapping lcdMapping = null, lcuMapping = null;
		for (FeatureTypeMapping ftMapping : ftMappings) {
			if (SOURCE_DATASET.equals(ftMapping.getSourceType())
					&& "lcv:LandCoverDataset".equals(ftMapping.getTargetElement())) {
				lcdMapping = ftMapping;
			}
			if (SOURCE_UNIT_DENORM.equals(ftMapping.getSourceType())
					&& "lcv:LandCoverUnit".equals(ftMapping.getTargetElement())) {
				lcuMapping = ftMapping;
			}
		}
		assertNotNull(lcdMapping);
		assertNotNull(lcuMapping);

		// check feature chaining configuration
		List<AttributeMappingType> lcdAttrMappings = lcdMapping.getAttributeMappings()
				.getAttributeMapping();
		List<AttributeMappingType> lcuAttrMappings = lcuMapping.getAttributeMappings()
				.getAttributeMapping();
		assertNotNull(lcdAttrMappings);
		assertNotNull(lcuAttrMappings);
		assertEquals(1, lcdAttrMappings.size());
		assertEquals(2, lcuAttrMappings.size());

		AttributeMappingType containerMapping = lcdAttrMappings.get(0);
		assertEquals("lcv:member", containerMapping.getTargetAttribute());
		assertEquals("lcv:LandCoverUnit", containerMapping.getSourceExpression().getLinkElement());
		assertEquals("FEATURE_LINK[1]", containerMapping.getSourceExpression().getLinkField());
		assertEquals(SOURCE_DATASET_ID, containerMapping.getSourceExpression().getOCQL());
		assertTrue(containerMapping.isIsMultiple());

		AttributeMappingType nestedMapping = lcuAttrMappings.get(0);
		assertEquals("FEATURE_LINK[1]", nestedMapping.getTargetAttribute());
		assertEquals(SOURCE_DATASET_ID, nestedMapping.getSourceExpression().getOCQL());
		assertNull(nestedMapping.getSourceExpression().getLinkElement());
		assertNull(nestedMapping.getSourceExpression().getLinkField());
	}

	@Test
	public void testXrefJoinHandler() {
		DefaultCell joinCell = buildJoinCell(null);

		// create minimal alignment and pass it to JoinHandler
		DefaultCell renameCell = new DefaultCell();
		renameCell.setTransformationIdentifier(RenameFunction.ID);
		renameCell.setSource(getUnitIdSourceProperty(unitDenormType));
		renameCell.setTarget(getNestedUnitHrefTargetProperty());

		DefaultAlignment alignment = new DefaultAlignment();
		alignment.addCell(joinCell);
		alignment.addCell(renameCell);

		processJoinAlignment(alignment, null);

//		logMapping(mappingWrapper.getMainMapping());

		List<FeatureTypeMapping> ftMappings = mappingWrapper.getMainMapping().getTypeMappings()
				.getFeatureTypeMapping();
		assertEquals(2, ftMappings.size());

		FeatureTypeMapping lcdMapping = null, lcuMapping = null;
		for (FeatureTypeMapping ftMapping : ftMappings) {
			if (SOURCE_DATASET.equals(ftMapping.getSourceType())
					&& "lcv:LandCoverDataset".equals(ftMapping.getTargetElement())) {
				lcdMapping = ftMapping;
			}
			if (SOURCE_UNIT_DENORM.equals(ftMapping.getSourceType())
					&& "lcv:LandCoverUnit".equals(ftMapping.getTargetElement())) {
				lcuMapping = ftMapping;
			}
		}
		assertNotNull(lcdMapping);
		assertNotNull(lcuMapping);

		// check feature chaining configuration
		List<AttributeMappingType> lcdAttrMappings = lcdMapping.getAttributeMappings()
				.getAttributeMapping();
		List<AttributeMappingType> lcuAttrMappings = lcuMapping.getAttributeMappings()
				.getAttributeMapping();
		assertNotNull(lcdAttrMappings);
		assertNotNull(lcuAttrMappings);
		assertEquals(1, lcdAttrMappings.size());
		assertEquals(1, lcuAttrMappings.size());

		AttributeMappingType containerMapping = lcdAttrMappings.get(0);
		assertEquals("lcv:member", containerMapping.getTargetAttribute());
		assertEquals("lcv:LandCoverUnit", containerMapping.getSourceExpression().getLinkElement());
		assertEquals("FEATURE_LINK[1]", containerMapping.getSourceExpression().getLinkField());
		assertEquals(SOURCE_DATASET_ID, containerMapping.getSourceExpression().getOCQL());
		assertTrue(containerMapping.isIsMultiple());
		assertNotNull(containerMapping.getClientProperty());
		assertEquals(1, containerMapping.getClientProperty().size());
		assertEquals("xlink:href", containerMapping.getClientProperty().get(0).getName());
		assertEquals(SOURCE_UNIT_ID, containerMapping.getClientProperty().get(0).getValue());

		AttributeMappingType nestedMapping = lcuAttrMappings.get(0);
		assertEquals("FEATURE_LINK[1]", nestedMapping.getTargetAttribute());
		assertEquals(SOURCE_DATASET_ID, nestedMapping.getSourceExpression().getOCQL());
		assertNull(nestedMapping.getSourceExpression().getLinkElement());
		assertNull(nestedMapping.getSourceExpression().getLinkField());
	}

	@Test
	public void testFeatureChainingJoin() throws Exception {
		FeatureChaining chainingConf = readFeatureChainingConfiguration(FEATURE_CHAINING_CONF_PATH);
		assertNotNull(chainingConf);

		DefaultCell joinCell = buildJoinCell(chainingConf);

		// create minimal alignment and pass it to JoinHandler
		DefaultAlignment alignment = new DefaultAlignment();
		alignment.addCell(joinCell);

		DefaultCell renameCell = new DefaultCell();
		renameCell.setTransformationIdentifier(RenameFunction.ID);
		renameCell.setSource(getUuidSourceProperty(unitType));
		renameCell.setTarget(getNestedUnitLocalIdTargetProperty());
		alignment.addCell(renameCell);

		DefaultCell renameClassCell = new DefaultCell();
		renameClassCell.setTransformationIdentifier(RenameFunction.ID);
		renameClassCell.setSource(getObservationClassSourceProperty());
		renameClassCell.setTarget(getNestedObservationClassTargetProperty());
		alignment.addCell(renameClassCell);

		processJoinAlignment(alignment, chainingConf);

//		logMapping(mappingWrapper.getMainMapping());
//		logMapping(mappingWrapper.getIncludedTypesMapping());

		List<FeatureTypeMapping> mainMappings = mappingWrapper.getMainMapping().getTypeMappings()
				.getFeatureTypeMapping();
		assertEquals(2, mainMappings.size());
		List<FeatureTypeMapping> includedMappings = mappingWrapper.getIncludedTypesMapping()
				.getTypeMappings().getFeatureTypeMapping();
		assertEquals(1, includedMappings.size());

		FeatureTypeMapping obsMapping = includedMappings.get(0);
		assertTrue(SOURCE_OBSERVATION.equals(obsMapping.getSourceType())
				&& "lcv:LandCoverObservation".equals(obsMapping.getTargetElement()));
		FeatureTypeMapping lcdMapping = null, lcuMapping = null;
		for (FeatureTypeMapping ftMapping : mainMappings) {
			if (SOURCE_DATASET.equals(ftMapping.getSourceType())
					&& "lcv:LandCoverDataset".equals(ftMapping.getTargetElement())) {
				lcdMapping = ftMapping;
			}
			if (SOURCE_UNIT.equals(ftMapping.getSourceType())
					&& "lcv:LandCoverUnit".equals(ftMapping.getTargetElement())) {
				lcuMapping = ftMapping;
			}
		}
		assertNotNull(lcdMapping);
		assertNotNull(lcuMapping);

		// check feature chaining configuration
		List<AttributeMappingType> lcdAttrMappings = lcdMapping.getAttributeMappings()
				.getAttributeMapping();
		List<AttributeMappingType> lcuAttrMappings = lcuMapping.getAttributeMappings()
				.getAttributeMapping();
		List<AttributeMappingType> obsAttrMappings = obsMapping.getAttributeMappings()
				.getAttributeMapping();
		assertNotNull(lcdAttrMappings);
		assertNotNull(lcuAttrMappings);
		assertNotNull(obsAttrMappings);
		assertEquals(1, lcdAttrMappings.size());
		assertEquals(3, lcuAttrMappings.size());
		assertEquals(2, obsAttrMappings.size());

		// NOTE: the order of attribute mappings is predictable, as it follows
		// the order of join conditions
		AttributeMappingType datasetContainerMapping = lcdAttrMappings.get(0);
		assertEquals("lcv:member", datasetContainerMapping.getTargetAttribute());
		assertEquals("lcv:LandCoverUnit", datasetContainerMapping.getSourceExpression()
				.getLinkElement());
		assertEquals("FEATURE_LINK[1]", datasetContainerMapping.getSourceExpression()
				.getLinkField());
		assertEquals(SOURCE_DATASET_ID, datasetContainerMapping.getSourceExpression().getOCQL());
		assertTrue(datasetContainerMapping.isIsMultiple());

		AttributeMappingType unitNestedMapping = lcuAttrMappings.get(0);
		assertEquals("FEATURE_LINK[1]", unitNestedMapping.getTargetAttribute());
		assertEquals(SOURCE_DATASET_ID, unitNestedMapping.getSourceExpression().getOCQL());
		assertNull(unitNestedMapping.getSourceExpression().getLinkElement());
		assertNull(unitNestedMapping.getSourceExpression().getLinkField());
		assertNull(unitNestedMapping.isIsMultiple());

		AttributeMappingType unitContainerMapping = lcuAttrMappings.get(1);
		assertEquals("lcv:landCoverObservation", unitContainerMapping.getTargetAttribute());
		assertEquals(SOURCE_UNIT_ID, unitContainerMapping.getSourceExpression().getOCQL());
		assertEquals("lcv:LandCoverObservation", unitContainerMapping.getSourceExpression()
				.getLinkElement());
		assertEquals("FEATURE_LINK[1]", unitContainerMapping.getSourceExpression().getLinkField());
		assertTrue(unitContainerMapping.isIsMultiple());

		AttributeMappingType unitLocalIdMapping = lcuAttrMappings.get(2);
		assertEquals(TARGET_LOCAL_ID, unitLocalIdMapping.getTargetAttribute());
		assertEquals(SOURCE_UUID_V1, unitLocalIdMapping.getSourceExpression().getOCQL());
		assertNull(unitLocalIdMapping.getSourceExpression().getLinkElement());
		assertNull(unitLocalIdMapping.getSourceExpression().getLinkField());
		assertNull(unitLocalIdMapping.isIsMultiple());

		AttributeMappingType observationNestedMapping = obsAttrMappings.get(0);
		assertEquals("FEATURE_LINK[1]", observationNestedMapping.getTargetAttribute());
		assertEquals(SOURCE_OBS_UNIT_ID, observationNestedMapping.getSourceExpression().getOCQL());
		assertNull(observationNestedMapping.getSourceExpression().getLinkElement());
		assertNull(observationNestedMapping.getSourceExpression().getLinkField());
		assertNull(observationNestedMapping.isIsMultiple());

		AttributeMappingType observationClassMapping = obsAttrMappings.get(1);
		assertEquals("lcv:class", observationClassMapping.getTargetAttribute());
		assertEquals(SOURCE_OBS_CLASS, observationClassMapping.getSourceExpression().getOCQL());
		assertNull(observationClassMapping.getSourceExpression().getLinkElement());
		assertNull(observationClassMapping.getSourceExpression().getLinkField());
		assertNull(observationClassMapping.isIsMultiple());
	}

	@SuppressWarnings("null")
	private DefaultCell buildJoinCell(FeatureChaining chainingConf) {
		boolean withChaining = chainingConf != null;

		DefaultCell joinCell = new DefaultCell();
		joinCell.setTransformationIdentifier(JoinFunction.ID);
		if (withChaining) {
			// set cell ID from feature chaining configuration
			// WARNING: this code assumes only one join is configured
			joinCell.setId(chainingConf.getJoins().keySet().iterator().next());
		}

		TypeEntityDefinition datasetEntityDef = new TypeEntityDefinition(datasetType,
				SchemaSpaceID.SOURCE, null);
		TypeEntityDefinition unitEntityDef = null;
		TypeEntityDefinition observationEntityDef = null;
		if (!withChaining) {
			unitEntityDef = new TypeEntityDefinition(unitDenormType, SchemaSpaceID.SOURCE, null);
		}
		else {
			unitEntityDef = new TypeEntityDefinition(unitType, SchemaSpaceID.SOURCE, null);
			observationEntityDef = new TypeEntityDefinition(observationType, SchemaSpaceID.SOURCE,
					null);
		}
		ListMultimap<String, Type> source = ArrayListMultimap.create();
		source.put(JoinFunction.JOIN_TYPES, new DefaultType(datasetEntityDef));
		source.put(JoinFunction.JOIN_TYPES, new DefaultType(unitEntityDef));
		if (observationEntityDef != null) {
			source.put(JoinFunction.JOIN_TYPES, new DefaultType(observationEntityDef));
			assertEquals(3, source.get(JoinFunction.JOIN_TYPES).size());
		}
		else {
			assertEquals(2, source.get(JoinFunction.JOIN_TYPES).size());
		}

		ListMultimap<String, Type> target = ArrayListMultimap.create();
		target.put(null, new DefaultType(new TypeEntityDefinition(landCoverDatasetType,
				SchemaSpaceID.TARGET, null)));

		List<TypeEntityDefinition> types = new ArrayList<TypeEntityDefinition>(Arrays.asList(
				datasetEntityDef, unitEntityDef));
		Set<JoinCondition> conditions = new HashSet<JoinCondition>();
		// join dataset and unit
		PropertyEntityDefinition baseProperty = getDatasetIdSourceProperty().values().iterator()
				.next().getDefinition();
		PropertyEntityDefinition joinProperty = getUnitDatasetIdSourceProperty(
				unitEntityDef.getType()).values().iterator().next().getDefinition();
		conditions.add(new JoinCondition(baseProperty, joinProperty));
		if (withChaining) {
			// add observation type
			types.add(observationEntityDef);
			// join unit and observation
			baseProperty = getUnitIdSourceProperty(unitEntityDef.getType()).values().iterator()
					.next().getDefinition();
			joinProperty = getObservationUnitIdSourceProperty().values().iterator().next()
					.getDefinition();
			conditions.add(new JoinCondition(baseProperty, joinProperty));
		}
		JoinParameter joinParam = new JoinParameter(types, conditions);

		ListMultimap<String, ParameterValue> parameters = ArrayListMultimap.create();
		parameters
				.put(JoinFunction.PARAMETER_JOIN, new ParameterValue(new ComplexValue(joinParam)));

		joinCell.setSource(source);
		joinCell.setTarget(target);
		joinCell.setTransformationParameters(parameters);

		return joinCell;
	}

	private void processJoinAlignment(Alignment alignment, FeatureChaining chainingConf) {
		AppSchemaMappingContext context = new AppSchemaMappingContext(mappingWrapper, alignment,
				targetTypes, chainingConf, null);
		Cell joinCell = alignment.getTypeCells().iterator().next();
		JoinHandler handler = new JoinHandler();
		handler.handleTypeTransformation(joinCell, context);
		for (Cell propertyCell : alignment.getPropertyCells(joinCell)) {
			String propertyTransformId = propertyCell.getTransformationIdentifier();
			PropertyTransformationHandler propertyTransformHandler;
			try {
				propertyTransformHandler = PropertyTransformationHandlerFactory.getInstance()
						.createPropertyTransformationHandler(propertyTransformId);
				propertyTransformHandler.handlePropertyTransformation(joinCell, propertyCell,
						context);
			} catch (UnsupportedTransformationException e) {
				Assert.fail("Unsupported transformation was found");
			}
		}
	}

	private FeatureChaining readFeatureChainingConfiguration(String confResource) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Element root = builder.parse(getClass().getResourceAsStream(confResource))
				.getDocumentElement();

		// read from XML
		FeatureChaining chainingConf = HaleIO.getComplexValue(root, FeatureChaining.class, null);
		assertNotNull(chainingConf);
		AppSchemaMappingUtils.resolvePropertyTypes(chainingConf, target, SchemaSpaceID.TARGET);
		return chainingConf;
	}

	@Test
	public void testRenameHandler() {
		DefaultCell cell = new DefaultCell();
		cell.setTransformationIdentifier(RenameFunction.ID);

		cell.setSource(getUuidSourceProperty(unitDenormType));
		cell.setTarget(getLocalIdTargetProperty());

		RenameHandler renameHandler = new RenameHandler();
		AttributeMappingType attrMapping = renameHandler.handlePropertyTransformation(
				getDefaultTypeCell(unitDenormType, landCoverUnitType), cell,
				new AppSchemaMappingContext(mappingWrapper));
		assertEquals("uuid_v1", attrMapping.getSourceExpression().getOCQL());
		assertEquals(TARGET_LOCAL_ID, attrMapping.getTargetAttribute());
	}

	@Test
	public void testDateExtractionHandler() {
		final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
		final String OCQL = "dateParse(" + SOURCE_UUID_V1 + ", '" + DATE_FORMAT + "')";

		DefaultCell cell = new DefaultCell();
		cell.setTransformationIdentifier(DateExtractionFunction.ID);

		ListMultimap<String, ParameterValue> parameters = ArrayListMultimap.create();
		parameters.put(DateExtractionFunction.PARAMETER_DATE_FORMAT,
				new ParameterValue(DATE_FORMAT));

		cell.setSource(getUuidSourceProperty(unitDenormType));
		cell.setTarget(getFirstObservationDateTargetProperty());
		cell.setTransformationParameters(parameters);

		DateExtractionHandler handler = new DateExtractionHandler();
		AttributeMappingType attrMapping = handler.handlePropertyTransformation(
				getDefaultTypeCell(unitDenormType, landCoverUnitType), cell,
				new AppSchemaMappingContext(mappingWrapper));
		assertEquals(OCQL, attrMapping.getSourceExpression().getOCQL());
		assertEquals(TARGET_FIRST_OBSERVATION_DATE, attrMapping.getTargetAttribute());
	}

	@Test
	public void testFormattedStringHandler() {
		final String PATTERN = "Class 2007: {ucs2007}; Class 2013: {ucs2013}";
		final String OCQL = "strConcat(strConcat(strConcat('Class 2007: ', ucs2007), '; Class 2013: '), ucs2013)";

		DefaultCell cell = new DefaultCell();
		cell.setTransformationIdentifier(FormattedStringFunction.ID);

		ListMultimap<String, ParameterValue> parameters = ArrayListMultimap.create();
		parameters.put(FormattedStringFunction.PARAMETER_PATTERN, new ParameterValue(PATTERN));

		ListMultimap<String, Property> source = ArrayListMultimap.create();
		source.putAll(FormattedStringFunction.ENTITY_VARIABLE, getUcs2007SourceProperty().values());
		source.putAll(FormattedStringFunction.ENTITY_VARIABLE, getUcs2013SourceProperty().values());
		cell.setSource(source);
		cell.setTarget(getDescriptionTargetProperty());
		cell.setTransformationParameters(parameters);

		FormattedStringHandler handler = new FormattedStringHandler();
		AttributeMappingType attrMapping = handler.handlePropertyTransformation(
				getDefaultTypeCell(unitDenormType, landCoverUnitType), cell,
				new AppSchemaMappingContext(mappingWrapper));
		assertEquals(OCQL, attrMapping.getSourceExpression().getOCQL());
		assertEquals(TARGET_DESCRIPTION, attrMapping.getTargetAttribute());
	}

	@Test
	public void testMathematicalExpressionHandler() {
		final String EXPRESSION = "100 * unit_id / 2";
		final String OCQL = EXPRESSION;

		DefaultCell cell = new DefaultCell();
		cell.setTransformationIdentifier(MathematicalExpressionFunction.ID);

		ListMultimap<String, ParameterValue> parameters = ArrayListMultimap.create();
		parameters.put(MathematicalExpressionFunction.PARAMETER_EXPRESSION, new ParameterValue(
				EXPRESSION));

		ListMultimap<String, Property> source = ArrayListMultimap.create();
		source.putAll(MathematicalExpressionFunction.ENTITY_VARIABLE,
				getUnitIdSourceProperty(unitDenormType).values());

		cell.setSource(source);
		cell.setTarget(getLocalIdTargetProperty());
		cell.setTransformationParameters(parameters);

		MathematicalExpressionHandler handler = new MathematicalExpressionHandler();
		AttributeMappingType attrMapping = handler.handlePropertyTransformation(
				getDefaultTypeCell(unitDenormType, landCoverUnitType), cell,
				new AppSchemaMappingContext(mappingWrapper));
		assertEquals(OCQL, attrMapping.getSourceExpression().getOCQL());
		assertEquals(TARGET_LOCAL_ID, attrMapping.getTargetAttribute());
	}

	@Test
	public void testAssignHandler() {
		final String ASSIGN_VALUE = "LCU_1234";
		final String OCQL = "'" + ASSIGN_VALUE + "'";
		final String OCQL_BOUND = "if_then_else(isNull(" + SOURCE_UUID_V1 + "), Expression.NIL, '"
				+ ASSIGN_VALUE + "')";

		Cell typeCell = getDefaultTypeCell(unitDenormType, landCoverUnitType);

		DefaultCell cell = new DefaultCell();
		cell.setTransformationIdentifier(AssignFunction.ID);

		ListMultimap<String, ParameterValue> parameters = ArrayListMultimap.create();
		parameters.put(AssignFunction.PARAMETER_VALUE, new ParameterValue(ASSIGN_VALUE));

		cell.setTarget(getLocalIdTargetProperty());
		cell.setTransformationParameters(parameters);

		AssignHandler assignHandler = new AssignHandler();
		AttributeMappingType attrMapping = assignHandler.handlePropertyTransformation(typeCell,
				cell, new AppSchemaMappingContext(mappingWrapper));
		assertEquals(OCQL, attrMapping.getSourceExpression().getOCQL());
		assertEquals(TARGET_LOCAL_ID, attrMapping.getTargetAttribute());

		// bound version of "Assign"
		DefaultCell cellCopy = new DefaultCell(cell);
		Collection<Property> anchor = getUuidSourceProperty(unitDenormType).values();
		ListMultimap<String, Property> source = ArrayListMultimap.create();
		source.putAll(AssignFunction.ENTITY_ANCHOR, anchor);
		cellCopy.setSource(source);
		cellCopy.setTransformationIdentifier(AssignFunction.ID_BOUND);

		attrMapping = assignHandler.handlePropertyTransformation(typeCell, cellCopy,
				new AppSchemaMappingContext(mappingWrapper));
		assertEquals(OCQL_BOUND, attrMapping.getSourceExpression().getOCQL());
		assertEquals(TARGET_LOCAL_ID, attrMapping.getTargetAttribute());
	}

	@Test
	public void testClassificationHandler() {
		final int FIRST_SOURCE = 1000;
		final String FIRST_TARGET = "http://www.example.com/first";
		final int SECOND_SOURCE = 2000;
		final String SECOND_TARGET = "http://www.example.com/second";
		final int THIRD_SOURCE = 3000;
		final String THIRD_TARGET = "http://www.example.com/third";
		final String FIXED_VALUE = "http://www.example.com/unknown";
		final String OCQL_PATTERN = "if_then_else(in(unit_id,%s), Recode(unit_id,%s), %s)";

		Cell typeCell = getDefaultTypeCell(unitDenormType, landCoverUnitType);

		DefaultCell cell = new DefaultCell();
		cell.setTransformationIdentifier(ClassificationMappingFunction.ID);

		cell.setSource(getUnitIdSourceProperty(unitDenormType));
		cell.setTarget(getMetaDataHrefTargetProperty());

		Map<Value, Value> tableValues = new HashMap<Value, Value>();
		tableValues.put(new StringValue(FIRST_SOURCE), new StringValue(FIRST_TARGET));
		tableValues.put(new StringValue(SECOND_SOURCE), new StringValue(SECOND_TARGET));
		tableValues.put(new StringValue(THIRD_SOURCE), new StringValue(THIRD_TARGET));
		LookupTable lookupTable = new LookupTableImpl(tableValues);

		ListMultimap<String, ParameterValue> parameters = ArrayListMultimap.create();
		parameters.put(ClassificationMappingFunction.PARAMETER_LOOKUPTABLE, new ParameterValue(
				new ComplexValue(lookupTable)));
		parameters.put(ClassificationMapping.PARAMETER_NOT_CLASSIFIED_ACTION, new ParameterValue(
				ClassificationMapping.USE_SOURCE_ACTION));
		cell.setTransformationParameters(parameters);

		StringBuilder inArgs = new StringBuilder();
		StringBuilder recodeArgs = new StringBuilder();
		int count = 0;
		for (Value sourceValue : tableValues.keySet()) {
			inArgs.append(sourceValue.as(String.class));
			recodeArgs.append(sourceValue).append(",'").append(tableValues.get(sourceValue))
					.append("'");
			if (count < tableValues.size() - 1) {
				inArgs.append(",");
				recodeArgs.append(",");
			}
			count++;
		}
		final String OCQL_USE_SOURCE = String.format(OCQL_PATTERN, inArgs.toString(),
				recodeArgs.toString(), "unit_id");
		final String OCQL_USE_NULL = String.format(OCQL_PATTERN, inArgs.toString(),
				recodeArgs.toString(), "Expression.NIL");
		final String OCQL_USE_FIXED = String.format(OCQL_PATTERN, inArgs.toString(),
				recodeArgs.toString(), "'" + FIXED_VALUE + "'");

		ClassificationHandler classificationHandler = new ClassificationHandler();
		AttributeMappingType attrMapping = classificationHandler.handlePropertyTransformation(
				typeCell, cell, new AppSchemaMappingContext(mappingWrapper));
		assertNotNull(attrMapping.getClientProperty());
		assertEquals(1, attrMapping.getClientProperty().size());
		assertEquals("xlink:href", attrMapping.getClientProperty().get(0).getName());
		assertEquals(OCQL_USE_SOURCE, attrMapping.getClientProperty().get(0).getValue());
		assertEquals(TARGET_METADATA, attrMapping.getTargetAttribute());

		// reset mapping
		initMapping();
		parameters.removeAll(ClassificationMapping.PARAMETER_NOT_CLASSIFIED_ACTION);
		parameters.put(ClassificationMapping.PARAMETER_NOT_CLASSIFIED_ACTION, new ParameterValue(
				ClassificationMapping.USE_NULL_ACTION));
		cell.setTransformationParameters(parameters);
		attrMapping = classificationHandler.handlePropertyTransformation(typeCell, cell,
				new AppSchemaMappingContext(mappingWrapper));
		assertNotNull(attrMapping.getClientProperty());
		assertEquals(1, attrMapping.getClientProperty().size());
		assertEquals("xlink:href", attrMapping.getClientProperty().get(0).getName());
		assertEquals(OCQL_USE_NULL, attrMapping.getClientProperty().get(0).getValue());
		assertEquals(TARGET_METADATA, attrMapping.getTargetAttribute());

		// reset mapping
		initMapping();
		parameters.removeAll(ClassificationMapping.PARAMETER_NOT_CLASSIFIED_ACTION);
		parameters.put(ClassificationMapping.PARAMETER_NOT_CLASSIFIED_ACTION, new ParameterValue(
				ClassificationMapping.USE_FIXED_VALUE_ACTION_PREFIX + FIXED_VALUE));
		cell.setTransformationParameters(parameters);
		attrMapping = classificationHandler.handlePropertyTransformation(typeCell, cell,
				new AppSchemaMappingContext(mappingWrapper));
		assertNotNull(attrMapping.getClientProperty());
		assertEquals(1, attrMapping.getClientProperty().size());
		assertEquals("xlink:href", attrMapping.getClientProperty().get(0).getName());
		assertEquals(OCQL_USE_FIXED, attrMapping.getClientProperty().get(0).getValue());
		assertEquals(TARGET_METADATA, attrMapping.getTargetAttribute());
	}

	@Test
	public void testGeometryEncoding() {
		DefaultCell cell = new DefaultCell();
		cell.setTransformationIdentifier(RenameFunction.ID);

		cell.setSource(getUnitGeomSourceProperty(unitDenormType));
		cell.setTarget(getLandCoverDatasetGeometryTargetProperty());

		RenameHandler renameHandler = new RenameHandler();
		AttributeMappingType attrMapping = renameHandler.handlePropertyTransformation(
				getDefaultTypeCell(datasetType, landCoverDatasetType), cell,
				new AppSchemaMappingContext(mappingWrapper));
		assertEquals(SOURCE_GEOM, attrMapping.getSourceExpression().getOCQL());
		assertEquals(TARGET_GEOMETRY_LCD, attrMapping.getTargetAttribute());
		assertEquals("gml:MultiSurfaceType", attrMapping.getTargetAttributeNode());
	}

	@Test
	public void testGmlGeometryPropertyIdEncoding() {
		final String GML_ID_PATTERN = "geom.{dataset_id}.{unit_id}";
		final String GML_ID_OCQL = "strConcat(strConcat(strConcat('geom.', dataset_id), '.'), unit_id)";

		// create mapping context
		AppSchemaMappingContext context = new AppSchemaMappingContext(mappingWrapper);
		// create retype cell
		Cell retypeCell = getDefaultTypeCell(unitDenormType, landCoverUnitType);

		// create rename cell to produce LCU's geometry
		DefaultCell geomRenameCell = new DefaultCell();
		geomRenameCell.setTransformationIdentifier(RenameFunction.ID);

		geomRenameCell.setSource(getUnitGeomSourceProperty(unitDenormType));
		geomRenameCell.setTarget(getLandCoverUnitGeometryTargetProperty());

		// create formatted string cell to produce LCU geometry's gml:id
		DefaultCell geomGmlIdFormatCell = new DefaultCell();
		geomGmlIdFormatCell.setTransformationIdentifier(FormattedStringFunction.ID);

		ListMultimap<String, ParameterValue> parameters = ArrayListMultimap.create();
		parameters.put(FormattedStringFunction.PARAMETER_PATTERN,
				new ParameterValue(GML_ID_PATTERN));

		ListMultimap<String, Property> source = ArrayListMultimap.create();
		source.putAll(FormattedStringFunction.ENTITY_VARIABLE,
				getUnitDatasetIdSourceProperty(unitDenormType).values());
		source.putAll(FormattedStringFunction.ENTITY_VARIABLE,
				getUnitIdSourceProperty(unitDenormType).values());
		geomGmlIdFormatCell.setSource(source);
		geomGmlIdFormatCell.setTarget(getLandCoverUnitGeometryGmlIdTargetProperty());
		geomGmlIdFormatCell.setTransformationParameters(parameters);

		// process cells in whatever order
		FormattedStringHandler formatHandler = new FormattedStringHandler();
		formatHandler.handlePropertyTransformation(retypeCell, geomGmlIdFormatCell, context);
		RenameHandler renameHandler = new RenameHandler();
		AttributeMappingType attrMapping = renameHandler.handlePropertyTransformation(retypeCell,
				geomRenameCell, context);

		assertEquals(SOURCE_GEOM, attrMapping.getSourceExpression().getOCQL());
		assertNull(attrMapping.getTargetAttributeNode());
		assertEquals(TARGET_GEOMETRY_LCU, attrMapping.getTargetAttribute());
		assertNotNull(attrMapping.getIdExpression());
		assertEquals(GML_ID_OCQL, attrMapping.getIdExpression().getOCQL());
	}

	@Test
	public void testGenericGeometryPropertyIdEncoding() {
		final String GML_ID_PATTERN = "geom.{dataset_id}";
		final String GML_ID_OCQL = "strConcat('geom.', dataset_id)";

		// create mapping context
		AppSchemaMappingContext context = new AppSchemaMappingContext(mappingWrapper);
		// create retype cell
		Cell retypeCell = getDefaultTypeCell(datasetType, landCoverDatasetType);

		// create rename cell to produce LCD's geometry
		DefaultCell geomRenameCell = new DefaultCell();
		geomRenameCell.setTransformationIdentifier(RenameFunction.ID);

		geomRenameCell.setSource(getDatasetGeomSourceProperty());
		geomRenameCell.setTarget(getLandCoverDatasetGeometryTargetProperty());

		// create formatted string cell to produce LCD geometry's gml:id
		DefaultCell geomGmlIdFormatCell = new DefaultCell();
		geomGmlIdFormatCell.setTransformationIdentifier(FormattedStringFunction.ID);

		ListMultimap<String, ParameterValue> parameters = ArrayListMultimap.create();
		parameters.put(FormattedStringFunction.PARAMETER_PATTERN,
				new ParameterValue(GML_ID_PATTERN));

		ListMultimap<String, Property> source = ArrayListMultimap.create();
		source.putAll(FormattedStringFunction.ENTITY_VARIABLE, getDatasetIdSourceProperty()
				.values());
		geomGmlIdFormatCell.setSource(source);
		geomGmlIdFormatCell.setTarget(getLandCoverDatasetGeometryGmlIdTargetProperty());
		geomGmlIdFormatCell.setTransformationParameters(parameters);

		// process cells in whatever order
		FormattedStringHandler formatHandler = new FormattedStringHandler();
		formatHandler.handlePropertyTransformation(retypeCell, geomGmlIdFormatCell, context);
		RenameHandler renameHandler = new RenameHandler();
		AttributeMappingType attrMapping = renameHandler.handlePropertyTransformation(retypeCell,
				geomRenameCell, context);

		assertEquals(SOURCE_GEOM, attrMapping.getSourceExpression().getOCQL());
		assertEquals("gml:MultiSurfaceType", attrMapping.getTargetAttributeNode());
		assertEquals(TARGET_GEOMETRY_LCD, attrMapping.getTargetAttribute());
		assertNotNull(attrMapping.getIdExpression());
		assertEquals(GML_ID_OCQL, attrMapping.getIdExpression().getOCQL());
	}

	@Test
	public void testFeatureGmlIdEncoding() {
		DefaultCell cell = new DefaultCell();
		cell.setTransformationIdentifier(RenameFunction.ID);

		cell.setSource(getUnitIdSourceProperty(unitDenormType));
		cell.setTarget(getGmlIdTargetProperty());

		RenameHandler renameHandler = new RenameHandler();
		AttributeMappingType attrMapping = renameHandler.handlePropertyTransformation(
				getDefaultTypeCell(unitDenormType, landCoverUnitType), cell,
				new AppSchemaMappingContext(mappingWrapper));
		assertNull(attrMapping.getSourceExpression());
		assertEquals(SOURCE_UNIT_ID, attrMapping.getIdExpression().getOCQL());
		assertEquals("lcv:LandCoverUnit", attrMapping.getTargetAttribute());
	}

	@Test
	public void testXmlAttributeEncodingUnqualified() {
		final String CODE_SPACE = "http://www.example.com/codespace";
		final String OCQL = "'" + CODE_SPACE + "'";

		DefaultCell cell = new DefaultCell();
		cell.setTransformationIdentifier(AssignFunction.ID);

		ListMultimap<String, ParameterValue> parameters = ArrayListMultimap.create();
		parameters.put(AssignFunction.PARAMETER_VALUE, new ParameterValue(CODE_SPACE));
		cell.setTransformationParameters(parameters);

		cell.setTarget(getCodeSpaceTargetProperty());

		AssignHandler handler = new AssignHandler();
		AttributeMappingType attrMapping = handler.handlePropertyTransformation(
				getDefaultTypeCell(unitDenormType, landCoverUnitType), cell,
				new AppSchemaMappingContext(mappingWrapper));
		assertNull(attrMapping.getSourceExpression());
		assertEquals("gml:name", attrMapping.getTargetAttribute());
		assertNotNull(attrMapping.getClientProperty());
		assertEquals(1, attrMapping.getClientProperty().size());
		ClientProperty attr = attrMapping.getClientProperty().get(0);
		assertEquals("codeSpace", attr.getName());
		assertEquals(OCQL, attr.getValue());
	}

	@Test
	public void testXmlAttributeEncodingQualified() {
		final String GML_ID = "ti.ds.landcover";
		final String OCQL = "'" + GML_ID + "'";

		DefaultCell cell = new DefaultCell();
		cell.setTransformationIdentifier(AssignFunction.ID);

		ListMultimap<String, ParameterValue> parameters = ArrayListMultimap.create();
		parameters.put(AssignFunction.PARAMETER_VALUE, new ParameterValue(GML_ID));
		cell.setTransformationParameters(parameters);

		cell.setTarget(getTimeInstantGmlIdTargetProperty());

		AssignHandler handler = new AssignHandler();
		AttributeMappingType attrMapping = handler.handlePropertyTransformation(
				getDefaultTypeCell(datasetType, landCoverDatasetType), cell,
				new AppSchemaMappingContext(mappingWrapper));
		assertNull(attrMapping.getSourceExpression());
		assertEquals(
				"lcv:extent/gmd:EX_Extent/gmd:temporalElement/gmd:EX_TemporalExtent/gmd:extent/gml:TimeInstant",
				attrMapping.getTargetAttribute());
		assertNotNull(attrMapping.getClientProperty());
		assertEquals(1, attrMapping.getClientProperty().size());
		ClientProperty attr = attrMapping.getClientProperty().get(0);
		assertEquals("gml:id", attr.getName());
		assertEquals(OCQL, attr.getValue());
	}

	@Test
	public void testEncodeIfEmptyTrue() {
		final String XLINK_HREF_CONSTANT = "#lcu.123";
		final String OCQL_LITERAL = "'" + XLINK_HREF_CONSTANT + "'";

		DefaultCell assign = new DefaultCell();
		assign.setTransformationIdentifier(AssignFunction.ID);
		ListMultimap<String, ParameterValue> parameters = ArrayListMultimap.create();
		parameters.put(AssignFunction.PARAMETER_VALUE, new ParameterValue(XLINK_HREF_CONSTANT));
		assign.setTransformationParameters(parameters);
		assign.setTarget(getNestedUnitHrefTargetProperty());

		AssignHandler assignHandler = new AssignHandler();
		AttributeMappingType attrMapping = assignHandler.handlePropertyTransformation(
				getDefaultTypeCell(datasetType, landCoverDatasetType), assign,
				new AppSchemaMappingContext(mappingWrapper));
		assertNull(attrMapping.getSourceExpression());
		assertEquals("lcv:member", attrMapping.getTargetAttribute());
		assertNotNull(attrMapping.getClientProperty());
		assertEquals(1, attrMapping.getClientProperty().size());
		assertEquals("xlink:href", attrMapping.getClientProperty().get(0).getName());
		assertEquals(OCQL_LITERAL, attrMapping.getClientProperty().get(0).getValue());
		// expression is constant, so encodeIfEmpty=true
		assertNotNull(attrMapping.isEncodeIfEmpty());
		assertTrue(attrMapping.isEncodeIfEmpty());
	}

	@Test
	public void testEncodeIfEmptyFalse() {
		final String XLINK_HREF_PATTERN = "#lcu.{dataset_id}";
		final String OCQL_STR_CONCAT = "strConcat('#lcu.', dataset_id)";

		DefaultCell format = new DefaultCell();
		format.setTransformationIdentifier(FormattedStringFunction.ID);
		ListMultimap<String, ParameterValue> formatParameters = ArrayListMultimap.create();
		formatParameters.put(FormattedStringFunction.PARAMETER_PATTERN, new ParameterValue(
				XLINK_HREF_PATTERN));
		ListMultimap<String, Property> source = ArrayListMultimap.create();
		source.putAll(FormattedStringFunction.ENTITY_VARIABLE, getDatasetIdSourceProperty()
				.values());
		format.setSource(source);
		format.setTarget(getNestedUnitHrefTargetProperty());
		format.setTransformationParameters(formatParameters);

		FormattedStringHandler formatHandler = new FormattedStringHandler();
		AttributeMappingType attrMapping = formatHandler.handlePropertyTransformation(
				getDefaultTypeCell(datasetType, landCoverDatasetType), format,
				new AppSchemaMappingContext(mappingWrapper));
		assertNull(attrMapping.getSourceExpression());
		assertEquals("lcv:member", attrMapping.getTargetAttribute());
		assertNotNull(attrMapping.getClientProperty());
		assertEquals(1, attrMapping.getClientProperty().size());
		assertEquals("xlink:href", attrMapping.getClientProperty().get(0).getName());
		assertEquals(OCQL_STR_CONCAT, attrMapping.getClientProperty().get(0).getValue());
		// expression is NOT constant, so encodeIfEmpty=null (won't be XML
		// encoded)
		assertNull(attrMapping.isEncodeIfEmpty());
	}

	@Test
	public void testNillable() {
		final String nilReason = "missing";
		final String nilReasonCQL = "'" + nilReason + "'";
		final String expectedXsiNilWithSource = "if_then_else(isNull(" + SOURCE_UNIT_ID
				+ "), 'true', Expression.NIL)";

		DefaultCell renameBeginLifespan = new DefaultCell();
		renameBeginLifespan.setTransformationIdentifier(RenameFunction.ID);
		renameBeginLifespan.setSource(getUnitIdSourceProperty(unitType));
		renameBeginLifespan.setTarget(getBeginLifespanTargetProperty());

		DefaultCell assignNilReason = new DefaultCell();
		assignNilReason.setTransformationIdentifier(AssignFunction.ID);
		ListMultimap<String, ParameterValue> parameters = ArrayListMultimap.create();
		parameters.put(AssignFunction.PARAMETER_VALUE, new ParameterValue(nilReason));
		assignNilReason.setTarget(getBeginLifespanNilReasonTargetProperty());
		assignNilReason.setTransformationParameters(parameters);

		Cell typeCell = getDefaultTypeCell(unitType, landCoverUnitType);

		AttributeMappingType attrMapping = null;
		AppSchemaMappingContext context = new AppSchemaMappingContext(mappingWrapper);

		AssignHandler assignHandler = new AssignHandler();
		attrMapping = assignHandler
				.handlePropertyTransformation(typeCell, assignNilReason, context);
		assertNotNull(attrMapping);
		assertEquals("lcv:beginLifespanVersion", attrMapping.getTargetAttribute());
		assertNull(attrMapping.getSourceExpression());
		assertEquals(2, attrMapping.getClientProperty().size());
		assertEquals(GML_NIL_REASON, attrMapping.getClientProperty().get(0).getName());
		assertEquals(nilReasonCQL, attrMapping.getClientProperty().get(0).getValue());
		assertEquals("xsi:nil", attrMapping.getClientProperty().get(1).getName());
		assertEquals("if_then_else(isNull(" + nilReasonCQL + "), Expression.NIL, 'true')",
				attrMapping.getClientProperty().get(1).getValue());
		assertTrue(attrMapping.isEncodeIfEmpty());

		// element mapping should cause xsi:nil expression being overridden
		RenameHandler renameHandler = new RenameHandler();
		attrMapping = renameHandler.handlePropertyTransformation(typeCell, renameBeginLifespan,
				context);
		assertNotNull(attrMapping);
		assertNotNull(attrMapping.getSourceExpression());
		assertEquals(SOURCE_UNIT_ID, attrMapping.getSourceExpression().getOCQL());
		assertEquals("lcv:beginLifespanVersion", attrMapping.getTargetAttribute());
		assertEquals(2, attrMapping.getClientProperty().size());
		boolean xsiNilFound = false;
		for (ClientProperty property : attrMapping.getClientProperty()) {
			if ("xsi:nil".equals(property.getName())) {
				assertEquals(expectedXsiNilWithSource, property.getValue());
				xsiNilFound = true;
			}
		}
		assertTrue(xsiNilFound);
		assertTrue(attrMapping.isEncodeIfEmpty());

		// remove nilReason client property
		attrMapping.getClientProperty().remove(0);
		// run again assing handler, nothing should change
		attrMapping = assignHandler
				.handlePropertyTransformation(typeCell, assignNilReason, context);
		assertEquals(2, attrMapping.getClientProperty().size());
		assertEquals("xsi:nil", attrMapping.getClientProperty().get(0).getName());
		assertEquals(expectedXsiNilWithSource, attrMapping.getClientProperty().get(0).getValue());
	}

	@Test
	public void testNilReasonOnNotNillableElement() {
		final String nilReason = "missing";
		final String nilReasonAssignCQL = "'" + nilReason + "'";
		final String nilReasonRenameCQL = SOURCE_UUID_V1;

		// --- test with a constant mapping --- //

		DefaultCell assignNilReason = new DefaultCell();
		assignNilReason.setTransformationIdentifier(AssignFunction.ID);
		ListMultimap<String, ParameterValue> parameters = ArrayListMultimap.create();
		parameters.put(AssignFunction.PARAMETER_VALUE, new ParameterValue(nilReason));
		assignNilReason.setTarget(getDescriptionNilReasonTargetProperty());
		assignNilReason.setTransformationParameters(parameters);

		Cell typeCell = getDefaultTypeCell(unitType, landCoverUnitType);

		AttributeMappingType attrMapping = null;
		AppSchemaMappingContext context = new AppSchemaMappingContext(mappingWrapper);

		AssignHandler assignHandler = new AssignHandler();
		attrMapping = assignHandler
				.handlePropertyTransformation(typeCell, assignNilReason, context);
		assertNotNull(attrMapping);
		assertEquals("gml:description", attrMapping.getTargetAttribute());
		assertNull(attrMapping.getSourceExpression());
		assertEquals(1, attrMapping.getClientProperty().size());
		assertEquals(GML_NIL_REASON, attrMapping.getClientProperty().get(0).getName());
		assertEquals(nilReasonAssignCQL, attrMapping.getClientProperty().get(0).getValue());
		// encodeIfEmpty=true because we are using a constant mapping
		assertTrue(attrMapping.isEncodeIfEmpty());

		// --- test with a non-constant mapping --- //

		DefaultCell rename = new DefaultCell();
		rename.setTransformationIdentifier(RenameFunction.ID);
		rename.setSource(getUuidSourceProperty(unitType));
		rename.setTarget(getDescriptionNilReasonTargetProperty());
		// reset mapping
		initMapping();
		context = new AppSchemaMappingContext(mappingWrapper);
		// process rename
		RenameHandler renameHandler = new RenameHandler();
		attrMapping = renameHandler.handlePropertyTransformation(typeCell, rename, context);
		assertNotNull(attrMapping);
		assertEquals("gml:description", attrMapping.getTargetAttribute());
		assertNull(attrMapping.getSourceExpression());
		assertEquals(1, attrMapping.getClientProperty().size());
		assertEquals(GML_NIL_REASON, attrMapping.getClientProperty().get(0).getName());
		assertEquals(nilReasonRenameCQL, attrMapping.getClientProperty().get(0).getValue());
		// encodeIfEmpty has not been set, because we are using a mapping which
		// is a function of the source value
		assertNull(attrMapping.isEncodeIfEmpty());
	}

	private ListMultimap<String, Property> getDatasetIdSourceProperty() {
		ChildDefinition<?> childDef = DefinitionUtil.getChild(datasetType, new QName(
				SOURCE_DATASET_ID));
		assertNotNull(childDef);

		return createSourceProperty(datasetType, childDef);
	}

	private ListMultimap<String, Property> getUnitDatasetIdSourceProperty(TypeDefinition unitType) {
		ChildDefinition<?> childDef = DefinitionUtil.getChild(unitType,
				new QName(SOURCE_DATASET_ID));
		assertNotNull(childDef);

		return createSourceProperty(unitType, childDef);
	}

	private ListMultimap<String, Property> getUnitIdSourceProperty(TypeDefinition unitType) {
		ChildDefinition<?> childDef = DefinitionUtil.getChild(unitType, new QName(SOURCE_UNIT_ID));
		assertNotNull(childDef);

		return createSourceProperty(unitType, childDef);
	}

	private ListMultimap<String, Property> getUuidSourceProperty(TypeDefinition unitType) {
		ChildDefinition<?> childDef = DefinitionUtil.getChild(unitType, new QName(SOURCE_UUID_V1));
		assertNotNull(childDef);

		return createSourceProperty(unitType, childDef);
	}

	private ListMultimap<String, Property> getUcs2007SourceProperty() {
		ChildDefinition<?> childDef = DefinitionUtil.getChild(unitDenormType, new QName(
				SOURCE_UCS2007));
		assertNotNull(childDef);

		return createSourceProperty(unitDenormType, childDef);
	}

	private ListMultimap<String, Property> getUcs2013SourceProperty() {
		ChildDefinition<?> childDef = DefinitionUtil.getChild(unitDenormType, new QName(
				SOURCE_UCS2013));
		assertNotNull(childDef);

		return createSourceProperty(unitDenormType, childDef);
	}

	private ListMultimap<String, Property> getUnitGeomSourceProperty(TypeDefinition unitType) {
		ChildDefinition<?> childDef = DefinitionUtil.getChild(unitType, new QName(SOURCE_GEOM));
		assertNotNull(childDef);

		return createSourceProperty(unitType, childDef);
	}

	private ListMultimap<String, Property> getDatasetGeomSourceProperty() {
		ChildDefinition<?> childDef = DefinitionUtil.getChild(datasetType, new QName(SOURCE_GEOM));
		assertNotNull(childDef);

		return createSourceProperty(datasetType, childDef);
	}

	private ListMultimap<String, Property> getObservationUnitIdSourceProperty() {
		ChildDefinition<?> childDef = DefinitionUtil.getChild(observationType, new QName(
				SOURCE_OBS_UNIT_ID));
		assertNotNull(childDef);

		return createSourceProperty(observationType, childDef);
	}

	private ListMultimap<String, Property> getObservationClassSourceProperty() {
		ChildDefinition<?> childDef = DefinitionUtil.getChild(observationType, new QName(
				SOURCE_OBS_CLASS));
		assertNotNull(childDef);

		return createSourceProperty(observationType, childDef);
	}

	private ListMultimap<String, Property> createSourceProperty(TypeDefinition sourceType,
			ChildDefinition<?> childDef) {
		List<ChildContext> childContext = new ArrayList<ChildContext>();
		childContext.add(new ChildContext(childDef));
		ListMultimap<String, Property> source = ArrayListMultimap.create();
		source.put(null, new DefaultProperty(new PropertyEntityDefinition(sourceType, childContext,
				SchemaSpaceID.SOURCE, null)));

		return source;
	}

	private ListMultimap<String, Property> getLocalIdTargetProperty() {
		ChildDefinition<?> inspireIdChildDef = DefinitionUtil.getChild(landCoverUnitType,
				new QName(LANDCOVER_NS, "inspireId"));
		assertNotNull(inspireIdChildDef);
		ChildDefinition<?> identifierChildDef = DefinitionUtil.getChild(inspireIdChildDef,
				new QName(BASE_NS, "Identifier"));
		assertNotNull(identifierChildDef);
		ChildDefinition<?> localIdChildDef = DefinitionUtil.getChild(identifierChildDef, new QName(
				BASE_NS, "localId"));
		assertNotNull(localIdChildDef);

		return createTargetProperty(Arrays.asList(inspireIdChildDef, identifierChildDef,
				localIdChildDef));
	}

	private ListMultimap<String, Property> getNestedUnitLocalIdTargetProperty() {
		ChildDefinition<?> memberChildDef = DefinitionUtil.getChild(landCoverDatasetType,
				new QName(LANDCOVER_NS, "member"));
		assertNotNull(memberChildDef);
		ChildDefinition<?> lcuChildDef = DefinitionUtil.getChild(memberChildDef, new QName(
				LANDCOVER_NS, "LandCoverUnit"));
		assertNotNull(lcuChildDef);
		ChildDefinition<?> inspireIdChildDef = DefinitionUtil.getChild(lcuChildDef, new QName(
				LANDCOVER_NS, "inspireId"));
		assertNotNull(inspireIdChildDef);
		ChildDefinition<?> identifierChildDef = DefinitionUtil.getChild(inspireIdChildDef,
				new QName(BASE_NS, "Identifier"));
		assertNotNull(identifierChildDef);
		ChildDefinition<?> localIdChildDef = DefinitionUtil.getChild(identifierChildDef, new QName(
				BASE_NS, "localId"));
		assertNotNull(localIdChildDef);

		return createNestedTargetProperty(Arrays.asList(memberChildDef, lcuChildDef,
				inspireIdChildDef, identifierChildDef, localIdChildDef));
	}

	private ListMultimap<String, Property> getNestedUnitHrefTargetProperty() {
		ChildDefinition<?> memberChildDef = DefinitionUtil.getChild(landCoverDatasetType,
				new QName(LANDCOVER_NS, "member"));
		assertNotNull(memberChildDef);
		ChildDefinition<?> hrefChildDef = DefinitionUtil.getChild(memberChildDef, new QName(
				XLINK_NS, "href"));
		assertNotNull(hrefChildDef);

		return createNestedTargetProperty(Arrays.asList(memberChildDef, hrefChildDef));
	}

	private ListMultimap<String, Property> getNestedObservationClassTargetProperty() {
		ChildDefinition<?> member = DefinitionUtil.getChild(landCoverDatasetType, new QName(
				LANDCOVER_NS, "member"));
		assertNotNull(member);
		ChildDefinition<?> lcu = DefinitionUtil.getChild(member, new QName(LANDCOVER_NS,
				"LandCoverUnit"));
		assertNotNull(lcu);
		ChildDefinition<?> observation = DefinitionUtil.getChild(lcu, new QName(LANDCOVER_NS,
				"landCoverObservation"));
		ChildDefinition<?> observationFeature = DefinitionUtil.getChild(observation, new QName(
				LANDCOVER_NS, "LandCoverObservation"));
		ChildDefinition<?> observationClass = DefinitionUtil.getChild(observationFeature,
				new QName(LANDCOVER_NS, "class"));

		return createNestedTargetProperty(Arrays.asList(member, lcu, observation,
				observationFeature, observationClass));
	}

	private ListMultimap<String, Property> getMetaDataHrefTargetProperty() {
		ChildDefinition<?> metaDataChildDef = DefinitionUtil.getChild(landCoverUnitType, new QName(
				GML_NS, "metaDataProperty"));
		assertNotNull(metaDataChildDef);
		ChildDefinition<?> hrefChildDef = DefinitionUtil.getChild(metaDataChildDef, new QName(
				XLINK_NS, "href"));
		assertNotNull(hrefChildDef);

		return createTargetProperty(Arrays.asList(metaDataChildDef, hrefChildDef));
	}

	private ListMultimap<String, Property> getFirstObservationDateTargetProperty() {
		ChildDefinition<?> lcvObsChildDef = DefinitionUtil.getChild(landCoverUnitType, new QName(
				LANDCOVER_NS, "landCoverObservation"));
		assertNotNull(lcvObsChildDef);
		ChildDefinition<?> lcvObsFeatureTypeChildDef = DefinitionUtil.getChild(lcvObsChildDef,
				new QName(LANDCOVER_NS, "LandCoverObservation"));
		assertNotNull(lcvObsFeatureTypeChildDef);
		ChildDefinition<?> obsDateChildDef = DefinitionUtil.getChild(lcvObsFeatureTypeChildDef,
				new QName(LANDCOVER_NS, "observationDate"));
		assertNotNull(obsDateChildDef);

		List<ChildDefinition<?>> childDefs = Arrays.asList(lcvObsChildDef,
				lcvObsFeatureTypeChildDef, obsDateChildDef);
		List<Integer> contextNames = Arrays.asList(0);

		return createTargetProperty(landCoverUnitType, childDefs, contextNames, null);
	}

	private ListMultimap<String, Property> getDescriptionTargetProperty() {
		ChildDefinition<?> descriptionChildDef = DefinitionUtil.getChild(landCoverUnitType,
				new QName(GML_NS, "description"));

		List<ChildDefinition<?>> childDefs = new ArrayList<ChildDefinition<?>>();
		childDefs.add(descriptionChildDef);

		return createTargetProperty(childDefs);
	}

	private ListMultimap<String, Property> getDescriptionNilReasonTargetProperty() {
		ChildDefinition<?> description = DefinitionUtil.getChild(landCoverUnitType, new QName(
				GML_NS, "description"));
		assertNotNull(description);
		ChildDefinition<?> nilReason = DefinitionUtil.getChild(description, new QName(
				GML_NIL_REASON));
		assertNotNull(nilReason);

		List<ChildDefinition<?>> childDefs = new ArrayList<ChildDefinition<?>>();
		childDefs.add(description);
		childDefs.add(nilReason);

		return createTargetProperty(childDefs);
	}

	private ListMultimap<String, Property> getBeginLifespanTargetProperty() {
		ChildDefinition<?> beginLifeSpan = DefinitionUtil.getChild(landCoverUnitType, new QName(
				LANDCOVER_NS, "beginLifespanVersion"));
		assertNotNull(beginLifeSpan);

		List<ChildDefinition<?>> childDefs = new ArrayList<ChildDefinition<?>>();
		childDefs.add(beginLifeSpan);

		return createTargetProperty(childDefs);
	}

	private ListMultimap<String, Property> getBeginLifespanNilReasonTargetProperty() {
		ChildDefinition<?> beginLifeSpan = DefinitionUtil.getChild(landCoverUnitType, new QName(
				LANDCOVER_NS, "beginLifespanVersion"));
		assertNotNull(beginLifeSpan);
		ChildDefinition<?> nilReason = DefinitionUtil.getChild(beginLifeSpan, new QName(
				GML_NIL_REASON));
		assertNotNull(nilReason);

		List<ChildDefinition<?>> childDefs = new ArrayList<ChildDefinition<?>>();
		childDefs.add(beginLifeSpan);
		childDefs.add(nilReason);

		return createTargetProperty(childDefs);
	}

	private ListMultimap<String, Property> getLandCoverUnitGeometryTargetProperty() {
		List<ChildDefinition<?>> childDefs = getLandCoverUnitGeometryPath();

		return createTargetProperty(childDefs);
	}

	private ListMultimap<String, Property> getLandCoverUnitGeometryGmlIdTargetProperty() {
		List<ChildDefinition<?>> childDefs = getLandCoverUnitGeometryPath();
		ChildDefinition<?> geometry = childDefs.get(childDefs.size() - 1);
		ChildDefinition<?> abstractGeometry = DefinitionUtil.getChild(geometry, new QName(
				"http://www.opengis.net/gml/3.2/AbstractGeometry", "choice"));
		assertNotNull(abstractGeometry);
		ChildDefinition<?> multiSurface = DefinitionUtil.getChild(abstractGeometry, new QName(
				GML_NS, "MultiSurface"));
		assertNotNull(multiSurface);
		ChildDefinition<?> gmlId = DefinitionUtil.getChild(multiSurface, new QName(GML_NS, "id"));
		assertNotNull(gmlId);

		childDefs.add(abstractGeometry);
		childDefs.add(multiSurface);
		childDefs.add(gmlId);

		return createTargetProperty(childDefs);
	}

	private List<ChildDefinition<?>> getLandCoverUnitGeometryPath() {
		ChildDefinition<?> geometry = DefinitionUtil.getChild(landCoverUnitType, new QName(
				LANDCOVER_NS, "geometry"));
		assertNotNull(geometry);

		List<ChildDefinition<?>> childDefs = new ArrayList<ChildDefinition<?>>();
		childDefs.add(geometry);

		return childDefs;
	}

	private ListMultimap<String, Property> getLandCoverDatasetGeometryTargetProperty() {
		List<ChildDefinition<?>> childDefs = getLandCoverDatasetGeometryPath();

		return createTargetProperty(childDefs);
	}

	private ListMultimap<String, Property> getLandCoverDatasetGeometryGmlIdTargetProperty() {
		List<ChildDefinition<?>> childDefs = getLandCoverDatasetGeometryPath();
		ChildDefinition<?> gmlId = DefinitionUtil.getChild(childDefs.get(childDefs.size() - 1),
				new QName(GML_NS, "id"));

		childDefs.add(gmlId);

		return createTargetProperty(childDefs);
	}

	private List<ChildDefinition<?>> getLandCoverDatasetGeometryPath() {
		ChildDefinition<?> extent = DefinitionUtil.getChild(landCoverDatasetType, new QName(
				LANDCOVER_NS, "extent"));
		assertNotNull(extent);
		ChildDefinition<?> exExtent = DefinitionUtil.getChild(extent,
				new QName(GMD_NS, "EX_Extent"));
		assertNotNull(exExtent);
		ChildDefinition<?> geographicElement = DefinitionUtil.getChild(exExtent, new QName(GMD_NS,
				"geographicElement"));
		assertNotNull(geographicElement);
		ChildDefinition<?> exGeographicExtentChoice = DefinitionUtil
				.getChild(geographicElement, new QName(
						"http://www.isotc211.org/2005/gmd/AbstractEX_GeographicExtent", "choice"));
		assertNotNull(exGeographicExtentChoice);
		ChildDefinition<?> exBoundingPolygon = DefinitionUtil.getChild(exGeographicExtentChoice,
				new QName(GMD_NS, "EX_BoundingPolygon"));
		assertNotNull(exBoundingPolygon);
		ChildDefinition<?> polygon = DefinitionUtil.getChild(exBoundingPolygon, new QName(GMD_NS,
				"polygon"));
		assertNotNull(polygon);
		ChildDefinition<?> abstractGeometry = DefinitionUtil.getChild(polygon, new QName(
				"http://www.opengis.net/gml/3.2/AbstractGeometry", "choice"));
		assertNotNull(abstractGeometry);
		ChildDefinition<?> multiSurface = DefinitionUtil.getChild(abstractGeometry, new QName(
				GML_NS, "MultiSurface"));
		assertNotNull(multiSurface);

		List<ChildDefinition<?>> childDefs = new ArrayList<ChildDefinition<?>>();
		childDefs.add(extent);
		childDefs.add(exExtent);
		childDefs.add(geographicElement);
		childDefs.add(exGeographicExtentChoice);
		childDefs.add(exBoundingPolygon);
		childDefs.add(polygon);
		childDefs.add(abstractGeometry);
		childDefs.add(multiSurface);

		return childDefs;
	}

	private ListMultimap<String, Property> getGmlIdTargetProperty() {
		ChildDefinition<?> gmlIdChildDef = DefinitionUtil.getChild(landCoverUnitType, new QName(
				GML_NS, "id"));

		List<ChildDefinition<?>> childDefs = new ArrayList<ChildDefinition<?>>();
		childDefs.add(gmlIdChildDef);

		return createTargetProperty(childDefs);
	}

	private ListMultimap<String, Property> getCodeSpaceTargetProperty() {
		ChildDefinition<?> nameChildDef = DefinitionUtil.getChild(landCoverDatasetType, new QName(
				GML_NS, "name"));
		ChildDefinition<?> codeSpaceChildDef = DefinitionUtil.getChild(nameChildDef, new QName(
				"codeSpace"));

		List<ChildDefinition<?>> childDefs = new ArrayList<ChildDefinition<?>>();
		childDefs.add(nameChildDef);
		childDefs.add(codeSpaceChildDef);

		return createTargetProperty(childDefs);
	}

	private ListMultimap<String, Property> getTimeInstantGmlIdTargetProperty() {
		ChildDefinition<?> extent = DefinitionUtil.getChild(landCoverDatasetType, new QName(
				LANDCOVER_NS, "extent"));
		assertNotNull(extent);
		ChildDefinition<?> exExtent = DefinitionUtil.getChild(extent,
				new QName(GMD_NS, "EX_Extent"));
		assertNotNull(exExtent);
		ChildDefinition<?> temporalElement = DefinitionUtil.getChild(exExtent, new QName(GMD_NS,
				"temporalElement"));
		assertNotNull(temporalElement);
		ChildDefinition<?> exTemporalExtentChoice = DefinitionUtil.getChild(temporalElement,
				new QName("http://www.isotc211.org/2005/gmd/EX_TemporalExtent", "choice"));
		assertNotNull(exTemporalExtentChoice);
		ChildDefinition<?> exTemporalExtent = DefinitionUtil.getChild(exTemporalExtentChoice,
				new QName(GMD_NS, "EX_TemporalExtent"));
		assertNotNull(exTemporalExtent);
		ChildDefinition<?> temporalExtent = DefinitionUtil.getChild(exTemporalExtent, new QName(
				GMD_NS, "extent"));
		assertNotNull(temporalExtent);
		ChildDefinition<?> abstractTimePrimitive = DefinitionUtil.getChild(temporalExtent,
				new QName("http://www.opengis.net/gml/3.2/AbstractTimePrimitive", "choice"));
		assertNotNull(abstractTimePrimitive);
		ChildDefinition<?> timeInstant = DefinitionUtil.getChild(abstractTimePrimitive, new QName(
				GML_NS, "TimeInstant"));
		assertNotNull(timeInstant);
		ChildDefinition<?> timeInstantGmlId = DefinitionUtil.getChild(timeInstant, new QName(
				GML_NS, "id"));
		assertNotNull(timeInstantGmlId);

		List<ChildDefinition<?>> childDefs = new ArrayList<ChildDefinition<?>>();
		childDefs.add(extent);
		childDefs.add(exExtent);
		childDefs.add(temporalElement);
		childDefs.add(exTemporalExtentChoice);
		childDefs.add(exTemporalExtent);
		childDefs.add(temporalExtent);
		childDefs.add(abstractTimePrimitive);
		childDefs.add(timeInstant);
		childDefs.add(timeInstantGmlId);

		return createTargetProperty(childDefs);
	}

	private ListMultimap<String, Property> createTargetProperty(List<ChildDefinition<?>> childDefs) {
		return createTargetProperty(landCoverUnitType, childDefs, null, null);
	}

	private ListMultimap<String, Property> createNestedTargetProperty(
			List<ChildDefinition<?>> childDefs) {
		return createTargetProperty(landCoverDatasetType, childDefs, null, null);
	}

	private ListMultimap<String, Property> createTargetProperty(TypeDefinition targetType,
			List<ChildDefinition<?>> childDefs, List<Integer> contextNames,
			List<Condition> conditions) {
		if (contextNames == null) {
			contextNames = Collections.emptyList();
		}
		if (conditions == null) {
			conditions = Collections.emptyList();
		}

		List<ChildContext> childContext = new ArrayList<ChildContext>();
		for (int i = 0; i < childDefs.size(); i++) {
			ChildDefinition<?> childDef = childDefs.get(i);
			Integer contextName = (contextNames.size() > i) ? contextNames.get(i) : null;
			Condition condition = (conditions.size() > 1) ? conditions.get(i) : null;
			childContext.add(new ChildContext(contextName, null, condition, childDef));
		}
		ListMultimap<String, Property> target = ArrayListMultimap.create();
		target.put(null, new DefaultProperty(new PropertyEntityDefinition(targetType, childContext,
				SchemaSpaceID.TARGET, null)));

		return target;
	}

	private Cell getDefaultTypeCell(TypeDefinition sourceType, TypeDefinition targetType) {
		DefaultCell typeCell = new DefaultCell();

		typeCell.setTransformationIdentifier(RetypeFunction.ID);
		ListMultimap<String, Type> source = ArrayListMultimap.create();
		source.put(null, new DefaultType(new TypeEntityDefinition(sourceType, SchemaSpaceID.SOURCE,
				null)));
		ListMultimap<String, Type> target = ArrayListMultimap.create();
		target.put(null, new DefaultType(new TypeEntityDefinition(targetType, SchemaSpaceID.TARGET,
				null)));
		typeCell.setSource(source);
		typeCell.setTarget(target);

		return typeCell;
	}

	@SuppressWarnings("unused")
	private void logMapping(AppSchemaDataAccessType mapping) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			AppSchemaMappingGenerator.writeMapping(bos, mapping);
			log.info(new String(bos.toByteArray()));
		} catch (JAXBException e) {
			log.debug("Could not serialize mapping", e);
		}
	}
}
