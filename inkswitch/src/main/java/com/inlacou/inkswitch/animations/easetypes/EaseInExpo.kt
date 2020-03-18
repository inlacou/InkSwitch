package com.inlacou.inkswitch.animations.easetypes

import com.inlacou.inkswitch.animations.CubicBezier

/**
 * Created by Weiping on 2016/3/3.
 */
class EaseInExpo : CubicBezier() {
	init {
		init(0.95, 0.05, 0.795, 0.035)
	}
}