package com.inlacou.inkswitch.animations.easetypes

import com.inlacou.inkswitch.animations.CubicBezier

/**
 * Created by Weiping on 2016/3/3.
 */
class EaseInCirc : CubicBezier() {
	init {
		init(0.6, 0.04, 0.98, 0.335)
	}
}