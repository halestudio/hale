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
package eu.esdihumboldt.hale.rcp.wizards.functions.literal;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;

import eu.esdihumboldt.cst.align.ext.IParameter;
import eu.esdihumboldt.cst.align.ICell.RelationType;
import eu.esdihumboldt.cst.transformer.impl.RenameAttributeTransformer;
import eu.esdihumboldt.cst.transformer.impl.RenameFeatureTransformer;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.align.Entity;
import eu.esdihumboldt.goml.oml.ext.Parameter;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.goml.rdf.Resource;
import eu.esdihumboldt.hale.rcp.wizards.functions.AbstractSingleCellWizard;
import eu.esdihumboldt.hale.rcp.wizards.functions.AlignmentInfo;
import eu.esdihumboldt.hale.rcp.wizards.functions.literal.RenamingFunctionWizardMainPage.InstanceMappingType;

/**
 * This {@link Wizard} is used to invoke a Renaming CstFunction for the Source
 * Feature Type
 * 
 * @author Anna Pitaev, Logica; Simon Templer, Fraunhofer IGD
 * @version $Id$
 */
public class RenamingFunctionWizard extends AbstractSingleCellWizard {

	/**
	 * Parameter name for instance merge condition
	 */
	public static final String PARAMETER_INSTANCE_MERGE_CONDITION = "InstanceMergeCondition";

	/**
	 * Parameter name for instance split condition
	 */
	public static final String PARAMETER_INSTANCE_SPLIT_CONDITION = "InstanceSplitCondition";

	private static Logger _log = Logger.getLogger(RenamingFunctionWizard.class);

	private RenamingFunctionWizardMainPage mainPage;

	/**
	 * @see AbstractSingleCellWizard#AbstractSingleCellWizard(AlignmentInfo)
	 */
	public RenamingFunctionWizard(AlignmentInfo selection) {
		super(selection);
	}

	/**
	 * @see AbstractSingleCellWizard#init()
	 */
	@Override
	protected void init() {
		this.mainPage = new RenamingFunctionWizardMainPage(
				"Configure Renaming Function",
				"Configure Renaming Function");
		super.setWindowTitle("Configure Function");
		super.setNeedsProgressMonitor(true);
		
		// initialize from cell
		Cell cell = getResultCell();
		if (cell.getEntity1().getTransformation() != null) {
			String condition = null;
			InstanceMappingType type = InstanceMappingType.NORMAL;
			
			List<IParameter> parameters = cell.getEntity1().getTransformation().getParameters();
			if (parameters != null) {
				Iterator<IParameter> it = parameters.iterator();
				while (condition == null && it.hasNext()) {
					IParameter param = it.next();
					if (param.getValue() != null) {
						if (param.getName().equals(PARAMETER_INSTANCE_MERGE_CONDITION)) {
							condition = param.getValue();
							type = InstanceMappingType.MERGE;
						}
						else if (param.getName().equals(PARAMETER_INSTANCE_SPLIT_CONDITION)) {
							condition = param.getValue();
							type = InstanceMappingType.SPLIT;
						}
					}
				}
			}
			
			mainPage.setType(type);
			mainPage.setInitialCondition(condition);
		}
	}
	
	/**
	 * @see Wizard#canFinish()
	 */
	@Override
	public boolean canFinish() {
		_log.debug("Wizard.canFinish: " + this.mainPage.isPageComplete());
		return this.mainPage.isPageComplete();
	}

	/**
	 * @see Wizard#performFinish()
	 */

	@Override
	public boolean performFinish() {
		Cell c = (Cell) getResultCell();
		
		Entity source = (Entity) c.getEntity1();
		Entity target = (Entity) c.getEntity2();
		
		Transformation t = new Transformation();
		
		if (getSourceItem().isFeatureType() && getTargetItem().isFeatureType()) {
			// Type renaming
			t.setLabel(RenameFeatureTransformer.class.getName());
			t.setService(new Resource(RenameFeatureTransformer.class.getName()));

			InstanceMappingType type = mainPage.getType();
			String condition = mainPage.getCondition();
			
			switch (type) {
			case SPLIT:
				t.getParameters().add(new Parameter(PARAMETER_INSTANCE_SPLIT_CONDITION, condition));
				break;
			case MERGE:
				t.getParameters().add(new Parameter(PARAMETER_INSTANCE_MERGE_CONDITION, condition));
				break;
			case NORMAL: // fall through
			default:
				// do nothing
			}
			c.setRelation(RelationType.Equivalence);
		}
		else if (getSourceItem().isAttribute() && getTargetItem().isAttribute()) {
			// Attribute renaming
			t.setLabel(RenameAttributeTransformer.class.getName());
			t.setService(new Resource(RenameFeatureTransformer.class.getName()));
			
			//Add old attribute name
			t.getParameters().add(new Parameter(
					RenameAttributeTransformer.OLD_ATTRIBUTE_NAME_PARAMETER, 
					source.getAbout().getAbout()));
			t.getParameters().add(new Parameter(
					RenameAttributeTransformer.NEW_ATTRIBUTE_NAME_PARAMETER, 
					target.getAbout().getAbout()));
		}
		else {
			//TODO error message?
			return false;
		}

		source.setTransformation(t);

		return true;
	}

	/**
	 * @see IWizard#addPages()
	 */
	@Override
	public void addPages() {
		super.addPages();
		addPage(mainPage);
	}

}
