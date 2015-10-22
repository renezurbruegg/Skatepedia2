package com.skatepedia.android;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class SkatepediaFileClient extends FTPClient {
    private final static char[] IP = { /* Censored */ };
    private final static char[] PASSWORD = { /* Censored */};
    private final static int PORT = 21;
    private final static char[] USER = { /* Censored */};

    public SkatepediaFileClient() {
	super();
	try {

	    connect(new String(IP), PORT);
	    if (FTPReply.isPositiveCompletion(getReplyCode())) {
		login(new String(USER), new String(PASSWORD));
		setFileType(FTP.BINARY_FILE_TYPE);
		enterLocalPassiveMode();
	    }
	} catch (final Exception e) {
	    e.printStackTrace();
	}
    }

}
