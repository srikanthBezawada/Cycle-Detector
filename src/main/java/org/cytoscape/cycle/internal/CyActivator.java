package org.cytoscape.cycle.internal;

import java.util.Properties;
import org.cytoscape.app.CyAppAdapter;
import org.cytoscape.app.swing.CySwingAppAdapter;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.events.NetworkAddedListener;
import org.cytoscape.model.events.NetworkDestroyedListener;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.osgi.framework.BundleContext;


/**
 * @author SrikanthB
 *  This class is the entry point to Cytoscape app
 */

public class CyActivator extends AbstractCyActivator {
    public static final String APPNAME = "Cycle Detector - 1.0";
    private static CyAppAdapter appAdapter;
    private static CyEventHelper eventHelper;
    private static CyApplicationManager cyApplicationManager;
    private static CySwingApplication cyDesktopService;
    private static CyServiceRegistrar cyServiceRegistrar;
    private static CyNetworkFactory networkFactory;
    private static CyNetworkManager networkManager;
    private static CyNetworkViewFactory networkViewFactory;
    private static CyNetworkViewManager networkViewManager;
    private static CySwingAppAdapter adapter;
    private MenuAction menuaction;
    
    public CyActivator() {
        super();
    }
    
    public void start(BundleContext context) throws Exception {
        this.appAdapter = getService(context, CyAppAdapter.class);
        this.eventHelper = getService(context, CyEventHelper.class);
        this.cyApplicationManager = getService(context, CyApplicationManager.class);
        this.cyDesktopService = getService(context, CySwingApplication.class);
        this.cyServiceRegistrar = getService(context, CyServiceRegistrar.class);
        this.networkFactory = getService(context, CyNetworkFactory.class);
        this.networkManager = getService(context, CyNetworkManager.class);
        this.networkViewFactory = getService(context, CyNetworkViewFactory.class);
        this.networkViewManager = getService(context, CyNetworkViewManager.class);
        this.adapter = getService(context,CySwingAppAdapter.class);
        this.menuaction =  new MenuAction(cyApplicationManager, APPNAME);
        NetworkEventsListener networkEventsListener = new NetworkEventsListener();
        registerService(context,networkEventsListener,NetworkAddedListener.class, new Properties());
        registerService(context,networkEventsListener,NetworkDestroyedListener.class, new Properties());
        registerAllServices(context, menuaction, new Properties());
        
    }
    
    public static CySwingApplication getCyDesktopService(){
        return cyDesktopService;
    }
    
    public static CyServiceRegistrar getCyServiceRegistrar() {
        return cyServiceRegistrar;
    }
    
    public static CyApplicationManager getCyApplicationManager(){
        return cyApplicationManager;
    }
    
    public static CyNetworkManager getCyNetworkManager() {
        return networkManager; 
    }
            
    public static CySwingAppAdapter getCySwingAppAdapter() {
        return adapter;
    }
}
