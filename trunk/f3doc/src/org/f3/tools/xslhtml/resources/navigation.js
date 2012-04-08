/*
 * Copyright 2008-2009 Sun Microsystems, Inc.  All Rights Reserved.
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
 */

function setTab(tab) {
/*
    document.getElementById('overview').style.display='none';
    document.getElementById('general').style.display='none';
    document.getElementById('fields').style.display='none';
    document.getElementById('constructors').style.display='none';
    document.getElementById('properties').style.display='none';
    document.getElementById('static').style.display='none';
    document.getElementById(tab).style.display='block';
*/

    var tabs = document.getElementById('tabs');
    var tabsList = tabs.getElementsByTagName('li');
    for(i=0; i< tabsList.length; i++) {
        removeName(tabsList[i],'selected');
    }


    document.getElementById('tab-'+tab).className += 'selected';
    window.location.hash=tab;

}

    
function removeName(el, name) {

    var i, curList, newList;

    // Remove the given class name from the element's className property.

    newList = new Array();
    curList = el.className.split(" ");
    for (i = 0; i < curList.length; i++)
    if (curList[i] != name)
        newList.push(curList[i]);
        el.className = newList.join(" ");
}

function togglecss(myclass,element,value,value2) {
    var CSSRules
    if (document.all) {
        CSSRules = 'rules'
    } else if (document.getElementById) {
        CSSRules = 'cssRules'
    }

    //alert("len = " + document.styleSheets.length);
    for(var j = 0; j< document.styleSheets.length; j++) {
        //alert("sheet rules len = " + document.styleSheets[j][CSSRules].length);
        for (var i = 0; i < document.styleSheets[j][CSSRules].length; i++) {
            if (document.styleSheets[j][CSSRules][i].selectorText == myclass) {
    
                if(document.styleSheets[j][CSSRules][i].style[element] == value) {
                    document.styleSheets[j][CSSRules][i].style[element] = value2
                } else {
                    document.styleSheets[j][CSSRules][i].style[element] = value
                }
            }
        }
    }	
}

function changecss(myclass,element,value) {
    var CSSRules
    if (document.all) {
        CSSRules = 'rules'
    } else if (document.getElementById) {
        CSSRules = 'cssRules'
    }
    for (var i = 0; i < document.styleSheets[0][CSSRules].length; i++) {
        if (document.styleSheets[0][CSSRules][i].selectorText == myclass) {
            document.styleSheets[0][CSSRules][i].style[element] = value
        }
    }	
}


function toggleLayer(whichLayer) {
    if (document.getElementById) {
        // this is the way the standards work
        var style2 = document.getElementById(whichLayer).style;
        style2.display = style2.display? "":"block";
    } else if (document.all) {
        // this is the way old msie versions work
        var style2 = document.all[whichLayer].style;
        style2.display = style2.display? "":"block";
    } else if (document.layers) {
        // this is the way nn4 works
        var style2 = document.layers[whichLayer].style;
        style2.display = style2.display? "":"block";
    }
}
