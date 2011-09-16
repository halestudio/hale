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

package eu.esdihumboldt.hale.ui.views.report.deprecated;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;

import eu.esdihumboldt.hale.common.core.report.Message;
import eu.esdihumboldt.hale.common.core.report.Report;
import eu.esdihumboldt.hale.ui.service.report.ReportListener;
import eu.esdihumboldt.hale.ui.service.report.ReportService;
import swing2swt.layout.BorderLayout;
import org.eclipse.swt.graphics.Point;

/**
 * 
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ReportOverview extends ViewPart implements ReportListener<Report<Message>, Message> {

	public static final String ID = "eu.esdihumboldt.hale.ui.views.report.ReportOverview"; //$NON-NLS-1$
	private final FormToolkit toolkit = new FormToolkit(Display.getCurrent());
	private Combo _reportCombo;
	private Composite _container;
	private Composite _composite;
	private Composite _leftComposite;
	private Button _btnSummary;
	private Button _btnDetails;
	private Composite _rightComposite;
	private Composite _contentComposite;

	public ReportOverview() {
		/* nothing */
	}

	/**
	 * Create contents of the view part.
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		// get ReportService and add listener
		ReportService repService = (ReportService) PlatformUI.getWorkbench().getService(ReportService.class);
		repService.addReportListener(this);
		
		_container = toolkit.createComposite(parent, SWT.NONE);
		toolkit.paintBordersFor(_container);
		_container.setLayout(new BorderLayout(0, 0));
		{
			_composite = new Composite(_container, SWT.NONE);
			_composite.setLayoutData(BorderLayout.NORTH);
			toolkit.adapt(_composite);
			toolkit.paintBordersFor(_composite);
			_composite.setLayout(new BorderLayout(0, 20));
			{
				_reportCombo = new Combo(_composite, SWT.READ_ONLY);
				_reportCombo.setSize(new Point(200, 23));
				_reportCombo.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						/*
						 * TODOimplement me
						 * viewer.setInput(new ReportModel(reports.get(combo.getSelectionIndex())));
						 */
					}
				});
				toolkit.adapt(_reportCombo);
				toolkit.paintBordersFor(_reportCombo);
			}
		}
		
		Label hSeperator = new Label(_container, SWT.SEPARATOR | SWT.HORIZONTAL);
		hSeperator.setLayoutData(BorderLayout.SOUTH);
		toolkit.adapt(hSeperator, true, true);
		
		_leftComposite = new Composite(_container, SWT.NONE);
		_leftComposite.setLayoutData(BorderLayout.WEST);
		toolkit.adapt(_leftComposite);
		toolkit.paintBordersFor(_leftComposite);
		RowLayout rl__leftComposite = new RowLayout(SWT.VERTICAL);
		rl__leftComposite.spacing = 0;
		rl__leftComposite.marginTop = 30;
		rl__leftComposite.marginRight = 0;
		rl__leftComposite.fill = true;
		rl__leftComposite.center = true;
		_leftComposite.setLayout(rl__leftComposite);
		
		_btnSummary = new Button(_leftComposite, SWT.FLAT | SWT.TOGGLE | SWT.CENTER);
		_btnSummary.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.err.println("State: "+_btnDetails.getSelection());
				_btnDetails.setSelection(false);
				System.err.println("State: "+_btnDetails.getSelection());
				
				ReportSummary rSummary = new ReportSummary(_contentComposite, SWT.NONE);
				rSummary.setLayoutData(BorderLayout.CENTER);
				_contentComposite.layout();
			}
		});
		_btnSummary.setLayoutData(new RowData(70, -1));
		_btnSummary.setText("Summary");
		_btnSummary.setSelection(true);
		_btnSummary.setGrayed(true);
		toolkit.adapt(_btnSummary, true, true);
		
		_btnDetails = new Button(_leftComposite, SWT.FLAT | SWT.TOGGLE | SWT.CENTER);
		_btnDetails.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
//				syser
				_btnSummary.setSelection(false);
				
				ReportDetails rDetails = new ReportDetails(_contentComposite, SWT.NONE);
				rDetails.setLayoutData(BorderLayout.CENTER);
				_contentComposite.layout();
			}
		});
		_btnDetails.setLayoutData(new RowData(66, -1));
		_btnDetails.setText("Details");
		toolkit.adapt(_btnDetails, true, true);
		
		_rightComposite = new Composite(_container, SWT.NONE);
		_rightComposite.setLayoutData(BorderLayout.CENTER);
		toolkit.adapt(_rightComposite);
		toolkit.paintBordersFor(_rightComposite);
		_rightComposite.setLayout(new BorderLayout(0, 0));
		
		Label vSeperator = new Label(_rightComposite, SWT.SEPARATOR | SWT.VERTICAL);
		vSeperator.setLayoutData(BorderLayout.WEST);
		toolkit.adapt(vSeperator, true, true);
		
		_contentComposite = new Composite(_rightComposite, SWT.NONE);
		_contentComposite.setLayoutData(BorderLayout.CENTER);
		toolkit.adapt(_contentComposite);
		toolkit.paintBordersFor(_contentComposite);
		_contentComposite.setLayout(new BorderLayout(0, 0));

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

	public void getSummary() {
		
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.report.ReportListener#getReportType()
	 */
	@Override
	public Class getReportType() {
		return Report.class;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.report.ReportListener#getMessageType()
	 */
	@Override
	public Class getMessageType() {
		return Message.class;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.report.ReportListener#reportAdded(eu.esdihumboldt.hale.common.core.report.Report)
	 */
	@Override
	public void reportAdded(final Report<Message> report) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				try{
					// create new ReportModel and set it as input
//					viewer.setInput(new ReportModel(report));
					
					// add label to the combo box
					// TODO maybe add the current project to the label?
					_reportCombo.add("["+report.getTimestamp()+"] "+report.getTaskName()+" -- "+report.getSummary());
					
					// select current item
					_reportCombo.select(_reportCombo.getItemCount()-1);
					
					// add report to internal list
//					reports.add(report);
				} catch (NullPointerException e) {
					// TODO remove this or add proper Exception handling
					System.err.println("NullPointer... "+report.getSummary());
					e.printStackTrace();
				}
			}
		});
	}
}
