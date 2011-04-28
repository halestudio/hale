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
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
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
		
		return false;
	}

	/**
	 * @see HaleWizardPage#createContent(Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		page.setLayout(new FillLayout());
		
		list = new FilteredList(page, SWT.SINGLE, new LabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof Definition) {
					return ((Definition) element).getDisplayName();
				}
				return super.getText(element);
			}
			
		}, true, true, true);
		
		//TODO need to add filter text etc.
		
		updateList();
	}
	
	private void updateList() {
		Schema targetSchema = getWizard().getTargetSchema();
		list.setElements(targetSchema.getAllElements().values().toArray());
	}
	
	//TODO enable/disable - react on provider changes?! or is onShowPage enough?

	/**
	 * @see HaleWizardPage#onShowPage()
	 */
	@Override
	protected void onShowPage() {
		
		// TODO Auto-generated method stub
		super.onShowPage();
	}

}
