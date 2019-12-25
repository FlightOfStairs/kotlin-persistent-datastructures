package org.flightofstairs.kpd

import org.flightofstairs.kpd.utils.maybeUpdate
import kotlin.math.sign

private class TreeMapNode<K, V>(
    private val entry: Map.Entry<K, V>,
    private val comparator: Comparator<K>,
    private val left: TreeMapNode<K, V>?,
    private val right: TreeMapNode<K, V>?
) {
    constructor(entry: Map.Entry<K, V>, comparator: Comparator<K>) : this(entry, comparator, null, null)

    val size: Int = (left?.size ?: 0) + (right?.size ?: 0) + 1

    operator fun get(key: K): Map.Entry<K, V>? = when (comparator.compare(key, entry.key).sign) {
        -1 -> left?.get(key)
        1 -> right?.get(key)
        else -> entry
    }

    fun set(newEntry: Map.Entry<K, V>): TreeMapNode<K, V> = when (comparator.compare(newEntry.key, entry.key).sign) {
        -1 -> addLeft(newEntry)
        1 -> addRight(newEntry)
        else -> if (newEntry.value === entry.value) this else TreeMapNode(newEntry, comparator, left, right)
    }

    private fun addLeft(newEntry: Map.Entry<K, V>): TreeMapNode<K, V> {
        if (left == null) return TreeMapNode(this.entry, comparator, TreeMapNode(newEntry, comparator), right)
        return left.maybeUpdate { it.set(newEntry) }?.let { TreeMapNode(this.entry, comparator, it, right) } ?: this
    }

    private fun addRight(newEntry: Map.Entry<K, V>): TreeMapNode<K, V> {
        if (right == null) return TreeMapNode(this.entry, comparator, left, TreeMapNode(newEntry, comparator))
        return right.maybeUpdate { it.set(newEntry) }?.let { TreeMapNode(this.entry, comparator, left, it) } ?: this
    }

    fun sequence(): Sequence<Map.Entry<K, V>> = sequence {
        if (left != null) yieldAll(left.sequence())
        yield(entry)
        if (right != null) yieldAll(right.sequence())
    }
}

class TreeMap<K, V> private constructor(
    private val root: TreeMapNode<K, V>?,
    private val comparator: Comparator<K>
) : Map<K, V> {
    constructor(comparator: Comparator<K>) : this(null, comparator)

    override fun get(key: K): V? = root?.get(key)?.value

    override fun set(key: K, value: V): TreeMap<K, V> {
        val entry = Map.Entry(key, value)
        return if (root == null) {
            TreeMap(TreeMapNode(entry, comparator), comparator)
        } else root.maybeUpdate { it.set(entry) }
            ?.let { TreeMap(it, comparator) }
            ?: this
    }

    override val size: Int = root?.size ?: 0

    override fun contains(key: K): Boolean = root?.get(key) != null
    override fun iterator(): Iterator<Map.Entry<K, V>> = root?.sequence()?.iterator() ?: emptySequence<Map.Entry<K, V>>().iterator()
}
