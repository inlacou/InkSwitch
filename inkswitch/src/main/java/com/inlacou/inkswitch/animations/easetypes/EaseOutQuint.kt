package com.inlacou.inkswitch.animations.easetypes

import com.inlacou.inkswitch.animations.CubicBezier

/**
 * Created by Weiping on 2016/3/3.
 */
class EaseOutQuint : CubicBezier() {
	init {
		init(0.23, 1.0, 0.32, 1.0)
	}
}