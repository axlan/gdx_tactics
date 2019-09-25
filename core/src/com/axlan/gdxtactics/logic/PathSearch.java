package com.axlan.gdxtactics.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

public class PathSearch {

  private static ArrayList<PathSearchNode> reconstructPath(
      HashMap<PathSearchNode, PathSearchNode> cameFrom, PathSearchNode current) {
    ArrayList<PathSearchNode> totalPath = new ArrayList<>();
    totalPath.add(current);
    while (cameFrom.containsKey(current)) {
      current = cameFrom.get(current);
      totalPath.add(0, current);
    }
    return totalPath;
  }

  // A* finds a path from start to goal.
  // Implementation adapted from https://en.wikipedia.org/wiki/A*_search_algorithm
  public static ArrayList<PathSearchNode> aStarSearch(PathSearchNode start, PathSearchNode goal) {
    // The set of discovered nodes that need to be (re-)expanded.
    // Initially, only the start node is known.
    PriorityQueue<PriorityItem> openSet = new PriorityQueue<>();
    HashMap<PathSearchNode, PriorityItem> valueMap = new HashMap<>();
    ArrayList<PathSearchNode> closedSet = new ArrayList<>();

    // For node n, gScore[n] is the cost of the cheapest path from start to n currently known.
    // For node n, fScore[n] := gScore[n] + h(n).
    PriorityItem item = new PriorityItem(start, 0);
    openSet.add(item);
    valueMap.put(start, item);

    // For node n, cameFrom[n] is the node immediately preceding it on the cheapest path from start to n currently known.
    HashMap<PathSearchNode, PathSearchNode> cameFrom = new HashMap<>();

    while (openSet.size() > 0) {
      PriorityItem current = openSet.remove();
      if (current.obj == goal) {
        return reconstructPath(cameFrom, current.obj);
      }
      closedSet.add(current.obj);
      for (PathSearchNode neighbor : current.obj.getNeighbors()) {
        if (closedSet.contains(neighbor)) {
          continue;
        }
        // d(current,neighbor) is the weight of the edge from current to neighbor always (1)
        // tentative_gScore is the distance from start to the neighbor through current
        int tentativeGScore = current.gScore + 1;
        if (!valueMap.containsKey(neighbor) || tentativeGScore < valueMap.get(neighbor).gScore) {
          // This path to neighbor is better than any previous one. Record it!
          cameFrom.put(neighbor, current.obj);
          item = new PriorityItem(neighbor, tentativeGScore);
          valueMap.put(neighbor, item);
          if (!openSet.contains(item)) {
            openSet.add(item);
          }
        }
      }
    }
    // Open set is empty but goal was never reached
    return null;
  }

  public interface PathSearchNode {

    int heuristics();

    ArrayList<PathSearchNode> getNeighbors();
  }

  private static class PriorityItem implements Comparable {

    final Integer fScore;
    final Integer gScore;
    final PathSearchNode obj;

    PriorityItem(PathSearchNode obj, Integer gScore) {
      this.obj = obj;
      this.fScore = gScore + obj.heuristics();
      this.gScore = gScore;
    }

    @Override
    public int compareTo(Object o) {
      PriorityItem arg0 = (PriorityItem) o;
      if (!arg0.fScore.equals(fScore)) {
        return fScore.compareTo(arg0.fScore);
      }
      return arg0.gScore.compareTo(gScore);
    }

  }

}
