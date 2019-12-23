package org.flightofstairs.kpd

import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.AnnotationSpec
import java.util.EmptyStackException

internal class StackTest : AnnotationSpec() {

    @Test
    fun `pop empty stack fails`() {
        val stack = Stack<Int>()

        shouldThrow<EmptyStackException> {
            stack.pop()
        }
    }

    @Test
    fun `items returned in reverse order`() {
        var stack = Stack<Int>().push(10).push(20).push(30).push(40).push(50)

        stack.size shouldBe 5
        stack.peek() shouldBe 50

        for (expectedValue in listOf(50, 40, 30, 20, 10)) {
            val (newStack, value) = stack.pop()
            stack = newStack
            value shouldBe expectedValue
        }

        stack.size shouldBe 0
    }

    @Test
    fun `mutated stacks don't affect others`() {
        val rootStack = Stack<Int>().push(1).push(2)
        val (poppedStack) = rootStack.pop()
        val pushedStack = rootStack.push(3)

        poppedStack.toList() shouldBe listOf(1)
        rootStack.toList() shouldBe listOf(2, 1)
        pushedStack.toList() shouldBe listOf(3, 2, 1)
    }

    @Test
    fun `iterate produces in reverse order`() {
        Stack<Int>().push(1).push(2).push(3).toList() shouldBe listOf(3, 2, 1)
    }

    @Test
    fun `iterators do not interfere`() {
        val stack = Stack<Int>().push(1).push(2).push(3)
        val iteratorA = stack.iterator()
        val iteratorB = stack.iterator()

        iteratorA.asSequence().toList() shouldBe listOf(3, 2, 1)
        iteratorB.asSequence().toList() shouldBe listOf(3, 2, 1)
    }
}
