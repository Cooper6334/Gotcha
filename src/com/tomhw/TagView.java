package com.tomhw;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;

public class TagView extends LinearLayout {
	ImageButton image;
	TextView text;
	String name;
	View line;
	boolean first = false;
	ArrayList<TagEdge> edges = new ArrayList<TagEdge>();

	@SuppressLint("NewApi")
	public TagView(Context context, String n, boolean f) {
		super(context);
		// TODO Auto-generated constructor stub
		first = f;
		if (!first) {
			line = new View(context);
			line.setBackgroundColor(Color.WHITE);
			LinearLayout.LayoutParams layoutParams3 = new LayoutParams(2, 92);
			layoutParams3.setMargins(7, 23, 7, 23);
			this.addView(line, layoutParams3);
		}
		// <View
		// android:id="@+id/view"
		// android:layout_width="fill_parent"
		// android:layout_height="2dp"
		// android:layout_alignParentLeft="true"
		// android:layout_below="@+id/button1"
		// android:layout_marginTop="79px"
		// android:background="#ffffffff" />

		name = n;
		setOrientation(LinearLayout.HORIZONTAL);
		this.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 138));
		LinearLayout.LayoutParams layoutParams = new LayoutParams(60, 138);
		LinearLayout.LayoutParams layoutParams2 = new LayoutParams(
				LayoutParams.WRAP_CONTENT, 138);

		image = new ImageButton(context);
		image.setBackground(getResources().getDrawable(R.drawable.ft3));
		image.setOnClickListener(new ImageButton.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((FindActivity) getContext()).removeTag(TagView.this);
			}
		});

		this.addView(image, layoutParams);
		text = new TextView(context);
		text.setTextSize(24);
		text.setText(name);
		text.setTextColor(Color.WHITE);
		text.setGravity(Gravity.CENTER);
		this.addView(text, layoutParams2);
	}

	void setFirst() {
		if (!first) {
			this.removeView(line);
		}
	}
}
