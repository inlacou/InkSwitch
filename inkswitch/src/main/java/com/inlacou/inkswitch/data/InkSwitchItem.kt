package com.inlacou.inkswitch.data

abstract class InkSwitchItem(
		val backgroundColor: Int?,
		val textIconColorActive: Int?,
		val textIconColorInactive: Int?,
		val padding: Int) {
	
	override fun toString(): String {
		return "padding: $padding " +
				"| backgroundColor: $backgroundColor " +
				"| textIconColorActive: $textIconColorActive " +
				"| textIconColorInactive: $textIconColorInactive"
	}
	
	//TODO add gradient support for background and marker
	
}