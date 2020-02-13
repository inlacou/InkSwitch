package com.inlacou.inkswitch

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import io.reactivex.disposables.Disposable
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
	private var displays: LinearLayout? = null
	
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
	
	private val totalWidth: Float get() = (if(isInEditMode) editModeItemNumber else (items?.size ?: 0))*itemWidth+innerMargin*2
	private val totalHeight: Float get() = itemHeight+innerMargin*2
	
	private var currentPosition: Int = 0
	private var fingerDown = false
	val currentItem get() = items?.get(currentPosition)
	
	var generalCornerRadii: List<Float>? = listOf(10000f)
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
	
	private var editModeItemNumber = 2
	
	private fun readAttrs(attrs: AttributeSet) {
		val ta = context.obtainStyledAttributes(attrs, R.styleable.InkSwitch, 0, 0)
		if (ta.hasValue(R.styleable.InkSwitch_itemWidth)) {
			itemWidth = ta.getDimension(R.styleable.InkSwitch_itemWidth, itemWidth)
		}
		if (ta.hasValue(R.styleable.InkSwitch_itemHeight)) {
			itemHeight = ta.getDimension(R.styleable.InkSwitch_itemHeight, itemHeight)
		}
		if (ta.hasValue(R.styleable.InkSwitch_editModeItemNumber)) {
			editModeItemNumber = ta.getInt(R.styleable.InkSwitch_editModeItemNumber, editModeItemNumber)
		}
		setListeners()
		lightUpdate()
	}
	
	init {
		val rootView = View.inflate(context, R.layout.ink_switch, this)
		clickableView = rootView.findViewById(R.id.inkswitch_clickable)
		backgroundView = rootView.findViewById(R.id.inkswitch_background)
		markerView = rootView.findViewById(R.id.inkswitch_marker)
		displays = rootView.findViewById(R.id.inkswitch_displays)
		backgroundView?.let { it.onDrawn(false) { lightUpdate() } }
		clickableView?.centerVertical()
		backgroundView?.centerVertical()
		markerView?.centerVertical()
		markerView?.alignParentLeft()
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
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				viewTreeObserver?.removeOnGlobalLayoutListener(listener)
			}
		}
		viewTreeObserver?.addOnGlobalLayoutListener(listener)
		clickableView?.setOnTouchListener { _, event ->
			if(!isEnabled) return@setOnTouchListener false
			val newPosition = getItemPositionFromClickOnViewWithMargins(clickX = event.x, margin = innerMargin, itemWidth = itemWidth, itemNumber = items?.size ?: 0)
			var changed = false
			if(currentPosition!=newPosition) {
				changed = true
				onValueChangeListener?.invoke(newPosition, true)
			}
			currentPosition = newPosition
			if(changed) updateBackground()
			startUpdate()
			
			when(event.action){
				MotionEvent.ACTION_DOWN -> {
					attemptClaimDrag()
					fingerDown = true
					true
				}
				MotionEvent.ACTION_CANCEL -> false
				MotionEvent.ACTION_UP -> {
					onValueSetListener?.invoke(currentPosition, true)
					fingerDown = false
					false
				}
				MotionEvent.ACTION_MOVE -> true
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
	
	private fun lightUpdate() {
		displays?.childViews?.forEachIndexed { index, view ->
			val item = items?.get(index)
			if(item!=null) {
				if (view is TextView) {
					view.setTextColor(if (index == currentPosition) item.textIconColorActive else item.textIconColorInactive)
				} else if (view is ImageView) {
					view.tintByColor(if (index == currentPosition) item.textIconColorActive else item.textIconColorInactive)
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
		markerView?.setMargins(left = innerMargin.toInt()+(itemWidth*currentPosition).toInt(), right = innerMargin.toInt(), top = innerMargin.toInt(), bottom = innerMargin.toInt())
	}
	
	private fun startUpdate(animate: Boolean = false, durationPrimary: Long = DEFAULT_ANIMATION_DURATION, durationSecondary: Long = DEFAULT_ANIMATION_DURATION, primaryDelay: Long = 0, secondaryDelay: Long = 0) {
		if(false/*animate*/) {
			//tryUpdateAnimated(durationPrimary, durationSecondary, primaryDelay, secondaryDelay)
		}else{
			disposable?.dispose() //We stop the animation in progress if a no-animation-update is requested
			lightUpdate()
		}
	}
	
	fun updateBackground() {
		updateBackground(backgroundView, backgroundGradientOrientation, sanitizeColors(backgroundView, listOf()), generalCornerRadii ?: mutableListOf())
		updateBackground(markerView, markerGradientOrientation, sanitizeColors(markerView, listOf()), markerCornerRadii ?: generalCornerRadii ?: mutableListOf())
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
		const val DEFAULT_ANIMATION_DURATION = 2_000L
	}
	
}