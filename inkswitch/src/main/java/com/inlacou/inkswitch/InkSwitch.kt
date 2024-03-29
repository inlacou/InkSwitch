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
import android.widget.*
import com.inlacou.inkanimationtypes.Interpolable
import com.inlacou.inkanimationtypes.easetypes.EaseType
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
import timber.log.Timber
import java.lang.Exception
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

class InkSwitch: FrameLayout {
	constructor(context: Context) : super(context) { initialize() }
	constructor(context: Context, attrSet: AttributeSet) : super(context, attrSet) { readAttrs(attrSet); initialize() }
	constructor(context: Context, attrSet: AttributeSet, arg: Int) : super(context, attrSet, arg) { readAttrs(attrSet); initialize() }
	
	private var initialTouchPosition: Float? = null
	private lateinit var listener: ViewTreeObserver.OnGlobalLayoutListener
	private var disposable2: Disposable? = null
	private var disposable3: io.reactivex.rxjava3.disposables.Disposable? = null
	private var clickableView: View? = null
	private var backgroundView: View? = null
	private var markerView: View? = null
	private var markerItemTextView: TextView? = null
	private var markerItemIconView: ImageView? = null
	private var displays: LinearLayout? = null
	private var touchDownTimestamp = 0L
	
	//TODO add a way to change marker background color
	
	/**
	 * Color array, not color resource array
	 */
	private val baseBackgroundColors: MutableList<Int> = mutableListOf(context.getColorCompat(R.color.inkswitch_background_default))
	/**
	 * Color array, not color resource array
	 */
	private val baseMarkerColors: MutableList<Int> = mutableListOf(context.getColorCompat(R.color.inkswitch_marker_default))
	/**
	 * Color, not color resource
	 */
	private var baseTextIconColorActive: Int = context.getColorCompat(R.color.inkswitch_text_default_active)
	/**
	 * Color, not color resource
	 */
	private var baseTextIconColorInactive: Int = context.getColorCompat(R.color.inkswitch_text_default_inactive)
	
	var onClickBehaviour: OnClickBehaviour = OnClickBehaviour.OnClickMoveToNext(animate = false)
	var clickThreshold = DEFAULT_CLICK_THRESHOLD
	var animationDuration = DEFAULT_ANIMATION_DURATION
	/**
	 * Variable used to block new input when animating
	 */
	val isAnimating get() = animationPercentage<animationPercentageRequired
	/**
	 * Percentage of the current animation required to be able to start a new animation
	 */
	var animationPercentageRequired = .2f
	var animationPercentage = 1f
		private set
	
