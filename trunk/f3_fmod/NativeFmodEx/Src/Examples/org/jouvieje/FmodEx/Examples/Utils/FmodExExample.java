/*
 * Created on 12 janv. 08
 */
package org.jouvieje.FmodEx.Examples.Utils;

import javax.swing.JPanel;

public interface FmodExExample extends Runnable
{
	public String getTitle();
	public JPanel getPanel();
	public void stop();
}
