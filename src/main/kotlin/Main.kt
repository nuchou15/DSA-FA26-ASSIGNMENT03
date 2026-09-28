package org.example

/**
 * ``Graph`` represents a directed graph
 * @param VertexType the type that represents a vertex in the graph
 */
interface Graph<VertexType> {
    /**
     * @return the vertices in the graph
     */
    fun getVertices(): Set<VertexType>

    /**
     * Add an edge between [from] and [to] with edge weight [cost]
     */
    fun addEdge(from: VertexType, to: VertexType, cost: Double)

    /**
     * Get all the edges that begin at [from]
     * @return a map where each key represents a vertex connected to [from] and the value represents the edge weight.
     */
    fun getEdges(from: VertexType): Map<VertexType, Double>

    /**
     * Remove all edges and vertices from the graph
     */
    fun clear()
}

/**
 * Implementation of a directed weighted graph where each vertex is associated with
 * a mapp of its neighbors
 * For example, for a graph like this: A --3--> B, B--2--> C, A--5--> C
 * It would be stored like this: A-> {B=3.0, C=5.0}, B-> {C=2.0}, C-> {}
 *
 */

class WeightedGraph<VertexType> : Graph<VertexType> {
    // The main data structure to store graph
    // Outer map: vertex -> its neighbors
    // Inner map: neighbors -> edge cost

    private val adjacency =
        mutableMapOf<VertexType, MutableMap<VertexType, Double>>()

    // Returns the vertices in the graph
    override fun getVertices(): Set<VertexType> {
        // Return the keys of the adjanceny map
        // toSet() gives a Set rather than allowing them to directly modify map
        return adjacency.keys.toSet()
    }

    // Add an edge between [from] and [to] with edge weight [cost]
    override fun addEdge(from: VertexType, to: VertexType, cost: Double) {
        // Dijkstra's algorithm does not work with negative edge costs so we reject them
        require(cost >= 0) {
            "Dijkstra's algorithm requires non negative edge costs."
        }

        // Make sure 'from' exists in the graph, if not, create empty map of neighbors
        adjacency.putIfAbsent(from, mutableMapOf())

        // Make sure 'to' is also considered a vertex, even though it doesn't have
        // outgoing edges
        adjacency.putIfAbsent(to, mutableMapOf())

        // Add the edge and its cost, if edge already exists then it'll replace old cost
        adjacency[from]!![to] = cost
    }

    // Get all the edges that begin at [from]
    // return a map where each key represents a vertex connected to [from] and the value represents the edge weight.
    override fun getEdges(from: VertexType): Map<VertexType, Double> {
        // If the vertex doesn't exist, there are no outgoing edges. Return empty map in that case
        return adjacency[from]?.toMap() ?: emptyMap()
    }

    //Remove all edges and vertices from the graph
    override fun clear() {
        adjacency.clear()
    }
}

/**
 * ``MinPriorityQueue`` maintains a priority queue where the lower
 *  the priority value, the sooner the element will be removed from
 *  the queue.
 *  @param T the representation of the items in the queue
 */
interface MinPriorityQueue<T> {
    /**
     * @return true if the queue is empty, false otherwise
     */
    fun isEmpty(): Boolean

    /**
     * Add [elem] with at level [priority]
     */
    fun addWithPriority(elem: T, priority: Double)

    /**
     * Get the next (highest priority) element and remove this element from the queue.
     * @return the next element in terms of priority.  If empty, return null.
     */
    fun next(): T?

    /**
     * Adjust the priority of the given element
     * @param elem whose priority should change
     * @param newPriority the priority to use for the element
     *   the lower the priority the earlier the element int
     *   the order.
     */
    fun adjustPriority(elem: T, newPriority: Double)
}

// Implementation of Priority Queue (using my MinHeap)
class PriorityQueue<T> : MinPriorityQueue<T> {

    // Priority queue contains one min heap, which stores: element -> priority
    private val heap = MinHeap<T>()

    //return true if the queue is empty, false otherwise
    override fun isEmpty(): Boolean {
        // Let the heap determine whether it is empty
        return heap.isEmpty()
    }

    // Add [elem] with at level [priority]
    override fun addWithPriority(
        elem: T,
        priority: Double
    ) {
        // Heap already knows how to insert element while maintaining
        // heap invariant
        heap.insert(elem, priority)
    }

