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
package eu.esdihumboldt.hale.ui.style.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.Symbolizer;

import eu.esdihumboldt.hale.ui.style.internal.Messages;

/**
 * Symbolizer dialog
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class SymbolizerDialog extends TrayDialog {

	private static final StyleBuilder styleBuilder = new StyleBuilder();

	private Combo combo;

	private Symbolizer symbolizer = null;

	/**
	 * @see TrayDialog#TrayDialog(Shell)
	 */
	public SymbolizerDialog(Shell shell) {
		super(shell);
	}

	/**
	 * @see Window#configureShell(Shell)
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);

		newShell.setText(Messages.SymbolizerDialog_ShellSymbolizerText);
	}

	/**
	 * @see Dialog#createDialogArea(Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite page = (Composite) super.createDialogArea(parent);
		page.setLayout(new RowLayout(SWT.VERTICAL));

		Label label = new Label(page, SWT.NONE);
		label.setText(Messages.SymbolizerDialog_LabelText);

		combo = new Combo(page, SWT.READ_ONLY);
		combo.add(LineSymbolizer.class.getSimpleName());
		combo.add(PolygonSymbolizer.class.getSimpleName());
		combo.add(PointSymbolizer.class.getSimpleName());

		combo.select(0);

		return page;
	}

	/**
	 * @see Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		switch (combo.getSelectionIndex()) {
		case 1:
			symbolizer = styleBuilder.createPolygonSymbolizer();
			break;
		case 2:
			symbolizer = styleBuilder.createPointSymbolizer();
			break;
		case 0:
			// fall through
		default:
			symbolizer = styleBuilder.createLineSymbolizer();
		}

		super.okPressed();
	}

	/**
	 * Get the selected symbolizer
	 * 
	 * @return the symbolizer or null if none was selected
	 */
	public Symbolizer getSymbolizer() {
		return symbolizer;
	}

}
