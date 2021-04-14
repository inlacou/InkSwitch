package com.inlacou.inkswitchlibraryproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.inlacou.inkswitch.InkSwitch
import com.inlacou.inkswitch.data.InkSwitchItemText
import kotlinx.android.synthetic.main.fragment_1.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class Fragment1 : Fragment() {
	
	override fun onCreateView(
			inflater: LayoutInflater, container: ViewGroup?,
			savedInstanceState: Bundle?
	): View? {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_1, container, false)
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		inkswitch_example?.onClickBehaviour = InkSwitch.OnClickBehaviour.OnClickMoveToSelected(false)
		inkswitch_example?.items = listOf(
				InkSwitchItemText(
						text = "OFF",
						padding = 10,
						textIconColorActive = resources.getColorCompat(R.color.basic_black),
						textIconColorInactive = resources.getColorCompat(R.color.transparent),
						backgroundColor = resources.getColorCompat(R.color.basic_black),
						textSize = 16f, textStyle = InkSwitchItemText.TextStyle.ITALIC
				),
				InkSwitchItemText(
						text = "ON",
						padding = 10,
						textIconColorActive = resources.getColorCompat(R.color.basic_green),
						textIconColorInactive = resources.getColorCompat(R.color.transparent),
						backgroundColor = resources.getColorCompat(R.color.basic_green),
						textSize = 24f, textStyle = InkSwitchItemText.TextStyle.BOLD
				))
		inkswitch_animate?.items = listOf(
				InkSwitchItemText(
						text = "NO",
						padding = 10,
						textIconColorActive = resources.getColorCompat(R.color.basic_black),
						textIconColorInactive = resources.getColorCompat(R.color.basic_green),
						backgroundColor = resources.getColorCompat(R.color.basic_black),
						textSize = 8f, textStyle = InkSwitchItemText.TextStyle.ITALIC
				),
				InkSwitchItemText(
						text = "YES",
						padding = 10,
						textIconColorActive = resources.getColorCompat(R.color.basic_green),
						textIconColorInactive = resources.getColorCompat(R.color.basic_black),
						backgroundColor = resources.getColorCompat(R.color.basic_green),
						textSize = 12f, textStyle = InkSwitchItemText.TextStyle.BOLD
				))
		inkswitch_animate?.itemWidth = 40f
		inkswitch_animate?.itemHeight = 40f
		inkswitch_animate?.innerMargin = 4f
		inkswitch_animate?.onValueSetListener = { index, fromUser ->
			inkswitch_example?.onClickBehaviour = InkSwitch.OnClickBehaviour.OnClickMoveToSelected(index==1)
		}
		inkswitch_example.onValueChangeListener = { index, fromUser -> Toast.makeText(requireContext(), index.toString(), Toast.LENGTH_LONG).show() }
		
		view.findViewById<Button>(R.id.button_first).setOnClickListener {
			//inkswitch_example?.moveToNext()
			findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
		}
	}
}

