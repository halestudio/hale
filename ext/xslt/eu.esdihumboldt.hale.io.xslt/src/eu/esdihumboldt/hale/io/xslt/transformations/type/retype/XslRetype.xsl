## Template for XslRetype
<xsl:template name="$name">
	## For each relevant source instance
	<xsl:for-each select="$select_instances">
		## ...create a target instance 
		<$targetElement>
			<xsl:call-template name="$inline_name" />
		</$targetElement>
	</xsl:for-each>
</xsl:template>
<xsl:template name="$inline_name">
	$properties
</xsl:template>