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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import eu.esdihumboldt.commons.goml.omwg.FeatureClass;
import eu.esdihumboldt.commons.goml.omwg.Property;
import eu.esdihumboldt.commons.goml.rdf.About;
import eu.xsdi.mdl.model.Consequence;
import eu.xsdi.mdl.model.Mismatch;
import eu.xsdi.mdl.model.Mismatch.MismatchType;
import eu.xsdi.mdl.model.MismatchCell;
import eu.xsdi.mdl.model.Reason;
import eu.xsdi.mdl.model.Reason.EntityCharacteristic;
import eu.xsdi.mdl.model.reason.ReasonCondition;
import eu.xsdi.mdl.model.reason.ReasonRule;
import eu.xsdi.mdl.model.reason.ReasonSet;

/**
 * This {@link MasterDetailsBlock} implementation allows to edit {@link Mismatch} objects.
 * 
 * @author Thorsten Reitz
 * @version $Id$ 
 * @since 0.1.0
 */
public class MismatchPropertiesBlock extends MasterDetailsBlock {
	
	/**
	 * Default Constructor.
	 */
	public MismatchPropertiesBlock() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.MasterDetailsBlock#createMasterPart(org.eclipse.ui.forms.IManagedForm, org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createMasterPart(final IManagedForm managedForm, Composite parent) {
		FormToolkit toolkit = managedForm.getToolkit();
		
		Section section = toolkit.createSection(parent, 
				Section.DESCRIPTION|Section.TITLE_BAR);
		section.setText("Mismatches"); //$NON-NLS-1$
		section.setDescription(
				"Select the Mismatch or one of it's children to view and edit it."); //$NON-NLS-1$
		section.marginWidth = 10;
		section.marginHeight = 5;
		
		Composite client = toolkit.createComposite(section, SWT.WRAP);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		client.setLayout(layout);
		Tree tree = toolkit.createTree(client, SWT.NULL);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 20;
		gd.widthHint = 250;
		tree.setLayoutData(gd);
		toolkit.paintBordersFor(client);
		section.setClient(client);
		
		final SectionPart spart = new SectionPart(section);
		managedForm.addPart(spart);
		TreeViewer mismatchTreeViewer = new TreeViewer(tree);
		mismatchTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				managedForm.fireSelectionChanged(spart, event.getSelection());
			}
		});
		mismatchTreeViewer.setContentProvider(new MismatchTreeContentProvider());
		mismatchTreeViewer.setLabelProvider(new MismatchTreeLabelProvider());
		mismatchTreeViewer.setInput(getDummyCell());
	}

	private Object getDummyCell() {
		Mismatch a = new Mismatch(MismatchType.AggregationLevelMismatch);
		a.setReason(new Reason(
				new FeatureClass(new About("FC1")), 
				new FeatureClass(new About("FC2")),
				EntityCharacteristic.AssociationCardinalityConstraint));

		a.getConsequences().add(new Consequence());
		a.getConsequences().add(new Consequence());
		a.getConsequences().add(new Consequence());
		a.setProvenance("HALE MDL Inference Engine");
		
		Mismatch b = new Mismatch(MismatchType.StructureMismatch);
		b.setReason(new Reason(
				new Property(new About("Prop1Name")), 
				new Property(new About("Prop2Name")),
				EntityCharacteristic.AttributeTypeConstraint));
		b.getReason().setReasonRule(new ReasonRule(
				new ReasonSet("Prop1Name"), 
				new ReasonSet("Prop2Name")));
		List<ReasonCondition> conditions = new ArrayList<ReasonCondition>();
		conditions.add(new ReasonCondition("attributeName", "attributeFilterValue", null));
		b.getReason().getReasonRule().getSet1().setConditions(conditions );
		b.getReason().getReasonRule().getSet1().setSubSet(new ReasonSet("SubProp1Name"));
		b.getConsequences().add(new Consequence());
		b.setProvenance("User");
		
		Mismatch c = new Mismatch(MismatchType.AbstractionMismatch);
		c.setReason(new Reason(
				new Property(new About("Prop3Name")), 
				new Property(new About("Prop4Name")),
				EntityCharacteristic.AttributeDerivedPopulatedValues));
		c.getReason().setReasonRule(new ReasonRule(
				new ReasonSet("Prop3Name"), 
				new ReasonSet("Prop4Name")));
		c.getConsequences().add(new Consequence());
		c.getConsequences().add(new Consequence());
		c.setProvenance("HALE MDL Inference Engine");
		
		MismatchCell mc = new MismatchCell();
		mc.getMismatches().add(a);
		mc.getMismatches().add(b);
		mc.getMismatches().add(c);
		return mc;
	}
	
	/**
	 * @see org.eclipse.ui.forms.MasterDetailsBlock#applyLayoutData(SashForm)
	 */
	@Override
	protected void applyLayoutData(SashForm sashForm) {
		sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	/**
	 * @see org.eclipse.ui.forms.MasterDetailsBlock#createToolBarActions(org.eclipse.ui.forms.IManagedForm)
	 */
	@Override
	protected void createToolBarActions(IManagedForm managedForm) {
		// TODO Auto-generated method stub

	}

	/**
	 * @see org.eclipse.ui.forms.MasterDetailsBlock#registerPages(org.eclipse.ui.forms.DetailsPart)
	 */
	@Override
	protected void registerPages(DetailsPart detailsPart) {
		detailsPart.registerPage(Mismatch.class, new MismatchDetailsPage());
		detailsPart.registerPage(Reason.class, new ReasonDetailsPage());
		detailsPart.registerPage(Consequence.class, new ConsequenceDetailsPage());
	}

	/**
	 * @param weights the int array of weights
	 */
	public void setContentWeight(int[] weights) {
		super.sashForm.setWeights(weights);
	}

}
