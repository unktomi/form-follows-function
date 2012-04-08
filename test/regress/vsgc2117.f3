/*
 * @test
 * @run
 */

import java.lang.System;

class Person{
    public var name:String;
}

class Company{
    public var persons: Person[] on replace oldValue[lo..hi] = newVals{
        System.out.println("****** trigger persons [{lo} .. {hi}]");
        for(person in oldValue[lo..hi]){System.out.println("Delete: {person.name}"); }
        for(person in newVals){ System.out.println("Add: {person.name}"); }
    };

}

var persons:Person[];

var handler = Company{
    persons: bind persons with inverse
};

insert Person { name: "Peter" } into persons;
insert Person { name: "John" } into persons;
