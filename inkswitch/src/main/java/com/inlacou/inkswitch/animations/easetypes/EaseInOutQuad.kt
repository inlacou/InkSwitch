package com.inlacou.inkswitch.animations.easetypes

import com.inlacou.inkswitch.animations.CubicBezier

/**
 * Created by Weiping on 2016/3/3.
 */
class EaseInOutQuad : CubicBezier() {
	init {
		init(0.455, 0.03, 0.515, 0.955)
	}
}