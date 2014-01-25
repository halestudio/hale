// Fraunhofer Institute for Computer Graphics Research (IGD)
// Department Graphical Information Systems (GIS)
//
// Copyright (c) 2004-2013 Fraunhofer IGD. All rights reserved.
//
// This source code is property of the Fraunhofer IGD and underlies
// copyright restrictions. It may only be used with explicit
// permission from the respective owner.

import groovy.text.GStringTemplateEngine
import org.gradle.api.Project

class PomGenerator {
    private Project project

    PomGenerator(Project project) {
        this.project = project
    }

    /**
     * Creates a generic pom file for an OSGi bundle using the specified packaging
     */
    def makePluginPomFileWithPackaging(symbolicName, version, needsScala, needsGroovy, packaging, path) {
        makePomFileWithPackaging(symbolicName, version, needsScala, needsGroovy, packaging, 'pom-plugin.xml', path)
    }

    /**
     * Creates a generic pom file for an OSGi bundle using the specified packaging and template file
     */
    def makePomFileWithPackaging(symbolicName, version, needsScala, needsGroovy, packaging, templateName, path) {
        // calculate relative path to pom.xml of parent project
        def relativePath = path.path.substring(project.ext.rootDir.path.length() + 1).replace('\\', '/')
        relativePath = relativePath.replaceAll(/.+?\//, '../')
        relativePath = relativePath.replaceFirst(/\/[^\/]+$/, '/../pom.xml')
		
        new File(path, 'pom.xml').withWriter { w ->
            def template = new GStringTemplateEngine().createTemplate(new File('templates', templateName))
            def result = template.make([
                'groupId': project.group,
                'artifactId': symbolicName,
                'version': version,
                'packaging': packaging,
                'parentGroupId': project.group,
                'parentArtifactId': project.ext.parentArtifactId,
                'parentVersion': project.version + project.ext.versionSuffix,
                'parentRelativePath': relativePath,
                'needsScala': needsScala,
				'needsGroovy': needsGroovy
            ]).toString()
            w << result
        }
    }

    /**
     * Creates a generic pom file for an OSGi bundle
     */
    def makePomFile(symbolicName, version, needsScala, needsGroovy, path) {
        def packaging = 'eclipse-plugin'
        if (symbolicName.endsWith('.test')) {
            packaging = 'eclipse-test-plugin'
        }
        if (project.ext.generateArtifacts) {
            packaging = 'jar'
        }
        version += project.ext.versionSuffix
        makePluginPomFileWithPackaging(symbolicName, version, needsScala, needsGroovy, packaging, path)
    }

    /**
     * Generate pom.xml files for all bundles
     */
    def generatePomFiles() {
        // generate pom files for all bundles
        def parsedBundles = new BundleParser(project).getParsedBundles()
        println('Generating pom.xml files ...')

        parsedBundles.each {
            makePomFile(it.key, it.value.version, it.value.needsScala, it.value.needsGroovy, it.value.path)
        }

        // generate pom file for target platform
        new File(project.ext.platformBundle, 'pom.xml').withWriter { w ->
            def template = new GStringTemplateEngine().createTemplate(new File('templates', 'pom-platform.xml'))
            def result = template.make([
                    'groupId': project.group,
                    'version': project.version + project.ext.versionSuffix,
                    'parentGroupId': project.group,
                    'parentArtifactId': project.ext.parentArtifactId,
                    'parentVersion': project.version + project.ext.versionSuffix,
					'platformClassifier': project.ext.platformFileName
            ]).toString()
            w << result
        }
    }

    /**
     * Generates a pom file for the parent project and adds the given modules. The
     * modules must be passed in the same format as the hash returned by BundleParser#getParsedBundles()
     */
    def generateParentPomFile(additionalModules = [:]) {
        new File(project.ext.rootDir, 'pom.xml').withWriter { w ->
            def template = new GStringTemplateEngine().createTemplate(new File('templates', 'pom-parent.xml'))
            def bundles = new BundleParser(project).getParsedBundles()
            def result = template.make([
                    'groupId': project.group,
                    'version': project.version + project.ext.versionSuffix,
                    'parentGroupId': project.group,
                    'parentArtifactId': project.ext.parentArtifactId,
                    'parentVersion': project.version + project.ext.versionSuffix,
                    'modules': (bundles + additionalModules).values().collect {
                        it.path.path.substring(project.ext.rootDir.path.length() + 1).replace('\\', '/')
                    }.sort(),
                    'envOs': project.ext.osgiOS,
                    'envWs': project.ext.osgiWS,
                    'envArch': project.ext.osgiArch,
                    'generateArtifacts': project.ext.generateArtifacts,
					'platformClassifier': project.ext.platformFileName
            ]).toString()
            w << result
        }
    }
}
