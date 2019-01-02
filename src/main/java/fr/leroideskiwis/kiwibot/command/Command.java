package fr.leroideskiwis.kiwibot.command;

import fr.leroideskiwis.kiwibot.Role;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value=ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)

public @interface Command {

	String name();
	String description() default "aucune description";
	ExecutorType type() default ExecutorType.USER;
	Role role() default Role.MEMBER;
	String[] aliases() default {};


	enum ExecutorType{
		ALL, USER, CONSOLE
	}
	
}
