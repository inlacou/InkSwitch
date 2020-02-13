package com.inlacou.inkswitch

class InkSwitchItemText(
		backgroundColor: Int,
		textIconColorActive: Int,
		textIconColorInactive: Int,
		padding: Int,
		val text: String
): InkSwitchItem(
		backgroundColor = backgroundColor,
		textIconColorActive = textIconColorActive,
		textIconColorInactive = textIconColorInactive,
		padding = padding,
		selected = false
)