	var innerMargin: Float = 15f
		set(value) {
			field = value
			heavyUpdate()
		}
	var itemWidth = 100f
		set(value) {
			field = value
			heavyUpdate()
		}
	var itemHeight = 100f
		set(value) {
			field = value
			heavyUpdate()
		}
	var items: List<InkSwitchItem>? = null
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
	val currentItem get() = items?.get(currentPosition)
	val previousItem get() = items?.get(previousPosition)
	
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
			if (ta.hasValue(R.styleable.InkSwitch_inkSwitchBackgroundGradientOrientation)) {
				ta.getInt(R.styleable.InkSwitch_inkSwitchBackgroundGradientOrientation, -1).let {
					backgroundGradientOrientation = if(it==-1) backgroundGradientOrientation
					else GradientDrawable.Orientation.values()[it]
				}
			}
			if (ta.hasValue(R.styleable.InkSwitch_inkSwitchMarkerGradientOrientation)) {
				ta.getInt(R.styleable.InkSwitch_inkSwitchMarkerGradientOrientation, -1).let {
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
	}
	
	private fun initialize() {
		backgroundView?.let { it.onDrawn(false) { lightUpdate(from = "onDrawn") } }
		clickableView?.centerVertical()
		backgroundView?.centerVertical()
		markerView?.centerVertical()
		markerView?.alignParentLeft()
		markerItemTextView?.matchParent()
		markerItemIconView?.matchParent()
		if(isInEditMode) {
			items = listOf(
					InkSwitchItemText(
							text = "OFF",
							padding = 5,
							textIconColorActive = resources.getColorCompat(R.color.inkswitch_background_default),
							textIconColorInactive = resources.getColorCompat(R.color.inkswitch_transparent),
							backgroundColor = resources.getColorCompat(R.color.inkswitch_background_default),
							textSize = 16f, textStyle = InkSwitchItemText.TextStyle.ITALIC
					),
					InkSwitchItemText(
							text = "ON",
							padding = 5,
							textIconColorActive = resources.getColorCompat(R.color.inkswitch_active_default),
							textIconColorInactive = resources.getColorCompat(R.color.inkswitch_transparent),
							backgroundColor = resources.getColorCompat(R.color.inkswitch_active_default),
							textSize = 24f, textStyle = InkSwitchItemText.TextStyle.BOLD
					))
		}
	}
	
	override fun onAttachedToWindow() {
		super.onAttachedToWindow()
		updateBackground(false)
		startUpdate()
	}
	
	fun setItemByIndex(index: Int, fromUser: Boolean) {
		val changed = index!=currentPosition
		currentPosition = index
		if(changed) {
			onValueSetListener?.invoke(index, fromUser)
			onValueChangeListener?.invoke(index, fromUser)
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
			startUpdate()
		}
	}
	
	var clickDisposable: Disposable? = null
	
	@SuppressLint("ClickableViewAccessibility")
	private fun setListeners() {
		listener = ViewTreeObserver.OnGlobalLayoutListener {
			lightUpdate(from = "OnGlobalLayoutListener")
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) { viewTreeObserver?.removeOnGlobalLayoutListener(listener) }
		}
		viewTreeObserver?.addOnGlobalLayoutListener(listener)
		clickableView?.setOnTouchListener { _, event ->
			if(!isEnabled || isAnimating) return@setOnTouchListener false
			if(event.action==MotionEvent.ACTION_DOWN) touchDownTimestamp = now
			var click = false
			if ((onClickBehaviour !is OnClickBehaviour.JustSwipe) && abs(touchDownTimestamp-System.currentTimeMillis())<clickThreshold) {
				click = true //this variable will make us call startUpdate(with animation) on touch release
			}
			when(event.action) {
				MotionEvent.ACTION_DOWN -> {
					log("InkSwitch | ACTION_DOWN")
					attemptClaimDrag()
					initialTouchPosition = event.x
					true
				}
				MotionEvent.ACTION_MOVE -> {
					log("InkSwitch | ACTION_MOVE")
					clickDisposable?.dispose()
					initialTouchPosition.let { if(it==null || abs(it-event.x)>30) actAsIfMoved(event.x) }
					true
				}
				MotionEvent.ACTION_UP -> {
					log("InkSwitch | ACTION_UP")
					clickDisposable?.dispose()
					if(click) {
						val newPosition = when {
							onClickBehaviour is OnClickBehaviour.OnClickMoveToSelected -> getItemPositionFromClickOnViewWithMargins(clickX = event.x, margin = innerMargin, itemWidth = itemWidth, itemNumber = items?.size ?: 0)
							currentPosition<(items?.size ?: 0)-1 -> currentPosition+1
							else -> 0
						}
						if(newPosition!=currentPosition){
							currentPosition = newPosition
							onValueSetListener?.invoke(currentPosition, true)
							onValueChangeListener?.invoke(currentPosition, true)
							startUpdate(animate = onClickBehaviour.animate, duration = animationDuration)
						}
					}else{
						log("InkSwitch | ACTION_UP | ${event.x}")
						actAsIfMoved(event.x)
					}
					false
				}
				else -> false
			}
		}
	}
	
	fun moveToNext() {
		val newPosition = when {
			currentPosition<(items?.size ?: 0)-1 -> currentPosition+1
			else -> 0
		}
		if(newPosition!=currentPosition){
			currentPosition = newPosition
			onValueSetListener?.invoke(currentPosition, true)
			startUpdate(animate = onClickBehaviour.animate, duration = animationDuration)
		}
	}
	
