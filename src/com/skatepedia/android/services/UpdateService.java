package com.skatepedia.android.services;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.skatepedia.android.Functions;
import com.skatepedia.android.SkatepediaFileClient;
import com.skatepedia.android.receivers.UpdateReceiver;

public class UpdateService extends IntentService {

    private class CheckUpdatesTask extends AsyncTask<Void, Void, Integer> {

	@Override
	protected Integer doInBackground(Void... params) {
	    try {

		final URL url = new URL( //
			"http://www.skateparkwetzikon.ch/Skatepedia/skateList.txt"); // Start
										     // Connecton
										     // to
										     // Tricklist
										     // on
										     // Server.
		final File list = new File(Functions.mPath.getAbsolutePath() // alos
									     // to
									     // Tricklist
									     // on
									     // Phone
			+ "/skateList.txt");

		Functions.log("DOWNLOAD",
			"Length:  " + (url.openConnection().getContentLength()) // Log
										// size
				+ "     " + list.length());
		if (url.openConnection().getContentLength() != list.length()) // if
									      // sizeServer
									      // !=
									      // sizeMobile
		    return (RESULT_UPDATES); // return there are updates

		final SkatepediaFileClient mFTPUpdateClient = new SkatepediaFileClient();

		mFTPUpdateClient.setFileType(FTP.BINARY_FILE_TYPE);
		mFTPUpdateClient.enterLocalPassiveMode();
		final FTPFile[] files = clear(mFTPUpdateClient.listFiles());
		Functions.log("DOWNLOAD",
			"files: "
				+ files.length
				+ "   "
				+ clear(Functions.getSkatepediaDir()
					.listFiles()).length);
		if (files.length != clear(Functions.getSkatepediaDir()
			.listFiles()).length)
		    return (RESULT_UPDATES);

	    } catch (final Exception e) {
		final StackTraceElement[] stack = e.getStackTrace();
		for (final StackTraceElement line : stack)
		    Functions.log("Exception at Update Service:   ",
			    line.toString());

	    }
	    return null;

	}

	@Override
	protected void onPostExecute(Integer result) {
	    if (result != null)
		finish(result);

	}

    }

    private class DownloadTask extends AsyncTask<Void, Void, Integer> {

	@Override
	protected Integer doInBackground(Void... params) {
	    try {
		return download();
	    } catch (final Exception e) {
		// TODO Auto-generated catch block
		final StackTraceElement[] stack = e.getStackTrace();
		for (final StackTraceElement line : stack)
		    Functions.log("Exception at Update Service:   ",
			    line.toString());
	    }
	    return 0;
	}

	@Override
	protected void onPostExecute(Integer i) {
	    final Intent sendintent = new Intent(
		    UpdateReceiver.DOWNLOAD_FINISHED);
	    sendBroadcast(sendintent);
	}

    }

    public static String ANSWER_UPDATE = "update";

    public static final int RESULT_UPDATES = 1;

    public UpdateService() {
	super("Service Skatepedia");
	// TODO Auto-generated constructor stub
    }

    private File[] clear(File[] files) {

	final ArrayList<File> list = new ArrayList<File>();

	for (final File ftpFile : files)
	    if ((ftpFile.getName().equals(".")
		    || ftpFile.getName().equals("..")
		    || ftpFile.getName().equals("Map")
		    || ftpFile.getName().equals("NewTricks")
		    || ftpFile.getName().contains(".nomedia") || ftpFile
		    .getName().contains("ErrorLog")) == false)
		list.add(ftpFile);

	final File[] retFiles = new File[list.size()];
	for (int i = 0; i < retFiles.length; i++)
	    retFiles[i] = list.get(i);

	return retFiles;
    }

    private FTPFile[] clear(FTPFile[] files) {

	final ArrayList<FTPFile> list = new ArrayList<FTPFile>();

	for (final FTPFile ftpFile : files)
	    if ((ftpFile.getName().equals(".")
		    || ftpFile.getName().equals("..")
		    || ftpFile.getName().equals("Map")
		    || ftpFile.getName().equals("NewTricks")
		    || ftpFile.getName().contains(".nomedia") || ftpFile
		    .getName().contains("ErrorLog")) == false)
		list.add(ftpFile);

	final FTPFile[] retFiles = new FTPFile[list.size()];
	for (int i = 0; i < retFiles.length; i++)
	    retFiles[i] = list.get(i);

	return retFiles;
    }

