package com.inlacou.inkswitch.animations.easetypes

import com.inlacou.inkswitch.animations.CubicBezier

/**
 * Created by Weiping on 2016/3/3.
 */
class EaseInOutQuint : CubicBezier() {
	init {
		init(0.86, 0.0, 0.07, 1.0)
	}
}