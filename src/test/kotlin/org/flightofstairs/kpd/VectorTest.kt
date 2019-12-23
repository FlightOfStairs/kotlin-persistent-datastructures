package org.flightofstairs.kpd

import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.AnnotationSpec

internal class VectorTest : AnnotationSpec() {
    @Test
    fun `get negative fails`() {
        shouldThrow<IndexOutOfBoundsException> {
            Vector<Int>()[-1]
        }
    }

    @Test
    fun `set negative fails`() {
        shouldThrow<IndexOutOfBoundsException> {
            Vector<Int>().set(-1, 0)
        }
    }

    @Test
    fun `set first`() {
        Vector<Int>().set(0, 1)[0] shouldBe 1
    }

    @Test
    fun `extreme range`() {
        val vector = Vector<String>(nodeWidth = 8)
            .set(0, "foo")
            .set(1000000000, "bar")

        vector[0] shouldBe "foo"
        vector[1000000000] shouldBe "bar"
    }

    @Test
    fun `iterator produces nulls`() {
        val nodeWidth = 8

        val vector = Vector<String>(nodeWidth)
            .set(0, "foo")
            .set(nodeWidth, "bar") // Sibling node to first
            .set(nodeWidth * nodeWidth, "baz") // Cousin node to first

        vector.toList() shouldBe List(nodeWidth * nodeWidth + 1) {
            when (it) {
                0 -> "foo"
                nodeWidth -> "bar"
                nodeWidth * nodeWidth -> "baz"
                else -> null
            }
        }
    }

    @Test
    fun `iterator produces in correct order`() {
        val vector = IntRange(0, 100).fold(Vector<Int>()) { vector, element ->
            vector.set(element, element)
        }

        vector.toList() shouldBe IntRange(0, 100).toList()
    }

    @Test
    fun `append appends`() {
        val vector = IntRange(0, 100).fold(Vector<Int>()) { vector, element -> vector.append(element) }

        vector.toList() shouldBe IntRange(0, 100).toList()
    }
}
