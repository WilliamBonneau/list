package angers.bonneau.list;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class listItemsAdapter extends BaseAdapter {

    private Context context;
    private List<TechItem> listItemslist;
    private LayoutInflater inflater;
    public Activity mContext;

    public listItemsAdapter(Context context, List<TechItem> listItemslist){

        this.context = context;
        this.listItemslist = listItemslist;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listItemslist.size();
    }

    @Override
    public TechItem getItem(int position) {
        return listItemslist.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.adapter_item, null);
        //récupere les info de l'item actuel
        TechItem currentItem = getItem(position);
        String itemTitle = currentItem.getTitle();
        String imgLink = currentItem.getImgLink();
        String itemNbNote = currentItem.getNbNote();
        String itemNoteValue = currentItem.getNoteValue();
        String itemHrefLink = currentItem.getHrefLink();
        String itemAllCommentInfos = currentItem.getAllCommentInfos();
        
        TextView itemTitleView = view.findViewById(R.id.item_title);
        TextView itemNbNoteView = view.findViewById(R.id.item_nb_note);
        TextView itemNoteValueListView = view.findViewById(R.id.item_note_value);
        ImageView itemImgView = view.findViewById(R.id.item_img);

        TextView itemPseudo = view.findViewById(R.id.item_pseudo);
        TextView itemNoteGiven = view.findViewById(R.id.item_note_given);
        TextView itemComment = view.findViewById(R.id.item_comment);
        TextView itemDatePost = view.findViewById(R.id.item_date_post);

        String pseudo;
        String noteGiven;
        String comment;
        String datePost;


        String regexDefaut = ".*: ";
        String regexComment = "commentaire donné : .*";
        String regexNoteGiven = "Note donné : .*";
        String regexDatePost = "date donné : .*";

        //extract all info and set it

        Pattern ptrnCom = Pattern.compile("commentaire donné : .*");
        Pattern ptrnNoteGiven = Pattern.compile("Note donné : .*");
        Pattern ptrnDatePost = Pattern.compile("date donné : .*");
        Pattern ptrnPseudo = Pattern.compile(".*");


        Matcher mtchDatePost = ptrnDatePost.matcher(itemAllCommentInfos);
        if (mtchDatePost.find())
        {
            datePost = mtchDatePost.group().replaceAll(regexDefaut,"").trim();
            itemAllCommentInfos = itemAllCommentInfos.replaceAll(regexDatePost,"").trim();
            itemDatePost.setText(datePost);
        }

        Matcher mtchComment = ptrnCom.matcher(itemAllCommentInfos);
        if (mtchComment.find())
        {
            comment = mtchComment.group().replaceAll(regexDefaut,"").trim();
            itemAllCommentInfos = itemAllCommentInfos.replaceAll(regexComment,"").trim();
            itemComment.setText(comment);
        }

        Matcher mtchNoteGiven = ptrnNoteGiven.matcher(itemAllCommentInfos);
        if (mtchNoteGiven.find())
        {
            noteGiven = mtchNoteGiven.group().replaceAll(regexDefaut,"").trim();
            itemAllCommentInfos = itemAllCommentInfos.replaceAll(regexNoteGiven,"").trim();
            itemNoteGiven.setText(noteGiven);
        }

        Matcher mtchPseudo = ptrnPseudo.matcher(itemAllCommentInfos);
        if (mtchPseudo.find())
        {
            pseudo = mtchPseudo.group().replaceAll(regexDefaut,"");
            itemPseudo.setText(pseudo);
        }



        itemTitleView.setText(itemTitle);
        itemNbNoteView.setText(itemNbNote);
        itemNoteValueListView.setText(itemNoteValue);
        
        if (!imgLink.isEmpty()){
            Picasso.get().load(imgLink).into(itemImgView);
        }
        else {
            String ressourceName = "item_"+imgLink+"_icon";
            int resId = context.getResources().getIdentifier(ressourceName,"drawable",context.getPackageName());
            itemImgView.setImageResource(resId);
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext = (Activity) context;

                Intent intent = new Intent(view.getContext(), openRecette.class);
                intent.putExtra("url",itemHrefLink);
                context.startActivity(intent);
                mContext .overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });


        return view;
    }
}
