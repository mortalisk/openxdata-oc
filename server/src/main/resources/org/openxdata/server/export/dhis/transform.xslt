<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:oxd="org.openxdata.server.export.XsltFunctions"
	exclude-result-prefixes="oxd xs">

	<xsl:output method="xml" indent="yes" encoding="UTF-8" />
	<xsl:param name="orgunitid" />

	<xsl:template match="/">
		<dataValueSet xmlns="http://dhis2.org/schema/dxf/2.0-SNAPSHOT"
			period="{oxd:convertDateToWeekOfYear(descendant::ending_date_of_week)}"
			orgUnit="{$orgunitid}">
			<xsl:for-each select="child::*">
				<xsl:for-each select="child::*">
					<xsl:if test="contains(name(),'--')">
						<dataValue dataElement="{substring-after(name(), '--')}"
							value="{.}" />
					</xsl:if>
				</xsl:for-each>
			</xsl:for-each>
		</dataValueSet>
	</xsl:template>
</xsl:stylesheet>