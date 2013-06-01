package com.tomhw;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class DrawActivity extends Activity {
	HandDrawView drawView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drawactivity);

		drawView = (HandDrawView) findViewById(R.id.surfaceView1);
		drawView.init();
		((Button) findViewById(R.id.button1))
				.setOnClickListener(new Button.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

						Uri upload = drawView.save();

						// b=drawView.getHolder().get'
						if (upload != null) {

							Intent intent = new Intent(Intent.ACTION_SEND);
							intent.setType("image/*");
							intent.putExtra(Intent.EXTRA_STREAM, upload);
							intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
							intent.putExtra(Intent.EXTRA_TEXT, "你好 ");
							intent.putExtra(Intent.EXTRA_TITLE, "我是标题");
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(Intent.createChooser(intent, "请选择"));
							finish();
						} else {
							Toast.makeText(DrawActivity.this, "fail",
									Toast.LENGTH_LONG).show();
						}

					}
				});
		((Button) findViewById(R.id.button2))
				.setOnClickListener(new Button.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						drawView.clear();
					}
				});

	}
}
