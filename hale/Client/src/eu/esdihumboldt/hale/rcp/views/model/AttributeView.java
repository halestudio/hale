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
package eu.esdihumboldt.hale.rcp.views.model;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.goml.align.Entity;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.hale.models.AlignmentService;
import eu.esdihumboldt.hale.models.SchemaService;

/**
 * The {@link AttributeView_copy} displays the attributes from the selected data
 * class in the {@link ModelNavigationView_merged}. The
 * {@link AttributeView_copy} consist of the Labels for the names of the
 * selected data classes and the operator between them and Lists for the
 * attributes.
 * 
 * @author Thorsten Reitz
 * @version $Id$
 */
public class AttributeView extends ViewPart implements ISelectionListener {

	public static final String ID = "eu.esdihumboldt.hale.rcp.views.model.AttributeView";

	private static final String FEATURE_TYPE_SUFFIX = "Type";

	// List for the attributes from the selected User Model class
	private Table sourceAttributeList;
	// List for the attributes from the selected INSPIRE Model class
	private Table targetAttributeList;
	// Label for the class name selected in ModelnavigationView source Model
	private Label sourceModelLabel;
	// Label for the class name selected in ModelnavigationView target Model.
	private Label targetModelLabel;
	// Button to open FunctionWizard
	private Button selectFunctionButton;

	private Composite labelComposite;

	// Image Label to show relation between source and target feature types.
	private Label alLabel;

	private AlignmentService as = null;
	private SchemaService schemaService = null;

	private Image transparentImage;
	// Viewer for the sorceAttributeTable
	private TableViewer sourceAttributeViewer;

	private boolean isSourceFeatureType = false;
	private boolean isTargetFeaureType = false;

	public TableViewer getSourceAttributeViewer() {
		return sourceAttributeViewer;
	}

	// Viewer for the targetAttributeTable
	private TableViewer targetAttributeViewer;

	public TableViewer getTargetAttributeViewer() {
		return targetAttributeViewer;
	}

	// the listener we register with the selection service
	private ISelectionListener sourceAttributeListListener = new ISelectionListener() {

		@Override
		public void selectionChanged(IWorkbenchPart part, ISelection selection) {
			// use selection button in case of the multiple attribute selection
			// only
			if (sourceAttributeList.getSelection().length > 1)
				selectFunctionButton.setEnabled(true);
			else
				selectFunctionButton.setEnabled(false);
			selectFunctionButton.redraw();

		}

	};

	// the listener we register with the selection service
	private ISelectionListener targetAttributeListListener = new ISelectionListener() {

		@Override
		public void selectionChanged(IWorkbenchPart part, ISelection selection) {
			// use selection button in case of the multiple attribute selection
			// only
			// if (targetAttributeList.getSelection().length>1)
			// selectFunctionButton.setEnabled(true);
			selectFunctionButton.setEnabled(true);
			// if sourceFeatureType and targetFeatureType selected and no
			// arguments selected
			// else selectFunctionButton.setEnabled(false);
			selectFunctionButton.redraw();

		}

	};

	@Override
	public void createPartControl(Composite _parent) {

		this.schemaService = (SchemaService) this.getSite().getService(
				SchemaService.class);
		this.as = (AlignmentService) this.getSite().getService(
				AlignmentService.class);
		getSite().getWorkbenchWindow().getSelectionService()
				.addSelectionListener(this);
		Composite modelComposite = new Composite(_parent, SWT.BEGINNING);
		GridLayout layout = new GridLayout(2, true);
		layout.verticalSpacing = 6;
		layout.horizontalSpacing = 3;
		modelComposite.setLayout(layout);

		/*
		 * layout.marginHeight = 10; layout.marginWidth = 5;
		 */
		/*
		 * layout.numColumns = 2; layout.makeColumnsEqualWidth = true;
		 */
		// layout.verticalSpacing = 20;
		// layout.horizontalSpacing = 10;
		// add wizard selection button
		Composite buttonComposite = new Composite(modelComposite, SWT.BAR);

		buttonComposite.setLayout(new GridLayout(1, false));
		GridData gData = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_FILL);
		gData.horizontalSpan = 2;
		buttonComposite.setLayoutData(gData);

