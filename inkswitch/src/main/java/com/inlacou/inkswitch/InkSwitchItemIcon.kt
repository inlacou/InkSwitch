package com.inlacou.inkswitch

class InkSwitchItemIcon(
		backgroundColor: Int,
		textIconColorActive: Int,
		textIconColorInactive: Int,
		padding: Int,
		val iconResId: Int
): InkSwitchItem(
		backgroundColor = backgroundColor,
		textIconColorActive = textIconColorActive,
		textIconColorInactive = textIconColorInactive,
		padding = padding,
		selected = false
)