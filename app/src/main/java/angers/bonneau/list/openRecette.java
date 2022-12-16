package angers.bonneau.list;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import okio.Utf8;

public class openRecette extends AppCompatActivity {
    WebView wbView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_recette);
        wbView = (WebView) findViewById(R.id.webView);

        //webview de la recette on charge la page en question directement dans l'application

        //string erreur 404
        String defaut = "<html dir=\"ltr\" lang=\"fr\" class=\"\" lazy-loaded=\"true\"><head>\n" +
                "\t<head>\n" +
                "\t</head>\n" +
                "\t<body style=\"background-color: rgb(255, 255, 255);\">\n" +
                "\t\t<div class=\"lightSection\" style=\"opacity: 1;\"><div class=\"narrowSection\">\n" +
                "\t\t\t<div class=\"bodyText bodyTextNormal\" style=\"opacity: 1;\">\n" +
                "\t\t\t\t<h2>Erreur 404</h2>\n" +
                "\t\t\t\t<p>La page demander n'est pas trouvée</p>\n" +
                "\t\t\t</div>\n" +
                "\t\t</div>\n" +
                "\t</body>\n" +
                "</html>";


        wbView.setWebViewClient(new WebViewClient());

        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        if (url.isEmpty() || url == null) {
            wbView.loadData(defaut, "text/html", "Utf8");
        } else {
            wbView.loadUrl(url);
        }
    }
}