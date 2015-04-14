# Access Modifier

`protected` & `default` are **package private**. They can only be seen or used by the **package** in which it was declared.

## Usage

In my opinion, sometimes `default` can be used to restrict other class from accessing its methods. In this case, some mediator class may not needed. Sometimes, an certain class don't want to expose all methods to other classes.

Take a State Pattern for example

    class VendingMachine{
        void changeState() // use default here
        
        public void insertCoin()
        public void pressButton()
    }
    
    class InsertedState;
    class ReadyState;

When `VendingMachine vm = new VendingMachine()` is used in **client** which is normally not in a same package, `vm.changeState()` should not exposed to client, otherwise client can change the state which can result in misbehavior. 

And also, `default` can be used for constructor, so some classes can be only initialized inside the package.