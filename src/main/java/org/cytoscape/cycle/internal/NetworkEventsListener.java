package org.cytoscape.cycle.internal;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.events.NetworkAddedEvent;
import org.cytoscape.model.events.NetworkAddedListener;
import org.cytoscape.model.events.NetworkDestroyedEvent;
import org.cytoscape.model.events.NetworkDestroyedListener;

/**
 * @author SrikanthB
 *  This class takes care of updating network combo box of GUI
 */

public class NetworkEventsListener implements NetworkAddedListener, NetworkDestroyedListener {

    public void handleEvent(NetworkAddedEvent e){
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            Logger.getLogger(NetworkEventsListener.class.getName()).log(Level.SEVERE, null, ex);
        }
        CyNetwork net = e.getNetwork();
        String title = net.getRow(net).get(CyNetwork.NAME, String.class);
        Cyclegui gui = CycleCore.getCyclegui();
        ((DefaultComboBoxModel)gui.networkComboBox.getModel()).addElement(title);
    }

    public void handleEvent(NetworkDestroyedEvent e){
        Cyclegui menu = CycleCore.getCyclegui();
        menu.updateNetworkList();
    }
}