/*
 * Build configuration that is specific for HALE and HALE server applications
 * that should not be reused in other applications. 
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
		
		// Name of the Windows service executable (without extension), must be different
		// from the product's launcher name
		serviceExeName = 'hale_srvc'
		
		// Product aliases pointing to product file locations
		productAlias = [
			HALE: '../ui/plugins/eu.esdihumboldt.hale.ui.application/HALE.product',
			Infocenter: '../doc/plugins/eu.esdihumboldt.hale.doc.application/Infocenter.product',
			Server: '../server/plugins/eu.esdihumboldt.hale.server.application/Server.product',
			Templates: '../server/plugins/eu.esdihumboldt.hale.server.application/Templates.product'
		]
	}
}