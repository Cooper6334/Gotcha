package com.tomhw;

import java.io.UnsupportedEncodingException;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

public class FindActivity extends Activity {
	String[] testString = { "西瓜", "鳳梨", "番茄", "荔枝", "香蕉", "芭樂", "水梨" };
	// int cnt = 0;

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message m) {
			switch (m.what) {
			case 0:
				for (int i = 0; i < 3; i++) {
					linear[i].removeAllViews();
				}
				int l = 24;
				if (l > showingEdges.size()) {
					l = showingEdges.size();
				}

				int i;
				boolean flag = false;
				for (i = 0; i < l; i++) {
					if (showingEdges.get(i).cnt < tags.size()) {
						flag = true;
						break;
					}
					linear[i % 3].addView(new FindTagView(FindActivity.this,
							showingEdges.get(i).name));
				}
				if (flag) {

					linear[i % 3].addView(new FindTagView(FindActivity.this));

					for (; i < l - 1; i++) {
						linear[(i + 1) % 3].addView(new FindTagView(
								FindActivity.this, showingEdges.get(i).name));

					}
				}
				findingDialog.dismiss();

				break;

			}
		}
	};
	ArrayList<TagEdge> showingEdges;
	ArrayList<TagView> tags = new ArrayList<TagView>();
	InputMethodManager imm;

	String queryTag;
	DataSet queryData;

	PopupWindow findingDialog;
	// ProgressDialog progress;
	EditText editText;
	LinearLayout[] linear = new LinearLayout[3];
	LinearLayout tagLinear;
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.finding);

		imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		editText = (EditText) findViewById(R.id.editText1);
		editText.setText("");

		findingDialog = new PopupWindow(this);

		LayoutInflater lay = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View v = lay.inflate(R.layout.ftloaddialog, null);

		findingDialog.setWidth(300);
		findingDialog.setHeight(450);
		findingDialog.setBackgroundDrawable(null);
		findingDialog.setContentView(v);

		// findingDialog.setTitle("尋找關連中");
		ImageView i = (ImageView) v.findViewById(R.id.imageView1);

		AnimationDrawable animation = new AnimationDrawable();
		animation = (AnimationDrawable) i.getBackground();
		animation.start();
		// progress = new ProgressDialog(this);

		// progress.setMessage("搜尋中");

		// query
		((Button) findViewById(R.id.button2))
				.setOnClickListener(new Button.OnClickListener() {

					@Override
					public void onClick(View v) {

						if (editText.getText().toString().equals("")) {
							return;
						}

						for (int i = 0; i < 3; i++) {
							linear[i].removeAllViews();
						}
						imm.hideSoftInputFromWindow(editText.getWindowToken(),
								0);
						String s = editText.getText().toString();
						findingDialog.showAtLocation(
								findViewById(R.id.RelativeLayout1),
								Gravity.CENTER, 0, 0);
	
						TagView t;
						if (tags.size() == 0) {
							t = new TagView(FindActivity.this, s, true);
						} else {
							t = new TagView(FindActivity.this, s, false);
						}
						tags.add(t);
						tagLinear.addView(tags.get(tags.size() - 1));

						editText.setText("");

						query(s);
					}
				});

		// reset
		((Button) findViewById(R.id.button1))
				.setOnClickListener(new Button.OnClickListener() {

					@Override
					public void onClick(View v) {
						showingEdges = null;
						// listView.setAdapter(null);

						queryData = null;
						editText.setText("");

						for (int i = 0; i < 3; i++) {
							linear[i].removeAllViews();
						}
						tags.clear();
						tagLinear.removeAllViews();
						//
						// for (int i = 0; i < 4; i++) {
						//
						// nameViews[i].setText(null);
						// imageButtons[i].setImageBitmap(null);
						//
						// }
					}
				});

		// finish
		((Button) findViewById(R.id.button4))
				.setOnClickListener(new Button.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						finish();
					}
				});
		linear[0] = (LinearLayout) findViewById(R.id.linear1);
		linear[1] = (LinearLayout) findViewById(R.id.linear2);
		linear[2] = (LinearLayout) findViewById(R.id.linear3);
		tagLinear = (LinearLayout) findViewById(R.id.taglinear);

	}

	@Override
	public void onResume() {
		super.onResume();
		MainActivity.flagFindTagOpening = true;
	}

	@Override
	public void onPause() {
		super.onPause();
		MainActivity.flagFindTagOpening = false;
	}

	void query(String tag) {
		queryTag = tag;

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				// query

				try {
					String tag = queryTag;

					queryTag = URLEncoder.encode(queryTag, "UTF-8");

					String ss = GetDataFromURL
							.getJsonResponseFromURL("http://conceptnet5.media.mit.edu/data/5.1/c/zh_TW/"
									+ queryTag
									+ "?get=incoming_edges+outgoing_edges&limit=50");
					JSONObject object = new JSONObject(ss);

					JSONArray edges = object.getJSONArray("edges");
					queryData = new DataSet(tag, edges);

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				tags.get(tags.size() - 1).edges = (ArrayList<TagEdge>) (queryData.edgeList)
						.clone();
				reQuery();

			}
		}).start();
	}

	void reQuery() {

		// 取出現次數
		showingEdges = new ArrayList<TagEdge>();
		for (TagView t : tags) {
			ArrayList<TagEdge> edges = t.edges;
			for (TagEdge e : edges) {
				boolean flag = false;
				for (TagEdge r : showingEdges) {
					if (r.name.equals(e.name)) {
						r.cntadd();
						flag = true;
					}
				}
				if (!flag) {
					showingEdges.add(e.clone());
				}
			}

		}
		// 排序
		Collections.sort(showingEdges, new Comparator<TagEdge>() {

			@Override
			public int compare(TagEdge lhs, TagEdge rhs) {
				// TODO Auto-generated method stub
				if (rhs.cnt != lhs.cnt) {
					return rhs.cnt - lhs.cnt;
				}
				return (int) (rhs.score - lhs.score);
			}

		});

		for (TagEdge e : showingEdges) {

			Log.e("tag", e.name + ":" + e.score + "," + e.cnt);
		}
		handler.sendEmptyMessage(0);
	}

	private void startVoiceRecognitionActivity() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
				"Speech recognition demo");
		startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
	}

	/**
	 * Handle the results from the recognition activity.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE
				&& resultCode == RESULT_OK) {
			// Fill the list view with the strings the recognizer thought it
			// could have heard
			ArrayList<String> matches = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			if (matches.size() > 0) {
				editText.setText(matches.get(0));
			}

		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	void removeTag(TagView l) {
		// for (TagEdge e : l.edges) {
		// Log.e("tag", e.name);
		// }
		tags.remove(l);
		tagLinear.removeView(l);
		if (tagLinear.getChildCount() > 0) {
			((TagView) tagLinear.getChildAt(0)).setFirst();
		}
		reQuery();
	}

	void selectTag(String s) {
		MainActivity.testString = s;
		finish();
	}

	void googleSearch() {
		String s = "";
		for (TagView v : tags) {
			s += v.name + " ";
		}
		Intent i = new Intent(Intent.ACTION_WEB_SEARCH);
		i.putExtra(SearchManager.QUERY, s);
		startActivity(i);
		finish();
	}
}
