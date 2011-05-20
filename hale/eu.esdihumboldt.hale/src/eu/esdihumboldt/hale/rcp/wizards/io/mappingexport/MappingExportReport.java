/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.rcp.wizards.io.mappingexport;

import java.util.HashMap;
import java.util.Map;

import eu.esdihumboldt.specification.cst.align.ICell;

/**
 * Report of a mapping export process
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class MappingExportReport {

	/**
	 * Cells mapped to a reason of why the export failed
	 */
	private final Map<ICell, String> failed = new HashMap<ICell, String>();
	
	/**
	 * Cells mapped to a warning/remark regarding its export
	 */
	private final Map<ICell, String> warnings = new HashMap<ICell, String>();

	/**
	 * Add a cell that cannot be exported
	 * 
	 * @param cell the cell
	 * @param reason the reason why the export is not possible
	 */
	public void setFailed(ICell cell, String reason) {
		failed.put(cell, reason);
		// remove warning if there was any associated with that cell
		warnings.remove(cell);
	}
	
	/**
	 * Add a warning/remark for a cell has been exported
	 * 
	 * @param cell the cell
	 * @param warning the warning/remark
	 */
	public void setWarning(ICell cell, String warning) {
		warnings.put(cell, warning);
	}
	
	/**
	 * @return the failed
	 */
	public Map<ICell, String> getFailed() {
		return failed;
	}

	/**
	 * @return the warnings
	 */
	public Map<ICell, String> getWarnings() {
		return warnings;
	}

	/**
	 * Determines if the report is empty
	 * 
	 * @return if the report is empty
	 */
	public boolean isEmpty() {
		return failed.isEmpty() && warnings.isEmpty();
	}
}
