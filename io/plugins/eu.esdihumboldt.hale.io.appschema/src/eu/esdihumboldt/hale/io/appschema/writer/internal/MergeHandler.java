package eu.esdihumboldt.hale.io.appschema.writer.internal;

import eu.esdihumboldt.cst.functions.core.Merge;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.io.appschema.impl.internal.generated.app_schema.TypeMappingsPropertyType.FeatureTypeMapping;

/**
 * Translates a type cell specifying a {@link Merge} transformation function to
 * an app-schema feature type mapping.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class MergeHandler extends SingleSourceToTargetHandler {

	@Override
	public FeatureTypeMapping handleTypeTransformation(Alignment alignment, Cell typeCell,
			AppSchemaMappingWrapper mapping) {
		FeatureTypeMapping ftMapping = super.handleTypeTransformation(alignment, typeCell, mapping);

		// this is the only variation from RetypeHandler so far
		ftMapping.setIsDenormalised(true);

		return ftMapping;
	}

}
