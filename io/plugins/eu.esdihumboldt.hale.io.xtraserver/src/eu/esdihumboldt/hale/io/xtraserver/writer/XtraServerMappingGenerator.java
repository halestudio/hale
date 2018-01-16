/*
 * Copyright (c) 2017 interactive instruments GmbH
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
 *     interactive instruments GmbH <http://www.interactive-instruments.de>
 */

package eu.esdihumboldt.hale.io.xtraserver.writer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ws.commons.schema.XmlSchemaAppInfo;
import org.w3c.dom.Node;

import de.interactive_instruments.xtraserver.config.util.api.AssociationTarget;
import de.interactive_instruments.xtraserver.config.util.api.FeatureTypeMapping;
import de.interactive_instruments.xtraserver.config.util.api.MappingJoin;
import de.interactive_instruments.xtraserver.config.util.api.MappingValue;
import de.interactive_instruments.xtraserver.config.util.api.XtraServerMapping;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Property;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Reference;
import eu.esdihumboldt.hale.io.appschema.writer.AppSchemaMappingUtils;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlAppInfo;
import eu.esdihumboldt.hale.io.xtraserver.writer.handler.CellParentWrapper;
import eu.esdihumboldt.hale.io.xtraserver.writer.handler.MappingContext;
import eu.esdihumboldt.hale.io.xtraserver.writer.handler.PropertyTransformationHandler;
import eu.esdihumboldt.hale.io.xtraserver.writer.handler.PropertyTransformationHandlerFactory;
import eu.esdihumboldt.hale.io.xtraserver.writer.handler.TypeTransformationHandler;
import eu.esdihumboldt.hale.io.xtraserver.writer.handler.TypeTransformationHandlerFactory;
import eu.esdihumboldt.hale.io.xtraserver.writer.handler.UnsupportedTransformationException;

