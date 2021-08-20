@file:Suppress("MemberVisibilityCanBePrivate")

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

internal class ConvexHullKtTest {
	@Test
	fun a() {
		
		val points = setOf(
			Point(0.0, 1.0),
			Point(1.0, 1.0),
			Point(1.0, 0.0),
			Point(1.0, -1.0),
			Point(0.0, -1.0),
			Point(-1.0, -1.0),
			Point(-1.0, 0.0),
			Point(-1.0, 1.0),
			Point(0.0, 0.0)
		)
		val hull = findHullPoints(points)
		val expected = setOf(
			Point(1.0, 1.0),
			Point(1.0, -1.0),
			Point(-1.0, -1.0),
			Point(-1.0, 1.0)
		)
		assertEquals(expected.toSortedSet(xComparator), hull.toSortedSet(xComparator))
	}
	
	@Test
	fun b() {
		val points = setOf(
			Point(0.0, 0.0),
			Point(1.0, 0.0),
			Point(2.0, 0.0),
			Point(-1.0, 0.0),
			Point(-2.0, 0.0),
			Point(0.0, 1.0),
			Point(0.0, 2.0),
			Point(0.0, -1.0),
			Point(0.0, -2.0),
			//
			Point(1.0, 1.0),
			Point(0.5, 1.5),
			Point(1.0, 1.5),
			Point(1.5, 1.5),
			Point(1.5, 1.0),
			Point(1.5, 0.5),
			//
			Point(1.0, -1.0),
			Point(0.5, -1.5),
			Point(1.0, -1.5),
			Point(1.5, -1.5),
			Point(1.5, -1.0),
			Point(1.5, -0.5),
			//
			Point(-1.0, 1.0),
			Point(-0.5, 1.5),
			Point(-1.0, 1.5),
			Point(-1.5, 1.5),
			Point(-1.5, 1.0),
			Point(-1.5, 0.5),
			//
			Point(-1.0, -1.0),
			Point(-0.5, -1.5),
			Point(-1.0, -1.5),
			Point(-1.5, -1.5),
			Point(-1.5, -1.0),
			Point(-1.5, -0.5),
		)
		val hull = findHullPoints(points)
		val expected = setOf(
			Point(2.0, 0.0),
			Point(-2.0, 0.0),
			Point(0.0, 2.0),
			Point(0.0, -2.0),
			Point(1.5, 1.5),
			Point(1.5, -1.5),
			Point(-1.5, 1.5),
			Point(-1.5, -1.5),
		)
		assertEquals(expected.toSortedSet(xComparator), hull.toSortedSet(xComparator))
	}
	
	@Test
	fun c() {
		val points = setOf(
			Point(0.0, 0.0),
			Point(1.0, 0.0),
			Point(2.0, 0.0),
			Point(0.0, 1.0),
			Point(0.0, 2.0),
			//
			Point(1.0, 1.0),
			Point(0.5, 1.5),
			Point(1.0, 1.5),
			Point(1.2, 1.2),
			Point(1.5, 1.0),
			Point(1.5, 0.5),
		)
		val hull = findHullPoints(points)
		val expected = setOf(
			Point(0.0, 0.0),
			Point(2.0, 0.0),
			Point(0.0, 2.0),
			Point(1.0, 1.5),
			Point(1.5, 1.0),
		)
		assertEquals(expected.toSortedSet(xComparator), hull.toSortedSet(xComparator))
	}
	
	@Test
	fun d() {
		val points = setOf(
			Point(1, 0.25),
			Point(0.6, 0.2),
			Point(0, 0),
			Point(0.2, 0),
			Point(1.2, 0.8),
		)
		val hull = findHullPoints(points)
		val expected = setOf(
			Point(1, 0.25),
//			Point(0.6, 0.2),
			Point(0, 0),
			Point(0.2, 0),
			Point(1.2, 0.8),
		)
		assertEquals(expected.toSortedSet(xComparator), hull.toSortedSet(xComparator))
	}
	
	@Test
	fun random() {
		val random = Random(20211208)
		
		val points = mutableSetOf<Point>()
		
		repeat(20) {
			points.add(Point(random.nextDouble(), random.nextDouble()))
		}
		
		val hull = findHullPoints(points)
		
		val expected = mutableSetOf<Point>()
		expected.add(points.elementAt(2))
		expected.add(points.elementAt(5))
		expected.add(points.elementAt(6))
		expected.add(points.elementAt(8))
		expected.add(points.elementAt(9))
		expected.add(points.elementAt(12))
		expected.add(points.elementAt(13))

		assertEquals(expected.toSortedSet(xComparator), hull.toSortedSet(xComparator))
	}
}
