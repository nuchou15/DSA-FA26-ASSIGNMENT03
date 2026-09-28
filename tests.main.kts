#!/usr/bin/env kotlin
package org.example

fun testMinHeap() {
    // Create a heap containing strings
    val heap = MinHeap<String>()

    // Add several elements with different priorities
    heap.insert("A", 5.0)
    heap.insert("B", 2.0)
    heap.insert("C", 8.0)
    heap.insert("D", 1.0)

    // D priority 1 so should be removed first
    check(heap.getMin() == "D")

    // B should be next
    check(heap.getMin() == "B")

    // A next
    check(heap.getMin() == "A")

    // C is now only remaining
    check(heap.getMin() == "C")

    // Heap should be smpty
    check(heap.isEmpty())

    // Return null bc empty
    check(heap.getMin() == null)

    // Test inserting elements so we can test changing priorities
    heap.insert("A", 10.0)
    heap.insert("B", 20.0)
    heap.insert("C", 30.0)

    // Change C's priority so it is minimum
    heap.adjustPriority("C", 1.0)
    check(heap.getMin() == "C")

    // Change B priority
    heap.adjustPriority("B", 2.0)
    check(heap.getMin() == "B")

    // Only A remaining
    check(heap.getMin() == "A")
    println("Min heap tests passed")
}

fun testDijkstra() {
    // Create our graph
    val graph = WeightedGraph<String>()

    // Add the directed edges
    graph.addEdge("A", "B", 4.0)
    graph.addEdge("A", "C", 2.0)
    graph.addEdge("B", "D", 1.0)
    graph.addEdge("C", "D", 3.0)

    // Find Shortest parth from A to D
    // A -> B -> D (cost = 4 + 1 = 5)
    // A -> C -> D (cost = 2+3 = 5)
    // Since both are 5, they are both valid
    val path = dijkstra(
        graph,
        "A",
        "D"
    )

    // Accept either since both work
    check(
        path == listOf("A", "B", "D") ||
                path == listOf("A", "C", "D")
    )

    // Test a path with only one option
    check(
        dijkstra(graph, "A", "B")
                == listOf("A", "B")
    )

    // No path from D to A because all edges point to D
    check(
        dijkstra(graph, "D", "A") == null
    )

    println("Dijkstra graphs tests passed")
}

fun main() {

    testMinHeap()
    testDijkstra()

    println("All tests passed")
}


