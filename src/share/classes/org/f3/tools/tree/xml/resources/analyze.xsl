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
    This stylesheet serves as "base" template which other sheets can import.
-->
<xsl:transform version="1.0"
               xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
               xmlns:c="http://xml.apache.org/xalan/java/org.f3.tools.tree.xml.Compiler"
               xmlns:f3="http://f3.org">
    
    <xsl:strip-space elements="*"/>
    <xsl:template match="@*|node()"/>

    <xsl:template match="/">
      <xsl:apply-templates select="f3:f3"/>
    </xsl:template>

    <xsl:template match="f3:f3">
        <xsl:apply-templates select="f3:file"/>
        <xsl:apply-templates select="f3:package"/>
        <xsl:apply-templates select="f3:defs"/>
    </xsl:template>

    <xsl:template match="f3:file"/>
    <xsl:template match="f3:package"/>
   
    <xsl:template match="f3:defs">
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="f3:import"/>
    <xsl:template match="f3:bind-status"/>
    
    <xsl:template match="f3:var">
        <xsl:apply-templates select="f3:init-value/*"/>
        <xsl:apply-templates select="f3:on-replace"/>
        <xsl:apply-templates select="f3:on-invalidate"/>
    </xsl:template>
    <xsl:template match="f3:def">
        <xsl:apply-templates select="f3:init-value/*"/>
        <xsl:apply-templates select="f3:on-replace"/>
        <xsl:apply-templates select="f3:on-invalidate"/>
    </xsl:template>

    <xsl:template match="f3:empty"/>
    
    <xsl:template match="f3:while">
        <xsl:apply-templates select="f3:test/*"/>
        <xsl:apply-templates select="f3:stmt/*"/>
    </xsl:template>
   
    <xsl:template match="f3:try">
        <xsl:apply-templates select="f3:block"/>
        <xsl:apply-templates select="f3:catches/f3:catch"/>
        <xsl:if test="f3:finally">
            <xsl:apply-templates select="f3:finally/f3:block"/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="f3:catch">
        <xsl:apply-templates select="f3:block"/>
    </xsl:template>
    
    <xsl:template match="f3:if">
        <xsl:apply-templates select="f3:test/*"/>
        <xsl:apply-templates select="f3:then/*"/>
        <xsl:if test="f3:else">
            <xsl:apply-templates select="f3:else/*"/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="f3:break"/>
    <xsl:template match="f3:continue"/>
    <xsl:template match="f3:return">
        <xsl:apply-templates select="*"/>
    </xsl:template>
    
    <xsl:template match="f3:throw">
        <xsl:apply-templates select="*"/>
    </xsl:template>
    
    <xsl:template match="f3:invoke">
        <xsl:apply-templates select="f3:method/*"/>
        <xsl:apply-templates select="f3:args/*"/>
    </xsl:template>
    
    <xsl:template match="f3:paren">
        <xsl:apply-templates select="*[1]"/>
    </xsl:template>
    
    <xsl:template name="handle-binary-expr">
        <xsl:param name="operator"/>
        <xsl:apply-templates select="f3:left/*"/>
        <xsl:apply-templates select="f3:right/*"/>
    </xsl:template>
    
    <xsl:template match="f3:assignment">
        <xsl:call-template name="handle-binary-expr">
            <xsl:with-param name="operator"> = </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <!-- compound assignments -->
    <xsl:template match="f3:multiply-assignment">
        <xsl:call-template name="handle-binary-expr">
            <xsl:with-param name="operator"> *= </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:divide-assignment">
        <xsl:call-template name="handle-binary-expr">
            <xsl:with-param name="operator"> /= </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:remainder-assignment">
        <xsl:call-template name="handle-binary-expr">
            <xsl:with-param name="operator"> %= </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:plus-assignment">
        <xsl:call-template name="handle-binary-expr">
            <xsl:with-param name="operator"> += </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:minus-assignment">
        <xsl:call-template name="handle-binary-expr">
            <xsl:with-param name="operator"> -= </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:left-shift-assignment">
        <xsl:call-template name="handle-binary-expr">
            <xsl:with-param name="operator"> &lt;&lt;= </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:right-shift-assignment">
        <xsl:call-template name="handle-binary-expr">
            <xsl:with-param name="operator"> &gt;&gt;= </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:unsigned-right-shift-assignment">
        <xsl:call-template name="handle-binary-expr">
            <xsl:with-param name="operator"> &gt;&gt;&gt;= </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:and-assignment">
        <xsl:call-template name="handle-binary-expr">
            <xsl:with-param name="operator"> &amp;= </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:xor-assignment">
        <xsl:call-template name="handle-binary-expr">
            <xsl:with-param name="operator"> ^= </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:or-assignment">
        <xsl:call-template name="handle-binary-expr">
            <xsl:with-param name="operator"> |= </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <!-- unary operators -->
    <xsl:template match="f3:sizeof">
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template name="handle-unary-expr">
        <xsl:param name="operator"/>
        <xsl:apply-templates select="*[1]"/>
    </xsl:template>
    
    <xsl:template match="f3:postfix-increment">
        <xsl:apply-templates select="*[1]"/>
    </xsl:template>
    
    <xsl:template match="f3:prefix-increment">
        <xsl:call-template name="handle-unary-expr">
            <xsl:with-param name="operator">++</xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:postfix-decrement">
        <xsl:apply-templates select="*[1]"/>
    </xsl:template>
    
    <xsl:template match="f3:prefix-decrement">
        <xsl:call-template name="handle-unary-expr">
            <xsl:with-param name="operator">--</xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:unary-plus">
        <xsl:call-template name="handle-unary-expr">
            <xsl:with-param name="operator">+</xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:unary-minus">
        <xsl:call-template name="handle-unary-expr">
            <xsl:with-param name="operator">-</xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="f3:logical-complement">
        <xsl:call-template name="handle-unary-expr">
            <xsl:with-param name="operator">not </xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <!-- binary operators -->
    <xsl:template match="f3:multiply">
        <xsl:call-template name="handle-binary-expr">
            <xsl:with-param name="operator"> * </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:divide">
        <xsl:call-template name="handle-binary-expr">
            <xsl:with-param name="operator"> / </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:remainder">
        <xsl:call-template name="handle-binary-expr">
            <xsl:with-param name="operator"> mod </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:plus">
        <xsl:call-template name="handle-binary-expr">
            <xsl:with-param name="operator"> + </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:minus">
        <xsl:call-template name="handle-binary-expr">
            <xsl:with-param name="operator"> - </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:left-shift">
        <xsl:call-template name="handle-binary-expr">
            <xsl:with-param name="operator"> &lt;&lt; </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:right-shift">
        <xsl:call-template name="handle-binary-expr">
            <xsl:with-param name="operator"> &gt;&gt; </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:unsigned-right-shift">
        <xsl:call-template name="handle-binary-expr">
            <xsl:with-param name="operator"> &gt;&gt;&gt; </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:less-than">
        <xsl:call-template name="handle-binary-expr">
            <xsl:with-param name="operator"> &lt; </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:greater-than">
        <xsl:call-template name="handle-binary-expr">
            <xsl:with-param name="operator"> &gt; </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:less-than-equal">
        <xsl:call-template name="handle-binary-expr">
            <xsl:with-param name="operator"> &lt;= </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:greater-than-equal">
        <xsl:call-template name="handle-binary-expr">
            <xsl:with-param name="operator"> &gt;= </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:equal-to">
        <xsl:call-template name="handle-binary-expr">
            <xsl:with-param name="operator"> == </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:not-equal-to">
        <xsl:call-template name="handle-binary-expr">
            <xsl:with-param name="operator"> != </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:and">
        <xsl:call-template name="handle-binary-expr">
            <xsl:with-param name="operator"> &amp; </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:xor">
        <xsl:call-template name="handle-binary-expr">
            <xsl:with-param name="operator"> ^ </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:or">
        <xsl:call-template name="handle-binary-expr">
            <xsl:with-param name="operator"> | </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:conditional-and">
        <xsl:call-template name="handle-binary-expr">
            <xsl:with-param name="operator"> and </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:conditional-or">
        <xsl:call-template name="handle-binary-expr">
            <xsl:with-param name="operator"> or </xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="f3:cast">
        <xsl:apply-templates select="f3:expr/*"/>
        <xsl:apply-templates select="f3:type/*" mode="no-colon"/>
    </xsl:template>
    
    <xsl:template match="f3:instanceof">
        <xsl:apply-templates select="f3:expr/*"/>
        <xsl:apply-templates select="f3:type/*"/>
    </xsl:template>
    
    <xsl:template match="f3:select">
        <xsl:apply-templates select="f3:expr/*"/>
        <xsl:apply-templates select="f3:member/*"/>
    </xsl:template>
    
    <xsl:template match="f3:ident"/>
    
    <!-- literals -->
    <xsl:template match="f3:int-literal"/>
    <xsl:template match="f3:long-literal"/>
    <xsl:template match="f3:float-literal"/>
    <xsl:template match="f3:double-literal"/>
    <xsl:template match="f3:true"/>
    <xsl:template match="f3:false"/>
    <xsl:template match="f3:string-literal"/>
    <xsl:template match="f3:null"/>
    
    <!-- modifiers -->
    <xsl:template match="f3:modifiers"/>
    
    <xsl:template name="handle-list">
        <xsl:param name="parent"/>
        <xsl:for-each select="$parent/*">
            <xsl:apply-templates select="."/>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="f3:value">
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="f3:block">
        <xsl:call-template name="handle-list">
            <xsl:with-param name="parent" select="f3:stmts"/>
        </xsl:call-template>
        <xsl:apply-templates select="f3:value"/>
    </xsl:template>
    
    <!-- importing stylesheet may override these -->
    <xsl:template name="class-body-begin"/>
    <xsl:template name="class-body-end"/>
    
    <xsl:template name="handle-class-body">
        <xsl:call-template name="class-body-begin"/>
        <xsl:call-template name="handle-list">
            <xsl:with-param name="parent" select="f3:members"/>
        </xsl:call-template>
        <xsl:call-template name="class-body-end"/>
    </xsl:template>
    
    <xsl:template match="f3:class">
        <xsl:apply-templates select="f3:modifiers"/>
        
        <!-- class body -->
        <xsl:call-template name="handle-class-body"/>
    </xsl:template>
    
    <xsl:template match="f3:for">
        <xsl:for-each select="f3:in/*">
            <xsl:apply-templates select="f3:var"/>
            <xsl:apply-templates select="f3:seq/*"/>
            <xsl:if test="f3:where">
                <xsl:apply-templates select="f3:where/*"/>
            </xsl:if>
        </xsl:for-each>
        <xsl:apply-templates select="f3:body/*"/>
    </xsl:template>
    
    <xsl:template match="f3:indexof">
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="f3:init">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="f3:postinit">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="f3:new">
        <xsl:apply-templates select="f3:class/*"/>
        <xsl:call-template name="handle-list">
            <xsl:with-param name="parent" select="f3:args"/>
        </xsl:call-template>
    </xsl:template>
            
    <xsl:template name="object-literal-begin"/>
    <xsl:template name="object-literal-end"/>
    <xsl:template match="f3:object-literal">
        <xsl:apply-templates select="f3:class/*"/>
        <xsl:call-template name="object-literal-begin"/>
        <xsl:for-each select="f3:defs/*">
            <xsl:apply-templates select="."/>
        </xsl:for-each>
        <xsl:call-template name="object-literal-end"/>
    </xsl:template>
    
    <xsl:template match="f3:object-literal-init">
        <xsl:apply-templates select="f3:bind-status"/>
        <xsl:apply-templates select="f3:expr/*"/>
    </xsl:template>
   
   <xsl:template match="f3:override-var">
        <xsl:apply-templates select="f3:expr/*"/>
        <xsl:apply-templates select="f3:on-replace"/>
        <xsl:apply-templates select="f3:on-invalidate"/>
    </xsl:template>

    <xsl:template name="handle-on-replace-clause">
        <xsl:apply-templates select="f3:old-value/*"/>
        <xsl:apply-templates select="f3:first-index/*"/>
        <xsl:apply-templates select="f3:last-index/*"/>
        <xsl:apply-templates select="f3:new-elements/*"/>
        <xsl:apply-templates select="f3:block"/>
    </xsl:template>

    <xsl:template match="f3:on-replace">
        <xsl:call-template name="handle-on-replace-clause"/>
    </xsl:template>

    <xsl:template match="f3:on-invalidate">
        <xsl:call-template name="handle-on-replace-clause"/>
    </xsl:template>

    <!-- functions -->
    <xsl:template name="function-body-begin"/>
    <xsl:template name="function-body-end"/>
    <xsl:template name="handle-function-body">
        <xsl:apply-templates select="f3:params/*"/>
        <xsl:apply-templates select="f3:return-type/*"/> 
        <xsl:choose>
            <xsl:when test="f3:block">
                <xsl:call-template name="function-body-begin"/>
                <xsl:call-template name="handle-list">
                    <xsl:with-param name="parent" select="f3:block/f3:stmts"/>
                </xsl:call-template>
                <xsl:apply-templates select="f3:block/f3:value"/>
                <xsl:call-template name="function-body-end"/>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="f3:function">
        <xsl:variable name="name" select="f3:name"/>
        <xsl:apply-templates select="f3:modifiers"/>
        <xsl:call-template name="handle-function-body"/>
    </xsl:template>
    
    <xsl:template match="f3:anon-function">
        <xsl:call-template name="handle-function-body"/>
    </xsl:template>
    
    <xsl:template match="f3:seq-delete">
        <xsl:apply-templates select="f3:elem/*"/>
        <xsl:apply-templates select="f3:seq/*"/>
    </xsl:template>
    
    <xsl:template match="f3:seq-empty"/>
    
    <xsl:template match="f3:seq-explicit">
        <xsl:call-template name="handle-list">
            <xsl:with-param name="parent" select="f3:items"/>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="f3:seq-indexed">
        <xsl:apply-templates select="f3:seq/*"/>
        <xsl:apply-templates select="f3:index/*"/>
    </xsl:template>
    
    <xsl:template match="f3:seq-slice">
        <xsl:apply-templates select="f3:seq/*"/>
        <xsl:apply-templates select="f3:first/*"/>
        <xsl:apply-templates select="f3:last/*"/>
    </xsl:template>
    
    <xsl:template match="f3:seq-insert">
        <xsl:apply-templates select="f3:elem/*"/>
        <xsl:apply-templates select="f3:seq/*"/>
    </xsl:template>
    
    <xsl:template match="f3:seq-range">
        <xsl:apply-templates select="f3:lower/*"/>
        <xsl:apply-templates select="f3:upper/*"/>
        <xsl:apply-templates select="f3:step/*"/>
    </xsl:template>

    <xsl:template match="f3:invalidate">
        <xsl:apply-templates select="f3:var/*"/>
    </xsl:template>
    
    <xsl:template match="f3:string-expr">
        <xsl:for-each select="f3:part">
            <xsl:choose>
                <xsl:when test="f3:string-literal">
                    <xsl:apply-templates/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:apply-templates select="f3:expr/*"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="f3:time-literal"/>

    <xsl:template match="f3:interpolate-value">
       <xsl:if test="f3:attribute">
           <xsl:for-each select="f3:attribute/*">
               <xsl:apply-templates select="."/>
           </xsl:for-each>
       </xsl:if>
       <xsl:for-each select="f3:value/*">
           <xsl:apply-templates select="."/>
       </xsl:for-each>
       <xsl:if test="f3:interpolation">
           <xsl:apply-templates select="f3:interpolation/*"/>
       </xsl:if>
    </xsl:template>
    
    <xsl:template match="f3:keyframe-literal">
        <xsl:apply-templates select="f3:start-dur/*"/>
        <xsl:for-each select="f3:interpolation-values/*">
            <xsl:apply-templates select="."/>
        </xsl:for-each>
        <xsl:if test="f3:trigger">
            <xsl:apply-templates select="./*"/>
        </xsl:if> 
    </xsl:template>

</xsl:transform>
