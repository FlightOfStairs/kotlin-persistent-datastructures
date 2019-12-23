package org.flightofstairs.kpd

private const val DEFAULT_NODEWIDTH = 8

private interface VectorTree<T> {
    operator fun get(index: Int): T?
    fun set(index: Int, value: T): VectorTree<T>
    fun sequence(): Sequence<T?>
}

private data class VectorLeaf<T>(private val value: T) : VectorTree<T> {
    override fun get(index: Int) = if (index == 0) value else error("Invalid index provided. Should never happen.")
    override fun set(index: Int, value: T) = if (index == 0) VectorLeaf(value) else error("Invalid index provided. Should never happen.")
    override fun sequence() = sequenceOf(value)
}

private class VectorNode<T>(
    private val children: Array<VectorTree<T>?>,
    private val capacity: Int,
    private val nodeWidth: Int
) : VectorTree<T> {

    private val childCapacity: Int = capacity / nodeWidth

    override fun get(index: Int): T? {
        if (index < 0 || index >= capacity) throw IndexOutOfBoundsException()
        return children[index / childCapacity]?.get(index % childCapacity)
    }

    override fun set(index: Int, value: T): VectorTree<T> {
        if (index < 0) throw IndexOutOfBoundsException()
        if (index >= capacity) {
            val newChildren: Array<VectorTree<T>?> = Array(nodeWidth) { if (it == 0) this else null }
            return VectorNode(newChildren, capacity * nodeWidth, nodeWidth).set(index, value)
        }

        val childIndex = index / childCapacity

        val child = children[childIndex]
            ?: if (childCapacity == 1) VectorLeaf(value) else VectorNode(arrayOfNulls(nodeWidth), childCapacity, nodeWidth)
        val updatedChild = child.set(index % childCapacity, value)

        val newChildren = Array(nodeWidth) { if (it == childIndex) updatedChild else children[it] }
        return VectorNode(newChildren, capacity, nodeWidth)
    }

    override fun sequence() = children.asSequence().flatMap {
        when (it) {
            null -> (0 until childCapacity).asSequence().map { null }
            else -> it.sequence()
        }
    }
}

class Vector<T> private constructor(private val root: VectorTree<T>, private val length: Int) : Iterable<T?> {
    /**
     * @param nodeWidth Can be tuned for use-case. Typically smaller will give faster write performance, and larger will give faster read.
     */
    constructor(nodeWidth: Int = DEFAULT_NODEWIDTH) : this(VectorNode(arrayOfNulls(DEFAULT_NODEWIDTH), nodeWidth, nodeWidth), 0) {
        check(nodeWidth > 1) { "nodeWidth must be greater than 1" }
    }

    operator fun get(index: Int): T? = root[index]
    fun set(index: Int, value: T): Vector<T> = Vector(root.set(index, value), setOf(index + 1, length).max()!!)
    fun append(value: T): Vector<T> = set(length, value)

    override fun iterator(): Iterator<T?> = root.sequence().take(length).iterator()
}
