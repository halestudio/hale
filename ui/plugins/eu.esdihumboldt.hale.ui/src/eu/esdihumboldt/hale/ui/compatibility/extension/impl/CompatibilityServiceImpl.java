/*
 * Copyright (c) 2013 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.compatibility.extension.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.ui.PlatformUI;

import de.cs3d.ui.util.eclipse.extension.exclusive.PreferencesExclusiveExtension;
import de.cs3d.util.eclipse.extension.AbstractConfigurationFactory;
import de.cs3d.util.eclipse.extension.AbstractExtension;
import de.cs3d.util.eclipse.extension.AbstractObjectFactory;
import de.cs3d.util.eclipse.extension.ExtensionObjectDefinition;
import de.cs3d.util.eclipse.extension.ExtensionObjectFactory;
import de.cs3d.util.eclipse.extension.ObjectExtension;
import eu.esdihumboldt.cst.internal.CSTCompatibilityMode;
import eu.esdihumboldt.hale.common.align.compatibility.CompatibilityMode;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.ui.compatibility.CompatibilityServiceListener;
import eu.esdihumboldt.hale.ui.compatibility.extension.CompatibilityModeFactory;
import eu.esdihumboldt.hale.ui.compatibility.extension.CompatibilityService;
import eu.esdihumboldt.hale.ui.internal.HALEUIPlugin;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.service.align.AlignmentServiceListener;

/**
 * Compatibility Service, main class and extension for handling all issues for
 * compatibility (e.g. CST or Xslt)
 * 
 * @author Sebastian Reinhardt
 */
