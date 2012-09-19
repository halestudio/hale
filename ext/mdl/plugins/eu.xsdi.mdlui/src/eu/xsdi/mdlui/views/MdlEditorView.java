/*
 * LICENSE: This program is being made available under the LGPL 3.0 license.
 * For more information on the license, please read the following:
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * 
 * For additional information on the Model behind Mismatches, please refer to
 * the following publication(s):
 * Thorsten Reitz (2010): A Mismatch Description Language for Conceptual Schema 
 * Mapping and Its Cartographic Representation, Geographic Information Science,
 * http://www.springerlink.com/content/um2082120r51232u/
 */
package eu.xsdi.mdlui.views;


import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;

import eu.xsdi.mdl.model.Mismatch;
import eu.xsdi.mdlui.MdlUiPlugin;


/**
 * This view provides editing and analysis functionality for {@link Mismatch}es.
 * It follows the Master-Detail pattern, with a selection tree of the Mismatch,
 * its Reason and Consequences being selected in a tree in the master part.
 * 
 * @author Thorsten Reitz
 * @version $Id$ 
 * @since 0.1.0
 */
public class MdlEditorView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "eu.xsdi.mdlui.views.MdlEditorView";
	
	private FormToolkit formToolkit;
	private ScrolledForm overviewForm; 
	private MismatchPropertiesBlock block;
	private ManagedForm managedForm;

	/**
	 * The constructor.
	 */
	public MdlEditorView() {
		
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		
		this.formToolkit = new FormToolkit(parent.getDisplay());
		this.overviewForm = this.formToolkit.createScrolledForm(parent);
		this.managedForm = new ManagedForm(this.formToolkit, this.overviewForm);
		this.block = new MismatchPropertiesBlock();
	
		this.overviewForm.setText("Mismatch Overview"); //$NON-NLS-1$
		this.overviewForm.setToolTipText("Select a Mismatch and get overview " +
	    		"information.");
		
		block.createContent(this.managedForm);
		this.block.setContentWeight(new int[]{25, 75});
		
		this.overviewForm.setBackgroundImage(MdlUiPlugin.getDefault().getImage(
				MdlUiPlugin.IMG_FORM_BG));

	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		this.overviewForm.setFocus();
	}

	/*
	 * The content provider class is responsible for providing objects to the
	 * view. It can wrap existing objects in adapters or simply return objects
	 * as-is. These objects may be sensitive to the current input of the view,
	 * or ignore it and always show the same content (like Task List, for
	 * example).
	 */
	class ViewContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}

		public Object[] getElements(Object parent) {
			return new String[] { "One", "Two", "Three" };
		}
	}

	class ViewLabelProvider extends LabelProvider implements
			ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			return getText(obj);
		}

		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}

		public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().getSharedImages().getImage(
					ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	class NameSorter extends ViewerSorter {
	}
}
