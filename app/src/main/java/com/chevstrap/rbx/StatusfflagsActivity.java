package com.chevstrap.rbx;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class StatusfflagsActivity extends Activity {

	private String resulttxt = "";

	private LinearLayout linear2;
	private ScrollView vscroll1;
	private LinearLayout linear6;
	private LinearLayout linear7;
	private LinearLayout linear8;
	private LinearLayout linear10;
	private LinearLayout linear11;
	private TextView textview2;
	private EditText edittext12;
	private Button buttoncatch1;
	private TextView existtext1;

	private AlertDialog.Builder get_a_better_wifi_dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.statusfflags);
		initialize(savedInstanceState);
		initializeLogic();
	}

	private void initialize(Bundle savedInstanceState) {
		linear2 = findViewById(R.id.linear2);
		vscroll1 = findViewById(R.id.vscroll1);
		linear6 = findViewById(R.id.linear6);
		linear7 = findViewById(R.id.linear7);
		linear8 = findViewById(R.id.linear8);
		linear10 = findViewById(R.id.linear10);
		linear11 = findViewById(R.id.linear11);
		textview2 = findViewById(R.id.textview2);
		edittext12 = findViewById(R.id.edittext12);
		buttoncatch1 = findViewById(R.id.buttoncatch1);
		existtext1 = findViewById(R.id.existtext1);
		get_a_better_wifi_dialog = new AlertDialog.Builder(this);

		buttoncatch1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				FVariablesFetcher.fetch(new FVariablesFetcher.Callback() {
					@Override
					public void onResult(final String result) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								resulttxt = result;
								// Update UI on main thread
								if ("\"".concat(resulttxt.concat("\"")).contains(edittext12.getText().toString())) {
									existtext1.setText(getString(R.string.IsExisted2));
								} else {
									existtext1.setText(getString(R.string.IsExisted3));
								}
							}
						});
					}
				});
			}
		});

		aboutApp appCheckWifi = new aboutApp(this);

		// Network check on a background thread
		new Thread(() -> {
			if (!appCheckWifi.isInternetWorking()) {
				runOnUiThread(() -> {
					get_a_better_wifi_dialog.setTitle("Message");
					get_a_better_wifi_dialog.setMessage("No connection or unstable connection");
					get_a_better_wifi_dialog.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface _dialog, int _which) {
							finish();
						}
					});
					get_a_better_wifi_dialog.create().show();
				});
			}
		}).start();
	}

	private void initializeLogic() {
		Typeface customFont = Typeface.createFromAsset(getAssets(), "fonts/arial.ttf");

		textview2.setTypeface(customFont);
		buttoncatch1.setTypeface(customFont);
		existtext1.setTypeface(customFont);

		buttoncatch1.setBackground(new GradientDrawable() {
			public GradientDrawable getIns(int a, int b) {
				this.setCornerRadius(a);
				this.setColor(b);
				return this;
			}
		}.getIns(20, 0xFF090909));

		textview2.setText(getString(R.string.FastFlagsCatcher));
		buttoncatch1.setText(getString(R.string.CatchIt));
		edittext12.setHint(getString(R.string.NameFastFlag));
		existtext1.setText(" ");
	}

	// Removed deprecated methods
}