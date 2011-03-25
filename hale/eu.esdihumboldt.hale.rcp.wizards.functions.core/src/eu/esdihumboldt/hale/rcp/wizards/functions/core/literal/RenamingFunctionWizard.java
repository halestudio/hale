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
package eu.esdihumboldt.hale.rcp.wizards.functions.core.literal;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.cst.align.ICell.RelationType;
import eu.esdihumboldt.cst.align.ext.IParameter;
import eu.esdihumboldt.cst.transformer.service.rename.RenameFeatureFunction;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.align.Entity;
import eu.esdihumboldt.goml.oml.ext.Parameter;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.goml.rdf.Resource;
import eu.esdihumboldt.hale.rcp.wizards.functions.AbstractSingleCellWizard;
import eu.esdihumboldt.hale.rcp.wizards.functions.AlignmentInfo;
import eu.esdihumboldt.hale.rcp.wizards.functions.core.Messages;
import eu.esdihumboldt.hale.rcp.wizards.functions.core.literal.RenamingFunctionWizardMainPage.InstanceMappingType;

/**
 * This {@link Wizard} is used to invoke a Renaming CstFunction for the Source
 * Feature Type
 * 
 * @author Anna Pitaev, Logica; Simon Templer, Fraunhofer IGD
 * @version $Id$
 */
public class RenamingFunctionWizard extends AbstractSingleCellWizard {

	/**
	 * Parameter name for the attribute to split/merge on
	 */
	public static final String PARAMETER_SELECTED_ATTRIBUTE = "SelectedAttribute"; //$NON-NLS-1$
	
	
	/**
	 * Parameter name for instance merge condition
	 */
	public static final String PARAMETER_INSTANCE_MERGE_CONDITION = "InstanceMergeCondition"; //$NON-NLS-1$

	/**
	 * Parameter name for instance split condition
	 */
	public static final String PARAMETER_INSTANCE_SPLIT_CONDITION = "InstanceSplitCondition"; //$NON-NLS-1$

	private static ALogger _log = ALoggerFactory.getLogger(RenamingFunctionWizard.class);

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
				Messages.RenamingFunctionWizard_3,
				Messages.RenamingFunctionWizard_4);
		super.setWindowTitle(Messages.RenamingFunctionWizard_5);
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
		_log.debug("Wizard.canFinish: " + this.mainPage.isPageComplete()); //$NON-NLS-1$
		return this.mainPage.isPageComplete();
	}

	/**
	 * @see Wizard#performFinish()
	 */

	@Override
	public boolean performFinish() {
		Cell c = getResultCell();
		
		Entity source = (Entity) c.getEntity1();
		
		Transformation t = new Transformation();
		
		if (getSourceItem().isType() && getTargetItem().isType()) {
			// Type renaming
			t.setLabel(RenameFeatureFunction.class.getName());
			t.setService(new Resource(RenameFeatureFunction.class.getName()));

			InstanceMappingType type = mainPage.getType();
			String condition = mainPage.getCondition();
			String selectedVariable = mainPage.getSelectedVariable();
			
			t.getParameters().add(new Parameter(PARAMETER_SELECTED_ATTRIBUTE, selectedVariable));
			
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
			
			source.setTransformation(t);
			
			return true;
		}
		else {
			_log.userError(Messages.RenamingFunctionWizard_6);
			return false;
		}
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
