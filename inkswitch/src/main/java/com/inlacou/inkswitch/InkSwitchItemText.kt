package com.inlacou.inkswitch

class InkSwitchItemText(
		backgroundColor: Int,
		textIconColorActive: Int,
		textIconColorInactive: Int,
		val text: String
): InkSwitchItem(
		backgroundColor = backgroundColor,
		textIconColorActive = textIconColorActive,
		textIconColorInactive = textIconColorInactive,
		selected = false
)