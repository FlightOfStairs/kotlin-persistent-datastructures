package org.flightofstairs.kpd

import io.kotlintest.matchers.collections.shouldBeSorted
import io.kotlintest.matchers.types.shouldBeSameInstanceAs
import io.kotlintest.shouldBe
import io.kotlintest.specs.AnnotationSpec
import java.util.Random

class TreeSetTest : AnnotationSpec() {

    @Test
    fun `empty tree contains nothing`() {
        val set = TreeSet<String>(compareBy { it })

        set.size shouldBe 0
        set.contains("foo") shouldBe false
    }

    @Test
    fun `single element tree contains element`() {
        val set = TreeSet<String>(compareBy { it }).add("foo")
        set.size shouldBe 1

        set.contains("foo") shouldBe true
    }

    @Test
    fun `single element tree does not contain other element`() {
        val set = TreeSet<String>(compareBy { it }).add("foo")
        set.size shouldBe 1

        set.contains("bar") shouldBe false
    }

    @Test
    fun `accepts duplicate elements`() {
        val set = TreeSet<String>(compareBy { it }).add("foo").add("foo")
        set.size shouldBe 1

        set.contains("foo") shouldBe true
        set.contains("bar") shouldBe false
    }

    @Test
    fun `mutation does not affect other users`() {
        val first = TreeSet<String>(compareBy { it }).add("foo")
        val second = first.add("bar")

        first.contains("bar") shouldBe false
        second.contains("bar") shouldBe true
    }

    @Test
    fun `arbitrary insertion order`() {
        val range = IntRange(1, 100)

        val shuffledRange = range.shuffled(Random(0))
        val set = shuffledRange.fold(TreeSet<Int>(compareBy { it })) { set, element -> set.add(element) }

        for (element in range) {
            set.contains(element) shouldBe true
        }
    }

    @Test
    fun `non-updates do not replace reference`() {
        val range = IntRange(1, 100)

        val originalSet = range.fold(TreeSet<Int>(compareBy { it })) { set, element -> set.add(element) }
        val nonUpdatedSet = range.fold(originalSet) { set, element -> set.add(element) }

        originalSet shouldBeSameInstanceAs nonUpdatedSet
    }

    @Test
    fun `iterator is sorted`() {
        val shuffledRange = IntRange(1, 100).shuffled(Random(0))

        val set = shuffledRange.fold(TreeSet<Int>(compareBy { it })) { map, element -> map.add(element) }

        set.toList().size shouldBe 100
        set.toList().shouldBeSorted()
    }
}
