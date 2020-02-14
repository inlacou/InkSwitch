package com.inlacou.inkswitch.data

import com.inlacou.inkswitch.data.InkSwitchItem

class InkSwitchItemIcon(
		backgroundColor: Int? = null,
		textIconColorActive: Int? = null,
		textIconColorInactive: Int? = null,
		padding: Int? = null,
		val iconResId: Int
): InkSwitchItem(
		backgroundColor = backgroundColor,
		textIconColorActive = textIconColorActive,
		textIconColorInactive = textIconColorInactive,
		padding = padding ?: 0
)