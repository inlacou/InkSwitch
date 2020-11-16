package com.inlacou.inkswitch.utils

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.view.get
import androidx.core.widget.ImageViewCompat
import java.util.*

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

internal fun View.resize(width: Int? = null, height: Int? = null) {
	layoutParams?.let {
		layoutParams = it.apply {
			if(width!=null) this.width = if(width>-1) width else 0
			if(height!=null) this.height = if(height>-1) height else 0
		}
	}
}

internal val now get() = System.currentTimeMillis()

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

internal fun View.center(){
	layoutParams?.let { layoutParams ->
		if(layoutParams is RelativeLayout.LayoutParams){
			layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
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
		if(layoutParams is RelativeLayout.LayoutParams) {
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0)
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0)
		}
	}
}

internal fun View.matchParent() {
	layoutParams?.let { layoutParams ->
		if(layoutParams is RelativeLayout.LayoutParams) {
			layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
			layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
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
internal fun String.hexToDecimal() = Integer.parseInt(this, 16)

internal fun Int.colorToRgb(): Triple<Int, Int, Int> {
	var hex = colorToHex()
	if(hex.contains("#")) hex = hex.replace("#", "")
	if(hex.length>6) hex = hex.substring(2, hex.length)
	return Triple(hex.substring(0, 2).hexToDecimal(), hex.substring(2, 4).hexToDecimal(), hex.substring(4, 6).hexToDecimal())
}

fun Int.decimalToHex(): String {
	var result = toString(16)
	if(result.length%2!=0) result = "0$result"
	return result.toUpperCase(Locale.ROOT)
}

internal fun Triple<Int, Int, Int>.rgbToHex(): String {
	return first.decimalToHex() + second.decimalToHex() + third.decimalToHex()
}

internal fun Triple<Int, Int, Int>.rgbToColor(): Int {
	return rgbToHex().hexToColor()
}

internal fun String.hexToColor(): Int {
	var aux = this
	if(!aux.startsWith("#")) aux = "#$aux"
	return Color.parseColor(aux)
}

internal fun Int.mergeColors(newColor: Int?, newColorPercentage: Float): Int {
	val newPercentage = when {
		newColorPercentage>1f -> 1f
		newColorPercentage<0f -> 0f
		else -> newColorPercentage
	}
	if(newColor==null) return this
	val color1Triple = colorToRgb()
	val color2Triple = newColor.colorToRgb()
	val selfPercentage = 1f-newPercentage
	val mergedRgb = Triple(((color1Triple.first*selfPercentage)+(color2Triple.first*newPercentage)).toInt(),
			((color1Triple.second*selfPercentage)+(color2Triple.second*newPercentage)).toInt(),
			((color1Triple.third*selfPercentage)+(color2Triple.third*newPercentage)).toInt())
	
	return mergedRgb.rgbToColor()
}

internal val View.stringId: String get() = if (id == View.NO_ID) "no-id" else resources.getResourceName(id)

internal val ViewGroup.childViews get() = (0 until childCount).map { get(it) }

internal fun ImageView.tintByResId(colorResId: Int){
	ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(this.context.getColorCompat(colorResId)))
}

internal fun ImageView.tintByColor(color: Int){
	ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(color))
}

internal fun ImageView.getTint() = ImageViewCompat.getImageTintList(this)

internal fun getItemPositionFromClickOnViewWithMargins(clickX: Float, itemWidth: Float, itemNumber: Int, margin: Float): Int {
	val totalWidth = (itemNumber*itemWidth)+margin*2
	var fixedRelativePosition = clickX-margin //Fix touch
	if(fixedRelativePosition<=(0+margin)) fixedRelativePosition = 0f //if less than minimum, minimum
	if(fixedRelativePosition>=(totalWidth-margin*2)) fixedRelativePosition = totalWidth-(margin*2)-1 //if more than max, max
	return (fixedRelativePosition/itemWidth).toInt()
}

internal fun View?.setVisible(visible: Boolean, holdSpaceOnDisappear: Boolean = false, animate: Boolean = false) {
	if (this == null) return
	if(animate){
		if(visible) {
			if(visible && visibility== View.VISIBLE) return
		} else {
			if(!visible && visibility!= View.VISIBLE) return
		}
	} else {
		if(visible){
			this.visibility = View.VISIBLE
		}else{
			if(holdSpaceOnDisappear){
				this.visibility = View.INVISIBLE
			}else{
				this.visibility = View.GONE
			}
		}
	}
}
