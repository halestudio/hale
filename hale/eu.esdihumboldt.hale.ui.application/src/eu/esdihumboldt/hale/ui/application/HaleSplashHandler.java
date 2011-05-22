/**
 * 
 */
package eu.esdihumboldt.hale.ui.application;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.splash.AbstractSplashHandler;

/**
 * HALE splash handler
 * @author Thorsten Reitz
 */
public class HaleSplashHandler 
	extends AbstractSplashHandler {

	/**
	 * Default constructor
	 */
	public HaleSplashHandler() {
		super();
	}
	
	@Override
	public void init(Shell splash) {
		splash.setBackgroundImage(new Image(null, Application.getBasePath() + "images/splash.png")); //$NON-NLS-1$
	}

}
