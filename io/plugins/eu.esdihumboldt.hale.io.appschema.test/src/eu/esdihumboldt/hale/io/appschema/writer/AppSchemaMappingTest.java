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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.cst.functions.core.ClassificationMapping;
import eu.esdihumboldt.cst.functions.numeric.MathematicalExpressionFunction;
import eu.esdihumboldt.cst.functions.string.DateExtractionFunction;
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
import eu.esdihumboldt.hale.io.appschema.writer.internal.AppSchemaMappingWrapper;
import eu.esdihumboldt.hale.io.appschema.writer.internal.AssignHandler;
import eu.esdihumboldt.hale.io.appschema.writer.internal.ClassificationHandler;
import eu.esdihumboldt.hale.io.appschema.writer.internal.DateExtractionHandler;
import eu.esdihumboldt.hale.io.appschema.writer.internal.FormattedStringHandler;
import eu.esdihumboldt.hale.io.appschema.writer.internal.JoinHandler;
import eu.esdihumboldt.hale.io.appschema.writer.internal.MathematicalExpressionHandler;
import eu.esdihumboldt.hale.io.appschema.writer.internal.RenameHandler;
import eu.esdihumboldt.hale.io.appschema.writer.internal.RetypeHandler;
import eu.esdihumboldt.hale.io.xsd.reader.XmlSchemaReader;

@SuppressWarnings("javadoc")
public class AppSchemaMappingTest {

	private static final String SOURCE_PATH = "/data/source.hsd";
	private static final String TARGET_PATH = "/data/LandCoverVector.xsd";

	private static final String SOURCE_NS = "jdbc:postgresql:lamma:public";
	private static final String LANDCOVER_NS = "http://inspire.ec.europa.eu/schemas/lcv/3.0";
	private static final String LANDCOVER_PREFIX = "lcv";
	private static final String BASE_NS = "http://inspire.ec.europa.eu/schemas/base/3.3";
	private static final String BASE_PREFIX = "base";
	private static final String GML_NS = "http://www.opengis.net/gml/3.2";
	private static final String GML_PREFIX = "gml";
	private static final String XLINK_NS = "http://www.w3.org/1999/xlink";
	private static final String XLINK_PREFIX = "xlink";

	private static final String SOURCE_DATASET_ID = "dataset_id";
	private static final String SOURCE_UNIT_ID = "unit_id";
	private static final String SOURCE_UUID_V1 = "uuid_v1";
	private static final String SOURCE_UCS2007 = "ucs2007";
	private static final String SOURCE_UCS2013 = "ucs2013";
	private static final String SOURCE_GEOM = "geom";
	private static final String TARGET_LOCAL_ID = "lcv:inspireId/base:Identifier/base:localId";
	private static final String TARGET_FIRST_OBSERVATION_DATE = "lcv:landCoverObservation[1]/lcv:LandCoverObservation/lcv:observationDate";
	private static final String TARGET_DESCRIPTION = "gml:description";
	private static final String TARGET_GEOMETRY = "lcv:geometry/gml:AbstractGeometry";
	private static final String TARGET_METADATA = "gml:metaDataProperty";

	private static Schema source;
	private static Schema target;
	private static TypeDefinition datasetType;
	private static TypeDefinition landCoverType;
	private static TypeDefinition landCoverUnitType;
	private static TypeDefinition landCoverDatasetType;

	private AppSchemaMappingWrapper mappingWrapper;

	@BeforeClass
	public static void init() throws Exception {

		TestUtil.startConversionService();

		source = loadSchema(new HaleSchemaReader(), SOURCE_PATH);
		assertNotNull(source);
		target = loadSchema(new XmlSchemaReader(), TARGET_PATH);
		assertNotNull(target);

		datasetType = source.getType(new QName(SOURCE_NS, "dataset_norm"));
		assertNotNull(datasetType);
		landCoverType = source.getType(new QName(SOURCE_NS, "landcover_norm"));
		assertNotNull(landCoverType);
		landCoverUnitType = target.getType(new QName(LANDCOVER_NS, "LandCoverUnitType"));
		assertNotNull(landCoverUnitType);
		landCoverDatasetType = target.getType(new QName(LANDCOVER_NS, "LandCoverDatasetType"));
		assertNotNull(landCoverDatasetType);

	}

