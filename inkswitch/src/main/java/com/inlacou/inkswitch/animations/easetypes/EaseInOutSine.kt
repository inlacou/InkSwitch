package com.inlacou.inkswitch.animations.easetypes

import com.inlacou.inkswitch.animations.CubicBezier

/**
 * Created by Weiping on 2016/3/3.
 */
class EaseInOutSine : CubicBezier() {
	init {
		init(0.445, 0.05, 0.55, 0.95)
	}
}