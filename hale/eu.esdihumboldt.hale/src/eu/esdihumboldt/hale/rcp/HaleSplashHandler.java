/**
 * 
 */
package eu.esdihumboldt.hale.rcp;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.splash.AbstractSplashHandler;

/**
 * @author Administrator
 *
 */
public class HaleSplashHandler 
	extends AbstractSplashHandler {

	/**
	 * 
	 */
	public HaleSplashHandler() {
		super();
	}
	
	@Override
	public void init(Shell splash) {
		splash.setBackgroundImage(new Image(null, Application.getBasePath() + "icons/splash.png")); //$NON-NLS-1$
	}

}