	@Before
	public void initMapping() {
		mappingWrapper = new AppSchemaMappingWrapper(new AppSchemaDataAccessType());
		// initialize namespaces
		mappingWrapper.getOrCreateNamespace(LANDCOVER_NS, LANDCOVER_PREFIX);
		mappingWrapper.getOrCreateNamespace(BASE_NS, BASE_PREFIX);
		mappingWrapper.getOrCreateNamespace(GML_NS, GML_PREFIX);
		mappingWrapper.getOrCreateNamespace(XLINK_NS, XLINK_PREFIX);
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
		source.put(null, new DefaultType(new TypeEntityDefinition(landCoverType,
				SchemaSpaceID.SOURCE, null)));
		ListMultimap<String, Type> target = ArrayListMultimap.create();
		target.put(null, new DefaultType(new TypeEntityDefinition(landCoverUnitType,
				SchemaSpaceID.TARGET, null)));
		cell.setSource(source);
		cell.setTarget(target);

		RetypeHandler handler = new RetypeHandler();
		FeatureTypeMapping ftMapping = handler.handleTypeTransformation(null, cell, mappingWrapper);
		assertEquals("landcover_norm", ftMapping.getSourceType());
		assertEquals("lcv:LandCoverUnit", ftMapping.getTargetElement());
	}

