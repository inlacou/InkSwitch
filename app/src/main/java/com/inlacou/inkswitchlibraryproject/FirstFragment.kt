package com.inlacou.inkswitchlibraryproject

import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
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
						padding = 10,
						textIconColorActive = resources.getColorCompat(R.color.basic_black),
						textIconColorInactive = resources.getColorCompat(R.color.basic_white),
						backgroundColor = resources.getColorCompat(R.color.basic_black)
				),
				InkSwitchItemText(
						text = "ON",
						padding = 10,
						textIconColorActive = resources.getColorCompat(R.color.basic_black),
						textIconColorInactive = resources.getColorCompat(R.color.basic_white),
						backgroundColor = resources.getColorCompat(R.color.basic_green_dark)
				),
				InkSwitchItemText(
						text = "WTF",
						padding = 10,
						textIconColorActive = resources.getColorCompat(R.color.basic_black),
						textIconColorInactive = resources.getColorCompat(R.color.basic_white),
						backgroundColor = resources.getColorCompat(R.color.basic_green)
				))
		
		inkswitch.onValueChangeListener = { index, fromUser ->
			Toast.makeText(context!!, index.toString(), Toast.LENGTH_LONG).show()
		}
		
		view.findViewById<Button>(R.id.button_first).setOnClickListener {
			findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
		}
	}
}

