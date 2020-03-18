package com.inlacou.inkswitch.animations.easetypes

import com.inlacou.inkswitch.animations.CubicBezier

/**
 * Created by Weiping on 2016/3/3.
 */
class EaseOutExpo : CubicBezier() {
	init {
		init(0.19, 1.0, 0.22, 1.0)
	}
}