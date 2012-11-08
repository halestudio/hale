<?xml version="1.0" encoding="UTF-8"?>
<xsl:transform
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	version="2.0"
	xsi:schemaLocation="http://www.w3.org/1999/XSL/Transform http://www.w3.org/2007/schema-for-xslt20.xsd">
	
	<xsl:output method="xml" encoding="UTF-8" indent="yes" />
	
	<xsl:template match="/">
		<xsl:message>Document root</xsl:message>
		<dummy></dummy>
	</xsl:template>
</xsl:transform>