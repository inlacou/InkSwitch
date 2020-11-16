package com.inlacou.inkswitch.data

import android.graphics.Typeface

data class InkSwitchItemText(
		override var backgroundColor: Int? = null,
		override var textIconColorActive: Int? = null,
		override var textIconColorInactive: Int? = null,
		override var padding: Int = 0,
		val text: String,
		val textStyle: TextStyle = TextStyle.NORMAL,
		val textSize: Float? = null
): InkSwitchItem(
		backgroundColor = backgroundColor,
		textIconColorActive = textIconColorActive,
		textIconColorInactive = textIconColorInactive,
		padding = padding
){
	enum class TextStyle(val value: Int) {
		BOLD(Typeface.BOLD), BOLD_ITALIC(Typeface.BOLD_ITALIC), ITALIC(Typeface.ITALIC), NORMAL(Typeface.NORMAL)
	}
}