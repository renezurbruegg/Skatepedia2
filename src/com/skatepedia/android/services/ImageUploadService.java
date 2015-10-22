package com.skatepedia.android.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

import org.apache.commons.net.ftp.FTP;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.skatepedia.android.Functions;
import com.skatepedia.android.SkatepediaFileClient;

public class ImageUploadService extends IntentService {

    public final static String ID = "id";
    public final static String IMAGE = "img";
    public static final String TAG = "tag";
    private Uri img;
    private String markerID;

    public ImageUploadService() {
	super("New Upload Service - skatepedia");

    }

    @Override
    protected void onHandleIntent(Intent intent) {
	Functions.log("Image service", "Got Answer ");
	img = intent.getParcelableExtra(IMAGE);
	markerID = intent.getStringExtra(ID);
	Functions.log("Image service", "Got Answer ID:  " + markerID);
	try {
	    uploadImage(img, markerID);

	} catch (final Exception e) {
	    e.printStackTrace();
	}
    }

    private void uploadImage(Uri img, String id) throws IOException {
	final SkatepediaFileClient mFTPClient = new SkatepediaFileClient();

	mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
	mFTPClient.setFileTransferMode(FTP.BINARY_FILE_TYPE);
	mFTPClient.enterLocalPassiveMode();
	mFTPClient.changeWorkingDirectory("/" + "Map" + "/" + "markers");
	Functions.log("Image service", "Uploading...");
	if (!mFTPClient.changeWorkingDirectory("/" + "Map" + "/" + "markers"
		+ "/" + id)) {
	    mFTPClient.makeDirectory(id);
	    mFTPClient.changeWorkingDirectory("/" + "Map" + "/" + "markers"
		    + "/" + id);
	    Functions.log("upload", " created directory with name " + id);
	}

	Bitmap bitmap;
	try {
	    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),
		    img);

	    final ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    bitmap.compress(CompressFormat.PNG, 0 /* ignored for PNG */, bos);
	    final byte[] bitmapdata = bos.toByteArray();
	    final ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);
	    final Random r = new Random();
	    final boolean result2 = mFTPClient.storeFile(r.nextInt(20)
		    + ".JPEG", bs);
	    Functions.log("Image service", " Result " + result2);

	} catch (final Exception e) {
	    Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
	    Log.d(TAG, "Failed to load", e);
	}

	mFTPClient.disconnect();

    }

}