    private int download() throws Exception {

	int result = Activity.RESULT_OK;
	final SkatepediaFileClient mFTPClient = new SkatepediaFileClient();

	final FTPFile[] files = clear(mFTPClient.listFiles());

	for (int i = 0; i < files.length; i++) {
	    mFTPClient.changeWorkingDirectory("");

	    if (!files[i].isDirectory()) {
		if (!download(files[i], mFTPClient, "")) {
		    Functions
			    .log("DownloadService",
				    "Schicke file zum Download:  "
					    + files[i].getName());

		    result = Activity.RESULT_CANCELED;
		}
	    } else {

		mFTPClient.changeWorkingDirectory("/" + files[i].getName());

		final FTPFile[] filesInDirect = clear(mFTPClient.listFiles());

		for (int j = 0; j < filesInDirect.length; j++) {
		    Functions.log(
			    "DownloadService",
			    "Schicke file zum Download:  "
				    + filesInDirect[j].getName());
		    if (!filesInDirect[j].isDirectory()) {
			if (!download(filesInDirect[j], mFTPClient, "/"
				+ files[i].getName()))
			    result = Activity.RESULT_CANCELED;
		    } else {
			mFTPClient.changeWorkingDirectory("/"
				+ files[i].getName() + "/"
				+ filesInDirect[j].getName());

			final FTPFile[] filesInDirect2 = clear(mFTPClient
				.listFiles());

			for (int j2 = 0; j2 < filesInDirect2.length; j2++) {
			    Functions.log("DownloadService",
				    "Schicke file zum Download:  "
					    + filesInDirect2[j2].getName());

			    if (!download(filesInDirect2[j2], mFTPClient, "/"
				    + files[i].getName() + "/"
				    + filesInDirect[j].getName()))
				result = Activity.RESULT_CANCELED;

			}
			mFTPClient.changeToParentDirectory();
		    }
		}
		mFTPClient.changeToParentDirectory();
	    }

	}
	return result;

    }

    private boolean download(FTPFile file, FTPClient ftpClient, String Path)
	    throws IOException {

	final File root = android.os.Environment.getExternalStorageDirectory();
	Log.e("DOWNLOADSERVICE", " Habe file:   " + file.getName());
	Log.e("DOWNLOADSERVICE", " und Pfad::   " + Path);
	final File dir = new File(root.getAbsolutePath() + "/skatepedia" + Path);
	if (file.getName().contains("skateList")) {
	    Functions.log("habe skatelist", file.getName());

	    final File list = new File(Functions.getSkatepediaDir()
		    .getAbsolutePath() + "/skateList.txt");
	    list.delete();
	    Functions.log("Update Service",
		    "hab egelöscht  :   " + list.getAbsolutePath());
	}
	if (dir.exists() == false) {
	    dir.mkdirs();
	    Log.e("DOWNLOADSERVICE", " ORdner : " + dir.getAbsolutePath()
		    + " ERSTELLT");
	} else {

	    final File[] files = dir.listFiles();

	    for (final File file2 : files)
		if (file2.getName().equals(file.getName()))
		    return true;

	}
	final OutputStream outputStream1 = new BufferedOutputStream(
		new FileOutputStream(dir.getAbsolutePath() + "/"
			+ file.getName()));
	final boolean success = ftpClient.retrieveFile(file.getName(),
		outputStream1);
	outputStream1.close();

	return success;
    }

    private void finish(int result) {
	Functions.log("finish", result + "  result");
	final Intent intent = new Intent();
	intent.setAction("com.skatepedia.android.UPDATES");
	intent.putExtra(ANSWER_UPDATE, result);
	sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent i) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

	Functions.log("Wuhuuuuuuuuuuuuuuuuuuu",
		" *GÄHN*, Endlich wurde ich geweckt, bei handle intent UpdateService Zeit:  "
			+ new Date().getTime());

	final Boolean download = intent.getBooleanExtra(
		UpdateReceiver.DOWNLOAD, false);
	if (download)
	    new DownloadTask().execute();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
	// TODO Auto-generated method stub
	Functions.log("Wuhuuuuuuuuuuuuuuuuuuu",
		" *GÄHN*, Endlich wurde ich geweckt. UpdateService Zeit:  "
			+ new Date().getTime());

	/*
	 * if(UpdateReceiver.DOWNLOAD==null) finish(startId);
	 */
	try {
	    final Boolean download = intent.getBooleanExtra(
		    UpdateReceiver.DOWNLOAD, false);
	    Functions.log("Update Service", download + "");

	    if (download)
		new DownloadTask().execute();
	    else {
		final CheckUpdatesTask cut = new CheckUpdatesTask();
		cut.execute();
	    }

	} catch (final Exception e) {
	    e.printStackTrace();
	}
	return START_STICKY;

    }

}
