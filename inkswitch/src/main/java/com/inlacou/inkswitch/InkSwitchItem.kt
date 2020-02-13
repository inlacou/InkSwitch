package com.inlacou.inkswitch

abstract class InkSwitchItem(val backgroundColorActive: Int, val backgroundColorInactive: Int, val textIconColorActive: Int, val textIconColorInactive: Int, val selected: Boolean){
	
	override fun toString(): String {
		return "selected: $selected " +
				"| backgroundColorActive: $backgroundColorActive " +
				"| backgroundColorInactive: $backgroundColorInactive " +
				"| textIconColorActive: $textIconColorActive " +
				"| textIconColorInactive: $textIconColorInactive"
	}
	
}