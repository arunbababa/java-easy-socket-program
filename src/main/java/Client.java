import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 80;

        // なおサーバのソケット待ち受けの方はServerSocketというクラス型で受け付ける
        // こっちはsocket投げる方なのでsocketでＯＫ
        try (Socket socket = new Socket(host, port)){
            // 入出力ストリームの取得
            BufferedReader in = new BufferedReader(new InputStreamReader((socket.getInputStream())));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // サーバにメッセージを送信
            out.println("hello server, im client");

            // サーバの応答を受信
            String res = in.readLine();
            System.out.println("サーバの応答：" + res);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
