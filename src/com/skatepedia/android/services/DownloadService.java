package com.skatepedia.android.services;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.skatepedia.android.Functions;
import com.skatepedia.android.SkatepediaFileClient;

public class DownloadService extends IntentService {
    public static final String MODE = "mode";
    public static final int MODE_DOWNLOAD = 2;
    public static final int MODE_GETUPDATES = 5;
    public static final int MODE_STREAM = 3;

    private static final int MODE_UPDATE = 1;
    public static final String NOTIFICATION = "com.skatepedia.android";
    public static String RESULT = "RESULT";
    public static int RESULT_UPDATE_BAR = 6;
    public static final int RESULT_UPDATES = 4;
    public static String UPDATE_VALUE = "updateValue";

    public static FTPFile[] clear(FTPFile[] files) {

	final ArrayList<FTPFile> list = new ArrayList<FTPFile>();

	for (final FTPFile ftpFile : files)
	    if ((ftpFile.getName().equals(".")
		    || ftpFile.getName().equals("..")
		    || ftpFile.getName().equals("Map") || ftpFile.getName()
		    .equals("NewTricks")) == false)
		list.add(ftpFile);

	final FTPFile[] retFiles = new FTPFile[list.size()];
	for (int i = 0; i < retFiles.length; i++)
	    retFiles[i] = list.get(i);

	return retFiles;
    }

    public DownloadService() {
	super("download Service - skatepedia");

    }

