import java.util.*;
import java.util.function.Predicate;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Stream;

public class AStarPathingStrategy implements PathingStrategy {


    @Override
    public List<Point> computePath(Point start, Point goal, Predicate<Point> canPassThrough, BiPredicate<Point, Point> withinReach, Function<Point, Stream<Point>> potentialNeighbors) {
        PriorityQueue<AStarNode> openSet = new PriorityQueue<>(Comparator.comparingInt(node -> node.f));
        Set<Point> closedSet = new HashSet<>();
        Map<Point, AStarNode> cameFrom = new HashMap<>();


        AStarNode startNode = new AStarNode(start, 0, heuristicCost(start, goal));
        openSet.add(startNode);

        while (!openSet.isEmpty()) {
            AStarNode current = openSet.poll();

            if (withinReach.test(current.position, goal)) {
                return reconstructPath(cameFrom, current);
            }

            closedSet.add(current.position);
            potentialNeighbors.apply(current.position)
                    .filter(canPassThrough)
                    .forEach(neighbor -> {
                        if (closedSet.contains(neighbor)) {
                            return; // Skip already evaluated neighbors
                        }

                        int tentativeG = current.g + 1; // Assuming each step has a cost of 1

                        AStarNode neighborNode = cameFrom.get(neighbor);
                        boolean isNewNode = neighborNode == null;

                        if (isNewNode || tentativeG < neighborNode.g) {
                            if (isNewNode) {
                                neighborNode = new AStarNode(neighbor, tentativeG, heuristicCost(neighbor, goal));
                                openSet.add(neighborNode);
                            } else {
                                openSet.remove(neighborNode); // Remove and re-add to update priority
                                neighborNode.g = tentativeG;
                                neighborNode.f = neighborNode.g + neighborNode.h;
                                openSet.add(neighborNode);
                            }

                            cameFrom.put(neighbor, current);
                        }
                    });
        }

        // If no path found
        return Collections.emptyList();
    }

    private List<Point> reconstructPath(Map<Point, AStarNode> cameFrom, AStarNode current) {
        List<Point> path = new ArrayList<>();
        path.add(current.position);

        while (cameFrom.containsKey(current.position)) {
            current = cameFrom.get(current.position);
            path.add(0, current.position); // Add to the front of the list to maintain order
        }
        return path;
    }

    private int heuristicCost(Point from, Point to) {
        // A simple heuristic (Manhattan distance)
        return Math.abs(from.x - to.x) + Math.abs(from.y - to.y);
    }

    private static class AStarNode {
        Point position;
        int g;  // Cost from start to current position
        int h;  // Heuristic cost from current position to goal
        int f;  // Total estimated cost

        public AStarNode(Point position, int g, int h) {
            this.position = position;
            this.g = g;
            this.h = h;
            this.f = g + h;
        }
    }
}
