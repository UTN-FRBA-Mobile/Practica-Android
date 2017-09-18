package ar.edu.utn.frba.practica;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;
import java.io.InputStream;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private static final String APP_SCHEME = "app";
    private WebView webView;
    private ProgressBar progressBar;

    public static MainActivityFragment newInstance() {
        return new MainActivityFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        webView = view.findViewById(R.id.webView);
        progressBar = view.findViewById(R.id.progressBar);
        view.findViewById(R.id.staticLocal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadStaticPage();
            }
        });
        view.findViewById(R.id.dynamicLocal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadDynamicPage();
            }
        });
        view.findViewById(R.id.remote).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadRemotePage();
            }
        });
        setupWebView();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        // Fondo transparente para que se "mezcle" con el resto de la app
        webView.setBackgroundColor(Color.TRANSPARENT);
        // Activamos javascript para poder interactuar con la página
        webView.getSettings().setJavaScriptEnabled(true);
        // Eventos de navegación
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                view.loadUrl("javascript:setText(\"¡Hola JavaScript!\")");
                progressBar.setVisibility(View.INVISIBLE);
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                progressBar.setVisibility(View.VISIBLE);
                Snackbar.make(getView(), error.getDescription(), Snackbar.LENGTH_LONG);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                progressBar.setVisibility(View.VISIBLE);
                Snackbar.make(getView(), description, Snackbar.LENGTH_LONG);
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return MainActivityFragment.this.shouldOverrideUrlLoading(request.getUrl());
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return MainActivityFragment.this.shouldOverrideUrlLoading(Uri.parse(url));
            }
        });
        webView.addJavascriptInterface(new Object() {

            // Los métodos de este objeto anotados con @JavascriptInterface son visibles con js
            // utilizando la variable `app`. Esto corre en background, así que para hacer cambios
            // en UI hay que volver al main thread.
            @JavascriptInterface
            public String toString() {
                return "injectedObject";
            }
        }, "app");
    }

    private void loadStaticPage() {
        String url = "file:///android_asset/termsAndConditions.html";
        webView.loadUrl(url);
    }

    private void loadDynamicPage() {
        // Al usar como baseUrl la url de assets se pueden acceder a los archivos desde el HTML
        // con referencias relativas.
        String baseUrl = "file:///android_asset/";
        InputStream templateInputStream = getResources().openRawResource(R.raw.template);
        StringBuilder data = new StringBuilder(Helper.readInputStream(templateInputStream));
        Helper.replace(data, "{TITLE}", getString(R.string.app_name));
        Helper.replace(data, "{CONTENT}", "<p><a onClick=\"app.toast('hola')\">prueba</a></p>");
        webView.loadDataWithBaseURL(baseUrl, data.toString(), "text/html", "UTF-8", "");
    }

    private void loadRemotePage() {
        // En una página remota funciona todo igual, solo hay que tener cuidado con lo que se
        // expone, para evitar que un sitio tome control de la aplicación
        String url = "http://www.google.com";
        webView.loadUrl(url);
    }

    private boolean shouldOverrideUrlLoading(Uri url) {
        // Si el scheme de la url coincide con nuestra app, le decimos al webview que
        // nosotros manejamos el request.
        if (APP_SCHEME.equals(url.getScheme())) {
            return performCommand(url.getHost());
        }
        return false;
    }

    private boolean performCommand(String command) {
        switch (command) {
            case "accept":
                acceptTermsAndConditions();
                return true;
            case "doNotAccept":
                doNotAcceptTermsAndConditions();
                return true;
        }
        return false;
    }

    private void acceptTermsAndConditions() {
        Snackbar.make(getView(), R.string.accept, Snackbar.LENGTH_LONG).show();
    }

    private void doNotAcceptTermsAndConditions() {
        Toast.makeText(getContext(), R.string.dontAccept, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add(R.string.acceptMenu).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                acceptTermsAndConditions();
                return true;
            }
        });
        menu.add(R.string.dontAcceptMenu).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                doNotAcceptTermsAndConditions();
                return true;
            }
        });
    }
}
