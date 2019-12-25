package org.flightofstairs.kpd

import org.flightofstairs.kpd.utils.maybeUpdate

class TreeSet<E> private constructor(private val map: Map<E, Boolean>) : Set<E> {
    constructor(comparator: Comparator<E>) : this(TreeMap(comparator))

    override fun add(element: E): TreeSet<E> = map.maybeUpdate { it.set(element, true) }?.let { TreeSet(it) } ?: this
    override fun contains(element: E): Boolean = map[element] ?: false
    override fun iterator(): Iterator<E> = map.asSequence().map { (k, _) -> k }.iterator()

    val size = map.size
}
