// Fraunhofer Institute for Computer Graphics Research (IGD)
// Department Graphical Information Systems (GIS)
//
// Copyright (c) 2004-2013 Fraunhofer IGD. All rights reserved.
//
// This source code is property of the Fraunhofer IGD and underlies
// copyright restrictions. It may only be used with explicit
// permission from the respective owner.

import java.util.jar.JarFile
import java.util.zip.ZipFile
import org.eclipse.osgi.util.ManifestElement
import org.gradle.api.Project
import org.osgi.framework.Constants

class UnitTestRunner {
    private Project project
    private static final String osgiTestBundleSymbolicName = 'de.cs3d.util.osgi.test'
    private static final String osgiTestRunnerClass = 'de.cs3d.util.osgi.test.OSGITestRunner'
    private static final String osgiTestMaxMemory = '256M'

    UnitTestRunner(Project project) {
        this.project = project
    }

    /**
     * Tries to find a unique path that matches the given pattern.
     * Alphabetically sorts all paths and uses the last one. This should give
     * you the one with the highest version number, for example.
     * Throws an exception with the given message if the path was not found.
    */
    def findUniquePath(dir, pattern, msg) {
        def candidates = project.fileTree(dir: dir, include: pattern).files.toList().sort()
        if (candidates.isEmpty()) {
            throw new IllegalStateException(msg)
        } else {
            candidates[candidates.size - 1]
        }
    }

    /**
     * Reads the symbolic name from a bundle jar
     */
    def readBundleJarSymbolicName(bundle) {
        InputStream manifestStream = null
        ZipFile jarFile = null
        try {
            jarFile = new ZipFile(bundle, ZipFile.OPEN_READ)
            def manifestEntry = jarFile.getEntry(JarFile.MANIFEST_NAME)
            if (manifestEntry == null) {
                throw new IllegalStateException('Test bundle contains no MANIFEST.MF: ' + bundle)
            }

            manifestStream = jarFile.getInputStream(manifestEntry)
            def map = ManifestElement.parseBundleManifest(manifestStream, null)
            def bsn = map.get(Constants.BUNDLE_SYMBOLICNAME)
            if (bsn == null) {
                throw new IllegalStateException('Test bundle contains no symbolic name: ' + bundle)
            }
            return bsn.split(';')[0]
        } finally {
            if (manifestStream != null) {
                manifestStream.close()
            }
            if (jarFile != null) {
                jarFile.close()
            }
        }
    }

    /**
     * Runs the unit tests in the given bundles. This method does not
     * fail the build if one unit test fails.
     */
    def doRunUnitTests(bundles, updateSitePath, osgiTestBundle, equinoxBundle) {
        def testReportOutputPath = new File(project.rootDir, 'target/testReports')
        project.delete(testReportOutputPath)

        // read symbolic names of test bundles
        def symbolicNames = bundles.collect { readBundleJarSymbolicName(it) }

        // create output directory for test reports
        testReportOutputPath.mkdirs()

        // create args to run OSGi tests
        def runnerargs = [
            '-bundlePath', updateSitePath,
            '-defaultLaunchFile', project.ext.defaultUnitTestLaunchConfiguration,
            '-formatter', 'xml',
            '-todir', testReportOutputPath.path
        ]

        runnerargs += symbolicNames

        def errorFile = new File(project.buildDir, 'hs_err_pid%p.log')

        try {
            project.javaexec {
                classpath = project.files(equinoxBundle, osgiTestBundle)
                main = osgiTestRunnerClass
                args = runnerargs
                maxHeapSize = osgiTestMaxMemory
		jvmArgs = ['-XX:ErrorFile=${errorFile.absolutePath}']
            }
        } catch (e) {
            // do not fail the whole build if one unit test fails
            println 'Error executing OSGi test runner'
            e.printStackTrace()
        }
    }

    def executeUnitTests(testBundlePattern) {
        // get path to exported update site (see task buildUpdateSite)
        def updateSitePath = new File(project.buildDir, 'de.cs3d.updatesite/target/repository/plugins')

        // look for OSGi test bundle
        def osgiTestBundle = findUniquePath(updateSitePath, osgiTestBundleSymbolicName + '_*.jar',
            'Could not find OSGi test bundle: ' + osgiTestBundleSymbolicName)

        // look for bundle containing the equinox launcher
        def equinoxBundle = findUniquePath(updateSitePath, 'org.eclipse.osgi_*.jar',
            'Could not find bundle org.eclipse.osgi in update site')

        def testBundles = project.fileTree(dir: updateSitePath, include: testBundlePattern) - project.files(osgiTestBundle)
        doRunUnitTests(testBundles, updateSitePath, osgiTestBundle, equinoxBundle)
    }
}
