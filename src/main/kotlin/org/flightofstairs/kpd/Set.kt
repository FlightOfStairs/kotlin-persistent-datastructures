package org.flightofstairs.kpd

interface Set<E> : Iterable<E> {
    fun add(element: E): Set<E>
    fun contains(element: E): Boolean
}
