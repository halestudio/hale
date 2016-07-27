/*
 * Copyright (c) 2016 Fraunhofer IGD
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
 *     Fraunhofer IGD <http://www.igd.fraunhofer.de/>
 */
package de.fhg.igd.mapviewer.server.file;

import java.lang.reflect.Constructor;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.mapviewer.PixelConverter;
import org.jdesktop.swingx.mapviewer.TileProvider;

/**
 * Factory for {@link PixelConverter} that can be configured with a
 * {@link Properties} object
 *
 * @author <a href="mailto:simon.templer@igd.fhg.de">Simon Templer</a>
 *
 * @version $Id$
 */
public abstract class PropertiesConverterFactory {

	private static final Log log = LogFactory.getLog(PropertiesConverterFactory.class);

	/** converter class property name */
	public static final String PROP_CONVERTER_CLASS = "converterClass"; //$NON-NLS-1$

	/**
	 * Creates a {@link PixelConverter} for the given {@link TileProvider} from
	 * the given properties
	 * 
	 * @param properties the properties that specify the {@link PixelConverter}
	 * @param tileProvider the {@link TileProvider} for to be associated with
	 *            the {@link PixelConverter}
	 * 
	 * @return a {@link PixelConverter} or null if the properties didn't specify
	 *         a valid converter
	 */
	@SuppressWarnings("unchecked")
	public static PixelConverter createConverter(Properties properties, TileProvider tileProvider) {
		String className = properties.getProperty(PROP_CONVERTER_CLASS);

		if (className == null) {
			log.error("Didn't find " + PROP_CONVERTER_CLASS + " property"); //$NON-NLS-1$ //$NON-NLS-2$
			return null;
		}
		else {
			Class<?> converterClass;
			try {
				converterClass = Class.forName(className);

				Constructor<? extends PixelConverter> constructor;

				// try Properties/TileProvider constructor
				try {
					constructor = (Constructor<? extends PixelConverter>) converterClass
							.getConstructor(Properties.class, TileProvider.class);
					log.info(
							"Found converter constructor with Properties and TileProvider arguments"); //$NON-NLS-1$
					return constructor.newInstance(properties, tileProvider);
				} catch (Throwable e) {
					// ignoring
				}

				// try TileProvider/Properties constructor
				try {
					constructor = (Constructor<? extends PixelConverter>) converterClass
							.getConstructor(TileProvider.class, Properties.class);
					log.info(
							"Found converter constructor with TileProvider and Properties arguments"); //$NON-NLS-1$
					return constructor.newInstance(tileProvider, properties);
				} catch (Throwable e) {
					// ignoring
				}

				// try Properties constructor
				try {
					constructor = (Constructor<? extends PixelConverter>) converterClass
							.getConstructor(Properties.class);
					log.info("Found converter constructor with Properties argument"); //$NON-NLS-1$
					return constructor.newInstance(properties);
				} catch (Throwable e) {
					// ignoring
				}

				// try TileProvider constructor
				try {
					constructor = (Constructor<? extends PixelConverter>) converterClass
							.getConstructor(TileProvider.class);
					log.info("Found converter constructor with TileProvider argument"); //$NON-NLS-1$
					return constructor.newInstance(tileProvider);
				} catch (Throwable e) {
					// ignoring
				}

				// try Default constructor
				try {
					constructor = (Constructor<? extends PixelConverter>) converterClass
							.getConstructor();
					log.info("Found converter default constructor"); //$NON-NLS-1$
					return constructor.newInstance();
				} catch (Throwable e) {
					// ignoring
				}

				log.warn("Found no supported constructor: " + className); //$NON-NLS-1$
			} catch (ClassNotFoundException e) {
				log.error("Converter class not found", e); //$NON-NLS-1$
			}
		}

		return null;
	}

}
