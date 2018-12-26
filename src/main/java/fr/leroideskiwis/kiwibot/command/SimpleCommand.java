package fr.leroideskiwis.kiwibot.command;

import java.lang.reflect.Method;

import fr.leroideskiwis.kiwibot.command.Command.ExecutorType;
import fr.leroideskiwis.kiwibot.Role;

public final class SimpleCommand {

	private final String name, description;
	private final ExecutorType executorType;
	private final Object object;
	private final Method method;
	private final Role needRole;

	public SimpleCommand(Role needRole, String name, String description, ExecutorType executorType, Object object, Method method) {
		super();
		this.name = name;
		this.description = description;
		this.executorType = executorType;
		this.object = object;
		this.needRole = needRole;
		this.method = method;
	}
	
	public boolean needRole(Role role){ return needRole == role; }
	public Role getNeededRole(){return needRole;}


	public String getName() {
		return name;
	}
	public String getDescription() {
		return description;
	}
	public ExecutorType getExecutorType() {
		return executorType;
	}
	public Object getObject() {
		return object;
	}
	public Method getMethod() {
		return method;
	}
	
}
