package com.inlacou.inkswitch

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.inlacou.inkswitch.animations.Interpolable
import com.inlacou.inkswitch.animations.easetypes.EaseType
import com.inlacou.inkswitch.data.InkSwitchItem
import com.inlacou.inkswitch.data.InkSwitchItemIcon
import com.inlacou.inkswitch.data.InkSwitchItemText
import com.inlacou.inkswitch.exceptions.ItemNotFoundException
import com.inlacou.inkswitch.utils.*
import com.inlacou.inkswitch.utils.onDrawn
import com.inlacou.inkswitch.utils.setBackgroundCompat
import com.inlacou.inkswitch.utils.setMargins
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

class InkSwitch: FrameLayout {
	constructor(context: Context) : super(context)
	constructor(context: Context, attrSet: AttributeSet) : super(context, attrSet) { readAttrs(attrSet) }
	constructor(context: Context, attrSet: AttributeSet, arg: Int) : super(context, attrSet, arg) { readAttrs(attrSet) }
	
	private lateinit var listener: ViewTreeObserver.OnGlobalLayoutListener
	private var disposable: Disposable? = null
	private var clickableView: View? = null
	private var backgroundView: View? = null
	private var markerView: View? = null
	private var markerItemTextView: TextView? = null
	private var markerItemIconView: ImageView? = null
	private var displays: LinearLayout? = null
	private var touchDownTimestamp = 0L
	
	/**
	 * Color array, not color resource array
	 */
	val baseBackgroundColors: MutableList<Int> = mutableListOf(context.getColorCompat(R.color.inkswitch_background_default))
	/**
	 * Color array, not color resource array
	 */
	val baseMarkerColors: MutableList<Int> = mutableListOf(context.getColorCompat(R.color.inkswitch_marker_default))
	/**
	 * Color, not color resource
	 */
	var baseTextIconColorActive: Int = context.getColorCompat(R.color.inkswitch_text_default_active)
	/**
	 * Color, not color resource
	 */
	var baseTextIconColorInactive: Int = context.getColorCompat(R.color.inkswitch_text_default_inactive)
	
	var onClickBehaviour: OnClickBehaviour = OnClickBehaviour.OnClickMoveToNext(animate = false)
	var clickThreshold = DEFAULT_CLICK_THRESHOLD
	var animationDuration = DEFAULT_ANIMATION_DURATION
	/**
	 * Variable used to block new input when animating
	 * //TODO allow input some configurable time before animation end. Or in other words, make this soft and not hard
	 */
	private var animating = false
	
	var innerMargin: Float = 15f
		set(value) {
			field = value
			startUpdate()
		}
	var itemWidth = 100f
		set(value) {
			field = value
			startUpdate()
		}
	var itemHeight = 100f
		set(value) {
			field = value
			startUpdate()
		}
	var items: List<InkSwitchItem>? = null
		set(value) {
			field = value
			heavyUpdate()
		}
	var markerItem: List<InkSwitchItem>? = null
		set(value) {
			field = value
			heavyUpdate()
		}
	
	private val totalWidth: Float get() = (if(isInEditMode) editModeItemNumber else (items?.size ?: 0))*itemWidth+innerMargin*2
	private val totalHeight: Float get() = itemHeight+innerMargin*2
	
	private var previousPosition: Int = 0
	private var currentPosition: Int = 0
		set(value) {
			if(field!=value) {
				previousPosition = field
				field = value
			}
		}
	private var fingerDown = false
	val currentItem get() = items?.get(currentPosition)
	
	var generalCornerRadii: List<Float>? = listOf(10000f)
	var backgroundCornerRadii: List<Float>? = null
		get() { return field ?: generalCornerRadii }
	var markerCornerRadii: List<Float>? = null
		get() { return field ?: generalCornerRadii }
	var backgroundGradientOrientation: GradientDrawable.Orientation = GradientDrawable.Orientation.TOP_BOTTOM
	var markerGradientOrientation: GradientDrawable.Orientation = GradientDrawable.Orientation.TOP_BOTTOM
	
	/**
	 * Fired on any value change by user touch movement or set programmatically
	 */
	var onValueChangeListener: ((primary: Int, fromUser: Boolean) -> Unit)? = null
	/**
	 * Fired when user releases touch or when progress is set programmatically
	 */
	var onValueSetListener: ((primary: Int, fromUser: Boolean) -> Unit)? = null
	/**
	 * You can create your own Interpolable or use one of my own.
	 * My own (for example, there is more):
	 * EaseType.EaseOutBounce.newInstance()
	 * or
	 * EaseType.EaseOutCubic.newInstance()
	 */
	var easeType: Interpolable = DEFAULT_EASE_TYPE
	
