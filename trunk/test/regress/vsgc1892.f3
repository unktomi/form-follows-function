import java.lang.System; 

/* This scenario is -> define a sequce seq:
 *                   -> bind slice on seq: on some filter to a slice slc:
 *                   -> bind size of slice slc: to a variable count.
 *                   -> expected output should be: count == sizeof slice slc:
 * 
 *  @test
 *  @run
 *   
 */
function testSeqenceBind(): Void {
       testSequenceBind1();
       testSequenceBind2();
       testSequenceBind3();
}

function testSequenceBind1() {

	var seq = ['a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v'];

	var filter = "";
	var seq1 = bind seq[n | filter.indexOf(n) != -1 ];

	var count = bind sizeof seq1;

	print(count,seq1);
	
	filter = "efgh";
	print(count,seq1);

        filter = "abcdefgh";
	print(count,seq1);
	
	filter = "v";
	print(count,seq1);
		
	filter = "ijkmnopqrstuvwxyz";
	print(count,seq1);

	filter = "l";
	print(count,seq1);

}


function testSequenceBind2(){
       
	var values = [1,2,3,4,5,6,7,8];
	var filter1 = 2;
	var filteredValues1 = bind if ( filter1 == 2 ) 
	                              values
	                           else    
	                              values[n | n >filter1];
                              
	var count = bind sizeof filteredValues1;
	print(count,filteredValues1);
	
	filter1 = 3; 
	print(count,filteredValues1);	

	filter1 = 5; 
	print(count,filteredValues1);
	
}


function testSequenceBind3():Void {
	var values = ["A", "AB", "AC", "CD", "E", "F", "G"]; 

	var filter = ""; 

	var filteredValues = bind if (filter == "") values 
                          else values[n | n.matches(filter)] ;
                          
                          
	var count = bind sizeof filteredValues ;
        print(count,filteredValues);
	
	filter = "A.*"; 
	print(count,filteredValues);
	
	filter = "AB.*"; 
	print(count,filteredValues);
	
}
function print(count:Integer, seq: Object[]):Void {
    	System.out.println("count : {count} - sizeof seq: {sizeof seq} - sequence {seq.toString()}");
}

function run(){
 	testSeqenceBind();
}
