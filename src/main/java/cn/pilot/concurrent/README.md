# Concurrent

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