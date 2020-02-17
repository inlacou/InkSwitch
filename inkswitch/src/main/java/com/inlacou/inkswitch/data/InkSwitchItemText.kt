package com.inlacou.inkswitch.data

import android.graphics.Typeface

class InkSwitchItemText(
		backgroundColor: Int? = null,
		textIconColorActive: Int? = null,
		textIconColorInactive: Int? = null,
		padding: Int? = null,
		val text: String,
		val textStyle: TextStyle = TextStyle.NORMAL,
		val textSize: Float? = null
): InkSwitchItem(
		backgroundColor = backgroundColor,
		textIconColorActive = textIconColorActive,
		textIconColorInactive = textIconColorInactive,
		padding = padding ?: 0
){
	enum class TextStyle(val value: Int) {
		BOLD(Typeface.BOLD), BOLD_ITALIC(Typeface.BOLD_ITALIC), ITALIC(Typeface.ITALIC), NORMAL(Typeface.NORMAL)
	}
}
//TODO other text attributes