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

package eu.esdihumboldt.hale.ui.io.align;

import eu.esdihumboldt.hale.common.align.io.AlignmentWriter;
import eu.esdihumboldt.hale.ui.io.ExportWizard;

/**
 * Wizard for exporting alignments
 * 
 * @author Simon Templer
 * @since 2.5
 */
public class AlignmentExportWizard extends ExportWizard<AlignmentWriter> {

	/**
	 * Create a schema export wizard
	 */
	public AlignmentExportWizard() {
		super(AlignmentWriter.class);
	}

}
