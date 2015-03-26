#!/usr/bin/env groovy

import java.util.jar.Manifest

ant = new AntBuilder()

ant.patternset(id: 'manifests') {
	include(name: '**/MANIFEST.MF')
	exclude(name: '**/build/')
	exclude(name: '**/.git/')
	exclude(name: '**/bin/')
	exclude(name: '**/classes/')
	exclude(name: '**/platform/')
	exclude(name: '**/target/')
}

ant.patternset(id: 'plugins') {
	include(name: '**/plugin.xml')
	exclude(name: '**/build/')
	exclude(name: '**/.svn/')
	exclude(name: '**/bin/')
	exclude(name: '**/classes/')
	exclude(name: '**/platform/')
	exclude(name: '**/target/')
}

ant.patternset(id: 'features') {
	include(name: '**/feature.xml')
	exclude(name: '**/build/')
	exclude(name: '**/.svn/')
	exclude(name: '**/bin/')
	exclude(name: '**/classes/')
	exclude(name: '**/platform/')
	exclude(name: '**/target/')
}

ant.patternset(id: 'products') {
	include(name: '**/*.product')
	exclude(name: '**/build/')
	exclude(name: '**/.git/')
	exclude(name: '**/bin/')
	exclude(name: '**/classes/')
	exclude(name: '**/platform/eclipse')
	exclude(name: '**/platform/igd')
	exclude(name: '**/platform/target')
	exclude(name: '**/target/')
}

def manifests(folders) {
	ant.fileScanner {
		folders.each {
			fileset(dir: it) {
				patternset(refid: 'manifests')
			}
		}
	}
}

def plugins(folders) {
	ant.fileScanner {
		folders.each {
			fileset(dir: it) {
				patternset(refid: 'plugins')
			}
		}
	}
}

def features(folders) {
	ant.fileScanner {
		folders.each {
			fileset(dir: it) {
				patternset(refid: 'features')
			}
		}
	}
}

def products(folders) {
	ant.fileScanner {
		folders.each {
			fileset(dir: it) {
				patternset(refid: 'products')
			}
		}
	}
}

def listVersions(folders = ['.'], doPrint = true) {
	def vers = [:]
	for (f in manifests(folders)) {
		def fis = new FileInputStream(f)
		def manifest = new Manifest(fis)
		def attributes = manifest.getMainAttributes()
		def name = attributes.getValue('Bundle-SymbolicName')
		def ver = attributes.getValue('Bundle-Version')
		if (vers[ver] == null)
			vers[ver] = []
		vers[ver] += name
	}

	for (f in features(folders)) {
		def feature = new groovy.util.XmlSlurper().parse(f)
		def name = feature.@id as String
		def ver = feature.@version as String
		if (vers[ver] == null)
			vers[ver] = []
		vers[ver] += "(feature) $name"
	}

	for (p in products(folders)) {
		def product = new groovy.util.XmlSlurper().parse(p)
		def name = product.@uid as String
		def ver = product.@version as String
		if (vers[ver] == null)
			vers[ver] = []
		vers[ver] += '(product) ' + name
	}

	if (doPrint) {
		for (k in vers.sort()) {
			println k.key + ':'
			for (v in k.value) {
				println '    ' + v
			}
		}
	}

	vers
}

def updateBundles(o, n, folders) {
	println 'Replacing bundle versions ...'
	println manifests(folders).iterator().toList().size() + ' MANIFEST.MF files found.'
	folders.each {
		ant.replace(dir: it, summary: true, token: "Bundle-Version: ${o}", value: "Bundle-Version: ${n}") {
			patternset(refid: 'manifests')
		}
	}
}

def updatePlugins(o, n, folders) {
	println 'Replacing plugin versions ...'
	println plugins(folders).iterator().toList().size() + ' plugin.xml files found.'
	folders.each {
		ant.replace(dir: it, summary: true, token: "Version ${o}", value: "Version ${n}") {
			patternset(refid: 'plugins')
		}
	}
}

