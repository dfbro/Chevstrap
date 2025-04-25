package com.chevstrap.rbx;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.*;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.*;
import android.util.*;
import android.view.*;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ExplorerActivity extends Activity {
	
	public final int REQ_CD_REPLACEFILE = 101;
	
	private double nr = 0;
	private String currentFilePath = "";
	private boolean mainBack = false;
	private String path = "";
	
	private ArrayList<String> list = new ArrayList<>();
	private ArrayList<HashMap<String, Object>> listmap = new ArrayList<>();
	private ArrayList<String> copyMove = new ArrayList<>();
	
	private LinearLayout linear1;
	private LinearLayout linear3;
	private LinearLayout linear2;
	private ListView listview1;
	private ImageView imageview1;
	private TextView textview1;
	
	private Intent replacefile = new Intent(Intent.ACTION_GET_CONTENT);
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.explorer);
		initialize(_savedInstanceState);
		
		if (Build.VERSION.SDK_INT >= 23) {
			if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
				requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
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
		linear3 = findViewById(R.id.linear3);
		linear2 = findViewById(R.id.linear2);
		listview1 = findViewById(R.id.listview1);
		imageview1 = findViewById(R.id.imageview1);
		textview1 = findViewById(R.id.textview1);
		replacefile.setType("*/*");
		replacefile.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
		
		listview1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> _param1, View _param2, int _param3, long _param4) {
				final int _position = _param3;
				path = listmap.get((int)_position).get("path").toString();
				if (FileUtil.isFile(listmap.get((int)_position).get("path").toString())) {
					if (path.contains(".mp4") || path.contains(".avi")) {
						
					} else {
						if (path.contains(".mp3") || path.contains(".wav")) {
							
						} else {
							if (path.contains(".html") || path.contains(".php")) {
								
							} else {
								if (path.contains(".jpg") || (path.contains(".jpeg") || path.contains(".png"))) {
									
								} else {
									
								}
							}
						}
					}
				} else {
					_getFileList(listmap.get((int)_position).get("path").toString());
				}
			}
		});
		
		listview1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> _param1, View _param2, int _param3, long _param4) {
				final int _position = _param3;
				
				return true;
			}
		});
		
		imageview1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				finish();
			}
		});
	}
	
	private void initializeLogic() {
		_getFileList(getIntent().getStringExtra("starterPath"));
		copyMove.add("Move");
		copyMove.add("Copy");
		mainBack = true;
		textview1.setText(currentFilePath);
		textview1.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/arial.ttf"));
	}
	
	@Override
	protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
		super.onActivityResult(_requestCode, _resultCode, _data);
		
		switch (_requestCode) {
			case REQ_CD_REPLACEFILE:
			if (_resultCode == Activity.RESULT_OK) {
				ArrayList<String> _filePath = new ArrayList<>();
				if (_data != null) {
					if (_data.getClipData() != null) {
						for (int _index = 0; _index < _data.getClipData().getItemCount(); _index++) {
							ClipData.Item _item = _data.getClipData().getItemAt(_index);
							_filePath.add(FileUtil.convertUriToFilePath(getApplicationContext(), _item.getUri()));
						}
					}
					else {
						_filePath.add(FileUtil.convertUriToFilePath(getApplicationContext(), _data.getData()));
					}
				}
				textview1.setText(FileUtil.readFile(_filePath.get((int)(0))));
			}
			else {
				
			}
			break;
			default:
			break;
		}
	}
	
	@Override
	public void onBackPressed() {
		if (currentFilePath.replace(Uri.parse(Uri.parse(currentFilePath).getLastPathSegment()).getLastPathSegment(), "").contains(FileUtil.getExternalStorageDir().concat("/Chevstrap"))) {
			textview1.setText(currentFilePath.replace(Uri.parse(Uri.parse(currentFilePath).getLastPathSegment()).getLastPathSegment(), ""));
			_getFileList(currentFilePath.replace(Uri.parse(Uri.parse(currentFilePath).getLastPathSegment()).getLastPathSegment(), ""));
		} else {
			finish();
		}
	}
	public void _getFileList(final String _fromPath) {
		list.clear();
		listmap.clear();
		FileUtil.listDir(_fromPath, list);
		nr = 0;
		for(int _repeat15 = 0; _repeat15 < (int)(list.size()); _repeat15++) {
			{
				HashMap<String, Object> _item = new HashMap<>();
				_item.put("path", list.get((int)(nr)));
				listmap.add(_item);
			}
			nr++;
		}
		currentFilePath = _fromPath;
		listview1.setAdapter(new Listview1Adapter(listmap));
	}
	
	public class Listview1Adapter extends BaseAdapter {
		
		ArrayList<HashMap<String, Object>> _data;
		
		public Listview1Adapter(ArrayList<HashMap<String, Object>> _arr) {
			_data = _arr;
		}
		
		@Override
		public int getCount() {
			return _data.size();
		}
		
		@Override
		public HashMap<String, Object> getItem(int _index) {
			return _data.get(_index);
		}
		
		@Override
		public long getItemId(int _index) {
			return _index;
		}
		
		@Override
		public View getView(final int _position, View _v, ViewGroup _container) {
			LayoutInflater _inflater = getLayoutInflater();
			View _view = _v;
			if (_view == null) {
				_view = _inflater.inflate(R.layout.listview_explorer, null);
			}
			
			final LinearLayout linear1 = _view.findViewById(R.id.linear1);
			final ImageView icon1 = _view.findViewById(R.id.icon1);
			final LinearLayout linear2 = _view.findViewById(R.id.linear2);
			final TextView textview1 = _view.findViewById(R.id.textview1);
			final ImageView replacebutton1 = _view.findViewById(R.id.replacebutton1);
			
			replacebutton1.setVisibility(View.GONE);
			textview1.setText(Uri.parse(Uri.parse(listmap.get((int)_position).get("path").toString()).getLastPathSegment()).getLastPathSegment());
			if (FileUtil.isFile(listmap.get((int)_position).get("path").toString())) {
				if (textview1.getText().toString().contains(".mp4") || textview1.getText().toString().contains(".avi")) {
					icon1.setImageResource(R.drawable.blank90x90);
				} else {
					if (textview1.getText().toString().contains(".mp3") || textview1.getText().toString().contains(".wav")) {
						icon1.setImageResource(R.drawable.blank90x90);
					} else {
						if (textview1.getText().toString().contains(".html") || textview1.getText().toString().contains(".php")) {
							icon1.setImageResource(R.drawable.blank90x90);
						} else {
							if (textview1.getText().toString().contains(".jpg") || (textview1.getText().toString().contains(".jpeg") || textview1.getText().toString().contains(".png"))) {
								Bitmap bmp = FileUtil.decodeSampleBitmapFromPath(listmap.get(_position).get("path").toString(), 300, 300);
								if (bmp != null) {
									    int w = bmp.getWidth(), h = bmp.getHeight();
									    float scale = Math.max(150f / w, 150f / h);
									    
									    Bitmap resized = Bitmap.createScaledBitmap(bmp, Math.round(w * scale), Math.round(h * scale), true);
									    icon1.setImageBitmap(Bitmap.createBitmap(resized, (resized.getWidth() - 150) / 2, (resized.getHeight() - 150) / 2, 150, 150));
								}
								
							} else {
								icon1.setImageResource(R.drawable.folder1);
							}
						}
					}
				}
				replacebutton1.setVisibility(View.VISIBLE);
			} else {
				icon1.setImageResource(R.drawable.folder1);
			}
			replacebutton1.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View _view) {
					startActivityForResult(replacefile, REQ_CD_REPLACEFILE);
				}
			});
			
			return _view;
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
