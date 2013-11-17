/*===============================================================================================
DSP Plugin Viewer Example
Copyright (c), Firelight Technologies Pty, Ltd 2004-2008.

This example ....
===============================================================================================*/

package org.jouvieje.FmodEx.Examples;

import static org.jouvieje.FmodEx.Defines.PLATFORMS.LINUX64;
import static org.jouvieje.FmodEx.Defines.PLATFORMS.WIN64;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;

import org.jouvieje.FmodEx.FmodEx;
import org.jouvieje.FmodEx.Defines.PLATFORMS;

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
public abstract class DspPluginViewerFrame extends JFrame
{
	public final static String TITLE = "DSP Plugin Viewer Example. Copyright (c), Firelight Technologies Pty, Ltd 2004-2008.";
	public final static int GRAPHICWINDOW_WIDTH  = 256;
	public final static int GRAPHICWINDOW_HEIGHT = 116;
	private Dimension plotDimension = new Dimension(GRAPHICWINDOW_WIDTH, GRAPHICWINDOW_HEIGHT);
	
	private JPanel jContentPane = null;
	private JPanel analyserPanel = null;
	private JPanel spectrumPanel = null;
	private JPanel osciloscopePanel = null;
	private JPanel filePanel = null;
	private JPanel filePanel1 = null;
	private JTextField fileName = null;
	private JPanel buttonPanel = null;
	private JButton openButton = null;
	private JButton playStopButton = null;
	private JButton pauseButton = null;
	private JPanel progressPanel = null;
	private JProgressBar progressBar = null;
	private JPanel microphonePanel = null;
	private JButton recordButton = null;
	private JPanel pluginPanel = null;
	private JScrollPane pluginScrollPane = null;
	private JPanel pluginList = null;
	private JButton addPluginButton = null;
	
	public DspPluginViewerFrame()
	{
		super();
		initialize();
	}

