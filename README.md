# SimpleRangeBar-Android

Two thumbed Seekbar-Like custom view for android.

![alt text](https://i.imgur.com/ruSgJyt.png)

# How to include in xml
    <com.example.roman.thesimplerangebar.SimpleRangeBar
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:id="@+id/range_bar"
        app:roundCorners="true"
        app:minValue="0"
        app:maxValue="200"
        app:thickness="@dimen/activity_rangebar_thickness"
        app:padding="@dimen/activity_vertical_margin"
        app:radiusThumb="@dimen/activity_rangebar_radius"
        app:colorRangeBar="@color/colorPrimaryDark"
        app:colorRange="@color/colorLightBlue"
        app:colorThumb="@color/colorAccent"
        app:colorThumbPressed="@color/colorAccentDark"/>
       
# Basic usage in activity/fragment

        simpleRangeBar = (SimpleRangeBar) findViewById(R.id.range_bar);

        simpleRangeBar.setOnSimpleRangeBarChangeListener(new SimpleRangeBarOnChangeListener() {
            @Override
            public void leftThumbValueChanged(final long min) {
                // do stuff
            }

            @Override
            public void rightThumbValueChanged(final long max) {
                // do stuff
            }
        });
        
        // careful with the set method, as the left thumb cannot be set higher than the right thumb
        simpleRangeBar.setRanges(0, 100);
        simpleRangeBar.setThumbValues(20, 80);
        
For more methods check out the interfaces SimpleRangeFunctionality and SimpleRangeVisuals

# How to include in your project

Add in your gradle files:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

	dependencies {
	        compile 'com.github.recklessracoon:SimpleRangeBar-Android:0.1.1'
	}
        