	private var progressVisual = 0f
	private val progress get() = itemWidth*currentPosition
	private val previousProgress get() = itemWidth*previousPosition
	
	private var editModeItemNumber = 2
	
	private fun readAttrs(attrs: AttributeSet) {
		val ta = context.obtainStyledAttributes(attrs, R.styleable.InkSwitch, 0, 0)
		try {
			if (ta.hasValue(R.styleable.InkSwitch_itemWidth)) {
				itemWidth = ta.getDimension(R.styleable.InkSwitch_itemWidth, itemWidth)
			}
			if (ta.hasValue(R.styleable.InkSwitch_itemHeight)) {
				itemHeight = ta.getDimension(R.styleable.InkSwitch_itemHeight, itemHeight)
			}
			if (ta.hasValue(R.styleable.InkSwitch_editModeItemNumber)) {
				editModeItemNumber = ta.getInt(R.styleable.InkSwitch_editModeItemNumber, editModeItemNumber)
			}
			if (ta.hasValue(R.styleable.InkSwitch_innerMargin)) {
				innerMargin = ta.getDimension(R.styleable.InkSwitch_innerMargin, innerMargin)
			}
			if (ta.hasValue(R.styleable.InkSwitch_corners)) {
				val aux = ta.getDimension(R.styleable.InkSwitch_corners, -10f)
				if(aux!=-10f) {
					generalCornerRadii = listOf(aux)
				}
			}
			if (ta.hasValue(R.styleable.InkSwitch_backgroundCorners)) {
				val aux = ta.getDimension(R.styleable.InkSwitch_backgroundCorners, -10f)
				if(aux!=-10f) {
					backgroundCornerRadii = listOf(aux)
				}
			}
			if (ta.hasValue(R.styleable.InkSwitch_markerCorners)) {
				val aux = ta.getDimension(R.styleable.InkSwitch_markerCorners, -10f)
				if(aux!=-10f) {
					markerCornerRadii = listOf(aux)
				}
			}
			if (ta.hasValue(R.styleable.InkSwitch_backgroundGradientOrientation)) {
				ta.getInt(R.styleable.InkSwitch_backgroundGradientOrientation, -1).let {
					backgroundGradientOrientation = if(it==-1) backgroundGradientOrientation
					else GradientDrawable.Orientation.values()[it]
				}
			}
			if (ta.hasValue(R.styleable.InkSwitch_markerGradientOrientation)) {
				ta.getInt(R.styleable.InkSwitch_markerGradientOrientation, -1).let {
					markerGradientOrientation = if(it==-1) markerGradientOrientation
					else GradientDrawable.Orientation.values()[it]
				}
			}
			if (ta.hasValue(R.styleable.InkSwitch_backgroundColor)) {
				val aux = ta.getColor(R.styleable.InkSwitch_backgroundColor, -1)
				if(aux!=-1) {
					baseBackgroundColors.apply { clear(); add(aux) }
				}
			}
			if (ta.hasValue(R.styleable.InkSwitch_backgroundColors)) {
				ta.resources.getIntArray(ta.getResourceId(R.styleable.InkSwitch_backgroundColors, -1)).toList().let {
					if(it.isNotEmpty()) {
						baseMarkerColors.clear()
						it.forEach { baseMarkerColors.add(it) }
					}
				}
			}
			if (ta.hasValue(R.styleable.InkSwitch_markerColor)) {
				val aux = ta.getColor(R.styleable.InkSwitch_markerColor, -1)
				if(aux!=-1) {
					baseMarkerColors.apply { clear(); add(aux) }
				}
			}
			if (ta.hasValue(R.styleable.InkSwitch_markerColors)) {
				ta.resources.getIntArray(ta.getResourceId(R.styleable.InkSwitch_markerColors, -1)).toList().let {
					if(it.isNotEmpty()) {
						baseMarkerColors.clear()
						it.forEach { baseMarkerColors.add(it) }
					}
				}
			}
			if (ta.hasValue(R.styleable.InkSwitch_textIconColorActive)) {
				val aux = ta.getColor(R.styleable.InkSwitch_textIconColorActive, -1)
				if(aux!=-1) {
					baseTextIconColorActive = aux
				}
			}
			if (ta.hasValue(R.styleable.InkSwitch_textIconColorInactive)) {
				val aux = ta.getColor(R.styleable.InkSwitch_textIconColorInactive, -1)
				if(aux!=-1) {
					baseTextIconColorInactive = aux
				}
			}
		}finally {
			ta.recycle()
		}
		setListeners()
		lightUpdate()
	}
	