	@Test
	public void testNestedJoinHandler() {
		DefaultCell joinCell = buildJoinCell();

		// create minimal alignment and pass it to JoinHandler
		DefaultCell renameCell = new DefaultCell();
		renameCell.setTransformationIdentifier(RenameFunction.ID);
		renameCell.setSource(getUuidSourceProperty());
		renameCell.setTarget(getNestedLocalIdTargetProperty());

		DefaultAlignment alignment = new DefaultAlignment();
		alignment.addCell(joinCell);
		alignment.addCell(renameCell);

		JoinHandler handler = new JoinHandler();
		handler.handleTypeTransformation(alignment, joinCell, mappingWrapper);

		List<FeatureTypeMapping> ftMappings = mappingWrapper.getAppSchemaMapping()
				.getTypeMappings().getFeatureTypeMapping();
		assertEquals(2, ftMappings.size());

		FeatureTypeMapping lcdMapping = null, lcuMapping = null;
		for (FeatureTypeMapping ftMapping : ftMappings) {
			if ("dataset_norm".equals(ftMapping.getSourceType())
					&& "lcv:LandCoverDataset".equals(ftMapping.getTargetElement())) {
				lcdMapping = ftMapping;
			}
			if ("landcover_norm".equals(ftMapping.getSourceType())
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
		assertEquals("FEATURE_LINK", containerMapping.getSourceExpression().getLinkField());
		assertEquals(SOURCE_DATASET_ID, containerMapping.getSourceExpression().getOCQL());
		assertTrue(containerMapping.isIsMultiple());

		AttributeMappingType nestedMapping = lcuAttrMappings.get(0);
		assertEquals("FEATURE_LINK", nestedMapping.getTargetAttribute());
		assertEquals(SOURCE_DATASET_ID, nestedMapping.getSourceExpression().getOCQL());
		assertNull(nestedMapping.getSourceExpression().getLinkElement());
		assertNull(nestedMapping.getSourceExpression().getLinkField());
	}

	@Test
	public void testXrefJoinHandler() {
		DefaultCell joinCell = buildJoinCell();

		// create minimal alignment and pass it to JoinHandler
		DefaultCell renameCell = new DefaultCell();
		renameCell.setTransformationIdentifier(RenameFunction.ID);
		renameCell.setSource(getUnitIdSourceProperty());
		renameCell.setTarget(getNestedHrefTargetProperty());

		DefaultAlignment alignment = new DefaultAlignment();
		alignment.addCell(joinCell);
		alignment.addCell(renameCell);

		JoinHandler handler = new JoinHandler();
		handler.handleTypeTransformation(alignment, joinCell, mappingWrapper);
		RenameHandler rename = new RenameHandler();
		rename.handlePropertyTransformation(joinCell, renameCell, mappingWrapper);

		List<FeatureTypeMapping> ftMappings = mappingWrapper.getAppSchemaMapping()
				.getTypeMappings().getFeatureTypeMapping();
		assertEquals(2, ftMappings.size());

		FeatureTypeMapping lcdMapping = null, lcuMapping = null;
		for (FeatureTypeMapping ftMapping : ftMappings) {
			if ("dataset_norm".equals(ftMapping.getSourceType())
					&& "lcv:LandCoverDataset".equals(ftMapping.getTargetElement())) {
				lcdMapping = ftMapping;
			}
			if ("landcover_norm".equals(ftMapping.getSourceType())
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
		assertEquals("FEATURE_LINK", containerMapping.getSourceExpression().getLinkField());
		assertEquals(SOURCE_DATASET_ID, containerMapping.getSourceExpression().getOCQL());
		assertTrue(containerMapping.isIsMultiple());
		assertNotNull(containerMapping.getClientProperty());
		assertEquals(1, containerMapping.getClientProperty().size());
		assertEquals("xlink:href", containerMapping.getClientProperty().get(0).getName());
		assertEquals(SOURCE_UNIT_ID, containerMapping.getClientProperty().get(0).getValue());

		AttributeMappingType nestedMapping = lcuAttrMappings.get(0);
		assertEquals("FEATURE_LINK", nestedMapping.getTargetAttribute());
		assertEquals(SOURCE_DATASET_ID, nestedMapping.getSourceExpression().getOCQL());
		assertNull(nestedMapping.getSourceExpression().getLinkElement());
		assertNull(nestedMapping.getSourceExpression().getLinkField());
	}

	private DefaultCell buildJoinCell() {
		DefaultCell joinCell = new DefaultCell();
		joinCell.setTransformationIdentifier(JoinFunction.ID);

		TypeEntityDefinition datasetEntityDef = new TypeEntityDefinition(datasetType,
				SchemaSpaceID.SOURCE, null);
		TypeEntityDefinition landcoverEntityDef = new TypeEntityDefinition(landCoverType,
				SchemaSpaceID.SOURCE, null);
		ListMultimap<String, Type> source = ArrayListMultimap.create();
		source.put(JoinFunction.JOIN_TYPES, new DefaultType(datasetEntityDef));
		source.put(JoinFunction.JOIN_TYPES, new DefaultType(landcoverEntityDef));
		assertEquals(2, source.get(JoinFunction.JOIN_TYPES).size());

		ListMultimap<String, Type> target = ArrayListMultimap.create();
		target.put(null, new DefaultType(new TypeEntityDefinition(landCoverDatasetType,
				SchemaSpaceID.TARGET, null)));

		PropertyEntityDefinition baseProperty = getDatasetIdSourceProperty().values().iterator()
				.next().getDefinition();
		PropertyEntityDefinition joinProperty = getLandCoverIdSourceProperty().values().iterator()
				.next().getDefinition();
		JoinCondition joinClause = new JoinCondition(baseProperty, joinProperty);
		JoinParameter joinParam = new JoinParameter(Arrays.asList(datasetEntityDef,
				landcoverEntityDef), Collections.singleton(joinClause));

		ListMultimap<String, ParameterValue> parameters = ArrayListMultimap.create();
		parameters
				.put(JoinFunction.PARAMETER_JOIN, new ParameterValue(new ComplexValue(joinParam)));

		joinCell.setSource(source);
		joinCell.setTarget(target);
		joinCell.setTransformationParameters(parameters);

		return joinCell;
	}

	@Test
	public void testRenameHandler() {
		DefaultCell cell = new DefaultCell();
		cell.setTransformationIdentifier(RenameFunction.ID);

		cell.setSource(getUuidSourceProperty());
		cell.setTarget(getLocalIdTargetProperty());

		RenameHandler renameHandler = new RenameHandler();
		AttributeMappingType attrMapping = renameHandler.handlePropertyTransformation(
				getDefaultTypeCell(landCoverType, landCoverUnitType), cell, mappingWrapper);
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

		cell.setSource(getUuidSourceProperty());
		cell.setTarget(getFirstObservationDateTargetProperty());
		cell.setTransformationParameters(parameters);

		DateExtractionHandler handler = new DateExtractionHandler();
		AttributeMappingType attrMapping = handler.handlePropertyTransformation(
				getDefaultTypeCell(landCoverType, landCoverUnitType), cell, mappingWrapper);
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
				getDefaultTypeCell(landCoverType, landCoverUnitType), cell, mappingWrapper);
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
		source.putAll(MathematicalExpressionFunction.ENTITY_VARIABLE, getUnitIdSourceProperty()
				.values());

		cell.setSource(source);
		cell.setTarget(getLocalIdTargetProperty());
		cell.setTransformationParameters(parameters);

		MathematicalExpressionHandler handler = new MathematicalExpressionHandler();
		AttributeMappingType attrMapping = handler.handlePropertyTransformation(
				getDefaultTypeCell(landCoverType, landCoverUnitType), cell, mappingWrapper);
		assertEquals(OCQL, attrMapping.getSourceExpression().getOCQL());
		assertEquals(TARGET_LOCAL_ID, attrMapping.getTargetAttribute());
	}

	@Test
	public void testAssignHandler() {
		final String ASSIGN_VALUE = "LCU_1234";
		final String OCQL = "'" + ASSIGN_VALUE + "'";
		final String OCQL_BOUND = "if_then_else(isNull(" + SOURCE_UUID_V1 + "), Expression.NIL, '"
				+ ASSIGN_VALUE + "')";

		Cell typeCell = getDefaultTypeCell(landCoverType, landCoverUnitType);

		DefaultCell cell = new DefaultCell();
		cell.setTransformationIdentifier(AssignFunction.ID);

		ListMultimap<String, ParameterValue> parameters = ArrayListMultimap.create();
		parameters.put(AssignFunction.PARAMETER_VALUE, new ParameterValue(ASSIGN_VALUE));

		cell.setTarget(getLocalIdTargetProperty());
		cell.setTransformationParameters(parameters);

		AssignHandler assignHandler = new AssignHandler();
		AttributeMappingType attrMapping = assignHandler.handlePropertyTransformation(typeCell,
				cell, mappingWrapper);
		assertEquals(OCQL, attrMapping.getSourceExpression().getOCQL());
		assertEquals(TARGET_LOCAL_ID, attrMapping.getTargetAttribute());

		// bound version of "Assign"
		DefaultCell cellCopy = new DefaultCell(cell);
		Collection<Property> anchor = getUuidSourceProperty().values();
		ListMultimap<String, Property> source = ArrayListMultimap.create();
		source.putAll(AssignFunction.ENTITY_ANCHOR, anchor);
		cellCopy.setSource(source);
		cellCopy.setTransformationIdentifier(AssignFunction.ID_BOUND);

		attrMapping = assignHandler
				.handlePropertyTransformation(typeCell, cellCopy, mappingWrapper);
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

		Cell typeCell = getDefaultTypeCell(landCoverType, landCoverUnitType);

		DefaultCell cell = new DefaultCell();
		cell.setTransformationIdentifier(ClassificationMappingFunction.ID);

		cell.setSource(getUnitIdSourceProperty());
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
				typeCell, cell, mappingWrapper);
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
				mappingWrapper);
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
				mappingWrapper);
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

		cell.setSource(getGeomSourceProperty());
		cell.setTarget(getGeometryTargetProperty());

		RenameHandler renameHandler = new RenameHandler();
		AttributeMappingType attrMapping = renameHandler.handlePropertyTransformation(
				getDefaultTypeCell(landCoverType, landCoverUnitType), cell, mappingWrapper);
		assertEquals(SOURCE_GEOM, attrMapping.getSourceExpression().getOCQL());
		assertEquals(TARGET_GEOMETRY, attrMapping.getTargetAttribute());
		assertEquals("gml:MultiSurfaceType", attrMapping.getTargetAttributeNode());
	}

