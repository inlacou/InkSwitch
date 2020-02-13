package com.inlacou.inkswitchlibraryproject

import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.inlacou.inkswitch.InkSwitchItemText
import kotlinx.android.synthetic.main.fragment_first.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {
	
	override fun onCreateView(
			inflater: LayoutInflater, container: ViewGroup?,
			savedInstanceState: Bundle?
	): View? {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_first, container, false)
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		inkswitch?.items = listOf(
				InkSwitchItemText(
						text = "OFF",
						textIconColorActive = resources.getColorCompat(R.color.basic_black),
						textIconColorInactive = resources.getColorCompat(R.color.basic_white),
						backgroundColor = resources.getColorCompat(R.color.basic_black)
				),
				InkSwitchItemText(
						text = "ON",
						textIconColorActive = resources.getColorCompat(R.color.basic_black),
						textIconColorInactive = resources.getColorCompat(R.color.basic_white),
						backgroundColor = resources.getColorCompat(R.color.basic_green_dark)
				),
				InkSwitchItemText(
						text = "WTF",
						textIconColorActive = resources.getColorCompat(R.color.basic_black),
						textIconColorInactive = resources.getColorCompat(R.color.basic_white),
						backgroundColor = resources.getColorCompat(R.color.basic_green)
				))
		
		view.findViewById<Button>(R.id.button_first).setOnClickListener {
			findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
		}
	}
}

internal fun Resources.getColorCompat(resId: Int): Int {
	return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
		getColor(resId, null)
	}else{
		getColor(resId)
	}
}
