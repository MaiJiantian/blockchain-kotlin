package websocket

import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.lang.Exception
import java.net.InetSocketAddress


/**
 * ClassName:MyServer
 * Description:
 */
class MyServer(port:Int): WebSocketServer(InetSocketAddress(port)) {
    /**
     * 打开连接
     */
    override fun onOpen(conn: WebSocket?, handshake: ClientHandshake?) {
        println("节点2服务端连接打开")
    }

    override fun onClose(conn: WebSocket?, code: Int, reason: String?, remote: Boolean) {
        println("节点2服务端连接关闭")
    }

    override fun onMessage(conn: WebSocket?, message: String?) {
        println("节点2服务端收到消息了")
    }

    override fun onStart() {
        println("节点2服务端开启了")
    }

    override fun onError(conn: WebSocket?, ex: Exception?) {
        println("节点2服务端失败了")
    }
}