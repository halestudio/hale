/*
 * Build configuration that is specific for HALE and HALE server applications
 * that should not be reused in other applications.
 */
project = {
	// version
	// major.minor.micro-SNAPSHOT - remove -SNAPSHOT for releases
	version = '5.0.1-SNAPSHOT'

	// when increasing the version number, at least the version of the .application bundles
	// should be increased so they state an updated application version

	ext {
		// The title of the win32 installer
	    win32InstallerTitle = 'hale»studio'

	    // The GUID used by the win32 installer for product upgrades (must never change!)
	    win32InstallerUpgradeGUID = '6b6151c0-e3f9-11de-8a39-0800200c9a66'

	    // The GUID for the shortcuts the installer will create
	    win32InstallerShortcutGUID = '76ed8ea0-e3f9-11de-8a39-0800200c9a66'

	    // Name of the Windows service
	    serviceName = 'HALE Server'

		// Name of the Windows service executable (without extension), must be different
		// from the product's launcher name
		serviceExeName = 'hale_srvc'

		// test product
		testProduct = '../common/plugins/eu.esdihumboldt.hale.common.test/Tests.product'
		testProductLauncher = 'hale_tests'

		// Product aliases pointing to product file locations
		productAlias = [
			HALE: '../ui/plugins/eu.esdihumboldt.hale.ui.application/HALE.product',
			Infocenter: '../doc/plugins/eu.esdihumboldt.hale.doc.application/Infocenter.product',
			Server: '../server/plugins/eu.esdihumboldt.hale.server.application/Server.product',
			Templates: '../server/plugins/eu.esdihumboldt.hale.server.application/Templates.product'
		]

		// Docker image settings
		productImages = [
			Infocenter: 'wetransform/hale-infocenter'
		]
	}
}
