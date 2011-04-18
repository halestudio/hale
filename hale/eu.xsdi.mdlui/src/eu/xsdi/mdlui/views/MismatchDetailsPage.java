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

import java.util.Arrays;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.ColumnLayoutData;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import eu.xsdi.mdl.model.Mismatch;

/**
 * The {@link IDetailsPage} implementation handling {@link Mismatch} objects.
 * 
 * @author Thorsten Reitz
 * @version $Id$ 
 * @since 0.1.0
 */
public class MismatchDetailsPage implements IDetailsPage {
	
	private FormToolkit toolkit;
	private ScrolledForm overviewForm;
	
	private Text textType;
	private Text textStatus;
	private Text textProvenance;
	private Text textComment;
	
	private TreeViewer reasonTreeViewer;
	private TreeViewer consequenceTreeViewer;
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.IDetailsPage#initialize(org.eclipse.ui.forms.IManagedForm)
	 */
	public void initialize(IManagedForm mform) {
		this.overviewForm = mform.getForm();
		this.toolkit = mform.getToolkit();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.IDetailsPage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createContents(Composite parent) {
		ColumnLayout layout = new ColumnLayout();
		layout.topMargin = 5;
		layout.leftMargin = 5;
		layout.rightMargin = 2;
		layout.bottomMargin = 2;
		layout.minNumColumns = 3;
		layout.maxNumColumns = 3;
		parent.setLayout(layout);
		
		overviewForm.setText("Mismatch Overview");
		overviewForm.getBody().setLayout(layout);
		
		this.createMismatchSection(parent);
		this.createReasonSection(parent);
		this.createConsequenceSection(parent);
	}
	
	@SuppressWarnings("unused")
	private void createMismatchSection(Composite parent) {
		// create "Mismatch" Section
		Section sectionMismatch = toolkit.createSection(parent, 
				  Section.DESCRIPTION|Section.TITLE_BAR|
				  Section.TWISTIE|Section.EXPANDED);
		ColumnLayoutData cd = new ColumnLayoutData();
		sectionMismatch.setLayoutData(cd);
		sectionMismatch.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				overviewForm.reflow(true);
			}
		});
		sectionMismatch.setText("Mismatch");
		sectionMismatch.setDescription("General information on this mismatch.");
		
		Composite sectionMismatchClient = toolkit.createComposite(sectionMismatch);
		sectionMismatchClient.setLayout(new GridLayout(2, false));
		Label labelType = toolkit.createLabel(sectionMismatchClient, "Type:");
		this.textType = toolkit.createText(sectionMismatchClient, "");
		this.textType.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		this.textType.setEditable(false);
		
		
		Label labelProvenance = toolkit.createLabel(sectionMismatchClient, "Provenance:");
		this.textProvenance  = toolkit.createText(sectionMismatchClient, "");
		this.textProvenance.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		this.textProvenance.setEditable(false);
		
		Label labelStatus = toolkit.createLabel(sectionMismatchClient, "Status:");
		this.textStatus = toolkit.createText(sectionMismatchClient, "");
		this.textStatus.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		
		Label labelComment = toolkit.createLabel(sectionMismatchClient, "Comments:");
		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		labelComment.setLayoutData(gd);
		this.textComment = toolkit.createText(sectionMismatchClient, "", SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		gd = new GridData();
		gd.horizontalSpan = 2;
		gd.grabExcessVerticalSpace = true;
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.FILL;
		gd.heightHint = 40;
		textComment.setLayoutData(gd);
		
		toolkit.paintBordersFor(sectionMismatchClient);
		sectionMismatch.setClient(sectionMismatchClient);
	}

	private void createReasonSection(Composite parent) {
		// create "Reason" Section
		Section sectionReason = toolkit.createSection(parent, 
				  Section.DESCRIPTION|Section.TITLE_BAR|
				  Section.TWISTIE|Section.EXPANDED);
		ColumnLayoutData cd = new ColumnLayoutData();
		sectionReason.setLayoutData(cd);
		sectionReason.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				overviewForm.reflow(true);
			}
		});
		sectionReason.setText("Reason");
		sectionReason.setDescription("Why this mismatch occurs.");
		Composite sectionReasonClient = toolkit.createComposite(sectionReason);
	
		ColumnLayout reasonColumnLayout = new ColumnLayout();
		reasonColumnLayout.maxNumColumns = 1;
		sectionReasonClient.setLayout(reasonColumnLayout);
		Tree treeReason = toolkit.createTree(sectionReasonClient, SWT.NULL);
		treeReason.setLayout(new ColumnLayout());
		ColumnLayoutData treeLayoutData = new ColumnLayoutData(50, 123);
		treeReason.setLayoutData(treeLayoutData);
		toolkit.paintBordersFor(sectionReasonClient);
		sectionReason.setClient(sectionReasonClient);
		
		this.reasonTreeViewer = new TreeViewer(treeReason);
		this.reasonTreeViewer.setContentProvider(new ReasonContentProvider());
		this.reasonTreeViewer.setLabelProvider(new ReasonLabelProvider());
	}
	
	private void createConsequenceSection(Composite parent) {
		// create "Consequence" Section
		Section sectionConsequence = toolkit.createSection(parent, 
				  Section.DESCRIPTION|Section.TITLE_BAR|
				  Section.TWISTIE|Section.EXPANDED);
		ColumnLayoutData cd = new ColumnLayoutData();
		sectionConsequence.setLayoutData(cd);
		sectionConsequence.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				overviewForm.reflow(true);
			}
		});
		sectionConsequence.setText("Consequence(s)");
		sectionConsequence.setDescription("What impacts this mismatch has.");
		Composite sectionConsequenceClient = toolkit.createComposite(sectionConsequence);
		
		ColumnLayout consequenceColumnLayout = new ColumnLayout();
		consequenceColumnLayout.maxNumColumns = 1;
		sectionConsequenceClient.setLayout(consequenceColumnLayout);
		Tree treeConsequence = toolkit.createTree(sectionConsequenceClient, SWT.NULL);
		treeConsequence.setLayout(new ColumnLayout());
		ColumnLayoutData treeLayoutData = new ColumnLayoutData(50, 123);
		treeConsequence.setLayoutData(treeLayoutData);
		toolkit.paintBordersFor(sectionConsequenceClient);
		sectionConsequence.setClient(sectionConsequenceClient);
		
		this.consequenceTreeViewer = new TreeViewer(treeConsequence);
		this.consequenceTreeViewer.setContentProvider(new ConsequenceContentProvider());
		this.consequenceTreeViewer.setLabelProvider(new ConsequenceLabelProvider());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.IFormPart#commit(boolean)
	 */
	@Override
	public void commit(boolean onSave) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.IFormPart#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.IFormPart#isDirty()
	 */
	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.IFormPart#isStale()
	 */
	@Override
	public boolean isStale() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.IFormPart#refresh()
	 */
	@Override
	public void refresh() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.IFormPart#setFocus()
	 */
	@Override
	public void setFocus() {
		this.overviewForm.setFocus();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.IFormPart#setFormInput(java.lang.Object)
	 */
	@Override
	public boolean setFormInput(Object input) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.IPartSelectionListener#selectionChanged(org.eclipse.ui.forms.IFormPart, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {
		IStructuredSelection ssel = (IStructuredSelection) selection;
		Mismatch input = null;
		if (ssel.size() == 1) {
			input = (Mismatch) ssel.getFirstElement();
			this.textType.setText(input.getType().toString());
			this.textStatus.setText(input.getStatus().toString());
			if (input.getProvenance() != null) {
				this.textProvenance.setText(input.getProvenance());
			}
			if (input.getComment() != null) {
				this.textComment.setText(input.getComment());
			}
			if (input.getReason() != null) {
				this.reasonTreeViewer.setInput(
						Arrays.asList(new Object[]{input.getReason()}));
			}
			if (input.getConsequences() != null) {
				this.consequenceTreeViewer.setInput(input.getConsequences());
			}
		}
		
	}

}
