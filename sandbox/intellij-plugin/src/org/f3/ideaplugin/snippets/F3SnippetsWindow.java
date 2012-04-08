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

package org.f3.ideaplugin.snippets;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.util.IconLoader;
import com.intellij.peer.PeerFactory;
import com.intellij.ui.content.Content;
import org.f3.ideaplugin.F3Plugin;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;

/**
 * @author David Kaspar
 */
public class F3SnippetsWindow implements ProjectComponent {

    private final Icon ICON_PLUS = IconLoader.getIcon("/icons/inspector/plus.png");
    private final Icon ICON_MINUS = IconLoader.getIcon ("/icons/inspector/minus.png");

    private final Project project;
    private final JTree tree;

    public F3SnippetsWindow (Project proj) {
        project = proj;

        DefaultMutableTreeNode root = new DefaultMutableTreeNode ();

        DefaultMutableTreeNode imports = new CategoryNode ("Imports");
        imports.add (new SnippetsNode ("General Java Imports", "import java.lang.*;\n" + "import java.util.*;\n"));
        imports.add (new SnippetsNode ("General F3 Imports", "import f3.animation.*;\n" + "import f3.application.*;\n" + "import f3.ext.swing.*;\n" + "import f3.input.*;\n" + "import f3.lang.*;\n" + "import f3.scene.*;\n" + "import f3.scene.effect.*;\n" + "import f3.scene.geometry.*;\n" + "import f3.scene.image.*;\n" + "import f3.scene.layout.*;\n" + "import f3.scene.media.*;\n" + "import f3.scene.paint.*;\n" + "import f3.scene.text.*;\n" + "import f3.scene.transform.*;\n"));
        root.add (imports);
        
        DefaultMutableTreeNode apps = new CategoryNode ("Applications");
        apps.add (new SnippetsNode ("Frame", "Frame {\n" + "    title: \"MyApplication\"\n" + "    width: 200\n" + "    height: 200\n" + "    closeAction: function() { java.lang.System.exit( 0 ); }\n" + "    visible: true\n" + "    \n" + "    content: Canvas {\n" + "        content: []\n" + "    }\n" + "}\n", "frame"));
        apps.add (new SnippetsNode ("Application", "Application {\n}\n", "application"));
        apps.add (new SnippetsNode ("CustomNode", "public class MyCustomNode extends CustomNode {\n" + "    \n" + "    public function create(): Node {\n" + "        return Group {\n" + "            content: []\n" + "        };\n" + "    }\n" + "}\n", "custom_node"));
        apps.add (new SnippetsNode ("Stage", "Stage {\n" + "    content: [\n" + "    ]\n" + "}", "canvas"));
        root.add (apps);

        DefaultMutableTreeNode actions = new CategoryNode ("Actions");
        actions.add (new SnippetsNode ("Action", "action: function() {\n}\n", "action"));
        actions.add (new SnippetsNode ("onMouseMoved", "onMouseMoved: function (e:MouseEvent) {\n}\n", "on_mouse_moved"));
        actions.add (new SnippetsNode ("onMouseEntered", "onMouseEntered: function (e:MouseEvent) {\n}\n", "on_mouse_entered"));
        actions.add (new SnippetsNode ("onMouseExited", "onMouseExited: function (e:MouseEvent) {\n}\n", "on_mouse_exited"));
        actions.add (new SnippetsNode ("onMouseClicked", "onMouseClicked: function (e:MouseEvent) {\n}\n", "on_mouse_clicked"));
        actions.add (new SnippetsNode ("onMousePressed", "onMousePressed: function (e:MouseEvent) {\n}\n", "on_mouse_pressed"));
        actions.add (new SnippetsNode ("onMouseReleased", "onMouseReleased: function (e:MouseEvent) {\n}\n", "on_mouse_released"));
        actions.add (new SnippetsNode ("onMouseDragged", "onMouseDragged: function (e:MouseEvent) {\n}\n", "on_mouse_dragged"));
        actions.add (new SnippetsNode ("onMouseWheelMoved", "onMouseWheelMoved: function (e:MouseEvent) {\n}\n", "on_mouse_wheel_moved"));
        actions.add (new SnippetsNode ("onKeyPressed", "onKeyPressed: function (e:KeyEvent) {\n}\n", "on_key_pressed"));
        actions.add (new SnippetsNode ("onKeyReleased", "onKeyReleased: function (e:KeyEvent) {\n}\n", "on_key_released"));
        actions.add (new SnippetsNode ("onKeyTyped", "onKeyTyped: function (e:KeyEvent) {\n}\n", "on_key_typed"));
        root.add (actions);

        DefaultMutableTreeNode shapes = new CategoryNode ("Basic Shapes");
        shapes.add (new SnippetsNode ("Arc", "Arc {\n" + "    centerX: 100, centerY: 100\n" + "    radiusX: 40, radiusY: 15\n" + "    startAngle: 18, length: 120\n" + "    type: ArcType.OPEN\n" + "    fill: Color.GREEN\n" + "}\n", "arc"));
        shapes.add (new SnippetsNode ("Circle", "Circle {\n" + "    centerX: 10, centerY: 10\n" + "    radius: 5\n" + "}\n", "circle"));
        shapes.add (new SnippetsNode ("Ellipse", "Ellipse {\n" + "    centerX: 100, centerY: 100\n" + "    radiusX: 40, radiusY: 15\n" + "    fill: Color.GREEN\n" + "}\n", "ellipse"));
        shapes.add (new SnippetsNode ("Image", "ImageView {\n" + "    image: Image {\n" + "        url: \"{__DIR__}myPicture.png\"\n" + "    }\n" + "}\n", "image"));
        shapes.add (new SnippetsNode ("Line", "Line {\n" + "    x1: 10, y1: 10\n" + "    x2: 10, y2: 10\n" + "    strokeWidth: 1\n" + "}\n", "line"));
        shapes.add (new SnippetsNode ("Polygon", "Polygon {\n" + "    points : [ 0,0, 100,0, 100,100 ]\n" + "    fill: Color.YELLOW\n" + "}\n", "polygon"));
        shapes.add (new SnippetsNode ("Polyline", "Polyline {\n" + "    points : [ 0,0, 100,0, 100,100 ]\n" + "    strokeWidth: 2.0\n" + "    stroke: Color.RED\n" + "}\n", "polyline"));
        shapes.add (new SnippetsNode ("Rectangle", "Rectangle {\n" + "    x: 10, y: 10\n" + "    width: 10, height: 10\n" + "}\n", "rectangle"));
        shapes.add (new SnippetsNode ("Text", "Text {\n" + "    font: Font { \n" + "        size: 24 \n" + "        style: FontStyle.PLAIN\n" + "    }\n" + "    x: 10, y: 30\n" + "    content: \"HelloWorld\"\n" + "}\n", "text"));
        root.add (shapes);

        DefaultMutableTreeNode transforms = new CategoryNode ("Transformations");
        transforms.add (new SnippetsNode ("Rotate", "Rotate { x : 0.0, y : 0.0, angle: 0.0 }", "rotate"));
        transforms.add (new SnippetsNode ("Scale", "Scale { x : 0.0, y : 0.0 }", "scale"));
        transforms.add (new SnippetsNode ("Translate", "Translate { x : 0.0, y : 0.0 }", "move"));
        root.add (transforms);

        DefaultMutableTreeNode colors = new CategoryNode ("Colors");
        colors.add (new SnippetsNode ("Black", "Color.BLACK", "black"));
        colors.add (new SnippetsNode ("Blue", "Color.BLUE", "blue"));
        colors.add (new SnippetsNode ("Cyan", "Color.CYAN", "cyan"));
        colors.add (new SnippetsNode ("Dark Gray", "Color.DARKGRAY", "dark_gray"));
        colors.add (new SnippetsNode ("Gray", "Color.GRAY", "gray"));
        colors.add (new SnippetsNode ("Green", "Color.GREEN", "green"));
        colors.add (new SnippetsNode ("Light Gray", "Color.LIGHTGRAY", "light_gray"));
        colors.add (new SnippetsNode ("Magenta", "Color.MAGENTA", "magenta"));
        colors.add (new SnippetsNode ("Orange", "Color.ORANGE", "orange"));
        colors.add (new SnippetsNode ("Pink", "Color.PINK", "pink"));
        colors.add (new SnippetsNode ("Red", "Color.RED", "red"));
        colors.add (new SnippetsNode ("White", "Color.WHITE", "white"));
        colors.add (new SnippetsNode ("Yellow", "Color.YELLOW", "yellow"));
        root.add (colors);

        DefaultMutableTreeNode timeline = new CategoryNode ("Timeline");
        timeline.add (new SnippetsNode ("Timeline", "Timeline {\n" + "    repeatCount: Timeline.INDEFINITE\n" + "    keyFrames : [\n" + "        KeyFrame {\n" + "            time : 1s\n" + "            \n" + "        }\n" + "    ]\n" + "}\n", "timeline"));
        timeline.add (new SnippetsNode ("KeyFrame", "KeyFrame {\n" + "    time: 1s\n" + "    \n" + "}\n", "key_frame"));
        timeline.add (new SnippetsNode ("Values", "values : {\n" + "    variable => 0.0\n" + "}\n", "values"));
        timeline.add (new SnippetsNode ("Action", "action: function() {\n}\n", "action"));
        root.add (timeline);

        DefaultMutableTreeNode swing = new CategoryNode ("Swing Components");
        swing.add (new SnippetsNode ("ComponentView", "ComponentView {\n" + "    transform: [  ]\n" + "    component: \n" + "}\n", "component_view"));
        swing.add (new SnippetsNode ("SwingFrame", "SwingFrame {\n" + "    title: \"MyApplication\"\n" + "    width: 200\n" + "    height: 200\n" + "    closeAction: function() { java.lang.System.exit( 0 ); }\n" + "    visible: true\n" + "\n" + "    menus: [  ]\n" + "\n" + "    content: Canvas {\n" + "        content: []\n" + "    }\n" + "}\n", "frame"));
        swing.add (new SnippetsNode ("Button", "SwingButton {\n" + "    text: \"Button\"\n" + "    action: function() { \n" + "    }\n" + "}\n", "button"));
        swing.add (new SnippetsNode ("CheckBox", "SwingCheckBox {\n" + "    text: \"CheckBox\"\n" + "}\n", "check_box"));
        swing.add (new SnippetsNode ("ComboBox", "ComboBox {\n" + "    items: [\n" + "        ComboBoxItem {\n" + "            text: \"File\"\n" + "            selected: true\n" + "        }\n" + "    ]\n" + "}\n", "combobox"));
        swing.add (new SnippetsNode ("ComboBoxItem", "ComboBoxItem {\n" + "    text: \"Item\"\n" + "}\n", "combobox_item"));
        swing.add (new SnippetsNode ("Label", "SwingLabel {\n" + "    text: \"Label\"\n" + "}\n", "label"));
        swing.add (new SnippetsNode ("Menu", "Menu {\n" + "    text: \"File\"\n" + "    items : [\n" + "    ]\n" + "}\n", "menu"));
        swing.add (new SnippetsNode ("MenuItem", "MenuItem {\n" + "    text: \"File\"\n" + "}\n", "menu_item"));
        swing.add (new SnippetsNode ("RadioButton", "SwingRadioButton {\n" + "    text: \"RadioButton\"\n" + "}\n", "radio_button"));
        swing.add (new SnippetsNode ("Slider", "SwingSlider {\n" + "    minimum: 0\n" + "    maximum: 10\n" + "    value: 3\n" + "    vertical: true\n" + "}\n", "slider"));
        swing.add (new SnippetsNode ("TextField", "SwingTextField {\n" + "    columns: 10\n" + "    text: \"TextField\"\n" + "    editable: true\n" + "}\n", "text_field"));
        swing.add (new SnippetsNode ("ToggleButton", "SwingToggleButton {\n" + "    text: \"ToggleButton\"\n" + "}\n", "toggle_button"));
        root.add (timeline);

        DefaultTreeModel model = new DefaultTreeModel (root, true);
        tree = new JTree (model);
        tree.setRootVisible (false);

        tree.expandPath (new TreePath (new Object[] {root, imports }));
        tree.expandPath (new TreePath (new Object[] {root, apps }));
        tree.expandPath (new TreePath (new Object[] {root, actions }));
        tree.expandPath (new TreePath (new Object[] {root, shapes }));
        tree.expandPath (new TreePath (new Object[] {root, transforms }));
        tree.expandPath (new TreePath (new Object[] {root, colors }));
        tree.expandPath (new TreePath (new Object[] {root, timeline }));
        tree.expandPath (new TreePath (new Object[] {root, swing }));

        tree.setCellRenderer (new DefaultTreeCellRenderer() {
            public Component getTreeCellRendererComponent (JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean focus) {
                Object pass = value;
                if (value instanceof CategoryNode) {
                    pass = "<html><b>" + ((CategoryNode) value).getName ();
                } else if (value instanceof SnippetsNode) {
                    pass = ((SnippetsNode) value).getName ();
                }
                Component component = super.getTreeCellRendererComponent (tree, pass, sel, expanded, leaf, row, focus);

                if (component instanceof JLabel) {
                    JLabel label = (JLabel) component;
                    if (value instanceof CategoryNode) {
                        label.setIcon (expanded ? ICON_MINUS : ICON_PLUS);
                    } else if (value instanceof SnippetsNode) {
                        SnippetsNode node = (SnippetsNode) value;
                        label.setIcon (node.getIcon ());
                        label.setToolTipText (node.getToolTip ());
                    }
                }
                return component;
            }
        });

        tree.setDragEnabled (false);
        DragSource.getDefaultDragSource ().createDefaultDragGestureRecognizer (tree, DnDConstants.ACTION_COPY, new DragGestureListener() {
            public void dragGestureRecognized (DragGestureEvent dge) {
                Point origin = dge.getDragOrigin ();
                TreePath path = tree.getClosestPathForLocation (origin.x, origin.y);
                Object object = path != null ? path.getLastPathComponent () : null;
                if (object instanceof SnippetsNode) {
                    String code = ((SnippetsNode) object).getCode ();
                    if (code != null)
                        dge.startDrag (null, new StringSelection (code));
                }
            }
        });
    }

