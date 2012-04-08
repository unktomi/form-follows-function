/*
 * Feature test #16 - sequence insert and delete
* 
 * @test
 * @run
 */

import java.lang.System;

var names = ["Bob", "Carol", "Ted", "Alice"];
delete names[1];
System.out.println(names);
delete "Alice" from names;
System.out.println(names);
delete "Berty" from names;
System.out.println(names);
insert "James" into names;
System.out.println(names);
insert "Max" into names;
System.out.println(names);
delete names;
insert "Self" into names;
insert "Self" into names;
System.out.println(names);

var nums = [0..10];
insert 97 into nums;
insert 98 into nums;
insert 99 into nums;
System.out.println(nums);
delete nums[10];
System.out.println(nums);
delete nums[8];
System.out.println(nums);
delete nums[6];
System.out.println(nums);
delete nums[4];
System.out.println(nums);
delete nums[2];
System.out.println(nums);
delete nums[0];
System.out.println(nums);
delete nums;
System.out.println(nums);
for (a in [1..20]) { insert a*a into nums; };
System.out.println(nums);
for (a in [1..200]) { delete a*2 from nums; };
System.out.println(nums);

nums = [0..10];
delete nums[3..7];
System.out.println(nums);
nums = [0..10];
delete nums[3..<7];
System.out.println(nums);
nums = [0..10];
delete nums[5..];
System.out.println(nums);
nums = [0..10];
delete nums[5..<];
System.out.println(nums);
nums = [0..10];
delete nums[9..2];
System.out.println(nums);

nums = [0..10];
insert 101 before nums[sizeof nums+2];
insert 100 before nums[sizeof nums+1];
insert 99 before nums[sizeof nums];
insert 98 before nums[6];
insert 97 before nums[1];
insert 96 before nums[0];
insert 95 before nums[-1];
insert 94 before nums[-2];
System.out.println(nums);

names = ["Evelyn", "Ann"];
insert ["Daz", "Barb"] before names[sizeof names];
insert ["Melissa", "Ron"] before names[1];
insert ["Jim", "Marsha"] before names[0];
System.out.println(names);

nums = [0..10];
insert 101 after nums[sizeof nums+1];
insert 100 after nums[sizeof nums];
insert 99 after nums[sizeof nums - 1];
insert 98 after nums[5];
insert 97 after nums[0];
insert 96 after nums[-1];
insert 95 after nums[-2];
insert 94 after nums[-3];
System.out.println(nums);

names = ["Evelyn", "Ann"];
insert ["Daz", "Barb"] after names[sizeof names - 1];
insert ["Melissa", "Ron"] after names[1];
insert ["Jim", "Marsha"] after names[0];
System.out.println(names);

// Some tests for inserting into empty sequence,
// as well as out-of-bounds insertions:
var nums1 : Integer[] = []; var nums2 : Integer[] = [];
insert 100 after nums1[0]; insert 100 before nums2[1];
System.out.println("insert/1 into []: {nums1} = {nums2}");
nums1 = []; nums2 = [];
insert 99 after nums1[-1]; insert 99 before nums2[0];
System.out.println("insert/2 into []: {nums1} = {nums2}");
nums1 = []; nums2 = [];
insert 98 after nums1[-2]; insert 98 before nums2[-1];
System.out.println("insert/3 into []: {nums1} = {nums2}");
nums1 = []; nums2 = [];
insert 97 after nums1[1]; insert 97 before nums2[2];
System.out.println("insert/4 into []: {nums1} = {nums2}");