	private void initialize()
	{
		this.setTitle(TITLE);
		this.setSize(560, 600);
		this.setLocationRelativeTo(null);
		this.setContentPane(getJContentPane());
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
	
	private JPanel getJContentPane()
	{
		if(jContentPane == null)
		{
			jContentPane = new JPanel();
			jContentPane.setLayout(new BoxLayout(jContentPane, BoxLayout.Y_AXIS));
			jContentPane.add(getAnalyserPanel());
			jContentPane.add(getFilePanel());
			jContentPane.add(getMicrophonePanel());
			jContentPane.add(getPluginPanel());
		}
		return jContentPane;
	}
   
	private JPanel getAnalyserPanel()
	{
		if(analyserPanel == null)
		{
			GridBagConstraints constraintSpectrum = new GridBagConstraints();
			constraintSpectrum.anchor = GridBagConstraints.CENTER;
			constraintSpectrum.gridx = 0;
			constraintSpectrum.gridy = 0;
			constraintSpectrum.weightx = 1;
			constraintSpectrum.weighty = 1;
			GridBagConstraints constraintOsciloscope = new GridBagConstraints();
			constraintSpectrum.anchor = GridBagConstraints.CENTER;
			constraintOsciloscope.gridx = 1;
			constraintOsciloscope.gridy = 0;
			constraintOsciloscope.weightx = 1;
			constraintOsciloscope.weighty = 1;
			analyserPanel = new JPanel();
			analyserPanel.setLayout(new GridBagLayout());
			analyserPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Analyser", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
			analyserPanel.setMaximumSize(new java.awt.Dimension(2000,160));
			analyserPanel.setPreferredSize(new java.awt.Dimension(450,160));
			analyserPanel.setMinimumSize(new java.awt.Dimension(450,145));
			analyserPanel.add(getSpectrumPanel(), constraintSpectrum);
			analyserPanel.add(getOsciloscopePanel(), constraintOsciloscope);
		}
		return analyserPanel;
	}
	protected JPanel getSpectrumPanel()
	{
		if(spectrumPanel == null)
		{
			spectrumPanel = new JPanel();
			spectrumPanel.setBackground(java.awt.Color.black);
			spectrumPanel.setMinimumSize(plotDimension);
			spectrumPanel.setPreferredSize(plotDimension);
			spectrumPanel.setMaximumSize(plotDimension);
		}
		return spectrumPanel;
	}

	protected JPanel getOsciloscopePanel()
	{
		if(osciloscopePanel == null)
		{
			osciloscopePanel = new JPanel();
			osciloscopePanel.setBackground(java.awt.Color.black);
			osciloscopePanel.setMinimumSize(plotDimension);
			osciloscopePanel.setPreferredSize(plotDimension);
			osciloscopePanel.setMaximumSize(plotDimension);
		}
		return osciloscopePanel;
	}

 
	private JPanel getFilePanel()
	{
		if(filePanel == null)
		{
			filePanel = new JPanel();
			filePanel.setLayout(new BoxLayout(filePanel, BoxLayout.Y_AXIS));
			filePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Audio file to play", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
			filePanel.setMaximumSize(new java.awt.Dimension(2000,110));
			filePanel.setPreferredSize(new java.awt.Dimension(450,110));
			filePanel.setMinimumSize(new java.awt.Dimension(450,110));
			filePanel.add(getFilePanel1());
			filePanel.add(getButtonPanel());
			filePanel.add(getProgressPanel());
		}
		return filePanel;
	}

	private JPanel getFilePanel1()
	{
		if(filePanel1 == null)
		{
			filePanel1 = new JPanel();
			filePanel1.setLayout(new BorderLayout());
			filePanel1.add(getFileName(), BorderLayout.CENTER);
			filePanel1.add(getOpenButton(), BorderLayout.EAST);
		}
		return filePanel1;
	}

	protected JTextField getFileName()
	{
		if(fileName == null)
		{
			fileName = new JTextField();
			fileName.setEditable(false);
		}
		return fileName;
	}

	private JButton getOpenButton()
	{
		if(openButton == null)
		{
			openButton = new JButton();
			openButton.setText("...");
			openButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					open();
				}
			});
		}
		return openButton;
	}
	
	private JPanel getButtonPanel()
	{
		if(buttonPanel == null)
		{
			buttonPanel = new JPanel();
			buttonPanel.add(getPlayStopButton());
			buttonPanel.add(getPauseButton());
		}
		return buttonPanel;
	}
	protected JButton getPlayStopButton()
	{
		if(playStopButton == null)
		{
			playStopButton = new JButton();
			playStopButton.setText("Play");
			playStopButton.setPreferredSize(new Dimension(80, 24));
			playStopButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					playStop();
				}
			});
		}
		return playStopButton;
	}

	protected JButton getPauseButton()
	{
		if(pauseButton == null)
		{
			pauseButton = new JButton();
			pauseButton.setText("Pause");
			pauseButton.setPreferredSize(new Dimension(80, 24));
			pauseButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					pause();
				}
			});
		}
		return pauseButton;
	}

	private JPanel getProgressPanel()
	{
		if(progressPanel == null)
		{
			progressPanel = new JPanel();
			progressPanel.setLayout(new BorderLayout());
			progressPanel.add(getProgressBar(), java.awt.BorderLayout.CENTER);
		}
		return progressPanel;
	}
	
	protected JProgressBar getProgressBar()
	{
		if(progressBar == null)
		{
			progressBar = new JProgressBar();
			progressBar.setMinimum(0);
			progressBar.setMaximum(1000);
			progressBar.setPreferredSize(new Dimension(100, 22));
			progressBar.addMouseListener(new java.awt.event.MouseAdapter() { 
				public void mousePressed(java.awt.event.MouseEvent e) { 
					int position = (int)( e.getX() / (float)getProgressBar().getSize().width * getProgressBar().getMaximum() );
					position = Math.min(position, getProgressBar().getMaximum());
					
					updateProgression(position);
				}
			});
			progressBar.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() { 
				public void mouseDragged(java.awt.event.MouseEvent e) {    
					int position = (int)( e.getX() / (float)getProgressBar().getSize().width * getProgressBar().getMaximum() );
					position = Math.min(position, getProgressBar().getMaximum());
					
					updateProgression(position);
				}
			});
		}
		return progressBar;
	}

	private JPanel getMicrophonePanel()
	{
		if(microphonePanel == null)
		{
			microphonePanel = new JPanel();
			microphonePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null,
					"Microphone (use FMOD_OUTPUTTYPE_ASIO for low latency record/playback)",
					javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
					javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
			microphonePanel.setMaximumSize(new java.awt.Dimension(2000,60));
			microphonePanel.setPreferredSize(new java.awt.Dimension(450,60));
			microphonePanel.setMinimumSize(new java.awt.Dimension(450,60));
			microphonePanel.add(getRecordButton());
		}
		return microphonePanel;
	}

	protected JButton getRecordButton()
	{
		if(recordButton == null)
		{
			recordButton = new JButton();
			recordButton.setText("Record");
			recordButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					record();
				}
			});
		}
		return recordButton;
	}
	
	private JPanel getPluginPanel()
	{
		if(pluginPanel == null)
		{
			pluginPanel = new JPanel();
			pluginPanel.setLayout(new BoxLayout(pluginPanel, BoxLayout.Y_AXIS));
			pluginPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Plugins", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
			pluginPanel.add(getPluginScrollPane());
			pluginPanel.add(getAddPluginButton());
		}
		return pluginPanel;
	}

	protected JScrollPane getPluginScrollPane()
	{
		if(pluginScrollPane == null)
		{
			pluginScrollPane = new JScrollPane();
			pluginScrollPane.setViewportView(getPluginList());
		}
		return pluginScrollPane;
	}
	  
	private JPanel getPluginList()
	{
		if(pluginList == null)
		{
			pluginList = new JPanel();
			pluginList.setLayout(new BoxLayout(pluginList, BoxLayout.Y_AXIS));
		}
		return pluginList;
	}
	
	private JButton getAddPluginButton()
	{
		if(addPluginButton == null)
		{
			addPluginButton = new JButton();
			addPluginButton.setText("Add plugin");
			addPluginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
			addPluginButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					addPlugins();
				}
			});
		}
		return addPluginButton;
	}
	
	private JFileChooser musicChooser = null;
	protected JFileChooser getMusicChooser()
	{
		if(musicChooser == null)
		{
			musicChooser = new JFileChooser();
			musicChooser.setMultiSelectionEnabled(false);
			musicChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			musicChooser.removeChoosableFileFilter(musicChooser.getFileFilter());
			musicChooser.addChoosableFileFilter(new FileFilter(){
				public String getDescription()
				{
					return "All audio files (*.*))";
				}
				public boolean accept(File f)
				{
					return true;
				}
			});
		}
		return musicChooser;
	}
	private JFileChooser pluginChooser = null;
	protected JFileChooser getPluginChooser()
	{
		if(pluginChooser == null)
		{
			pluginChooser = new JFileChooser();
			pluginChooser.setMultiSelectionEnabled(true);
			pluginChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			FileFilter allFilesFilter = pluginChooser.getFileFilter();
			pluginChooser.removeChoosableFileFilter(allFilesFilter);
			pluginChooser.addChoosableFileFilter(new FileFilter(){
				public String getDescription()
				{
					if(FmodEx.getPlatform() == PLATFORMS.WIN32 || FmodEx.getPlatform() == WIN64)
					{
						return "FMOD Plugins (dsp*.dll)";
					}
					else if(FmodEx.getPlatform() == PLATFORMS.LINUX || FmodEx.getPlatform() == LINUX64)
					{
						return "FMOD Plugins (dsp*.so)";
					}
					else if(FmodEx.getPlatform() == PLATFORMS.MAC)
					{
						return "FMOD Plugins (dsp*.dylib)";
					}
					return "";
				}
				public boolean accept(File f)
				{
					if(FmodEx.getPlatform() == PLATFORMS.WIN32 || FmodEx.getPlatform() == WIN64)
					{
						return f.isDirectory() || (f.getName().startsWith("dsp") && f.getName().endsWith(".dll"));
					}
					else if(FmodEx.getPlatform() == PLATFORMS.LINUX || FmodEx.getPlatform() == LINUX64)
					{
						return f.isDirectory() || (f.getName().startsWith("dsp") && f.getName().endsWith(".so"));
					}
					else if(FmodEx.getPlatform() == PLATFORMS.MAC)
					{
						return f.isDirectory() || (f.getName().startsWith("dsp") && f.getName().endsWith(".dylib"));
					}
					return false;
				}
			});
			pluginChooser.addChoosableFileFilter(new FileFilter(){
				public String getDescription()
				{
					if(FmodEx.getPlatform() == PLATFORMS.WIN32 || FmodEx.getPlatform() == WIN64)
					{
						return "VST Plugins (*.dll))";
					}
					else if(FmodEx.getPlatform() == PLATFORMS.LINUX || FmodEx.getPlatform() == LINUX64)
					{
						return "VST Plugins (*.so))";
					}
					else if(FmodEx.getPlatform() == PLATFORMS.MAC)
					{
						return "VST Plugins (*.dylib, *.vst)";
					}
					return "";
				}
				public boolean accept(File f)
				{
					if(FmodEx.getPlatform() == PLATFORMS.WIN32 || FmodEx.getPlatform() == WIN64)
					{
						return f.isDirectory() || f.getName().endsWith(".dll");
					}
					else if(FmodEx.getPlatform() == PLATFORMS.LINUX || FmodEx.getPlatform() == LINUX64)
					{
						return f.isDirectory() || f.getName().endsWith(".so");
					}
					else if(FmodEx.getPlatform() == PLATFORMS.MAC)
					{
						return f.isDirectory() || f.getName().endsWith(".dylib") || f.getName().endsWith(".vst");
					}
					return false;
				}
			});
			pluginChooser.addChoosableFileFilter(allFilesFilter);
			pluginChooser.setFileFilter(pluginChooser.getChoosableFileFilters()[0]);
		}
		return pluginChooser;
	}
	
	protected abstract void open();
	protected abstract void playStop();
	protected abstract void pause();
	protected abstract void record();
	protected abstract void addPlugins();
	protected abstract void updateGui();
	protected abstract void updateProgression(int position);
	
	protected void addPluginInList(DspPluginViewer.Plugin plugin)
	{
		getPluginList().add(plugin.pluginPanel);
		getPluginScrollPane().validate();
	}
	protected void removePlugin(DspPluginViewer.Plugin plugin)
	{
		getPluginList().remove(plugin.pluginPanel);
		getPluginScrollPane().validate();
		getPluginScrollPane().repaint();
	}
}