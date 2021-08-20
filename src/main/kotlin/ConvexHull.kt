import java.util.*
import java.util.concurrent.*
import kotlin.Double.Companion.NEGATIVE_INFINITY
import kotlin.Double.Companion.POSITIVE_INFINITY
import kotlin.system.measureTimeMillis
import kotlin.test.assertFalse

val line = "-".repeat(50)

val xComparator: (a: Point, b: Point) -> Int = { a, b ->
	when {
		a === b -> 0
		a.x > b.x -> 1
		a.x < b.x -> -1
		a.y > b.y -> 1
		a.y < b.y -> -1
		else -> 0
	}
}
val yComparator: (a: Point, b: Point) -> Int = { a, b ->
	when {
		a === b -> 0
		a.y > b.y -> 1
		a.y < b.y -> -1
		a.x > b.x -> -1
		a.x < b.x -> 1
		else -> 0
	}
}
val invXComparator: (a: Point, b: Point) -> Int = { a, b ->
	-xComparator(a, b)
}
val invYComparator: (a: Point, b: Point) -> Int = { a, b ->
	-yComparator(a, b)
}

val executor: ExecutorService = Executors.newFixedThreadPool(4)

fun debugLog(what: Any? = null) {}

fun main() {
	val random = Random(20210816)
	val points = mutableSetOf<Point>()
	repeat(1_000_000) {
		points.add(Point(random.nextDouble(), random.nextDouble()))
		val time = measureTimeMillis {
			findHullPoints(points)
		}
		println("$it, $time")
	}
}

fun findHullPoints(points: Set<Point>): Set<Point> {
	debugLog(line)
	debugLog("all points")
	points.forEach { debugLog(it.toStringWithIndex(points)) }
	debugLog(line)
	
	val bottomPoint = points.fold(Point(POSITIVE_INFINITY,
		POSITIVE_INFINITY)) { last, current -> if (current.y < last.y || current.y == last.y && current.x < last.x) current else last }
	val rightPoint = points.fold(Point(NEGATIVE_INFINITY,
		POSITIVE_INFINITY)) { last, current -> if (current.x > last.x || current.x == last.x && current.y < last.y) current else last }
	val topPoint = points.fold(Point(NEGATIVE_INFINITY,
		NEGATIVE_INFINITY)) { last, current -> if (current.y > last.y || current.y == last.y && current.x > last.x) current else last }
	val leftPoint = points.fold(Point(POSITIVE_INFINITY,
		NEGATIVE_INFINITY)) { last, current -> if (current.x < last.x || current.x == last.x && current.y > last.y) current else last }
	
	val hull = mutableSetOf<Point>()
	hull.add(leftPoint)
	
	val bottomLeft = Callable {
		// bottom left, from left to right
		debugLog("bottom left, from left to right")
		findHullPointInCorner(points, xComparator, leftPoint, bottomPoint, hull) { lastHullPoint, currentPoint, endHullPoint ->
			// only try if the point is below the last hull point, and left of the end hull point
			currentPoint.y <= lastHullPoint.y && currentPoint.x <= endHullPoint.x
		}
	}
	
	val bottomRight = Callable {
		// bottom right, from bottom to top
		debugLog("bottom right, from bottom to top")
		findHullPointInCorner(points, yComparator, bottomPoint, rightPoint, hull) { lastHullPoint, currentPoint, endHullPoint ->
			// only try if point is right of the last hull point, and below of the end hull point
			currentPoint.x >= lastHullPoint.x && currentPoint.y <= endHullPoint.y
		}
	}
	
	val topRight = Callable {
		// top right, from right to left
		debugLog("top right, from right to left")
		findHullPointInCorner(points, invXComparator, rightPoint, topPoint, hull) { lastHullPoint, currentPoint, endHullPoint ->
			// only try if point is above the last hull point, and right of the end hull point
			currentPoint.y >= lastHullPoint.y && currentPoint.x >= endHullPoint.x
		}
	}
	
	val topLeft = Callable {
		// top left, from top to bottom
		debugLog("top left, from top to bottom")
		findHullPointInCorner(points, invYComparator, topPoint, leftPoint, hull) { lastHullPoint, currentPoint, endHullPoint ->
			// only try if point is left of the last hull point, and above of the end hull point
			currentPoint.x <= lastHullPoint.x && currentPoint.y >= endHullPoint.y
		}
	}
	
	val futures = executor.invokeAll(listOf(topLeft, topRight, bottomLeft, bottomRight), 1, TimeUnit.SECONDS)
	if (futures.any { it.isCancelled }) throw TimeoutException()
	
	debugLog(line)
	debugLog("hull points")
	hull.forEach { debugLog(it.toStringWithIndex(points)) }
	debugLog(line)
	
	return hull
}

