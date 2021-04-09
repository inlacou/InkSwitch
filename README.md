# InkSwitch

Import with `implementation 'com.github.inlacou:InkSwitch:'` [![](https://jitpack.io/v/inlacou/InkSwitch.svg)](https://jitpack.io/#inlacou/InkSwitch)

Animation works only if rxjava2 (rxjava2:rxjava and rxjava2:rxandroid) or rxjava3 (rxjava3:rxjava and rxjava3:rxandroid) libraries are present.

## Customization

### Behaviour

##### JustSwipe
On click will move to selected, but also allows swipe gesture.  
Can't be animated.

##### OnClickMoveToNext
On click (anywhere) will move to next (from left to right).  
Can be animated.

##### OnClickMoveToSelected
On click will move to selected item. If clicked the currently selected, no change.  
Can be animated.

### Appearance

//TODO

## Example

```Kt
  inkswitch?.items = listOf(
      InkSwitchItemText(
          text = "",
          textIconColorActive = resources.getColorCompat(R.color.basic_black),
          textIconColorInactive = resources.getColorCompat(R.color.transparent),
          backgroundColor = resources.getColorCompat(R.color.basic_black),
          textSize = 0f
      ),
      InkSwitchItemText(
          text = "",
          textIconColorActive = resources.getColorCompat(R.color.basic_green),
          textIconColorInactive = resources.getColorCompat(R.color.transparent),
          backgroundColor = resources.getColorCompat(R.color.basic_green),
          textSize = 0f
      ))
  inkswitch?.onClickBehaviour = InkSwitch.OnClickBehaviour.OnClickMoveToNext(animate = true)
```

```xml
  <com.inlacou.inkswitch.InkSwitch
    android:id="@+id/inkswitch"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:itemWidth="30dp"
    app:itemHeight="30dp"
    app:innerMargin="4dp"/>
```

