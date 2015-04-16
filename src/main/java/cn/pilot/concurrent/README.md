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

#### Join

Using `aThread.join()` to let current thread wait until `aThread` is finished.

Threads scheduling is controlled by thread scheduler, there is no guarantee for a certain sequence for running threads.