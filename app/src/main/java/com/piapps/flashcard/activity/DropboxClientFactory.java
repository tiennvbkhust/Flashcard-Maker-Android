package com.piapps.flashcard.activity;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.http.OkHttp3Requestor;
import com.dropbox.core.http.StandardHttpRequestor;
import com.dropbox.core.v2.DbxClientV2;

/**
 * Created by abduaziz on 9/16/17.
 */


public class DropboxClientFactory {

    public static DbxClientV2 getClient(String ACCESS_TOKEN) {
        // Create Dropbox client
        DbxRequestConfig requestConfig = DbxRequestConfig.newBuilder("Flashcard Maker")
                .withHttpRequestor(new OkHttp3Requestor(OkHttp3Requestor.defaultOkHttpClient()))
                .build();
        DbxClientV2 client = new DbxClientV2(requestConfig, ACCESS_TOKEN);
        return client;
    }
}