	init {
		val rootView = View.inflate(context, R.layout.ink_switch, this)
		clickableView = rootView.findViewById(R.id.inkswitch_clickable)
		backgroundView = rootView.findViewById(R.id.inkswitch_background)
		markerView = rootView.findViewById(R.id.inkswitch_marker)
		markerItemTextView = rootView.findViewById(R.id.inkswitch_marker_item_text)
		markerItemIconView = rootView.findViewById(R.id.inkswitch_marker_item_icon)
		displays = rootView.findViewById(R.id.inkswitch_displays)
		backgroundView?.let { it.onDrawn(false) { lightUpdate() } }
		clickableView?.centerVertical()
		backgroundView?.centerVertical()
		markerView?.centerVertical()
		markerView?.alignParentLeft()
		markerItemTextView?.matchParent()
		markerItemIconView?.matchParent()
	}
	
	override fun onAttachedToWindow() {
		super.onAttachedToWindow()
		startUpdate()
		updateBackground()
	}
	
	fun setItemByIndex(index: Int, fromUser: Boolean) {
		val changed = index!=currentPosition
		currentPosition = index
		if(changed) {
			onValueSetListener?.invoke(index, fromUser)
			onValueChangeListener?.invoke(index, fromUser)
			updateBackground()
			startUpdate()
		}
	}
	
	fun setItem(item: InkSwitchItem, fromUser: Boolean) {
		val index = items?.indexOf(item) ?: throw ItemNotFoundException()
		val changed = index!=currentPosition
		currentPosition = index
		if(changed) {
			onValueSetListener?.invoke(index, fromUser)
			onValueChangeListener?.invoke(index, fromUser)
			updateBackground()
			startUpdate()
		}
	}
	
