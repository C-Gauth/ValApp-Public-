package com.example.valorantlineups;
import java.util.*;

public class DataStorage {
    private Map<String, Map<String, Map<String, List<List<String>>>>> data;

    public DataStorage() {
        data = new HashMap<>();
    }



    // Add an item to a specific title for an agent on a map
    public void addItem(String map, String agent, String title, String data) {
        Map<String, Map<String, List<List<String>>>> mapData = this.data.computeIfAbsent(map, k -> new HashMap<>());
        Map<String, List<List<String>>> agentData = mapData.computeIfAbsent(agent, k -> new HashMap<>());
        List<List<String>> titleData = agentData.computeIfAbsent(title, k -> new ArrayList<>());
        titleData.add(Collections.singletonList(data));
    }

    // Get items for a specific title for an agent on a map
    public List<List<String>> getItems(String map, String agent, String title) {
        return data.getOrDefault(map, Collections.emptyMap())
                .getOrDefault(agent, Collections.emptyMap())
                .getOrDefault(title, Collections.emptyList());
    }

    // Get all titles for a specific agent on a specific map
    public Set<String> getTitles(String map, String agent) {
        Map<String, Map<String, List<List<String>>>> agentData = data.getOrDefault(map, Collections.emptyMap());
        Map<String, List<List<String>>> agentTitles = agentData.getOrDefault(agent, Collections.emptyMap());
        return agentTitles.keySet();
    }


}