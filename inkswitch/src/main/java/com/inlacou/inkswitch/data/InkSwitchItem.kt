package com.inlacou.inkswitch.data

abstract class InkSwitchItem(
		open val backgroundColor: Int?,
		open val textIconColorActive: Int?,
		open val textIconColorInactive: Int?,
		open val padding: Int) {
	
	override fun toString(): String {
		return "padding: $padding " +
				"| backgroundColor: $backgroundColor " +
				"| textIconColorActive: $textIconColorActive " +
				"| textIconColorInactive: $textIconColorInactive"
	}
	
	//TODO add gradient support for background and marker
	
}