package com.inlacou.inkswitch.animations.easetypes

import com.inlacou.inkswitch.animations.CubicBezier

/**
 * Created by Weiping on 2016/3/3.
 */
class EaseInElastic : CubicBezier() {
	override fun getOffset(t: Float): Float {
		var t = t
		val b = 0f
		val c = 1f
		val d = 1f
		if (t == 0f) return b
		if (d.let { t /= it; t } == 1f) return b + c
		val p = d * .3f
		val s = p / 4
		return -(c * Math.pow(2.0, 10 * 1.let { t -= it; t }.toDouble()).toFloat() * Math.sin((t * d - s) * (2 * Math.PI.toFloat()) / p.toDouble()).toFloat()) + b
	}
}