public class CompatibilityServiceImpl extends
		PreferencesExclusiveExtension<CompatibilityMode, CompatibilityModeFactory> implements
		CompatibilityService {

	// stored listeners of the service
	private final CopyOnWriteArraySet<CompatibilityServiceListener> listeners = new CopyOnWriteArraySet<CompatibilityServiceListener>();

	CompatibilityAlignmentListener cal;

	/**
	 * Default factory based on a configuration element.
	 */
	private static class CompatibilityModeFactoryImpl extends
			AbstractConfigurationFactory<CompatibilityMode> implements CompatibilityModeFactory {

		/**
		 * Create an instance view factory based on a configuration element.
		 * 
		 * @param conf the configuration element
		 */
		protected CompatibilityModeFactoryImpl(IConfigurationElement conf) {
			super(conf, "class");
		}

		/**
		 * @see ExtensionObjectFactory#dispose(java.lang.Object)
		 */
		@Override
		public void dispose(CompatibilityMode instance) {
			// dispose is handled externally
		}

		/**
		 * @see ExtensionObjectDefinition#getIdentifier()
		 */
		@Override
		public String getIdentifier() {
			return conf.getAttribute("id");
		}

		/**
		 * @see ExtensionObjectDefinition#getDisplayName()
		 */
		@Override
		public String getDisplayName() {
			return conf.getAttribute("name");
		}

	}

	/**
	 * Default fallback factory
	 */
	private static class CompatibilityDefaultFactory extends
			AbstractObjectFactory<CompatibilityMode> implements CompatibilityModeFactory {

		/**
		 * @see ExtensionObjectFactory#createExtensionObject()
		 */
		@Override
		public CompatibilityMode createExtensionObject() throws Exception {
			return new CSTCompatibilityMode();
		}

		/**
		 * @see ExtensionObjectFactory#dispose(java.lang.Object)
		 */
		@Override
		public void dispose(CompatibilityMode instance) {
			// dispose is handled externally
		}

		/**
		 * @see ExtensionObjectDefinition#getIdentifier()
		 */
		@Override
		public String getIdentifier() {
			return "eu.esdihumboldt.hale.ui.compatibility";
		}

		/**
		 * @see ExtensionObjectDefinition#getDisplayName()
		 */
		@Override
		public String getDisplayName() {
			return ("CST Compatibility");
		}

		/**
		 * @see de.cs3d.util.eclipse.extension.ExtensionObjectDefinition#getTypeName()
		 */
		@SuppressWarnings("restriction")
		@Override
		public String getTypeName() {
			return CSTCompatibilityMode.class.getName();
		}

	}

	/**
	 * default service constructor, also adds a listener to the alignment
	 * service
	 */
	public CompatibilityServiceImpl() {
		super(new CompatibilityModeExtension(), HALEUIPlugin.getDefault().getPreferenceStore(),
				"compatibilityMode");
		cal = new CompatibilityAlignmentListener();
		((AlignmentService) PlatformUI.getWorkbench().getService(AlignmentService.class))
				.addListener(cal);
	}

	/**
	 * The extension ID
	 */
	public static final String EXTENSION_ID = "eu.esdihumboldt.hale.align.compatibility";

	/**
	 * {@link CompatibilityMode} extension
	 */
	public static class CompatibilityModeExtension extends
			AbstractExtension<CompatibilityMode, CompatibilityModeFactory> implements
			ObjectExtension<CompatibilityMode, CompatibilityModeFactory> {

		/**
		 * Default constructor
		 */
		public CompatibilityModeExtension() {
			super(EXTENSION_ID);
		}

		/**
		 * @see AbstractExtension#createFactory(IConfigurationElement)
		 */
		@Override
		protected CompatibilityModeFactory createFactory(IConfigurationElement conf)
				throws Exception {
			if (conf.getName().equals("compatibility")) {
				return new CompatibilityModeFactoryImpl(conf);
			}

			return null;
		}

	}

	/**
	 * @see de.cs3d.ui.util.eclipse.extension.exclusive.PreferencesExclusiveExtension#getFallbackFactory()
	 */
	@Override
	protected CompatibilityModeFactory getFallbackFactory() {
		return new CompatibilityDefaultFactory();
	}

	/**
	 * Compatibility Listener for the Alignment
	 * 
	 * @author Sebastian Reinhardt
	 */
	class CompatibilityAlignmentListener implements AlignmentServiceListener {

		List<Cell> incompatibleCells;

		/**
		 * 
		 */
		public CompatibilityAlignmentListener() {
			incompatibleCells = new ArrayList<Cell>();
		}

		/**
		 * @see eu.esdihumboldt.hale.ui.service.align.AlignmentServiceListener#alignmentCleared()
		 */
		@Override
		public void alignmentCleared() {
			incompatibleCells.clear();
			finish();
		}

		/**
		 * @see eu.esdihumboldt.hale.ui.service.align.AlignmentServiceListener#cellsAdded(java.lang.Iterable)
		 */
		@Override
		public void cellsAdded(Iterable<Cell> cells) {
			Iterator<Cell> cit = cells.iterator();
			while (cit.hasNext()) {
				Cell cell = cit.next();
				boolean isCompatibleNow = getCurrent().supportsFunction(
						cell.getTransformationIdentifier());

				if (!isCompatibleNow) {
					incompatibleCells.add(cell);
				}

			}
			finish();
		}

		/**
		 * @see eu.esdihumboldt.hale.ui.service.align.AlignmentServiceListener#cellReplaced(eu.esdihumboldt.hale.common.align.model.Cell,
		 *      eu.esdihumboldt.hale.common.align.model.Cell)
		 */
		@Override
		public void cellReplaced(Cell oldCell, Cell newCell) {
			if (incompatibleCells.contains(oldCell)) {
				incompatibleCells.remove(oldCell);
			}

			if (!getCurrent().supportsFunction(newCell.getTransformationIdentifier())) {
				incompatibleCells.add(newCell);
			}

			finish();
		}

		/**
		 * @see eu.esdihumboldt.hale.ui.service.align.AlignmentServiceListener#cellsRemoved(java.lang.Iterable)
		 */
		@Override
		public void cellsRemoved(Iterable<Cell> cells) {
			Iterator<Cell> cit = cells.iterator();
			while (cit.hasNext()) {
				Cell cell = cit.next();

				if (incompatibleCells.contains(cell)) {
					incompatibleCells.remove(cell);
				}
			}
			finish();
		}

		/**
		 * @see eu.esdihumboldt.hale.ui.service.align.AlignmentServiceListener#cellsPropertyChanged(java.lang.Iterable,
		 *      java.lang.String)
		 */
		@Override
		public void cellsPropertyChanged(Iterable<Cell> cells, String propertyName) {
			// Do nothing

		}

		/**
		 * Completes the checkup after the alignment changes were checked
		 */
		public void finish() {
			if (!incompatibleCells.isEmpty()) {
				for (CompatibilityServiceListener listener : listeners) {
					listener.compatibilityChanged(false, "Incompatibility detected!");
				}
			}

			if (incompatibleCells.isEmpty())
				for (CompatibilityServiceListener listener : listeners) {
					listener.compatibilityChanged(true, "No compatibility problems detected!");
				}
		}

		/**
		 * @see eu.esdihumboldt.hale.ui.service.align.AlignmentServiceListener#alignmentChanged()
		 */
		@Override
		public void alignmentChanged() {
			incompatibleCells.clear();
			Collection<? extends Cell> cells = ((AlignmentService) PlatformUI.getWorkbench()
					.getService(AlignmentService.class)).getAlignment().getCells();
			Iterator<? extends Cell> cit = cells.iterator();
			while (cit.hasNext()) {
				Cell cell = cit.next();
				boolean isCompatibleNow = getCurrent().supportsFunction(
						cell.getTransformationIdentifier());

				if (!isCompatibleNow) {
					incompatibleCells.add(cell);
				}

			}
			finish();

		}

	}

	/**
	 * @see eu.esdihumboldt.hale.ui.compatibility.extension.CompatibilityService#addCompatibilityListener(eu.esdihumboldt.hale.ui.compatibility.CompatibilityServiceListener)
	 */
	@Override
	public void addCompatibilityListener(CompatibilityServiceListener listener) {
		listeners.add(listener);

	}

	/**
	 * @see eu.esdihumboldt.hale.ui.compatibility.extension.CompatibilityService#removeCompatibilityListener(CompatibilityServiceListener
	 *      listener)
	 */
	@Override
	public void removeCompatibilityListener(CompatibilityServiceListener listener) {
		listeners.remove(listener);

	}

	/**
	 * @see eu.esdihumboldt.hale.ui.compatibility.extension.CompatibilityService#compatibilityModeChanged()
	 */
	@Override
	public void compatibilityModeChanged() {
		cal.alignmentChanged();
	}

}