	@SuppressLint("ClickableViewAccessibility")
	private fun setListeners() {
		listener = ViewTreeObserver.OnGlobalLayoutListener {
			lightUpdate()
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) { viewTreeObserver?.removeOnGlobalLayoutListener(listener) }
		}
		viewTreeObserver?.addOnGlobalLayoutListener(listener)
		clickableView?.setOnTouchListener { _, event ->
			if(!isEnabled || animating) return@setOnTouchListener false
			if(event.action==MotionEvent.ACTION_DOWN) touchDownTimestamp = now
			var changed = false
			var click = false
			if ((onClickBehaviour !is OnClickBehaviour.JustSwipe) && abs(touchDownTimestamp-System.currentTimeMillis())<clickThreshold) {
				click = true //this variable will make us call startUpdate(with animation) on touch release
			}else{
				val newPosition = getItemPositionFromClickOnViewWithMargins(clickX = event.x, margin = innerMargin, itemWidth = itemWidth, itemNumber = items?.size ?: 0)
				if (currentPosition!=newPosition) {
					changed = true
					onValueChangeListener?.invoke(newPosition, true)
				}
				currentPosition = newPosition
				if (changed) updateBackground()
				startUpdate()
				
				//TODO on ACTION_DOWN no change should be made, I think. Only on ACTION_UP, ACTION_MOVE, or ACTION_DOWN after some time if none of the previous ones have fired
				
			}
			when(event.action) {
				MotionEvent.ACTION_DOWN -> {
					attemptClaimDrag()
					fingerDown = true
					true
				}
				MotionEvent.ACTION_CANCEL -> { false }
				MotionEvent.ACTION_UP -> {
					if(click) {
						val newPosition = when {
							onClickBehaviour is OnClickBehaviour.OnClickMoveToSelected -> getItemPositionFromClickOnViewWithMargins(clickX = event.x, margin = innerMargin, itemWidth = itemWidth, itemNumber = items?.size ?: 0)
							currentPosition<(items?.size ?: 0)-1 -> currentPosition+1
							else -> 0
						}
						if(newPosition!=currentPosition){
							currentPosition = newPosition
							startUpdate(animate = onClickBehaviour.animate, duration = animationDuration)
						}
					}
					if(changed) onValueSetListener?.invoke(currentPosition, true)
					fingerDown = false
					false
				}
				MotionEvent.ACTION_MOVE -> { true }
				else -> false
			}
		}
	}
	
	private fun heavyUpdate() {
		displays?.removeAllViews()
		items?.mapNotNull {
			when (it) {
				is InkSwitchItemText -> TextView(context).apply { text = it.text; gravity = Gravity.CENTER }
				is InkSwitchItemIcon -> ImageView(context).apply { setImageDrawable(context.getDrawableCompat(it.iconResId)) }
				else -> null
			}
		}?.forEach {
			it.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
			it.layoutParams?.width = itemWidth.toInt()
			it.layoutParams?.height = itemHeight.toInt()
			displays?.addView(it)
		}
	}
	
	private fun lightUpdate(animate: Boolean = false) {
		displays?.childViews?.forEachIndexed { index, view ->
			val item = items?.get(index)
			if(item!=null) {
				val color = (item.textIconColorInactive ?: baseTextIconColorInactive)
				if (view is TextView) {
					view.setTextColor(color)
				} else if (view is ImageView) {
					view.tintByColor(color)
				}
				view.setVisible(index!=currentPosition, holdSpaceOnDisappear = true)
				if (index==currentPosition) {
					markerItemTextView?.setTextColor(item.textIconColorActive  ?: baseTextIconColorActive)
					markerItemIconView?.tintByColor(item.textIconColorActive  ?: baseTextIconColorActive)
					if(item is InkSwitchItemIcon) {
						markerItemIconView?.setImageDrawable(context.getDrawableCompat(item.iconResId))
					} else if(item is InkSwitchItemText) {
						markerItemTextView?.text = item.text
						item.textSize?.let { markerItemTextView?.textSize = it }
						markerItemTextView?.let { it.setTypeface(it.typeface, item.textStyle.value) }
					}
					view.setPadding(item.padding, item.padding, item.padding, item.padding)
				}
				view.setPadding(item.padding, item.padding, item.padding, item.padding)
			}
		}
		displays?.setPadding(innerMargin.toInt(), innerMargin.toInt(), innerMargin.toInt(), innerMargin.toInt())
		displays?.layoutParams?.width  = totalWidth.roundToInt()
		displays?.layoutParams?.height = totalHeight.roundToInt()
		markerView?.layoutParams?.width = itemWidth.roundToInt()
		markerView?.layoutParams?.height = itemHeight.roundToInt()
		clickableView?.layoutParams?.width  = totalWidth.roundToInt()
		clickableView?.layoutParams?.height = max(itemHeight.roundToInt(), height)
		backgroundView?.layoutParams?.width  = totalWidth.roundToInt()
		backgroundView?.layoutParams?.height = totalHeight.roundToInt()
		markerItemTextView?.layoutParams?.width = itemWidth.roundToInt()
		markerItemTextView?.layoutParams?.height = itemHeight.roundToInt()
		markerItemIconView?.layoutParams?.width = itemWidth.roundToInt()
		markerItemIconView?.layoutParams?.height = itemHeight.roundToInt()
		println("lightUpdate: $progressVisual")
		markerView?.setMargins(left = innerMargin.toInt()+(if(animate) progressVisual else progress).toInt(), right = innerMargin.toInt(), top = innerMargin.toInt(), bottom = innerMargin.toInt())
	}
	
	private fun startUpdate(animate: Boolean = false, duration: Long = DEFAULT_ANIMATION_DURATION, delay: Long = 0) {
		if(animate) {
			println("updateAnimated")
			tryUpdateAnimated(duration, delay)
		}else{
			println("updateAnimated - nope")
			disposable?.dispose() //We stop the animation in progress if a no-animation-update is requested
			lightUpdate(animate = false)
		}
	}
	
	private fun makeUpdate() {
		progressVisual = progress
		updateBackground()
		lightUpdate(true)
	}
	
	private fun tryUpdateAnimated(duration: Long, delay: Long) {
		try {
			disposable?.dispose()
			disposable = Observable.interval(10L, TimeUnit.MILLISECONDS)
					.doOnDispose {
						animating = false
					}.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).map { it * 10L }.subscribe({
						if(animating==false) updateBackground()
						animating = true
						if(it in delay .. (delay+duration)) {
							progressVisual = previousProgress + (progress-previousProgress) * easeType.getOffset(((it-delay)/10L).toFloat() / (duration / 10L))
							println("on disposable progressVisual: $progressVisual")
						}else { disposable?.dispose() }
						lightUpdate(true)
					}, {
						animating = false
					},{},{
						animating = false
					})
		}catch (e: NoClassDefFoundError) {
			Log.w("InkSwitch", "update animation failed, RX library not found. Fault back to non-animated updated")
			makeUpdate()
		}
	}
	
	fun updateBackground() {
		updateBackground(backgroundView, backgroundGradientOrientation, sanitizeColors(backgroundView, listOf()), backgroundCornerRadii ?: mutableListOf())
		updateBackground(markerView, markerGradientOrientation, sanitizeColors(markerView, listOf()), markerCornerRadii ?: mutableListOf())
	}
	
	/**
	 * Method to sanitize a colors list array to have the correct number of items.
	 * @param colors Array to sanitize.
	 * @param view View to get the current color from if possible.
	 * @return colors list of 2 or more items. Always. So we can build a GrandientDrawable.
	 */
	private fun sanitizeColors(view: View?, colors: List<Int>): List<Int> {
		println("sanitizeColors: ${view?.stringId} | currentItem ($currentPosition)")
		return when {
			colors.size>1  -> colors //We have 2 or more colors, we can create a GrandientDrawable
			colors.size==1 ->
				colors.toMutableList().apply { //Add one more color so we have two
					add(colors.first())
				}
			else -> {
				mutableListOf<Int>().apply {
					view?.background.let { back ->
						//Try to get color from background
						if (back != null && back is ColorDrawable && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
							add(back.color)
							add(back.color)
						} else {
							//Else get default colors
							when (view?.id) {
								R.id.inkswitch_background -> currentItem?.backgroundColor ?: context.resources.getColorCompat(R.color.inkswitch_background_default)
								R.id.inkswitch_clickable -> context.resources.getColorCompat(R.color.inkswitch_transparent)
								R.id.inkswitch_marker -> context.resources.getColorCompat(R.color.inkswitch_marker_default)
								else -> context.resources.getColorCompat(R.color.inkseekbar_default_default)
							}.let {
								add(it)
								add(it)
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Changes the background of the view to a GradientDrawable with the given params.
	 * @param colorList colors for the GradientDrawable (always, minimum of 2 items)
	 * @param orientation GradientDrawable.Orientation
	 * @param customCornerRadii GradientDrawable corner radii. 1, 4 or 8 items, else ignored.
	 * @param view View to apply the background to
	 */
	private fun updateBackground(view: View?, orientation: GradientDrawable.Orientation, colorList: List<Int>, customCornerRadii: List<Float>) {
		view?.apply {
			this.setBackgroundCompat(GradientDrawable(orientation, colorList.toIntArray()).apply {
				this.cornerRadii = when (customCornerRadii.size) {
					1 -> floatArrayOf(
							customCornerRadii[0], customCornerRadii[0], customCornerRadii[0], customCornerRadii[0],
							customCornerRadii[0], customCornerRadii[0], customCornerRadii[0], customCornerRadii[0])
					4 -> floatArrayOf(
							customCornerRadii[0], customCornerRadii[0], customCornerRadii[1], customCornerRadii[1],
							customCornerRadii[2], customCornerRadii[2], customCornerRadii[3], customCornerRadii[3])
					8 -> floatArrayOf(
							customCornerRadii[0], customCornerRadii[1], customCornerRadii[2], customCornerRadii[3],
							customCornerRadii[4], customCornerRadii[5], customCornerRadii[6], customCornerRadii[7])
					else -> floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
				}
			})
			requestLayout()
		}
	}
	
	/**
	 * Tries to claim the user's drag motion, and requests disallowing any
	 * ancestors from stealing events in the drag.
	 */
	private fun attemptClaimDrag() {
		parent?.requestDisallowInterceptTouchEvent(true)
	}
	
	companion object {
		const val DEFAULT_ANIMATION_DURATION = 1_500L
		const val DEFAULT_CLICK_THRESHOLD = 150L
		val DEFAULT_EASE_TYPE = EaseType.EaseOutExpo.newInstance()
	}
	
	sealed class OnClickBehaviour(val animate: Boolean) {
		class JustSwipe: OnClickBehaviour(false)
		class OnClickMoveToNext(animate: Boolean = false): OnClickBehaviour(animate)
		class OnClickMoveToSelected(animate: Boolean = false): OnClickBehaviour(animate)
	}
	
}