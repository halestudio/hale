// Fraunhofer Institute for Computer Graphics Research (IGD)
// Department Graphical Information Systems (GIS)
//
// Copyright (c) 2004-2013 Fraunhofer IGD. All rights reserved.
//
// This source code is property of the Fraunhofer IGD and underlies
// copyright restrictions. It may only be used with explicit
// permission from the respective owner.

import groovy.text.GStringTemplateEngine

import org.eclipse.osgi.util.ManifestElement
import org.gradle.api.Project
import org.osgi.framework.Constants

class BundleParser {
    private Project project

    BundleParser(Project project) {
        this.project = project
    }

    /**
     * Reads a bundle's symbolic name from its MANIFEST.MF file
     */
    def readSymbolicName(manifestFile) {
        def map = ManifestElement.parseBundleManifest(new FileInputStream(manifestFile), null)
        return map.get(Constants.BUNDLE_SYMBOLICNAME).split(';')[0]
    }

    /**
     * Reads a bundle's version from its MANIFEST.MF file
     */
    def readVersion(manifestFile) {
        def map = ManifestElement.parseBundleManifest(new FileInputStream(manifestFile), null)
        return map.get(Constants.BUNDLE_VERSION).split(';')[0]
    }

    /**
     * Checks if a plugin in the given path has the Scala nature
     */
    def needsScala(path) {
        def xml = new XmlSlurper().parse(new File(path, '.project'))
        return xml.natures.nature.any { it.text() == 'org.scala-ide.sdt.core.scalanature' }
    }
	
	/**
	 * Checks if a plugin in the given path has the Groovy nature
	 */
	def needsGroovy(path) {
		def xml = new XmlSlurper().parse(new File(path, '.project'))
		return xml.natures.nature.any { it.text() == 'org.eclipse.jdt.groovy.core.groovyNature' }
	}

    private def getParsedBundlesTraverse(dir) {
        dir.listFiles().each { path ->
            def manifestPath = new File(path, 'META-INF/MANIFEST.MF')
            if (path.isDirectory()) {
                if (manifestPath.exists()) {
                    def sname = readSymbolicName(manifestPath)
                    if (!project.ext.parsedBundles.containsKey(sname)) {
                        project.ext.parsedBundles[sname] = [
                                'version': readVersion(manifestPath),
                                'path': path,
                                'needsScala': needsScala(path),
								'needsGroovy': needsGroovy(path)
                        ]
                    } else {
                        throw new IllegalStateException("Duplicate symbolic bundle name ${sname}")
                    }
                } else {
                    getParsedBundlesTraverse(path)
                }
            }
        }
    }

    /**
     * Returns a map of all bundles' symbolic names, their versions and paths
     * Format:
     * [
     *   <symbolicName1>: [
     *     'version': <version>,
     *     'path': <path>,
     *     'needScala': <true/false>
     *   ],
     *   <symbolicName2>: ...
     * ]
     */
    def getParsedBundles() {
        if (!project.ext.properties.containsKey("parsedBundles")) {
            println('Parsing bundles ...')
            project.ext.parsedBundles = [:]
            for (b in project.ext.bundles) {
                getParsedBundlesTraverse(b)
            }
        }

        return project.ext.parsedBundles
    }
}
