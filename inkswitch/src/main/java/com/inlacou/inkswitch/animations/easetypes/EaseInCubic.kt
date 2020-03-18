package com.inlacou.inkswitch.animations.easetypes

import com.inlacou.inkswitch.animations.CubicBezier

/**
 * Created by Weiping on 2016/3/3.
 */
class EaseInCubic : CubicBezier() {
	init {
		init(0.55, 0.055, 0.675, 0.19)
	}
}