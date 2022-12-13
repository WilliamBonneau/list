package angers.bonneau.list;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;

import org.jsoup.select.Elements;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

    private TextView input;
    private ImageView imgSearch;
    String readTextFromRequest;
    private Button btnNext,btnActuel,btnPrevious;
    int pageNb=1;
    String inputText;
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
        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchRequest();
            }
        });

    }

    private void saveRequestAsFile(String inputText){
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

    public class description_webscrappe extends AsyncTask<Void, Void, String> {

        public description_webscrappe(String text) {
            Log.e("",text);
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
                loadRequest();
                document = Jsoup.connect("https://www.marmiton.org/recettes/recherche.aspx?aqt="+readTextFromRequest.replaceAll(" ","-")+"&page=" + pageNb ).get();
                Elements allInfo = document.getElementsByClass("MRTN__sc-1gofnyi-0 YLcEb");


                List<String> titleList = new ArrayList<String>();
                List<String> noteValueList = new ArrayList<String>();
                List<String> nbNoteList = new ArrayList<String>();
                List<String> hrefList = new ArrayList<String>();
                List<String> imgList = new ArrayList<String>();
                List<String> allInfoCommentList = new ArrayList<String>();

                Pattern all = Pattern.compile("(.*)");
                Pattern regexHtmlImgSrc = Pattern.compile("((img|data).src=\"(https?:\\/\\/[^\\s]+).(?:png|jpg|jpeg|gif|png|svg|webp))");
                String regexHTMChevron = "<[^>]*>";
                String regexBreakLine = "(\\r\\n|\\n|\\r)";
                Pattern regexAspxWebsiteLink = Pattern.compile("(\\/[^\\s]+)\\.aspx");
                String fixSrc = ".*=\"";
                String marmitonAdress = "https://www.marmiton.org";


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

                //faire matcher pour href



                for (int i = 0; i < titleList.size();i++) {
                    //Log.e("", titleList.get(i));



                    List<String> pseudoListe = new ArrayList<String>();
                    List<String> noteValueGivenList = new ArrayList<String>();
                    List<String> commentList = new ArrayList<String>();
                    List<String> dateList = new ArrayList<String>();


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


                    int nbRand = (int) Math.floor(Math.random() * (commentList.size()));
                    //Log.e("", titleList.get(i));

                    if (pseudoListe.size()==0){
                        pseudoListe.add("rien récupéré");
                    }

                    if (noteValueGivenList.size()==0){
                        noteValueGivenList.add("rien récupéré");
                    }

                    if (commentList.size()==0) {
                        commentList.add("rien récupéré");
                    }
                    allInfoCommentList.add("Pseudo : " + pseudoListe.get(nbRand) + " Note donné : " + noteValueGivenList.get(nbRand) + " commentaire donné : " + commentList.get(nbRand));
                    //mettre dans une string puis ajouter dans une liste et l'envoyer
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        List<TechItem> TechItemList = new ArrayList<>();
                        for (int i = 0; i < titleList.size();i++)
                        {
                            TechItemList.add(new TechItem(titleList.get(i),nbNoteList.get(i),imgList.get(i),noteValueList.get(i), hrefList.get(i),allInfoCommentList.get(i)));
                        }

                        ListView listItemsView = findViewById(R.id.list_items);
                        listItemsView.setAdapter(new listItemsAdapter(MainActivity.this, TechItemList));
                    }
                });


            } catch (IOException e) {
                e.printStackTrace();
            }
            //outputJsoup.setText(temp);
            return null;
        }

        private void loadRequest() {
            File path = getApplicationContext().getFilesDir();
            File readFrom = new File(path, "list.txt");
            byte[] content = new byte[(int) readFrom.length()];

            FileInputStream stream = null;
            try {
                stream = new FileInputStream(readFrom);
                stream.read(content);

                String s = new String(content);

                s = s.substring(1, s.length() - 1);
                String requestText = s.trim();

                // There may be no items in the grocery list.
                if (requestText.isEmpty()){
                    readTextFromRequest = "";
                    Toast.makeText(MainActivity.this, "Une erreur s'est produite.", Toast.LENGTH_SHORT).show();
                }
                else {
                    readTextFromRequest = requestText;
                    Toast.makeText(MainActivity.this, requestText, Toast.LENGTH_SHORT).show();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }



        @Override
        protected void onPostExecute(String unused) {

            btnActuel.setVisibility(View.VISIBLE);
            if (!btnActuel.getText().toString().equals("1")){
                btnPrevious.setVisibility(View.VISIBLE);
            }else {
                btnPrevious.setVisibility(View.GONE);
                btnNext.setText(String.valueOf(pageNb+1) + " →");
                btnNext.setVisibility(View.VISIBLE);
            }



            btnPrevious.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //to do recup nb de la page +1
                    pageNb-=1;
                    inputText = input.getText().toString();

                    btnPrevious.setText("← "+ String.valueOf(pageNb-1));
                    btnActuel.setText(String.valueOf(pageNb));
                    btnNext.setText(String.valueOf(pageNb+1) + " →");

                    description_webscrappe dw = new description_webscrappe(inputText);
                    dw.execute();
                }
            });

            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //to do recup nb de la page +1
                    pageNb+=1;
                    inputText = input.getText().toString();

                    btnPrevious.setText("← "+ String.valueOf(pageNb-1));
                    btnActuel.setText(String.valueOf(pageNb));
                    btnNext.setText(String.valueOf(pageNb+1) + " →");
                    description_webscrappe dw = new description_webscrappe(inputText);
                    dw.execute();
                }
            });
        }

    }



}
