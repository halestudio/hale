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

package eu.esdihumboldt.hale.ui.function.generic.pages;

import java.util.Map;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.extension.function.AbstractParameter;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionParameter;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.function.generic.AbstractGenericFunctionWizard;

/**
 * Page for configuring function parameters.
 * 
 * @author Simon Templer
 * @author Kai Schwierczek
 */
public class ParameterPage extends HaleWizardPage<AbstractGenericFunctionWizard<?, ?>> implements FunctionWizardPage {
	private final Cell initialCell;
	private final ListMultimap<FunctionParameter, Text> inputFields;

	/**
	 * Default constructor
	 * 
	 * @param initialCell the initial cell, may be <code>null</code>
	 */
	public ParameterPage(Cell initialCell) {
		super("parameters");

		setTitle("Function parameters");
		setDescription("Specify the parameters for the relation");

		this.initialCell = initialCell;
		inputFields = ArrayListMultimap.create();

		setPageComplete(false);
	}

	/**
	 * @see HaleWizardPage#onShowPage(boolean)
	 */
	@Override
	protected void onShowPage(boolean firstShow) {
		updateState();
	}

	/**
	 * Update the page state
	 */
	private void updateState() {
		// XXX how to validate fields? -> bindings!?
		// for testing: check whether all fields aren't empty.
		for (Map.Entry<FunctionParameter, Text> entry : inputFields.entries())
			if (entry.getValue().getText().isEmpty()) {
				setPageComplete(false);
				return;
			}
		setPageComplete(true);
	}

	/**
	 * @see FunctionWizardPage#configureCell(MutableCell)
	 */
	@Override
	public void configureCell(MutableCell cell) {
		ListMultimap<String, String> map = cell.getTransformationParameters();
		if (map == null) {
			map = ArrayListMultimap.create();
			cell.setTransformationParameters(map);
		}
		// XXX update instead of reset?
		//     how determine which key-value pairs replace which?
		//     or remove all key-value pairs where key is used by this function?
		//        can there even be some?
		map.clear();
		for (Map.Entry<FunctionParameter, Text> entry : inputFields.entries())
			map.put(entry.getKey().getName(), entry.getValue().getText());
	}

	/**
	 * @see HaleWizardPage#createContent(Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		page.setLayout(GridLayoutFactory.swtDefaults().create());

		for (FunctionParameter fp : this.getWizard().getFunction().getDefinedParameters()) {
			Group group = new Group(page, SWT.NONE);
			group.setText(fp.getDisplayName());
			group.setLayout(GridLayoutFactory.swtDefaults().create());
			if (fp.getDescription() != null) {
				Label description = new Label(group, SWT.NONE);
				description.setText(fp.getDescription());
			}

			for (int i = 0; i < fp.getMinOccurrence(); i++) {
				Text text = new Text(group, SWT.SINGLE);
				text.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
				text.addModifyListener(new ModifyListener() {
					@Override
					public void modifyText(ModifyEvent e) {
						updateState();
					}
				});
				inputFields.put(fp, text);
			}
			if (fp.getMaxOccurrence() == AbstractParameter.UNBOUNDED || fp.getMaxOccurrence() > fp.getMinOccurrence()) {
				// TODO plus button to add more entries
			}
			group.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		}
		//TODO configure with initial cell if present
	}

	/**
	 * @return the initialCell
	 */
	public Cell getInitialCell() {
		return initialCell;
	}
}
