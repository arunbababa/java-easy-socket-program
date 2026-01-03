import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        int port = 80;
        try(ServerSocket serversocket = new ServerSocket(port)) {
            while (true) {
                // クライアントからの接続を開いてこれで待機できている状態にする
                Socket socket = serversocket.accept();
                System.out.println("クライアントが接続：" + socket.getInetAddress());

                // 入出力ストリームの取得
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true); // trueをつけることで、オートフラッシュをONにし、out.println()等を実行したらすぐにクライアントのソケットに送信されるようにする



                // クライアントからのメッセージを受信 → リクエストを受け付ける
                String message = in.readLine();
                System.out.println(message);

                // クライアントに応答を送信 → レスポンスを送る
                out.println("サーバからの応答：" + message.toUpperCase());

                // ソケットを閉じる
                socket.close();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
