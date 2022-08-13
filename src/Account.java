import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Account {
    private int userId;
    private String username;
    private String password;
    private ArrayList<Email> mailbox;
    private boolean passwordsMatch = false;
    private boolean validUsername = false;

    public Account(String userName , String userPassword) {
        this.username = userName;
        this.password = userPassword;
        this.mailbox = new ArrayList<>();
        this.userId = 1;
    }



    //Getters
    public ArrayList<Email> getMailbox() {
        return mailbox;
    }

    public String getUsername() {
        return username;
    }



    /*
       Method that checks if passwords are the same. The parameter inputPassword is the user's input.
       If they match it returns true, else false.
    */
    public boolean correctPassword(String inputPassword){
        passwordsMatch = password.equals(inputPassword);
        return passwordsMatch;
    }


    /* Method that checks if the username is valid. The parameter username is the username to be checked.
       If they match it returns true, else false.
    */
    public boolean validUsername(String username){
        Pattern checkPattern = Pattern.compile(".+@.+\\...+");
        Matcher matcher = checkPattern.matcher(username);
        validUsername = matcher.matches();
        return validUsername;
    }


    /*
       Method that returns an email (if found), based on an input user's id
       searchId -> user's id, so that it searches for a email of this user
    */
    public Email SearchEmail(int searchId){
        for(Email returnedMail : mailbox)
        {
            if(searchId == returnedMail.getId())
            {
                return  returnedMail;
            }
        }
        return null;
    }


    //Method that adds a new Email
    public boolean addNewEmail(Email EmailToAdd){

        if(EmailToAdd.getReceiver().equals(EmailToAdd.getSender()))
        {
            return  false;
        }

        return EmailToAdd.getReceiver().equals(username);
    }


    //Method that checks if an Email should be deleted
    public boolean EmailToDelete(int emailId){
        Email emailToDelete = SearchEmail(emailId);
        if(emailToDelete==null)
        {
            return false;
        }

        return mailbox.remove(emailToDelete);

    }


}
