package com.jockie.bot.command.core.non_command;

public class NonCommandTriggerPoint {
	
	private String initial_message_id;
	
	private String command;

	private Object object;
	
	private boolean is_paged;
	
	public NonCommandTriggerPoint(String initial_message_id, String command, Object object) {
		this.initial_message_id = initial_message_id;
		this.command = command;
		this.object = object;
		
		if(object instanceof PagedResult<?>)
			this.is_paged = true;
	}
	
	public NonCommandTriggerPoint(String command, Object object) {
		this(null, command, object);
	}
	
	public String getInitalMessageId() {
		return this.initial_message_id;
	}
	
	public void setMessageId(String message_id) {
		this.initial_message_id = message_id;
	}
	
	public String getCommand() {
		return this.command;
	}
	
	public Object getObject() {
		return this.object;
	}
	
	public boolean isPaged() {
		return this.is_paged;
	}
}