/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.common.align.model.functions.explanations;

import java.text.MessageFormat;
import java.util.List;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellExplanation;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.functions.MergeFunction;

/**
 * Explanation for merge function cells.
 * @author Simon Templer
 */
public class MergeExplanation implements CellExplanation, MergeFunction {

	/**
	 * @see CellExplanation#getExplanation(Cell)
	 */
	@Override
	public String getExplanation(Cell cell) {
		Entity source = CellUtil.getFirstEntity(cell.getSource());
		Entity target = CellUtil.getFirstEntity(cell.getTarget());
		
		List<String> properties = (cell.getTransformationParameters() == null)?(null):(cell.getTransformationParameters().get(PARAMETER_PROPERTY)); 
		
		if (source != null && target != null && properties != null 
				&& !properties.isEmpty()) {
			StringBuffer propertiesString = new StringBuffer();
			for (int i = 0; i < properties.size(); i++) {
				propertiesString.append('\'');
				propertiesString.append(properties.get(i));
				propertiesString.append('\'');
				
				if (i == properties.size() - 2) {
					propertiesString.append(" and ");
				}
				else if (i < properties.size() - 2) {
					propertiesString.append(", ");
				}
			}
			
			return MessageFormat.format("Merges different instances of the type {0} based on its properties {2} being equal. The values of these properties are merged into one, while the values of the other properties will be available in the target instance of type {1} as separate values for each source instance.",
					source.getDefinition().getDefinition().getDisplayName(),
					target.getDefinition().getDefinition().getDisplayName(),
					propertiesString);
		}
		
		return null;
	}

}
