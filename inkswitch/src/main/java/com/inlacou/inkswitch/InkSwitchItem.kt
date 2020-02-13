package com.inlacou.inkswitch

abstract class InkSwitchItem(
		val backgroundColor: Int,
		val textIconColorActive: Int,
		val textIconColorInactive: Int,
		val selected: Boolean){
	
	override fun toString(): String {
		return "selected: $selected " +
				"| backgroundColor: $backgroundColor " +
				"| textIconColorActive: $textIconColorActive " +
				"| textIconColorInactive: $textIconColorInactive"
	}
	
}