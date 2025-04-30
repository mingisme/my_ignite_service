package com.swang.myservice;

import org.apache.ignite.Ignite;
import org.apache.ignite.cache.affinity.Affinity;
import org.apache.ignite.cluster.ClusterNode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.swang.myservice.MyService.MY_CACHE;
import static org.apache.ignite.internal.util.tostring.GridToStringBuilder.arrayToString;

@RestController
@RequestMapping("/monitor")
public class MyMonitor {

    private final Ignite ignite;

    public MyMonitor(Ignite ignite) {
        this.ignite = ignite;
    }

    @GetMapping("/nodes")
    public Object getPartitions(){
        // Get affinity for the cache
        Affinity<Object> affinity = ignite.affinity(MY_CACHE);

        List<Object> nodes = new ArrayList<>();
        // Get all nodes in the cluster
        for (ClusterNode node : ignite.cluster().nodes()) {

            Map<String, String> map = new HashMap<>();
            String nodeId = node.id().toString();
            map.put("node",nodeId);
            String hostname = node.attribute("HOSTNAME");
            map.put("hostname",hostname);

            // Get partitions assigned to this node (primary and backup)
            int[] primaryPartitions = affinity.primaryPartitions(node);
            int[] backupPartitions = affinity.backupPartitions(node);
            map.put("primary",primaryPartitions.length + " (IDs: " + arrayToString(primaryPartitions) + ")");
            map.put("backup",backupPartitions.length + " (IDs: " + arrayToString(backupPartitions) + ")");

            nodes.add(map);
        }

        return nodes;
    }

    @GetMapping("/key-location")
    public Object getKeyLocation(@RequestParam String key){
        // Get affinity for the cache
        Affinity<Object> affinity = ignite.affinity(MY_CACHE);
        int partition = affinity.partition(key);
        ClusterNode primaryNode = affinity.mapPartitionToNode(partition);
        Map<String,Object> map = new HashMap<>();
        map.put("key", key);
        map.put("partition", partition);
        map.put("node", primaryNode.id().toString());
        return map;
    }
}
