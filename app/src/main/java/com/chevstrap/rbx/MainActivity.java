package com.chevstrap.rbx;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.chevstrap.rbx.Integrations.ActivityWatcher;
import com.chevstrap.rbx.UI.Elements.CustomDialogs.AboutFragment;
import com.chevstrap.rbx.UI.Elements.CustomDialogs.MessageboxFragment;
import com.chevstrap.rbx.Utility.aboutApp;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
	private ExecutorService RBXActivityWatcher;
	private LinearLayout buttonLaunchrbx;
	private LinearLayout buttonConfiguresettings;
	private LinearLayout buttonAbout;
	private TextView textview_version;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		setContentView(R.layout.main); // Ensure the XML file is named main.xml

		initialize();
		initializeLogic();
	}

	private void initialize() {
		LaunchHandler launchHandler = new LaunchHandler(this, getSupportFragmentManager());

		buttonLaunchrbx = findViewById(R.id.button_launchrbx);
		buttonConfiguresettings = findViewById(R.id.button_configuresettings);
		buttonAbout = findViewById(R.id.button_about);
		textview_version = findViewById(R.id.textview_version);

		buttonLaunchrbx.setOnClickListener(v -> {
			FFlagsSettingsManager manager = new FFlagsSettingsManager(getApplicationContext());
			try {
				launchHandler.LaunchRoblox(manager.getPackageTarget());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});

		buttonConfiguresettings.setOnClickListener(v -> {
			startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
			finish();
		});

		buttonAbout.setOnClickListener(v -> {
			AboutFragment dialog = new AboutFragment();
			dialog.show(getSupportFragmentManager(), "aD");
		});
	}

	private void initializeLogic() {
		aboutApp app = new aboutApp(getApplicationContext());
		textview_version.setText(app.getAppVersion());

		AstyleButtonBlack1(buttonAbout);
		AstyleButtonBlack1(buttonConfiguresettings);
		AstyleButtonBlack1(buttonLaunchrbx);

		StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
		long blockSize = stat.getBlockSizeLong();
		long free = (stat.getAvailableBlocksLong() * blockSize) / (1024 * 1024 * 1024);

		if (free < 0.6) {
			showStorageFullDialog();
		}
	}

	public void LaunchWatcher() {
		FFlagsSettingsManager manager = new FFlagsSettingsManager(getApplicationContext());

		boolean getSetting1 = Boolean.parseBoolean(manager.getSetting("EnableActivityTracking"));
		boolean getSetting2 = Boolean.parseBoolean(manager.getSetting("ShowServerDetails"));

		if (!getSetting1 || !getSetting2) return;

		if (RBXActivityWatcher != null && !RBXActivityWatcher.isShutdown()) {
			RBXActivityWatcher.shutdownNow();
		}

		RBXActivityWatcher = Executors.newSingleThreadExecutor();

		String rbxpath = manager.getRbxPath();

		ActivityWatcher watcher = new ActivityWatcher(getApplicationContext(), rbxpath.concat("appData/logs"), RBXActivityWatcher);
		RBXActivityWatcher.submit(watcher::start);
	}

	public void showStorageFullDialog() {
		MessageboxFragment fragment = getMessageboxFragment3();
		fragment.show(getSupportFragmentManager(), "Messagebox");
	}

	@NonNull
	private MessageboxFragment getMessageboxFragment3() {
		MessageboxFragment fragment = new MessageboxFragment();
		fragment.setMessageText(getString(R.string.StorageFullMessage));
		fragment.setMessageboxListener(new MessageboxFragment.MessageboxListener() {
			@Override
			public void onOkClicked() {
				runOnUiThread(() -> {
					getSupportFragmentManager().beginTransaction().remove(fragment).commit();
					finish();
				});
			}

			@Override
			public void onCancelClicked() {
				// Handle Cancel
				runOnUiThread(() -> {
					getSupportFragmentManager().beginTransaction().remove(fragment).commit();
				});
			}
		});
		return fragment;
	}

	public void AstyleButtonBlack1(LinearLayout button) {
		GradientDrawable drawable = new GradientDrawable();
		drawable.setCornerRadius(5);
		drawable.setStroke(2, Color.parseColor("#0C0F19")); // Stroke color
		drawable.setColor(Color.parseColor("#000000"));     // Fill color
		button.setBackground(drawable);
	}
}
