

class bindforglobal extends cbm {
    var max:Number = 100000;
    var min:Number = 0;
    def values = bind for (i in [min..max]) {
       i;
    }
    override public function test() {    
        for (x in [min..max]) {
            for (y in [max..min]) {
                max = x;
                min = y;
            }
        }
         return 0;
    }//test

};//class

/**
 * define a run method to call runtests passing args and instance of this class
 */
public function run(args:String[]) {
    var t = new bindforglobal();
    cbm.runtest(args,t);
}

