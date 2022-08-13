public class Email {
    private int id;
    private  boolean isNew = true;
    private String sender;
    private String receiver;
    private String subject;
    private String mainbody;
    private int idplus = 1;


    public Email(String sender, String receiver, String subject, String mainbody) {
        this.sender = sender;
        this.receiver = receiver;
        this.subject = subject;
        this.mainbody = mainbody;

        setId();
    }


    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getSubject() {
        return subject;
    }

    public String getMainbody() {
        return mainbody;
    }

    public int getId() {
        return id;
    }



    //For each email we increase the ID, so that they are unique.
    public void setId() {
        id = idplus++;
    }

    // Checks if the email has been read

    public boolean readEmail(){
        return isNew;
    }

    //Sets the variable isNew to false because it has been read so it's not a new one.
    public  void isRead(){
        isNew = false;
    }

}
