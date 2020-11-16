package com.inlacou.inkswitch.data

data class InkSwitchItemIcon(
		override var backgroundColor: Int? = null,
		override var textIconColorActive: Int? = null,
		override var textIconColorInactive: Int? = null,
		override var padding: Int = 0,
		val iconResId: Int
): InkSwitchItem(
		backgroundColor = backgroundColor,
		textIconColorActive = textIconColorActive,
		textIconColorInactive = textIconColorInactive,
		padding = padding
)