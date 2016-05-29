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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.ui.PlatformUI;

import de.fhg.igd.eclipse.util.extension.AbstractConfigurationFactory;
import de.fhg.igd.eclipse.util.extension.AbstractExtension;
import de.fhg.igd.eclipse.util.extension.AbstractObjectFactory;
import de.fhg.igd.eclipse.util.extension.ObjectExtension;
import eu.esdihumboldt.cst.internal.CSTCompatibilityMode;
import eu.esdihumboldt.hale.common.align.compatibility.CompatibilityMode;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.filter.definition.CQLFilterDefinition;
import eu.esdihumboldt.hale.common.filter.definition.ECQLFilterDefinition;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.common.service.compatibility.CompatibilityModeFactory;
import eu.esdihumboldt.hale.ui.common.service.compatibility.CompatibilityService;
import eu.esdihumboldt.hale.ui.common.service.compatibility.CompatibilityServiceListener;
import eu.esdihumboldt.hale.ui.compatibility.ProjectExclusiveExtension;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.service.align.AlignmentServiceListener;

/**
 * Compatibility Service, main class and extension for handling all issues for
 * compatibility (e.g. CST or Xslt)
 * 
 * @author Sebastian Reinhardt
 */
@SuppressWarnings("restriction")
public class CompatibilityServiceImpl
		extends ProjectExclusiveExtension<CompatibilityMode, CompatibilityModeFactory>
		implements CompatibilityService {

	// stored listeners of the service
	private final CopyOnWriteArraySet<CompatibilityServiceListener> listeners = new CopyOnWriteArraySet<CompatibilityServiceListener>();

	private final CompatibilityAlignmentListener cal;

	/**
	 * Default factory based on a configuration element.
	 */
	private static class CompatibilityModeFactoryImpl extends
			AbstractConfigurationFactory<CompatibilityMode>implements CompatibilityModeFactory {

		/**
		 * Create an instance view factory based on a configuration element.
		 * 
		 * @param conf the configuration element
		 */
		protected CompatibilityModeFactoryImpl(IConfigurationElement conf) {
			super(conf, "class");
		}

		@Override
		public void dispose(CompatibilityMode instance) {
			// dispose is handled externally
		}

		@Override
		public String getIdentifier() {
			return conf.getAttribute("id");
		}

		@Override
		public String getDisplayName() {
			return conf.getAttribute("name");
		}

		@Override
		public Set<String> getSupportedFilters() {
			Set<String> result = new HashSet<>();

			for (IConfigurationElement filter : conf.getChildren("supportsFilter")) {
				String id = filter.getAttribute("ref");
				if (id != null) {
					result.add(id);
				}
			}

			return result;
		}

	}

	/**
	 * Default fallback factory
	 */
	private static class CompatibilityDefaultFactory
			extends AbstractObjectFactory<CompatibilityMode>implements CompatibilityModeFactory {

		@Override
		public CompatibilityMode createExtensionObject() throws Exception {
			return new CSTCompatibilityMode();
		}

		@Override
		public void dispose(CompatibilityMode instance) {
			// dispose is handled externally
		}

		@Override
		public String getIdentifier() {
			return CSTCompatibilityMode.ID;
		}

		@Override
		public String getDisplayName() {
			return ("CST Compatibility");
		}

		@Override
		public String getTypeName() {
			return CSTCompatibilityMode.class.getName();
		}

		@Override
		public Set<String> getSupportedFilters() {
			Set<String> result = new HashSet<>();

			result.add(CQLFilterDefinition.ID);
			result.add(ECQLFilterDefinition.ID);

			return result;
		}

	}

	/**
	 * default service constructor, also adds a listener to the alignment
	 * service
	 */
	public CompatibilityServiceImpl() {
		super(new CompatibilityModeExtension(), "compatibilityMode");
		cal = new CompatibilityAlignmentListener();
		PlatformUI.getWorkbench().getService(AlignmentService.class).addListener(cal);

		this.addListener(
				new ExclusiveExtensionListener<CompatibilityMode, CompatibilityModeFactory>() {

					@Override
					public void currentObjectChanged(final CompatibilityMode arg0,
							final CompatibilityModeFactory arg1) {
						compatibilityModeChanged();
					}

				});
	}

	/**
	 * The extension ID
	 */
	public static final String EXTENSION_ID = "eu.esdihumboldt.hale.align.compatibility";

	/**
	 * {@link CompatibilityMode} extension
	 */
	public static class CompatibilityModeExtension
			extends AbstractExtension<CompatibilityMode, CompatibilityModeFactory>
			implements ObjectExtension<CompatibilityMode, CompatibilityModeFactory> {

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

	@Override
	protected String getDefaultId() {
		return CSTCompatibilityMode.ID;
	}

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
			CompatibilityMode mode = getCurrent();

			Iterator<Cell> cit = cells.iterator();
			while (cit.hasNext()) {
				Cell cell = cit.next();
				boolean isCompatibleNow = mode.supportsFunction(cell.getTransformationIdentifier(),
						HaleUI.getServiceProvider()) && mode.supportsCell(cell);

				if (!isCompatibleNow) {
					incompatibleCells.add(cell);
				}

			}
			finish();
		}

		/**
		 * @see eu.esdihumboldt.hale.ui.service.align.AlignmentServiceListener#cellsReplaced(Map)
		 */
		@Override
		public void cellsReplaced(Map<? extends Cell, ? extends Cell> cells) {
			CompatibilityMode mode = getCurrent();

			for (Entry<? extends Cell, ? extends Cell> e : cells.entrySet()) {
				if (incompatibleCells.contains(e.getKey())) {
					incompatibleCells.remove(e.getKey());
				}

				if (!mode.supportsFunction(e.getValue().getTransformationIdentifier(),
						HaleUI.getServiceProvider())
						|| !mode.supportsCell(e.getValue())) {
					incompatibleCells.add(e.getValue());
				}
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
					listener.compatibilityChanged(false, incompatibleCells);
				}
			}

			if (incompatibleCells.isEmpty())
				for (CompatibilityServiceListener listener : listeners) {
					listener.compatibilityChanged(true, null);
				}
		}

		@Override
		public void customFunctionsChanged() {
			// re-evaluate all cells
			alignmentChanged();
		}

		/**
		 * @see eu.esdihumboldt.hale.ui.service.align.AlignmentServiceListener#alignmentChanged()
		 */
		@Override
		public void alignmentChanged() {
			incompatibleCells.clear();
			Collection<? extends Cell> cells = PlatformUI.getWorkbench()
					.getService(AlignmentService.class).getAlignment().getCells();
			Iterator<? extends Cell> cit = cells.iterator();
			CompatibilityMode mode = getCurrent();
			while (cit.hasNext()) {
				Cell cell = cit.next();
				boolean isCompatibleNow = mode.supportsFunction(cell.getTransformationIdentifier(),
						HaleUI.getServiceProvider()) && mode.supportsCell(cell);

				if (!isCompatibleNow) {
					incompatibleCells.add(cell);
				}

			}
			finish();

		}

	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.service.compatibility.CompatibilityService#addCompatibilityListener(eu.esdihumboldt.hale.ui.common.service.compatibility.CompatibilityServiceListener)
	 */
	@Override
	public void addCompatibilityListener(CompatibilityServiceListener listener) {
		listeners.add(listener);

	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.service.compatibility.CompatibilityService#removeCompatibilityListener(CompatibilityServiceListener
	 *      listener)
	 */
	@Override
	public void removeCompatibilityListener(CompatibilityServiceListener listener) {
		listeners.remove(listener);

	}

	/**
	 * called when the mode is changed (externally, e.g. through user at the ui)
	 */
	public void compatibilityModeChanged() {
		cal.alignmentChanged();
	}

}
