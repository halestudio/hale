/*
 * Build configuration that is specific for HALE and HALE server applications. 
 */
project = {
	// version
	version = '2.9.0'
	
	ext {
		// The title of the win32 installer
	    win32InstallerTitle = 'HALE'
	
	    // The GUID used by the win32 installer for product upgrades (must never change!)
	    win32InstallerUpgradeGUID = '6b6151c0-e3f9-11de-8a39-0800200c9a66'
	
	    // The GUID for the shortcuts the installer will create
	    win32InstallerShortcutGUID = '76ed8ea0-e3f9-11de-8a39-0800200c9a66'
	
	    // Name of the Windows service
	    serviceName = 'HALE Server'
		
		// path to default unit test launch configuration
		defaultUnitTestLaunchConfiguration = file('../common/plugins/eu.esdihumboldt.hale.common.core.test/defaultUnitTestLaunchConfiguration.launch')
		
		// Name of the Windows service executable (without extension), must be different
		// from the product's launcher name
		serviceExeName = 'hale_srvc'
	}
}