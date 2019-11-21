package com.axlan.gdxtactics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

@SuppressWarnings({"WeakerAccess", "unused"})
public class PathSearch {

  /**
   * Finds a path from start to goal.
   *
   * @param start the start point of the path
   * @param goal the desired finish point
   * @return list of nodes that form the shortest path to the node in the parameter current or null
   *     if no path exists
   */
  public static ArrayList<PathSearchNode> runSearchByGoal(
      PathSearchNode start, PathSearchNode goal) {
    return runSearchByGoal(start, goal, null);
  }

  /**
   * Finds a path from start to goal if one exists that is <=
   *
   * @param start the start point of the path
   * @param goal the desired finish point
   * @param distanceLimit the distance limit for the paths searched
   * @return list of nodes that form the shortest path to the node in the parameter current or null
   *     if no path exists within the distanceLimit
   */
  public static ArrayList<PathSearchNode> runSearchByGoal(
      PathSearchNode start, PathSearchNode goal, Integer distanceLimit) {
    AStarSearchResult result = aStarSearch(start, goal, distanceLimit);
    if (result == null) {
      return null;
    }
    return reconstructPath(result.cameFrom, goal);
  }

  /**
   * Get the shortest distance and paths to nodes that are within distanceLimit of the start node
   *
   * @param start start of search space
   * @param distanceLimit distance limit for search
   * @return results to reconstruct reachable nodes within distanceLimit
   */
  public static AStarSearchResult runSearchByDistance(PathSearchNode start, int distanceLimit) {
    return aStarSearch(start, null, distanceLimit);
  }

  /**
   * Get the shortest distance and paths to nodes that are reachable from the start node
   *
   * @param start node to start search from
   * @return results to reconstruct reachable nodes
   */
  public static AStarSearchResult runSearchAll(PathSearchNode start) {
    return aStarSearch(start, null, null);
  }

  /**
   * Filter a result set to the nodes that are within a distance limit of the start point
   *
   * @param result the result set to filter
   * @param distanceLimit max distance for results in new set
   * @return a new set of results where the nodes are <= distanceLimit from the start point
   */
  public static AStarSearchResult filterResultByDistance(
      AStarSearchResult result, int distanceLimit) {
    AStarSearchResult newResult = new AStarSearchResult(result);
    List<PathSearchNode> nodes = new ArrayList<>(result.valueMap.keySet());
    for (PathSearchNode node : nodes) {
      if (result.valueMap.get(node).gScore > distanceLimit) {
        result.valueMap.remove(node);
        result.cameFrom.remove(node);
      }
    }
    return newResult;
  }

  /**
   * Take the mapping of nodes and retrieve the list of nodes that form the shortest path to the
   * goal
   *
   * @param cameFrom A mapping of nodes to the node that got to them with the shortest distance
   * @param endPoint The node to get the shortest path to
   * @return list of nodes that form the shortest path to the node in the parameter current or null
   *     if no path exists
   */
  public static ArrayList<PathSearchNode> reconstructPath(
      HashMap<PathSearchNode, PathSearchNode> cameFrom, PathSearchNode endPoint) {
    if (!cameFrom.containsKey(endPoint)) {
      return null;
    }
    ArrayList<PathSearchNode> totalPath = new ArrayList<>();
    totalPath.add(endPoint);
    while (cameFrom.containsKey(endPoint)) {
      endPoint = cameFrom.get(endPoint);
      totalPath.add(0, endPoint);
    }
    return totalPath;
  }

  /**
   * A* finds path to nodes connected to start node
   *
   * <p>Implementation adapted from <a
   * href=https://en.wikipedia.org/wiki/A*_search_algorithm>https://en.wikipedia.org/wiki/A*_search_algorithm</a>
   *
   * @param start node to start from
   * @param goal node to end at. If null map all nodes linked to start.
   * @param distanceLimit only search nodes that are <= this distance limit. No limit if null
   * @return Returns results mapping distances and paths. Used in other class functions.
   */
  private static AStarSearchResult aStarSearch(
      PathSearchNode start, PathSearchNode goal, Integer distanceLimit) {
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

    // For node n, cameFrom[n] is the node immediately preceding it on the cheapest path from start
    // to n currently known.
    HashMap<PathSearchNode, PathSearchNode> cameFrom = new HashMap<>();

    while (openSet.size() > 0) {
      PriorityItem current = openSet.remove();
      if (current.obj.equals(goal)) {
        return new AStarSearchResult(cameFrom, valueMap);
      }
      closedSet.add(current.obj);
      for (PathSearchNode neighbor : current.obj.getNeighbors()) {
        if (closedSet.contains(neighbor)) {
          continue;
        }
        // d(current,neighbor) is the weight of the edge from current to neighbor always
        // tentative_gScore is the distance from start to the neighbor through current
        int tentativeGScore = current.gScore + current.obj.edgeWeight(neighbor);
        // Ignore nodes that are over the distance limit if present
        if (distanceLimit != null && tentativeGScore > distanceLimit) {
          continue;
        }
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
    if (goal == null) {
      return new AStarSearchResult(cameFrom, valueMap);
    }
    // Open set is empty but goal was never reached
    return null;
  }

  /**
   * Interface for nodes that will be searched with AStar algorithm
   */
  public interface PathSearchNode {

    /**
     * Heuristics estimate of distance from the node to the goal.
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
   * Raw results of A* search
   */
  public static class AStarSearchResult {

    /**
     * For node n, cameFrom[n] is the node immediately preceding it on the cheapest path from start
     * to n currently known.
     */
    public final HashMap<PathSearchNode, PathSearchNode> cameFrom;

    /**
     * Mapping of nodes to their distance scores.
     */
    public final HashMap<PathSearchNode, PriorityItem> valueMap;

    /**
     * @param cameFrom {@link #cameFrom}
     * @param valueMap {@link #valueMap}
     */
    AStarSearchResult(
        HashMap<PathSearchNode, PathSearchNode> cameFrom,
        HashMap<PathSearchNode, PriorityItem> valueMap) {
      this.cameFrom = cameFrom;
      this.valueMap = valueMap;
    }

    AStarSearchResult(AStarSearchResult result) {
      this.cameFrom = new HashMap<>(result.cameFrom);
      this.valueMap = new HashMap<>(result.valueMap);
    }
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

    /**
     * Sets comparison method for sorting
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
