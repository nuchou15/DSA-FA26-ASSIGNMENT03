#!/usr/bin/env kotlin
package org.example

/**
 * Representation of a min heap
 * @param T the type of the heap elements
 * @property elements has some data of type T and a number associated with that data
 * @property indexMap this is used for quick lookups of existing vertices
 */
class MinHeap<T> {

    // List to store actual heap
    // each pair contains: first (the element) and second (element's priority/heap number)
    // ex.) [(A, 2.0), (B, 5.0), (C, 7.0)}
    private val elements: MutableList<Pair<T, Double>> = mutableListOf()

    // Maps each element to its current position in the heap
    // ex.) if elements[3] = Pair("A", 5.0)
    // then: indexMap["A"] = 3
    // This allows us to find an element quickly when we want to change its priority
    private val indexMap: MutableMap<T, Int> = mutableMapOf()



    // @return true if the heap is empty and false otherwise
    fun isEmpty(): Boolean {
        return elements.isEmpty()
    }

    // Returns true if [data] is currently in the heap.
    fun contains(data: T): Boolean {
        return indexMap.containsKey(data)
    }

    // Inserts an element into the heap.
    // @param data the element to insert
    // @param priority the number associated with the element
    // @return true if element inserted, false if element's already in heap
    fun insert(data: T, priority: Double): Boolean {

        // prevents same element from appearing twice
        if (contains(data)) {
            return false
        }

        // Add new element to end of list
        elements.add(Pair(data, priority))

        // new element is currently in last position of list
        val newIndex = elements.lastIndex

        // Remember where this element is located
        indexMap[data] = newIndex

        // Bubble up to maintain heap invariant
        bubbleUp(newIndex)

        return true
    }

    // Return and remove element with smallest priority
    // @return the element with smallest priority or null if heap is empty
    fun getMin(): T? {

        // If heap is empty return null
        if (elements.isEmpty()) {
            return null
        }

        // Element at root is the minimum element
        val minimum = elements[0].first

        // If only one element, remove it
        if (elements.size == 1) {
            elements.clear()
            indexMap.remove(minimum)
            return minimum
        }

        // Move last element to root
        val lastElement = elements.removeAt(elements.lastIndex)
        elements[0] = lastElement

        // update index
        indexMap[lastElement.first] = 0

        // remove old minimum
        indexMap.remove(minimum)

        // Bubble down if new root is larger than children to maintain heap invariant
        bubbleDown(0)

        return minimum
    }

    // Change priority of existing element
    // @param data: the element whose priority should change
    // @param newPriority: new priority
    fun adjustPriority(data: T, newPriority: Double) {

        // Find where element currently is in heap
        val index = indexMap[data] ?: return

        // replace old pair with new pair that has new priority
        elements[index] = Pair(data, newPriority)

        // bubble up or down depending on whether new priority is smaller or larger than old
        bubbleUp(index)
        bubbleDown(index)
    }

    // Bubble up after inserting element or lowering priority if necessary.
    private fun bubbleUp(startIndex: Int) {

        // Keep track of the position we're currently examining.
        var currentIndex = startIndex

        // Continue until we reach root
        while (currentIndex > 0) {

            // Find the parent's position
            val parentIndex = parentIndex(currentIndex)

            // Does it satisfy the heap invariant?
            if (elements[currentIndex].second >= elements[parentIndex].second) {
                break
            }

            // Or else swap is needed
            swap(currentIndex, parentIndex)

            // Check from element's new position
            currentIndex = parentIndex
        }
    }


    // Bubble down after removing minimum element if necessary
    private fun bubbleDown(startIndex: Int) {

        // Start at the position provided.
        var currentIndex = startIndex

        // Continue until element has no smaller child
        while (true) {

            // Find the two possible children (left and right)
            val left = leftChildIndex(currentIndex)
            val right = rightChildIndex(currentIndex)

            if (left >= elements.size) {
                break
            }

            // assuming left child is smaller
            var smallerChild = left

            // if right exists and is smaller than left, use right
            if (
                right < elements.size &&
                elements[right].second < elements[left].second
            ) {
                smallerChild = right
            }

            // Make sure heap invariant is satisfied
            if (elements[currentIndex].second <= elements[smallerChild].second) {
                break
            }

            // Or else swap with smaller child
            swap(currentIndex, smallerChild)

            // Keep doing this
            currentIndex = smallerChild
        }
    }

    // Swap two elements in heap
    // Also have to update indexMap because elements moved to new positions
    private fun swap(index1: Int, index2: Int) {

        // Save the two elements before swapping them.
        val first = elements[index1]
        val second = elements[index2]

        // Swap them in the list.
        elements[index1] = second
        elements[index2] = first

        // update index map
        indexMap[first.first] = index2
        indexMap[second.first] = index1
    }

    // Returns the index of parent
    private fun parentIndex(index: Int): Int {
        return (index - 1) / 2
    }

    // Return index of left child
    private fun leftChildIndex(index: Int): Int {
        return 2 * index + 1
    }

    // Return index of right child
    private fun rightChildIndex(index: Int): Int {
        return 2 * index + 2
    }
}
