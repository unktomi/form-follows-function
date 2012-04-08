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

package org.f3.ideaplugin.run;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * @author David Kaspar
 */
public class F3RunSettingsEditor extends SettingsEditor<F3RunConfiguration> {

    private final F3RunSettingsPanel panel;
    private final DefaultComboBoxModel moduleModel;

    public F3RunSettingsEditor (Project project) {
        panel = new F3RunSettingsPanel ();
        panel.moduleCombo.setModel (moduleModel = new DefaultComboBoxModel ());
        panel.moduleCombo.setRenderer (new DefaultListCellRenderer() {
            public Component getListCellRendererComponent (JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent (list, value, index, isSelected, cellHasFocus);
                final Module module = (Module) value;
                if (module != null) {
                    setIcon (module.getModuleType ().getNodeIcon (false));
                    setText (module.getName ());
                }
                return this;
            }
        });
    }

    protected void resetEditorFrom (F3RunConfiguration configuration) {
        reset (panel.mainClassText, configuration.getMainClass ());
        reset (panel.vmParamsText, configuration.getVmParameters ());
        reset (panel.programParamsText, configuration.getProgramParameters ());
        reset (panel.workingDirectoryText, configuration.getWorkingDirectory ());
        moduleModel.removeAllElements ();
        for (Module module : configuration.getValidModules ())
            moduleModel.addElement (module);
        moduleModel.setSelectedItem (configuration.getConfigurationModule ().getModule ());
    }

    private void reset (JTextField field, String text) {
        field.setText (text != null ? text : "");
    }

    protected void applyEditorTo (F3RunConfiguration configuration) throws ConfigurationException {
        configuration.setMainClass (panel.mainClassText.getText ());
        configuration.setVmParameters (panel.vmParamsText.getText ());
        configuration.setProgramParameters (panel.programParamsText.getText ());
        configuration.setWorkingDirectory (panel.workingDirectoryText.getText ());
        configuration.getConfigurationModule ().setModule ((Module) moduleModel.getSelectedItem ());
    }

    @NotNull protected JComponent createEditor () {
        return panel.panel;
    }

    protected void disposeEditor () {
    }

}
