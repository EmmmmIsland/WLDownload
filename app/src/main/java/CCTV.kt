import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.system.measureTimeMillis

/**
 * User: wanglei
 * Date: 2023/4/4 9:31
 * Description:
 */
// TODO ======== 案例8 ===================
//fun main() {
//    val useTime = measureTimeMillis {
//        runBlocking<Unit> {
//            println("main start") // 1
//
//            val job = GlobalScope.launch { // TODO 全局协程
//                println("launch 1 start") // 2
//                delay(1000L) // 延迟1
//                println("launch 1 end") // 3
//            }
//
//            println("main mid") // 4
//            launch(context = job) { // TODO 子协程
//                println("launch 2 start") // 5
//                delay(2000L) // 延迟2
//                println("launch 2 end") // 6
//            }
//
//            println("main end") // 7
//        }
//    }
//
//    println("使用时间: $useTime")
//}
//
//fun main() {
//    runBlocking {
//        val channel = Channel<String>(capacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
//        printlnThread("main start")
//        launch {
//            printlnThread("result 111")
//        }
//
//        launch {
//            printlnThread("result 222")
//        }
//    }
//}

//fun main() {
//    runBlocking {
//        val flow = MutableStateFlow("默认数据")
//        launch {
//            flow.collect{
//                printlnThread("res$it")
//            }
//        }
//        launch {
//            flow.emit("flowqqqqqqqqqq")
//        }.join()
//
//        launch {
//            flow.emit("flow222222")
//        }.join()
//
//        launch {
//            flow.emit("flow333333")
//        }.join()
//    }
//}

private suspend fun <T> requestLoginNetworkData(account: String, pwd: String): String {
    return withContext(Dispatchers.IO) {
        delay(2000)  // 模拟网络耗时需要2s
        return@withContext suspendCoroutine {
            if (account == "123456789" && pwd == "666666") {
                it.resume("登陆成功")
            } else {
                it.resumeWithException(RuntimeException("登陆失败"))
            }
        }
    }
}

suspend fun main() = runBlocking {
    val scope = CoroutineScope(Dispatchers.IO)
    // 开启一个协程
    val deferred = scope.async {
        // 模拟网络请求
        requestLoginNetworkData<String>("987654321", "666666")
    }
    // 获取网络返回数据，判断成功与失败
    val result = runCatching {
        deferred.await()
    }

    if (result.isSuccess) {
        printlnThread("登陆成功:${result.getOrNull()}")
    } else {
        printlnThread("登陆失败:${result.exceptionOrNull()}")
    }
}



fun printlnThread(any: Any) {
    println("$any:\tthread:${Thread.currentThread().name}")
}