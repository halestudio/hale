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

import org.gradle.api.Project
import org.apache.maven.cli.MavenCli

class Helper {
	
	/**
	 * Resolve a template file or folder.
	 */
	static def resolveTemplate(Project project, String name) {
		//TODO look also in alternative locations?
		new File(new File(project.projectDir, 'templates'), name)
	}
	
    /**
     * @return true if we're building for a 64 bit platform
     */
    static def buildFor64bit(Project project) {
        return project.ext.osgiArch == 'x86_64'
    }

    /**
     * @return true if we're building for Windows
     */
    static def buildForWindows(Project project) {
        return project.ext.osgiOS == 'win32'
    }

    /**
     * @return true if we're building for Linux
     */
    static def buildForLinux(Project project) {
        return project.ext.osgiOS == 'linux'
    }
	
	/**
	 * @return true if we're building for Mac OS X
	 */
	static def buildForMac(Project project) {
		return project.ext.osgiOS == 'macosx'
	}

    static int runMaven(List<String> args, File workingDir, Map<String, String> systemProperties = null) {
        // determine if embedded Maven should be used
        // for now defaults to embedded if not provided (as this was the original behavior)
        def useEmbedded = System.getenv('HALE_BUILD_MAVEN_EMBEDDED')?.toLowerCase() != 'false'

        if (useEmbedded) {
            if (systemProperties) {
                systemProperties.each { key, value ->
                    System.setProperty(key, value)
                }
            }

            System.out.println "Running Maven embedded with arguments ${args.join(' ')}"
            return new MavenCli().doMain(args as String[], workingDir.absolutePath, System.out, System.err)
        }
        else {
            System.out.println "Running Maven from command line with arguments ${args.join(' ')}"
            def command = ['mvn']
            if (systemProperties) {
                systemProperties.each { key, value ->
                    command.add("-D${key}=${value}" as String)
                }
            }
            command.addAll(args)
            def processBuilder = new ProcessBuilder(command)
            processBuilder.directory(workingDir)

            // Start the process
            def process = processBuilder.start()

            // Redirect standard output and error
            process.consumeProcessOutput(System.out, System.err)

            // Wait for the process to finish
            process.waitFor()

            // Return exit code
            return process.exitValue()
        }
    }
}
