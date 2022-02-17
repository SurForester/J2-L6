package geekbrains.com;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClientConsole {

    private static Socket socket;
    private static DataInputStream in;
    private static DataOutputStream out;

    public static void main(String[] args) {
        try {
            socket = new Socket(Constants.SERVER_ADDRESS, Constants.SERVER_PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            ThreadReader readerMessage = new ThreadReader();
            readerMessage.start();
            boolean continueLoop = true;
            while (continueLoop) {
                try {
                    String message = in.readUTF();
                    if (message.equals("/end")) {
                        continueLoop = false;
                    }
                    System.out.println(message);
                } catch (IOException exception) {
                    continueLoop = false;
                }
            }
            if (!socket.isClosed()) {
                in.close();
                out.close();
                socket.close();
            }
            readerMessage.interrupt();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private static class ThreadReader extends Thread {
        @Override
        public void run() {
            Scanner scanner = new Scanner(System.in);
            String message;
            try {
                while (true) {
                    message = scanner.nextLine();
                    if (message.equals("/end")) {
                        out.writeUTF(message);
                        break;
                    }
                    if (!message.isBlank()) {
                        out.writeUTF(message);
                    }
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            } finally {
                try {
                    in.close();
                    out.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
