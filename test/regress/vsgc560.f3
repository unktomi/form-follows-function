/*
 * @compilearg -XDfwdRefError=false
 * @test
 * @run
 *
 */

import java.lang.System;

class GroupPanel {
    var row: Integer[];
    var content: Integer;
}

class PanelsPanel {
    var groupPanel: GroupPanel = GroupPanel {
        row: [1..3]
        content: groupPanel.row[0]
    }
}

var panel = new PanelsPanel;
System.out.println("panel.groupPanel.content={panel.groupPanel.content}");
