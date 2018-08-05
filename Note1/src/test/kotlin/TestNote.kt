import bean.NoteBook
import org.junit.Test

/**
 * ClassName:TestNote
 * Description:
 */
class TestNote {
    @Test
    fun testAddGenesis(){
        val noteBook = NoteBook()
        val result = noteBook.addGenesis("笔记本")
        println(result)
    }
    @Test
    fun testAddNote(){
        val noteBook = NoteBook()
        noteBook.addGenesis("笔记本")
        val result = noteBook.addNote("张三给王五转账3毛钱")
        println(result)
    }
    @Test
    fun testListAll(){
        val noteBook = NoteBook()
        noteBook.addGenesis("笔记本")
        val result = noteBook.addNote("张三给王五转账3毛钱")
        val content = noteBook.listAll()
        println(content)
    }
}