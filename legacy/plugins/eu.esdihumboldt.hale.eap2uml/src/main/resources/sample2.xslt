<!-- <?xml version="1.0" encoding="UTF-8"?>-->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" encoding="UTF-8"/>
<xsl:template match="cont">
	<xsl:copy-of select="letter"/>
</xsl:template>
</xsl:stylesheet>