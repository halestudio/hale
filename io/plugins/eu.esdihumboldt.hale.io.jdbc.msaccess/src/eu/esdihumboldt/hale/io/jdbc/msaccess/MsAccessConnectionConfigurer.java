package eu.esdihumboldt.hale.io.jdbc.msaccess;

import java.sql.Connection;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.io.jdbc.extension.ConnectionConfigurer;
import net.ucanaccess.jdbc.UcanaccessConnection;

/**
 * Enables extension on the given {@link Connection}.
 */
public class MsAccessConnectionConfigurer implements ConnectionConfigurer<UcanaccessConnection> {

	private static final ALogger log = ALoggerFactory.getLogger(MsAccessConnectionConfigurer.class);

	@Override
	public void configureConnection(UcanaccessConnection connection) {

		try {

			// TODO :: Extension to MS access.
		} catch (Exception e) {
			log.warn("Fail to load MS access extension. Please check help", e);
		}

	}

}
