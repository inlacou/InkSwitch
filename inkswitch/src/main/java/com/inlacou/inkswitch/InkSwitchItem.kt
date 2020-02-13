package com.inlacou.inkswitch

abstract class InkSwitchItem(
		val backgroundColor: Int,
		val textIconColorActive: Int,
		val textIconColorInactive: Int,
		val selected: Boolean,
		val padding: Int = 0){
	
	override fun toString(): String {
		return "selected: $selected " +
				"| padding: $padding " +
				"| backgroundColor: $backgroundColor " +
				"| textIconColorActive: $textIconColorActive " +
				"| textIconColorInactive: $textIconColorInactive"
	}
	
}