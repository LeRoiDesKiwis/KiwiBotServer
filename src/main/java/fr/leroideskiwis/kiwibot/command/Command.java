package fr.leroideskiwis.kiwibot.command;

import fr.leroideskiwis.kiwibot.Role;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value=ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)

public @interface Command {

	public String name();
	public String description() default "aucune description";
	public ExecutorType type() default ExecutorType.USER;
	public Role role() default Role.MEMBER;
	public String[] aliases() default {};


	public enum ExecutorType{
		ALL, USER, CONSOLE;
	}
	
}
