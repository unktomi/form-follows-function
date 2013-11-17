/*===============================================================================================
DSP Plugin Viewer Example
Copyright (c), Firelight Technologies Pty, Ltd 2004-2008.

This example ....
===============================================================================================*/

package org.jouvieje.FmodEx.Examples;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JSlider;

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
public abstract class DspPluginViewerParamPanel extends JPanel
{
	private float FLOAT_TO_INT = 100000.f;
	
	private DspPluginViewer.PluginParam pluginParam;
	private JLabel paramName = null;
	private JSlider slider = null;
	private JLabel paramValue = null;

	public DspPluginViewerParamPanel(DspPluginViewer.PluginParam pluginParam)
	{
		super();
		this.pluginParam = pluginParam;
		initialize();
	}

	private void initialize()
	{
		this.setLayout(new GridLayout(0, 3));
		this.setSize(300, 200);
		this.add(getParamName());
		this.add(getSlider());
		this.add(getParamValue());
		refresh();
	}

	private JLabel getParamName()
	{
		if(paramName == null)
		{
			paramName = new JLabel();
			paramName.setText(BufferUtils.toString(pluginParam.paramname));
		}
		return paramName;
	}
	
	private JSlider getSlider()
	{
		if(slider == null)
		{
			slider = new JSlider();
			slider.setMinimum((int)(pluginParam.min.get(0)*FLOAT_TO_INT));
			slider.setMaximum((int)(pluginParam.max.get(0)*FLOAT_TO_INT));
			slider.setValue((int)(pluginParam.value.get(0)*FLOAT_TO_INT));
			slider.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					if(getSlider().getValueIsAdjusting())
					{
						pluginParam.value.put(0, getSlider().getValue()/FLOAT_TO_INT);
						paramValueChange(pluginParam, pluginParam.value.get(0));
						refresh();
					}
				}
			});
		}
		return slider;
	}
	
	private JLabel getParamValue()
	{
		if(paramValue == null)
		{
			paramValue = new JLabel();
		}
		return paramValue;
	}
	
	private void refresh()
	{
		getParamValue().setText(BufferUtils.toString(pluginParam.valuestr)+" "+BufferUtils.toString(pluginParam.label));
	}
	
	protected abstract void paramValueChange(DspPluginViewer.PluginParam pluginParam, float value);
}