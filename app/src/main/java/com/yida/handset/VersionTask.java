package com.yida.handset;

import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yida.handset.entity.ResultVo;
import com.yida.handset.entity.VersionObject;
import com.yida.handset.entity.VersionResult;

/**
 * Created by gujiao on 2015/11/3.
 */
public class VersionTask extends AsyncTask<Void, Void, Void> {

    public static final String TAG_AUTO = "tag_auto";
    public static final String TAG_MANUAL = "tag_manual";
    public static final String VERSION_CODE = "preference_version_code";
    public static final String VERSION_NAME = "preference_version_name";
    public static final String DOWNLOAD_REFERENCE = "download_reference";
    private static final String TAG_CHECK_UPDATE = "tag_check_update";
    public static final String ACTION_VERSION_CHECKED = "com.yida.handset.action.VERSION_CHECKED";
    public static final String ACTION_APP_DOWNLOADING = "com.yida.handset.action.APP_DOWNLOADING";

    private Context context;
    private String tag;
    private ProgressDialog pd;

    public VersionTask(Context context, String tag) {
        this.context = context;
        this.tag = tag;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (TAG_MANUAL.equals(this.tag)) {
            pd = new ProgressDialog(context);
            pd.setMessage(context.getString(R.string.loading));
            pd.setCanceledOnTouchOutside(false);
            pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    dismiss();
                    RequestQueueSingleton.getInstance(context).getRequestQueue().cancelAll(TAG_CHECK_UPDATE);
                    cancel(true);
                }
            });
            pd.show();
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (TAG_MANUAL.equals(this.tag)) {
            dismiss();
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        dismiss();
        RequestQueueSingleton.getInstance(context).getRequestQueue().cancelAll(TAG_CHECK_UPDATE);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        String mUrl = Constants.HTTP_HEAD + Constants.IP + ":" + Constants.PORT + Constants.SYSTEM_NAME + Constants.CHECK_UPDATE;
        StringRequest checkUpdateRequest = new StringRequest(Request.Method.GET, mUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                VersionResult result = null;
                try {
                    result = gson.fromJson(response, new TypeToken<VersionResult>() {
                        }.getType());
                } catch (Exception e) {
                    dismiss();
                    e.printStackTrace();
                }
                if (result == null) {
                    dismiss();
                    return;
                }
                if (ResultVo.CODE_SUCCESS.equals(result.getCode())) {
                    VersionObject object = result.getObject();
                    if (object == null) {
                        return;
                    }
                    PackageManager pm = context.getPackageManager();
                    try {
                        PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
                        if (info.versionCode < object.getVersionCode()) {
                            SharedPreferences preferences = context.getSharedPreferences(LoginActivity.REFERENCE_NAME, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putInt(VERSION_CODE, object.getVersionCode());
                            editor.putString(VERSION_NAME, object.getVersionName());
                            editor.apply();
                            if (TAG_AUTO.equals(tag)) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle(R.string.update_dialog);
                                builder.setMessage(R.string.you_should_update_your_app);
                                builder.setPositiveButton("去更新", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                                        Uri uri = Uri.parse(Constants.DOWNLOAD_APP);
                                        DownloadManager.Request request = new DownloadManager.Request(uri);
                                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
                                        request.allowScanningByMediaScanner();
                                        long reference = dm.enqueue(request);
                                        SharedPreferences preferences = context.getSharedPreferences(LoginActivity.REFERENCE_NAME, Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putLong(DOWNLOAD_REFERENCE, reference);
                                        editor.apply();
//                                        Intent intent = new Intent(ACTION_APP_DOWNLOADING);
//                                        intent.putExtra(VersionTask.DOWNLOAD_REFERENCE, reference);
//                                        context.sendBroadcast(intent);
                                    }
                                });
                                builder.setNegativeButton("取消", null);
                                Dialog dialog = builder.create();
                                dialog.setCanceledOnTouchOutside(false);
                                dialog.show();
                            } else {
                                Intent intent2 = new Intent(ACTION_VERSION_CHECKED); //改变UpdateActivity的按钮
                                context.sendBroadcast(intent2);
                            }

                        } else {
                            SharedPreferences preferences = context.getSharedPreferences(LoginActivity.REFERENCE_NAME, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putInt(VERSION_CODE, 0);
                            editor.putString(VERSION_NAME, "");
                            editor.apply();
                        }
                        dismiss();
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                } else if(ResultVo.CODE_FAILURE.equals(result.getCode())) {
                    Toast.makeText(context, result.getMessage(), Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                dismiss();
                volleyError.printStackTrace();
            }
        });
        checkUpdateRequest.setTag(TAG_CHECK_UPDATE);

        RequestQueueSingleton.getInstance(context).addToRequestQueue(checkUpdateRequest);
        return null;
    }

    private void dismiss() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }
}
