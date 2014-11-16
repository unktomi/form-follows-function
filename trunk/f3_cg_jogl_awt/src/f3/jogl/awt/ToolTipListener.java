package f3.jogl.awt;

import java.awt.*;
import java.awt.event.*;
import javax.swing.Timer;
import javax.swing.JLabel;

public class ToolTipListener implements MouseListener, MouseMotionListener
{
    Component testCanvas;
    Frame owner;
    Window tooltip;
    Timer entryTimer;
    Point p;
    String text = "";
 
    public ToolTipListener(Component ttt, Frame f)
    {
        testCanvas = ttt;
        owner = f;
        testCanvas.addMouseListener(this);
        testCanvas.addMouseMotionListener(this);
        tooltip = new Window(owner);
        JLabel label = new JLabel("");
        label.setBackground(new Color(200,220,240));
        tooltip.add(label);
        tooltip.pack();
        entryTimer = new Timer(750, entryListener);
        entryTimer.setRepeats(false);
    }
 
    public void setToolTipText(String s)
    {
        text = s == null ? "" : s;
        JLabel label = (JLabel)tooltip.getComponent(0);
        label.setText(s);
        tooltip.pack();
    }

    public void showToolTip(int x, int y) {
        showToolTip(new Point(x, y));
    }

 
    public void showToolTip(Point p)
    {
        if (text.length() > 0) {
            javax.swing.SwingUtilities.convertPointToScreen(p, owner);
            tooltip.setLocation(p.x, p.y);
            tooltip.setVisible(true);
        }
    }
 
    private void hideToolTip()
    {
        tooltip.setVisible(false);
    }
 
    public void mouseEntered(MouseEvent e)
    {
        entryTimer.start();
    }
 
    public void mouseExited(MouseEvent e)
    {
        hideToolTip();
    }
 
    public void mousePressed(MouseEvent e)
    {
        hideToolTip();
        entryTimer.start();
    }
 
    public void mouseMoved(MouseEvent e)
    {
        p = e.getPoint();
    }
 
    private ActionListener entryListener = new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                showToolTip(p);
            }
        };
 
    public void mouseReleased(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mouseDragged(MouseEvent e) {}
}