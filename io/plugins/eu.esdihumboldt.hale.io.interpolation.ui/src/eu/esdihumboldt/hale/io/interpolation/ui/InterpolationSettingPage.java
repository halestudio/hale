package eu.esdihumboldt.hale.io.interpolation.ui;

import java.util.Map;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.ui.io.IOWizard;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;

/**
 * Configuration page for specifying maximum position error for interpolation
 * 
 * @author Arun
 *
 */
public class InterpolationSettingPage
		extends AbstractConfigurationPage<IOProvider, IOWizard<IOProvider>>
		implements InterpolationConstant {

	private Text error;

	/**
	 * Default constructor
	 */
	public InterpolationSettingPage() {
		super("maximumPositionError", "Interpolation", null);

		setDescription("Please enter maximum position error for interpolation of curve geometries");
	}

	@Override
	public void enable() {
		// do nothing

	}

	@Override
	public void disable() {
		// do nothing

	}

	@Override
	public boolean updateConfiguration(IOProvider provider) {
		provider.setParameter(INTERPOL_MAX_POSITION_ERROR, Value.of(error.getText()));
		return true;
	}

	@Override
	protected void createContent(Composite page) {
		page.setLayout(GridLayoutFactory.swtDefaults().numColumns(3).create());

		// label error
		Label labelError = new Label(page, SWT.NONE);
		labelError.setText("User:");
		labelError.setLayoutData(GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).create());

		error = new Text(page, SWT.BORDER | SWT.SINGLE);
		error.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER)
				.grab(true, false).create());

		// label unit
		Label labelUnit = new Label(page, SWT.NONE);
		labelUnit.setText("(unit)");
		labelUnit.setLayoutData(GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).create());

		// filler
		new Label(page, SWT.NONE);

		// label with warning message
		Composite warnComp = new Composite(page, SWT.NONE);
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(warnComp);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(warnComp);

		setPageComplete(false);
	}

	@Override
	protected void onShowPage(boolean firstShow) {
		if (firstShow) {
			setPageComplete(true);
		}
	}

	@Override
	public void loadPreSelection(IOConfiguration conf) {
		Map<String, Value> configs = conf.getProviderConfiguration();
		error.setText(String.valueOf(configs.get(INTERPOL_MAX_POSITION_ERROR).getValue()));
	}

}
