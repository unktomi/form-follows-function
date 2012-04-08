<!--
 * Copyright 2009 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 -->

<xsl:transform
     xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
     xmlns:c="http://xml.apache.org/xalan/java/org.f3.tools.tree.xml.Compiler"
     xmlns:f3="http://f3.org">

    <xsl:import href="analyze.xsl"/>    
    <xsl:strip-space elements="*"/>
    <xsl:output method="text"/>

    <xsl:template name="init-globals">
        <xsl:text>#</xsl:text>
        <xsl:value-of select="c:putGlobal('eager-binds', 0)"/>
        <xsl:value-of select="c:putGlobal('lazy-binds', 0)"/>
        <xsl:value-of select="c:putGlobal('on-replaces', 0)"/>
        <xsl:value-of select="c:putGlobal('on-invalidates', 0)"/>

        <xsl:value-of select="c:putGlobal('local-eager-binds', 0)"/>
        <xsl:value-of select="c:putGlobal('local-lazy-binds', 0)"/>
        <xsl:value-of select="c:putGlobal('local-on-replaces', 0)"/>

        <xsl:value-of select="c:putGlobal('object-literal-eager-binds', 0)"/>
        <xsl:value-of select="c:putGlobal('object-literal-lazy-binds', 0)"/>
        <xsl:value-of select="c:putGlobal('object-literals-with-binds', 0)"/>
        <xsl:text>&#10;</xsl:text>
    </xsl:template>

    <xsl:template match="/">
      <xsl:call-template name="init-globals"/>
      <xsl:apply-templates select="f3:f3"/>
    </xsl:template>

    <xsl:template match="f3:f3">
<!-- output the source file name and write down global variable values as properties -->
        <xsl:apply-templates select="f3:file"/>
        <xsl:apply-templates select="f3:defs"/>
count.of.eager.binds=<xsl:value-of select="c:getGlobal('eager-binds')"/>
count.of.lazy.binds=<xsl:value-of select="c:getGlobal('lazy-binds')"/>
count.of.on.replaces=<xsl:value-of select="c:getGlobal('on-replaces')"/>
count.of.on.invalidates=<xsl:value-of select="c:getGlobal('on-invalidates')"/>
count.of.local.eager.binds=<xsl:value-of select="c:getGlobal('local-eager-binds')"/>
count.of.local.lazy.binds=<xsl:value-of select="c:getGlobal('local-lazy-binds')"/>
count.of.local.on.replaces=<xsl:value-of select="c:getGlobal('local-on-replaces')"/>
count.of.object.literal.eager.binds=<xsl:value-of select="c:getGlobal('object-literal-eager-binds')"/>
count.of.object.literal.lazy.binds=<xsl:value-of select="c:getGlobal('object-literal-lazy-binds')"/>
count.of.object.literals.with.binds=<xsl:value-of select="c:getGlobal('object-literals-with-binds')"/>
    </xsl:template>

    <xsl:template match="f3:file">
        <xsl:text># compiled from </xsl:text> <xsl:value-of select="."/>
    </xsl:template>
    
    <xsl:template name="handle-var" >
        <xsl:param name="varEagerBinds"/>
        <xsl:param name="varLazyBinds"/>
        <xsl:param name="varOnReplaces"/>
        <xsl:param name="varOnInvalidates"/>

        <xsl:for-each select="f3:bind-status">
             <xsl:choose>
                 <xsl:when test=". = 'bind' or . = 'bind-with-inverse'">
                     <xsl:if test="not(c:putGlobal($varEagerBinds, c:getGlobal($varEagerBinds) + 1))"/>
                 </xsl:when>
                 <xsl:when test=". = 'bind-lazy' or . = 'bind-lazy-with-inverse'">
                     <xsl:if test="not(c:putGlobal($varLazyBinds, c:getGlobal($varLazyBinds) + 1))"/>
                 </xsl:when>
             </xsl:choose>
        </xsl:for-each>

        <xsl:apply-templates select="f3:init-value/*"/>

        <xsl:if test="f3:on-replace">
            <xsl:if test="not(c:putGlobal($varOnReplaces, c:getGlobal($varOnReplaces) + 1))"/>
            <xsl:apply-templates select="f3:on-replace"/>
        </xsl:if>

        <xsl:if test="f3:on-invalidate">
            <xsl:if test="not(c:putGlobal($varOnInvalidates, c:getGlobal($varOnInvalidates) + 1))"/>
            <xsl:apply-templates select="f3:on-invalidate"/>
        </xsl:if>
    </xsl:template>

    <xsl:template match="f3:class/f3:members/f3:var|f3:class/f3:members/f3:def">
        <xsl:call-template name="handle-var">
            <xsl:with-param name="varEagerBinds" select="'eager-binds'"/>
            <xsl:with-param name="varLazyBinds" select="'lazy-binds'"/>
            <xsl:with-param name="varOnReplaces" select="'on-replaces'"/>
            <xsl:with-param name="varOnInvalidates" select="'on-invalidates'"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="f3:f3/f3:defs/f3:var|f3:f3/f3:defs/f3:def">
        <xsl:call-template name="handle-var">
            <xsl:with-param name="varEagerBinds" select="'eager-binds'"/>
            <xsl:with-param name="varLazyBinds" select="'lazy-binds'"/>
            <xsl:with-param name="varOnReplaces" select="'on-replaces'"/>
            <xsl:with-param name="varOnInvalidates" select="'on-invalidates'"/>
        </xsl:call-template>
    </xsl:template>

    <!-- a local var or def -->
    <xsl:template match="f3:var|f3:def">
        <xsl:call-template name="handle-var">
            <xsl:with-param name="varEagerBinds" select="'local-eager-binds'"/>
            <xsl:with-param name="varLazyBinds" select="'local-lazy-binds'"/>
            <xsl:with-param name="varOnReplaces" select="'local-on-replaces'"/>
            <xsl:with-param name="varOnInvalidates" select="'on-invalidates'"/>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:object-literal-init">
        <xsl:for-each select="f3:bind-status">
             <xsl:choose>
                 <xsl:when test=". = 'bind' or . = 'bind-with-inverse'">
                     <xsl:if test="not(c:putGlobal('object-literal-eager-binds', c:getGlobal('object-literal-eager-binds') + 1))"/>
                 </xsl:when>
                 <xsl:when test=". = 'bind-lazy' or . = 'bind-lazy-with-inverse'">
                     <xsl:if test="not(c:putGlobal('object-literal-lazy-binds', c:getGlobal('object-literal-lazy-binds') + 1))"/>
                 </xsl:when>
             </xsl:choose>
        </xsl:for-each>
        <xsl:apply-templates select="f3:expr/*"/>
    </xsl:template>
  
    <xsl:template match="f3:object-literal"> 
        <!-- check if we have atleast one initialization with bind expr -->
        <xsl:if test="f3:defs/f3:object-literal-init/f3:bind-status != 'unbound'">
            <xsl:if test="not(c:putGlobal('object-literals-with-binds', c:getGlobal('object-literals-with-binds') + 1))"/>
        </xsl:if>
        <xsl:apply-imports/>
    </xsl:template>

</xsl:transform>
