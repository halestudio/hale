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

manifests = ant.fileScanner {
	fileset(dir: '.') {
		patternset(refid: 'manifests')
	}
}

plugins = ant.fileScanner {
	fileset(dir: '.') {
		patternset(refid: 'plugins')
	}
}

features = ant.fileScanner {
	fileset(dir: '.') {
		patternset(refid: 'features')
	}
}

products = ant.fileScanner {
	fileset(dir: '.') {
		patternset(refid: 'products')
	}
}

def listVersions() {
	def vers = [:]
	for (f in manifests) {
		def fis = new FileInputStream(f)
		def manifest = new Manifest(fis)
		def attributes = manifest.getMainAttributes()
		def name = attributes.getValue('Bundle-SymbolicName')
		def ver = attributes.getValue('Bundle-Version')
		if (vers[ver] == null)
			vers[ver] = []
		vers[ver] += name
	}
	
	for (f in features) {
		def feature = new groovy.util.XmlSlurper().parse(f)
		def name = feature.@id as String
		def ver = feature.@version as String
		if (vers[ver] == null)
			vers[ver] = []
		vers[ver] += "(feature) $name"
	}
	
	for (p in products) {
		def product = new groovy.util.XmlSlurper().parse(p)
		def name = product.@uid as String
		def ver = product.@version as String
		if (vers[ver] == null)
			vers[ver] = []
		vers[ver] += '(product) ' + name
	}
	
	for (k in vers.sort()) {
		println k.key + ':'
		for (v in k.value) {
			println '    ' + v
		}
	}
}

def updateBundles(o, n) {
	println 'Replacing bundle versions ...'
	println manifests.iterator().toList().size() + ' MANIFEST.MF files found.'
	ant.replace(dir: '.', summary: true, token: "Bundle-Version: ${o}", value: "Bundle-Version: ${n}") {
		patternset(refid: 'manifests')
	}
}

def updatePlugins(o, n) {
	println 'Replacing plugin versions ...'
	println plugins.iterator().toList().size() + ' plugin.xml files found.'
	ant.replace(dir: '.', summary: true, token: "Version ${o}", value: "Version ${n}") {
		patternset(refid: 'plugins')
	}
}

def updateFeatures(o, n) {
	println 'Replacing feature versions ...'
	println features.iterator().toList().size() + ' feature.xml files found.'
	for (f in features) {
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

def updateProducts(o, n) {
	println 'Replacing product versions ...'
	println products.iterator().toList().size() + ' product files found.'
	ant.replace(dir: '.', summary: true, token: "version=\"${o}", value: "version=\"${n}") {
		patternset(refid: 'products')
	}
	ant.replace(dir: '.', summary: true, token: "Version ${o}", value: "Version ${n}") {
		patternset(refid: 'products')
	}
}

def update(o, n) {
	println "Old version: ${o}"
	println "New version: ${n}"
	updateBundles(o, n)
	updatePlugins(o, n)
	updateFeatures(o, n)
	updateProducts(o, n)
}

def cli = new CliBuilder(usage: 'updateversionnumbers.groovy [options]')
cli.with {
	h longOpt: 'help', 'Show usage information'
	l longOpt: 'list', 'List all plugins and features sorted by their respective version'
	o longOpt: 'old', args: 1, argName: 'OLD', 'Old version number'
	n longOpt: 'new', args: 1, argName: 'NEW', 'New version number'
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
	update(options.o, options.n)
	return
}

cli.usage()
