package angers.bonneau.list;

public class TechItem {
    //attributs
    private String title;
    private String nbNote;
    private String imgLink;
    private String noteValue;
    private String hrefLink;
    private String allCommentInfos;

    //constructeur pour creer un objet TechItem
    public TechItem(String title, String nbNote, String imgLink, String noteValue, String hrefLink, String allCommentInfos){
        this.title = title;
        this.nbNote = nbNote;
        this.imgLink = imgLink;
        this.noteValue = noteValue;
        this.hrefLink = hrefLink;
        this.allCommentInfos = allCommentInfos;
    }

    public String getTitle(){return title;}
    public String getNbNote(){return nbNote;}
    public String getImgLink(){return imgLink;}
    public String getNoteValue(){return noteValue;}
    public String getHrefLink(){return hrefLink;}
    public String getAllCommentInfos(){return allCommentInfos;}

}