/**
 * Translates an Alignment to a XtraServer Mapping.
 * 
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public class XtraServerMappingGenerator {

	private final Alignment alignment;
	private final TypeTransformationHandlerFactory typeHandlerFactory;
	private final PropertyTransformationHandlerFactory propertyHandlerFactory;
	private final ProgressIndicator progress;
	private final List<String> missingAssociationTargets = new ArrayList<String>();
	private final MappingContext mappingContext;

	/**
	 * Constructor
	 * 
	 * @param alignment the Alignment with all cells
	 * @param targetSchemaSpace the target schema
	 * @param progress Progress indicator
	 * @throws IOException if the
	 */
	public XtraServerMappingGenerator(final Alignment alignment,
			final SchemaSpace targetSchemaSpace, final ProgressIndicator progress)
			throws IOException {
		this.alignment = alignment;
		mappingContext = new MappingContext(alignment, targetSchemaSpace);
		this.typeHandlerFactory = TypeTransformationHandler.createFactory(mappingContext);
		this.propertyHandlerFactory = PropertyTransformationHandler.createFactory(mappingContext);
		// Calculate the total work units for the progress indicator (+1 for writing the
		// file)
		int c = 1;
		for (final Cell typeCell : this.alignment.getActiveTypeCells()) {
			c += this.alignment.getPropertyCells(typeCell).size() + 1;
		}
		progress.begin("Translating hale alignment to XtraServer Mapping file", c);
		this.progress = progress;
	}

	/**
	 * Generates the Mapping object
	 * 
	 * @param reporter status reporter
	 * @return the generated XtraServer Mapping
	 * 
	 * @throws IOException if an error occurs loading the mapping template file
	 * @throws UnsupportedTransformationException if the transformation of types or
	 *             properties is not supported
	 */
	public XtraServerMapping generate(final IOReporter reporter)
			throws IOException, UnsupportedTransformationException {

		final Map<String, FeatureTypeMapping> features = new HashMap<>();
		missingAssociationTargets.clear();

		for (final Cell typeCell : this.alignment.getActiveTypeCells()) {
			final String typeTransformationIdentifier = typeCell.getTransformationIdentifier();
			// Create FeatureTypeMapping from the type cells. The Mapping tables are created
			// and added by the Type Handlers
			this.progress.setCurrentTask("Transforming type");
			final TypeTransformationHandler typeHandler = typeHandlerFactory
					.create(typeTransformationIdentifier);
			if (typeHandler != null) {
				final FeatureTypeMapping featureTypeMapping = typeHandler.handle(typeCell);
				features.put(featureTypeMapping.getName(), featureTypeMapping);
				this.progress.setCurrentTask(
						"Mapping values for Feature Type " + featureTypeMapping.getName());

				// Add MappingValues from the type cell's property cells
				for (final Cell propertyCell : this.alignment.getPropertyCells(typeCell)) {
					final String propertyTransformationIdentifier = propertyCell
							.getTransformationIdentifier();
					final PropertyTransformationHandler propertyHandler = propertyHandlerFactory
							.create(propertyTransformationIdentifier);
					if (propertyHandler != null) {
						final MappingValue mappingValue = propertyHandlerFactory
								.create(propertyTransformationIdentifier)
								.handle(new CellParentWrapper(typeCell, propertyCell));
						featureTypeMapping.addValue(mappingValue);
						ensureAssociationTarget(propertyCell, mappingValue, featureTypeMapping);
					}
					this.progress.advance(1);
				}
				this.progress.setCurrentTask(
						"Updating joins for Feature Type " + featureTypeMapping.getName());
				for (final MappingJoin join : featureTypeMapping.getJoins()) {
					if (join.getTarget() == null || "NOT_SET".equals(join.getTarget())) {
						final String table = join.getTargetTable();
						join.setTarget(featureTypeMapping.getTable(table).getTarget());
					}
				}
				this.progress.advance(1);
			}
			else {
				this.progress.advance(this.alignment.getPropertyCells(typeCell).size());
			}
		}
		final XtraServerMapping mapping = mappingContext.createMapping();

		features.values().forEach(m -> mapping.addFeatureTypeMapping(m));
		return mapping;
	}

	/**
	 * Return all property paths for which no association target could be found in
	 * the schema.
	 * 
	 * @return list of properties with missing association targets
	 */
	public List<String> getMissingAssociationTargets() {
		return this.missingAssociationTargets;
	}

	/**
	 * Check if the property cell is a reference and if yes add the association
	 * target that is found in the schema.
	 * 
	 * @param propertyCell Property cell
	 * @param lastValue associated value mapping for the property
	 * @param featureTypeMapping FeatureTypeMapping object to which the
	 *            AssociationTarget object is added
	 */
	private void ensureAssociationTarget(final Cell propertyCell, final MappingValue lastValue,
			final FeatureTypeMapping featureTypeMapping) {
		final Property targetProperty = AppSchemaMappingUtils.getTargetProperty(propertyCell);
		if (targetProperty.getDefinition().getDefinition().getConstraint(Reference.class)
				.isReference()) {
			final String associationTargetRef = getTargetFromSchema(targetProperty);
			if (associationTargetRef == null) {
				final PropertyEntityDefinition propDef = targetProperty.getDefinition();
				missingAssociationTargets.add(propDef.toString());
			}
			else {
				if (lastValue.getTarget() == null) {
					throw new IllegalStateException("Target not set by handler");
				}
				final AssociationTarget xsAssociationTarget = AssociationTarget.create();
				xsAssociationTarget.setObjectRef(associationTargetRef);
				xsAssociationTarget
						.setTarget(lastValue.getTarget().replaceAll("/?@(xlink:)?href", ""));
				featureTypeMapping.addAssociationTarget(xsAssociationTarget);
			}
		}
	}

	/**
	 * Find the association target from the AppInfo annotation in the XSD
	 * 
	 * @param targetProperty target property to analyze
	 * @return association target as String
	 */
	private String getTargetFromSchema(final Property targetProperty) {
		if (targetProperty.getDefinition().getPropertyPath().isEmpty()) {
			return null;
		}

		final ChildDefinition<?> firstChild = targetProperty.getDefinition().getPropertyPath()
				.get(0).getChild();
		if (!(firstChild instanceof PropertyDefinition)) {
			return null;
		}
		final XmlAppInfo appInfoAnnotation = ((PropertyDefinition) firstChild)
				.getConstraint(XmlAppInfo.class);

		for (final XmlSchemaAppInfo appInfo : appInfoAnnotation.getAppInfos()) {
			for (int i = 0; i < appInfo.getMarkup().getLength(); i++) {
				final Node item = appInfo.getMarkup().item(i);
				if ("targetElement".equals(item.getNodeName())) {
					final String target = item.getTextContent();
					return target;
				}
			}
		}
		return null;
	}
}