    @NonNls @NotNull public String getComponentName () {
        return "F3 Snippets";
    }

    public void initComponent () {
    }

    public void disposeComponent () {
    }

    public void projectOpened () {
        JScrollPane pane = new JScrollPane (tree);
        pane.setPreferredSize(new Dimension (100, 100));
        ToolWindowManager windowmgr = ToolWindowManager.getInstance (project);
		ToolWindow window = windowmgr.registerToolWindow("F3 Snippets", true, ToolWindowAnchor.RIGHT);
		PeerFactory pf = PeerFactory.getInstance();
		Content content = pf.getContentFactory().createContent(pane, null, false);
		window.getContentManager().addContent(content);
		window.setIcon(F3Plugin.F3_ICON);
    }

    public void projectClosed () {
//        window.setAvailable (false, null);
        ToolWindowManager.getInstance (project).unregisterToolWindow ("F3 Snippets");
    }

    private static final class CategoryNode extends DefaultMutableTreeNode {

        private final String name;

        private CategoryNode (String n) {
            super(null, true);
            name = n;
        }

        public String getName () {
            return name;
        }

    }

    private static final class SnippetsNode extends DefaultMutableTreeNode {

        private final String name;
        private final String code;
        private final String toolTip;
        private final Icon icon;

        private SnippetsNode (String name, String code) {
            this (name, code, null);
        }

        private SnippetsNode (String name, String code, String iconName) {
            super (null, false);
            this.name = name;
            this.code = code;
            this.toolTip = "<html>" + code.replaceAll ("\n", "<br>");
            this.icon = iconName != null ? IconLoader.getIcon("/icons/snippets/" + iconName + "_16.png") : null;
        }

        public String getName () {
            return name;
        }

        public String getCode () {
            return code;
        }

        public String getToolTip () {
            return toolTip;
        }

        public Icon getIcon () {
            return icon;
        }

    }

}
