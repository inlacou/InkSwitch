package com.inlacou.inkswitch

class InkSwitchItemIcon(
		backgroundColor: Int,
		textIconColorActive: Int,
		textIconColorInactive: Int,
		val iconResId: Int
): InkSwitchItem(
		backgroundColor = backgroundColor,
		textIconColorActive = textIconColorActive,
		textIconColorInactive = textIconColorInactive,
		selected = false
)