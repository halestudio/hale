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

package eu.esdihumboldt.hale.ui.views.report;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;
import swing2swt.layout.BorderLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Combo;

/**
 * TODO Type description
 * @author andi
 */
public class ReportOverview extends ViewPart {

	public static final String ID = "eu.esdihumboldt.hale.ui.views.report.ReportOverview"; //$NON-NLS-1$
	private final FormToolkit toolkit = new FormToolkit(Display.getCurrent());

	public ReportOverview() {
	}

	/**
	 * Create contents of the view part.
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite container = toolkit.createComposite(parent, SWT.NONE);
		toolkit.paintBordersFor(container);
		container.setLayout(new BorderLayout(0, 0));
		{
			Composite leftComposite = new Composite(container, SWT.NONE);
			leftComposite.setLayoutData(BorderLayout.WEST);
			toolkit.adapt(leftComposite);
			toolkit.paintBordersFor(leftComposite);
			RowLayout rl_leftComposite = new RowLayout(SWT.VERTICAL);
			rl_leftComposite.spacing = 0;
			rl_leftComposite.marginTop = 30;
			rl_leftComposite.marginRight = 0;
			rl_leftComposite.fill = true;
			rl_leftComposite.center = true;
			leftComposite.setLayout(rl_leftComposite);
			{
				Button button = new Button(leftComposite, SWT.FLAT | SWT.TOGGLE | SWT.CENTER);
				button.setLayoutData(new RowData(70, -1));
				button.setText("Summary");
				button.setSelection(true);
				button.setGrayed(true);
				toolkit.adapt(button, true, true);
			}
			{
				Button button = new Button(leftComposite, SWT.FLAT | SWT.TOGGLE | SWT.CENTER);
				button.setLayoutData(new RowData(66, -1));
				button.setText("Details");
				toolkit.adapt(button, true, true);
			}
		}
		{
			Combo reportCombo = new Combo(container, SWT.NONE);
			reportCombo.setLayoutData(BorderLayout.NORTH);
			toolkit.adapt(reportCombo);
			toolkit.paintBordersFor(reportCombo);
		}
		{
			Composite contentComposite = new Composite(container, SWT.NONE);
			contentComposite.setLayoutData(BorderLayout.CENTER);
			toolkit.adapt(contentComposite);
			toolkit.paintBordersFor(contentComposite);
		}

		createActions();
		initializeToolBar();
		initializeMenu();
	}

	public void dispose() {
		toolkit.dispose();
		super.dispose();
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
	}

	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		IToolBarManager tbm = getViewSite().getActionBars().getToolBarManager();
	}

	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
		IMenuManager manager = getViewSite().getActionBars().getMenuManager();
	}

	@Override
	public void setFocus() {
		// Set the focus
	}

}