    private int download() throws Exception {

	int result = Activity.RESULT_OK;
	final FTPClient mFTPClient = new FTPClient();

	mFTPClient.connect("46.252.22.71", 21);

	if (FTPReply.isPositiveCompletion(mFTPClient.getReplyCode())) {
	    // login using username & password

	    mFTPClient.login("332911-229-skatepedia", "skatepedia");

	    /*
	     * Set File Transfer Mode
	     * 
	     * To avoid corruption issue you must specified a correct transfer
	     * mode, such as ASCII_FILE_TYPE, BINARY_FILE_TYPE, EBCDIC_FILE_TYPE
	     * .etc. Here, I use BINARY_FILE_TYPE for transferring text, image,
	     * and compressed files.
	     */
	    mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
	    mFTPClient.enterLocalPassiveMode();
	    final FTPFile[] files = clear(mFTPClient.listFiles());
	    final float step = (100 / files.length);
	    for (int i = 0; i < files.length; i++) {
		updateProgressBar((i + 1) * step);
		mFTPClient.changeWorkingDirectory("");

		if (!files[i].isDirectory()) {
		    if (!download(files[i], mFTPClient, "")) {
			Log.d("DownloadService", "Schicke file zum Download:  "
				+ files[i].getName());

			result = Activity.RESULT_CANCELED;
		    }
		} else {

		    mFTPClient.changeWorkingDirectory("/" + files[i].getName());

		    final FTPFile[] filesInDirect = clear(mFTPClient
			    .listFiles());
		    final float step2 = (step / filesInDirect.length);
		    for (int j = 0; j < filesInDirect.length; j++) {
			updateProgressBar((i + 1) * step + (j + 1) * step2);
			Log.d("DownloadService", "Schicke file zum Download:  "
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
			    final float step3 = (step2 / filesInDirect2.length);

			    for (int j2 = 0; j2 < filesInDirect2.length; j2++) {
				updateProgressBar((i + 1) * step + (j + 1)
					* step2 + (j2 + 1) * step3);
				Log.d("DownloadService",
					"Schicke file zum Download:  "
						+ filesInDirect2[j2].getName());

				if (!download(filesInDirect2[j2], mFTPClient,
					"/" + files[i].getName() + "/"
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
	} else
	    return Activity.RESULT_CANCELED;

    }

    private boolean download(FTPFile file, FTPClient ftpClient, String Path)
	    throws IOException {

	final File root = android.os.Environment.getExternalStorageDirectory();
	Log.e("DOWNLOADSERVICE", " Habe file:   " + file.getName());
	Log.e("DOWNLOADSERVICE", " und Pfad::   " + Path);
	final File dir = new File(root.getAbsolutePath() + "/skatepedia" + Path);

	if (dir.exists() == false) {
	    dir.mkdirs();
	    final File f = new File(dir.getAbsoluteFile() + "/.nomedia");
	    f.createNewFile();
	    Log.e("DOWNLOADSERVICE", " ORdner : " + dir.getAbsolutePath()
		    + " ERSTELLT");
	} else if (dir.isDirectory()) {

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
	final Intent intent = new Intent(NOTIFICATION);
	intent.putExtra(RESULT, result);
	sendBroadcast(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
	int result = Activity.RESULT_OK;

	if (intent.getIntExtra(MODE, 0) == MODE_UPDATE) {
	    try {

		final URL url = new URL(
			"http://www.skateparkwetzikon.ch/Skatepedia/skateList.txt");
		final File list = new File(Functions.mPath.getAbsolutePath()
			+ "/skateList.txt");

		Log.d("DOWNLOAD",
			"path   " + (url.openConnection().getContentLength())
				+ "     " + list.length());
		if (url.getFile().length() != list.length())
		    finish(DownloadService.RESULT_UPDATES);

		final SkatepediaFileClient mFTPUpdateClient = new SkatepediaFileClient();

		/*
		 * Set File Transfer Mode
		 * 
		 * To avoid corruption issue you must specified a correct
		 * transfer mode, such as ASCII_FILE_TYPE, BINARY_FILE_TYPE,
		 * EBCDIC_FILE_TYPE .etc. Here, I use BINARY_FILE_TYPE for
		 * transferring text, image, and compressed files.
		 */
		mFTPUpdateClient.setFileType(FTP.BINARY_FILE_TYPE);
		mFTPUpdateClient.enterLocalPassiveMode();
		final FTPFile[] files = clear(mFTPUpdateClient.listFiles());
		Log.d("DOWNLOAD", "files: " + files.length + "   "
			+ Functions.getSkatepediaDir().listFiles().length);
		if (files.length != Functions.getSkatepediaDir().listFiles().length) {
		    Log.d("DOWNLOAD", "files: " + files.length + "   "
			    + Functions.getSkatepediaDir().listFiles().length);
		    finish(DownloadService.RESULT_UPDATES);

		}
	    } catch (final Exception e) {
	    }
	    finish(result);

	} else if (intent.getIntExtra(MODE, 0) == MODE_STREAM) {
	    try {
		Functions.saveTrickFileOnPhone();
	    } catch (final IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		result = Activity.RESULT_CANCELED;
	    }
	    finish(result);
	} else if (intent.getIntExtra(MODE, 0) == MODE_GETUPDATES) {
	    int answer = Activity.RESULT_CANCELED;
	    try {

		final URL url = new URL(
			"http://www.skateparkwetzikon.ch/Skatepedia/skateList.txt");
		final File list = new File(Functions.mPath.getAbsolutePath()
			+ "/skateList.txt");

		if (url.getFile().length() != list.length())
		    Functions.saveTrickFileOnPhone();

		try {
		    answer = download();
		} catch (final Exception e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}

	    } catch (final Exception e) {
	    }
	    finish(answer);

	} else if (intent.getIntExtra(MODE, 0) == MODE_DOWNLOAD) {
	    int answer = Activity.RESULT_CANCELED;
	    try {
		answer = download();
	    } catch (final Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    finish(answer);

	}

    }

    private void updateProgressBar(float progress) {
	final Intent intent = new Intent(NOTIFICATION);
	intent.putExtra(RESULT, RESULT_UPDATE_BAR);
	intent.putExtra(UPDATE_VALUE, (int) progress);
	sendBroadcast(intent);

    }

}
