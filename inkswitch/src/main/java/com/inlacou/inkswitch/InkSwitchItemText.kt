package com.inlacou.inkswitch

class InkSwitchItemText(
		backgroundColorActive: Int,
		backgroundColorInactive: Int,
		textIconColorActive: Int,
		textIconColorInactive: Int,
		text: String
): InkSwitchItem(
		backgroundColorActive = backgroundColorActive,
		backgroundColorInactive = backgroundColorInactive,
		textIconColorActive = textIconColorActive,
		textIconColorInactive = textIconColorInactive,
		selected = false
)