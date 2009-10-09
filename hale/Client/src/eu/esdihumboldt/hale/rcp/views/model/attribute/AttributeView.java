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
package eu.esdihumboldt.hale.rcp.views.model.attribute;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.jface.viewers.ISelection;
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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.part.WorkbenchPart;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.goml.align.Entity;
import eu.esdihumboldt.goml.omwg.FeatureClass;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.goml.omwg.Restriction;
import eu.esdihumboldt.hale.models.AlignmentService;
import eu.esdihumboldt.hale.models.HaleServiceListener;
import eu.esdihumboldt.hale.models.SchemaService;
import eu.esdihumboldt.hale.models.UpdateMessage;
import eu.esdihumboldt.hale.rcp.views.model.ModelNavigationView;
import eu.esdihumboldt.hale.rcp.views.model.SchemaItem;
import eu.esdihumboldt.hale.rcp.views.model.SchemaSelection;
import eu.esdihumboldt.hale.rcp.views.model.SchemaType;
import eu.esdihumboldt.hale.rcp.views.model.TreeParent;

/**
 * The {@link AttributeView} displays the attributes from the selected data
 * class in the {@link ModelNavigationView}. The
 * {@link AttributeView} consist of the Labels for the names of the
 * selected data classes and the operator between them and Lists for the
 * attributes.
 * 
 * @author Thorsten Reitz, Simon Templer
 * @version $Id$
 */
public class AttributeView extends ViewPart implements ISelectionListener {

	/**
	 * The view id
	 */
	public static final String ID = "eu.esdihumboldt.hale.rcp.views.model.AttributeView";

	// List for the attributes from the selected User Model class
	private Table sourceAttributeList;
	// List for the attributes from the selected INSPIRE Model class
	private Table targetAttributeList;
	// Label for the class name selected in ModelnavigationView source Model
	private Label sourceModelLabel;
	// Label for the class name selected in ModelnavigationView target Model.
	private Label targetModelLabel;
	
	private SchemaSelection lastSelection = new SchemaSelection();

	//XXX private Composite labelComposite;

	// Image Label to show relation between source and target feature types.
	private Label alLabel;

	private AlignmentService as = null;
	private SchemaService schemaService = null;

	//XXX private Image transparentImage;
	
	// Viewer for the sorceAttributeTable
	private TableViewer sourceAttributeViewer;

	private boolean isSourceFeatureType = false;
	private boolean isTargetFeaureType = false;

	// Viewer for the targetAttributeTable
	private TableViewer targetAttributeViewer;

	@Override
	public void createPartControl(Composite _parent) {
		// get schema service
		this.schemaService = (SchemaService) this.getSite().getService(
				SchemaService.class);
		
		// get alignment service
		this.as = (AlignmentService) this.getSite().getService(
				AlignmentService.class);
		
		// register as selection listener
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

		Composite labelComposite = new Composite(modelComposite, SWT.BEGINNING);
		labelComposite.setLayout(new GridLayout(3, false));
		gData = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_FILL);
		gData.horizontalSpan = 2;
		labelComposite.setLayoutData(gData);
		// source feature type
		sourceModelLabel = new Label(labelComposite, SWT.RIGHT);

		gData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
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
		gData = new GridData(SWT.END, SWT.CENTER, false, false);

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
		
