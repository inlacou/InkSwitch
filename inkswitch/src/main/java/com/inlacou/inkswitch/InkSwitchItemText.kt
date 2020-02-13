package com.inlacou.inkswitch

class InkSwitchItemText(
		backgroundColor: Int? = null,
		textIconColorActive: Int? = null,
		textIconColorInactive: Int? = null,
		padding: Int? = null,
		val text: String
): InkSwitchItem(
		backgroundColor = backgroundColor,
		textIconColorActive = textIconColorActive,
		textIconColorInactive = textIconColorInactive,
		padding = padding ?: 0
)
//TODO other text attributes