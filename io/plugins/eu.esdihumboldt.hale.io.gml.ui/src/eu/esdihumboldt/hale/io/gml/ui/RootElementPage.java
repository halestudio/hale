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

package eu.esdihumboldt.hale.io.gml.ui;

import javax.xml.namespace.QName;

import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.instance.io.InstanceWriter;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;
import eu.esdihumboldt.hale.io.gml.writer.internal.StreamGmlWriter;
import eu.esdihumboldt.hale.io.xsd.model.XmlElement;
import eu.esdihumboldt.hale.io.xsd.model.XmlIndex;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.io.IOWizardPage;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;
import eu.esdihumboldt.hale.ui.io.instance.InstanceWriterConfigurationPage;

/**
 * Configuration page for setting an XML root element 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
@SuppressWarnings("restriction")
public class RootElementPage extends InstanceWriterConfigurationPage {

	private ListViewer list;
	private Text filterText;

	/**
	 * Default constructor
	 */
	public RootElementPage() {
		super("xml.rootElement");
		
		setTitle("XML root element");
		setDescription("Please select the root element to use in the XML file");
	}

	/**
	 * @see IOWizardPage#updateConfiguration(IOProvider)
	 */
	@Override
	public boolean updateConfiguration(InstanceWriter provider) {
		ISelection sel = list.getSelection();
		
		if (!sel.isEmpty() && sel instanceof IStructuredSelection) {
			Object selected = ((IStructuredSelection) sel).getFirstElement();
			
			if (selected instanceof XmlElement) {
				QName name = ((XmlElement) selected).getName();
				
				provider.setParameter(StreamGmlWriter.PARAM_ROOT_ELEMENT_NAMESPACE, name.getNamespaceURI());
				provider.setParameter(StreamGmlWriter.PARAM_ROOT_ELEMENT_NAME, name.getLocalPart());
				return true;
			}
		}
		
		provider.setParameter(StreamGmlWriter.PARAM_ROOT_ELEMENT_NAMESPACE, null);
		provider.setParameter(StreamGmlWriter.PARAM_ROOT_ELEMENT_NAME, null);
		
		return false;
	}

	/**
	 * @see HaleWizardPage#createContent(Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		page.setLayout(new GridLayout(1, false));
		
		// add filter text
		filterText = new Text(page, SWT.SINGLE | SWT.BORDER);
        filterText.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
        filterText.setText(""); //$NON-NLS-1$
        
		// add filtered list
		list = new ListViewer(page, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL | 
				SWT.H_SCROLL);
		list.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof XmlElement) {
					QName name = ((XmlElement) element).getName();
					
					return name.getLocalPart() + " (" + name.getNamespaceURI() + ")";
				}
				if (element instanceof Definition) {
					return ((Definition<?>) element).getDisplayName();
				}
				return super.getText(element);
			}
			
		});
		list.setContentProvider(ArrayContentProvider.getInstance());
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
		layoutData.widthHint = SWT.DEFAULT;
		layoutData.heightHint = 8 * list.getList().getItemHeight();
		list.getControl().setLayoutData(layoutData);
		
        // page status update
        list.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				setPageComplete(!selection.isEmpty());
			}
		});
		
		// search filter & update
		list.addFilter(new ViewerFilter() {
			
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				String filter = filterText.getText();
				// handle empty filter
				if (filter == null || filter.isEmpty()) {
					return true;
				}
				
				if (element instanceof Definition) {
					Definition<?> def = (Definition<?>) element;
					filter = filter.toLowerCase();
					
					if (def.getDisplayName().toLowerCase().contains(filter)) {
						return true;
					}
				}
				
				return false;
			}
		});
		list.setComparator(new ViewerComparator());
		filterText.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				// refilter
				list.refresh();
			}
		});
	}
	
	/**
	 * @see DialogPage#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		
		filterText.setFocus();
	}

	/**
	 * @see AbstractConfigurationPage#disable()
	 */
	@Override
	public void disable() {
		// do nothing
	}

	/**
	 * @see AbstractConfigurationPage#enable()
	 */
	@Override
	public void enable() {
		updateList();
	}

	private void updateList() {
		//TODO instead of showing all elemets allow filtering for elements that can hold the type in some form?
		SchemaSpace schemas = getWizard().getProvider().getTargetSchema();
		XmlIndex index = StreamGmlWriter.getXMLIndex(schemas);
		//FIXME use filtered table for selection?
		list.setInput(index.getElements().values());
		setPageComplete(!list.getSelection().isEmpty());
	}
	
}
