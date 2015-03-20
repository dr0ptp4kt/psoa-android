package parksupplyofamerica.com.parksupply;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.os.Build;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class MainActivity extends ActionBarActivity {

    private WebView mWebView;
    private MenuItem back;
    private MenuItem forward;
    private boolean hasErrorState = false;
    private boolean hasVisitedFirstPage = false;
    private Context ctx = this;
    private static final String startingPage = "http://www.parksupplyofamerica.com/mobet.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWebView = (WebView) findViewById(R.id.activity_main_webview);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        if (Build.VERSION.SDK_INT > 10) {
            webSettings.setDisplayZoomControls(false);
        }

        mWebView.setInitialScale(80);
        mWebView.setWebViewClient(new ParkWebViewClient());
        mWebView.loadUrl(startingPage);
    }

    @Override
    public void onBackPressed() {
        goBack(true);
    }

    private void goBack(boolean backStack) {
        if(mWebView.canGoBack()) {
            mWebView.goBack();
        } else if (backStack) {
            super.onBackPressed();
        }
    }

    private void goForward() {
        if(mWebView.canGoForward()) {
            mWebView.goForward();
        }
    }

    private void refreshButtons() {
        back.setEnabled(mWebView.canGoBack());
        forward.setEnabled(mWebView.canGoForward());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        back = menu.findItem(R.id.back);
        forward = menu.findItem(R.id.forward);
        refreshButtons();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.back) {
            goBack(false);
        } else if (id == R.id.forward) {
            goForward();
        }
        return super.onOptionsItemSelected(item);
    }

    public class ParkWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            String host = Uri.parse(url).getHost();
            if(host == null || host.equals("parksupplyofamerica.com") || host.endsWith(".parksupplyofamerica.com")) {
                return false;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            view.getContext().startActivity(intent);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            final WebView wv = view;
            if (hasErrorState && !hasVisitedFirstPage) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                builder.setMessage(R.string.conn)
                        .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                hasErrorState = false;
                                wv.reload();
                            }
                        });
                builder.create();
                builder.show();
                return;

            }
            hasErrorState = false;
            hasVisitedFirstPage = true;
            refreshButtons();
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            if (!hasVisitedFirstPage) {
                hasErrorState = true;
                return;
            }
            view.loadData("<html><body><h1 style='text-align:center'>Connection problem :(</h1></body></html>", "text/html", "UTF-8");
            refreshButtons();
        }
    }
}
