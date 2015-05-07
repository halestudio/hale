package eu.esdihumboldt.hale.ui.functions.geometric;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.geotools.referencing.CRS;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.cst.functions.geometric.ReprojectGeometryFunction;
import eu.esdihumboldt.hale.common.align.extension.function.PropertyFunctionExtension;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.instance.geometry.CRSDefinitionManager;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.hale.ui.function.generic.pages.AbstractParameterPage;
import eu.esdihumboldt.hale.ui.io.instance.crs.SelectCRSDialog;

/**
 * Page for choosing the coordinate reference system to convert to.
 * 
 * @author Stefano Costa
 */
public class CoordinateReferenceSystemParameterPage extends AbstractParameterPage implements
		ReprojectGeometryFunction {

	private Label crsLabel;
	private Button selectCrs;
	private CRSDefinition crsDef;

	/**
	 * Default constructor.
	 */
	public CoordinateReferenceSystemParameterPage() {
		super(PropertyFunctionExtension.getInstance().get(ID),
				"Please select the CRS of the destination geometry");
		setPageComplete(false);
	}

	@Override
	protected void onShowPage(boolean firstShow) {
		super.onShowPage(firstShow);

		if (firstShow && getWizard().getInitCell() != null) {
			List<ParameterValue> parameters = getWizard().getInitCell()
					.getTransformationParameters().get(PARAMETER_REFERENCE_SYSTEM);
			if (parameters != null && parameters.size() > 0) {
				String crs = parameters.get(0).getStringRepresentation();
				crsDef = CRSDefinitionManager.getInstance().parse(crs);
			}
		}

		update();
	}

	@Override
	public ListMultimap<String, ParameterValue> getConfiguration() {
		ListMultimap<String, ParameterValue> conf = ArrayListMultimap.create();

		String crsAsString = null;
		if (crsDef != null) {
			crsAsString = CRSDefinitionManager.getInstance().asString(crsDef);
		}
		conf.put(PARAMETER_REFERENCE_SYSTEM, new ParameterValue(crsAsString));

		return conf;
	}

	@Override
	protected void createContent(Composite page) {
		GridLayoutFactory.swtDefaults().numColumns(1).applyTo(page);

		Group convertGroup = new Group(page, SWT.NONE);
		convertGroup.setText("Reproject to CRS");
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(convertGroup);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(convertGroup);

		crsLabel = new Label(convertGroup, SWT.NONE);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false)
				.applyTo(crsLabel);

		selectCrs = new Button(convertGroup, SWT.PUSH);
		selectCrs.setText("Select...");
		GridDataFactory.swtDefaults().applyTo(selectCrs);

		selectCrs.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				SelectCRSDialog dlg = new SelectCRSDialog(e.display.getActiveShell(), crsDef);
				if (dlg.open() == Dialog.OK) {
					crsDef = dlg.getValue();
				}
				update();
			}

		});
	}

	/**
	 * Update the page state.
	 */
	private void update() {
		// update the CRS label
		if (crsDef == null || crsDef.getCRS() == null) {
			crsLabel.setText("<None selected>");
		}
		else {
			String name = crsDef.getCRS().getName().toString();
			name = name.replaceAll("EPSG:", "");
			name += " (" + CRS.toSRS(crsDef.getCRS()) + ")";
			crsLabel.setText(name);
		}

		// page status
		if (crsDef == null) {
			setPageComplete(false);
			setMessage("Specify a valid target CRS to reproject to", DialogPage.ERROR);
		}
		else {
			setPageComplete(true);
			setMessage(null);
		}
	}

}
