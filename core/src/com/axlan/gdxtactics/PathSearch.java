package com.axlan.gdxtactics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

public class PathSearch {

  /**
   * Take the mapping of nodes and retrieve the list of nodes that form the shortest path to the
   * goal
   *
   * @param cameFrom A mapping of nodes to the node that got to them with the shortest distance
   * @param current  The node to get the shortest path to
   * @return list of nodes that form the shortest path to the node in the parameter current
   */
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

  /** A* finds a path from start to goal.
   * <p>Implementation adapted from <a href=https://en.wikipedia.org/wiki/A*_search_algorithm>https://en.wikipedia.org/wiki/A*_search_algorithm</a>
   * @param start node to start from
   * @param goal node to end at
   * @return Returns the shortest path from start to goal, or null if no path exists
   */
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
      if (current.obj.equals(goal)) {
        return reconstructPath(cameFrom, current.obj);
      }
      closedSet.add(current.obj);
      for (PathSearchNode neighbor : current.obj.getNeighbors()) {
        if (closedSet.contains(neighbor)) {
          continue;
        }
        // d(current,neighbor) is the weight of the edge from current to neighbor always
        // tentative_gScore is the distance from start to the neighbor through current
        int tentativeGScore = current.gScore + current.obj.edgeWeight(neighbor);
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

  /**
   * Interface for nodes that will be searched with AStar algorithm
   */
  public interface PathSearchNode {

    /** Heuristics estimate of distance from the node to the goal.
     *
     * <p>As long as the heuristic does not overestimate distances, A* finds an optimal path
     *
     * @return Estimated distance from the node to the goal
     */
    int heuristics();

    /**
     * @param neighbor The neighboring node to get the distance to
     * @return The distance to the specified neighbor
     */
    @SuppressWarnings({"unused", "SameReturnValue"})
    int edgeWeight(PathSearchNode neighbor);

    /**
     * @return List of nodes neighboring this node
     */
    List<PathSearchNode> getNeighbors();
  }

  /**
   * Helper class to allow nodes to be sorted in priority Queue
   */
  private static class PriorityItem implements Comparable {

    /** The estimated total distance to the goal through the node obj */
    final Integer fScore;
    /** The best known distance from the start to the obj node */
    final Integer gScore;
    /** The node being sorted */
    final PathSearchNode obj;

    /**
     * @see #obj
     * @see #gScore
     */
    PriorityItem(PathSearchNode obj, Integer gScore) {
      this.obj = obj;
      this.fScore = gScore + obj.heuristics();
      this.gScore = gScore;
    }

    /** Sets comparison method for sorting
     */
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
