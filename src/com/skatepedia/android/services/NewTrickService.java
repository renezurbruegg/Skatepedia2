package com.skatepedia.android.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.util.Log;

import com.skatepedia.android.Functions;
import com.skatepedia.android.NewTrickActivity;
import com.skatepedia.android.SkatepediaFileClient;

public class NewTrickService extends IntentService {

    private String descripton;
    private String difficulty;
    private String kind;
    private String language = "en";
    private String name;
    private ArrayList<Parcelable> photos;
    private ArrayList<String> texts;
    private Uri video;

    public NewTrickService() {
	super("New Trick Service - skatepedia");

    }

    private void createFolder(String name, FTPClient client) {
	try {
	    for (final FTPFile file : client.listFiles())
		if (file.getName().equals(name)) {
		    final Random random = new Random();

		    createFolder(name + random.nextInt(10 + 1), client);
		    return;

		}
	    client.makeDirectory(name);
	    client.changeWorkingDirectory("/NewTricks/" + name);
	} catch (final IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	return;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

	video = intent.getParcelableExtra(NewTrickActivity.URI);
	photos = intent.getParcelableArrayListExtra(NewTrickActivity.PHOTO);
	texts = intent.getStringArrayListExtra(NewTrickActivity.TEXT);
	name = intent.getStringExtra(NewTrickActivity.NAME);
	kind = intent.getStringExtra(NewTrickActivity.KIND);
	descripton = intent.getStringExtra(NewTrickActivity.DESCRIPTION);
	difficulty = intent.getStringExtra(NewTrickActivity.DIFFICULTY);
	if (Locale.getDefault().getDisplayLanguage().equals("Deutsch")) {
	    Log.d("TrickActivity", "Sprache = Deutsch");
	    language = "de";
	}
	try {
	    upload();
	} catch (final Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }

    private int upload() throws Exception {

	Log.d("upload", " in puload");
	final int result = Activity.RESULT_OK;
	final SkatepediaFileClient mFTPClient = new SkatepediaFileClient();

	mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
	mFTPClient.setFileTransferMode(FTP.BINARY_FILE_TYPE);
	mFTPClient.enterLocalPassiveMode();
	mFTPClient.changeWorkingDirectory("/" + "NewTricks");
	createFolder(name, mFTPClient);
	Log.d("upload", " created directory with name " + name);

	uploadTextFile("Name: " + name + "             Descirption : "
		+ descripton + "Kind             " + kind
		+ "             difficulty: " + difficulty, "CONTENTS",
		mFTPClient);

	for (int i = 0; i < photos.size(); i++) {
	    Log.d("files", photos.get(i).toString());
	    final InputStream in = getContentResolver().openInputStream(
		    (Uri) photos.get(i));
	    mFTPClient.storeFile("img_" + (i + 1) + ".JPG", in);
	}

	final InputStream in = getContentResolver().openInputStream(video);
	mFTPClient.storeFile(name + ".mp4", in);

	mFTPClient.makeDirectory(language);
	mFTPClient.changeWorkingDirectory(mFTPClient.printWorkingDirectory()
		+ "/" + language);

	for (int i = 0; i < texts.size(); i++)
	    uploadTextFile(texts.get(i), "img_" + (i + 1), mFTPClient);

	mFTPClient.disconnect();

	final HttpClient httpclient = new DefaultHttpClient();
	final HttpPost httppost = new HttpPost(
		"http://www.skateparkwetzikon.ch/Skatepedia/NewTricks/newTrickEmail.php");

	final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

	try {
	    nameValuePairs.add(new BasicNameValuePair("password",
		    "skatepedia_Mail"));
	    nameValuePairs.add(new BasicNameValuePair("trickName", name));
	    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	    final HttpResponse response = httpclient.execute(httppost);

	    Log.d("response", response + "");
	} catch (final ClientProtocolException e) {
	    // TODO Auto-generated catch block

	    e.printStackTrace();
	} catch (final IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return result;

    }

    private void uploadTextFile(String text, String filename,
	    FTPClient mFTPClient) throws IOException {

	final File f = new File(Functions.mPath.getAbsolutePath() + filename
		+ ".txt");
	@SuppressWarnings("resource")
	final FileWriter fw = new FileWriter(f);
	fw.write(text);
	fw.flush();

	final FileInputStream in = new FileInputStream(f);
	mFTPClient.storeFile(filename + ".txt", in);

	f.delete();
    }

}
