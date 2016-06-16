package org.cytoscape.cycle.internal;

import java.util.Properties;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;

/**
 * @author SrikanthB
 *
 */

public class CycleCore {
    
    private static Cyclegui gui;
    public CycleCore(){
        gui = createCyclegui();
    }

    public Cyclegui createCyclegui(){
        gui = new Cyclegui(this);
        CyActivator.getCyServiceRegistrar().registerService(gui, CytoPanelComponent.class, new Properties());
        CytoPanel cytopanelwest = CyActivator.getCyDesktopService().getCytoPanel(CytoPanelName.WEST);
        int index = cytopanelwest.indexOfComponent(gui);
        cytopanelwest.setSelectedIndex(index);
        return gui;
    }
    
    public void closeStartMenu(Cyclegui menu) {
        CyActivator.getCyServiceRegistrar().unregisterService(menu, CytoPanelComponent.class);
    }
    
    public static Cyclegui getCyclegui(){
        return gui;
    }
    
}