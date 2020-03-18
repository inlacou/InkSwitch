package com.inlacou.inkswitch.animations.easetypes

import com.inlacou.inkswitch.animations.CubicBezier

/**
 * Created by Weiping on 2016/3/3.
 */
class EaseInOutExpo : CubicBezier() {
	init {
		init(1f, 0f, 0f, 1f)
	}
}