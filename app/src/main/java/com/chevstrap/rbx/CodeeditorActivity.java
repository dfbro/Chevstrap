package com.chevstrap.rbx;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.IOException;


import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class CodeeditorActivity extends Activity {

	public final int REQ_CD_JSOOOON = 101;
	private Timer _timer = new Timer();

	private String rbxpath = "";
	private String data_data = "";
	private boolean IsRoot = false;
	private double isedit = 0;
	private String lastchanged = "";
	private HashMap<String, Object> map = new HashMap<>();
	private HashMap<String, Object> jsonn = new HashMap<>();

    private ArrayList<HashMap<String, Object>> jsoncache = new ArrayList<>();
	private ArrayList<HashMap<String, Object>> tabjsonhelp = new ArrayList<>();
	private ArrayList<String> jsa = new ArrayList<>();

	private LinearLayout linear6;
	private TextView textview4;
	private LinearLayout linear8;
	private LinearLayout linear9;
	private Button button3;
	private Button button5;
	private ScrollView vscroll1;
	private LinearLayout linear10;
	private EditText edittext2;

	private TimerTask wait;
	private AlertDialog.Builder requestfflagschange;
	private AlertDialog.Builder ask;
	private AlertDialog dialog;
	private AlertDialog.Builder requestaddfflag;
	private final Intent jsoooon = new Intent(Intent.ACTION_GET_CONTENT);

	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.codeeditor);
		initialize(_savedInstanceState);
		if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
				|| checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
			requestPermissions(new String[] {
					Manifest.permission.READ_EXTERNAL_STORAGE,
					Manifest.permission.WRITE_EXTERNAL_STORAGE
			}, 1000);
		} else {
			initializeLogic();
		}
    }

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == 1000) {
			initializeLogic();
		}
	}

	private void initialize(Bundle _savedInstanceState) {
		linear6 = findViewById(R.id.linear6);
		textview4 = findViewById(R.id.textview4);
		linear8 = findViewById(R.id.linear8);
		linear9 = findViewById(R.id.linear9);
		button3 = findViewById(R.id.button3);
		button5 = findViewById(R.id.button5);
		vscroll1 = findViewById(R.id.vscroll1);
		linear10 = findViewById(R.id.linear10);
		edittext2 = findViewById(R.id.edittext2);
		requestfflagschange = new AlertDialog.Builder(this);
		ask = new AlertDialog.Builder(this);
		requestaddfflag = new AlertDialog.Builder(this);

		jsoooon.setType("application/json");
		jsoooon.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

		button3.setOnClickListener(_view -> {
            try {
                String formattedJson = JsonFormatter.formatJson(edittext2.getText().toString());
                if (Objects.equals(formattedJson, "Invalid JSON")) {
                    Toast.makeText(CodeeditorActivity.this, "Invalid JSON", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    edittext2.setText(formattedJson);
                }
            } catch (Exception ignore) {

            }

            File clientSettingsDir = new File(getExternalFilesDir(null), "Modifications/ClientSettings");

            if (!clientSettingsDir.exists()) {
                boolean dirCreated = clientSettingsDir.mkdirs();
                if (!dirCreated) {
                    return; // Exit early if directory creation fails
                }
            }

            File outFile = new File(clientSettingsDir, "ClientAppSettings.json");

            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile)))) {
                writer.write(edittext2.getText().toString());
                showMessage("FFlags have been successfully changed");
            } catch (IOException e) {
                showMessage("Error writing to file: " + e.getMessage());
            }
        });

		button5.setOnClickListener(_view -> startActivityForResult(jsoooon, REQ_CD_JSOOOON));

		linear10.setOnClickListener(_view -> {
			// Placeholder for future functionality
		});
	}

	private void initializeLogic() {
		GradientDrawable button5Bg = new GradientDrawable();
		button5Bg.setCornerRadius(30);
		button5Bg.setStroke(5, 0xFF101010);
		button5Bg.setColor(Color.TRANSPARENT);
		button5.setBackground(button5Bg);

		GradientDrawable button3Bg = new GradientDrawable();
		button3Bg.setCornerRadius(30);
		button3Bg.setStroke(1, 0xFF101010);
		button3Bg.setColor(0xFF090909);
		button3.setBackground(button3Bg);

		GradientDrawable scrollBg = new GradientDrawable();
		scrollBg.setCornerRadius(10);
		scrollBg.setStroke(1, 0xFF101010);
		scrollBg.setColor(0xFF090909);
		vscroll1.setBackground(scrollBg);

		Typeface arial = Typeface.createFromAsset(getAssets(), "fonts/arial.ttf");
		button3.setTypeface(arial);
		button5.setTypeface(arial);
		textview4.setTypeface(arial);

		textview4.setText(getString(R.string.FastFlagsEditorTXT));
		button3.setText(getString(R.string.SaveText));
		button5.setText(getString(R.string.LoadText));
		edittext2.setHint(getString(R.string.TypeHere));

		File configFile = new File(Environment.getExternalStorageDirectory(), "Chevstrap/Modifications/ClientSettings/ClientAppSettings.json");
		if (configFile.exists()) {
			edittext2.setText(FileUtil.readFile(configFile.getAbsolutePath()));
		}

		try {
			getPackageManager().getPackageInfo("com.roblox.client", 0);
		} catch (PackageManager.NameNotFoundException e) {
            //boolean isRobloxNotInstalled = true;
		}
	}

	@Override
	protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
		super.onActivityResult(_requestCode, _resultCode, _data);

		if (_requestCode == REQ_CD_JSOOOON && _resultCode == Activity.RESULT_OK) {
			ArrayList<String> _filePath = new ArrayList<>();
			if (_data != null) {
				if (_data.getClipData() != null) {
					for (int i = 0; i < _data.getClipData().getItemCount(); i++) {
						ClipData.Item item = _data.getClipData().getItemAt(i);
						_filePath.add(FileUtil.convertUriToFilePath(getApplicationContext(), item.getUri()));
					}
				} else if (_data.getData() != null) {
					_filePath.add(FileUtil.convertUriToFilePath(getApplicationContext(), _data.getData()));
				}
				if (!_filePath.isEmpty()) {
					edittext2.setText(FileUtil.readFile(_filePath.get(0)));
				}
			}
		}
	}

	private void showMessage(String _s) {
		Toast.makeText(getApplicationContext(), _s, Toast.LENGTH_SHORT).show();
	}

	private void saveJsonToFile() {
		File clientSettingsDir = new File(getExternalFilesDir(null), "Chevstrap/Modifications/ClientSettings");

		if (!clientSettingsDir.exists() && !clientSettingsDir.mkdirs()) {
			showMessage("Failed to create settings directory");
			return;
		}

		File outFile = new File(clientSettingsDir, "ClientAppSettings.json");

		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), StandardCharsets.UTF_8))) {
			writer.write(edittext2.getText().toString());
			showMessage("FFlags have been successfully changed");
		} catch (IOException e) {
			showMessage("Error writing to file: " + e.getMessage());
		}
	}


	@Override
	protected void onDestroy() {
		if (_timer != null) {
			_timer.cancel();
		}
		super.onDestroy();
	}
}
