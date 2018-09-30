package Vid13;

import java.util.Arrays;
import java.util.List;
import java.util.Collections;

public class Vid13 {

	public static void main(String[] args) {
		List<Person> people = Arrays.asList(
				new Person("Charles", "Dickens", 60),
				new Person("Lewis", "Carroll", 42),
				new Person("Thomas", "Carlyle", 51),
				new Person("Charlotte", "Bronte", 45),
				new Person("Matthew", "Arnold", 39)
				);
		
		// Step 1: Sort by last name
		Collections.sort(people, (Person a, Person b)->{
			return a.getLastName().compareTo(b.getLastName());
		});
		// Step 2: Create a method that prints list
		Runnable printPeople = ()->{
			people.forEach((person)->{
				System.out.println(person);
			});
		};
		printPeople.run();
		// Step 3: Create a method that prints people whose last name begins with C 
		System.out.println();
		people.stream()
		.filter((person)->{return person.getLastName().startsWith("C");})
		.forEach((person)->{System.out.println(person);});
	}

}
