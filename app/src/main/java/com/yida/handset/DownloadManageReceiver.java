package com.yida.handset;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

/**
 * Created by gujiao on 2015/11/3.
 */
public class DownloadManageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            SharedPreferences preferences = context.getSharedPreferences(LoginActivity.REFERENCE_NAME, Context.MODE_PRIVATE);
            long reference = preferences.getLong(VersionTask.DOWNLOAD_REFERENCE, 0l);

            long referenceDownloaded = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (reference != 0l && referenceDownloaded != -1
                    && reference == referenceDownloaded) {
                DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                Intent install = new Intent(Intent.ACTION_VIEW);
                Uri downloadFileUri = dm.getUriForDownloadedFile(reference);
                install.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
                install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(install);
            }
        }
    }
}
