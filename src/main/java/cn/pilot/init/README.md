# Init

## Block

1. Assign memory for `var` by finding its type
2. Assign `var` from top to bottom
3. Run constructor

Note that only assignment operation is allowed in the first block before declaring it.

    {
        s = "1"; // legal
        // s.charAt(0) // illegal
    }
    
    String s = "2";

## Weird

Note that

* class variables will always be initialized first.
* only one copy of each class variable exists in memory.

Steps:

1. declare all class-level variables first from top to bottom

        int CLASS_FOO = 0;
        Weird INSTANCE = null;
        int CLASS_BAR = 0;

2. init all class-level variables one by one from top to bottom

  	* init `CLASS_FOO`:
    
			int CLASS_FOO = 20;
			Weird INSTANCE = null;
			int CLASS_BAR = 0;

	* init `INSTANCE`: it also needs to init this class (repeat):

			// declare two instance-level variable
			int foo = 0;
			int bar = 0;
			
			// then run assignments inside constructor
			foo = 20 - 10 = 10;
			bar = 0 - 10 = -10;
			
	* init `CLASS_BAR`


			int CLASS_FOO = 20;
			Weird INSTANCE = (foo=10,bar=-10);
			int CLASS_BAR = 20;

## Summary

1. 先初始化class variables，然后instance variables
2. 对于variables，先声明所有variables的类型（分配空间，并且有默认值），再按从上到下的顺序，加上constructor里的逻辑，按顺序赋值。
3. Class是编译期就生成好，静态变量已经初始化完毕。

## Inheritance

### init

1. 为child&parent分配空间（给予default value）
2. 初始化parent
3. 初始化child

new Child()：

1. [Parent] 运行`this.parentVar = this.weird`

2. [Parent] 运行`this.weird = this.weird()`

	第一个this是parent类，第二个this是child 
	
3. [Parent] 运行`child.weird()`

	这个时候child还未初始化，但是内存已经分配了空间，但是只有默认值
	
4. [Child] 运行`this.var = 3`
	覆写了`var`

### this

* this.var --> this 为代码所在的类
* this.method() --> this 为正在new的那个类

	`Parent child = new Child()`
	
	无论是在constructor里，还是parent的method里，对于this.method()而言是运行期动态绑定的，但是this只能看到declaredClass的methods，因为要编译时只会知道declaredClass类下的methods。
	
	类似于`Parent child`中child只能调用Parent声明的methods。



