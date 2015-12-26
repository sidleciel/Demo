package com.rhodes.demo.activity;

    import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.*;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import com.rhodes.demo.R;
import com.rhodes.demo.Util.Logger;
import com.rhodes.demo.Util.StringUtils;
import com.rhodes.demo.Util.ToastUtils;

import java.io.Serializable;

/**
 * Created by xiet on 2015/12/8.
 */
public class VideoPlayerActivity extends Activity {
    private String TAG = getClass().getSimpleName();

    private WebView     mWebView;
    private FrameLayout video_fullView;// 全屏时视频加载view
    private View        xCustomView;
    private ProgressDialog waitdialog = null;
    private WebChromeClient.CustomViewCallback xCustomViewCallback;
    private MyWebChromeClient                  xwebchromeclient;

    VideoInfo videoInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉应用标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.video_player_activity);
        mWebView = (WebView) findViewById(R.id.mWebView);
        video_fullView = (FrameLayout) findViewById(R.id.video_fullView);

        initData();
        initViews();
    }

    private void initData() {
        if (videoInfo == null) {
            videoInfo = new VideoInfo();
            videoInfo.setMime("video/mp4");
            videoInfo.setUrl("http://7xozte.com1.z0.glb.clouddn.com/devNexus%206-%20Space%20to%20explore.mp4");
        }
    }

    private void initViews() {
        if (videoInfo == null || StringUtils.isEmpty(videoInfo.getUrl())) {
            finish();
            return;
        }

        waitdialog = new ProgressDialog(this);
        waitdialog.setTitle("提示");
        waitdialog.setMessage("视频页面加载中...");
        waitdialog.setIndeterminate(true);
        waitdialog.setCancelable(true);
        waitdialog.show();

        WebSettings ws = mWebView.getSettings();
//        ws.setBuiltInZoomControls(true);// 隐藏缩放按钮
//        ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);// 排版适应屏幕

//        ws.setUseWideViewPort(true);// 可任意比例缩放
//        ws.setLoadWithOverviewMode(true);// setUseWideViewPort方法设置webview推荐使用的窗口。setLoadWithOverviewMode方法是设置webview加载的页面的模式。

        ws.setSavePassword(true);
        ws.setSaveFormData(true);
        ws.setJavaScriptEnabled(true);
        ws.setSupportZoom(false);

        //android 2.3直接崩溃;3.0以上处理硬件加速后webview闪烁问题. 处理报错 E/external/chromium/net/disk_cache/stat_hub.cc(25912): netstack:  STAT_HUB - App app isn't supported
        if (Build.VERSION.SDK_INT >= 11) {
//            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        ws.setAllowFileAccess(true);
        if (Build.VERSION.SDK_INT >= 16) {
            ws.setAllowFileAccessFromFileURLs(true);
        }
//        ws.setGeolocationEnabled(true);// 启用地理定位
//        String defDatabasePath = this.getFilesDir().getAbsolutePath() + "/databases/";
//        ws.setGeolocationDatabasePath(defDatabasePath);// 设置定位的数据库路径
        ws.setDomStorageEnabled(true);
        ws.setSupportMultipleWindows(true);// 新加
        xwebchromeclient = new MyWebChromeClient();
        mWebView.setBackgroundColor(0);
        mWebView.addJavascriptInterface(new MyJavaScriptInterface(), "papa");
        mWebView.setWebChromeClient(xwebchromeclient);
        mWebView.setWebViewClient(new MyWebViewClient());

//        mWebView.loadUrl("http://look.appjx.cn/mobile_api.php?mod=news&id=12604");
//        mWebView.loadUrl("http://www.baidu.com/");
//        mWebView.loadUrl("file:///android_asset/videoplayer_google.html");
//        mWebView.loadUrl("file:///android_asset/video.html");
        mWebView.loadUrl("file:///android_asset/videoplayer_videojs.html");
//        mWebView.loadUrl("http://7xozte.com1.z0.glb.clouddn.com/devNexus%206-%20Space%20to%20explore.mp4");
    }

    public class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Logger.log("MyWebViewClient", "shouldOverrideUrlLoading", url);
            view.loadUrl(url);
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Logger.log("MyWebViewClient", "onPageFinished", url);
            waitdialog.dismiss();
        }
    }

    public class MyWebChromeClient extends WebChromeClient {
        private View xprogressvideo;

        // 播放网络视频时全屏会被调用的方法
        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            Logger.log("MyWebChromeClient", "onShowCustomView");
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            mWebView.setVisibility(View.INVISIBLE);
            // 如果一个视图已经存在，那么立刻终止并新建一个
            if (xCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }
            video_fullView.addView(view);
            xCustomView = view;
            xCustomViewCallback = callback;
            video_fullView.setVisibility(View.VISIBLE);
        }

        // 视频播放退出全屏会被调用的
        @Override
        public void onHideCustomView() {
            Logger.log("MyWebChromeClient", "onHideCustomView");
            if (xCustomView == null)// 不是全屏播放状态
                return;

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            xCustomView.setVisibility(View.GONE);
            video_fullView.removeView(xCustomView);
            xCustomView = null;
            video_fullView.setVisibility(View.GONE);
            xCustomViewCallback.onCustomViewHidden();
            mWebView.setVisibility(View.VISIBLE);
        }

        // 视频加载时进程loading
        @Override
        public View getVideoLoadingProgressView() {
            Logger.log("MyWebChromeClient", "getVideoLoadingProgressView");
            if (xprogressvideo == null) {
                LayoutInflater inflater = LayoutInflater
                        .from(VideoPlayerActivity.this);
                xprogressvideo = inflater.inflate(
                        R.layout.video_loading_progress, null);
            }
            return xprogressvideo;
        }
    }

    final class MyJavaScriptInterface {
        private Context mContext;

        MyJavaScriptInterface() {
            mContext = getBaseContext();
        }

        public void showToast(String msg) {
            Logger.log("MyJavaScriptInterface", "showToast", msg);
            ToastUtils.getInstance(getBaseContext()).showToastSystem(msg);
        }

        public String getVideoInfo() {
            Logger.log("MyJavaScriptInterface", "getVideoInfo");
            String ret = "";
            if (videoInfo == null) return ret;

            ret = videoInfo.toString();
            Logger.log("MyJavaScriptInterface", "getVideoInfo", "info=" + ret);
            return ret;
        }
    }

    /**
     * 判断是否是全屏
     *
     * @return
     */
    public boolean inCustomView() {
        return (xCustomView != null);
    }

    /**
     * 全屏时按返加键执行退出全屏方法
     */
    public void hideCustomView() {
        xwebchromeclient.onHideCustomView();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        super.onResume();
        mWebView.onResume();
        mWebView.resumeTimers();

        /**
         * 设置为横屏
         */
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebView.onPause();
        mWebView.pauseTimers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        video_fullView.removeAllViews();
        ((ViewGroup) mWebView.getParent()).removeAllViews();
        mWebView.loadUrl("about:blank");
        mWebView.stopLoading();
        mWebView.setWebChromeClient(null);
        mWebView.setWebViewClient(null);
        mWebView.removeJavascriptInterface("papa");
        mWebView.destroy();
        mWebView = null;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (inCustomView()) {
                // webViewDetails.loadUrl("about:blank");
                hideCustomView();
                return true;
            } else {
                mWebView.loadUrl("about:blank");
                VideoPlayerActivity.this.finish();
            }
        }
        return false;
    }

    public static class VideoInfo implements Serializable {
        String url;
        String mime;

        public VideoInfo() {
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getMime() {
            return mime;
        }

        public void setMime(String mime) {
            this.mime = mime;
        }

        @Override
        public String toString() {
//            return "VideoInfo{" +
//                    "url='" + url + '\'' +
//                    ", mime='" + mime + '\'' +
//                    '}';
            String ret = "{\"mime\":\"" + mime + "\",\"url\":\"" + url + "\"}";
            return ret;
        }
    }
}
