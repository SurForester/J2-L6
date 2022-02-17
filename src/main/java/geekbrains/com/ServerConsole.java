package geekbrains.com;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ServerConsole {

    private static Socket socket;
    private static DataInputStream in;
    private static DataOutputStream out;

    public static void main(String[] args) {
        String message;
        try (ServerSocket serverSocket = new ServerSocket(Constants.SERVER_PORT)) {
            System.out.println("Сервер запущен");
            // запускаем параллельный поток отправки сообщений
            ThreadReader readerMessage = new ThreadReader();
            readerMessage.start();
            while (true) {
                //Ожидаем клиента
                socket = serverSocket.accept();
                System.out.println("Клиент подключился");
                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());
                while (true) {
                    try {
                        message = in.readUTF();
                        if (message.equals("/end")) {
                            break;
                        }
                        System.out.println(message);
                    } catch (IOException exception) {
                        break;
                    }
                }
                if (!socket.isClosed()) {
                    in.close();
                    out.close();
                    socket.close();
                }
                System.out.println("Клиент отключился");
            }
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
                    if (out == null) {
                        System.out.println("Нет подключенного клиента");
                    } else if (!message.isBlank()) {
                        out.writeUTF(message);
                    }
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

}
