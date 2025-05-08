package com.chevstrap.rbx;

import android.os.Handler;
import android.os.Looper;

import android.Manifest;
import android.animation.*;
import android.app.*;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.*;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.*;
import org.json.*;

public class MainActivity extends Activity {

	private String rbxpath = "";

	private String AppPackageTOlaunch = "";
	private String data_data = "";
	private String lastClientAppSettings = "";
	private boolean isRoot = false;

	private ScrollView vscroll1;
	private LinearLayout linear5;
	private LinearLayout linear7;
	private Button button1;
	private Button button2;
	private Button button3;
	private TextView textview2;
	private TextView sayhi1;
	private Button button1_2;

	private Intent startIntent = new Intent();
	private Intent teleport = new Intent();
	private AlertDialog.Builder maynotopen;
	private AlertDialog.Builder updateTime;

	private Handler handler = new Handler();

	private boolean isLoading = false;

	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.main);
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
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == 1000) {
			initializeLogic();
		}
	}

	private void initialize(Bundle _savedInstanceState) {
		vscroll1 = findViewById(R.id.vscroll1);
		linear5 = findViewById(R.id.linear5);
		linear7 = findViewById(R.id.linear7);
		button1 = findViewById(R.id.button1);
		button1_2 = findViewById(R.id.button1_2);
		button2 = findViewById(R.id.button2);
		button3 = findViewById(R.id.button3);
		textview2 = findViewById(R.id.textview2);
		sayhi1 = findViewById(R.id.sayhi1);
		maynotopen = new AlertDialog.Builder(this);
		updateTime = new AlertDialog.Builder(this);

		String packageName = "com.roblox.client";
		try {
			Context context = getApplicationContext();
			ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName, 0);
			String dataDir = info.dataDir;

			// Toast.makeText(context, "Data dir: " + dataDir, Toast.LENGTH_LONG).show();
		} catch (PackageManager.NameNotFoundException e) {

			// Toast.makeText(getApplicationContext(), "Package not found: " + packageName, Toast.LENGTH_SHORT).show();
		}

		button1.setOnClickListener(_view -> {
			maynotopen.setTitle(getString(R.string.Message1));
			maynotopen.setMessage(getString(R.string.JustAMessageFastFlagNotLoaded));
			maynotopen.setPositiveButton(getString(R.string.launch_roblox), (_dialog, _which) -> {
				_getDataStorage();
				AppPackageTOlaunch = "com.roblox.client";
				rbxpath = data_data.replace(getPackageName(), "com.roblox.client");

				// Perform file operations in the background thread
				new Thread(() -> {
					if (!FileUtil.isExistFile(rbxpath)) {
						FileUtil.makeDir(rbxpath);
					}

					isRoot = fileStuff.isDirectoryAccessible(rbxpath);
					if (!isRoot) {
						runOnUiThread(() -> RootAccessDialog.show(MainActivity.this));
					} else {
						runOnUiThread(() -> showLoadingDialog());
					}
				}).start();
			});
			maynotopen.setNegativeButton(getString(R.string.Cancel), null);
			maynotopen.create().show();
		});

		button1_2.setOnClickListener(_view -> {
			maynotopen.setTitle(getString(R.string.Message1));
			maynotopen.setMessage(getString(R.string.JustAMessageFastFlagNotLoaded));
			maynotopen.setPositiveButton(getString(R.string.launch_robloxvn), (_dialog, _which) -> {
				_getDataStorage();
				AppPackageTOlaunch = "com.roblox.client.vnggames";
				rbxpath = data_data.replace(getPackageName(), "com.roblox.client.vnggames");

				// Perform file operations in the background thread
				new Thread(() -> {
					if (!FileUtil.isExistFile(rbxpath)) {
						FileUtil.makeDir(rbxpath);
					}

					isRoot = fileStuff.isDirectoryAccessible(rbxpath);
					if (!isRoot) {
						runOnUiThread(() -> RootAccessDialog.show(MainActivity.this));
					} else {
						runOnUiThread(() -> showLoadingDialog());
					}
				}).start();
			});
			maynotopen.setNegativeButton(getString(R.string.Cancel), null);
			maynotopen.create().show();
		});

		button2.setOnClickListener(_view -> {
			teleport.setClass(getApplicationContext(), SethereActivity.class);
			startActivity(teleport);
		});

		button3.setOnClickListener(_view -> {
			teleport.setClass(getApplicationContext(), AboutActivity.class);
			startActivity(teleport);
		});
	}

	private void showLoadingDialog() {
		final AlertDialog loadingDialog = new AlertDialog.Builder(MainActivity.this).create();
		View loadingDialogCV = getLayoutInflater().inflate(R.layout.loading, null);
		loadingDialog.setView(loadingDialogCV);
		loadingDialog.setCancelable(false);

		final ImageView imageViewLoadingSpin = loadingDialogCV.findViewById(R.id.imageview_loadingspin);
		final Button cancelButton = loadingDialogCV.findViewById(R.id.cancel_button);
		final TextView textViewLoadingStatus = loadingDialogCV.findViewById(R.id.texview_loadingstatus);

		cancelButton.setBackground(new GradientDrawable() {{
			setCornerRadius(20);
			setStroke(5, 0xFF050505);
			setColor(Color.TRANSPARENT);
		}});


		textViewLoadingStatus.setText(" ... ");
		loadingDialog.show();

		cancelButton.setOnClickListener(_v -> {
			isLoading = false;

			loadingDialog.dismiss();
		});

		// Perform file operation and apply settings in the background
		new Thread(() -> {
			String clientSettingsPath = rbxpath.concat("exe/ClientSettings/ClientAppSettings.json");
			String sourceSettingsPath = FileUtil.getExternalStorageDir().concat("/Chevstrap/Modifications/ClientSettings/ClientAppSettings.json");

			// Check if the files are different and need to be applied
			if (!FileUtil.readFile(sourceSettingsPath).equals(FileUtil.readFile(clientSettingsPath))) {
				FileUtil.writeFile(clientSettingsPath, FileUtil.readFile(sourceSettingsPath));
				runOnUiThread(() -> {
					cancelButton.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/arial.ttf"));
					textViewLoadingStatus.setText("Applying Roblox fast flags...");
				});
			}

			// Continue after applying settings (5 seconds delay simulation)
			try {
				Thread.sleep(5000);  // Wait for 5 seconds before starting Roblox
				runOnUiThread(() -> {
					textViewLoadingStatus.setText("Starting Roblox ... ");
					new Thread(() -> {
						try {
							Thread.sleep(5000);  // Wait for Roblox to start
							runOnUiThread(() -> {
								Intent launchIntent = getPackageManager().getLaunchIntentForPackage(AppPackageTOlaunch);
								if (launchIntent != null) {
									loadingDialog.dismiss();
									startActivity(launchIntent);
								} else {
									textViewLoadingStatus.setText("Failed to launch Roblox");
								}
							});
						} catch (InterruptedException e) {
							//e.printStackTrace();
						}
					}).start();
				});
			} catch (InterruptedException e) {
				//Log.e("","",  e);
			}
		}).start();
	}




	private void initializeLogic() {
		Typeface arial = Typeface.createFromAsset(getAssets(), "fonts/arial.ttf");

		textview2.setTypeface(arial);
		button1.setTypeface(arial);
		button2.setTypeface(arial);
		button3.setTypeface(arial);

		button1.setBackground(new GradientDrawable() {{
			setCornerRadius(20);
			setColor(0xFF090909);
		}});
		button2.setBackground(new GradientDrawable() {{
			setCornerRadius(20);
			setColor(0xFF090909);
		}});
		button3.setBackground(new GradientDrawable() {{
			setCornerRadius(20);
			setColor(0xFF090909);
		}});

		button3.setText(getString(R.string.Aboutt));
		sayhi1.setText(getString(R.string.SayHi));

		_getDataStorage();
		rbxpath = data_data.replace(getPackageName(), "com.roblox.client");

		String basePath = FileUtil.getExternalStorageDir().concat("/Chevstrap");
		if (!FileUtil.isExistFile(basePath)) FileUtil.makeDir(basePath);
		if (!FileUtil.isExistFile(basePath + "/Logs")) FileUtil.makeDir(basePath + "/Logs");
		if (!FileUtil.isExistFile(basePath + "/Modifications")) FileUtil.makeDir(basePath + "/Modifications");

		StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
		long blockSize = stat.getBlockSizeLong();
		long free = (stat.getAvailableBlocksLong() * blockSize) / (1024 * 1024 * 1024);

		if (free < 0.5) {
			StorageFullDialog.show(this);
		} else {
			new Thread(() -> {
				try {
					aboutApp appInfo = new aboutApp(MainActivity.this);

					String latestVersion = appInfo.getLatestVersion();
					String currentVersion = appInfo.getAppVersion();

					runOnUiThread(() -> {
						try {
							// Avoid crashes on closed or backgrounded activity
							if (isFinishing() || isDestroyed()) return;

							if (!latestVersion.equals(currentVersion) && !latestVersion.equals("no")) {
								AlertDialog.Builder updateTime = new AlertDialog.Builder(MainActivity.this);
								updateTime.setTitle(getString(R.string.Message1));
								updateTime.setMessage(getString(R.string.PleaseUpdate));
								updateTime.setPositiveButton(getString(R.string.Update), (_dialog, _which) -> {
									try {
										Intent browserIntent = new Intent(Intent.ACTION_VIEW,
												Uri.parse("https://github.com/FrosSky/Chevstrap/releases/latest"));
										startActivity(browserIntent);
									} catch (Exception e) {
										Log.e("UpdateIntent", "Failed to open browser", e);
										Toast.makeText(MainActivity.this, "Failed to open browser", Toast.LENGTH_SHORT).show();
									}
								});
								updateTime.setNegativeButton(getString(R.string.Cancel), null);
								updateTime.setCancelable(false);
								updateTime.create().show();
							}
						} catch (Exception e) {
							Log.e("UpdateDialog", "Error showing update dialog", e);
							Toast.makeText(this, "Failed to show update dialog", Toast.LENGTH_SHORT).show();
						}
					});

				} catch (Exception e) {
					Log.e("VersionCheck", "Error during version check", e);
					Toast.makeText(this, "Version check failed", Toast.LENGTH_SHORT).show();
				}
			}).start();
		}

	}

	public void _getDataStorage() {
		data_data = getFilesDir().getAbsolutePath() + "/";
	}
}