	private fun actAsIfMoved(x: Float) {
		val newPosition = getItemPositionFromClickOnViewWithMargins(clickX = x, margin = innerMargin, itemWidth = itemWidth, itemNumber = items?.size ?: 0)
		if (currentPosition!=newPosition) {
			onValueChangeListener?.invoke(newPosition, true)
		}
		currentPosition = newPosition
		startUpdate()
	}
	
	private fun heavyUpdate() {
		displays?.removeAllViews()
		items?.mapNotNull {
			when (it) {
				is InkSwitchItemText -> TextView(context).apply {
					text = it.text
					gravity = Gravity.CENTER
					layoutParams = RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
						addRule(RelativeLayout.CENTER_HORIZONTAL)
						addRule(RelativeLayout.CENTER_VERTICAL)
						width = itemWidth.toInt()
						height = itemHeight.toInt()
					}
				}
				is InkSwitchItemIcon -> ImageView(context).apply {
					setImageDrawable(context.getDrawableCompat(it.iconResId))
					resize(it.iconWidth?.toInt(), it.iconHeight?.toInt())
					layoutParams = RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
						addRule(RelativeLayout.CENTER_HORIZONTAL)
						addRule(RelativeLayout.CENTER_VERTICAL)
						width = it.iconWidth?.toInt() ?: itemWidth.toInt()
						height = it.iconHeight?.toInt() ?: itemHeight.toInt()
					}
				}
				else -> null
			}
		}?.map {
			RelativeLayout(context).apply {
				layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
					width = itemWidth.toInt()
					height = itemHeight.toInt()
				}
				addView(it)
			}
		}?.forEach {
			displays?.addView(it)
		}
		displays?.layoutParams?.width  = totalWidth.roundToInt()
		displays?.layoutParams?.height = totalHeight.roundToInt()
		displays?.layoutParams?.let {
			if(it is RelativeLayout.LayoutParams) {
				it.addRule(RelativeLayout.CENTER_HORIZONTAL)
				it.addRule(RelativeLayout.CENTER_VERTICAL)
			}
		}
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
		updateBackground(false)
	}
	
	private fun transitionUpdate() {
		log("updates | transitionUpdate")
		displays?.childViews?.forEachIndexed { index, view ->
			val item = items?.get(index)
			if(item!=null) {
				val transparent = context.getColorCompat(R.color.inkswitch_transparent)
				val newColor = if(index==currentPosition) (item.textIconColorActive ?: baseTextIconColorActive) else (item.textIconColorInactive ?: baseTextIconColorInactive)
				if (view is TextView && view.textColors.defaultColor!=newColor) {
					view.setTextColor(transparent)
				} else if (view is ImageView && view.getTint()?.defaultColor!=newColor) {
					log("solidColor: ${view.getTint()?.defaultColor}")
					view.tintByColor(transparent)
				}
			}
		}
	}
	
	private fun lightUpdate(animate: Boolean = false, from: String = "") {
		log("updates | lightUpdate | from: $from")
		displays?.setPadding(innerMargin.toInt(), innerMargin.toInt(), innerMargin.toInt(), innerMargin.toInt())
		updateDisplays(animate)
		updateDisplayColors(animate)
		updatePosition(animate)
	}
	
	private fun updateDisplays(animate: Boolean) {
		displays?.childViews?.map { it as RelativeLayout }?.mapNotNull { it.childViews.firstOrNull() }?.forEachIndexed { index, view ->
			val item = items?.get(index)
			if(item!=null) {
				view.setVisible(index!=currentPosition, holdSpaceOnDisappear = true)
				if (index==currentPosition) {
					if(item is InkSwitchItemIcon) {
						log("currentItem icon: $item")
						markerItemIconView?.setImageDrawable(context.getDrawableCompat(item.iconResId))
						markerItemTextView.setVisible(false, holdSpaceOnDisappear = false)
						markerItemIconView.setVisible(true, holdSpaceOnDisappear = true)
						markerItemIconView?.resize(item.iconWidth?.toInt(), item.iconHeight?.toInt())
					} else if(item is InkSwitchItemText) {
						log("currentItem text: $item")
						markerItemTextView?.text = item.text
						markerItemTextView.setVisible(true, holdSpaceOnDisappear = true)
						markerItemIconView.setVisible(false, holdSpaceOnDisappear = false)
						item.textSize?.let { markerItemTextView?.textSize = it }
						markerItemTextView?.let { it.setTypeface(it.typeface, item.textStyle.value) }
					}
				}
				view.setPadding(item.padding, item.padding, item.padding, item.padding)
			}
		}
	}
	
	private fun updateDisplayColors(animate: Boolean) {
		displays?.childViews?.map { it as RelativeLayout }?.mapNotNull { it.childViews.firstOrNull() }?.forEachIndexed { index, view ->
			items?.get(index)?.let { item -> //Get item associated with index
				view.setVisible(index!=currentPosition, holdSpaceOnDisappear = true)
				if (index==currentPosition) {
					//Not doing anything previousItem?.backgroundColor?.mergeColors(currentItem?.backgroundColor, if(animate) animationPercentage else 1f)
					markerItemTextView?.setTextColor(item.textIconColorActive  ?: baseTextIconColorActive)
					markerItemIconView?.tintByColor(item.textIconColorActive  ?: baseTextIconColorActive)
				}else{
					if (view is TextView) {
						view.setTextColor(item.textIconColorInactive ?: baseTextIconColorInactive)
					} else if (view is ImageView) {
						view.tintByColor(item.textIconColorInactive ?: baseTextIconColorInactive)
					}
				}
			}
		}
	}
	
	private fun updatePosition(animate: Boolean) {
		markerView?.setMargins(left = innerMargin.toInt()+(if(animate) progressVisual else progress).toInt(), right = innerMargin.toInt(), top = innerMargin.toInt(), bottom = innerMargin.toInt())
	}
	
	private fun startUpdate(animate: Boolean = false, duration: Long = DEFAULT_ANIMATION_DURATION, delay: Long = 0) {
		if(animate) {
			log("InkSwitch | updateAnimated - yep")
			transitionUpdate()
			tryUpdateAnimated(duration, delay)
		}else{
			log("InkSwitch | updateAnimated - nope")
			try{ disposable2?.dispose() } catch (e: Exception) {} //We stop the animation in progress if a no-animation-update is requested
			try{ disposable3?.dispose() } catch (e: Exception) {} //We stop the animation in progress if a no-animation-update is requested
			updateBackground(animate = false)
			lightUpdate(animate = false, from = "startUpdate")
		}
	}
	
	private fun makeUpdate() {
		progressVisual = progress
		updateBackground(true)
		lightUpdate(true, from = "makeUpdate")
	}
	
	private fun tryUpdateAnimated(duration: Long, delay: Long) {
		log("InkSwitch | tryUpdateAnimated | animationPercentage: $animationPercentage/$animationPercentageRequired")
		var rxjava2 = true
		var rxjava3 = true
		try {
			disposable2?.dispose()
			disposable2 = Observable.interval(0,10L, TimeUnit.MILLISECONDS)
					.doOnDispose {
						animationPercentage = 1f
					}.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).map { it * 10L }.subscribe({
						animationPercentage = it/(delay+duration).toFloat()
						log("InkSwitch | animationPercentage: $animationPercentage/$animationPercentageRequired")
						updateBackground(true)
						if(it==0L) {
							updateDisplays(true)
							updateDisplayColors(true)
						}
						if(it in delay .. (delay+duration)) {
							progressVisual = previousProgress + (progress-previousProgress) * easeType.getOffset(((it-delay)/10L).toFloat() / (duration / 10L))
							log("InkSwitch | on disposable progressVisual: $progressVisual")
						}else { disposable2?.dispose() }
						updatePosition(true)
					}, {
						animationPercentage = 1f
					},{},{
						animationPercentage = 1f
					})
		}catch (e: NoClassDefFoundError) {
			rxjava2 = false
		}
		try {
			disposable3?.dispose()
			disposable3 = io.reactivex.rxjava3.core.Observable.interval(0,10L, TimeUnit.MILLISECONDS)
					.doOnDispose {
						animationPercentage = 1f
					}.subscribeOn(io.reactivex.rxjava3.schedulers.Schedulers.newThread()).observeOn(io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread()).map { it * 10L }.subscribe({
						animationPercentage = it/(delay+duration).toFloat()
						log("InkSwitch | animationPercentage: $animationPercentage/$animationPercentageRequired")
						updateBackground(true)
						if(it==0L) {
							updateDisplays(true)
							updateDisplayColors(true)
						}
						if(it in delay .. (delay+duration)) {
							progressVisual = previousProgress + (progress-previousProgress) * easeType.getOffset(((it-delay)/10L).toFloat() / (duration / 10L))
							log("InkSwitch | on disposable progressVisual: $progressVisual")
						}else { disposable3?.dispose() }
						updatePosition(true)
					}, {
						animationPercentage = 1f
					},{
						animationPercentage = 1f
					})
		}catch (e: NoClassDefFoundError) {
			rxjava3 = false
		}
		if(!rxjava2 && !rxjava3) {
			Log.w("InkSwitch", "update animation failed, rxJava2 nor rxJava3 libraries found. Fault back to non-animated update")
			makeUpdate()
		}
	}
	
	fun updateBackground(animate: Boolean) {
		updateBackground(backgroundView, backgroundGradientOrientation, getBackgroundColor(backgroundView, animate), backgroundCornerRadii ?: mutableListOf())
		updateBackground(markerView, markerGradientOrientation, getBackgroundColor(markerView, animate), markerCornerRadii ?: mutableListOf())
	}
	
	/**
	 * Method to sanitize a colors list array to have the correct number of items.
	 * @param view View to get the current color from if possible.
	 * @return colors list of 2 or more items. Always. So we can build a GrandientDrawable.
	 */
	private fun getBackgroundColor(view: View?, animate: Boolean): List<Int> {
		log("InkSwitch | getBackgroundColor | target view: ${view?.stringId} | currentItem: $currentPosition | animationPercentage: $animationPercentage")
		return mutableListOf<Int>().apply {
					view?.background.let { back ->
						//Try to get color from background
						if (back != null && back is ColorDrawable) {
							add(back.color)
							add(back.color)
						} else {
							//Else get default colors
							when (view?.id) {
								R.id.inkswitch_background ->
									previousItem?.backgroundColor?.mergeColors(currentItem?.backgroundColor, if(animate && onClickBehaviour.animateBackgroundColorChange) animationPercentage else 1f)
											?: context.resources.getColorCompat(R.color.inkswitch_background_default)
								R.id.inkswitch_clickable -> context.resources.getColorCompat(R.color.inkswitch_transparent)
								R.id.inkswitch_marker -> context.resources.getColorCompat(R.color.inkswitch_marker_default)
								else -> context.resources.getColorCompat(R.color.inkseekbar_default_default)
							}.let {
								log("InkSwitch | getBackgroundColor | color: $it")
								add(it)
								add(it)
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
	
	sealed class OnClickBehaviour(val animate: Boolean, val animateBackgroundColorChange: Boolean) {
		/**
		 * On click will move to selected, but also allows swipe gesture.
		 * Not animable.
		 */
		class JustSwipe: OnClickBehaviour(false, false)
		/**
		 * On click (anywhere) will move to next (from left to right).
		 * Animable.
		 */
		class OnClickMoveToNext(animate: Boolean = false, animateBackgroundColorChange: Boolean = false): OnClickBehaviour(animate, animateBackgroundColorChange)
		/**
		 * On click will move to selected item. If clicked the currently selected, no change.
		 * Animable.
		 */
		class OnClickMoveToSelected(animate: Boolean = false, animateBackgroundColorChange: Boolean = false): OnClickBehaviour(animate, animateBackgroundColorChange)
	}
	
	private fun log(s: String) {
		if(InkSwitchConfig.log) try {
			Timber.d(s)
		}catch (e: Exception){
			Log.d("InkSwitch", s)
		}
	}

}