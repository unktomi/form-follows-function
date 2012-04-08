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

<!--
    This stylesheet converts XML representation of F3 AST (abstract
    syntax tree) to F3 source code form.
-->
<xsl:transform version="1.0"
               xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
               xmlns:c="http://xml.apache.org/xalan/java/org.f3.tools.tree.xml.Compiler"
               xmlns:f3="http://f3.org">
    
    <xsl:strip-space elements="*"/>
    <xsl:output method="text"/>
    
    <!-- prints 4 spaces for each 'tab' -->
    <xsl:template name="print-spaces">
        <xsl:param name="tabs"/>
        <xsl:text>    </xsl:text>
        <xsl:if test="not($tabs = 1)">
            <xsl:call-template name="print-spaces">
                <xsl:with-param name="tabs">
                    <xsl:value-of select="$tabs - 1"/>
                </xsl:with-param>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>
    
    <!-- initialize (current) number of tabs for alignment -->
    <xsl:template name="init-tabs">
        <xsl:value-of select="c:putGlobal('tabs', 0)"/>
    </xsl:template>
    
    <!-- increment number of tabs -->
    <xsl:template name="indent">
        <!-- hack to have only side-effect and ignore the result -->
        <xsl:if test="not(c:putGlobal('tabs', c:getGlobal('tabs') + 1))"/>
    </xsl:template>
    
    <!-- decrement number of tabs -->
    <xsl:template name="undent">
        <!-- hack to have only side-effect and ignore the result -->
        <xsl:if test="not(c:putGlobal('tabs', c:getGlobal('tabs') - 1))"/>
    </xsl:template>
    
    <!-- align prints spaces as per current tab count -->
    <xsl:template name="align">
        <xsl:if test="not(c:getGlobal('tabs') = 0)">
            <xsl:call-template name="print-spaces">
                <xsl:with-param name="tabs" select="c:getGlobal('tabs')"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>
    
    <!-- print a new line in output -->
    <xsl:template name="println">
        <xsl:text>&#xa;</xsl:text>
    </xsl:template>
    
    <!-- print a list of items separated by given separator -->
    <xsl:template name="print-separator-list">
        <xsl:param name="parent"/>
        <xsl:param name="sep"/>
        <xsl:for-each select="$parent/*">
            <xsl:apply-templates select="."/>
            <xsl:if test="not(position()=last())">
                <xsl:value-of select="$sep"/>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>
    
    <!-- print a comma separated list of items -->
    <xsl:template name="print-comma-list">
        <xsl:param name="parent"/>
        <xsl:call-template name="print-separator-list">
            <xsl:with-param name="parent" select="$parent"/>
            <xsl:with-param name="sep">, </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="@*|node()"/>

    <xsl:template match="/">
      <xsl:apply-templates select="f3:f3"/>
    </xsl:template>

    <xsl:template match="f3:f3">
        <xsl:call-template name="init-tabs"/>
        <xsl:apply-templates select="f3:file"/>
        <xsl:apply-templates select="f3:package"/>
        <xsl:apply-templates select="f3:defs"/>
    </xsl:template>

    <xsl:template match="f3:file">
        <xsl:text>// compiled from </xsl:text>
        <xsl:value-of select="."/>
        <xsl:call-template name="println"/>
    </xsl:template>
    
    <xsl:template match="f3:package">
        <xsl:text>package </xsl:text> 
        <xsl:apply-templates/>
        <xsl:text>;</xsl:text>
        <xsl:call-template name="println"/>
        <xsl:call-template name="println"/>
    </xsl:template>
   
    <xsl:template match="f3:defs">
        <xsl:for-each select="*">
            <xsl:apply-templates select="."/>
            <xsl:call-template name="println"/>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="f3:import">
        <xsl:text>import </xsl:text>
        <xsl:apply-templates/>
        <xsl:text>;</xsl:text>
    </xsl:template>
    
    <xsl:template match="f3:bind-status">
        <xsl:choose>
            <xsl:when test=". = 'bind'">
                <xsl:text> bind </xsl:text>
            </xsl:when>
            <xsl:when test=". = 'bind-with-inverse'">
                <xsl:text> bind </xsl:text>
            </xsl:when>
            <xsl:when test=". = 'bind-lazy'">
                <xsl:text> bind lazy </xsl:text>
            </xsl:when> 
            <xsl:when test=". = 'bind-lazy-with-inverse'">
                <xsl:text> bind lazy </xsl:text>
            </xsl:when>
            <xsl:when test=". = 'unbound-lazy'">
                <xsl:text> lazy </xsl:text>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="f3:bind-status" mode="suffix">
        <xsl:choose>
            <xsl:when test=". = 'bind-with-inverse'">
                <xsl:text> with inverse</xsl:text>
            </xsl:when>
            <xsl:when test=". = 'bind-lazy-with-inverse'">
                <xsl:text> with inverse</xsl:text>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template name="print-param">
        <!-- name of the param -->
        <xsl:if test="f3:name">
            <xsl:value-of select="f3:name"/>
        </xsl:if>
        
        <!-- print type -->
        <xsl:if test="f3:type">
            <xsl:apply-templates select="f3:type/*"/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="print-var" >
        <xsl:apply-templates select="f3:modifiers"/>
        <xsl:choose>
            <xsl:when test="name(.) = 'f3:var'">
                <xsl:text>var </xsl:text>
            </xsl:when>
            <xsl:when test="name(.) = 'f3:def'">
                <xsl:text>def </xsl:text>
            </xsl:when>
        </xsl:choose>
            
        
        <!-- name of the variable/attribute -->
        <xsl:if test="f3:name">
            <xsl:value-of select="f3:name"/>
        </xsl:if>
        
        <!-- print type -->
        <xsl:if test="f3:type">
            <xsl:apply-templates select="f3:type/*"/>
        </xsl:if>
        
        <!-- optional initializer -->
        <xsl:if test="f3:init-value">
            <xsl:text> = </xsl:text>
            <xsl:apply-templates select="f3:bind-status"/>
            <xsl:apply-templates select="f3:init-value/*"/>
            <xsl:apply-templates select="f3:bind-status" mode="suffix"/>
        </xsl:if>
        
        <xsl:apply-templates select="f3:on-replace"/>
        <xsl:apply-templates select="f3:on-invalidate"/>
    </xsl:template>
    
    <xsl:template match="f3:var">
        <xsl:call-template name="print-var"/>
        <xsl:text>;</xsl:text>
    </xsl:template>
    
    <xsl:template match="f3:def">
        <xsl:call-template name="print-var"/>
        <xsl:text>;</xsl:text>
    </xsl:template>
    
    <xsl:template match="f3:empty">
        <xsl:text>;</xsl:text>
    </xsl:template>
    
    <xsl:template match="f3:while">
        <xsl:text>while (</xsl:text>
        <xsl:apply-templates select="f3:test/*"/>
        <xsl:text>) </xsl:text>
        <xsl:apply-templates select="f3:stmt/*"/>
    </xsl:template>
   
    <xsl:template match="f3:try">
        <xsl:text>try </xsl:text>
        <xsl:apply-templates select="f3:block"/>
        <xsl:apply-templates select="f3:catches/f3:catch"/>
        <xsl:if test="f3:finally">
            <xsl:text> finally </xsl:text>
            <xsl:apply-templates select="f3:finally/f3:block"/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="f3:catch">
        <xsl:text> catch (</xsl:text>
        <xsl:for-each select="f3:var">
            <xsl:call-template name="print-param"/>
        </xsl:for-each>
        <xsl:text>) </xsl:text>
        <xsl:apply-templates select="f3:block"/>
    </xsl:template>
    
    <xsl:template match="f3:if">
        <xsl:text>if (</xsl:text>
        <xsl:apply-templates select="f3:test/*"/>
        <xsl:text>) </xsl:text>
        <xsl:apply-templates select="f3:then/*"/>
        <xsl:if test="f3:else">
            <xsl:text> else </xsl:text>
            <xsl:apply-templates select="f3:else/*"/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="f3:break">
        <xsl:text>break</xsl:text>
        <xsl:if test="f3:label">
            <xsl:text> </xsl:text>
            <xsl:value-of select="f3:label"/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="f3:continue">
        <xsl:text>continue</xsl:text>
        <xsl:if test="f3:label">
            <xsl:text> </xsl:text>
            <xsl:value-of select="f3:label"/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="f3:return">
        <xsl:text>return</xsl:text>
        <xsl:if test="*">
            <xsl:text> </xsl:text>
            <xsl:apply-templates select="*"/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="f3:throw">
        <xsl:text>throw</xsl:text>
        <xsl:text> </xsl:text>
        <xsl:apply-templates select="*"/>
    </xsl:template>
    
    <xsl:template match="f3:invoke">
        <xsl:apply-templates select="f3:method/*"/>
        <xsl:text>(</xsl:text>
        <xsl:call-template name="print-comma-list">
            <xsl:with-param name="parent" select="f3:args"/>
        </xsl:call-template>
        <xsl:text>)</xsl:text>
    </xsl:template>
    
    <xsl:template match="f3:paren">
        <xsl:text>(</xsl:text>
        <xsl:apply-templates select="*[1]"/>
        <xsl:text>)</xsl:text>
    </xsl:template>
    
    <xsl:template name="print-binary-expr">
        <xsl:param name="operator"/>
        <xsl:apply-templates select="f3:left/*"/>
        <xsl:value-of select="$operator"/>
        <xsl:apply-templates select="f3:right/*"/>
    </xsl:template>
    
    <xsl:template match="f3:assignment">
        <xsl:call-template name="print-binary-expr">
            <xsl:with-param name="operator"> = </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <!-- compound assignments -->
    <xsl:template match="f3:multiply-assignment">
        <xsl:call-template name="print-binary-expr">
            <xsl:with-param name="operator"> *= </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:divide-assignment">
        <xsl:call-template name="print-binary-expr">
            <xsl:with-param name="operator"> /= </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:remainder-assignment">
        <xsl:call-template name="print-binary-expr">
            <xsl:with-param name="operator"> %= </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:plus-assignment">
        <xsl:call-template name="print-binary-expr">
            <xsl:with-param name="operator"> += </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:minus-assignment">
        <xsl:call-template name="print-binary-expr">
            <xsl:with-param name="operator"> -= </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:left-shift-assignment">
        <xsl:call-template name="print-binary-expr">
            <xsl:with-param name="operator"> &lt;&lt;= </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:right-shift-assignment">
        <xsl:call-template name="print-binary-expr">
            <xsl:with-param name="operator"> &gt;&gt;= </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:unsigned-right-shift-assignment">
        <xsl:call-template name="print-binary-expr">
            <xsl:with-param name="operator"> &gt;&gt;&gt;= </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:and-assignment">
        <xsl:call-template name="print-binary-expr">
            <xsl:with-param name="operator"> &amp;= </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:xor-assignment">
        <xsl:call-template name="print-binary-expr">
            <xsl:with-param name="operator"> ^= </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:or-assignment">
        <xsl:call-template name="print-binary-expr">
            <xsl:with-param name="operator"> |= </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <!-- unary operators -->
    <xsl:template match="f3:sizeof">
        <xsl:text>sizeof </xsl:text>
        <xsl:apply-templates/>
        <xsl:text></xsl:text>
    </xsl:template>
    
    <xsl:template name="print-unary-expr">
        <xsl:param name="operator"/>
        <xsl:value-of select="$operator"/>
        <xsl:apply-templates select="*[1]"/>
    </xsl:template>
    
    <xsl:template match="f3:postfix-increment">
        <xsl:apply-templates select="*[1]"/>
        <xsl:text>++</xsl:text>
    </xsl:template>
    
    <xsl:template match="f3:prefix-increment">
        <xsl:call-template name="print-unary-expr">
            <xsl:with-param name="operator">++</xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:postfix-decrement">
        <xsl:apply-templates select="*[1]"/>
        <xsl:text>--</xsl:text>
    </xsl:template>
    
    <xsl:template match="f3:prefix-decrement">
        <xsl:call-template name="print-unary-expr">
            <xsl:with-param name="operator">--</xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:unary-plus">
        <xsl:call-template name="print-unary-expr">
            <xsl:with-param name="operator">+</xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:unary-minus">
        <xsl:call-template name="print-unary-expr">
            <xsl:with-param name="operator">-</xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="f3:logical-complement">
        <xsl:call-template name="print-unary-expr">
            <xsl:with-param name="operator">not </xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <!-- binary operators -->
    <xsl:template match="f3:multiply">
        <xsl:call-template name="print-binary-expr">
            <xsl:with-param name="operator"> * </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:divide">
        <xsl:call-template name="print-binary-expr">
            <xsl:with-param name="operator"> / </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:remainder">
        <xsl:call-template name="print-binary-expr">
            <xsl:with-param name="operator"> mod </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:plus">
        <xsl:call-template name="print-binary-expr">
            <xsl:with-param name="operator"> + </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:minus">
        <xsl:call-template name="print-binary-expr">
            <xsl:with-param name="operator"> - </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:left-shift">
        <xsl:call-template name="print-binary-expr">
            <xsl:with-param name="operator"> &lt;&lt; </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:right-shift">
        <xsl:call-template name="print-binary-expr">
            <xsl:with-param name="operator"> &gt;&gt; </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:unsigned-right-shift">
        <xsl:call-template name="print-binary-expr">
            <xsl:with-param name="operator"> &gt;&gt;&gt; </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:less-than">
        <xsl:call-template name="print-binary-expr">
            <xsl:with-param name="operator"> &lt; </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:greater-than">
        <xsl:call-template name="print-binary-expr">
            <xsl:with-param name="operator"> &gt; </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:less-than-equal">
        <xsl:call-template name="print-binary-expr">
            <xsl:with-param name="operator"> &lt;= </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:greater-than-equal">
        <xsl:call-template name="print-binary-expr">
            <xsl:with-param name="operator"> &gt;= </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:equal-to">
        <xsl:call-template name="print-binary-expr">
            <xsl:with-param name="operator"> == </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:not-equal-to">
        <xsl:call-template name="print-binary-expr">
            <xsl:with-param name="operator"> != </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:and">
        <xsl:call-template name="print-binary-expr">
            <xsl:with-param name="operator"> &amp; </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:xor">
        <xsl:call-template name="print-binary-expr">
            <xsl:with-param name="operator"> ^ </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:or">
        <xsl:call-template name="print-binary-expr">
            <xsl:with-param name="operator"> | </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:conditional-and">
        <xsl:call-template name="print-binary-expr">
            <xsl:with-param name="operator"> and </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:conditional-or">
        <xsl:call-template name="print-binary-expr">
            <xsl:with-param name="operator"> or </xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="f3:cast">
        <xsl:text>(</xsl:text>
        <xsl:apply-templates select="f3:expr/*"/>
        <xsl:text> as </xsl:text>
        <xsl:apply-templates select="f3:type/*" mode="no-colon"/>
        <xsl:text>)</xsl:text>
    </xsl:template>
    
    <xsl:template match="f3:instanceof">
        <xsl:apply-templates select="f3:expr/*"/>
        <xsl:text> instanceof </xsl:text>
        <xsl:apply-templates select="f3:type/*"/>
    </xsl:template>
    
    <xsl:template match="f3:select">
        <xsl:apply-templates select="f3:expr/*"/>
        <xsl:text>.</xsl:text>
        <xsl:value-of select="f3:member"/>
    </xsl:template>
    
    <xsl:template match="f3:ident">
        <xsl:value-of select="."/>
    </xsl:template>
    
    <!-- literals -->
    
    <xsl:template match="f3:int-literal">
        <xsl:value-of select="."/>
    </xsl:template>

    <xsl:template match="f3:long-literal">
        <xsl:value-of select="."/>
    </xsl:template>
    
    <xsl:template match="f3:float-literal">
        <xsl:value-of select="."/>
    </xsl:template>
    
    <xsl:template match="f3:double-literal">
        <xsl:value-of select="."/>
    </xsl:template>
    
    <xsl:template match="f3:true">
        <xsl:text>true</xsl:text>
    </xsl:template>
    
    <xsl:template match="f3:false">
        <xsl:text>false</xsl:text>
    </xsl:template>
    
    <!-- set flag to tell if we have q:quoteChar function -->
    <xsl:variable name="quote-char-exists" select="function-available('c:quoteChar')"/>
    
    <!-- set flag to tell if we have q:quoteString function -->
    <xsl:variable name="quote-string-exists" select="function-available('c:quoteString')"/>
    
    <xsl:template name="print-quoted-string">
        <xsl:param name="str"/>
        <xsl:choose>
            <xsl:when test="$quote-string-exists">
                <xsl:value-of select="c:quoteString($str)"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:message>WARNING : c:quoteString() is not available!</xsl:message>
                <xsl:value-of select="$str"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="f3:string-literal">
        <xsl:text>"</xsl:text>
        <xsl:value-of select="."/>
        <!--
        <xsl:call-template name="print-quoted-string">
            <xsl:with-param name="str" select="."/>
        </xsl:call-template>
        -->
        <xsl:text>"</xsl:text>
    </xsl:template>
    
    <xsl:template match="f3:null">
        <xsl:text>null</xsl:text>
    </xsl:template>
    
    <!-- modifiers -->
    <xsl:template match="f3:modifiers">
        <xsl:for-each select="f3:li">
            <xsl:variable name="flag">
                <xsl:value-of select="."/>
            </xsl:variable>
            <xsl:choose>
            <!-- script-private is "default" access -->
            <xsl:when test="$flag = 'script-private'">
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$flag"/>
                <xsl:text> </xsl:text>
            </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template name="print-stmts">
        <xsl:param name="parent"/>
        <xsl:param name="semicolon">true</xsl:param>
        <xsl:for-each select="$parent/*">
            <xsl:call-template name="align"/>
            <xsl:apply-templates select="."/>
            <xsl:if test="$semicolon">
                <xsl:text>;</xsl:text>
            </xsl:if>
            <xsl:call-template name="println"/>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="f3:value">
        <xsl:call-template name="align"/>
        <xsl:apply-templates/>
        <xsl:call-template name="println"/>
    </xsl:template>
    
    <!-- treat file-level blocks as special, don't include braces around -->
    <xsl:template match="f3:f3/f3:defs/f3:block">
        <xsl:call-template name="print-stmts">
        <xsl:with-param name="parent" select="f3:stmts"/>
        </xsl:call-template>
        <xsl:apply-templates select="f3:value"/>
    </xsl:template>

    <xsl:template match="f3:block">
        <xsl:text>{</xsl:text>
        <xsl:call-template name="println"/>
        <xsl:call-template name="indent"/>
        <xsl:call-template name="print-stmts">
            <xsl:with-param name="parent" select="f3:stmts"/>
        </xsl:call-template>
        <xsl:apply-templates select="f3:value"/>
        <xsl:call-template name="undent"/>
        <xsl:call-template name="align"/>
        <xsl:text>}</xsl:text>
    </xsl:template>
    
    <xsl:template name="print-super-types">
        <xsl:if test="f3:extends">
            <xsl:text> extends </xsl:text>
            <xsl:call-template name="print-comma-list">
                <xsl:with-param name="parent" select="f3:extends"/>
            </xsl:call-template>
            <xsl:text> </xsl:text>
        </xsl:if>
    </xsl:template>
    
    <!-- importing stylesheet may override these -->
    <xsl:template name="class-body-begin"/>
    <xsl:template name="class-body-end"/>
    
    <xsl:template name="print-class-body">
        <xsl:text>{</xsl:text>
        <xsl:call-template name="class-body-begin"/>
        
        <xsl:call-template name="println"/>
        <xsl:call-template name="indent"/>
        <xsl:call-template name="print-stmts">
            <xsl:with-param name="parent" select="f3:members"/>
            <xsl:with-param name="semicolon" select="false"/>
        </xsl:call-template>
        <xsl:call-template name="undent"/>
        <xsl:call-template name="align"/>
        
        <xsl:call-template name="class-body-end"/>
        <xsl:text>}</xsl:text>
    </xsl:template>
    
    <xsl:template match="f3:class">
        <xsl:call-template name="println"/>
        <xsl:call-template name="align"/>
        <xsl:apply-templates select="f3:modifiers"/>
        <xsl:text>class </xsl:text>
        <xsl:value-of select="f3:name"/>
        
        <xsl:call-template name="print-super-types"/>
        
        <!-- class body -->
        <xsl:text> </xsl:text>
        <xsl:call-template name="print-class-body"/>
    </xsl:template>
    
    <xsl:template match="f3:for">
        <xsl:text>for (</xsl:text>
        <xsl:for-each select="f3:in/*">
            <xsl:if test="not(position() = 1)">
                <xsl:text>, </xsl:text>
            </xsl:if>
            <xsl:value-of select="f3:var/f3:name"/>
            <xsl:apply-templates select="f3:var/f3:type/*"/>
            <xsl:text> in </xsl:text>
            <xsl:apply-templates select="f3:seq/*"/>
            <xsl:if test="f3:where">
                <xsl:text> where </xsl:text>
                <xsl:apply-templates select="f3:where/*"/>
            </xsl:if>
        </xsl:for-each>
        <xsl:text>) </xsl:text>
        <xsl:apply-templates select="f3:body/*"/>
    </xsl:template>
    
    <xsl:template match="f3:indexof">
        <xsl:text>indexof </xsl:text>
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="f3:init">
        <xsl:call-template name="println"/>
        <xsl:call-template name="align"/>
        <xsl:text>init </xsl:text>
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="f3:postinit">
        <xsl:call-template name="println"/>
        <xsl:call-template name="align"/>
        <xsl:text>postinit </xsl:text>
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="f3:new">
        <xsl:text>new </xsl:text>
        <xsl:apply-templates select="f3:class/*"/>
        <xsl:text>(</xsl:text>
            <xsl:call-template name="print-comma-list">
                <xsl:with-param name="parent" select="f3:args"/>
            </xsl:call-template>
        <xsl:text>)</xsl:text>
    </xsl:template>
            
    <xsl:template name="object-literal-begin"/>
    <xsl:template name="object-literal-end"/>
    <xsl:template match="f3:object-literal">
        <xsl:apply-templates select="f3:class/*"/>
        <xsl:text> {</xsl:text>
        <xsl:call-template name="object-literal-begin"/>
        <xsl:call-template name="indent"/>
        <xsl:for-each select="f3:defs/*">
            <xsl:call-template name="println"/>
            <xsl:call-template name="align"/>
            <xsl:apply-templates select="."/>
        </xsl:for-each>
        <xsl:call-template name="undent"/>
        <xsl:call-template name="println"/>
        <xsl:call-template name="align"/>
        <xsl:call-template name="object-literal-end"/>
        <xsl:text>}</xsl:text>
        <xsl:call-template name="println"/>
        <xsl:call-template name="align"/>
    </xsl:template>
    
    <xsl:template match="f3:object-literal-init">
        <xsl:value-of select="f3:name"/>
        <xsl:text> : </xsl:text>
        <xsl:apply-templates select="f3:bind-status"/>
        <xsl:apply-templates select="f3:expr/*"/>
        <xsl:apply-templates select="f3:bind-status" mode="suffix"/>
    </xsl:template>
   
   <xsl:template match="f3:override-var">
        <xsl:text>override var </xsl:text>
        <xsl:value-of select="./f3:name"/>
        <xsl:apply-templates select="f3:expr/*"/>
        <xsl:text> </xsl:text>
        <xsl:apply-templates select="f3:on-replace"/>
        <xsl:apply-templates select="f3:on-invalidate"/>
    </xsl:template>

    <xsl:template name="handle-on-replace-clause">
        <xsl:if test="f3:old-value">
            <xsl:text>  </xsl:text>
            <xsl:value-of select="f3:old-value/f3:var/f3:name"/>
        </xsl:if>
        <xsl:if test="f3:first-index">
            <xsl:text> [ </xsl:text>
            <xsl:value-of select="f3:first-index/f3:var/f3:name"/>
            <xsl:if test="f3:last-index">
                <xsl:text> .. </xsl:text>
                <xsl:value-of select="f3:last-index/f3:var/f3:name"/>
            </xsl:if>
            <xsl:text> ] </xsl:text>
        </xsl:if>
        <xsl:if test="f3:new-elements">
            <xsl:value-of select="f3:new-elements/f3:var/f3:name"/>
        </xsl:if>
        <xsl:apply-templates select="f3:block"/>
    </xsl:template>

    <xsl:template match="f3:on-replace">
        <xsl:text> on replace </xsl:text>
        <xsl:call-template name="handle-on-replace-clause"/>
    </xsl:template>

    <xsl:template match="f3:on-invalidate">
        <xsl:text> on invalidate </xsl:text>
        <xsl:call-template name="handle-on-replace-clause"/>
    </xsl:template>

    <!-- functions -->
    <xsl:template name="function-body-begin"/>
    <xsl:template name="function-body-end"/>
    <xsl:template name="print-function-body">
        <!-- print the comma separated parameters -->
        <xsl:text>(</xsl:text>
        <xsl:for-each select="f3:params/*">
            <xsl:call-template name="print-param"/>
            <xsl:if test="not(position()=last())">
                <xsl:text>, </xsl:text>
            </xsl:if>
        </xsl:for-each>
        <xsl:text>)</xsl:text>
        
        <!-- return type, if any -->
        <xsl:if test="f3:return-type">
            <xsl:apply-templates select="f3:return-type/*"/> 
        </xsl:if>
        
        <xsl:choose>
            <!-- print the code block, if any -->
            <xsl:when test="f3:block">
                <xsl:text> </xsl:text>
                <xsl:text>{</xsl:text>
                <xsl:call-template name="function-body-begin"/>
                <xsl:call-template name="println"/>
                <xsl:call-template name="indent"/>
                <xsl:call-template name="print-stmts">
                    <xsl:with-param name="parent" select="f3:block/f3:stmts"/>
                </xsl:call-template>
                <xsl:apply-templates select="f3:block/f3:value"/>
                <xsl:call-template name="undent"/>
                <xsl:call-template name="align"/>
                <xsl:call-template name="function-body-end"/>
                <xsl:text>}</xsl:text>
            </xsl:when>
            <!-- abstract/native methods have no body -->
            <xsl:otherwise>
                <xsl:text>;</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="f3:function">
        <xsl:variable name="name" select="f3:name"/>
        <xsl:call-template name="println"/>
        <xsl:call-template name="align"/>
            
        <xsl:apply-templates select="f3:modifiers"/>
        
        <xsl:text>function </xsl:text>
        <!-- method name -->
        <xsl:value-of select="$name"/>
            
        <xsl:call-template name="print-function-body"/>
    </xsl:template>
    
    <xsl:template match="f3:anon-function">
        <xsl:call-template name="println"/>
        <xsl:call-template name="align"/>
        
        <xsl:text>function </xsl:text>
        <xsl:call-template name="print-function-body"/>
    </xsl:template>
    
    <xsl:template match="f3:seq-delete">
        <xsl:text>delete </xsl:text>
        <xsl:if test="f3:elem">
            <xsl:apply-templates select="f3:elem/*"/>
            <xsl:text> from </xsl:text>
        </xsl:if>
        <xsl:apply-templates select="f3:seq/*"/>
    </xsl:template>
    
    <xsl:template match="f3:seq-empty">
        <xsl:text>[]</xsl:text>
    </xsl:template>
    
    <xsl:template match="f3:seq-explicit">
        <xsl:text>[</xsl:text>
        <xsl:call-template name="print-comma-list">
            <xsl:with-param name="parent" select="f3:items"/>
        </xsl:call-template>
        <xsl:text>]</xsl:text>
    </xsl:template>
    
    <xsl:template match="f3:seq-indexed">
        <xsl:apply-templates select="f3:seq/*"/>
        <xsl:text>[</xsl:text>
        <xsl:apply-templates select="f3:index/*"/>
        <xsl:text>]</xsl:text>
    </xsl:template>
    
    <xsl:template match="f3:seq-slice">
        <xsl:apply-templates select="f3:seq/*"/>
        <xsl:text>[</xsl:text>
        <xsl:apply-templates select="f3:first/*"/>
        <xsl:text>..</xsl:text>
        <xsl:if test="f3:slice-end-kind = 'exclusive'">
            <xsl:text>&lt;</xsl:text>
        </xsl:if>
        <xsl:apply-templates select="f3:last/*"/>
        <xsl:text>]</xsl:text>
    </xsl:template>
    
    <xsl:template match="f3:seq-insert">
        <xsl:text>insert </xsl:text>
        <xsl:apply-templates select="f3:elem/*"/>
        <xsl:text> into </xsl:text>
        <xsl:apply-templates select="f3:seq/*"/>
    </xsl:template>
    
    <xsl:template match="f3:seq-range">
        <xsl:text>[</xsl:text>
        <xsl:apply-templates select="f3:lower/*"/>
        <xsl:text>..</xsl:text>
        <xsl:if test="f3:exclusive = 'true'">
            <xsl:text> &lt;</xsl:text>
        </xsl:if>
        <xsl:apply-templates select="f3:upper/*"/>
        <xsl:if test="f3:step">
            <xsl:text> step </xsl:text>
            <xsl:apply-templates select="f3:step/*"/>
        </xsl:if>
        <xsl:text>]</xsl:text>
    </xsl:template>

    <xsl:template match="f3:invalidate">
        <xsl:text>invalidate </xsl:text>
        <xsl:apply-templates select="f3:var/*"/>
    </xsl:template>
    
    <xsl:template match="f3:string-expr">
        <xsl:text>"</xsl:text>
        <xsl:for-each select="f3:part">
            <xsl:choose>
                <xsl:when test="f3:string-literal">
                    <xsl:value-of select="."/>
                    <!--
                    <xsl:call-template name="print-quoted-string">
                        <xsl:with-param name="str" select="."/>
                    </xsl:call-template>
                    -->
                </xsl:when>
                <xsl:otherwise>
                    <xsl:text>{</xsl:text>
                    <xsl:if test="not(f3:format = '')">
                        <xsl:value-of select="f3:format"/>
                        <xsl:text> </xsl:text>
                    </xsl:if>
                    <xsl:apply-templates select="f3:expr/*"/>
                    <xsl:text>}</xsl:text>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
        <xsl:text>"</xsl:text>
    </xsl:template>
    
    <!-- types -->
    <xsl:template match="f3:type-any">
        <xsl:text> : *</xsl:text>
        <xsl:apply-templates select="f3:cardinality"/>
    </xsl:template>
    
    <xsl:template match="f3:type-class">
        <xsl:text> : </xsl:text>
        <xsl:apply-templates select="f3:class/*" mode="no-colon"/>
        <xsl:apply-templates select="f3:cardinality"/>
    </xsl:template>
    
    <xsl:template match="f3:type-functional">
        <xsl:text> : function (</xsl:text>
        <xsl:for-each select="f3:params/*">
            <xsl:apply-templates select="."/>
            <xsl:if test="not(position()=last())">
                <xsl:text>, </xsl:text>
            </xsl:if>
        </xsl:for-each>
        <xsl:text>)</xsl:text>
        <xsl:apply-templates select="f3:return-type/*"/>
        <xsl:apply-templates select="f3:cardinality"/>
    </xsl:template>

    <xsl:template match="f3:type-array">
        <xsl:text> : nativearray of </xsl:text>
        <xsl:apply-templates select="*" mode="no-colon"/>
    </xsl:template>
    
    <xsl:template match="f3:type-any" mode="no-colon">
        <xsl:text> *</xsl:text>
        <xsl:apply-templates select="f3:cardinality"/>
    </xsl:template>
    
    <xsl:template match="f3:type-class" mode="no-colon">
        <xsl:text> </xsl:text>
        <xsl:apply-templates select="f3:class/*"/>
        <xsl:apply-templates select="f3:cardinality"/>
    </xsl:template>
    
    <xsl:template match="f3:type-functional" mode="no-colon">
        <xsl:text> function (</xsl:text>
        <xsl:for-each select="f3:params/*">
            <xsl:apply-templates select="."/>
            <xsl:if test="not(position()=last())">
                <xsl:text>, </xsl:text>
            </xsl:if>
        </xsl:for-each>
        <xsl:text>)</xsl:text>
        <xsl:apply-templates select="f3:return-type/*"/>
        <xsl:apply-templates select="f3:cardinality"/>
    </xsl:template>

    <xsl:template match="f3:type-array" mode="no-colon">
        <xsl:text> nativearray of</xsl:text>
        <xsl:apply-templates select="*" mode="no-colon"/>
    </xsl:template>

    <xsl:template match="f3:type-unknown">
        <xsl:apply-templates select="f3:cardinality"/>
    </xsl:template>
    
    <xsl:template match="f3:cardinality">
        <xsl:choose>
            <xsl:when test=". = 'singleton'"/>
            <xsl:when test=". = 'unknown'"/>
            <xsl:when test=". = 'any'">
                <xsl:text>[]</xsl:text>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="f3:time-literal">
        <xsl:value-of select="."/>
        <xsl:text>ms</xsl:text>
    </xsl:template>

    <xsl:template match="f3:interpolate-value">
       <xsl:if test="f3:attribute">
           <xsl:for-each select="f3:attribute/*">
               <xsl:apply-templates select="."/>
           </xsl:for-each>
       </xsl:if>
       <xsl:text> =&gt; </xsl:text>
       <xsl:for-each select="f3:value/*">
           <xsl:apply-templates select="."/>
       </xsl:for-each>
       <xsl:if test="f3:interpolation">
           <xsl:text> tween </xsl:text>
           <xsl:apply-templates select="f3:interpolation/*"/>
       </xsl:if>
    </xsl:template>
    
    <xsl:template match="f3:keyframe-literal">
        <xsl:text>at (</xsl:text>
        <xsl:apply-templates select="f3:start-dur/*"/>
        <xsl:text>) {</xsl:text>
        <xsl:call-template name="println"/>
        <xsl:for-each select="f3:interpolation-values/*">
            <xsl:apply-templates select="."/>
            <xsl:text>;</xsl:text>
            <xsl:call-template name="println"/>
        </xsl:for-each>
        <xsl:if test="f3:trigger">
            <xsl:text> trigger </xsl:text>
            <xsl:apply-templates select="./*"/>
        </xsl:if> 
        <xsl:text>}</xsl:text>
        <xsl:call-template name="println"/>
    </xsl:template>

</xsl:transform>
