import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {
    public static void main(String[] args) {
        int port = 80;
        try(ServerSocket serversocket = new ServerSocket(port)) {
            while (true) {
                // クライアントからの接続を開いてこれで待機できている状態にする
                Socket socket = serversocket.accept();
                System.out.println("クライアントが接続：" + socket.getInetAddress());

                // リクエストの処理
                handleRequest(socket);

                // ソケットを閉じる
                socket.close();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private static void handleRequest(Socket socket) {
        try(
            // try-with-resource分にこいつら書くことでtry終わったら自動でバッファリーダー（読み込み）とプリントライター（書き込み）が閉じる
            // これがないと手書きでsocket.close()を書かないといけない
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                ){

            // 中身の具体実装
            String requestLine = in.readLine();
            String[] requestParams = requestLine.split(" ");
            String method = requestParams[0]; // POST、GETなどのメソッドを取る
            String path = requestParams[1]; // そういう程で送る　今回の場合 /ja?name=yamada の事ね
            String line; // ここでヘッダー様ね
            while (!(line = in.readLine()).equals("")) {
                System.out.println(line); // 空になるまで一行ずつinを見ていく
            }

            // パラメータ取得の部分
            // まずメソッドでの処理切り替えをする
            String body = "";
            if(method.equals("GET")){
                String[] query = path.split("//?");
                // name=hogehoge という形でGETのリクエストパラメータを送るので以下の様にしている
                String name = query[1].split("=")[1];
                String address = csvRead(name);
            }else if (method.equals("POST")){
                // リクエスト側から name=Katt&adress=arunbaというのがくる想定
                String[] nameAndAddress = in.readLine().split("&");
                String name = nameAndAddress[0].split("=")[1];
                String address = nameAndAddress[1].split("=")[1];
                boolean isWritten = csvWrite(name, address);
                if (isWritten){
                    body = "POSTによる書き込み成功";
                }else {
                    body = "POSTによる書き込み失敗";
                }
            }

            // HTTPレスポンスの送信
            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: text/plain");
            out.println("Content-Length: " + body.length());
            out.println(); // レスポンスヘッダーとレスポンスボディの境目
            out.println(body);

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private static boolean csvWrite(String name, String address) {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter("src/main/java/list.csv", true)); // とりまローカルにある形式問わずファイルいじるときは大体こんな感じかもね あとtrueは追記モード、これないと上書きですでにあるデータ吹き飛ぶ→ filewriterのところね、外のPrintWriterのところだとprintlnしたときに即っていう意味の実装になる
            pw.println(name + "," + address); // PrintWriterインスタンスに対してprintlnで書き込める感じかな、んでソケットの時は送りこめた感じ
            pw.close();
            return true;
        }catch(IOException e){
            e.printStackTrace();
            return false;
        }
    }

    private static String csvRead(String name) {
        try {
            BufferedReader br = new BufferedReader(new FileReader("src/main/java/list.csv"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equals(name)) {
                    return data[1];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "見つかりませんでした";
    }

}
