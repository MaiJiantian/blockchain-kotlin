package bean


/**
 * ClassName:BroadBean
 * Description:广播bean类  广播类型(1:任务  2:同步节点)  广播节点  广播任务对象 广播更新列表集合
 */
class BroadBean(val type:Int,val node:Int,val transaction: Transaction?,val list:ArrayList<Block>?)