# Thread and Concurrent

## synchronized

#### synchronized void method()

	int var = 1;
	
	synchronized method(){
		return var;
	}

Using synchronized allows only one thread is executing `method()`, but other methods can access and change `var` at same time.

#### synchronized (this)

Use this (current object instance) as a **lock**.

Once a **lock** is held by one thread, other threads need to wait for this locked being released before acquiring it.

It is basically the same as synchronized method, the concurrent block is only accessible for a single thread during a certain time.

This is a fine-grained way to control concurrent block comparing using synchronized method. The codes, which are outside concurrent block but still inside the method, can be executed by two different threads at the same time.

## Thread

### Basic

#### JUnit with multi threads

JUnit will call `System.exit()` in the end of test, even there are other threads running at same time.

Main thread (test thread) is not blocked and "destroy()" method will be called.

But for java program, `main(String[] args)` will end after all threads end.

#### join()

    main(){
        aThread.join();
    }

Using `aThread.join()` to let `main` thread wait until `aThread` is finished.

Threads scheduling is controlled by thread scheduler, there is no guarantee for a certain sequence for running threads.

#### wait() & notify()

`lockObject.wait()` and `lockObject.notify()` are using inside `sychronized(lockObject)` block.

`lockObject.wait()` will

1. release the holding of lockObject
2. block current thread and wait for `notify()` being called.

#### wait() & sleep()

    synchronized(lockObject){
        Thread.sleep(1000);
    }
    
`Thread.sleep()` will not release the holding of lockObject.

#### yield() & sleep() 

`Thread.yield()` and `Thread.sleep()` are both Thread's static method.

`yield()` will:

1. put current thread into block state and wait for acquiring CPU next time.
2. so other threads have chance to get current CPU. But `yield()` only give chances to those threads with the same priority.

`sleep()` could give chances to some threads with a lower priority to get CPU.


### How to stop a thread

Thread.stop()不安全，已不再建议使用。


## HashMap

HashMap can be used for multiple threads for accessing different keys.

## Executors

### Runnable & Callable

* Runnable 无返回值，可以抛出 checked exception
* Callable 有返回值，不能抛出 checked exception

Callable 可以替换 Runnable 了

### shutdown() or shutdownNow()

* `shutdown()`会等待当前 thread 运行完终止
* `shutdownNow`直接终止当前 thread
* `awaitTermination(timeout)`用在这两个方法之后，会返回 true/false 表明`shutdown()`和`shutdownNow()`是否在 timeout 之内终止成功。
	* 当然，timeout 只是一个 max 值，如果 thread 在 timeout 之前就终止了的话，`awaitTermintaion()`是不会阻塞这个 thread 的

**Best Practice**:

``` java
pool.shutdown(); // Disable new tasks from being submitted

try {
  // Wait a while for existing tasks to terminate
  if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
      pool.shutdownNow(); // Cancel currently executing tasks
      // Wait a while for tasks to respond to being cancelled
      if (!pool.awaitTermination(60, TimeUnit.SECONDS))
          System.err.println("Pool did not terminate");
  }
} catch (InterruptedException ie) {
  // (Re-)Cancel if current thread also interrupted
  pool.shutdownNow();
  // Preserve interrupt status
  Thread.currentThread().interrupt();
}
```

### Schedule()
* `scheduleAtFixedRate(command, 1, 2, second)`：确定**固定时间点**去 schedule
	* 如果 command 运行一次要花费1s，command 会从 1s、3s、5s、7s... 运行下去（从1s 开始是因为有个1s 的 initial delay）
	* 如果 command 运行一次要花费3s，command 会从`1s, 4s, 7s, 10s...`运行下去（不要想当然 pool 会在 fix period 到达的时候 create 另一个 command 去运行）
	* 当 command 花费时间大于 rate 的时间的时候，每次 command 一结束马上就开始新的运行
* `scheduleWithFixedDelay(command, 1, 2, second)`：确定**每次 command 结束之后 delay 多久**去 schedule
	* 如果 command 运行一次要花费3s，command 从1s，6s，11s..运行下去（每两个 command 之间的间隔是3s+2s 的 delay）