		selectFunctionButton = new Button(buttonComposite, SWT.PUSH);
		selectFunctionButton.setText("Select Function");
		gData = new GridData(GridData.CENTER, GridData.FILL, true, false);
		// gData.horizontalAlignment = 1;
		// gData.horizontalSpan = 2;
		selectFunctionButton.setLayoutData(gData);
		selectFunctionButton.setEnabled(true);
		selectFunctionButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// button is enabled in case of multiple selection

			}

			@Override
			public void widgetSelected(SelectionEvent e) {

				// start a wizard
				IHandlerService handlerService = (IHandlerService) getSite()
						.getService(IHandlerService.class);
				try {
					handlerService.executeCommand("org.eclipse.ui.newWizard",
							null);
				} catch (Exception e1) {
					throw new RuntimeException(e1);
				}

			}

		});

		Composite labelComposite = new Composite(modelComposite, SWT.BEGINNING);
		labelComposite.setLayout(new GridLayout(3, false));
		gData = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_FILL);
		gData.horizontalSpan = 2;
		labelComposite.setLayoutData(gData);
		// source feature type
		sourceModelLabel = new Label(labelComposite, SWT.RIGHT);

		gData = new GridData(SWT.FILL, SWT.FILL, true, false);
		sourceModelLabel.setLayoutData(gData);
		// sourceModelLabel.setText("source type");

		// aligment label

		alLabel = new Label(labelComposite, SWT.CENTER);
		gData = new GridData(SWT.FILL, SWT.FILL, true, false);

		alLabel.setLayoutData(gData);
		// alLabel.setText("no aligment");
		// aligmentLabel.pack();

		// target label
		targetModelLabel = new Label(labelComposite, SWT.BEGINNING);
		gData = new GridData(SWT.FILL, SWT.FILL, true, false);

		targetModelLabel.setLayoutData(gData);
		// targetModelLabel.setText("target type");

		Composite sourceComposite = new Composite(modelComposite, SWT.BEGINNING);
		sourceComposite.setLayout(new GridLayout(1, false));
		sourceComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true));

		// GridData gData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);

		/*
		 * Label operatorLabel = new Label(modelComposite, SWT.NONE); gData =
		 * new GridData(GridData.HORIZONTAL_ALIGN_FILL |
		 * GridData.VERTICAL_ALIGN_FILL); gData.horizontalAlignment =
		 * SWT.CENTER; operatorLabel.setText("placeholder");
		 * operatorLabel.setLayoutData(gData);
		 */

		// TODO drag and drop or selection button
		/*
		 * Composite buttonComposite = new Composite(modelComposite,
		 * SWT.BEGINNING); buttonComposite.setLayout(new GridLayout(1, false));
		 * buttonComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
		 * true));
		 */

		Composite targetComposite = new Composite(modelComposite, SWT.BEGINNING);
		targetComposite.setLayout(new GridLayout(1, false));
		targetComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true));

		this.sourceAttributeList = this.attributeListSetup(sourceComposite);

		/*
		 * gData = new GridData(GridData.HORIZONTAL_ALIGN_FILL |
		 * GridData.VERTICAL_ALIGN_FILL); gData.grabExcessHorizontalSpace =
		 * true; gData.grabExcessVerticalSpace = true;
		 */
		/*
		 * gData = new GridData(SWT.FILL, SWT.FILL, true, false);
		 * sourceAttributeList.setLayoutData(gData);
		 */
		// Allow data to be linked from the drag source
		int operations = DND.DROP_LINK;

		DragSource source = new DragSource(sourceAttributeList, operations);
		// provide data in Text format
		Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
		source.setTransfer(types);
		source.addDragListener(new DragSourceListener() {
			public void dragStart(DragSourceEvent event) {
				// System.out.println("drag start");
				// Only start the drag if some attribute selected
				// System.out.println("selected element: "
				// + sourceAttributeList.getSelection()[0]);
				if (sourceAttributeList.getSelection()[0] == null) {
					event.doit = false;
				} else
					event.detail = DND.DROP_LINK;
			}

			public void dragSetData(DragSourceEvent event) {
				// System.out.println("drag set data");
				// Provide the data of the requested type.

				DragSource ds = (DragSource) event.widget;
				Table table = (Table) ds.getControl();
				TableItem[] selection = table.getSelection();

				StringBuffer buff = new StringBuffer();
				// System.out.println(selection.length
				// + " Attributes have been selected");
				for (int i = 0, n = selection.length; i < n; i++) {
					buff.append(selection[i].getText());
				}
				event.data = buff.toString();
			}

			public void dragFinished(DragSourceEvent event) {
				// System.out.println("Drag Finished");

			}
		});

		targetAttributeList = this.targetAttributeListSetup(targetComposite);

		// Allow data to be linked to the drop target
		operations = DND.DROP_LINK;
		DropTarget target = new DropTarget(targetAttributeList, operations);	

		final TextTransfer textTransfer = TextTransfer.getInstance();
		types = new Transfer[] { textTransfer };
		target.setTransfer(types);
		target.addDropListener(new DropTargetListener() {

			public void dragEnter(DropTargetEvent event) {
				event.detail = DND.FEEDBACK_INSERT_AFTER;
			}

			public void dragOver(DropTargetEvent event) {

			}

			public void dragOperationChanged(DropTargetEvent event) {

			}

			public void dragLeave(DropTargetEvent event) {

			}

			public void dropAccept(DropTargetEvent event) {

			}

			public void drop(DropTargetEvent event) {
				if (textTransfer.isSupportedType(event.currentDataType)) {
					DropTarget target = (DropTarget) event.widget;
					TableItem targetAttribute = (TableItem) event.item;
					
					// set selection to this targtarget item
					targetAttributeList.setSelection(targetAttribute);

					// TODO replace with wizard call
					IHandlerService handlerService = (IHandlerService) getSite()
							.getService(IHandlerService.class);
					try {
						handlerService.executeCommand(
								"org.eclipse.ui.newWizard", null);
					} catch (Exception ex) {
						throw new RuntimeException(
								"org.eclipse.ui.newWizard not found");
					}
				}
			}
		});
	}

	private Table targetAttributeListSetup(Composite attributeComposite) {
		Composite viewerLComposite = new Composite(attributeComposite, SWT.NONE);
		FillLayout fLayout = new FillLayout();
		viewerLComposite.setLayout(fLayout);
		GridData gData = new GridData(GridData.VERTICAL_ALIGN_FILL
				| GridData.HORIZONTAL_ALIGN_FILL);
		gData.verticalSpan = 32;
		gData.grabExcessHorizontalSpace = true;
		gData.grabExcessVerticalSpace = true;
		gData.verticalIndent = 12;
		viewerLComposite.setLayoutData(gData);
		Table attributeList = new Table(viewerLComposite, SWT.BORDER
				| SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		this.targetAttributeViewer = new TableViewer(attributeList);
		getSite().setSelectionProvider(this.targetAttributeViewer);
		// add listener to the selection service
		getSite().getWorkbenchWindow().getSelectionService()
				.addSelectionListener(this.targetAttributeListListener);
		return attributeList;
	}

	private Table attributeListSetup(Composite attributeComposite) {
		Composite viewerLComposite = new Composite(attributeComposite, SWT.NONE);
		FillLayout fLayout = new FillLayout();
		viewerLComposite.setLayout(fLayout);
		GridData gData = new GridData(GridData.VERTICAL_ALIGN_FILL
				| GridData.HORIZONTAL_ALIGN_FILL);
		gData.verticalSpan = 32;
		gData.grabExcessHorizontalSpace = true;
		gData.grabExcessVerticalSpace = true;
		gData.verticalIndent = 12;
		viewerLComposite.setLayoutData(gData);
		Table attributeList = new Table(viewerLComposite, SWT.BORDER
				| SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);

		// set selection provider for the sourceAttributeList

		this.sourceAttributeViewer = new TableViewer(attributeList);
		getSite().setSelectionProvider(this.sourceAttributeViewer);
		// add listener to the selection service
		getSite().getWorkbenchWindow().getSelectionService()
				.addSelectionListener(this.sourceAttributeListListener);
		return attributeList;
	}

	/**
	 * updateView is called when the user selects another class in the
	 * ModelNavigatiomView. The input of the Lists and Labels of the
	 * AttributeView needs to be updated.
	 * 
	 * @param _viewer
	 *            if true, sourceAttributeList selection changed else
	 *            targetAttributeList selection changed
	 * @param type
	 *            the name of the class that should be displayed in the
	 *            corresponding Label
	 * @param _classnameNumber
	 *            the number of the class in the tree displyed in the
	 *            ModelNavigationView
	 */
	public void updateView(boolean _viewer, TreeObject type,
			TreeObject[] _items, int _classnameNumber) {

		if (_viewer == true) {

			sourceModelLabel.setText(type.getName().getLocalPart());
			// if selected item no attribute
			if (_items.length != 0) {
				setSourceFeatureType(true);

				for (TreeObject item : _items) {

					// display item in the attribute list only if attribute
					if (item.isAttribute()) {
						TableItem listItem = new TableItem(
								this.sourceAttributeList, SWT.NONE);
						listItem.setText(_classnameNumber + ":"
								+ item.getName().getLocalPart());
						// sourceAttributeList.add(_classnameNumber+":"
						// +item.getText());

					}
				}
			} else {
				setSourceFeatureType(false);
				TableItem listItem = new TableItem(this.sourceAttributeList,
						SWT.NONE);
				listItem.setText(type.getName().getLocalPart());
			}
		} else {
			targetModelLabel.setText(type.getName().getLocalPart());
			// if selected item no attribute
			if (_items.length != 0) {
				setTargetFeaureType(true);
				// targetModelLabel.setText(_classname);
				for (TreeObject item : _items) {

					// display item in the attribute list only if attribute
					if (item.isAttribute()) {
						TableItem listItem = new TableItem(
								this.targetAttributeList, SWT.NONE);
						listItem.setText(_classnameNumber + ":"
								+ item.getName().getLocalPart());
						// targetAttributeList.add(_classnameNumber+":"
						// +item.getText());
					}
				}
			} else {
				setTargetFeaureType(false);
				TableItem listItem = new TableItem(this.targetAttributeList,
						SWT.NONE);
				listItem.setText(type.getName().getLocalPart());

				// targetAttributeList.add();
			}
		}

		// if both labels not empty
		if (!sourceModelLabel.getText().equals("")
				&& !targetModelLabel.getText().equals("")
				&& isSourceFeatureType && isTargetFeaureType) {
			// get feature types for source and feature label

			String sourceType = sourceModelLabel.getText();
			String targetType = targetModelLabel.getText();
			FeatureType ft_source = this.schemaService
					.getFeatureTypeByName(sourceType);
			FeatureType ft_target = this.schemaService
					.getFeatureTypeByName(targetType);

			// get URI and local name
			java.util.List<String> nameparts_source = new ArrayList<String>();
			nameparts_source.add(ft_source.getName().getNamespaceURI());
			nameparts_source.add(ft_source.getName().getLocalPart());

			java.util.List<String> nameparts_target = new ArrayList<String>();
			nameparts_target.add(ft_target.getName().getNamespaceURI());
			nameparts_target.add(ft_target.getName().getLocalPart());

			String alignmentLabel = "";

			// create source entity
			Entity e1 = new Property(nameparts_source); // TODO might have to be fixed!

			// create target entity

			Entity e2 = new Property(nameparts_target); // TODO might have to be fixed!
			// get alignment e1, e2
			ICell cell = this.as.getCell(e1, e2);
			if (cell != null) {
				// get transformation type
				alignmentLabel = cell.getEntity1().getTransformation()
						.getLabel();
				// check if filtered before transformation
				ICell filterCell = this.as.getCell(e1, e1);
				// if filter is in a transformation chain and the transformation
				// chain has more than one filter transformation
				if (filterCell != null && !sourceType.equals(targetType))
					alignmentLabel = filterCell.getEntity1()
							.getTransformation().getLabel()
							+ ", " + alignmentLabel;
			}
			if (!alignmentLabel.equals("")) {

				alLabel.setImage(drawAlignmentImage(alignmentLabel));
			} else
				alLabel.setImage(drawAlignmentImage("no alignment"));

		} else if (!sourceModelLabel.getText().equals("")
				&& !targetModelLabel.getText().equals("")) {
			// no attribute transformation implemented.
			alLabel.setImage(drawAlignmentImage("no alignment"));
		}
	}

	public Image drawAlignmentImage(String string) {
		Display display = Display.getDefault();
		Image image = new Image(display, 200, 16);
		Color backgroundColer = display
				.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);

		Color color = display.getSystemColor(SWT.COLOR_BLACK);

		GC gc = new GC(image);
		gc.setBackground(backgroundColer);
		gc.fillRectangle(image.getBounds());
		gc.setForeground(color);
		gc.setLineWidth(1);
		gc.drawLine(0, 6, 200, 6);
		Font font = new Font(display, "Arial", 10, SWT.BOLD | SWT.ITALIC);
		gc.setFont(font);
		gc.drawText(string, 55, -3, false);

		gc.dispose();
		return image;
	}

	/**
	 * Delete the class name from Model label and clear the corresponding list.
	 * 
	 * @param _list
	 *            if true, selection in userDataViewer changes, else selection
	 *            in inspireDataViewer changes
	 */
	public void clear(boolean _list) {
		if (_list) {
			sourceAttributeList.removeAll();
			sourceModelLabel.setText("");
		} else {
			targetAttributeList.removeAll();
			targetModelLabel.setText("");
		}
	}

	@Override
	public void setFocus() {

	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			final Object selectionObject = ((IStructuredSelection) selection)
					.getFirstElement();
			if (selectionObject != null) {
				String selectedFeatureType = "not selected";
				FeatureTypeSelection ftSelection = (FeatureTypeSelection) selectionObject;
				TreeItem sourceItem = ftSelection.getSourceFeatureTypes()[0];
				if (sourceItem != null) {
					selectedFeatureType = sourceItem.getText();
				}

				selectedFeatureType = "not selected";
				TreeItem targetItem = ftSelection.getTargetFeatureType()[0];
				if (targetItem != null) {
					selectedFeatureType = targetItem.getText();
				}

				// TODO use it for SelectFunction, AttributeLists

			}
		}

	}

	public Label getAlLabel() {
		return alLabel;
	}

	public boolean isSourceFeatureType() {
		return isSourceFeatureType;
	}

	public void setSourceFeatureType(boolean isSourceFeatureType) {
		this.isSourceFeatureType = isSourceFeatureType;
	}

	public boolean isTargetFeaureType() {
		return isTargetFeaureType;
	}

	public void setTargetFeaureType(boolean isTargetFeaureType) {
		this.isTargetFeaureType = isTargetFeaureType;
	}

}
