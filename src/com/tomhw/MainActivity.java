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

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.method.MetaKeyKeyListener;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.SurfaceHolder.Callback;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Example of writing an input method for a soft keyboard. This code is focused
 * on simplicity over completeness, so it should in no way be considered to be a
 * complete soft keyboard implementation. Its purpose is to provide a basic
 * example for how you would get started writing an input method, to be fleshed
 * out as appropriate.
 */
@SuppressLint("NewApi")
public class MainActivity extends InputMethodService implements
		KeyboardView.OnKeyboardActionListener {
	static String testString = "";
	static final boolean DEBUG = false;
	boolean flagWriting = false;
	int str = 0;
	String writeWord = "";
	static final boolean PROCESS_HARD_KEYS = true;

	// 建立手写输入对象
	long recognizer = 0;
	long character = 0;
	long result = 0;
	int modelState = 0; // 显示model文件载入状态
	int strokes = 0; // 总笔画数
	int handwriteCount = 0; // 笔画数
	Path mPath = null;

	private InputMethodManager mInputMethodManager;

	private View mInputView;
	WriteView writeView;

	CandidateView mCandidateView;
	List<String> stringList;

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message m) {
			switch (m.what) {
			case 0:
				str--;
				if (str <= 0) {
					str = 0;
					// getCurrentInputConnection().commitText(writeWord,
					// writeWord.length());
					getCurrentInputConnection().setComposingText(writeWord,
							writeWord.length());
					characterClear(character);
					strokes = 0;
					mPath.reset();// 触摸结束即清除轨迹
					handwriteCount = 0;
					writeView.clear();
				}
				break;
			case 1:
				str = 0;

				characterClear(character);
				strokes = 0;
				mPath.reset();// 触摸结束即清除轨迹
				handwriteCount = 0;
				setCandidatesViewShown(false);
				writeWord = "";
				writeView.clear();

				break;
			}
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
		mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

		Log.e("IME", "onCreate");
	}

	@Override
	public View onCreateCandidatesView() {
		mCandidateView = new CandidateView(this);
		mCandidateView.setService(this);
		return mCandidateView;
	}

	@Override
	public View onCreateInputView() {
		Log.e("IME", "create input view");

		// mInputView = (LatinKeyboardView) getLayoutInflater().inflate(
		// R.layout.input, null);
		// mInputView.setOnKeyboardActionListener(this);
		// mInputView.setKeyboard(mQwertyKeyboard);
		mInputView = (View) getLayoutInflater().inflate(R.layout.test, null);
		((Button) mInputView.findViewById(R.id.button1))
				.setOnClickListener(new Button.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent i = new Intent(MainActivity.this,
								FindActivity.class);
						i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(i);

					}
				});
		writeView = new WriteView(this);
		writeView.setId(123);
		((LinearLayout) mInputView.findViewById(R.id.linearlayout))
				.addView(writeView);

		return mInputView;
	}

	@Override
	public void onStartInput(EditorInfo attribute, boolean restarting) {
		super.onStartInput(attribute, restarting);
		Log.e("IME", "onStart");
		if (!testString.equals("")) {
			getCurrentInputConnection().commitText(testString,
					testString.length());
			testString = "";
		}
		handler.sendEmptyMessage(1);
	}

	@Override
	public void onFinishInput() {
		super.onFinishInput();
		Log.e("IME", "onFinish");
	}

	public void pickSuggestionManually(int index) {
		// Toast.makeText(this, "" + index, Toast.LENGTH_LONG).show();
		getCurrentInputConnection().commitText(stringList.get(index),
				stringList.get(index).length());
		writeWord = "";
		setCandidatesViewShown(false);

	}

	@Override
	public void onKey(int primaryCode, int[] keyCodes) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPress(int primaryCode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRelease(int primaryCode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onText(CharSequence text) {
		// TODO Auto-generated method stub

	}

	@Override
	public void swipeDown() {
		// TODO Auto-generated method stub

	}

	@Override
	public void swipeLeft() {
		// TODO Auto-generated method stub

	}

	@Override
	public void swipeRight() {
		// TODO Auto-generated method stub

	}

	@Override
	public void swipeUp() {
		// TODO Auto-generated method stub

	}

	// 能显示出手写轨迹的view
	public class WriteView extends SurfaceView {

		SurfaceHolder mSurfaceHolder = null;

		Paint mPaint = null;
		Paint mTextPaint = null; // 文字画笔
		public static final int FRAME = 60;// 画布更新帧数
		boolean mIsRunning = false; // 控制是否更新
		float posX, posY; // 触摸点当前座标

		public WriteView(Context context) {
			super(context);

			// 设置拥有焦点
			this.setFocusable(true);
			// 设置触摸时拥有焦点
			this.setFocusableInTouchMode(true);
			// 获取holder
			mSurfaceHolder = this.getHolder();
			// 添加holder到callback函数之中
			// mSurfaceHolder.addCallback(this);

			// 创建画笔
			mPaint = new Paint();
			mPaint.setColor(Color.BLUE);// 颜色
			mPaint.setAntiAlias(true);// 抗锯齿
			// Paint.Style.STROKE 、Paint.Style.FILL、Paint.Style.FILL_AND_STROKE
			// 意思分别为 空心 、实心、实心与空心
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeCap(Paint.Cap.ROUND);// 设置画笔为圆滑状
			mPaint.setStrokeWidth(5);// 设置线的宽度

			// 创建路径轨迹
			mPath = new Path();

			// 创建文字画笔
			mTextPaint = new Paint();
			mTextPaint.setColor(Color.BLACK);
			mTextPaint.setTextSize(48);

			// 创建手写识别
			if (character == 0) {
				character = characterNew();
				characterClear(character);
				characterSetWidth(character, 300);
				characterSetHeight(character, 300);
			}
			if (recognizer == 0) {
				recognizer = recognizerNew();
			}

			// 打开成功返回1
			// modelState = recognizerOpen(recognizer,
			// "/data/data/handwriting-zh_CN.model");
			String s = Environment.getExternalStorageDirectory()
					+ "/download/handwriting-zh_TW2.model";
			Log.e("IME", "讀model");
			modelState = recognizerOpen(recognizer, s);
			if (modelState != 1) {
				System.out.println("model文件打开失败");
				return;
			}
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {

			// 获取触摸动作以及座标
			int action = event.getAction();
			float x = event.getX();
			float y = event.getY();

			// 按触摸动作分发执行内容
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				flagWriting = true;
				mPath.moveTo(x, y);// 设定轨迹的起始点

				if (str == 0 && !writeWord.equals("")) {
					getCurrentInputConnection().commitText(writeWord,
							writeWord.length());
					writeWord = "";
				}
				str++;
				break;

			case MotionEvent.ACTION_MOVE:
				mPath.quadTo(posX, posY, x, y); // 随触摸移动设置轨迹
				characterAdd(character, handwriteCount, (int) x / 2,
						(int) y / 2);
				break;

			case MotionEvent.ACTION_UP:
				handwriteCount++;

				strokes = (int) characterStrokesSize(character);
				// 进行文字检索
				if (strokes > 0) {
					result = recognizerClassify(recognizer, character, 10);
					if (result > 0) {
						writeWord = resultValue(result, 0);

						stringList = new ArrayList<String>();

						for (int i = 0; i < result; i++) {
							String s = resultValue(result, i);
							if (s != null) {
								stringList.add(s);
							}
							if (i > 10) {
								break;
							}
						}
						setCandidatesViewShown(true);
						mCandidateView.setSuggestions(stringList, true, true);
					}
				}
				handler.sendEmptyMessageDelayed(0, 1000);
				break;
			}

			// 记录当前座标
			posX = x;
			posY = y;
			Draw();
			return true;
		}

		void Draw() {
			// 防止canvas为null导致出现null pointer问题
			Canvas mCanvas = mSurfaceHolder.lockCanvas();
			if (mCanvas != null) {
				mCanvas.drawColor(Color.WHITE); // 清空画布
				mCanvas.drawPath(mPath, mPaint); // 画出轨迹

				mCanvas.drawLine(0, 600, 600, 600, mPaint);
				mCanvas.drawLine(600, 0, 600, 600, mPaint);
				// 数据记录
				mCanvas.drawText("model打开状态 : " + modelState, 5, 50, mTextPaint);
				mCanvas.drawText("触点X的座标 : " + posX, 5, 100, mTextPaint);
				mCanvas.drawText("触点Y的座标 : " + posY, 5, 150, mTextPaint);

				// mCanvas.drawText("总笔画数 : " + strokes, 5, 200, mTextPaint);

				// 显示识别出的文字
				if (result != 0) {
					for (int i = 0; i < resultSize(result); i++) {
						mCanvas.drawText(resultValue(result, i) + " : "
								+ resultScore(result, i), 5, 300 + i * 50,
								mTextPaint);
					}
				}
				mSurfaceHolder.unlockCanvasAndPost(mCanvas);
			}

		}

		void clear() {
			Canvas mCanvas = mSurfaceHolder.lockCanvas();
			if (mCanvas != null) {
				mCanvas.drawColor(Color.WHITE); // 清空画布
				mCanvas.drawLine(0, 600, 600, 600, mPaint);
				mCanvas.drawLine(600, 0, 600, 600, mPaint);
				mSurfaceHolder.unlockCanvasAndPost(mCanvas);
			}
		}
		/*
		 * @Override public void run() {
		 * 
		 * while (mIsRunning) { // 更新前的时间 long startTime =
		 * System.currentTimeMillis();
		 * 
		 * // 线程安全锁 synchronized (mSurfaceHolder) { mCanvas =
		 * mSurfaceHolder.lockCanvas(); Draw();
		 * mSurfaceHolder.unlockCanvasAndPost(mCanvas); } // 获取更新后的时间 long
		 * endTime = System.currentTimeMillis(); // 获取更新时间差 int diffTime = (int)
		 * (endTime - startTime); // 确保每次更新都为FRAME while (diffTime <= FRAME) {
		 * diffTime = (int) (System.currentTimeMillis() - startTime); //
		 * Thread.yield(): 与Thread.sleep(long millis):的区别， // Thread.yield():
		 * 是暂停当前正在执行的线程对象 ，并去执行其他线程。 // Thread.sleep(long
		 * millis):则是使当前线程暂停参数中所指定的毫秒数然后在继续执行线程 Thread.yield(); } }
		 * 
		 * }
		 */

		/*
		 * @Override public void surfaceCreated(SurfaceHolder holder) {
		 * mIsRunning = true; // mThread = new Thread(this); // mThread.start();
		 * }
		 * 
		 * @Override public void surfaceChanged(SurfaceHolder holder, int
		 * format, int width, int height) { // TODO Auto-generated method stub
		 * 
		 * }
		 * 
		 * @Override public void surfaceDestroyed(SurfaceHolder holder) {
		 * resultDestroy(result); characterDestroy(character);
		 * recognizerDestroy(recognizer); mIsRunning = false; mThread = null; }
		 */
	}

	// jni封装方法的声明
	// charater
	public native long characterNew();

	public native void characterDestroy(long c);

	public native void characterClear(long stroke);

	public native int characterAdd(long character, long id, int x, int y);

	public native void characterSetWidth(long character, long width);

	public native void characterSetHeight(long character, long height);

	public native long characterStrokesSize(long character);

	// recognizer
	public native long recognizerNew();

	public native void recognizerDestroy(long recognizer);

	public native int recognizerOpen(long recognizer, String filename);

	public native String recognizerStrerror(long recognizer);

	public native long recognizerClassify(long recognizer, long character,
			long nbest);

	// result
	public native String resultValue(long result, long index);

	public native float resultScore(long result, long index);

	public native long resultSize(long result);

	public native void resultDestroy(long result);

	// 载入.so文件
	static {
		Log.e("IME", "讀jni");
		System.loadLibrary("zinniajni");
	}

}
