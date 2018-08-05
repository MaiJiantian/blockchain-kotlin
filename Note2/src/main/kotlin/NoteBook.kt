package bean

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.TypeReference
import utils.SHA256Utils
import java.io.File

/**
 * ClassName:NoteBook
 * Description:笔记本
 */
//笔记本保存的是什么?转账消息(String)
class NoteBook {
    //保存文件的名
    val path = "note.txt"
    //保存转账消息的集合
    val list = ArrayList<Block>()

    //添加笔记本封面
    fun addGenesis(genesis: String?): Boolean {
        try {
            //1.没有信息  才能添加
            require(list.size == 0 && genesis != null, { "只能有一个封面" })
            //首页上一个hash
            val preHash = "0000000000000000000000000000000000000000000000000000000000000000"
            //获取工作量证明
            val noce = mine(preHash,genesis!!)
            //求hash值
            val hash = SHA256Utils.getSHA256StrJava("${noce}${preHash}${genesis}")
            //创建Block对象
            val block = Block(list.size, genesis, hash, noce, preHash)
            //2.添加
            list.add(block)
            //3.保存到本地
            saveToDisk()
            return true
        } catch (e: Exception) {
            return false
        }
    }

    /**
     * 添加转账信息
     */
    fun addNote(content: String?): Boolean {
        try {//1.必须有封面(集合长度大于0)
            require(list.size > 0 && content != null, { "必须有封面才能添加转账消息" })
            //首页上一个hash
            val preHash = list.last().hash
            //获取工作量证明
            val noce = mine(preHash,content!!)
            //求hash值
            val hash = SHA256Utils.getSHA256StrJava("${noce}${preHash}${content}")
            //创建Block对象
            val block = Block(list.size, content, hash, noce, preHash)
            //2.添加
            //2.添加转账消息
            list.add(block)
            //3.保存到本地
            saveToDisk()
            return true
        } catch (e: Exception) {
            return false
        }
    }

    /**
     * 列举消息
     */
    fun listAll(): String {
        //将list集合转换为json字符串
        val str = JSON.toJSONString(list)
        return str
    }

    /**
     * 保存消息到本地
     */
    fun saveToDisk() {
        //1.获取数据的字符串
        val str = listAll()
        //2.保存到本地
        val file = File(path)
        file.writeText(str)
    }

    /**
     * 从本地加载数据
     */
    fun loadFromDisk() {
        //1.创建本地文件
        val file = File(path)
        //2.判断本地文件是否存在
        if (!file.exists()) return
        //3.存在就加载
        val str = file.readText()
        //4.转换为集合对象
        val parseList = JSON.parseObject(str, object : TypeReference<ArrayList<Block>>() {})//集合应该传递type
        //5.添加到集合中
        list.clear()
        list.addAll(parseList)
    }

    /**
     * 篡改数据
     */
    fun modiry(index: Int, content: String) {
        trueModity(index, content)

        //修改后面所有的节点
        //1.是否是最后一个节点
        if (index + 1 < list.size) {//下一条不是最后一条
            //满足条件
            ((index + 1)..list.size - 1).forEach {
                trueModity(it, list[it].content)
            }
        }
        //保存到本地
        saveToDisk()
    }

    private fun trueModity(index: Int, content: String) {
        //获取数据
        val block = list[index]
        //修改preHash  注意
        if (index > 0) {
            block.preHash = list[index - 1].hash
        }
        //工作量证明
        val noce = mine(block.preHash,content)
        //篡改hash值
        val hash = SHA256Utils.getSHA256StrJava("${noce}${block.preHash}${content}")
        //修改数据
        block.content = content
        //修改hash
        block.hash = hash
        //修改工作量证明
        block.noce = noce

    }

    /**
     * 挖矿  穷举数据
     * @param 内容
     * @return 工作量证明
     */
    fun mine(preHash:String,content: String): Int {
        (0..Int.MAX_VALUE).forEach {
            val hash = SHA256Utils.getSHA256StrJava("${it}${preHash}${content}")
            //hash必须以000000开头才满足
            if (hash.startsWith("000")) {
                return it
            }
        }
        throw Exception("挖矿失败")
    }

    /**
     * 更新节点
     */
    fun updateNote(list:ArrayList<Block>){
        if(list.size> this.list.size){
            //更新自己的节点
            this.list.clear()
            this.list.addAll(list)
        }
    }
}

//工作量证明  让求的hash满足一定的要求 必须是以000000开头
//信息排序
class Block(var id: Int, var content: String, var hash: String, var noce: Int, var preHash: String)