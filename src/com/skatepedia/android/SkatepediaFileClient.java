package com.skatepedia.android;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class SkatepediaFileClient extends FTPClient {
    private final static char[] IP = { '4', '6', '.', '2', '5', '2', '.', '2',
	    '2', '.', '7', '1' };
    private final static char[] PASSWORD = { 's', 'k', 'a', 't', 'e', 'p', 'e',
	    'd', 'i', 'a' };
    private final static int PORT = 21;
    private final static char[] USER = { '3', '3', '2', '9', '1', '1', '-',
	    '2', '2', '9', '-', 's', 'k', 'a', 't', 'e', 'p', 'e', 'd', 'i',
	    'a' };

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
