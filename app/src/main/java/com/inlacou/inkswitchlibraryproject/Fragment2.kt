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
import com.inlacou.inkswitch.data.InkSwitchItemIcon
import com.inlacou.inkswitch.data.InkSwitchItemText
import kotlinx.android.synthetic.main.fragment_2.*

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class Fragment2 : Fragment() {
	
	override fun onCreateView(
			inflater: LayoutInflater, container: ViewGroup?,
			savedInstanceState: Bundle?
	): View? {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_2, container, false)
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		inkswitch_example?.onClickBehaviour = InkSwitch.OnClickBehaviour.OnClickMoveToNext(false)
		inkswitch_example?.items = listOf(
				InkSwitchItemIcon(iconResId = R.drawable.space_invader),
				InkSwitchItemIcon(iconResId = R.drawable.space_invader_2),
				InkSwitchItemIcon(iconResId = R.drawable.space_invader_3))
		
		inkswitch_example.onValueSetListener = { index, fromUser ->
			Toast.makeText(requireContext(), index.toString(), Toast.LENGTH_LONG).show()
		}
		
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
		inkswitch_animate?.onValueSetListener = { index, fromUser ->
			inkswitch_example?.onClickBehaviour = InkSwitch.OnClickBehaviour.OnClickMoveToNext(index==1)
		}
		
		view.findViewById<Button>(R.id.button_second).setOnClickListener {
			findNavController().navigate(R.id.action_SecondFragment_to_ThirdFragment)
		}
	}
}