		// listen on alignment service
		as.addListener(new HaleServiceListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void update(UpdateMessage message) {
				updateAlignment();
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

		this.sourceAttributeViewer = new TableViewer(attributeList);
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
	 * @param _items
	 * 			  the child items
	 * @param _classnameNumber
	 *            the number of the class in the tree displyed in the
	 *            ModelNavigationView
	 */
	public void updateView(SchemaType _viewer, SchemaItem type,
			SchemaItem[] _items, int _classnameNumber) {

		if (_viewer.equals(SchemaType.SOURCE)) {

			sourceModelLabel.setText(type.getName().getLocalPart());
			// if selected item no attribute
			if (_items.length != 0) {
				setSourceFeatureType(true);

				for (SchemaItem item : _items) {

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
				for (SchemaItem item : _items) {

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
		
		sourceModelLabel.getParent().layout(true);
	}

	/**
	 * Update the alignment label
	 */
	private void updateAlignment() {
		String label = null;
		
		if (lastSelection != null) {
			SchemaItem source = lastSelection.getFirstSourceItem();
			SchemaItem target = lastSelection.getFirstTargetItem();
			
			if (source != null && target != null &&
					(source.isAttribute() || source.isType()) &&
					(target.isAttribute() || target.isType())) {
				Entity sourceEntity = source.getEntity();
				ICell cell = as.getCell(sourceEntity, target.getEntity());
				
				
				
				if (cell != null) {
					// get transformation type
					label = shortenLabel(cell.getEntity1().getTransformation()
							.getLabel());
					
					// check if a Filter Restriction is applied to this cell
					Restriction r = null;
					if (cell.getEntity1() instanceof FeatureClass) {
						FeatureClass fc = (FeatureClass)cell.getEntity1();
						if (fc.getAttributeValueCondition() != null) {
							r = fc.getAttributeValueCondition().get(0);
						}
					}
					else if (cell.getEntity1() instanceof Property) {
						Property fc = (Property)cell.getEntity1();
						if (fc.getValueCondition() != null) {
							r = fc.getValueCondition().get(0);
						}
					}

					if (r != null && r.getCqlStr() != null) {
						label = "Filter (" + r.getCqlStr() + "), " + label;
					}
				}
			}
			else {
				label = "";
			}
		}
		
		Image newImage;
		if (label == null) {
			newImage = drawAlignmentImage("no alignment");
		}
		else if (label.equals("")) {
			newImage = null;
		}
		else {
			newImage = drawAlignmentImage(label);
		}
		
		Image oldImage = alLabel.getImage();
		alLabel.setImage(newImage);
		
		if (oldImage != null) {
			oldImage.dispose();
		}
	}

	private static String shortenLabel(String label) {
		if (label == null)
			return null;
		
		String[] split = label.split("\\.");
		
		return split[split.length - 1];
	}

	private Image drawAlignmentImage(String string) {
		Display display = Display.getDefault();
		int width = alLabel.getSize().x;
		Image image = new Image(display, width, 16);
		Color backgroundColer = display
				.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);

		Color color = display.getSystemColor(SWT.COLOR_BLACK);

		GC gc = new GC(image);
		Font font = new Font(display, "Arial", 10, SWT.BOLD | SWT.ITALIC);
		try {
			gc.setBackground(backgroundColer);
			gc.fillRectangle(image.getBounds());
			gc.setForeground(color);
			gc.setLineWidth(1);
			gc.drawLine(0, 6, width, 6);
			
			gc.setFont(font);
			int stringWidth = gc.stringExtent(string).x;
			gc.drawText(string, (width - stringWidth) / 2, -3, false);
		} finally {
			gc.dispose();
			font.dispose();
		}
		
		return image;
	}

	/**
	 * Delete the class name from Model label and clear the corresponding list.
	 * 
	 * @param type the schema type
	 */
	public void clear(SchemaType type) {
		if (type == null) {
			// clear all
			clear(SchemaType.SOURCE);
			clear(SchemaType.TARGET);
			return;
		}
		
		switch (type) {
		case SOURCE:
			sourceAttributeList.removeAll();
			sourceModelLabel.setText("");
			break;
		case TARGET:
			targetAttributeList.removeAll();
			targetModelLabel.setText("");
			break;
		default:
			// do nothing
		}
	}

	/**
	 * @see WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {

	}

	/**
	 * @see ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
	 */
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (selection instanceof SchemaSelection) {
			SchemaSelection sel = (SchemaSelection) selection;
			
			clear(null);
			
			addItems(sel.getSourceItems(), SchemaType.SOURCE);
			addItems(sel.getTargetItems(), SchemaType.TARGET);
			
			lastSelection = sel;
			updateAlignment();
		}
	}

	private void addItems(Set<SchemaItem> items, SchemaType type) {
		Iterator<SchemaItem> it = items.iterator();
		int number = 0;
		while (it.hasNext()) {
			SchemaItem item = it.next();
			number++;
			if (item.isAttribute() || item.isType()) { 
				if (item instanceof TreeParent) {
					updateView(type, item, ((TreeParent) item).getChildren(), number);
				}
				else {
					updateView(type, item, new SchemaItem[]{}, number);
				}
			}
		}
	}

	/**
	 * @return if the source feature type is present
	 */
	public boolean isSourceFeatureType() {
		return isSourceFeatureType;
	}
	

	/**
	 * Set if a source feature type is present
	 * 
	 * @param isSourceFeatureType if a source feature type is present
	 */
	public void setSourceFeatureType(boolean isSourceFeatureType) {
		this.isSourceFeatureType = isSourceFeatureType;
	}

	/**
	 * @return if the target feature type is present
	 */
	public boolean isTargetFeaureType() {
		return isTargetFeaureType;
	}

	/**
	 * Set if a target feature type is present
	 * 
	 * @param isTargetFeaureType if a target feature type is present
	 */
	public void setTargetFeaureType(boolean isTargetFeaureType) {
		this.isTargetFeaureType = isTargetFeaureType;
	}

	/**
	 * @see WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		if (!alLabel.isDisposed()) {
			Image image = alLabel.getImage();
			if (image != null) {
				alLabel.setImage(null);
				image.dispose();
			}
		}
		
		super.dispose();
	}

}
