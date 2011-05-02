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

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.FilteredList;
import org.opengis.feature.type.Name;

import eu.esdihumboldt.hale.core.io.IOProvider;
import eu.esdihumboldt.hale.instance.io.InstanceWriter;
import eu.esdihumboldt.hale.io.gml.writer.internal.StreamGmlWriter;
import eu.esdihumboldt.hale.schemaprovider.Schema;
import eu.esdihumboldt.hale.schemaprovider.model.Definition;
import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;
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

	private FilteredList list;

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
		Object[] sel = list.getSelection();
		
		if (sel != null && sel.length > 0) {
			Object selected = sel[0];
			
			if (selected instanceof SchemaElement) {
				Name name = ((SchemaElement) selected).getElementName();
				
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
		final Text filterText = new Text(page, SWT.SINGLE | SWT.BORDER);
        filterText.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
        filterText.setText(""); //$NON-NLS-1$
        
		// add filtered list
		list = new FilteredList(page, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL | 
				SWT.H_SCROLL, new LabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof Definition) {
					return ((Definition) element).getDisplayName();
				}
				return super.getText(element);
			}
			
		}, true, true, true);
		list.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		// add listeners to filter text
        Listener listener = new Listener() {
            public void handleEvent(Event e) {
                list.setFilter(filterText.getText());
            }
        };
        filterText.addListener(SWT.Modify, listener);

        filterText.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {
                if (e.keyCode == SWT.ARROW_DOWN) {
					list.setFocus();
				}
            }

            public void keyReleased(KeyEvent e) {
            	// do nothing
            }
        });
        
        // page status update
        list.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				setPageComplete(list.getSelectionIndex() != -1);
			}
		});
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
		Schema targetSchema = getWizard().getTargetSchema();
		list.setElements(targetSchema.getAllElements().values().toArray());
		setPageComplete(list.getSelectionIndex() != -1);
	}
	
}
