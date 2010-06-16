<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
version="1.0">
<xsl:template match="/">
<html>
<head><title>Real's HowTo</title></head>
<body>
<table border="1">
<tr>
<th>Title</th>
<th>URL</th>
</tr>
<xsl:for-each select="howto/topic">
<tr>
<td><xsl:value-of select="title"/></td>
<td><xsl:value-of select="url"/></td>
</tr>
</xsl:for-each>
</table>
</body></html>
</xsl:template>
</xsl:stylesheet>

