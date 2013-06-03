package com.tomhw;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.LinearLayout;

public class SelectKeyboardView extends LinearLayout {
	Button[] btn = new Button[3];
	int selectKeyboard;
	String[] name = { "手寫", "注音", "英文" };

	public SelectKeyboardView(Context context) {
		super(context);
		setFocusable(true);
		setFocusableInTouchMode(true);
		setOrientation(LinearLayout.VERTICAL);
		setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		// TODO Auto-generated constructor stub
		for (int i = 0; i < 3; i++) {
			btn[i] = new Button(context);
			btn[i].setText(name[i]);
			btn[i].setBackgroundColor(Color.GREEN);
			btn[i].setWidth(135);
			btn[i].setHeight(100);
			addView(btn[i]);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		Log.e("touch", e.getAction() + "!!");
		return false;
	}

	void setTarget(int x) {
		if (x == selectKeyboard) {
			return;
		}
		selectKeyboard = x;
		for (int i = 0; i < 3; i++) {
			if (selectKeyboard == i) {
				btn[i].setBackgroundColor(Color.YELLOW);
			} else {
				btn[i].setBackgroundColor(Color.GREEN);
			}
		}
	}

	void nextKeyboard() {
		setTarget((selectKeyboard + 1) % 3);
	}

}
