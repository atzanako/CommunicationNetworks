import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;


public class MailClient {

    public static final String error_in_connection = "! Connection error !",
            error_connection_not_found = "The server is down, please try again later.",
            error_wrong_input = "Please give valid inputs! (ip, port)",
            exit = "EXIT...BYE";

    private Scanner inputOfUser;
    private int port;
    private InetAddress ipAddress;
    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private String startingConnectionInput;
    private String responseValue;


    public MailClient() throws UnknownHostException{
        this(2401, InetAddress.getLocalHost());
    }

    public MailClient( int port, InetAddress ipAddress) {
        this.inputOfUser = new Scanner(System.in);
        this.port = port;
        this.ipAddress = ipAddress;

        check();
    }


    public static void main(String[] args){

        try{
            if(args.length == 2)
            {
                InetAddress ipAddress = InetAddress.getByName(args[0]);
                int port = Integer.parseInt(args[1]);
                new MailClient(port, ipAddress);

            }
            else {
                new MailClient();
            }
        }
        catch (Exception e){
            System.out.println(error_wrong_input);
        }
    }


    //Method that makes an effort to connect to the server. It returns true/false.
    public boolean effortForConnection(){
        try{
            socket = new Socket(ipAddress , port);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());

        }
        catch (IOException e){
            e.printStackTrace();
            return false;
        }

        return true;
    }


    //Method that closes the stream and returns the error
    public void closeStream(String error){
        if(error != null)
        {
            System.out.println(error);
        }
        try {
            inputStream.close();
            outputStream.close();
            socket.close();
        }
        catch (Exception e){
            System.out.println("The streams cannot be closed. ");
            e.printStackTrace();
        }

    }


    //Method that starts a connection with the server
    public void startConnection(){
        while (true)
        {
            if(UI())
            {
                startingConnectionInput = this.inputOfUser.nextLine();
                sendInputToServer(startingConnectionInput);
            }
            else{
                break;
            }
        }

        closeStream(null);
    }


    //Checks the connection, if it is possible it starts a connection, else shows an error message
    public void check(){
        if(effortForConnection())
        {
            startConnection();
        }
        else{
            closeStream(error_in_connection);
        }
    }


    //UI for the terminal
    public boolean UI(){
        ExtraClass extraClass = new ExtraClass();
        try{
            responseValue = inputStream.readObject().toString();

            if(responseValue.equals(exit)){
                return false;
            }

            System.out.println("-----------\nMailServer\n-----------");
            extraClass.add(responseValue);
            System.out.println(extraClass.passValue());
        }
        catch(Exception e){
            closeStream(error_connection_not_found);
            e.printStackTrace();
        }

        return true;
    }



    //Method that sends the input to the server
    public void sendInputToServer(String variable){
        try
        {
            outputStream.writeObject(variable);
        }
        catch (IOException e){
            closeStream(error_in_connection);
            e.printStackTrace();
        }

    }



}
