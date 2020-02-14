package com.inlacou.inkswitch

import com.inlacou.inkswitch.utils.getItemPositionFromClickOnViewWithMargins
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
	@Test
	fun addition_isCorrect() {
		assertEquals(4, 2 + 2)
	}
	
	@Test fun `position 0`() = assertEquals(0, getItemPositionFromClickOnViewWithMargins(clickX = 0, itemNumber = 3, itemWidth = 80f, margin = 20f))
	@Test fun `position 20`() = assertEquals(0, getItemPositionFromClickOnViewWithMargins(clickX = 20, itemNumber = 3, itemWidth = 80f, margin = 20f))
	@Test fun `position 40`() = assertEquals(0, getItemPositionFromClickOnViewWithMargins(clickX = 40, itemNumber = 3, itemWidth = 80f, margin = 20f))
	@Test fun `position 60`() = assertEquals(0, getItemPositionFromClickOnViewWithMargins(clickX = 60, itemNumber = 3, itemWidth = 80f, margin = 20f))
	@Test fun `position 80`() = assertEquals(0, getItemPositionFromClickOnViewWithMargins(clickX = 80, itemNumber = 3, itemWidth = 80f, margin = 20f))
	@Test fun `position 100`() = assertEquals(1, getItemPositionFromClickOnViewWithMargins(clickX = 100, itemNumber = 3, itemWidth = 80f, margin = 20f))
	@Test fun `position 120`() = assertEquals(1, getItemPositionFromClickOnViewWithMargins(clickX = 120, itemNumber = 3, itemWidth = 80f, margin = 20f))
	@Test fun `position 140`() = assertEquals(1, getItemPositionFromClickOnViewWithMargins(clickX = 140, itemNumber = 3, itemWidth = 80f, margin = 20f))
	@Test fun `position 160`() = assertEquals(1, getItemPositionFromClickOnViewWithMargins(clickX = 160, itemNumber = 3, itemWidth = 80f, margin = 20f))
	@Test fun `position 180`() = assertEquals(2, getItemPositionFromClickOnViewWithMargins(clickX = 180, itemNumber = 3, itemWidth = 80f, margin = 20f))
	@Test fun `position 200`() = assertEquals(2, getItemPositionFromClickOnViewWithMargins(clickX = 200, itemNumber = 3, itemWidth = 80f, margin = 20f))
	@Test fun `position 220`() = assertEquals(2, getItemPositionFromClickOnViewWithMargins(clickX = 220, itemNumber = 3, itemWidth = 80f, margin = 20f))
	@Test fun `position 240`() = assertEquals(2, getItemPositionFromClickOnViewWithMargins(clickX = 240, itemNumber = 3, itemWidth = 80f, margin = 20f))
	@Test fun `position 260`() = assertEquals(2, getItemPositionFromClickOnViewWithMargins(clickX = 260, itemNumber = 3, itemWidth = 80f, margin = 20f))
	@Test fun `position 280`() = assertEquals(2, getItemPositionFromClickOnViewWithMargins(clickX = 280, itemNumber = 3, itemWidth = 80f, margin = 20f))
}
