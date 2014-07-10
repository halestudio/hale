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
}
