package com.chevstrap.rbx;

import android.Manifest;
import android.animation.*;
import android.app.*;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.*;
import android.graphics.*;
import android.graphics.Typeface;
import android.graphics.drawable.*;
import android.media.*;
import android.net.*;
import android.net.Uri;
import android.os.*;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.view.View;
import android.view.View.*;
import android.view.animation.*;
import android.webkit.*;
import android.widget.*;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.HashMap;
import java.util.regex.*;
import org.json.*;

public class SethereActivity extends Activity {
	
	private String rbxpath = "";
	private String data_data = "";
	private HashMap<String, Object> hwuw = new HashMap<>();
	
	private LinearLayout linear1;
	private LinearLayout linear2;
	private ScrollView vscroll1;
	private TextView textview1;
	private LinearLayout linear6;
	private LinearLayout linear7;
	private LinearLayout linear8;
	private ListView listview1;
	private Button buttonopenmods2;
	private Button buttonfastflageditor1;
	private Button buttonfastflagcatch1;
	
	private Intent setagain = new Intent();
	private Intent fil12 = new Intent();
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.sethere);
		initialize(_savedInstanceState);
		
		if (Build.VERSION.SDK_INT >= 23) {
			if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
			||checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
				requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
			} else {
				initializeLogic();
			}
		} else {
			initializeLogic();
		}
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == 1000) {
			initializeLogic();
		}
	}
	
	private void initialize(Bundle _savedInstanceState) {
		linear1 = findViewById(R.id.linear1);
		linear2 = findViewById(R.id.linear2);
		vscroll1 = findViewById(R.id.vscroll1);
		textview1 = findViewById(R.id.textview1);
		linear6 = findViewById(R.id.linear6);
		linear7 = findViewById(R.id.linear7);
		linear8 = findViewById(R.id.linear8);
		listview1 = findViewById(R.id.listview1);
		buttonopenmods2 = findViewById(R.id.buttonopenmods2);
		buttonfastflageditor1 = findViewById(R.id.buttonfastflageditor1);
		buttonfastflagcatch1 = findViewById(R.id.buttonfastflagcatch1);
		
		buttonopenmods2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				Intent sagain = new Intent(Intent.ACTION_VIEW);
				Uri uri = Uri.parse(FileUtil.getExternalStorageDir().concat("/Chevstrap"));
				sagain.setDataAndType(uri, "resource/folder");
				
				// Check if a compatible file manager is installed
				if (sagain.resolveActivity(getPackageManager()) != null) {
					    startActivity(sagain);
				} else {
					_getDataStorage();
					rbxpath = data_data.replace(getPackageName().toString(), "com.roblox.client");
					fil12.setClass(getApplicationContext(), ExplorerActivity.class);
					fil12.putExtra("starterPath", FileUtil.getExternalStorageDir().concat("/Chevstrap/Modifications"));
					startActivity(fil12);
				}
			}
		});
		
		buttonfastflageditor1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				setagain.setClass(getApplicationContext(), CodeeditorActivity.class);
				startActivity(setagain);
			}
		});
		
		buttonfastflagcatch1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				setagain.setClass(getApplicationContext(), StatusfflagsActivity.class);
				startActivity(setagain);
			}
		});
	}
	
	private void initializeLogic() {
		_getDataStorage();
		buttonfastflageditor1.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b, int c, int d) { this.setCornerRadius(a); this.setStroke(b, c); this.setColor(d); return this; } }.getIns((int)10, (int)1, 0xFF101010, 0xFF090909));
		buttonopenmods2.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b, int c, int d) { this.setCornerRadius(a); this.setStroke(b, c); this.setColor(d); return this; } }.getIns((int)10, (int)1, 0xFF101010, 0xFF090909));
		linear7.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b, int c, int d) { this.setCornerRadius(a); this.setStroke(b, c); this.setColor(d); return this; } }.getIns((int)30, (int)0, 0xFF101010, 0xFF101010));

		Typeface arialTypeface = Typeface.createFromAsset(getAssets(), "fonts/arial.ttf");

		buttonopenmods2.setTypeface(arialTypeface, Typeface.NORMAL);
		buttonfastflageditor1.setTypeface(arialTypeface, Typeface.NORMAL);
		textview1.setTypeface(arialTypeface, Typeface.NORMAL);
		buttonfastflagcatch1.setTypeface(arialTypeface, Typeface.NORMAL);

		buttonfastflagcatch1.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b, int c, int d) { this.setCornerRadius(a); this.setStroke(b, c); this.setColor(d); return this; } }.getIns((int)10, (int)1, 0xFF101010, 0xFF090909));
		linear8.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b, int c, int d) { this.setCornerRadius(a); this.setStroke(b, c); this.setColor(d); return this; } }.getIns((int)30, (int)0, 0xFF101010, 0xFF101010));
		buttonfastflagcatch1.setText(getString(R.string.FastFlagsCatcher));
		buttonopenmods2.setText(getString(R.string.Open123));
		buttonfastflageditor1.setText(getString(R.string.FastFlagsEditorTXT));
	}
	
	public void _getDataStorage() {
		if (getFilesDir().getAbsolutePath().endsWith("/")) {
			data_data = getFilesDir().getAbsolutePath();
		} else {
			data_data = getFilesDir().getAbsolutePath() + "/";
		}
	}
	
	
	@Deprecated
	public void showMessage(String _s) {
		Toast.makeText(getApplicationContext(), _s, Toast.LENGTH_SHORT).show();
	}
	
	@Deprecated
	public int getLocationX(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[0];
	}
	
	@Deprecated
	public int getLocationY(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[1];
	}
	
	@Deprecated
	public int getRandom(int _min, int _max) {
		Random random = new Random();
		return random.nextInt(_max - _min + 1) + _min;
	}
	
	@Deprecated
	public ArrayList<Double> getCheckedItemPositionsToArray(ListView _list) {
		ArrayList<Double> _result = new ArrayList<Double>();
		SparseBooleanArray _arr = _list.getCheckedItemPositions();
		for (int _iIdx = 0; _iIdx < _arr.size(); _iIdx++) {
			if (_arr.valueAt(_iIdx))
			_result.add((double)_arr.keyAt(_iIdx));
		}
		return _result;
	}
	
	@Deprecated
	public float getDip(int _input) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, _input, getResources().getDisplayMetrics());
	}
	
	@Deprecated
	public int getDisplayWidthPixels() {
		return getResources().getDisplayMetrics().widthPixels;
	}
	
	@Deprecated
	public int getDisplayHeightPixels() {
		return getResources().getDisplayMetrics().heightPixels;
	}
}
