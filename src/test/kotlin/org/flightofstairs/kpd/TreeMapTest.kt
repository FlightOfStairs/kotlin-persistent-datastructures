package org.flightofstairs.kpd

import io.kotlintest.matchers.collections.shouldBeSorted
import io.kotlintest.matchers.types.shouldBeSameInstanceAs
import io.kotlintest.shouldBe
import io.kotlintest.specs.AnnotationSpec
import java.util.Random

class TreeMapTest : AnnotationSpec() {

    @Test
    fun `empty map contains nothing`() {
        val map = TreeMap<String, Int>(compareBy { it })

        map.size shouldBe 0
        ("foo" in map) shouldBe false
    }

    @Test
    fun `single element tree contains element`() {
        val map = TreeMap<String, Int>(compareBy { it }).set("foo", 1)
        map.size shouldBe 1

        ("foo" in map) shouldBe true
        map["foo"] shouldBe 1
        map("foo") shouldBe 1
    }

    @Test
    fun `single element tree does not contain other element`() {
        val map = TreeMap<String, Int>(compareBy { it }).set("foo", 1)
        map.size shouldBe 1

        ("bar" in map) shouldBe false
    }

    @Test
    fun `allows replacement`() {
        val map = TreeMap<String, Int>(compareBy { it }).set("foo", 1).set("foo", 2)
        map.size shouldBe 1

        map["foo"] shouldBe 2
    }

    @Test
    fun `mutation does not affect other users`() {
        val first = TreeMap<String, Int>(compareBy { it }).set("foo", 1)
        val second = first.set("bar", 2)

        second.size shouldBe 2

        ("foo" in first) shouldBe true
        ("bar" in first) shouldBe false

        ("foo" in second) shouldBe true
        ("bar" in second) shouldBe true
    }

    @Test
    fun `arbitrary insertion order`() {
        val range = IntRange(1, 100)

        val shuffledRange = range.shuffled(Random(0))
        val map = shuffledRange.fold(TreeMap<String, Int>(compareBy { it })) { map, element -> map.set("key-$element", element) }

        map.size shouldBe 100

        for (element in range) {
            map["key-$element"] shouldBe element
        }
    }

    @Test
    fun `non-updates do not replace reference`() {
        val range = IntRange(1, 100).toList()

        val originalMap = range.fold(TreeMap<String, Int>(compareBy { it })) { map, element -> map.set("key-$element", element) }
        val nonUpdatedMap = range.fold(originalMap) { map, element -> map.set("key-$element", element) }

        originalMap shouldBeSameInstanceAs nonUpdatedMap
    }

    @Test
    fun `iterator is sorted`() {
        val shuffledRange = IntRange(1, 100).shuffled(Random(0))

        val map = shuffledRange.fold(TreeMap<String, Int>(compareBy { it })) { map, element -> map.set("key-$element", element) }

        map.toList().size shouldBe 100
        map.map { it.key }.shouldBeSorted()
    }
}
