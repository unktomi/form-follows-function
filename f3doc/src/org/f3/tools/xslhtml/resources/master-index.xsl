<?xml version="1.0" encoding="UTF-8"?>
<!--
 Copyright 2008-2009 Sun Microsystems, Inc.  All Rights Reserved.
 DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.

 This code is free software; you can redistribute it and/or modify it
 under the terms of the GNU General Public License version 2 only, as
 published by the Free Software Foundation.

 This code is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 version 2 for more details (a copy is included in the LICENSE file that
 accompanied this code).

 You should have received a copy of the GNU General Public License version
 2 along with this work; if not, write to the Free Software Foundation,
 Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.

 Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 CA 95054 USA or visit www.sun.com if you need additional information or
 have any questions.
-->

<!--
    Document   : master-index.xsl
    Created on : September 11, 2008, 1:06 PM
    Author     : joshua.marinacci@sun.com
    Description: generate a master index of every variable and function in the entire api
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html"/>
    <xsl:import href="sdk.xsl"/>
    <xsl:template match="/">
        <xsl:text disable-output-escaping="yes">
            <![CDATA[<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">]]>
        </xsl:text>
        <html>
            <head>
                <link href="{$root-path}{$master-css}" rel="stylesheet"/>
                <xsl:if test="$extra-css">
                    <link href="{$root-path}{$extra-css}" rel="stylesheet"/>
                </xsl:if>
                <xsl:if test="$extra-js">
                    <script src="{$root-path}{$extra-js}"></script>
                </xsl:if>
                <xsl:call-template name="head-post"/>
                <xsl:call-template name="add-meta-tags"/>
                <title><xsl:value-of select="$std.doctitle.prefix"/> A-Index <xsl:value-of select="$std.doctitle.suffix"/></title>
            </head>
            <body>
                <xsl:call-template name="header-pre"/>
                <ul id="master-list"><!-- |//class | //function-->
                    <xsl:for-each select="//script-var | //var | //script-function | //function | //class">
                        <xsl:sort select="@name"/>
                        <xsl:if test="not(docComment/tags/treatasprivate)">
                        <li>
                            <xsl:attribute name="class">
                                <xsl:call-template name="profile-class"/>    
                            </xsl:attribute>
                            <xsl:apply-templates select="."/>
                        </li>
                        </xsl:if>
                    </xsl:for-each>
                </ul>
            </body>
        </html>
    </xsl:template>
    
    
    <xsl:template match="class">
        <a>
            <xsl:apply-templates select="." mode="href"/>
            <xsl:value-of select="@name"/>
        </a>
        - class in package <b><xsl:value-of select="@packageName"/></b>
    </xsl:template>
    <xsl:template match="var | script-var">
        <a>
            <xsl:apply-templates select="." mode="href"/>
            <xsl:value-of select="@name"/>
        </a>
        - variable in class 
        <a>
            <xsl:apply-templates select=".." mode="href"/>
            <xsl:value-of select="../@qualifiedName"/>
        </a>
    </xsl:template>
    <xsl:template match="function | script-function">
        <a>
            <xsl:apply-templates select="." mode="href"/>
            <xsl:value-of select="@name"/>
        </a>
        - function in class
        <a>
            <xsl:apply-templates select=".." mode="href"/>
            <xsl:value-of select="../@qualifiedName"/>
        </a>
    </xsl:template>
    <xsl:template match="*"></xsl:template>

</xsl:stylesheet>
