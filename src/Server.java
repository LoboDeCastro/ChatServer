
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Server {
    public static void main(String[] args) {

        ArrayList<ChatClient> clientList = new ArrayList<>();
        HashMap<String, ChatClient> chatMap = new HashMap<>();

        try (ServerSocket server = new ServerSocket(1111)) {
            int count = 0;
            while (true) {
                System.out.println("waiting for clients...");
                Socket client = server.accept();
                count++;
                System.out.println("client connected #" + count);
                ChatClient cc = new ChatClient(clientList, client, chatMap);
                new Thread(cc).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
