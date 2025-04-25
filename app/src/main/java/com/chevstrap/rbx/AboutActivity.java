package com.chevstrap.rbx;

import android.animation.*;
import android.app.*;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.graphics.Typeface;
import android.graphics.drawable.*;
import android.media.*;
import android.net.*;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.*;
import org.json.*;

public class AboutActivity extends Activity {
	
	private ArrayList<HashMap<String, Object>> list_license = new ArrayList<>();

	public static Typeface arial;
	
	private ScrollView vscroll3;
	private LinearLayout linear9;
	private LinearLayout linear10;
	private ListView listview1;
	private LinearLayout linear11;
	private LinearLayout linear12;
	private TextView textview_appversion;
	private TextView textview1;
	private TextView textview2;
	private TextView textview3;
	private TextView textview6;
	private TextView textview_credits;
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.about);
		initialize(_savedInstanceState);
		initializeLogic();

		arial = Typeface.createFromAsset(getAssets(), "fonts/arial.ttf");
	}
	
	private void initialize(Bundle _savedInstanceState) {
		vscroll3 = findViewById(R.id.vscroll3);
		linear9 = findViewById(R.id.linear9);
		linear10 = findViewById(R.id.linear10);
		listview1 = findViewById(R.id.listview1);
		linear11 = findViewById(R.id.linear11);
		linear12 = findViewById(R.id.linear12);
		textview_appversion = findViewById(R.id.textview_appversion);
		textview1 = findViewById(R.id.textview1);
		textview2 = findViewById(R.id.textview2);
		textview3 = findViewById(R.id.textview3);
		textview6 = findViewById(R.id.textview6);
		textview_credits = findViewById(R.id.textview_credits);
		
		listview1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> _param1, View _param2, int _param3, long _param4) {
				final int _position = _param3;
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(list_license.get(_position).values().iterator().next().toString()));
				startActivity(browserIntent);
			}
		});
		
		textview_appversion.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				
			}
		});
	}
	
	private void initializeLogic() {
		{
			HashMap<String, Object> _item = new HashMap<>();
			_item.put("Chevstrap", "https://github.com/FrosSky/Chevstrap/blob/main/LICENSE");
			list_license.add(_item);
		}
		listview1.setAdapter(new Listview1Adapter(list_license));
		((BaseAdapter)listview1.getAdapter()).notifyDataSetChanged();
		// Assuming 'aboutApp' has a method 'getAppVersion()' that returns a String
		aboutApp app = new aboutApp(this); // Instantiate the app object once
		textview_appversion.setText(new aboutApp(this).getAppVersion());

		textview_credits.setText("// Code & UI Design //\n- @FrosSky\n\n// List All Fast Flags of Roblox //\n- All Contributors of Roblox Client Tracker\n\n- - Also this App inspired by Bloxstrap - -");

		textview_credits.setTypeface(arial, Typeface.NORMAL);
		textview6.setTypeface(arial, Typeface.NORMAL);
		textview3.setTypeface(arial, Typeface.NORMAL);
		textview2.setTypeface(arial, Typeface.NORMAL);
		textview1.setTypeface(arial, Typeface.NORMAL);
		textview_appversion.setTypeface(arial, Typeface.NORMAL);

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
				_view = _inflater.inflate(R.layout.item_license, null);
			}
			
			final LinearLayout linear1 = _view.findViewById(R.id.linear1);
			final TextView textview1 = _view.findViewById(R.id.textview1);
			final ImageView imageview1 = _view.findViewById(R.id.imageview1);
			
			textview1.setText(list_license.get(_position).keySet().iterator().next());
			textview1.setTypeface(arial, Typeface.NORMAL);

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
