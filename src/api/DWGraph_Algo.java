package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class DWGraph_Algo implements dw_graph_algorithms {

    private directed_weighted_graph graph;

    @Override
    public void init(directed_weighted_graph g) {
        this.graph = g;
    }

    @Override
    public directed_weighted_graph getGraph() {
        return this.graph;
    }

    @Override
    public directed_weighted_graph copy() {
        if (this.graph == null) return null;
        directed_weighted_graph copy = new DWGraph_DS();

        //Go over the collection of all nodes and copy the attributes of the node
        for (node_data i : this.graph.getV()) {
            copy.addNode(i);

            //Copy of the neighbors
            for (edge_data v : this.graph.getE(i.getKey())) {
                copy.connect(i.getKey(), v.getDest(), v.getWeight());
            }
        }

        return copy;
    }

    @Override
    public boolean isConnected() {
        if(graph.getV().isEmpty()) return true;

        for(node_data i : graph.getV()){
            BFS(graph.getNode(i.getKey()));

            for(node_data x : graph.getV()){
                if(x.getTag() == 0) return false;
            }
        }
        return true;
    }

    @Override
    public double shortestPathDist(int src, int dest) {
        //If it does not exist src or dest.
        if (graph.getNode(dest) == null || graph.getNode(src) == null) return -1;
        Dijkstra(this.graph, graph.getNode(src));
        //If there is no path to the dest.
        if (graph.getNode(dest).getTag() == Integer.MAX_VALUE) return -1;

        ////*In the attribute of the node - "Tag", save the distance between the src and the dest.
        return graph.getNode(dest).getTag();
    }

    @Override
    public List<node_data> shortestPath(int src, int dest) {
        //Scan the entire graph and return the number of edges of the shortest path
        double sizePath = shortestPathDist(src, dest);
        if (sizePath == -1) return null;

        //Contain all nodes of the path
        List<node_data> path = new ArrayList<>();

        if (src == dest) path.add(graph.getNode(dest));
        else {
            path.add(graph.getNode(dest));
            node_data d = this.graph.getNode(dest);
            //*In the attribute of the node - "Info", save the key of the parent node in the shortest path
            node_data parent = this.graph.getNode(Integer.parseInt(d.getInfo()));

            //Loop from dest to src
            while (parent.getInfo() != null) {
                path.add(0, parent);
                parent = this.graph.getNode(Integer.parseInt(parent.getInfo()));
            }
            path.add(0, graph.getNode(src));
        }
        return path;
    }

    @Override
    public boolean save(String file) {
        Gson gson = new Gson();
        String json = gson.toJson(this.graph);
        System.out.println(json);

        try{
            PrintWriter pw = new PrintWriter(new File(file));
            pw.write(json);
            pw.close();
            return true;
        } catch (FileNotFoundException e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean load(String file) {
        return false;
    }

    private void Dijkstra(directed_weighted_graph g, node_data src) {
        HashMap<Integer, Boolean> visited = new HashMap<>();

        Comparator<node_data> nameSorter = Comparator.comparing(node_data::getTag);
        PriorityQueue<node_data> pQueue = new PriorityQueue<>(nameSorter);
        for (node_data x : g.getV()) {
            if (x != src) {
                x.setTag(Integer.MAX_VALUE);
            } else {
                x.setTag(0);
            }
            x.setInfo(null);
            pQueue.add(x);
            visited.put(x.getKey(), false);
        }

        while (!pQueue.isEmpty()) {
            node_data curr = pQueue.remove();

            for (edge_data v : g.getE(curr.getKey())) {
                if (!visited.get(curr.getKey())) {
                    if(g.getEdge(curr.getKey(), v.getDest()) != null) {
                        double t = curr.getTag() + g.getEdge(curr.getKey(), v.getDest()).getWeight();
                        node_data nei = g.getNode(v.getDest());
                        if (nei.getTag() > t) {
                            nei.setTag((int)t);
                            nei.setInfo("" + curr.getKey());
                            pQueue.add(nei);
                        }
                    }
                }
            }
            visited.put(curr.getKey(), true);
        }
    }

    public void BFS(node_data n){
        //Reset distances.
        for(node_data x : graph.getV()){
            x.setTag(0);
        }
        n.setTag(1);
        //Contain the neighbors of the current node.
        Queue<node_data> q = new LinkedList<>();
        q.add(n);

        while(!q.isEmpty()){
            node_data current = q.remove();

            for(edge_data v : graph.getE(current.getKey())){
                //If you have not yet passed the node.
                node_data temp = graph.getNode(v.getDest());
                if(temp.getTag() == 0){
                    //Marking the distance from the src
                    temp.setTag(1);
                    q.add(temp);
                }
            }
        }
    }

}