private fun findHullPointInCorner(
	points: Set<Point>,
	comparator: Comparator<Point>,
	startHullPoint: Point,
	endHullPoint: Point,
	hull: MutableSet<Point>,
	isInCorner: (lastHullPoint: Point, currentPoint: Point, endHullPoint: Point) -> Boolean,
) {
	debugLog("${startHullPoint.toStringWithIndex(points)} .. ${endHullPoint.toStringWithIndex(points)}")
	
	val currentPoints = TreeSet(comparator).apply { addAll(points) }
	var lastHullPoint = startHullPoint
	for (currentPoint in currentPoints) {
		if (lastHullPoint != currentPoint && currentPoint != endHullPoint && isInCorner(lastHullPoint, currentPoint, endHullPoint)) {
			debugLog("${lastHullPoint.toStringWithIndex(points)}  ${currentPoint.toStringWithIndex(points)}  ${endHullPoint.toStringWithIndex(points)}")
			val lastToNext = Vector(lastHullPoint, endHullPoint)
			val lastToCurrent = Vector(lastHullPoint, currentPoint)
			debugLog("$lastToNext  $lastToCurrent")
			
			val isOnHull = lastToCurrent.isRightOf(lastToNext)
			debugLog(isOnHull)
			if (isOnHull) {
				var i = hull.size - 1
				while (i > hull.indexOf(startHullPoint)) {
					val beforeLastHullPoint = hull.elementAt(i - 1)
					
					val removeLastPointFromHull = Vector(beforeLastHullPoint, currentPoint).isRightOf(Vector(beforeLastHullPoint, lastHullPoint))
					if (!removeLastPointFromHull) break
					
					hull.remove(lastHullPoint)
					lastHullPoint = beforeLastHullPoint
					i--
				}
				
				hull.add(currentPoint)
				lastHullPoint = currentPoint
			}
		} else debugLog("${currentPoint.toStringWithIndex(points)} not in corner")
	}
	
	hull.add(endHullPoint)
}

class Point(val x: Double, val y: Double) {
	constructor(x: Number, y: Number) : this(x.toDouble(), y.toDouble())
	
	override fun toString(): String = "($x, $y)"
	fun toStringWithIndex(points: Collection<Point>): String = ('A' + points.indexOf(this)).toString() + "($x, $y)"
	override fun equals(other: Any?): Boolean = other is Point && other.x == this.x && other.y == this.y
	override fun hashCode(): Int = 31 * x.hashCode() + y.hashCode()
}

class Vector(private val dx: Double, private val dy: Double) {
	constructor(from: Point, to: Point) : this(to.x - from.x, to.y - from.y)
	constructor(dx: Number, dy: Number) : this(dx.toDouble(), dy.toDouble())
	
	init {
		assertFalse(dx == 0.0 && dy == 0.0)
	}
	
	operator fun times(num: Number) = Vector(dx * num.toDouble(), dy * num.toDouble())
	
	fun isRightOf(other: Vector) = if (dx != 0.0) {
		other.dy * dx > dy * other.dx // == other.dy / other.dx > dy / dx
	} else when { // vertical
		dy < 0.0 -> other.dx > 0 // to bottom
		dy > 0.0 -> other.dx < 0 // to top
		else -> throw IllegalStateException() // dy == 0.0
	}
	
	override fun toString(): String = "(%,.2f, %,.2f)".format(Locale.US, dx, dy)
}

