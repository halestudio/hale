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
import com.beust.jcommander.JCommander
import com.beust.jcommander.ParameterException
import com.beust.jcommander.Parameter
import com.beust.jcommander.Parameters

class CommandLineBuilder {
    private def main = new MainCommand()
    private def jc = new JCommander(main)
    private def commitStage = new CommitStageCommand()
//    private def integrationTestStage = new IntegrationTestStageCommand()
//    private def deployArtifacts = new DeployArtifactsCommand()
    private def client = new ClientCommand()
    private def server = new ServerCommand()
	private def product = new ProductFileCommand()
    private def clean = new CleanCommand()
    private def help = new HelpCommand()
	private def site = new SiteCommand()
    private Project project

    CommandLineBuilder(Project project) {
        this.project = project
    }

    def run(args) {
        jc.setProgramName('build')
        jc.addCommand('commitStage', commitStage)
//        jc.addCommand('integrationTestStage', integrationTestStage)
//        jc.addCommand('deployArtifacts', deployArtifacts)
        jc.addCommand('client', client)
        jc.addCommand('server', server)
		jc.addCommand('product', product)
        jc.addCommand('clean', clean)
        jc.addCommand('help', help)
		jc.addCommand('site', site)

        try {
            jc.parse(args.split(' '))
        } catch (ParameterException e) {
            System.err.println()
            System.err.println(e.getMessage())
            help.run()
            return
        }

        if (main.help) {
            help.run()
            return
        }

        if (main.version) {
            println(project.version)
            return
        }

        def cmd = jc.getParsedCommand()
        if (cmd == 'client') {
            client.run()
        } else if (cmd == 'server') {
            server.run()
        } else if (cmd == 'clean') {
            clean.run()
        } else if (cmd == 'help') {
            help.run()
        } else if (cmd == 'integrationTestStage') {
            integrationTestStage.run()
        } else if (cmd == 'deployArtifacts') {
            deployArtifacts.run()
        } else if (cmd == 'product') {
			product.run()
		} else if (cmd == 'site') {
			site.run()
        } else {
            commitStage.run()
        }
    }

    class MainCommand {
        @Parameter(names = [ '-h', '--help' ], description = 'Display this help')
        boolean help

        @Parameter(names = [ '-V' , '--version' ], description = 'Display version information')
        boolean version
    }

    @Parameters(commandDescription = 'Compiles everything and runs all unit tests')
    class CommitStageCommand {
        def run() {
			if (!project.ext.testProduct) {
				throw new IllegalStateException('No test product specified')
			}
			
			// set productFile property to test product
			project.ext.productFile = project.ext.testProduct
			
            project.tasks['cli'].dependsOn(project.tasks['commitStage'])
        }
    }

    @Parameters(commandDescription = 'Compiles everything and runs all integration tests')
    class IntegrationTestStageCommand {
        def run() {
            project.tasks['cli'].dependsOn(project.tasks['integrationTestStage'])
        }
    }

    @Parameters(commandDescription = 'Deploys artifacts to remote Maven repository')
    class DeployArtifactsCommand {
        @Parameter(names = [ '-r', '--release' ], description = 'Deploy release artifacts instead of snapshots')
        boolean release

        def run() {
            if (release) {
                project.tasks['cli'].dependsOn(project.tasks['deployReleaseArtifacts'])
            } else {
                project.tasks['cli'].dependsOn(project.tasks['deployArtifacts'])
            }
        }
    }
	
	@Parameters(commandDescription = 'Build an Eclipse Update Site / p2 repository')
	class SiteCommand {
		@Override
		String getType() {
			return 'site'
		}
		
		@Parameter(description = '[<featureId>]')
		List<String> featureIds = []

		def run() {
			if (featureIds.size() > 1) {
				help.commands = [ getType() ]
				help.run()
				return
			}
			
			if (featureIds && featureIds[0]) {
				project.ext.customUpdateSiteFeatureId = featureIds[0]
			}
			project.tasks['cli'].dependsOn(project.tasks['packageUpdateSite'])
		}
	}

    abstract class ProductCommand {
        @Parameter(description = '<product name>')
        List<String> names = new ArrayList<String>();

        @Parameter(names = [ '-o', '--os' ], description = 'Targeted operating system: auto, windows, linux, macosx')
        String os = 'auto'

        @Parameter(names = [ '-a', '--arch' ], description = 'Targeted architecture: auto, x86, x86_64')
        String arch = 'auto'

        @Parameter(names = [ '-t', '--tag' ], description = 'Customer-specific tag')
        String tag

        @Parameter(names = [ '-l', '--lang' ], description = 'Targeted language: de, en')
        String lang = 'en'
		
		@Parameter(names = [ '--no-installer' ], description = 'For Windows builds create a ZIP package instead of an installer')
		boolean noInstaller = false;

        abstract String getType()

        def run() {
            if (names.size() != 1) {
                help.commands = [ getType() ]
                help.run()
                return
            }

            String productName = names[0]
            project.ext.productType = getType()
            project.ext.productName = productName
			project.ext.noInstaller = noInstaller
            if (os != null) {
                if (os == 'windows') {
                    project.ext.osgiOS = 'win32'
                    project.ext.osgiWS = 'win32'
                } else if (os == 'linux') {
                    project.ext.osgiOS = 'linux'
                    project.ext.osgiWS = 'gtk'
                } else if (os == 'macosx') {
					project.ext.osgiOS = 'macosx'
					project.ext.osgiWS = 'cocoa'
                } else if (os != 'auto') {
                    help.commands = [ getType() ]
                    help.run()
                    return
                }
            }
            if (arch != null) {
                if (arch == 'x86' || arch == 'x86_64') {
                    project.ext.osgiArch = arch
                } else if (arch != 'auto') {
                    help.commands = [ getType() ]
                    help.run()
                    return
                }
            }
            if (tag != null) {
                project.ext.tag = tag
            }
            if (lang != null) {
                project.ext.language = lang
            }
            project.tasks['cli'].dependsOn(project.tasks['packageProduct'])
        }
    }

	@Parameters(commandDescription = 'Build a product based on a product file or alias')
	class ProductFileCommand extends ProductCommand {
		@Override
		String getType() {
			return 'product'
		}
		
		def run() {
			String product = names[0]
			
			// check if product is a product alias
			if (project.ext.has('productAlias')) {
				String candidate = project.ext.productAlias[product]
				if (candidate != null) {
					product = candidate
				}
			}
			
			// set productFile property
			project.ext.productFile = product
			
			super.run()
		}
	}
	
    @Parameters(commandDescription = 'Build client product')
    class ClientCommand extends ProductCommand {
        @Override
        String getType() {
            return 'client'
        }
    }

    @Parameters(commandDescription = 'Build server product')
    class ServerCommand extends ProductCommand {
        @Override
        String getType() {
            return 'server'
        }
    }

    @Parameters(commandDescription = 'Removes build output')
    class CleanCommand {
        def run() {
            project.tasks['cli'].dependsOn(project.tasks['clean'])
        }
    }

    @Parameters(commandDescription = 'Display this help')
    class HelpCommand {
        @Parameter(description = '[commands]')
        List<String> commands = new ArrayList<String>();

        def run() {
            def sb = new StringBuilder()
            if (commands.isEmpty()) {
                sb.append("\n")
                jc.usage(sb)
            } else {
                for (cmd in commands) {
                    sb.append("\n")
                    jc.usage(cmd, sb)
                }
            }
            println(sb.toString())
        }
    }
}
