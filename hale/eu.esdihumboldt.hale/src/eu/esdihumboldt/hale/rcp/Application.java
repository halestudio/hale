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
package eu.esdihumboldt.hale.rcp;

import java.net.URL;

import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.geotools.referencing.CRS;
import org.geotools.util.logging.Log4JLoggerFactory;
import org.geotools.util.logging.Logging;

/**
 * This class controls all aspects of the application's execution
 * 
 * @author Thorsten Reitz
 */
public class Application implements IApplication {
	
	private static Logger _log = Logger.getLogger(Application.class);
	
	private static String basepath;

	/**
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
	public Object start(IApplicationContext context) {
		// set up log4j logger manually if necessary
		if (!Logger.getRootLogger().getAllAppenders().hasMoreElements()) {
			Appender appender = new ConsoleAppender(
					new PatternLayout("%d{ISO8601} %5p %C{1}:%L %m%n"), 
					ConsoleAppender.SYSTEM_OUT );
			appender.setName("A1");
			Logger.getRootLogger().addAppender(appender);
			
			_log.info("No Logging configuration available, setting up " +
					"programmatically.");
		}
		
		// set up log4j logger for GeoTools
		Logging.ALL.setLoggerFactory(Log4JLoggerFactory.getInstance());
		Logger.getLogger(Log4JLogger.class).setLevel(Level.WARN);
		// provide information on HALE version to console.
		_log.info("HALE Version: " 
				+ HALEActivator.getDefault().getBundle().getHeaders().get(
						"Bundle-Version"));
		Logger.getRootLogger().setLevel(Level.WARN);
		
		// init HSQL database
		try {
			CRS.decode("EPSG:4326");
		} catch (Exception e) {
			_log.error("Error while initializing epsg database", e);
		}
		
		// find base path of the application.
		URL location = this.getClass().getProtectionDomain().getCodeSource().getLocation();
		String location_path = location.getPath().replace(" ", "+");
		location_path = location_path.replace("bin/", "");
		_log.debug(location_path);
		Application.basepath = location_path;
		
		// initialize UI
		Display display = PlatformUI.createDisplay();
		try {
			int returnCode = PlatformUI.createAndRunWorkbench(
					display, new ApplicationWorkbenchAdvisor());
			if (returnCode == PlatformUI.RETURN_RESTART) {
				return IApplication.EXIT_RESTART;
			}
			return IApplication.EXIT_OK;
		} finally {
			display.dispose();
		}
		
	}

	/**
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	public void stop() {
		final IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench == null)
			return;
		final Display display = workbench.getDisplay();
		display.syncExec(new Runnable() {
			public void run() {
				if (!display.isDisposed())
					workbench.close();
			}
		});
	}
	
	public static String getBasePath() {
		return Application.basepath;
	}
}