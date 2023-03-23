
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ChatClient implements Runnable {
    private BufferedReader reader;
    private PrintWriter printWriter;
    private ArrayList<ChatClient> clients;
    private HashMap<String, ChatClient> chatMap;
    private Socket client;
    private String name;

    public ChatClient(ArrayList<ChatClient> clients, Socket client, HashMap<String, ChatClient> chatMap) throws IOException {
        this.clients = clients;
        this.client = client;
        reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        printWriter = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));
        this.chatMap = chatMap;
    }

    public void sendMessage(String message) {
        printWriter.println(message);
        printWriter.flush();
    }

    @Override
    public void run() {
        try {
            String cmd;
            while ((cmd = reader.readLine()) != null) {
                String[] cmdArray = cmd.split(":");
                if (cmdArray.length == 1) {
                    if (cmdArray[0].equalsIgnoreCase("bye")) {
                        close();
                        break;
                    } else {
                        printWriter.println("unknown command");
                        printWriter.flush();
                    }
                }
                if (cmdArray.length >= 2) {
                    if (cmdArray[0].equalsIgnoreCase("name")) {
                        name = cmdArray[1];
                        clients.add(this);
                        chatMap.put(name, this);
                        System.out.println("name " + name + " set");
                        for (ChatClient chatClient : clients) {
                            chatClient.sendMessage("new client connected: " + name);
                        }
                    } else if (cmdArray[0].equalsIgnoreCase("msg")) {
                        String message = cmdArray[1];
                        for (ChatClient chatClient : clients) {
                            if (chatClient != this) {
                                chatClient.sendMessage(message);
                            }
                        }
                        System.out.println("msg: " + message + " --- sent");
                    } else if (cmdArray[0].equalsIgnoreCase("msgto")) {
                        String cmdName = cmdArray[1];
                        String message = cmdArray[2];
                        /*for (ChatClient chatClient : clients) {
                            if (chatClient.name.equalsIgnoreCase(cmdName)) {
                                chatClient.sendMessage(message);
                            }
                        }*/
                        if (chatMap.containsKey(cmdName)) {
                            chatMap.get(cmdName).sendMessage(message);
                        }

                        System.out.println("msgto: " + cmdName + " --- " + message + " --- sent");
                    } else {
                        printWriter.println("unknown command");
                        printWriter.flush();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() throws IOException {
        printWriter.close();
        reader.close();
        clients.remove(this);
        chatMap.remove(this.name);
        for (ChatClient chatClient : clients) {
            chatClient.sendMessage(this.name + " leaves the chat");
        }
        client.close();
    }
}