    //Get the next (highest priority) element and remove this element from the queue.
    // return the next element in terms of priority.  If empty, return null.
    override fun next(): T? {
        // getMin() removes smallest element from min heap
        return heap.getMin()
    }

    // Adjust the priority of the given element
    // param elem whose priority should change
    // param newPriority the priority to use for the element
    // the lower the priority the earlier the element int
    // the order.
    override fun adjustPriority(
        elem: T,
        newPriority: Double
    ) {
        // let heap find element and move it to correct position after changing priority
        heap.adjustPriority(elem, newPriority)
    }
}

// Solving a problem with Djikstra. Specifically, Creating a graph that represents the cost
// (distance) to travel between particular various cities
fun <VertexType> dijkstra(graph: Graph<VertexType>, start: VertexType, target: VertexType): List<VertexType>? {
    // dist stores the cheapest distance we currently know from start to v
    // Initially, every distance is infinity
    val dist = mutableMapOf<VertexType, Double>()

    // prev stores the vertex before v on shortest path we found.
    // Ex.) prev[C] = B, meaning shortest path to C goes through B
    val prev = mutableMapOf<VertexType, VertexType>()

    // creating min priority queue
    // priority of vertex is its current distance from starting vertex.
    val queue = PriorityQueue<VertexType>()

    // keep track of vertices that are still in queue
    val unvisited = mutableSetOf<VertexType>()

    for (vertex in graph.getVertices()) {

        // initially, every vertex distance is infinite.
        dist[vertex] = Double.POSITIVE_INFINITY

        // Put it in our set of unvisited vertices
        unvisited.add(vertex)

        // Put it into the priority queue with infinite priority
        queue.addWithPriority(
            vertex,
            Double.POSITIVE_INFINITY
        )
    }

    // distance from start to start is 0.0
    dist[start] = 0.0

    // start vertex was added to queue, so change priority from infinity to 0
    queue.adjustPriority(start, 0.0)

    // keep processing vertices until priority queue becomes empty
    while (!queue.isEmpty()) {

        // remove vertex w/ smallest distance
        val u = queue.next() ?: break

        // u has been processed, so remove it from set of unvisited vertices
        unvisited.remove(u)

        // from every edge leaving u,
        // each pair contains v (neighboring vertex) and cost (cost of edge u->v)
        for ((v, cost) in graph.getEdges(u)) {

            // only consider vertices that haven't been removed from priority queue
            if (v !in unvisited) {
                continue
            }

            // calculate the cost (distance) of reaching v through u
            val alternativeDistance = dist[u]!! + cost

            // if this cost is cheaper than current best cost, update
            if (alternativeDistance < dist[v]!!) {

                // save the shorter distance
                dist[v] = alternativeDistance

                // remember this to backtrack
                prev[v] = u

                // update priority of v
                queue.adjustPriority(
                    v,
                    alternativeDistance
                )
            }
        }
    }

    // if target isn't start and doesn't appear in prev, there's no path
    if (target != start && target !in prev) {
        return null
    }

    // reconstructing shortest path, starting at target and prev backwards and then reverse list
    return generateSequence(target) { current ->
        prev[current]
    }.toList().asReversed()
}

// create graph to represent cities
// returns a graph where:
// vertex = city, edge = trip between cities, cost/edge weight = distance in miles
fun createCityGraph(): Graph<String> {

    // Create an empty weighted graph
    val graph = WeightedGraph<String>()

    // add edges between cities. Each edge has the form graph.addEdge(from, to, distance)
    // ex.) needham -> boston = 13 miles
    graph.addEdge("Boston", "Needham", 13.0)
    graph.addEdge("Needham", "Boston", 13.0)

    graph.addEdge("Boston", "Providence", 50.0)
    graph.addEdge("Providence", "Boston", 50.0)

    graph.addEdge("Needham", "Providence", 41.0)
    graph.addEdge("Providence", "Needham", 41.0)

    graph.addEdge("Needham", "New Haven", 132.0)
    graph.addEdge("New Haven", "Needham", 132.0)

    graph.addEdge("Providence", "New Haven", 103.0)
    graph.addEdge("New Haven", "Providence", 103.0)

    graph.addEdge("New Haven", "Paterson", 93.0)
    graph.addEdge("Paterson", "New Haven", 93.0)

    graph.addEdge("Providence", "Paterson", 193.0)
    graph.addEdge("Paterson", "Providence", 193.0)

    return graph
}