// Fraunhofer Institute for Computer Graphics Research (IGD)
// Department Graphical Information Systems (GIS)
//
// Copyright (c) 2004-2013 Fraunhofer IGD. All rights reserved.
//
// This source code is property of the Fraunhofer IGD and underlies
// copyright restrictions. It may only be used with explicit
// permission from the respective owner.

import org.gradle.api.Project

class Helper {
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
}
