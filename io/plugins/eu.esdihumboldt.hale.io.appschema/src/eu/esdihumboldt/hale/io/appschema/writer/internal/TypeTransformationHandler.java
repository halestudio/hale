package eu.esdihumboldt.hale.io.appschema.writer.internal;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.io.appschema.impl.internal.generated.app_schema.TypeMappingsPropertyType.FeatureTypeMapping;
import eu.esdihumboldt.hale.io.appschema.writer.internal.mapping.AppSchemaMappingContext;

/**
 * Interface defining the API for type transformation handlers.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public interface TypeTransformationHandler {

	/**
	 * Translates a type cell to an app-schema feature type mapping.
	 * 
	 * @param typeCell the type cell
	 * @param context the mapping context
	 * @return the feature type mapping
	 */
	public FeatureTypeMapping handleTypeTransformation(Cell typeCell,
			AppSchemaMappingContext context);

}
