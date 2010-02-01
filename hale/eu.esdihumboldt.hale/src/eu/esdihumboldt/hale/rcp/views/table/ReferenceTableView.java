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

package eu.esdihumboldt.hale.rcp.views.table;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.PlatformUI;
import org.opengis.feature.Feature;

import eu.esdihumboldt.hale.models.SchemaService.SchemaType;
import eu.esdihumboldt.hale.rcp.views.table.filter.InstanceServiceFeatureSelector;


/**
 * Table for viewing reference data
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class ReferenceTableView extends AbstractTableView {
	
	/**
	 * The view id
	 */
	public static final String ID = "eu.esdihumboldt.hale.rcp.views.ReferenceTable";

	/**
	 * Default constructor
	 */
	public ReferenceTableView() {
		super(new InstanceServiceFeatureSelector(SchemaType.SOURCE));
	}

	/**
	 * @see AbstractTableView#onSelectionChange(Iterable)
	 */
	@Override
	protected void onSelectionChange(Iterable<Feature> selection) {
		ReferenceSampleService rss = (ReferenceSampleService) PlatformUI.getWorkbench().getService(ReferenceSampleService.class);
		
		List<Feature> res = new ArrayList<Feature>();
		if (selection != null) {
			for (Feature feature : selection) {
				res.add(feature);
			}
		}
		
		rss.setReferenceFeatures(res);
	}

}
