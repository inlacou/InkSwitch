package com.inlacou.inkswitch.animations.easetypes

import com.inlacou.inkswitch.animations.CubicBezier

/**
 * Created by Weiping on 2016/3/3.
 */
class EaseInOutCubic : CubicBezier() {
	init {
		init(0.645, 0.045, 0.355, 1.0)
	}
}