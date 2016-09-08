/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.views.data;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.WorkbenchPart;

import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.ui.selection.InstanceSelection;
import eu.esdihumboldt.hale.ui.views.data.internal.DataViewPlugin;
import eu.esdihumboldt.hale.ui.views.data.internal.Messages;
import eu.esdihumboldt.hale.ui.views.data.internal.filter.InstanceServiceSelector;
import eu.esdihumboldt.hale.ui.views.data.internal.filter.SampleTransformInstanceSelector;
import eu.esdihumboldt.hale.ui.views.data.internal.filter.WindowSelectionSelector;
import eu.esdihumboldt.hale.ui.views.properties.PropertiesViewPart;

/**
 * Table for viewing transformed data
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class TransformedDataView extends AbstractDataView {

	/**
	 * The view id
	 */
	public static final String ID = "eu.esdihumboldt.hale.ui.views.data.transformed"; //$NON-NLS-1$

	private Image instanceImage;

	private Image sampleImage;

	private Image mapImage;

	private final InstanceServiceSelector instanceSelector;

	private final SampleTransformInstanceSelector sampleSelector;

	private final WindowSelectionSelector mapSelector;

	private Button mapButton;
	private List<Button> selectorButtons;

	/**
	 * Default constructor
	 */
	public TransformedDataView() {
		super(new SampleTransformInstanceSelector(), SchemaSpaceID.TARGET, ID + ".viewer");

		instanceSelector = new InstanceServiceSelector(SchemaSpaceID.TARGET);
		// another selector based on the reference sample service
		sampleSelector = (SampleTransformInstanceSelector) getDefaultInstanceSelector();
		// selector base on the map selection
		mapSelector = new WindowSelectionSelector(DataSet.TRANSFORMED);
	}

	/**
	 * @see PropertiesViewPart#getViewContext()
	 */
	@Override
	protected String getViewContext() {
		return "eu.esdihumboldt.hale.doc.user.transformed_data";
	}

	/**
	 * @see AbstractDataView#provideCustomControls(Composite)
	 */
	@Override
	protected void provideCustomControls(Composite parent) {
		GridLayout layout = new GridLayout(2, true);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		parent.setLayout(layout);

		selectorButtons = new ArrayList<Button>(3);

		final Button instanceButton = new Button(parent, SWT.RADIO);
		if (instanceImage == null) {
			instanceImage = DataViewPlugin.getImageDescriptor("icons/random.gif").createImage(); //$NON-NLS-1$
		}
		instanceButton.setImage(instanceImage);
		instanceButton.setToolTipText(Messages.TransformedTableView_0); // $NON-NLS-1$
		instanceButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (instanceButton.getSelection())
					setInstanceSelector(instanceSelector);
			}

		});
		selectorButtons.add(instanceButton);

		final Button sampleButton = new Button(parent, SWT.RADIO);
		if (sampleImage == null) {
			sampleImage = DataViewPlugin.getImageDescriptor("icons/table.gif").createImage(); //$NON-NLS-1$
		}
		sampleButton.setImage(sampleImage);
		sampleButton.setSelection(true);
		sampleButton.setToolTipText(Messages.TransformedTableView_SynchToolTipText);
		sampleButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (sampleButton.getSelection())
					setInstanceSelector(sampleSelector);
			}

		});
		selectorButtons.add(sampleButton);

		mapButton = new Button(parent, SWT.RADIO);
		if (mapImage == null) {
			mapImage = DataViewPlugin.getImageDescriptor("icons/synced.gif").createImage(); //$NON-NLS-1$
		}
		mapButton.setImage(mapImage);
		mapButton.setToolTipText(Messages.TransformedTableView_1); // $NON-NLS-1$
		mapButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (mapButton.getSelection())
					setInstanceSelector(mapSelector);
			}

		});
		mapSelector.setActivator(mapButton);
		selectorButtons.add(mapButton);
	}

	/**
	 * Show the given selection.
	 * 
	 * @param is the selection to show
	 * @param image an image to show for the selection, may be <code>null</code>
	 */
	public void showSelection(InstanceSelection is, Image image) {
		// mapButton.setSelected(true) neither fires an event (at least not
		// directly), nor deselects the other buttons
		for (Button b : selectorButtons)
			b.setSelection(false);
		mapButton.setSelection(true);
		setInstanceSelector(mapSelector);
		mapSelector.showSelection(is, image);
	}

	/**
	 * @see WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		instanceImage.dispose();
		sampleImage.dispose();
		mapImage.dispose();

		super.dispose();
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.views.data.AbstractDataView#enableControls(boolean)
	 */
	@Override
	protected void enableControls(boolean enable) {
		// disable/ enable button controls
		for (Button b : selectorButtons)
			b.setEnabled(enable);
	}
}
