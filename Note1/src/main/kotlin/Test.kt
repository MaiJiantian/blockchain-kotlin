import rsautil.RSAUtil

/**
 * ClassName:Test
 * Description:
 */
fun main(args: Array<String>) {
    /*---------------------------- 保存 ----------------------------*/
//    val noteBook = NoteBook()
//    noteBook.addGenesis("区块链一期笔记本")
//    val result = noteBook.addNote("张三给王五转账3毛钱")
//    noteBook.saveToDisk()

    /*---------------------------- 加载数据 ----------------------------*/
//    val str = noteBook.listAll()
//    println(str)
    /*---------------------------- 生成公钥和私钥 ----------------------------*/
//    RSAUtil.generatePublicAndPrivateKey()
    /*---------------------------- 生成转账地址公钥 ----------------------------*/
    RSAUtil.generatePublicKey()
}