package com.chevstrap.rbx;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Handler;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.*;
import android.graphics.Typeface;
import android.graphics.drawable.*;
import android.net.Uri;
import android.os.*;
import android.view.View;
import android.widget.*;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

import android.os.Bundle;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipFile;

import android.app.Notification;
import android.app.NotificationManager;

import android.os.Build;
import android.app.NotificationChannel;

import androidx.annotation.NonNull;

import org.json.JSONObject;

public class MainActivity extends Activity {
	private ExecutorService RBXActivityWatcher;
	private String rbxpath = "";
	private String latestLogName = "";
	private String lastJobId = null;
	private String lastPlaceId = null;

	private String UniverseId1 = "";
	private String appPackageTolaunch = "";
	private String data_data = "";
	private File LLLLFile = null;
	private ScrollView vscroll1;
	private LinearLayout linear5;
	private LinearLayout linear7;
	private Button button1;
	private Button button2;
	private Button button3;
	private TextView textview2;
	private TextView sayhi1;
	private Button button1_2;
	private AlertDialog currentDialogLastServer = null;

	private final Intent teleport = new Intent();
	private AlertDialog.Builder maynotopen;
	private AlertDialog.Builder updateTime;
	private Handler handler = new Handler();
	private boolean isLoading = false;

	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.main);
		initialize(_savedInstanceState);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
				requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1002);
			}
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationChannel channel = new NotificationChannel(
					"rbx_connection_channel",
					"RBX Connection Notifications",
					NotificationManager.IMPORTANCE_DEFAULT
			);
			channel.setDescription("Shows notifications when connected to a Roblox server");

			NotificationManager notificationManager = getSystemService(NotificationManager.class);
			if (notificationManager != null) {
				notificationManager.createNotificationChannel(channel);
			}

		}

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
	public File getLLLLFile() {
		return LLLLFile;
	}

	public void setLLLLFile(File LLLLFile) {
		this.LLLLFile = LLLLFile;
	}

	public void copyLLLL() {
		File targetDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "Chevstrap/Logs");
		File latestLog = new File(String.valueOf(getLLLLFile()));

		if (!targetDir.exists()) {
			boolean dirCreated = targetDir.mkdirs();
			if (!dirCreated) {
				return;
			}
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			try {
				File destinationFile = new File(targetDir, "lastRBX.txt");
				Files.copy(latestLog.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException ignored) {
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (RBXActivityWatcher != null && !RBXActivityWatcher.isShutdown()) {
			RBXActivityWatcher.shutdownNow();
			NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			if (notificationManager != null) {
				notificationManager.cancel(1001);
			}
			copyLLLL();
        }
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == 1000) {
			initializeLogic();
		}
	}
	public boolean rbxIsLibFolderExisted() {
		try {
			PackageManager pm = getPackageManager();
			ApplicationInfo ai = pm.getApplicationInfo(appPackageTolaunch, 0);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				try (ZipFile zipFile = new ZipFile(ai.sourceDir)) {
					return zipFile.stream().noneMatch(entry -> entry.getName().startsWith("lib/"));
				}
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	public void LaunchRBX(String PackageLaunch) {
		maynotopen.setTitle(getString(R.string.Message1));
		maynotopen.setMessage(getString(R.string.JustAMessageFastFlagNotLoaded));
		maynotopen.setPositiveButton(getString(R.string.launch_roblox), (_dialog, _which) -> {
			appPackageTolaunch = PackageLaunch;
			String Hi_rbx_path = "";

			try {
				Context context = getApplicationContext();
				ApplicationInfo info = context.getPackageManager().getApplicationInfo(appPackageTolaunch, 0);

				Hi_rbx_path = info.dataDir;
				rbxpath = info.dataDir + "/files/";
			} catch (PackageManager.NameNotFoundException e) {
				Toast.makeText(getApplicationContext(), "Roblox folder directory not found or It's blocked", Toast.LENGTH_SHORT).show();
			}

			// Perform file operations in the background thread
			File finalHi_rbx_path = new File(Hi_rbx_path);
			new Thread(() -> {
				if (!FileUtil.isExistFile(rbxpath)) {
					FileUtil.makeDir(rbxpath);
				}

				boolean isRoot = finalHi_rbx_path.exists();
				if (!isRoot) {
					runOnUiThread(() -> RootAccessDialog.show(MainActivity.this));
				} else {
					runOnUiThread(this::showLoadingDialog);
				}
			}).start();
		});
		maynotopen.setNegativeButton(getString(R.string.Cancel), null);
		maynotopen.create().show();
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

		button1.setOnClickListener(_view -> {
			appPackageTolaunch = "com.roblox.client";
			LaunchRBX(appPackageTolaunch);
		});

		button1_2.setOnClickListener(_view -> {
			appPackageTolaunch = "com.roblox.client.vnggames";
			LaunchRBX(appPackageTolaunch);
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




	@SuppressLint("SetTextI18n")
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
			String sourceSettingsPath = Environment.getExternalStorageDirectory().getAbsolutePath().concat("/Chevstrap/Modifications/ClientSettings/ClientAppSettings.json");

            // Check if the files are different and need to be applied
			if (!FileUtil.readFile(sourceSettingsPath).equals(FileUtil.readFile(clientSettingsPath))) {
				boolean hasLibFolder = rbxIsLibFolderExisted();

				if (hasLibFolder) {
					FileUtil.writeFile(clientSettingsPath, FileUtil.readFile(sourceSettingsPath));

					runOnUiThread(() -> {
						cancelButton.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/arial.ttf"));
						textViewLoadingStatus.setText("Applying Roblox fast flags...");
					});
				} else {
					runOnUiThread(() -> {
						textViewLoadingStatus.setText("Your Roblox may have been modified & Stopped the Task");
					});
				}
			}

			if (rbxIsLibFolderExisted()) {
				try {
					Thread.sleep(5000);  // Wait for 5 seconds before starting Roblox
					runOnUiThread(() -> {
						textViewLoadingStatus.setText("Starting Roblox ... ");
						new Thread(() -> {
							try {
								Thread.sleep(5000);  // Wait for Roblox to start
								runOnUiThread(() -> {
									Intent launchIntent = getPackageManager().getLaunchIntentForPackage(appPackageTolaunch);

									if (launchIntent != null) {
										loadingDialog.dismiss();
										_catchRBXplace();
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
			}
		}).start();
	}

	private void initializeLogic() {
		Typeface arial = Typeface.createFromAsset(getAssets(), "fonts/arial.ttf");

		textview2.setTypeface(arial);
		button1.setTypeface(arial);
		button1_2.setTypeface(arial);
		button2.setTypeface(arial);
		button3.setTypeface(arial);

		button1.setBackground(new GradientDrawable() {{
			setCornerRadius(20);
			setColor(0xFF090909);
		}});
		button1_2.setBackground(new GradientDrawable() {{
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

		String basePath = Environment.getExternalStorageDirectory().getAbsolutePath().concat("/Chevstrap");
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
										Toast.makeText(MainActivity.this, "Failed to open browser", Toast.LENGTH_SHORT).show();
									}
								});
								updateTime.setNegativeButton(getString(R.string.Cancel), null);
								updateTime.setCancelable(false);
								updateTime.create().show();
							}
						} catch (Exception e) {
							Toast.makeText(this, "Failed to show update dialog", Toast.LENGTH_SHORT).show();
						}
					});

				} catch (Exception e) {
					Toast.makeText(this, "Version check failed", Toast.LENGTH_SHORT).show();
				}
			}).start();
		}

	}

	public void _getDataStorage() {
		data_data = getFilesDir().getAbsolutePath() + "/";
	}

	private void fetchIPInfoIo(String ip, IPInfoCallback callback) {
		new Thread(() -> {
			try {
				URL url = new URL("https://ipinfo.io/" + ip + "/json");
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(connection.getInputStream()));
				StringBuilder result = new StringBuilder();
				String line;

				while ((line = reader.readLine()) != null) {
					result.append(line);
				}

				reader.close();

				JSONObject json = new JSONObject(result.toString());

				// Send result to callback on main thread
				new Handler(Looper.getMainLooper()).post(() -> {
					callback.onSuccess(json);
				});

			} catch (Exception e) {
				new Handler(Looper.getMainLooper()).post(() -> {
					callback.onError(e);
				});
			}
		}).start();
	}

    //public String getUniverseId1() {
    //    return UniverseId1;
    //}

    //public void setUniverseId1(String universeId1) {
	//   UniverseId1 = universeId1;
    //}

    public interface IPInfoCallback {
		void onSuccess(JSONObject json);
		void onError(Exception e);
	}

	public void fetchRBXGameInfo(String universeId, Callback1 callback) {
		new Thread(() -> {
			String result;
			try {
				BufferedReader reader = getBufferedReader(universeId);
				StringBuilder response = new StringBuilder();
				String line;

				while ((line = reader.readLine()) != null)
					response.append(line);

				reader.close();
				result = response.toString();
			} catch (Exception e) {
				result = "Error: " + e.getMessage();
			}

			// Return result via callback on UI thread
			String finalResult = result;
			new Handler(Looper.getMainLooper()).post(() -> callback.onResult(finalResult));
		}).start();
	}

	@NonNull
	private static BufferedReader getBufferedReader(String universeId) throws IOException {
		String urlString = "https://games.roblox.com/v1/games?universeIds=" + universeId;
		URL url = new URL(urlString);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");

		int status = connection.getResponseCode();
		InputStream inputStream = (status >= 200 && status < 300)
				? connection.getInputStream()
				: connection.getErrorStream();

        return new BufferedReader(new InputStreamReader(inputStream));
	}

	public void showLastServerDialog(String placeIddd, String instanceId) {
		// Dismiss the existing dialog if it's showing
		if (currentDialogLastServer != null && currentDialogLastServer.isShowing()) {
			currentDialogLastServer.dismiss();
		}

		String deeplink = "roblox://experiences/start?placeId=" + placeIddd + "&gameInstanceId=" + instanceId;
		String webLink = "https://www.roblox.com/games/start?placeId=" + placeIddd + "&gameInstanceId=" + instanceId;

		currentDialogLastServer = new AlertDialog.Builder(this)
				.setTitle("Message")
				.setMessage("Looks like you left the server, I caught your last server")
				.setCancelable(false)
				.setPositiveButton("Copy Deeplink", (dialog, id) -> {
					try {
						ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
						ClipData clip = ClipData.newPlainText("Roblox Server Link", webLink);
						clipboard.setPrimaryClip(clip);
						Toast.makeText(this, "Successfully copied to clipboard", Toast.LENGTH_SHORT).show();
					} catch (Exception e) {
						Toast.makeText(this, "Failed to copy to clipboard", Toast.LENGTH_SHORT).show();
					}
					dialog.dismiss();
				})
				.setNegativeButton("Cancel", (dialog, id) -> dialog.cancel())
				.create();

		currentDialogLastServer.show();  // Show the dialog
	}

	public interface Callback1 {
		void onResult(String data);
	}

	public void _catchRBXplace() {
		if (RBXActivityWatcher != null && !RBXActivityWatcher.isShutdown()) {
			RBXActivityWatcher.shutdownNow();
			try {
				boolean terminated = RBXActivityWatcher.awaitTermination(2, TimeUnit.SECONDS);
				if (!terminated) {
					// Handle the case where the shutdown didn't complete in time
					System.err.println("Timeout occurred while waiting for shutdown.");
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}


		RBXActivityWatcher = Executors.newSingleThreadExecutor();

		RBXActivityWatcher.submit(() -> {
			String gameJoiningEntry = "[FLog::Output] ! Joining game";
			String connectionAcceptedEntry = "[FLog::Output] Connection accepted from";
			String robloxLogsPath = rbxpath.concat("appData/logs");
			File logDir = new File(robloxLogsPath);

            if (!logDir.exists() || !logDir.isDirectory()) {
				runOnUiThread(() -> Toast.makeText(MainActivity.this, "Roblox logs folder not found", Toast.LENGTH_LONG).show());
				return;
			}

			File[] logsList = logDir.listFiles();
			if (logsList == null || logsList.length == 0) {
				runOnUiThread(() -> Toast.makeText(MainActivity.this, "No logs inside folder", Toast.LENGTH_LONG).show());
				return;
			}

			Arrays.sort(logsList, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
			File latestLog = logsList[0];

			try (RandomAccessFile raf = new RandomAccessFile(latestLog, "r")) {
				long filePointer = raf.length();
				long lastModified = latestLog.lastModified();

				boolean foundJoinLine = false;

				while (!Thread.currentThread().isInterrupted()) {
					if (latestLog.lastModified() != lastModified || latestLog.length() > filePointer) {
						lastModified = latestLog.lastModified();
						raf.seek(filePointer);
						String line;

						//if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
							latestLogName = latestLog.getName();
							setLLLLFile(latestLog);
							//break;

						while ((line = raf.readLine()) != null) {
							if (line.contains("[FLog::Network] NetworkClient:Remove")) {
								runOnUiThread(() -> {
									NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
									if (notificationManager != null) {
										notificationManager.cancel(1001);
									}
									showLastServerDialog(lastPlaceId, lastJobId);
								});
							} else if (!foundJoinLine && line.contains(gameJoiningEntry)) {
								// Find the game ID (inside single quotes)
								int gameStartIndex = line.indexOf("'") + 1;  // Start after the first quote
								int gameEndIndex = line.indexOf("'", gameStartIndex);  // End at the second quote
								if (gameStartIndex > 0 && gameEndIndex > gameStartIndex) {
									lastJobId = "'" + line.substring(gameStartIndex, gameEndIndex) + "'";  // Extract the game ID
								}

								// Find the place ID (after the word "place")
								int placeIdStartIndex = line.indexOf("place ") + "place ".length();
								int placeIdEndIndex = line.indexOf(" at", placeIdStartIndex);
								if (placeIdStartIndex > 0 && placeIdEndIndex > placeIdStartIndex) {
									lastPlaceId = line.substring(placeIdStartIndex, placeIdEndIndex);
								}

								// Check if both IDs are found
								if (lastJobId != null && lastPlaceId != null) {
									foundJoinLine = true;  // Mark that both IDs have been found

						}

							//} else if (line.contains("[FLog::GameJoinLoadTime] Report game_join_loadtime:")) {
								//String[] parts = line.split(",");
								//for (String part : parts) {
								//	part = part.trim();
								//	if (part.startsWith("universeid:")) {
								//		setUniverseId1(part.substring("universeid:".length()));
								//	break;
								//	}
								//}
							} else if (foundJoinLine && line.contains(connectionAcceptedEntry)) {
								String[] parts = line.split(" ");
								String ipPort = parts[parts.length - 1];
								String[] ipSplit = ipPort.split("\\|");

								if (ipSplit.length == 2) {
									String ip = ipSplit[0];

									runOnUiThread(() -> {
										Notification.Builder builder;
										if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
											builder = new Notification.Builder(MainActivity.this, "rbx_connection_channel");
										} else {
											builder = new Notification.Builder(MainActivity.this);
										}

										fetchIPInfoIo(ip, new IPInfoCallback() {
											@Override
											public void onSuccess(JSONObject json) {
												try {
													String city = json.getString("city");
													String region = json.getString("region");
													String country = json.getString("country");

													String result = "Located at: " + city + ", " + region + ", " + country;

													builder.setSmallIcon(R.drawable.ic_clear_black)
															.setContentTitle("Connected to server")
															.setContentText(result)
															.setAutoCancel(true);

													NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
													if (notificationManager != null) {
														notificationManager.notify(1001, builder.build());
													}
												} catch (Exception ignored) {
												}
											}

											@Override
											public void onError(Exception e) {
												builder.setSmallIcon(R.drawable.ic_clear_black)
														.setContentTitle("Connected to server")
														.setContentText("Failed")
														.setAutoCancel(true);

												NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
												if (notificationManager != null) {
													notificationManager.notify(1001, builder.build());
												}
											}
										});
									});
								}

								foundJoinLine = false;
							}
						}

						filePointer = raf.getFilePointer();
					}

					try {
						Thread.sleep(1000); // Delay between reads
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						break;
					}
				}

			} catch (Exception e) {
				runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error reading log: " + e.getMessage(), Toast.LENGTH_LONG).show());
			}
		});
	}
}
