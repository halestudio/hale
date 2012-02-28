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

package eu.esdihumboldt.hale.ui.codelist.editor;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import eu.esdihumboldt.hale.common.codelist.CodeList;
import eu.esdihumboldt.hale.common.codelist.CodeList.CodeEntry;
import eu.esdihumboldt.hale.schemaprovider.model.AttributeDefinition;
import eu.esdihumboldt.hale.ui.codelist.internal.CodeListUIPlugin;
import eu.esdihumboldt.hale.ui.codelist.internal.Messages;
import eu.esdihumboldt.hale.ui.common.definition.AbstractAttributeEditor;
import eu.esdihumboldt.hale.ui.common.definition.AttributeEditor;

/**
 * Editor for enumeration attributes.
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class CodeListAttributeEditor extends AbstractAttributeEditor<CodeEntry> {
	
	private ComboViewer codeEditor;
	
	private Text textEditor;
	
	private final Composite main;
	
	private final Composite editorContainer;
	
	private final String codeListNamespace;
	
	private final String codeListName;
	
	private final AttributeDefinition attribute;
	
	private CodeList codeList;
	
	private Image assignImage;

	/**
	 * Create a code list attribute editor
	 * 
	 * @param parent the parent composite
	 * @param attribute the attribute definition
	 */
	public CodeListAttributeEditor(Composite parent, AttributeDefinition attribute) {
		super();
		
		main = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.horizontalSpacing = 0;
		main.setLayout(gridLayout);
		
		editorContainer = new Composite(main, SWT.NONE);
		editorContainer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		editorContainer.setLayout(new FillLayout());
		
		codeListNamespace = attribute.getDeclaringType().getName().getNamespaceURI();
		String attributeName = attribute.getName();
		codeListName = Character.toUpperCase(attributeName.charAt(0)) + attributeName.substring(1) + "Value"; //$NON-NLS-1$
		this.attribute = attribute;
		
		// add editor
		updateCodeList();
		
		// add button
		assignImage = CodeListUIPlugin.getImageDescriptor("icons/assign_codelist.gif").createImage(); //$NON-NLS-1$
		
		Button assign = new Button(main, SWT.PUSH);
		assign.setImage(assignImage);
		assign.setToolTipText(Messages.CodeListAttributeEditor_2); //$NON-NLS-1$
		assign.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				selectCodeList();
			}
			
		});
		
		main.addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent e) {
				assignImage.dispose();
			}
		});
	}

	/**
	 * Select a new code list
	 */
	protected void selectCodeList() {
		//FIXME update
//		final Display display = Display.getCurrent();
//		CodeListSelectionDialog dialog = new CodeListSelectionDialog(display.getActiveShell(), codeList,
//				MessageFormat.format(Messages.CodeListAttributeEditor_0,attribute.getDisplayName())); //$NON-NLS-1$
//		if (dialog.open() == CodeListSelectionDialog.OK) {
//			CodeList newCodeList = dialog.getCodeList();
//			CodeListService codeListService = (CodeListService) PlatformUI.getWorkbench().getService(CodeListService.class);
//				
//			codeListService.assignAttributeCodeList(attribute.getIdentifier(), newCodeList);
//				
//			updateCodeList();
//		}
	}

	/**
	 * Update the editor's code list
	 */
	protected void updateCodeList() {
		//FIXME update
//		CodeListService codeListService = (CodeListService) PlatformUI.getWorkbench().getService(CodeListService.class);
//		codeList = codeListService.findCodeListByAttribute(attribute.getIdentifier());
//		if (codeList == null) {
//			codeList = codeListService.findCodeListByIdentifier(codeListNamespace, codeListName);
//		}
//		
//		String oldValue = getAsText();
//		
//		if (codeList != null) {
//			// create or update code editor
//			if (textEditor != null) {
//				textEditor.dispose();
//				textEditor = null;
//			}
//			
//			if (codeEditor == null) {
//				codeEditor = new ComboViewer(editorContainer, SWT.READ_ONLY);
//				codeEditor.setContentProvider(ArrayContentProvider.getInstance());
//				codeEditor.setLabelProvider(new LabelProvider() {
//	
//					@Override
//					public String getText(Object element) {
//						if (element instanceof CodeEntry) {
//							return ((CodeEntry) element).getName();
//						}
//						else {
//							return super.getText(element);
//						}
//					}
//					
//				});
//				final Combo combo =  codeEditor.getCombo();
//				codeEditor.addPostSelectionChangedListener(new ISelectionChangedListener() {
//					
//					@Override
//					public void selectionChanged(SelectionChangedEvent event) {
//						ISelection selection = event.getSelection();
//						if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
//							CodeEntry entry = (CodeEntry) ((IStructuredSelection) selection).getFirstElement();
//							combo.setToolTipText(entry.getName() + ":\n\n" + entry.getDescription()); //$NON-NLS-1$
//						}
//						else {
//							combo.setToolTipText(null);
//						}
//					}
//				});
//			}
//			
//			codeEditor.setInput(codeList.getEntries());
//		}
//		else {
//			// create or update text editor
//			if (codeEditor != null) {
//				codeEditor.getControl().dispose();
//				codeEditor = null;
//			}
//			
//			if (textEditor == null) {
//				textEditor = new Text(editorContainer, SWT.BORDER | SWT.SINGLE);
//			}
//		}
//		
//		if (editorContainer.getParent() != null) {
//			editorContainer.getParent().layout(true, true);
//		}
//		else {
//			editorContainer.layout(true, true);
//		}
//		
//		setAsText(oldValue);
	}

	/**
	 * @see AttributeEditor#getAsText()
	 */
	@Override
	public String getAsText() {
		CodeEntry value = getValue();
		if (value != null) {
			return value.getIdentifier();
		}
		else {
			return null;
		}
	}

	/**
	 * @see AttributeEditor#getControl()
	 */
	@Override
	public Control getControl() {
		return main;
	}

	/**
	 * @see AttributeEditor#getValue()
	 */
	@Override
	public CodeEntry getValue() {
		if (codeEditor != null) {
			ISelection selection = codeEditor.getSelection();
			if (selection == null || selection.isEmpty() || !(selection instanceof IStructuredSelection)) {
				return null;
			}
			else {
				return (CodeEntry) ((IStructuredSelection) selection).getFirstElement();
			}
		}
		else if (textEditor != null) {
			return createCustomEntry(textEditor.getText());
		}
		else {
			return null;
		}
	}

	/**
	 * Create a custom code entry with the given text
	 * 
	 * @param text the text
	 * 
	 * @return the code entry
	 */
	private CodeEntry createCustomEntry(String text) {
		return new CodeEntry(text, null, text, "custom"); //$NON-NLS-1$
	}

	/**
	 * @see AttributeEditor#setAsText(String)
	 */
	@Override
	public void setAsText(String text) {
		if (codeList != null) {
			CodeEntry value = codeList.getEntryByIdentifier(text);
			if (value == null) {
				// try entry by name as fall-back
				value = codeList.getEntryByName(text);
			}
			setValue(value);
		}
		else {
			setValue(createCustomEntry((text == null)?(""):(text))); //$NON-NLS-1$
		}
	}

	/**
	 * @see AttributeEditor#setValue(Object)
	 */
	@Override
	public void setValue(CodeEntry value) {
		if (codeEditor != null) {
			codeEditor.setSelection((value == null)?(new StructuredSelection()):(new StructuredSelection(value)), true);
		}
		else if (textEditor != null) {
			textEditor.setText((value == null)?(""):(value.getName())); //$NON-NLS-1$
		}
	}

	/**
	 * @see AttributeEditor#isValid()
	 */
	@Override
	public boolean isValid() {
		return true;
	}

}
