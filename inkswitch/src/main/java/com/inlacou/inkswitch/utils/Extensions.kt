package com.inlacou.inkswitch.utils

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.view.get
import androidx.core.widget.ImageViewCompat

internal fun View.onDrawn(continuous: Boolean = false, callback: () -> Unit) {
	viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
		override fun onGlobalLayout() {
			if(!continuous) {
				viewTreeObserver?.removeOnGlobalLayoutListener(this)
			}
			callback.invoke()
		}
	})
}

internal fun View.setMargins(left: Int? = null, top: Int? = null, right: Int? = null, bottom: Int? = null) {
	if (layoutParams is ViewGroup.MarginLayoutParams) {
		val p = layoutParams as ViewGroup.MarginLayoutParams
		p.setMargins(left ?: p.leftMargin, top ?: p.topMargin, right ?: p.rightMargin, bottom ?: p.bottomMargin)
		layoutParams = p
		requestLayout()
	}
}

internal fun View.setBackgroundCompat(drawable: Drawable){
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
		this.background = drawable
	}else{
		this.setBackgroundDrawable(drawable)
	}
}

internal fun View.centerHorizontal() {
	layoutParams?.let { layoutParams ->
		if(layoutParams is RelativeLayout.LayoutParams){
			layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
			layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, 0)
		}
	}
}

internal fun View.centerVertical(){
	layoutParams?.let { layoutParams ->
		if(layoutParams is RelativeLayout.LayoutParams){
			layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, 0)
			layoutParams.addRule(RelativeLayout.CENTER_VERTICAL)
		}
	}
}

internal fun View.alignParentTop() {
	layoutParams?.let { layoutParams ->
		if(layoutParams is RelativeLayout.LayoutParams){
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0)
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0)
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0)
		}
	}
}

internal fun View.alignParentBottom() {
	layoutParams?.let { layoutParams ->
		if(layoutParams is RelativeLayout.LayoutParams){
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0)
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0)
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
		}
	}
}

internal fun View.alignParentLeft() {
	layoutParams?.let { layoutParams ->
		if(layoutParams is RelativeLayout.LayoutParams){
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0)
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0)
		}
	}
}

internal fun View.alignParentRight() {
	layoutParams?.let { layoutParams ->
		if(layoutParams is RelativeLayout.LayoutParams){
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0)
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0)
		}
	}
}

internal fun Context.getColorCompat(resId: Int): Int {
	return resources.getColorCompat(resId)
}

internal fun Resources.getColorCompat(resId: Int): Int {
	return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
		getColor(resId, null)
	}else{
		getColor(resId)
	}
}

internal fun Context.getDrawableCompat(resId: Int): Drawable {
	return resources.getDrawableCompat(resId)
}

internal fun Resources.getDrawableCompat(resId: Int): Drawable {
	return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
		getDrawable(resId, null)
	}else{
		getDrawable(resId)
	}
}

internal fun Int.colorToHex(): String = String.format("#%06X", 0xFFFFFF and this)
internal val View.stringId: String get() = if (id == View.NO_ID) "no-id" else resources.getResourceName(id)

internal val ViewGroup.childViews get() = (0 until childCount).map { get(it) }

internal fun ImageView.tintByResId(colorResId: Int){
	ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(this.context.getColorCompat(colorResId)))
}

internal fun ImageView.tintByColor(color: Int){
	ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(color))
}

fun getItemPositionFromClickOnViewWithMargins(clickX: Float, itemWidth: Float, itemNumber: Int, margin: Float): Int {
	val totalWidth = (itemNumber*itemWidth)+margin*2
	var fixedRelativePosition = clickX-margin //Fix touch
	if(fixedRelativePosition<=(0+margin)) fixedRelativePosition = 0f //if less than minimum, minimum
	if(fixedRelativePosition>=(totalWidth-margin*2)) fixedRelativePosition = totalWidth-(margin*2)-1 //if more than max, max
	return (fixedRelativePosition/itemWidth).toInt()
}