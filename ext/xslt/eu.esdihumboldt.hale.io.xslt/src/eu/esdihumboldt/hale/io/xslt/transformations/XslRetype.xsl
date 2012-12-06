## Template for XslRetype
<xsl:template name="$name">
	## For each relevant source instance
	<xsl:for-each select="$select_instances">
		## ...create a target instance 
		<$targetElement>
			## TODO
		</$targetElement>
	</xsl:for-each>
</xsl:template>