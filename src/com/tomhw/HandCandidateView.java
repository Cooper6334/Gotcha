/*
 * Copyright (C) 2008-2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tomhw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class HandCandidateView extends View {

	private MainActivity mService;
	private List<String> mSuggestions;
	// -1 null -2 open 0~ word
	private int selectWord;

	private static final List<String> EMPTY_LIST = new ArrayList<String>();

	private Paint mPaint;

	boolean flagOpen = false;
	int yoffset = 10;

	// Handler h = new Handler() {
	// @Override
	// public void handleMessage(Message m) {
	// setSuggestions(null, true, true);
	// }
	// };

	/**
	 * Construct a CandidateView for showing suggested words for completion.
	 * 
	 * @param context
	 * @param attrs
	 */
	public HandCandidateView(Context context) {
		super(context);

		mPaint = new Paint();
		mPaint.setTextSize(72);

		setHorizontalFadingEdgeEnabled(true);
		setWillNotDraw(false);
		setHorizontalScrollBarEnabled(false);
		setVerticalScrollBarEnabled(false);

		setSuggestions(null, true, true);
		// mService.openHandCandidate(flagOpen);
	}

	/**
	 * A connection back to the service to communicate with the text field
	 * 
	 * @param listener
	 */
	public void setService(MainActivity listener) {
		mService = listener;
	}

	/**
	 * If the canvas is null, then only touch calculations are performed to pick
	 * the target candidate.
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		if (canvas != null) {
			super.onDraw(canvas);

			canvas.drawColor(Color.GREEN);
			if (mSuggestions == null)
				return;

			final int count = mSuggestions.size();

			if (!flagOpen) {
				int x = 0;
				int y = 146;
				mPaint.setColor(Color.RED);
				canvas.drawText("開", 30, 544, mPaint);
				canvas.drawText("刪", 176, 544, mPaint);

				int c = 6;
				if (count < 6) {
					c = count;
				}
				canvas.drawLine(0, 146, 292, 146, mPaint);
				canvas.drawLine(0, 292, 292, 292, mPaint);
				canvas.drawLine(0, 438, 292, 438, mPaint);
				canvas.drawLine(146, 0, 146, 584, mPaint);
				for (int i = 0; i < c; i++) {

					String suggestion = mSuggestions.get(i);

					if (i == selectWord) {
						mPaint.setColor(Color.RED);
					} else {
						mPaint.setColor(Color.BLACK);
					}
					canvas.drawText(suggestion, x + 30, y - 40, mPaint);

					if (i % 3 == 2) {
						x = 146;
						y = 146;
					} else {
						y += 146;
					}
				}

				// mTotalWidth = x;
				// if (mTargetScrollX != getScrollX()) {
				// scrollToTarget();
				// }
			} else {
				int x = 204;
				int y = 146;
				mPaint.setColor(Color.RED);

				// canvas.drawLine(204, 0, 204, 584, mPaint);
				for (int i = 0; i < (this.getWidth() - 204) / 146; i++) {
					canvas.drawLine(204 + i * 146, 0, 204 + i * 146, 5846,
							mPaint);
				}

				canvas.drawLine(204, 146, getWidth(), 146, mPaint);
				canvas.drawLine(204, 292, getWidth(), 292, mPaint);
				canvas.drawLine(204, 438, getWidth(), 438, mPaint);

				canvas.drawText("關", 30, 324, mPaint);
				canvas.drawText("刪", this.getWidth() - 146 + 30, 544, mPaint);

				int c = 23;
				if (count < 23) {
					c = count;
				}
				for (int i = 0; i < c; i++) {
					String suggestion = mSuggestions.get(i);

					if (i == selectWord) {
						mPaint.setColor(Color.RED);
					} else {
						mPaint.setColor(Color.BLACK);
					}
					canvas.drawText(suggestion, x + 30, y - 30, mPaint);

					if (i % 4 == 3) {
						x += 146;
						y = 146;
					} else {
						y += 146;
					}
				}
			}
		}
	}

	public void setSuggestions(List<String> suggestions, boolean completions,
			boolean typedWordValid) {
		clear();
		if (suggestions != null) {
			mSuggestions = new ArrayList<String>(suggestions);
		}
		scrollTo(0, 0);
		// Compute the total width
		invalidate();
		requestLayout();
	}

	public void clear() {
		mSuggestions = EMPTY_LIST;
		selectWord = -1;
		invalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent me) {

		int action = me.getAction();
		int x = (int) me.getX();
		int y = (int) me.getY();
		if (flagOpen) {
			selectWord = 4 * ((x - 204) / 146) + (y / 146);
			if (x < 204) {
				selectWord = -2;
			}
			if (y / 146 >= 3 && getWidth() - x < 146) {
				selectWord = -3;
			}

		} else {
			selectWord = 3 * (x / 146) + (y / 146);
			if (y / 146 >= 3) {
				if (x < 146) {
					selectWord = -2;
				} else {
					selectWord = -3;
				}
			}
		}

		switch (action) {
		case MotionEvent.ACTION_DOWN:

			break;
		case MotionEvent.ACTION_MOVE:

			break;
		case MotionEvent.ACTION_UP:

			if (selectWord == -2) {

				flagOpen = !flagOpen;
				mService.openHandCandidate(flagOpen, mSuggestions.size() + 1);

			} else if (selectWord == -3) {
				// Toast.makeText(getContext(), "-3",
				// Toast.LENGTH_SHORT).show();
			} else if (selectWord >= 0 && selectWord < mSuggestions.size()) {
				mService.pickSuggestionManually(selectWord);
				if (flagOpen) {
					flagOpen = false;
					mService.openHandCandidate(flagOpen, 0);
				}
			}
			selectWord = -1;
			break;
		}
		invalidate();
		return true;
	}

}
