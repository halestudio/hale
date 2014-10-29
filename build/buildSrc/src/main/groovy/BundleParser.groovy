// Fraunhofer Institute for Computer Graphics Research (IGD)
// Department Spatial Information Management (GEO)
//
// Copyright (c) 2013-2014 Fraunhofer IGD.
//
// This file is part of hale-build.
//
// hale-build is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// hale-build is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with hale-build.  If not, see <http://www.gnu.org/licenses/>.

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
    def readVersion(manifestFile, boolean bundleVersion = false) {
        def map = ManifestElement.parseBundleManifest(new FileInputStream(manifestFile), null)
        def version = map.get(Constants.BUNDLE_VERSION).split(';')[0]
		if (bundleVersion) {
			version
		}
		else {
			formatVersion(version)
		}
    }
	
	/**
	 * Format a version how it should be represented in a POM file.
	 */
	String formatVersion(String version) {
		if (version.endsWith('.qualifier')) {
			// turn qualifiers into SNAPSHOTS
			version[0..-11] + '-SNAPSHOT'
		}
		else {
			// return as-is
			version
		}
	}
	
	/**
	 * Tries to detect the bundle's Java version from the manifest.
	 * If detection fails, it returns the default version.
	 */
	def readJavaVersion(manifestFile) {
		def map = ManifestElement.parseBundleManifest(new FileInputStream(manifestFile), null)
		def env = map.get(Constants.BUNDLE_REQUIREDEXECUTIONENVIRONMENT)
		if (env) {
			String envStr = env as String
			if (env.startsWith('JavaSE-')) {
				return env.substring(7)
			}
			if (env.startsWith('OSGi/Minimum-')) {
				return env.substring(13)
			}
		}
		
		//TODO also check possible instruction in build.properties?
	
		return project.ext.defaultJavaVersion
	}

    /**
     * Checks if a plugin in the given path has the Scala nature.
     */
    def needsScala(path) {
        hasNature(path, 'org.scala-ide.sdt.core.scalanature')
    }
	
	/**
	 * Checks if a plugin in the given path has the Groovy nature.
	 */
	def needsGroovy(path) {
		hasNature(path, 'org.eclipse.jdt.groovy.core.groovyNature')
	}
	
	/**
	 * Checks if a plugin in the given path has the Java nature.
	 */
	def isJavaProject(path) {
		hasNature(path, 'org.eclipse.jdt.core.javanature')
	}
	
	/**
	 * Checks if a plugin in the given path has a specific nature.
	 */
	def hasNature(path, String nature) {
		def xml = new XmlSlurper().parse(new File(path, '.project'))
		return xml.natures.nature.any { it.text() == nature }
	}
	
	/**
	 * Get's a plugin's build properties.
	 */
	def getBuildProperties(path) {
		def props = new Properties()
		new File(path, 'build.properties').withReader { r ->
			props.load(r)
		}
		props
	}
	
	/**
	 * Determines if a plugin requires the Twirl compiler.
	 */
	def needsTwirl(path) {
		def props = getBuildProperties(path)
		Boolean.parseBoolean(props.getProperty('extras.twirl', 'false').trim())
	}
	
	/**
	 * Determines if a workspace project should be included in the update site.
	 */
	boolean acceptProject(def path, def sname) {
		// skip projects that are explicitly excluded
		if (project.ext.excludeBundles?.any { it == sname})
			return false
		
		// skip projects that don't have the plugin nature
		if (!hasNature(path, 'org.eclipse.pde.PluginNature'))
			return false
			
		// skip projects where the specified OS does not match the build
		def os = project.ext.osSpecificBundles[sname]
		if (os && !os.any { it == project.ext.osgiOS}) {
			// OS information is there but does not match
			return false
		}
			
		true
	}
	
	/**
	 * Determines if a workspace feature should be included.
	 */
	boolean acceptFeature(def path, def sname) {
		// skip features that are explicitly excluded
		if (project.ext.excludeBundles?.any { it == sname })
			return false
		
		// skip features where the specified OS does not match the build
		def os = project.ext.osSpecificBundles[sname]
		if (os && !os.any { it == project.ext.osgiOS}) {
			// OS information is there but does not match
			return false
		}
			
		true
	}

    private def getParsedBundlesTraverse(dir) {
        dir.listFiles().each { path ->
            def manifestPath = new File(path, 'META-INF/MANIFEST.MF')
            if (path.isDirectory()) {
                if (manifestPath.exists()) {
                    def sname = readSymbolicName(manifestPath)
                    if (!project.ext.parsedBundles.containsKey(sname)) {
						// check if the project is valid / should be part of the update site
						if (acceptProject(path, sname)) {
	                        project.ext.parsedBundles[sname] = [
	                                'version': readVersion(manifestPath),
	                                'path': path,
	                                'needsScala': needsScala(path),
									'needsGroovy': needsGroovy(path),
									'isJavaProject': isJavaProject(path),
									'javaVersion': readJavaVersion(manifestPath),
									'needsTwirl': needsTwirl(path)
	                        ]
						}
						else {
							println "Skipping project at $path ($sname)"
						}
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
	
	private def getParsedFeaturesTraverse(dir) {
		dir.listFiles().each { path ->
			if (path.isDirectory()) {
				def featureXmlPath = new File(path, 'feature.xml')
				if (featureXmlPath.exists()) {
					// read feature information
					def feature = new groovy.util.XmlSlurper().parse(featureXmlPath)
					
					def id = feature.@id as String
					if (!project.ext.parsedFeatures.containsKey(id)) {
						if (acceptFeature(path, id)) {
							project.ext.parsedFeatures[id] = [
									'version': formatVersion(feature.@version as String),
									'path': path,
									'label': feature.@id as String
							]
						}
						else {
							println "Skipping feature at $path ($id)"
						}
					} else {
						throw new IllegalStateException("Duplicate feature ID ${id}")
					}
				} else {
					getParsedFeaturesTraverse(path)
				}
			}
		}
	}
	
	def getParsedFeatures() {
		if (!project.ext.properties.containsKey("parsedFeatures")) {
			project.ext.parsedFeatures = [:]
			for (b in project.ext.bundles) {
				getParsedFeaturesTraverse(b)
			}
		}

		return project.ext.parsedFeatures
	}
}
