package com.inlacou.inkswitch

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
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
	
	private fun readAttrs(attrSet: AttributeSet) {
		//TODO
	}
	
	init {
		val rootView = View.inflate(context, R.layout.ink_switch, this)
		clickableView = rootView.findViewById(R.id.inkswitch_clickable)
		backgroundView = rootView.findViewById(R.id.inkswitch_background)
		markerView = rootView.findViewById(R.id.inkswitch_marker)
		backgroundView?.let {
			it.onDrawn(false) {
				update()
			}
		}
		setListeners()
		update()
	}
	
	override fun setEnabled(enabled: Boolean) {
		super.setEnabled(enabled)
	}
	
	var innerMargin: Float = 20f
		set(value) {
			field = value
			startUpdate()
		}
	var itemWidth = 80f
		set(value) {
			field = value
			startUpdate()
		}
	var itemHeight = 80f
		set(value) {
			field = value
			startUpdate()
		}
	val items: List<InkSwitchItem>? = listOf(
			InkSwitchItemText(
					text = "OFF",
					textIconColorActive = context.getColorCompat(R.color.inkswitch_text_default_active),
					textIconColorInactive = context.getColorCompat(R.color.inkswitch_text_default_inactive),
					backgroundColorActive = context.getColorCompat(R.color.inkswitch_background_default_active),
					backgroundColorInactive = context.getColorCompat(R.color.inkswitch_background_default_inactive)
			),InkSwitchItemText(
					text = "ON",
					textIconColorActive = context.getColorCompat(R.color.inkswitch_text_default_active),
					textIconColorInactive = context.getColorCompat(R.color.inkswitch_text_default_inactive),
					backgroundColorActive = context.getColorCompat(R.color.inkswitch_background_default_active),
					backgroundColorInactive = context.getColorCompat(R.color.inkswitch_background_default_inactive)
			),InkSwitchItemText(
					text = "WTF",
					textIconColorActive = context.getColorCompat(R.color.inkswitch_text_default_active),
					textIconColorInactive = context.getColorCompat(R.color.inkswitch_text_default_inactive),
					backgroundColorActive = context.getColorCompat(R.color.inkswitch_background_default_active),
					backgroundColorInactive = context.getColorCompat(R.color.inkswitch_background_default_inactive)
			))
	
	private val totalWidth: Float get() = itemWidth*(items?.size ?: 0)+innerMargin*2
	private val totalHeight: Float get() = itemHeight+innerMargin*2

	private var currentPosition: Int = 0
	private var fingerDown = false
	
	
	@SuppressLint("ClickableViewAccessibility")
	private fun setListeners() {
		listener = ViewTreeObserver.OnGlobalLayoutListener {
			update()
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				viewTreeObserver?.removeOnGlobalLayoutListener(listener)
			}
		}
		viewTreeObserver?.addOnGlobalLayoutListener(listener)
		clickableView?.setOnTouchListener { _, event ->
			if(!isEnabled) return@setOnTouchListener false
			val newPosition = getItemPositionFromClickOnViewWithMargins(clickX = event.x, margin = innerMargin, itemWidth = itemWidth, itemNumber = items?.size ?: 0)
			if(currentPosition!=newPosition) {
				//TODO fire listeners
				//onValueChangeListener?.invoke(primaryProgress, secondaryProgress)
				//onValuePrimaryChangeListener?.invoke(primaryProgress, true)
			}
			currentPosition = newPosition
			startUpdate()
			
			when(event.action){
				MotionEvent.ACTION_DOWN -> {
					attemptClaimDrag()
					fingerDown = true
					true
				}
				MotionEvent.ACTION_CANCEL -> false
				MotionEvent.ACTION_UP -> {
					//TODO fire listeners
					//onValuePrimarySetListener?.invoke(primaryProgress, true)
					fingerDown = false
					false
				}
				MotionEvent.ACTION_MOVE -> true
				else -> false
			}
		}
	}
	
	private fun update() {
		clickableView?.centerVertical()
		backgroundView?.centerVertical()
		markerView?.centerVertical()
		markerView?.alignParentLeft()
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
			update()
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