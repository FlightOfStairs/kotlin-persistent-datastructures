package org.flightofstairs.kpd

import org.flightofstairs.kpd.utils.maybeUpdate

private data class TreeSetNode<E>(
    val element: E,
    val comparator: Comparator<E>,
    val left: TreeSetNode<E>?,
    val right: TreeSetNode<E>?
) {
    constructor(element: E, comparator: Comparator<E>) : this(element, comparator, null, null)

    val size: Int = (left?.size ?: 0) + (right?.size ?: 0) + 1

    fun contains(element: E): Boolean {
        val comparison = comparator.compare(element, this.element)
        return when {
            comparison < 0 -> left != null && left.contains(element)
            comparison > 0 -> right != null && right.contains(element)
            else -> return true
        }
    }

    fun add(newElement: E): TreeSetNode<E> {
        val comparison = comparator.compare(newElement, this.element)

        return when {
            comparison < 0 -> addLeft(newElement, left)
            comparison > 0 -> addRight(newElement, right)
            else -> this
        }
    }

    private fun addLeft(newElement: E, left: TreeSetNode<E>?) = when {
        left == null -> TreeSetNode(this.element, comparator, TreeSetNode(newElement, comparator), right)
        comparator.compare(newElement, left.element) > 0 -> TreeSetNode(newElement, comparator, left, this)
        else -> left.maybeUpdate { it.add(newElement) }
            ?.let { TreeSetNode(this.element, comparator, it, right) }
            ?: this
    }

    private fun addRight(newElement: E, right: TreeSetNode<E>?) = when {
        right == null -> TreeSetNode(this.element, comparator, left, TreeSetNode(newElement, comparator))
        comparator.compare(newElement, right.element) < 0 -> TreeSetNode(newElement, comparator, this, right)
        else -> right.maybeUpdate { it.add(newElement) }
            ?.let { TreeSetNode(this.element, comparator, left, it) }
            ?: this
    }
}

class TreeSet<E> private constructor(private val root: TreeSetNode<E>?, private val comparator: Comparator<E>) {
    constructor(comparator: Comparator<E>) : this(null, comparator)

    fun add(element: E): TreeSet<E> {
        if (root == null) return TreeSet(TreeSetNode(element, comparator), comparator)
        return root.maybeUpdate { it.add(element) }?.let { TreeSet(it, comparator) } ?: this
    }

    fun contains(element: E): Boolean = root?.contains(element) ?: false

    val size = root?.size ?: 0
}
