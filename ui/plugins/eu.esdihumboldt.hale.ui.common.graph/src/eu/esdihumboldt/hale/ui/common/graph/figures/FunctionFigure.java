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

package eu.esdihumboldt.hale.ui.common.graph.figures;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.Label;
import org.eclipse.swt.SWT;

import eu.esdihumboldt.hale.common.align.extension.function.FunctionParameter;
import eu.esdihumboldt.hale.ui.util.graph.CustomShapeFigure;
import eu.esdihumboldt.hale.ui.util.graph.shapes.StretchedHexagon;

/**
 * The shape figure for functions
 * @author Patrick Lieb
 */
public class FunctionFigure extends CustomShapeFigure {

	/**
	 * @param parameters the Parameters of the Function
	 */
	public FunctionFigure (Set<FunctionParameter> parameters) {
		super(new StretchedHexagon(10));

		setAntialias(SWT.ON);

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.marginHeight = 3;
		gridLayout.marginWidth = 3;
		setLayoutManager(gridLayout);
		
		Label label = new Label();
		GridData gridData = new GridData(GridData.FILL, GridData.BEGINNING, true,
				false, 2, 1);
		add(label, gridData);
		setTextLabel(label);
		setIconLabel(label);
		
		if(!parameters.isEmpty()){
			
			Label name = new Label();
			GridData nameGrid = new GridData(GridData.FILL, GridData.FILL, true,
					true);
			name.setText("Name");
			add(name, nameGrid);
			
			Label occurence = new Label();
			GridData occurenceGrid = new GridData(GridData.FILL, GridData.FILL, true,
					true);
			occurence.setText("Occurence");
			add(occurence, occurenceGrid);
		
			Iterator<FunctionParameter> iter = parameters.iterator();
			while(iter.hasNext()){
				FunctionParameter para = iter.next();
				name = new Label();
				nameGrid = new GridData(GridData.FILL, GridData.FILL, true,
						true);
				name.setText(para.getDisplayName());
				add(name, nameGrid);
				
				occurence = new Label();
				occurenceGrid = new GridData(GridData.FILL, GridData.FILL, true,
						true);
				occurence.setText(String.valueOf(para.getMinOccurrence())
						+ ".."
						+ (String.valueOf(para.getMaxOccurrence())));
				add(occurence, occurenceGrid);
			}
		}
	}

}
