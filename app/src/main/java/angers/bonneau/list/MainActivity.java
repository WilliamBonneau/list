package angers.bonneau.list;

import static java.lang.Integer.parseInt;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;

import org.jsoup.select.Elements;


public class MainActivity extends AppCompatActivity {

    private TextView input;
    private TextView lastResearch;
    private ImageView imgSearch;
    String readTextFromRequest;
    private Button btnNext,btnActuel,btnPrevious;
    int pageNb=1;
    String inputText;

    List<String> titleList = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        input = findViewById(R.id.input);
        imgSearch = findViewById(R.id.img_search);

        btnPrevious = findViewById(R.id.button_previous);
        btnActuel = findViewById(R.id.button_actuel);
        btnNext = findViewById(R.id.button_next);
        btnActuel.setText("1");
        lastResearch = findViewById(R.id.last_research);
        //chargement de la derniere requete afin de pouvoir afficher "derniere recherche : "
        loadRequest();

        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //déclanche une recherche
                pageNb=1;
                searchRequest();
            }
        });

        lastResearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //si on click sur "derniere recherche : item " on déclanche la recherche
                Toast.makeText(MainActivity.this, "Recherche pour : " + readTextFromRequest + " sur le site Marmiton", Toast.LENGTH_SHORT).show();
                description_webscrappe dw = new description_webscrappe(readTextFromRequest);
                dw.execute();
            }
        });

    }

    private void saveRequestAsFile(String inputText){
        //sauvegarde la derniere recherche
        File path = getApplicationContext().getFilesDir();
        try {
            FileOutputStream writer = new FileOutputStream(new File(path, "list.txt"));
            writer.write(inputText.toString().getBytes());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "probleme.", Toast.LENGTH_SHORT).show();
        }

    }




    private void searchRequest() {
        inputText = input.getText().toString();
        if (inputText == null || inputText.length() == 0) {
            input.setText("");
            Toast.makeText(this, "Merci de rentrer quelque chose.", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Recherche pour : " + inputText + " sur le site Marmiton", Toast.LENGTH_SHORT).show();
            saveRequestAsFile(inputText);

            description_webscrappe dw = new description_webscrappe(inputText);
            dw.execute();
        }

    }

    private void loadRequest() {
        //récupère les infos de la dernière recherche
        File path = getApplicationContext().getFilesDir();
        File readFrom = new File(path, "list.txt");
        byte[] content = new byte[(int) readFrom.length()];

        FileInputStream stream = null;
        try {
            stream = new FileInputStream(readFrom);
            stream.read(content);

            String s = new String(content);

            String requestText = s.trim();

            // There may be no items in the grocery list.
            if (requestText.isEmpty()){
                readTextFromRequest = "";
            }
            else {
                readTextFromRequest = requestText;
                lastResearch.setText("Dernière recherche : "+readTextFromRequest);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class description_webscrappe extends AsyncTask<Void, Void, String> {

        public description_webscrappe(String text) {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected String doInBackground(Void... voids) {

            org.jsoup.nodes.Document document = null;
            try {
                //on récupère les infos de la dernière recherche et on fait une requete sur le site marmiton avec un numéro de page
                loadRequest();
                document = Jsoup.connect("https://www.marmiton.org/recettes/recherche.aspx?aqt="+readTextFromRequest.trim().replaceAll(" ","-")+"&page=" + pageNb).get();
                Elements allInfo = document.getElementsByClass("MRTN__sc-1gofnyi-0 YLcEb");

                //clear de la titleList

                titleList.clear();
                List<String> noteValueList = new ArrayList<String>();
                List<String> nbNoteList = new ArrayList<String>();
                List<String> hrefList = new ArrayList<String>();
                List<String> imgList = new ArrayList<String>();
                List<String> allInfoCommentList = new ArrayList<String>();

                //les regex afin de clean ce qui est reçu
                Pattern all = Pattern.compile("(.*)");
                Pattern regexHtmlImgSrc = Pattern.compile("((img|data).src=\"(https?:\\/\\/[^\\s]+).(?:png|jpg|jpeg|gif|png|svg|webp))");
                String regexHTMChevron = "<[^>]*>";
                String regexBreakLine = "(\\r\\n|\\n|\\r)";
                Pattern regexAspxWebsiteLink = Pattern.compile("(\\/[^\\s]+)\\.aspx");
                String fixSrc = ".*=\"";
                String marmitonAdress = "https://www.marmiton.org";

                //Si la regex detecte qu'il y a bien un élément qui match alors on l'ajoute dans une liste
                //on clean aussi toute les chose de trop reçu
                Matcher mtchtitle = all.matcher(allInfo.select(".MRTN__sc-30rwkm-0.dJvfhM").toString());
                while(mtchtitle.find()){
                    titleList.add(mtchtitle.group().replaceAll(regexHTMChevron,"").trim());
                }
                titleList.removeAll(Arrays.asList("", null));

                Matcher mtchnbNote = all.matcher(allInfo.select(".MRTN__sc-30rwkm-3.fyhZvB").toString());
                while(mtchnbNote.find()){
                    nbNoteList.add(mtchnbNote.group().replaceAll(regexHTMChevron,"").trim());
                }
                nbNoteList.removeAll(Arrays.asList("", null));

                Matcher mtchNoteValue = all.matcher(allInfo.select(".SHRD__sc-10plygc-0.jHwZwD").toString());
                while(mtchNoteValue.find()){
                    noteValueList.add(mtchNoteValue.group().replaceAll(regexHTMChevron,"").trim());
                }
                noteValueList.removeAll(Arrays.asList("", null));

                Matcher mtchImgHtmlSrc = regexHtmlImgSrc.matcher(allInfo.toString());
                while(mtchImgHtmlSrc.find()){
                    imgList.add(mtchImgHtmlSrc.group().replaceAll(fixSrc, "").trim());
                }
                imgList.removeAll(Arrays.asList("", null));

                Matcher mtchhref = regexAspxWebsiteLink.matcher(allInfo.toString());
                while(mtchhref.find()){
                    hrefList.add( marmitonAdress + mtchhref.group().trim());
                }
                hrefList.removeAll(Arrays.asList("", null));

                //si on ne récupère rien alors on set l'élément avec son titre par défaut
                if (titleList.size()==0){
                    String defautTitle = getResources().getString(R.string.item_default_recette);
                    titleList.add(defautTitle);
                }
                if (nbNoteList.size()==0){
                    String defautNbNote = getResources().getString(R.string.item_default_nbNote);
                    nbNoteList.add(defautNbNote);
                }
                if (noteValueList.size()==0){
                    String defautNoteValue = getResources().getString(R.string.item_default_note_value);
                    noteValueList.add(defautNoteValue);
                }
                if (hrefList.size()==0){
                    hrefList.add("");
                }

                for (int i = 0; i < titleList.size();i++) {
                    //recherche pour le commentaire, et de manière aléatoire
                    List<String> pseudoListe = new ArrayList<String>();
                    List<String> noteValueGivenList = new ArrayList<String>();
                    List<String> commentList = new ArrayList<String>();
                    List<String> dateList = new ArrayList<String>();
                    //ici il faut faire attention a si le resultat est null
                    if (!hrefList.get(i).isEmpty()) {


                        document = Jsoup.connect(hrefList.get(i)).get();
                        Elements allInfoArticle = document.getElementsByClass("RCP__sc-2jkou4-2 eEVBBK");

                        Matcher mtchPseudo = all.matcher(allInfoArticle.select(".MuiTypography-root.MuiTypography-h6").toString());
                        while (mtchPseudo.find()) {
                            pseudoListe.add(mtchPseudo.group().replaceAll(regexHTMChevron, "").trim());
                        }
                        pseudoListe.removeAll(Arrays.asList("", null));

                        Matcher mtchNoteValueGivenList = all.matcher(allInfoArticle.select(".SHRD__sc-10plygc-0.jHwZwD").toString());
                        while (mtchNoteValueGivenList.find()) {
                            noteValueGivenList.add(mtchNoteValueGivenList.group().replaceAll(regexHTMChevron, "").trim());
                        }
                        noteValueGivenList.removeAll(Arrays.asList("", null));

                        Matcher mtchCommentList = all.matcher(allInfoArticle.select(".SHRD__sc-10plygc-0.bzAHrL").toString());
                        while (mtchCommentList.find()) {
                            commentList.add(mtchCommentList.group().replaceAll(regexHTMChevron, "").trim());
                        }
                        commentList.removeAll(Arrays.asList("", null));

                        Matcher mtchDateList = all.matcher(allInfoArticle.select(".MuiTypography-root.MuiTypography-body2").toString());
                        while (mtchDateList.find()) {
                            dateList.add(mtchDateList.group().replaceAll(regexHTMChevron, "").trim());
                        }
                        dateList.removeAll(Arrays.asList("", null));
                    }
                    //si + un pseudo existe alors on génère un numéro aléartoire pour afficher un commentaire
                    //sinon on set les titre par défaut présent dans un fichier de String defaut
                    int nbRand ;
                    if(pseudoListe.size()!=0){
                        nbRand = (int) Math.floor(Math.random() * (pseudoListe.size()));
                    }else {
                        nbRand =0;
                    }

                    if (pseudoListe.size()==0){
                        String defautPseudo = getResources().getString(R.string.item_default_pseudo);
                        pseudoListe.add(defautPseudo);
                    }

                    if (noteValueGivenList.size()==0){
                        String defautnoteValueGiven = getResources().getString(R.string.item_default_note_given);
                        noteValueGivenList.add(defautnoteValueGiven);
                    }

                    if (commentList.size()==0) {
                        String defautComment = getResources().getString(R.string.item_default_comment);
                        commentList.add(defautComment);
                    }

                    if (dateList.size()==0) {
                        String defautDatePost = getResources().getString(R.string.item_default_date);
                        dateList.add(defautDatePost);
                    }

                    if (imgList.size()==0) {
                        String defautImg = getResources().getString(R.string.item_default_img);
                        imgList.add(defautImg);
                    }
                    //on mets tout dans une seule chaine de carractères car celà est plus court
                    allInfoCommentList.add("Pseudo : " + pseudoListe.get(nbRand) + " Note donné : " + noteValueGivenList.get(nbRand) + " commentaire donné : " + commentList.get(nbRand) + " date donné : " +dateList.get(nbRand));

                                        //mettre dans une string puis ajouter dans une liste et l'envoyer
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        List<Recette> recetteList = new ArrayList<>();
                        //pour le  nombre de titre recu on creer un nouvel objet recette avec tout les élément présent
                        for (int i = 0; i < titleList.size();i++)
                        {
                            recetteList.add(new Recette(titleList.get(i),nbNoteList.get(i),imgList.get(i),noteValueList.get(i), hrefList.get(i),allInfoCommentList.get(i)));
                        }
                        //puis on affecte à la liste d'item les éléments
                        ListView listItemsView = findViewById(R.id.list_items);
                        listItemsView.setAdapter(new listItemsAdapter(MainActivity.this, recetteList));
                    }
                });


            } catch (IOException e) {
                e.printStackTrace();
            }
            //outputJsoup.setText(temp);
            return null;
        }





        @Override
        protected void onPostExecute(String unused) {
            //après l'execution on affiche ou numéro des bouton en fonction de la page ou l'on se trouve
            //dans la liste le nombre maximum renvoyé par le site étant de 12 alors on regarde cette size
            if (pageNb>1){
                btnPrevious.setText("← "+ String.valueOf(pageNb-1));
                btnPrevious.setVisibility(View.VISIBLE);
            }else {
                btnPrevious.setVisibility(View.GONE);
            }


            btnActuel.setText(String.valueOf(pageNb));
            btnActuel.setVisibility(View.VISIBLE);

            if (titleList.size()>=12){
                btnNext.setText(String.valueOf(pageNb + 1) + " →");
                btnNext.setVisibility(View.VISIBLE);
            }else {
                btnNext.setVisibility(View.GONE);
            }


            btnPrevious.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //to do recup nb de la page +1
                    pageNb-=1;


                    if (pageNb>1){
                        btnPrevious.setText("← "+ String.valueOf(pageNb-1));
                        btnPrevious.setVisibility(View.VISIBLE);
                    }else {
                        btnPrevious.setVisibility(View.GONE);
                    }

                    btnActuel.setText(String.valueOf(pageNb));
                    btnActuel.setVisibility(View.VISIBLE);

                    if (titleList.size()>=12){
                        btnNext.setText(String.valueOf(pageNb + 1) + " →");
                        btnNext.setVisibility(View.VISIBLE);
                    }else {
                        btnNext.setVisibility(View.GONE);
                    }

                    description_webscrappe dw = new description_webscrappe(inputText);
                    dw.execute();
                }
            });

            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //to do recup nb de la page +1
                    pageNb+=1;

                    if (pageNb>1){
                        btnPrevious.setText("← "+ String.valueOf(pageNb-1));
                        btnPrevious.setVisibility(View.VISIBLE);
                    }else {
                        btnPrevious.setVisibility(View.GONE);
                    }

                    btnActuel.setText(String.valueOf(pageNb));
                    btnActuel.setVisibility(View.VISIBLE);

                    if (titleList.size()>=12){
                        btnNext.setText(String.valueOf(pageNb + 1) + " →");
                        btnNext.setVisibility(View.VISIBLE);
                    }else {
                        btnNext.setVisibility(View.GONE);
                    }
                    description_webscrappe dw = new description_webscrappe(inputText);
                    dw.execute();
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.e("derniere recherceh ", inputText);
        saveRequestAsFile(inputText);
    }
}