def updateFeatures(o, n, folders) {
	println 'Replacing feature versions ...'
	println features(folders).iterator().toList().size() + ' feature.xml files found.'
	for (f in features(folders)) {
		// check if a replacement should be done for the feature
		def feature = new XmlSlurper().parse(f)
		def version = feature.@version as String
		if (version.startsWith(o)) {
			def newVersion
			if (version == o) {
				newVersion = n
			}
			else {
				newVersion = n + version[o.length()..-1]
			}

			// make sure to only replace the first occurrence
			def fileText = f.text
			f.text = fileText.replaceFirst(java.util.regex.Pattern.quote("version=\"$version\""), "version=\"$newVersion\"")

			println "Updated feature ${feature.@id}"
		}
	}
}

def updateProducts(o, n, folders) {
	println 'Replacing product versions ...'
	println products(folders).iterator().toList().size() + ' product files found.'
	folders.each {
		ant.replace(dir: it, summary: true, token: "version=\"${o}", value: "version=\"${n}") {
			patternset(refid: 'products')
		}
	}
	folders.each {
		ant.replace(dir: it, summary: true, token: "Version ${o}", value: "Version ${n}") {
			patternset(refid: 'products')
		}
	}
}

def update(o, n, changed) {
	println "Old version: ${o}"
	println "New version: ${n}"

	def folders = [];
	def oldVersions = [];

	if (changed) {
		println 'Updating only changed bundles / features'
		println 'Checking for git...'
		def gitVersion = "git --version".execute()
		gitVersion.waitForProcessOutput()
		if (gitVersion.exitValue() != 0) {
			println 'Could not find git, exiting.'
			return
		}

		def gitDiff = "git diff --name-only ${o}".execute()
		def changedFiles = gitDiff.text
		if (gitDiff.exitValue() != 0) {
			println "Diff against tag ${o} failed, exiting."
			return
		}

		println 'Looking for changed bundles / features...'

		Set paths = new LinkedHashSet()
		Set ignoreChildren = ['.settings', '.project', '._project'].toSet()

		changedFiles.splitEachLine(/(\\|\/)/) { fileParts ->
			int nameIndex = 0
			for (int i = 0; i < fileParts.size() && nameIndex == 0; i++) {
				def part = fileParts[i]
				switch(part) {
				case 'plugins':
				case 'features':
					nameIndex = i+1
					break
				}
			}

			if (nameIndex > 0 && fileParts.size() > nameIndex) {
				String name = fileParts[nameIndex]

				boolean ignore = (ignoreChildren.contains(fileParts[nameIndex+1]))

				if (!ignore) {
					// build bundle / feature path
					String path = fileParts[0..nameIndex].join(File.separator)
					paths.add(path)
				}
			}
		}

		if (paths) {
			println 'Found the following changed bundles / features:'
			for (path in paths) {
				println path
				// add to search folders
				folders << path
			}
		}
		else {
			println 'No changed bundles or features found, exiting.'
			return
		}

		// look for current versions in changed bundles / features
		def versions = listVersions(folders, false).keySet()
		println 'with versions:'
		versions.each { version ->
			if (version.endsWith('.qualifier')) {
				version = version[0..-('.qualifier'.length() + 1)]
			}
			println version
			oldVersions << version
		}
	}
	else {
		// search in current folder
		folders << '.'
		// and only with the specified version
		oldVersions << o
	}

	oldVersions.each {
		updateBundles(it, n, folders)
	}
	oldVersions.each {
		updatePlugins(it, n, folders)
	}
	oldVersions.each {
		updateFeatures(it, n, folders)
	}
	oldVersions.each {
		updateProducts(it, n, folders)
	}
}

def cli = new CliBuilder(usage: 'updateversionnumbers.groovy [options]')
cli.with {
	h longOpt: 'help', 'Show usage information'
	l longOpt: 'list', 'List all plugins and features sorted by their respective version'
	o longOpt: 'old', args: 1, argName: 'OLD', 'Old version number'
	n longOpt: 'new', args: 1, argName: 'NEW', 'New version number'
	_ longOpt: 'changed', 'Only update versions changed since the OLD version tag'
}

def options = cli.parse(args)
if (!options) {
	cli.usage()
	return
}

if (options.h) {
	cli.usage()
	return
}

if (options.l) {
	listVersions()
	return
}

if (options.o && options.n) {
	update(options.o, options.n, options.changed)
	return
}

cli.usage()
