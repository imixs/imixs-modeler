<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0" xmlns:xpdl="http://www.wfmc.org/2002/XPDL1.0">

	<xsl:output method="xml" encoding="utf-8" />

	<xsl:template match="/">

		<xpdl:WorkflowProcesses
			xsi:schemaLocation="http://www.wfmc.org/2002/XPDL1.0 xpdl.xsd"
			xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			xmlns:xpdl="http://www.wfmc.org/2002/XPDL1.0">

			<xsl:apply-templates select="/model/processgroup/processentity" />

		</xpdl:WorkflowProcesses>

	</xsl:template>


	<xsl:template match="/model/processgroup/processentity">
		<xpdl:WorkflowProcess AccessLevel="PUBLIC"
			Id="{itemcollection/item[@name='numprocessid']}"
			Name="{itemcollection/item[@name='txtname']}">
			
			<xpdl:ProcessHeader/>
		
			<xpdl:Activities>
				<xsl:apply-templates select="activityentity"/>
			</xpdl:Activities>
			
			<xpdl:Transitions>
				<xsl:apply-templates select="activityentity" mode="transition"/>
			</xpdl:Transitions>
		
		</xpdl:WorkflowProcess>
	</xsl:template>
	
	
	<xsl:template match="activityentity">
		<xpdl:Activity Id="{@id}" Name="{@name}">
			<xpdl:Description></xpdl:Description>
			<xpdl:Route/>
			<xpdl:ExtendedAttributes>
				<xsl:apply-templates select="itemcollection/item"/>
			</xpdl:ExtendedAttributes>
		</xpdl:Activity>
	</xsl:template>
	
	
	<!-- Berechnung der Transitions -->
	<xsl:template match="activityentity" mode="transition">
		<xpdl:Transition From="{../@id}" To="{@nextid}" Id="{@id}"/>
	</xsl:template>
	
	
	<xsl:template match="activityentity/itemcollection/item">
		<xpdl:ExtendedAttribute Name="{@name}" Value="{.}"/>
	</xsl:template>


</xsl:stylesheet>
