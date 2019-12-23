package org.flightofstairs.kpd

import java.util.EmptyStackException

private data class Node<T>(val next: Node<T>?, val value: T)

data class StackPop<T>(val stack: Stack<T>, val value: T)

class Stack<T> private constructor(private val head: Node<T>?, val size: Int): Iterable<T> {

    constructor() : this(null, 0)

    fun push(value: T): Stack<T> = Stack(Node(head, value), size + 1)

    fun pop(): StackPop<T> {
        if (head == null) throw EmptyStackException()
        val (newHead, value) = head
        return StackPop(Stack(newHead, size - 1), value)
    }

    fun peek(): T = head?.value ?: throw EmptyStackException()

    override fun iterator(): Iterator<T> = StackIterator(this)
}

private class StackIterator<T>(private var stack: Stack<T>): Iterator<T> {
    override fun hasNext() = stack.size != 0

    override fun next(): T {
        val (nextStack, value) = stack.pop()
        stack = nextStack
        return value
    }
}