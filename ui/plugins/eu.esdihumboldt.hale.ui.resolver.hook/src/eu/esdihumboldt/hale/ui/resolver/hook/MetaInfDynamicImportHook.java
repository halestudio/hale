/*
 * Copyright (c) 2026 wetransform GmbH
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.esdihumboldt.hale.ui.resolver.hook;

import java.util.Collection;

import org.osgi.framework.hooks.resolver.ResolverHook;
import org.osgi.framework.namespace.PackageNamespace;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.resource.Namespace;

/**
 * Prevents dynamic imports (<code>DynamicImport-Package: *</code>) from being
 * wired to packages below <code>META-INF</code>.
 *
 * org.eclipse.jdt.core.compiler.batch (required by the help system since
 * Eclipse 2023-12) exports the package <code>META-INF.services</code>. Once a
 * bundle with <code>DynamicImport-Package: *</code> (e.g. the JAXB runtime or
 * Groovy) looks up a service file it does not contain itself, the package is
 * wired to that export, and from then on the bundle's own
 * <code>META-INF/services</code> files are no longer visible. The JAXB runtime
 * then is no longer found by {@link java.util.ServiceLoader} (via Aries SPI
 * Fly), and hale cannot load the alignment of a project.
 */
public class MetaInfDynamicImportHook implements ResolverHook {

	@Override
	public void filterResolvable(Collection<BundleRevision> candidates) {
		// no filtering
	}

	@Override
	public void filterSingletonCollisions(BundleCapability singleton,
			Collection<BundleCapability> collisionCandidates) {
		// no filtering
	}

	@Override
	public void filterMatches(BundleRequirement requirement, Collection<BundleCapability> candidates) {
		if (isDynamicPackageImport(requirement)) {
			candidates.removeIf(MetaInfDynamicImportHook::isMetaInfPackage);
		}
	}

	@Override
	public void end() {
		// nothing to clean up
	}

	/**
	 * @param requirement the requirement
	 * @return if the requirement is a dynamic package import
	 */
	static boolean isDynamicPackageImport(BundleRequirement requirement) {
		return PackageNamespace.PACKAGE_NAMESPACE.equals(requirement.getNamespace())
				&& PackageNamespace.RESOLUTION_DYNAMIC
						.equals(requirement.getDirectives().get(Namespace.REQUIREMENT_RESOLUTION_DIRECTIVE));
	}

	/**
	 * @param capability the package capability
	 * @return if the capability is a package below <code>META-INF</code>
	 */
	static boolean isMetaInfPackage(BundleCapability capability) {
		Object name = capability.getAttributes().get(PackageNamespace.PACKAGE_NAMESPACE);
		return name instanceof String pkg && (pkg.equals("META-INF") || pkg.startsWith("META-INF."));
	}

}
