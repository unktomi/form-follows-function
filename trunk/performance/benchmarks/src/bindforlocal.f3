
class bindforlocal extends cbm {
    var max:Number = 100000;
    var min:Number = 0;
    override public function test() {
        var values = bind for (i in [min..max]) {
            i;
        }
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
    var t = new bindforlocal();
    cbm.runtest(args,t);
}

