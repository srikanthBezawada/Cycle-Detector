package org.cytoscape.cycle.internal.logic;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import org.cytoscape.cycle.internal.CyActivator;
import org.cytoscape.cycle.internal.Cyclegui;
import static org.cytoscape.cycle.internal.logic.UpdateSubNetView.updateView;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.jgrapht.DirectedGraph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.cycle.PatonCycleBase;
import org.jgrapht.alg.cycle.SzwarcfiterLauerSimpleCycles;
import org.jgrapht.alg.cycle.TarjanSimpleCycles;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.SimpleGraph;

public class CycleLogic extends Thread{
    CyNetwork currentnetwork;
    CyNetworkView currentnetworkview;
    Cyclegui gui;
    boolean isUnDirected;
    String currentNetworkName;
    
    public CycleLogic(Cyclegui gui, CyNetwork currentnetwork, CyNetworkView currentnetworkview, boolean isUnDirected) {
        this.gui = gui;
        this.currentnetwork = currentnetwork;
        this.currentnetworkview = currentnetworkview;
        this.isUnDirected = isUnDirected;
    }
    
    public void run(){
        gui.startComputation();
        long startTime = System.currentTimeMillis();
        int count = 0;
        currentNetworkName = currentnetwork.getRow(currentnetwork).get(CyNetwork.NAME, String.class);
        List<CyNode> nodeList = currentnetwork.getNodeList();
        List<CyEdge> edgeList = currentnetwork.getEdgeList();
        CyTable nTable = currentnetwork.getDefaultNodeTable();
        CyTable eTable = currentnetwork.getDefaultEdgeTable();
        unselectAll(nTable, eTable, nodeList, edgeList);
        List<List<CyNode>> cycleList = null;
        
        if(isUnDirected) {
            UndirectedGraph<CyNode, CyEdge> ug = new SimpleGraph<CyNode, CyEdge>(CyEdge.class);
            for(CyNode n : nodeList){
                ug.addVertex(n);
            }
            for(CyEdge e : edgeList){
                if(e.getSource().equals(e.getTarget())){
                    continue; // removing self-loops
                }
                ug.addEdge(e.getSource(), e.getTarget(),e);
            }
            PatonCycleBase cycleFinder = new PatonCycleBase(ug);
            cycleList = cycleFinder.findCycleBase();
            if(cycleList.isEmpty()) {
                noCyclesFound();
                return;
            }
        } else {
            DirectedGraph<CyNode, CyEdge> dg = new DefaultDirectedGraph<CyNode, CyEdge>(CyEdge.class);
            for(CyNode n : nodeList){
                dg.addVertex(n);
            }
            for(CyEdge e : edgeList){
                if(e.getSource().equals(e.getTarget())){
                    continue; // removing self-loops
                }
                dg.addEdge(e.getSource(), e.getTarget(), e);
            }
            TarjanSimpleCycles<CyNode, CyEdge> cycleFinder = new TarjanSimpleCycles<CyNode, CyEdge>();
            if(cycleFinder.findSimpleCycles().isEmpty()) {
                noCyclesFound();
                return;
            }
            cycleList = cycleFinder.findSimpleCycles();
            /*
            cycleList = cycleFinder.findSimpleCycles();
            SzwarcfiterLauerSimpleCycles<CyNode, CyEdge> cycleFinder = new SzwarcfiterLauerSimpleCycles<CyNode, CyEdge>();
            cycleList = cycleFinder.findSimpleCycles();
            */
        }
        List<CyNode> requiredNodes = new ArrayList<CyNode>();
        List<CyEdge> requiredEdges = new ArrayList<CyEdge>();
        for(List<CyNode> cycle : cycleList) {
            count++;
            requiredNodes.addAll(cycle);
            requiredEdges.addAll(findNeighbourEdges(edgeList, requiredNodes));
            select(nTable, eTable, requiredNodes, requiredEdges);
            createSubNetwork(requiredNodes, requiredEdges, count);
        }
    
        long endTime = System.currentTimeMillis();
        long difference = endTime - startTime;
        System.out.println("Execution time for this cycle detection app : " + difference +" milli seconds");
        gui.endComputation();
    }
    
    
    
    public List<CyEdge> findNeighbourEdges(List<CyEdge> edgeList, List<CyNode> neightbourNodes) {
        List<CyEdge> neighbourEges = new ArrayList<CyEdge>();
        for(CyEdge e : edgeList) {
            if(neightbourNodes.contains(e.getSource()) && neightbourNodes.contains(e.getTarget())) 
                neighbourEges.add(e);
        }
        return neighbourEges;
    }
    
    
    public void createSubNetwork(List<CyNode> requiredNodes, List<CyEdge> requiredEdges, int count) {
        CyRootNetwork root = ((CySubNetwork)currentnetwork).getRootNetwork();
        CyNetwork subNetwork = root.addSubNetwork(requiredNodes, requiredEdges);
        subNetwork.getRow(subNetwork).set(CyNetwork.NAME, currentNetworkName + " Cycle  " + count);  
        CyActivator.getCyNetworkManager().addNetwork(subNetwork);
        CyNetworkView subNetView = CyActivator.getCyNetworkViewFactory().createNetworkView(subNetwork);
        CyActivator.getCyNetworkViewManager().addNetworkView(subNetView);
        updateView(currentnetworkview, subNetView, "circular");
        //CyActivator.getCyEventHelper().flushPayloadEvents();
    }
    
    
    public void unselectAll(CyTable nTable, CyTable eTable, List<CyNode> nodeList, List<CyEdge> edgeList){
        for(CyEdge e : edgeList){
                CyRow row = eTable.getRow(e.getSUID());
                row.set("selected", false);
            }
        for(CyNode n : nodeList){
            CyRow row = nTable.getRow(n.getSUID());
            row.set("selected", false);
        }
    }
        
        
    public void select(CyTable nTable, CyTable eTable, List<CyNode> nList, List<CyEdge> eList){
        for(CyEdge e : eList){
                CyRow row = eTable.getRow(e.getSUID());
                row.set("selected", true);
            }
        for(CyNode n : nList){
            CyRow row = nTable.getRow(n.getSUID());
            row.set("selected", true);
        }
    }
    
    public void noCyclesFound() {
        JOptionPane.showMessageDialog(null, "No Cycles found !", "Try another network ", JOptionPane.INFORMATION_MESSAGE);
        gui.endComputation();
        return;
    }
    
}
