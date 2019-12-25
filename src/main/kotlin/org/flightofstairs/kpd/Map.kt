package org.flightofstairs.kpd

interface Map<K, V> : Iterable<Map.Entry<K, V>>, (K) -> V? {
    operator fun get(key: K): V?
    operator fun set(key: K, value: V): Map<K, V>

    operator fun contains(key: K): Boolean

    val size: Int

    override fun invoke(key: K): V? = get(key)

    data class Entry<K, V>(val key: K, val value: V)
}
