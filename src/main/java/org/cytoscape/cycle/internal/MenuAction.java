package org.cytoscape.cycle.internal;

import java.awt.event.ActionEvent;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;

/**
 * @author SrikanthB
 * This class creates a new option for the app to load in Apps menu
 */

public class MenuAction extends AbstractCyAction {
    
    private CyApplicationManager cyApplicationManager;
    private CySwingApplication cyDesktopService;
    
    public MenuAction(CyApplicationManager cyApplicationManager, final String menuTitle) {
        super(menuTitle, cyApplicationManager, null, null);
        setPreferredMenu("Apps");
        this.cyApplicationManager = cyApplicationManager;
        this.cyDesktopService = CyActivator.getCyDesktopService();
    }

    public void actionPerformed(ActionEvent e) {
        CycleCore core = new CycleCore();
    }
    
}