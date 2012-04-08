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
    Author     : joshua.marinacci@sun.com
    Description: customize the output with special doctags for use only
    by the F3 GUI project.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html"/>
    <xsl:param name="sdk-overview">false</xsl:param>
    <xsl:import href="javadoc.xsl"/>
    
    <!-- duplicates from previous reprise custom.xsl -->
    <xsl:template match="var[docComment/tags/treatasprivate]" mode="toc"></xsl:template>
    <xsl:template match="script-var[docComment/tags/treatasprivate]" mode="toc"></xsl:template>

    <xsl:template name="extra-var">
        <xsl:if test="docComment/tags/treatasprivate">
            <xsl:text>private</xsl:text>
        </xsl:if>
    </xsl:template>
    <xsl:template name="extra-method">
        <xsl:if test="docComment/tags/treatasprivate">
            <xsl:text>private</xsl:text>
        </xsl:if>
    </xsl:template>
    <xsl:template name="extra-class">
        <xsl:if test="docComment/tags/treatasprivate">
            <xsl:text>private</xsl:text>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="extra-var-column-header">
        <th><a class="tooltip" title="Indicates the variable can be read">Can Read</a></th>
        <th><a title="Indicates the variable can only be set in the Object initializer. Any further changes will be ignored."
         class="tooltip">Can Init</a></th>
        <th><a title="Indicates the varible can set at any time."
         class="tooltip">Can Write</a></th>
        <th><a title="Indicates the default value of this variable"
         class="tooltip">Default Value</a></th>
    </xsl:template>
    
    <xsl:template name="extra-var-column-data">
        <td class="canread">
            <xsl:choose>
                <xsl:when test="modifiers/public"><img src="{$root-path}/images/F3_highlight_dot.png"/></xsl:when>
                <xsl:when test="modifiers/public-init"><img src="{$root-path}/images/F3_highlight_dot.png"/></xsl:when>
                <xsl:when test="modifiers/public-read"><img src="{$root-path}/images/F3_highlight_dot.png"/></xsl:when>
                <xsl:when test="modifiers/protected">subclass</xsl:when>
            </xsl:choose>
        </td>
        <td class="caninit">
            <xsl:choose>
                <xsl:when test="modifiers/public-init"><img src="{$root-path}/images/F3_highlight_dot.png"/></xsl:when>
                <xsl:when test="modifiers/public">
                    <xsl:choose>
                        <xsl:when test="modifiers/read-only"></xsl:when>
                        <xsl:otherwise>
                            <img src="{$root-path}/images/F3_highlight_dot.png"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:when>
                <xsl:when test="modifiers/protected">
                    <xsl:choose>
                        <xsl:when test="modifiers/read-only"></xsl:when>
                        <xsl:otherwise>subclass</xsl:otherwise>
                    </xsl:choose>
                </xsl:when>
            </xsl:choose>
        </td>
        <td class="canwrite">
            <xsl:choose>
                <xsl:when test="modifiers/public">
                    <xsl:choose>
                        <xsl:when test="modifiers/read-only"></xsl:when>
                        <xsl:otherwise>
                            <img src="{$root-path}/images/F3_highlight_dot.png"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:when>
                <xsl:when test="modifiers/protected">
                    <xsl:choose>
                        <xsl:when test="modifiers/read-only"></xsl:when>
                        <xsl:otherwise>subclass</xsl:otherwise>
                    </xsl:choose>
                </xsl:when>
            </xsl:choose>
        </td>
        <td class="defaultvalue">
            <xsl:value-of select="docComment/tags/defaultvalue"/>
        </td>
    </xsl:template>
    
    <xsl:template name="var-table-width">6</xsl:template>

    <!-- new stuff -->
    <xsl:template match="seeTags">
        <p><b>See Also:</b><br/>
            <xsl:apply-templates select="see"/>
        </p>
    </xsl:template>
    
    <!-- turn off jumpdown links -->
    <xsl:template match="function | script-function | method | constructor" mode="toc-signature">
        <xsl:apply-templates select="modifiers"/>
        <xsl:text> </xsl:text>
        
        <!-- f3 -->
        <xsl:if test="not(../@language='java')">
            <b><xsl:value-of select="@name"/></b>
            <xsl:apply-templates select="parameters" mode="signature"/>
            :
            <!-- build return type link, if appropriate -->
            <xsl:apply-templates select="returns" mode="signature"/>
            <xsl:value-of select="type/@dimension"/>
        </xsl:if>
        
        <!-- java -->
        <xsl:if test="../@language='java'">
            <xsl:apply-templates select="returns" mode="signature"/>
            <xsl:text> </xsl:text>
            <b><xsl:value-of select="@name"/></b>
            <xsl:apply-templates select="parameters" mode="signature"/>
            <xsl:call-template name="throws-clause"/>
        </xsl:if>
            
    </xsl:template>
    
    
    <xsl:template name="head-post">
        <link href="{$root-path}sdk.css" rel="stylesheet"/>
        <script type="text/javascript" src="{$root-path}mootools-1.2.1-yui.js"/>
        <script type="text/javascript" src="{$root-path}sessvars.js"/>
        <script type="text/javascript" src="{$root-path}sdk.js"/>
    </xsl:template>
    
    
    <xsl:template name="header-pre">
        <div id="top-header">
        <h1><a href="{$root-path}index.html">F3: <i>Bringing Rich Experiences To All the Screens Of Your Life</i></a></h1>
        <h3 id="master-index-link"><a href="{$root-path}master-index.html">master index</a></h3>
        <h3 id="collapse-expand-switcher"><a href="#" id="collapse-expand-link">expand all</a></h3>
        <h3 id="profile-switcher">Profile: <a href="#" id="select-desktop-profile">desktop</a>, <a href="#" id="select-common-profile">common</a></h3>
        </div>
        
    </xsl:template>
    
    
    <!-- new index / overview page -->
    <xsl:template match="/packageList[@mode='overview-summary']">
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
                <title><xsl:value-of select="$std.doctitle.prefix"/> Overview <xsl:value-of select="$std.doctitle.suffix"/></title>
            </head>
            <body>
                <xsl:call-template name="header-pre"/>
                <ul id="classes-toc">
                    <xsl:for-each select="package">
                        <xsl:sort select="@name"/>
                        <li>
                            <h4 class='header'><a href="#"><xsl:value-of select="@name"/></a></h4>
                            <ul class='content'>
                                <xsl:for-each select="class">
                                    <li>
                                        <xsl:attribute name="class">
                                            <xsl:call-template name="profile-class"/>
                                            <xsl:call-template name="extra-class"/>
                                        </xsl:attribute>
                                        <a>
                                        <xsl:attribute name="href">
                                            <xsl:text></xsl:text>
                                            <xsl:value-of select="@packageName"/>
                                            <xsl:text>/</xsl:text>
                                            <xsl:value-of select="@qualifiedName"/>
                                            <xsl:text>.html</xsl:text>
                                        </xsl:attribute>
                                        <xsl:value-of select="@name"/>
                                        </a></li>
                                </xsl:for-each>
                            </ul>
                        </li>
                    </xsl:for-each>
                    <li id="copyright">
                        <xsl:call-template name="add-copyright-link"/>
                    </li>
                </ul>
                
                <div id="content">
                    <h3><xsl:value-of select="$std.doctitle.prefix"/> Overview <xsl:value-of select="$std.doctitle.suffix"/></h3>
                    
                    <xsl:if test="$sdk-overview='true'">
                        <p>The F3 <sup>tm</sup> Platform is a rich client platform for cross-screen rich internet applications (RIA) and content. It consists of common elements (2D graphics, Animation, Text and Media) and device specific elements for desktop, mobile and TV.  The F3 common set of APIs allow source level portability of the common set of functionalities across all platforms supported by F3.

                        The F3 Runtimes targeted for different devices will ensure consistency and fidelity for content created based on the F3 Common APIs.

                        The F3 Common APIs will continue to evolve to match more powerful, common capabilities on the various device types.

                        </p>

                        <p><img src="platform_diagram.png"/></p>

                        <h3>What you can build with F3:</h3>

                        <p><b>Cross Platform Applications:</b> If you want to develop a RIA across screens then you need to use F3 Common APIs only. The F3 Common APIs currently support 2D Graphics, Animation and Text across all platforms. In future, there will be support for audio, video, networking, local storage and other relevant components in F3 Common.</p>

                        <p><b>Desktop Applications:</b> If you are designing a desktop only application  ( Windows and Mac are currently supported) you can extend the functionality of the F3 applications by using APIs that are optimized for the desktop in addition to F3 Common. This will allow your application to adapt to a desktop look and feel with the F3 Swing extensions and also take advantage of Device Media Frameworks and advanced graphics support.</p>
                    </xsl:if>
                    <table class="package-docs">
                        <tr><th></th></tr>
                        <xsl:for-each select="package">
                            <xsl:sort select="@name"/>
                            <tr>
                                <td class="name">
                                    <b>
                                        <!-- <a><xsl:attribute name="href"><xsl:value-of select="@name"/>/package-summary.html</xsl:attribute></a> -->
                                        <xsl:value-of select="@name"/>
                                    </b>
                                </td>
                                <td class="description">
                                    <xsl:apply-templates select="docComment/firstSentenceTags"/>
                                    <xsl:if test="$inline-descriptions='true'">
                                        <xsl:if test="docComment/inlineTags | docComment/seeTags | docComment/needsReview">
                                            <a href="#" class="long-desc-open"><img src="images/F3_arrow_right.png"/></a>
                                            <div class="long-desc">
                                                <!-- the rest of the docs -->
                                                <!-- see comments below for mode "packages-overview" mode -->
                                                <xsl:apply-templates select="docComment/inlineTags" mode="packages-overview"/>
                                                <xsl:apply-templates select="docComment/seeTags" mode="package-overview"/>
                                                <xsl:apply-templates select="docComment/tags/needsreview"/>
                                                &#160;
                                            </div>
                                        </xsl:if>
                                    </xsl:if>
                                </td>
                            </tr>
                        </xsl:for-each>
                    </table>
                </div>
            </body>
        </html>
    </xsl:template>
    
    <xsl:template match="docComment/inlineTags" mode="packages-overview">
        <p class="comment">
            <xsl:for-each select="*">
            <xsl:choose>
                <!-- special case handling for @link from package level doc -->
                <xsl:when test="name(.)='see'">
                    <xsl:apply-templates select="." mode="packages-overview"/>
                </xsl:when>
                <xsl:otherwise>
                    <!-- for any other node apply default mode template -->
                    <xsl:apply-templates select="."/>
                </xsl:otherwise>
            </xsl:choose>
            </xsl:for-each>
        </p>
    </xsl:template>
    
    <xsl:apply-templates select="docComment/seeTags" mode="packages-overview">
        <p><b>See Also:</b><br/>
            <xsl:apply-templates select="see" mode="packages-overview"/>
        </p>
    </xsl:apply-templates>
                                                
    <!-- 
        @see and @link from package level doc comments are handled here.
        The "see" elements generated by XMLDoclet have "../" prefix in the
        "href" attributes. This is because normally these are meant to link
        between classes. But, package docs live in the top-level "index.html"
        and so we should *not* have "../" prefix in hrefs from there. Admittedly,
        this is a hack. Need to look for better alternative.
    -->
    <xsl:template match="see" mode="packages-overview">
        <a>
            <!-- remove "../" prefix in href -->
            <xsl:attribute name="href"><xsl:value-of select="substring(@href, 4)"/></xsl:attribute>
            <xsl:choose>
                <xsl:when test="@label">
                    <xsl:text><xsl:value-of select="@label"/></xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:text><xsl:value-of select="text()"/></xsl:text>
                </xsl:otherwise>
            </xsl:choose>
        </a>
        <xsl:if test="position()!=last()"><xsl:text>, </xsl:text></xsl:if>
    </xsl:template>
    
</xsl:stylesheet>
