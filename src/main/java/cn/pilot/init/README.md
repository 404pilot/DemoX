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