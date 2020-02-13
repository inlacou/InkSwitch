package com.inlacou.inkswitchlibraryproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.inlacou.inkswitch.InkSwitchItemIcon
import com.inlacou.inkswitch.InkSwitchItemText
import kotlinx.android.synthetic.main.fragment_first.*

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {
	
	override fun onCreateView(
			inflater: LayoutInflater, container: ViewGroup?,
			savedInstanceState: Bundle?
	): View? {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_second, container, false)
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		inkswitch?.itemWidth = 200f
		inkswitch?.itemHeight = 200f
		inkswitch?.items = listOf(
				InkSwitchItemIcon(
						iconResId = R.drawable.space_invader,
						padding = 30,
						textIconColorActive = resources.getColorCompat(R.color.basic_black),
						textIconColorInactive = resources.getColorCompat(R.color.basic_white),
						backgroundColor = resources.getColorCompat(R.color.basic_black)
				),
				InkSwitchItemIcon(
						iconResId = R.drawable.space_invader,
						padding = 30,
						textIconColorActive = resources.getColorCompat(R.color.basic_black),
						textIconColorInactive = resources.getColorCompat(R.color.basic_white),
						backgroundColor = resources.getColorCompat(R.color.basic_green_dark)
				),
				InkSwitchItemIcon(
						iconResId = R.drawable.space_invader,
						padding = 30,
						textIconColorActive = resources.getColorCompat(R.color.basic_black),
						textIconColorInactive = resources.getColorCompat(R.color.basic_white),
						backgroundColor = resources.getColorCompat(R.color.basic_green)
				))
		
		inkswitch.onValueSetListener = { index, fromUser ->
			Toast.makeText(context!!, index.toString(), Toast.LENGTH_LONG).show()
		}
		
		view.findViewById<Button>(R.id.button_second).setOnClickListener {
			findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
		}
	}
}
