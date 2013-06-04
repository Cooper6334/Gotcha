package com.tomhw;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FindTagView extends LinearLayout {
	ImageButton image;
	TextView text;
	String name;
	Bitmap b;
	Handler h = new Handler() {
		@SuppressLint("NewApi")
		@Override
		public void handleMessage(Message m) {
			image.setImageBitmap(b);
			image.setBackground(null);
			// image.setBackgroundColor(Color.RED);
		}
	};

	@SuppressLint("NewApi")
	public FindTagView(Context context) {
		super(context);
		name = "Google";
		setOrientation(LinearLayout.VERTICAL);
		this.setLayoutParams(new LayoutParams(300, 440));
		LinearLayout.LayoutParams layoutParams = new LayoutParams(300, 320);
		LinearLayout.LayoutParams layoutParams2 = new LayoutParams(300, 120);

		image = new ImageButton(context);
		image.setBackground(getResources().getDrawable(R.drawable.ftg));
		// image.setImageResource(R.drawable.ftbk);
		image.setScaleType(ScaleType.CENTER_CROP);
		image.setPadding(0, 0, 0, 0);
		image.setOnClickListener(new ImageButton.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((FindActivity) getContext()).googleSearch();
			}
		});

		this.addView(image, layoutParams);
		text = new TextView(context);
		text.setTextSize(24);
		text.setText(name);
		text.setTextColor(Color.WHITE);
		text.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
		this.addView(text, layoutParams2);

		// TODO Auto-generated constructor stub

	}

	@SuppressLint("NewApi")
	public FindTagView(Context context, String n) {
		super(context);
		name = n;
		setOrientation(LinearLayout.VERTICAL);
		this.setLayoutParams(new LayoutParams(300, 440));
		LinearLayout.LayoutParams layoutParams = new LayoutParams(300, 320);
		LinearLayout.LayoutParams layoutParams2 = new LayoutParams(300, 120);

		image = new ImageButton(context);
		image.setBackground(getResources().getDrawable(R.anim.ftload));
		AnimationDrawable animation = new AnimationDrawable();
		animation = (AnimationDrawable) image.getBackground();
		animation.start();

		// image.setImageResource(R.drawable.ftbk);
		image.setScaleType(ScaleType.CENTER_CROP);
		image.setPadding(0, 0, 0, 0);
		image.setOnClickListener(new ImageButton.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((FindActivity) getContext()).selectTag(name);

			}
		});
		image.setOnLongClickListener(new ImageButton.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub

				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						URI uri;
						try {
							uri = new URI(

							"http://translate.google.com/translate_tts?tl=zh-TW&q="
									+ name);
							URL u = new URL(uri.toASCIIString());
							HttpURLConnection c = (HttpURLConnection) u
									.openConnection();
							c.addRequestProperty("User-Agent", "Mozilla/5.0");
							c.setRequestMethod("GET");
							c.setDoOutput(true);
							c.connect();
							File file = new File(Environment
									.getExternalStorageDirectory()
									+ "/download/" + "testsound");
							FileOutputStream f = new FileOutputStream(file);
							InputStream in = c.getInputStream();
							byte[] buffer = new byte[1024];
							int len1 = 0;
							while ((len1 = in.read(buffer)) > 0) {
								f.write(buffer, 0, len1);
							}
							f.close();

							MediaPlayer mediaPlayer = new MediaPlayer();

							// 設定外部音訊檔路徑,此路徑可為本機SD卡或是網路資源
							String path = Environment
									.getExternalStorageDirectory()
									.getAbsolutePath()
									+ "/download/" + "testsound";

							mediaPlayer.setDataSource(path);

							// 外部資源需先執行prepare預載
							mediaPlayer.prepare();
							mediaPlayer.start();

						} catch (URISyntaxException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}).start();

				return true;
			}
		});
		this.addView(image, layoutParams);
		text = new TextView(context);
		text.setTextSize(24);
		text.setText(name);
		text.setTextColor(Color.WHITE);
		text.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
		this.addView(text, layoutParams2);

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				// 找圖片
				
				// try {
				// Thread.sleep(5000);
				// } catch (InterruptedException e1) {
				// // TODO Auto-generated catch block
				// e1.printStackTrace();
				// }
				URL url;
				try {
					String s = URLEncoder.encode(name, "UTF-8");
					url = new URL(
							"https://ajax.googleapis.com/ajax/services/search/images?"
									+ "v=1.0&q=" + s + "&rsz=1&hl=zh-TW");
					URLConnection connection = url.openConnection();
					connection.addRequestProperty("Referer",
							"http://technotalkative.com");

					String line;
					StringBuilder builder = new StringBuilder();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(connection.getInputStream()));
					while ((line = reader.readLine()) != null) {
						builder.append(line);
					}

					JSONObject json = new JSONObject(builder.toString());
					JSONObject responseObject = json
							.getJSONObject("responseData");
					JSONArray resultArray = responseObject
							.getJSONArray("results");
					if (resultArray.length() > 0) {
						JSONObject image = resultArray.getJSONObject(0);
						// Log.e("json", image.getString("tbUrl"));
						b = GetDataFromURL.getBitmapFromURL(image
								.getString("tbUrl"));
					}
					h.sendEmptyMessage(0);

				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}).start();
		// TODO Auto-generated constructor stub

	}
}
