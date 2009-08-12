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
package eu.esdihumboldt.hale.rcp.views.map.style;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Style;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.hale.models.StyleService;
import eu.esdihumboldt.hale.rcp.HALEActivator;

/**
 * Dialog for editing feature type styles
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class FeatureStyleDialog extends MultiPageDialog<FeatureStylePage> {

	private static Image styleImage;
	
	private final FeatureType type;
	
	private final StyleService styles;
	
	private Style style;
	
	/**
	 * Creates a dialog for editing a feature type style
	 * 
	 * @param type
	 */
	public FeatureStyleDialog(final FeatureType type) {
		super();
		
		this.type = type;
		
		if (styleImage == null) {
			styleImage = AbstractUIPlugin.imageDescriptorFromPlugin(
					HALEActivator.PLUGIN_ID, "/icons/styles.gif").createImage();
		}
		
		setTitle("FeatureStyle: " + type.getName().getLocalPart());
		setImage(styleImage);
		
		styles = (StyleService) PlatformUI.getWorkbench().getService(StyleService.class);
	}
	
	/**
	 * @see MultiPageDialog#allowPageChange(IDialogPage, IDialogPage)
	 */
	@Override
	protected boolean allowPageChange(FeatureStylePage oldPage,
			FeatureStylePage newPage) {
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
				if (MessageDialog.openConfirm(getShell(), "Switch editor",
						"The current style is not valid, if you continue you will loose your changes."
						+ "\n\nError message:\n" + e.getMessage())) {
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
				setStyle(temp);
				return true;
			}
			else {
				return true;
			}
		}
	}

	/**
	 * @see MultiPageDialog#onPageChange(IDialogPage, IDialogPage)
	 */
	@Override
	protected void onPageChange(FeatureStylePage oldPage,
			FeatureStylePage newPage) {
		// ignore
	}

	/**
	 * @see Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		FeatureStylePage page = getCurrentPage();
		
		Style temp = null;
		try {
			temp = page.getStyle(true);
		} catch (Exception e) {
			MessageDialog.openError(getShell(), "Style error",
					"The current style is not valid, the following error occurred:\n\n"
					+ e.getMessage());
			return;
		}
		
		if (temp != null) {
			setStyle(temp);
		}
		
		styles.addStyles(style);
		super.okPressed();
	}

	/**
	 * Get the style
	 * 
	 * @return the style for the feature type
	 */
	public Style getStyle() {
		if (style == null) {
			setStyle(styles.getStyle(type));
		}
		
		return style;
	}
	
	/**
	 * Set the feature type style
	 * 
	 * @param style the style
	 */
	@SuppressWarnings("deprecation")
	public void setStyle(Style style) {
		// set the feature names
		for (FeatureTypeStyle fts : style.getFeatureTypeStyles()) {
			fts.setFeatureTypeName(type.getName().getLocalPart());
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
		addPage(new XMLStylePage3(this));
	}

	/**
	 * @return the type
	 */
	public FeatureType getType() {
		return type;
	}
	
}