	@Test
	public void testGmlIdEncoding() {
		DefaultCell cell = new DefaultCell();
		cell.setTransformationIdentifier(RenameFunction.ID);

		cell.setSource(getUnitIdSourceProperty());
		cell.setTarget(getGmlIdTargetProperty());

		RenameHandler renameHandler = new RenameHandler();
		AttributeMappingType attrMapping = renameHandler.handlePropertyTransformation(
				getDefaultTypeCell(landCoverType, landCoverUnitType), cell, mappingWrapper);
		assertNull(attrMapping.getSourceExpression());
		assertEquals(SOURCE_UNIT_ID, attrMapping.getIdExpression().getOCQL());
		assertEquals("lcv:LandCoverUnit", attrMapping.getTargetAttribute());
	}

	@Test
	public void testXmlAttributeEncoding() {
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
				getDefaultTypeCell(landCoverType, landCoverUnitType), cell, mappingWrapper);
		assertNull(attrMapping.getSourceExpression());
		assertEquals("gml:name", attrMapping.getTargetAttribute());
		assertNotNull(attrMapping.getClientProperty());
		assertEquals(1, attrMapping.getClientProperty().size());
		ClientProperty attr = attrMapping.getClientProperty().get(0);
		assertEquals("codeSpace", attr.getName());
		assertEquals(OCQL, attr.getValue());
	}

	private ListMultimap<String, Property> getDatasetIdSourceProperty() {
		ChildDefinition<?> childDef = DefinitionUtil.getChild(datasetType, new QName(
				SOURCE_DATASET_ID));
		assertNotNull(childDef);

		return createSourceProperty(datasetType, childDef);
	}

	private ListMultimap<String, Property> getLandCoverIdSourceProperty() {
		ChildDefinition<?> childDef = DefinitionUtil.getChild(landCoverType, new QName(
				SOURCE_DATASET_ID));
		assertNotNull(childDef);

		return createSourceProperty(landCoverType, childDef);
	}

	private ListMultimap<String, Property> getUnitIdSourceProperty() {
		ChildDefinition<?> childDef = DefinitionUtil.getChild(landCoverType, new QName(
				SOURCE_UNIT_ID));
		assertNotNull(childDef);

		return createSourceProperty(landCoverType, childDef);
	}

	private ListMultimap<String, Property> getUuidSourceProperty() {
		ChildDefinition<?> childDef = DefinitionUtil.getChild(landCoverType, new QName(
				SOURCE_UUID_V1));
		assertNotNull(childDef);

		return createSourceProperty(landCoverType, childDef);
	}

	private ListMultimap<String, Property> getUcs2007SourceProperty() {
		ChildDefinition<?> childDef = DefinitionUtil.getChild(landCoverType, new QName(
				SOURCE_UCS2007));
		assertNotNull(childDef);

		return createSourceProperty(landCoverType, childDef);
	}

	private ListMultimap<String, Property> getUcs2013SourceProperty() {
		ChildDefinition<?> childDef = DefinitionUtil.getChild(landCoverType, new QName(
				SOURCE_UCS2013));
		assertNotNull(childDef);

		return createSourceProperty(landCoverType, childDef);
	}

	private ListMultimap<String, Property> getGeomSourceProperty() {
		ChildDefinition<?> childDef = DefinitionUtil
				.getChild(landCoverType, new QName(SOURCE_GEOM));
		assertNotNull(childDef);

		return createSourceProperty(landCoverType, childDef);
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

	private ListMultimap<String, Property> getNestedLocalIdTargetProperty() {
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

	private ListMultimap<String, Property> getNestedHrefTargetProperty() {
		ChildDefinition<?> memberChildDef = DefinitionUtil.getChild(landCoverDatasetType,
				new QName(LANDCOVER_NS, "member"));
		assertNotNull(memberChildDef);
		ChildDefinition<?> hrefChildDef = DefinitionUtil.getChild(memberChildDef, new QName(
				XLINK_NS, "href"));
		assertNotNull(hrefChildDef);

		return createNestedTargetProperty(Arrays.asList(memberChildDef, hrefChildDef));
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

	private ListMultimap<String, Property> getGeometryTargetProperty() {
		ChildDefinition<?> geometryChildDef = DefinitionUtil.getChild(landCoverUnitType, new QName(
				LANDCOVER_NS, "geometry"));
		ChildDefinition<?> abstractGeometryChildDef = DefinitionUtil.getChild(geometryChildDef,
				new QName("http://www.opengis.net/gml/3.2/AbstractGeometry", "choice"));
		ChildDefinition<?> multiSurfaceChildDef = DefinitionUtil.getChild(abstractGeometryChildDef,
				new QName(GML_NS, "MultiSurface"));

		List<ChildDefinition<?>> childDefs = new ArrayList<ChildDefinition<?>>();
		childDefs.add(geometryChildDef);
		childDefs.add(abstractGeometryChildDef);
		childDefs.add(multiSurfaceChildDef);

		return createTargetProperty(childDefs);
	}

	private ListMultimap<String, Property> getGmlIdTargetProperty() {
		ChildDefinition<?> gmlIdChildDef = DefinitionUtil.getChild(landCoverUnitType, new QName(
				GML_NS, "id"));

		List<ChildDefinition<?>> childDefs = new ArrayList<ChildDefinition<?>>();
		childDefs.add(gmlIdChildDef);

		return createTargetProperty(childDefs);
	}

	private ListMultimap<String, Property> getCodeSpaceTargetProperty() {
		ChildDefinition<?> nameChildDef = DefinitionUtil.getChild(landCoverUnitType, new QName(
				GML_NS, "name"));
		ChildDefinition<?> codeSpaceChildDef = DefinitionUtil.getChild(nameChildDef, new QName(
				"codeSpace"));

		List<ChildDefinition<?>> childDefs = new ArrayList<ChildDefinition<?>>();
		childDefs.add(nameChildDef);
		childDefs.add(codeSpaceChildDef);

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
}
