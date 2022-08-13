import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MailServer  {

    private int port;
    private ServerSocket socket;
    private List<Account> account;
    private List<MailServerFunctions> functions;


    private static final int PORT = 2401;
    private static  final String error_wrong_input = "Please give valid inputs! (ip, port)";


    public MailServer(int port){
        this.port = port;
        account = new ArrayList<>();
        functions = new ArrayList<>();
        MailServerFunctions.mailServer = this;

        existingAccounts();
        startServer();
    }


    public MailServer(){
        this(PORT);
    }


    public static void main(String[] args) {
        try{
            if(args.length==1)
            {
                new MailServer(Integer.parseInt(args[0]));
            }
            else{
                new MailServer();
            }
        }catch (Exception e){
            System.out.println(error_wrong_input);
            e.printStackTrace();
        }

    }

    //Searches an account by the username
    public synchronized Account searchAccount(String username){
        for(Account account : account)
        {
            if(username.equals(account.getUsername()))
            {
                return account;
            }
        }
        return null;
    }


    //Adds an account to the account ArrayList
    public synchronized boolean addAccountInArrayList(Account account){

        if(searchAccount(account.getUsername() )!= null)
        {
            return false;
        }

        if(!account.validUsername(account.getUsername()))
        {
            return false;
        }

        this.account.add(account);
        return true;
    }


    public void existingAccounts(){
        Account ac1 = new Account("tanenbaum@gmail.com" , "1234");
        Account ac2 = new Account("watherall@hotmail.com" , "7574");
        Account ac3 = new Account("red@gmail.com" , "545235");

        ac1.addNewEmail(new Email("tanenbaum@gmail.com" ,"red@gmail.com","More info","Hello, I would like some more information about your rooms. Are they pet friendly?"));
        ac2.addNewEmail(new Email("watherall@hotmail.com" ,"tanenbaum@gmail.com","Internship","Hello, you've been chosen for our internship! Contact us!"));
        ac1.addNewEmail(new Email( "tanenbaum@gmail.com","watherall@hotmail.com","RE: Internship","Thank you so much for this opportunity! Please tell me when can I come to the office."));
        ac3.addNewEmail(new Email("red@gmail.com" ,"watherall@hotmail.com","Results","Your results are ready, you can come to the lab from 9AM to 5PM."));

        addAccountInArrayList(ac1);
        addAccountInArrayList(ac2);
        addAccountInArrayList(ac3);
    }


    /**
     * To start the server in the port that it listens
     * Moreover, sends new function, stores the
     * function into the list.
     *
     */

    //Method that starts the server
    public void startServer(){
        try
        {
            socket = new ServerSocket(PORT);
            System.out.println("....Incoming connections...");

        }catch (IOException e){
            System.out.println("! ERROR !: Incoming connectionS");
            e.printStackTrace();
        }

        while (true)
        {
            Socket socket1 = null;
            try
            {
                socket1 = socket.accept();
            }catch (IOException e){

                e.printStackTrace();
                continue;
            }

            System.out.println("Connection ✔: " + socket1.getInetAddress() + ":" + PORT);
            MailServerFunctions functionNotTheList = new MailServerFunctions(socket1);
            functions.add(functionNotTheList);
        }
    }




    //Class that has the functions that the MailServer needs
    public static class MailServerFunctions extends Thread{
        public static final String exit = "EXIT...BYE";
        public static MailServer mailServer;
        private static Socket client;
        private ObjectInputStream in;
        private ObjectOutputStream out;
        private ExtraClass extra;
        private ArrayList<String> withLogin = new ArrayList<>();
        private ArrayList <String> withoutLogin = new ArrayList<>();
        private Account user;


        public MailServerFunctions(Socket client){
            this.client = client;
            extra = new ExtraClass();

            try{
                in = new ObjectInputStream(client.getInputStream());
                out = new ObjectOutputStream(client.getOutputStream());
                start();
            }catch (IOException e){
                exit();
                e.printStackTrace();
            }
        }



        private String menu(ArrayList<String> options){
            String input;

            while (true){
                System.out.println("=========================");
                for (String option: options)
                {
                    extra.add(">" + option + System.lineSeparator() );
                }
                System.out.println("=========================");
                input = getTheInput();


                assert input != null;
                if(options.contains(input))
                {
                    return input;
                }
                else{
                    extra.add("Check input" + System.lineSeparator());
                }

            }
        }


        public boolean message(String message, boolean b){
            if((user ==null && b) || (user !=null && !b)){

                System.out.println("-------------------------------");
                extra.add(message + System.lineSeparator());
                return !b;
            }

            return b;
        }


        //Adds options to the menu
        public void run(){

            String menu;
            boolean b = true;

            withoutLogin.add("Login");
            withoutLogin.add("Register");
            withoutLogin.add("Exit");
            withLogin.add("ShowEmails");
            withLogin.add("ReadEmail");
            withLogin.add("NewEmail");
            withLogin.add("DeleteEmail");
            withLogin.add("LogOut");
            withLogin.add("Exit");


            while (true){

                if(user == null)
                {
                    b = message("Hello, you  connected as a guest",b);
                    menu = menu(withoutLogin);
                }else{
                    b = message("Welcome back "+ user.getUsername(),b);
                    menu = menu(withLogin);
                }

                if(menu.equals("Exit"))
                {
                    exit();
                } else if(menu.equals("Login"))
                {
                    logIn();
                }else if(menu.equals("Register"))
                {
                    register();
                } else if(menu.equals("NewEmail"))
                {
                    newEmail();
                } else if(menu.equals("ShowEmails"))
                {
                    showEmails();
                } else if(menu.equals("ReadEmail"))
                {
                    readEmails();
                } else if(menu.equals("DeleteEmail"))
                {
                    deleteEmails();
                } else if(menu.equals("LogOut"))
                {
                    logOut();
                }
            }
        }


        //Method the closes the streams
        public void exit(){
            try{
                out.writeObject(exit);
                in.close();
                out.close();
                client.close();

            }catch (Exception e){
                e.printStackTrace();
            }
        }


        //Method for the user to login
        public void logIn(){
            String username;
            String password;

            extra.add("Give a username:"+System.lineSeparator());
            username = getTheInput();

            extra.add("Give a password:"+System.lineSeparator());
            password= getTheInput();
            Account user1 = mailServer.searchAccount(username);

            if(user1==null|| !user1.correctPassword(password))
            {
                extra.add("! ERROR ! Wrong username or password"+System.lineSeparator());
            }else {
                this.user = user1;
            }
        }


        //Logout
        public void logOut() {
            user = null;
        }

        //Creates a new account
        public void register(){
            String username;
            String password;

            extra.add("Give a username "+System.lineSeparator());
            username = getTheInput();

            extra.add("Give a password"+System.lineSeparator());
            password= getTheInput();
            Account user2 = new Account(username, password);

            if(mailServer.addAccountInArrayList(user2))
            {
                this.user = user2;
            }else{
                extra.add("This username already exists. "+System.lineSeparator());
            }
        }

        //Method that creates a new Email and sent it to the receiver
        public void newEmail(){
            Email email;
            String mainBody;
            String receiver;
            String subject;
            Account user3;

            extra.add("->Receiver: "+System.lineSeparator());
            receiver = getTheInput();
            extra.add("->Subject: "+System.lineSeparator());
            subject = getTheInput();
            extra.add("->Main Body: "+System.lineSeparator());
            mainBody = getTheInput();
            user3 = mailServer.searchAccount(receiver);
            email = new Email(user.getUsername() , receiver , subject , mainBody);

            if(user3.addNewEmail(email))
            {
                extra.add("Email sent"+System.lineSeparator());

            }else{
                extra.add("! ERROR ! WAS NOT SENT"+System.lineSeparator());
            }
        }


        //Method that shows the email with the input id
        public void readEmails(){
            int iddd;
            Email email;

            extra.add("Adding email with id: "+System.lineSeparator());

            try
            {
                iddd= Integer.parseInt(Objects.requireNonNull(getTheInput()));
            }catch (Exception e){
                iddd = -1;
                e.printStackTrace();
            }
            email = user.SearchEmail(iddd);

            if(email != null)
            {
                extra.addIndexSize(10 , "From: ");
                extra.add(email.getSender());
                extra.add(System.lineSeparator());
                extra.addIndexSize(10 , "Subject: ");
                extra.add(email.getSubject());
                extra.add(System.lineSeparator());

                System.out.println("============================");

                extra.add(email.getMainbody() + System.lineSeparator());
                email.isRead();
            }
            else{
                extra.add("! Error ! Reading email"+System.lineSeparator() );
            }

        }


        //Method the shows all emails to the user
        public boolean  showEmails(){
            if(user.getMailbox().size() <1)
            {
                extra.add("Your mailbox is empty..."+System.lineSeparator());
                return false;
            }

            extra.addIndexSize(10, "ID \t\t\t");
            extra.addIndexSize(30, "FROM\t\t ");
            extra.addIndexSize(20, "SUBJECT ");
            extra.add(System.lineSeparator());

            for(Email email : user.getMailbox())
            {
                String id2 =  String.format("%d. %s", email.getId() , email.readEmail() ? " [New] " : "");

                extra.addIndexSize(10, id2);
                extra.addIndexSize(30, email.getSender() + " ");
                extra.addIndexSize(20, email.getSubject());
                extra.add(System.lineSeparator());
            }
            return true;
        }

        //Method that deletes an email
        public void deleteEmails(){
            int id3;
            extra.add("Email id: "+System.lineSeparator());
            try{
                id3 = Integer.parseInt(Objects.requireNonNull(getTheInput()));

            }catch (Exception e){
                id3 = -1;
                e.printStackTrace();
            }

            if(user.EmailToDelete(id3))
            {
                extra.add("The Email with id:" + id3 + " is deleted"+System.lineSeparator() );
            }
            else{
                extra.add("! ERROR ! Problem in deletion"+System.lineSeparator() );
            }


        }


        //Method that returns the input and sends the output
        private String getTheInput(){

            try {
                out.writeObject(extra.passValue());
                return (String) in.readObject();
            }
            catch (Exception e){
                e.printStackTrace();
                return null;
            }

        }


    }
}
