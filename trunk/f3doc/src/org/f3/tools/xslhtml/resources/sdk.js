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

var sliders;

window.addEvent('domready', function(){
	attachSliders();
	
	attachDescOpen();	

 	attachCollapseExpand();
	
	initProfile();

	//setup initial package
	if(sessvars.currentPackageIndex == undefined) {
		sessvars.currentPackageIndex = 0;
	}
	
	var myAccordion2 = new Accordion($$('h4.header'), $$('ul.content'), {
		display : sessvars.currentPackageIndex,
		alwaysHide: true,
		opacity: false,
		duration: 'short'
	});
	
	$$('h4.header').each(function(lnk,index) {
		lnk.addEvent('click', function(e) { 
			sessvars.currentPackageIndex = index;
		});
	});
	
	
	new Tips('.tooltip', { fixed: true });

});

function attachSliders() {
	sliders = $$('.long-desc').map(function(target) {
		//window.alert("found function");
		return new Visage.Slide(target, {
			duration: 'short'
		}).hide();
	});
}

function attachDescOpen() {
	$$('.long-desc-open').each(function(lnk,index) {
		//window.alert("found desc short index " + sliders[index]);
	
		lnk.addEvent('click', function(e) { 
			//window.alert("in click");
			sliders[index].toggle();
			
			var im = lnk.getElement('img');
                        // from "index.html" page, "images" dir is at same level. For class
                        // level docs "images" directory is at ".."
                        var imageDirPrefix = (im.get('src').indexOf('../') == 0)? '../' : '';
                        if(im.get('rel') == 'open') {
                            im.set('src', imageDirPrefix + 'images/Visage_arrow_right.png').set('rel','close');
                        } else {
                            im.set('src', imageDirPrefix + 'images/Visage_arrow_down.png').set('rel','open');
                        }
                       
			e = new Event(e);
			e.stop();
		});
	});
}

var collapsed = true;
function attachCollapseExpand() {
        $$('#collapse-expand-link').addEvent('click', function(e) {
                // toggle collapsed state
                collapsed = !collapsed;
                $$('.long-desc-open').each(function(lnk,index) {
                        try {
                                lnk.fireEvent("click");
                        } catch (ignored) {}
                });
                var linktext = collapsed? "expand all" : "collapse all";
                $$('#collapse-expand-link').setProperty("text", linktext);
        });

}

function initProfile() {
	//profile switchers
	$('select-desktop-profile').addEvent('click', function(e) {
		switchToDesktop();
	});
	$('select-common-profile').addEvent('click', function(e) {
		switchToCommon();
	});
		
	//setup initial profile
	if(sessvars.currentProfile == undefined) {
		sessvars.currentProfile = "desktop";
	}
	
	if(sessvars.currentProfile=="desktop") {
		switchToDesktop();
	} else {
		switchToCommon();
	}
}

function isdefined( variable)
{
    return (typeof(window[variable]) == "undefined")?  false: true;
}

function switchToDesktop() {
	//--- Set desktop as selected
    $('select-desktop-profile').addClass("selected");
    $('select-common-profile').removeClass("selected");
    	
    $$('li.profile-desktop').fade('in');
    $$('dt.profile-desktop').setStyle('display', 'block');
    $$('dd.profile-desktop').setStyle('display', 'block');
    $$('tr.profile-desktop').setStyle('display', ''); 
	
    sessvars.currentProfile="desktop";
}
function switchToCommon() {
	//--- Set desktop as selected
    $('select-desktop-profile').removeClass("selected");
    $('select-common-profile').addClass("selected");
	
    $$('li.profile-desktop').fade('out');
    $$('dt.profile-desktop').setStyle('display', 'none');
    $$('dd.profile-desktop').setStyle('display', 'none');
    $$('tr.profile-desktop').setStyle('display', 'none'); 
    
    sessvars.currentProfile="common";
}
