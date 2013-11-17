/*===============================================================================================
DSP Plugin Viewer Example
Copyright (c), Firelight Technologies Pty, Ltd 2004-2008.

This example ....
===============================================================================================*/

package org.jouvieje.FmodEx.Examples;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JButton;

import org.jouvieje.FmodEx.Misc.BufferUtils;

/**
 * I've ported the C++ FMOD Ex example to NativeFmodEx.
 * 
 * @author Jérôme JOUVIE (Jouvieje)
 * 
 * WANT TO CONTACT ME ?
 * E-mail :
 * 		jerome.jouvie@gmail.com
 * Site :
 * 		http://jerome.jouvie.free.fr/
 */
public abstract class DspPluginViewerPluginPanel extends JPanel
{
	private final DspPluginViewer.Plugin plugin;
	
	private JPanel buttons = null;
	private JCheckBox active = null;
	private JButton config = null;
	private JButton remove = null;
	private JPanel parameters = null;
	private JPanel canvasContainer = null;
	
	public DspPluginViewerPluginPanel(DspPluginViewer.Plugin plugin)
	{
		super();
		this.plugin = plugin;
		initialize();
	}

	private void initialize()
	{
		this.setLayout(new BorderLayout());
		this.setSize(300, 200);
		this.setBorder(javax.swing.BorderFactory.createTitledBorder(null,
				BufferUtils.toString(plugin.name)+" "+String.format("%1x.%2x", plugin.version.get(0) >> 16, plugin.version.get(0) & 0xFFFF),
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
		this.add(getButtons(), BorderLayout.NORTH);
		this.add(getParameters(), BorderLayout.CENTER);
		this.add(getCanvasContainer(), BorderLayout.SOUTH);
	}

	private JPanel getButtons()
	{
		if(buttons == null)
		{
			buttons = new JPanel();
			buttons.add(getActive());
			buttons.add(getConfig());
			buttons.add(getRemove());
		}
		return buttons;
	}

	private JPanel getParameters()
	{
		if(parameters == null)
		{
			parameters = new JPanel();
			parameters.setLayout(new BoxLayout(parameters, BoxLayout.Y_AXIS));
		}
		return parameters;
	}

	private JCheckBox getActive()
	{
		if(active == null)
		{
			active = new JCheckBox();
			active.setText("Active");
			active.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					setActive(DspPluginViewerPluginPanel.this.plugin, active.isSelected());
				}
			});
		}
		return active;
	}

	private JButton getRemove()
	{
		if(remove == null)
		{
			remove = new JButton();
			remove.setText("Remove");
			remove.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					remove(DspPluginViewerPluginPanel.this.plugin);
				}
			});
		}
		return remove;
	}

	private JButton getConfig()
	{
		if(config == null)
		{
			config = new JButton();
			config.setText("Config");
			config.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					showConfig(DspPluginViewerPluginPanel.this.plugin);
				}
			});
		}
		return config;
	}
	
	private JPanel getCanvasContainer()
	{
		if(canvasContainer == null)
		{
			canvasContainer = new JPanel();
		}
		return canvasContainer;
	}
	
	public void addPluginParameter(DspPluginViewer.PluginParam pluginParameter)
	{
		getParameters().add(pluginParameter.paramPanel);
	}
	
	public void addConfigDialog(Component canvas)
	{
		getCanvasContainer().add(canvas);
		getCanvasContainer().validate();
		
	}
	public void removeConfigDialog(Component canvas)
	{
		getCanvasContainer().remove(canvas);
		getCanvasContainer().validate();
		
	}
	
	public abstract void setActive(DspPluginViewer.Plugin plugin, boolean active);
	public abstract void remove(DspPluginViewer.Plugin plugin);
	public abstract void showConfig(DspPluginViewer.Plugin plugin);
}