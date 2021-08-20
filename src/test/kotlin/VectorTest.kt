@file:Suppress("MemberVisibilityCanBePrivate")

import org.junit.jupiter.api.*

import org.junit.jupiter.api.Assertions.*

internal class VectorTest {
	val oben = Vector(0, 1)
	val rechtsOben = Vector(1, 1)
	val rechts = Vector(1, 0)
	val rechtsUnten = Vector(1, -1)
	val unten = Vector(0, -1)
	val linksUnten = Vector(-1, -1)
	val links = Vector(-1, 0)
	val linksOben = Vector(-1, 1)
	
	@Test
	fun isRightOf() {
		assertTrue(oben.isRightOf(linksOben))
		assertTrue(oben.isRightOf(links))
		assertTrue(oben.isRightOf(linksUnten))
		
		assertTrue(unten.isRightOf(rechtsOben))
		assertTrue(unten.isRightOf(rechts))
		assertTrue(unten.isRightOf(rechtsUnten))
		
		assertTrue(links.isRightOf(linksUnten))
		assertTrue(links.isRightOf(unten))
		assertTrue(links.isRightOf(rechtsUnten))
		
		assertTrue(rechts.isRightOf(linksOben))
		assertTrue(rechts.isRightOf(oben))
		assertTrue(rechts.isRightOf(rechtsOben))
		
		assertFalse(unten.isRightOf(linksOben))
		assertFalse(unten.isRightOf(links))
		assertFalse(unten.isRightOf(linksUnten))
		assertFalse(unten.isRightOf(unten * 2))
		
		assertFalse(oben.isRightOf(rechtsOben))
		assertFalse(oben.isRightOf(rechts))
		assertFalse(oben.isRightOf(rechtsUnten))
		assertFalse(oben.isRightOf(oben * 2))
		
		assertFalse(rechts.isRightOf(linksUnten))
		assertFalse(rechts.isRightOf(unten))
		assertFalse(rechts.isRightOf(rechtsUnten))
		assertFalse(rechts.isRightOf(rechts * 2))
		
		assertFalse(links.isRightOf(linksOben))
		assertFalse(links.isRightOf(oben))
		assertFalse(links.isRightOf(rechtsOben))
		assertFalse(links.isRightOf(links * 2))
	}
}
