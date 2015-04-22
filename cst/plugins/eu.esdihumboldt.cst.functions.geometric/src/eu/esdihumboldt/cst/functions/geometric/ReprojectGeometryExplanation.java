package eu.esdihumboldt.cst.functions.geometric;

import java.text.MessageFormat;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.impl.AbstractCellExplanation;

public class ReprojectGeometryExplanation extends AbstractCellExplanation implements ReprojectGeometryFunction{

	@Override
	protected String getExplanation(Cell cell, boolean html) {
		Entity source = CellUtil.getFirstEntity(cell.getSource());
		Entity target = CellUtil.getFirstEntity(cell.getTarget());
		String srcParam = CellUtil.getFirstParameter(cell, PARAMETER_REFERENCE_SYSTEM).as(String.class);

		if (target != null && source != null) {
			String message = "Converts the coordinate reference system of the geometry contained in the {1} property and assigns the result to the {0} property.\n" +
					"The destination coordinate reference system is {2}";
			return MessageFormat.format(message, formatEntity(target, html, true),formatEntity(source, html, true), srcParam);
		}
		return null;
	}

}
