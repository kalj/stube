package se.tiled.stube;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.Window;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {

    WebView webview = null;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
            webview.goBack();
            injectCSS();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        webview = new WebView(getApplicationContext());
        // webview.setVisibility(View.INVISIBLE);

        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                injectCSS();
                //webview.setVisibility(View.VISIBLE);
                super.onPageFinished(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            //This function opens another application if you press a link to something
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//                String host = request.getUrl().getHost();
                return false;
            }
        });

        webview.loadUrl("https://m.youtube.com/");

        setContentView(webview);
    }

    private void injectCSS() {
        String default_code = "* [aria-label=\"Trending\"] { display: none !important; }"
                + "\n * [aria-label=\"Home\"] { display: none !important; }"
                + "\n * [aria-label=\"Show More\"] { display: none !important; }"
                + "\n div.icb div.dvb { display: none !important; }"
                + "\n div.icb ol { display: none !important; }";

        String startpage_code = default_code + "\n div .ir {display:none;}";

        try {
            String default_encode = Base64.encodeToString(default_code.getBytes(), Base64.NO_WRAP);
            String startpage_encode = Base64.encodeToString(startpage_code.getBytes(), Base64.NO_WRAP);
            webview.loadUrl("javascript:(function() {" +
                    "if(window.location.href == 'https://m.youtube.com/') {"+
                    "  new_style_code = window.atob('" + startpage_encode + "');" +
                    "}" +
                    "else {" +
                    "  new_style_code = window.atob('" + default_encode + "');" +
                    "}" +
                    "var existing_style_elem = document.getElementById('anus-css');" +
                    "if(existing_style_elem) {" +
                    "  existing_style_elem.innerHTML = new_style_code;" +
                    "}" +
                    "else {" +
                    "  var parent = document.getElementsByTagName('head').item(0);" +
                    "  var new_style_elem = document.createElement('style');" +
                    "  new_style_elem.type = 'text/css';" +
                    "  new_style_elem.id = 'anus-css';" +
                    "  new_style_elem.innerHTML = new_style_code;" +
                    "  parent.appendChild(new_style_elem);" +
                    "}" +
                    "})()");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
