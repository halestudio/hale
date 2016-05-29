/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */
package eu.esdihumboldt.hale.ui.style.dialog;

import java.text.MessageFormat;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.geotools.feature.NameImpl;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Style;

import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.common.service.style.StyleService;
import eu.esdihumboldt.hale.ui.style.StyleHelper;
import eu.esdihumboldt.hale.ui.style.internal.InstanceStylePlugin;
import eu.esdihumboldt.hale.ui.style.internal.Messages;
import eu.esdihumboldt.hale.ui.util.dialog.MultiPageDialog;

/**
 * Dialog for editing feature type styles.
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class FeatureStyleDialog extends MultiPageDialog<FeatureStylePage> {

	private static final int APPLY_ID = IDialogConstants.CLIENT_ID + 1;

	private static Image styleImage;

	private final TypeDefinition type;

	private final StyleService styles;

	private final DataSet dataSet;

	private Style style;

	/**
	 * Creates a dialog for editing a feature type style
	 * 
	 * @param type the type definition
	 * @param dataSet the type data set
	 */
	public FeatureStyleDialog(final TypeDefinition type, DataSet dataSet) {
		super();

		this.type = type;
		this.dataSet = dataSet;

		if (styleImage == null) {
			styleImage = InstanceStylePlugin.getImageDescriptor("/icons/styles.gif").createImage(); //$NON-NLS-1$
		}

		setTitle(MessageFormat.format(Messages.FeatureStyleDialog_Title,
				type.getName().getLocalPart()));
		setImage(styleImage);

		styles = PlatformUI.getWorkbench().getService(StyleService.class);
	}

	/**
	 * Get the type data set.
	 * 
	 * @return the data set
	 */
	public DataSet getDataSet() {
		return dataSet;
	}

	/**
	 * @see MultiPageDialog#allowPageChange(IDialogPage, IDialogPage)
	 */
	@Override
	protected boolean allowPageChange(FeatureStylePage oldPage, FeatureStylePage newPage) {
		if (oldPage == null) {
			return true;
		}
		else if (newPage == null) {
			return false;
		}
		else {
			Style temp;
			try {
				temp = oldPage.getStyle(false);
			} catch (Exception e) {
				if (MessageDialog
						.openConfirm(getShell(), Messages.FeatureStyleDialog_SwitchStyleTitle,
								MessageFormat.format(
										Messages.FeatureStyleDialog_SwitchStyleDescription,
										e.getMessage()))) {
					// revert changes
					temp = null;
				}
				else {
					// keep page
					return false;
				}
			}

			if (temp != null) {
				// set style
				if (MessageDialog.openQuestion(getShell(),
						Messages.FeatureStyleDialog_SwitchStyleTitle2,
						Messages.FeatureStyleDialog_SwitchStyleDescription2)) {
					setStyle(temp);
				}
				return true;
			}
			else {
				return true;
			}
		}
	}

	/**
	 * @see Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// create OK and cancel
		super.createButtonsForButtonBar(parent);

		// create apply
		createButton(parent, APPLY_ID, Messages.FeatureStyleDialog_ApplyButtonText, false);
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
	 */
	@Override
	protected void buttonPressed(int buttonId) {
		switch (buttonId) {
		case APPLY_ID:
			applyPressed();
			break;
		default:
			super.buttonPressed(buttonId);
		}
	}

	/**
	 * @see MultiPageDialog#onPageChange(IDialogPage, IDialogPage)
	 */
	@Override
	protected void onPageChange(FeatureStylePage oldPage, FeatureStylePage newPage) {
		// ignore
	}

	/**
	 * @see Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		if (apply()) {
			super.okPressed();
		}
	}

	/**
	 * Called when the apply button was pressed
	 */
	protected void applyPressed() {
		apply();
	}

	private boolean apply() {
		FeatureStylePage page = getCurrentPage();

		Style temp = null;
		try {
			temp = page.getStyle(true);
		} catch (Exception e) {
			MessageDialog.openError(getShell(), Messages.FeatureStyleDialog_ErrorMessageTitle,
					MessageFormat.format(Messages.FeatureStyleDialog_ErrorMessageDescription,
							e.getMessage()));
			return false;
		}

		if (temp != null) {
			setStyle(temp);
		}

		styles.addStyles(style);

		return true;
	}

	/**
	 * Get the style
	 * 
	 * @return the style for the feature type
	 */
	public Style getStyle() {
		if (style == null) {
			setStyle(styles.getStyle(type, dataSet));
		}

		return style;
	}

	/**
	 * Set the feature type style
	 * 
	 * @param style the style
	 */
	public void setStyle(Style style) {
		// set the feature names
		for (FeatureTypeStyle fts : style.featureTypeStyles()) {
			fts.featureTypeNames().clear();
			fts.featureTypeNames().add(new NameImpl(StyleHelper.getFeatureTypeName(type)));
		}

		this.style = style;
	}

	/**
	 * @see MultiPageDialog#createPages()
	 */
	@Override
	protected void createPages() {
		addPage(new RuleStylePage(this));
		addPage(new SimpleLineStylePage(this));
		addPage(new SimplePointStylePage(this));
		addPage(new SimplePolygonStylePage(this));
		addPage(new SimpleGraphicStylePage(this));
		addPage(new XMLStylePage3(this));
	}

	/**
	 * @return the type
	 */
	public TypeDefinition getType() {
		return type;
	}

}
