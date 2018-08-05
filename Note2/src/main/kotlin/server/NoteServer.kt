package server

import bean.BroadBean
import bean.NoteBook
import bean.Transaction
import com.alibaba.fastjson.JSON
import io.ktor.application.call
import io.ktor.content.resources
import io.ktor.content.static
import io.ktor.request.receive
import io.ktor.request.receiveParameters
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import rsautil.RSAUtil
import rsautil.SignatureUtils
import utils.SHA256Utils
import websocket.MyClient
import websocket.MyServer


/**
 * ClassName:NoteServer
 * Description:
 */
val noteBook = NoteBook()
//websocket服务端
val server = MyServer(80)
//客户端地址
val clientList = listOf(MyClient("ws://localhost:70"),MyClient("ws://localhost:90"))
fun main(args: Array<String>) {
    //启动websocket服务端
    Thread(server).start()
    //客户端连接服务器
    Thread {
        Thread.sleep(10000)
        clientList.forEach { it.connect() }
    }.start()

    //从本地加载数据
    noteBook.loadFromDisk()

    embeddedServer(Netty, 81) {
        routing {
            static("static") {//路由地址
                resources("static")//静态界面放在static文件夹下
            }
            /**
             * 添加首页
             */
            post("addGenesis"){
                //1.获取添加的数据(获取body 的value  根据key为genesis)
                val genesis = call.receiveParameters().get("genesis")

                //2.添加到笔记本里面
                val result = noteBook.addGenesis(genesis)
                //3.有没有成功
                if(result){
                    //成功
                    call.respondText("添加首页成功")
                }else{
                    call.respondText("添加首页失败")
                }
            }
            //添加笔记内容
            post("addNote"){
                //1.获取添加的数据(获取body 的value  根据key为genesis)
                val content = call.receiveParameters().get("content")

                //2.添加到笔记本里面
                val result = noteBook.addNote(content)
                //3.有没有成功
                if(result){
                    //成功
                    call.respondText("添加内容成功")
                }else{
                    call.respondText("添加内容失败")
                }
            }

            //获取所有的信息
            get("listAll"){
                //1.查询笔记本中所有的信息
                val str = noteBook.listAll()
                //2.返回给客户端
                call.respondText(str)
            }
            //串改数据
            post("modify"){
                //串改那一条数据(index)? 串改的数据(content)
                val params = call.receiveParameters()//只能从管道里面获取一次
                val index = params.get("index")!!.toInt()
                val content = params.get("content")!!
                //修改笔记本数据
                noteBook.modiry(index,content)

                //返回修改成功
                call.respondText("篡改数据成功")
            }
            //数据验证
            get("check"){
                //遍历集合查看每一条数据是否正确
                val sb = StringBuffer()
                //遍历集合  是否篡改
                val list = noteBook.list
                list.forEachIndexed { index, block ->
                    //工作量证明
                    val noce = block.noce
                    //内容
                    val content = block.content
                    //判断当前条数据求取的hash值(工作量证明和content内容)和保存的hash值是否一致
                    //保存的上一个hash和真实的上一个hash是否一致
                    val savePreHash = block.preHash
                    val truePreHash = if(index==0) "0000000000000000000000000000000000000000000000000000000000000000" else list.get(index-1).hash

                    //第一条数据
                    val curHash = block.hash
                    val hash = SHA256Utils.getSHA256StrJava("${noce}${savePreHash}${content}")
                    if(curHash.equals(hash)&&savePreHash.equals(truePreHash)){
                        //数据正确
                        sb.append("第${index}条数据正确\r\n")
                    }else{
                        //数据不正确
                        sb.append("第${index}条数据错误\r\n")
                    }
                }
                //返回检查的结果
                call.respondText(sb.toString())
            }
            /**
             * 接收转账请求
             */
            post("transaction"){
                println("收到请求")
                //获取数据
                val msg = call.receive<String>()
                //2.自己验证
                //将获取的信息解析成对象
                val transaction = JSON.parseObject(msg, Transaction::class.java)
                //1.把任务广播给其他节点
                val broadBean = BroadBean(1,1,transaction,null)
                //将数据转换为json数据格式
                val broadMsg = JSON.toJSONString(broadBean)
                server.broadcast(broadMsg)

                //明文
                val content = transaction.content
                //公钥字符串
                val publicStr = transaction.publicKey
                //将公钥字符串还原为公钥对象
                val publicKey = RSAUtil.createPublicKey(publicStr)
                //签名之后的密文
                val signed = transaction.signed
                val result = SignatureUtils.verifySignature(content, publicKey, signed)
                if (result) {
                    println("数据正确")
                    //保存到本地节点里面
                    noteBook.addNote(content)
                    //广播给其他节点进行更新
                    val broadBean = BroadBean(2,1,null,noteBook.list)
                    //json字符串
                    val msg = JSON.toJSONString(broadBean)
                    server.broadcast(msg)
                } else {
                    println("数据错误")
                }
                call.respondText("请求响应")
            }
        }
    }.start(wait = true)
}