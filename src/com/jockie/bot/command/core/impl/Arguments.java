package com.jockie.bot.command.core.impl;

import java.util.function.Function;

import com.jockie.bot.command.core.Argument;
import com.jockie.bot.command.core.Command;
import com.jockie.bot.main.JockieBot;
import com.jockie.sql.action.ActionGet;
import com.jockie.sql.base.Column;
import com.jockie.sql.base.Result;
import com.jockie.sql.base.Table;
import com.jockie.sql.base.Where.Operator;

import net.dv8tion.jda.core.entities.Message.Attachment;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Arguments {
	
	private static abstract class ArgumentImpl<T> implements Argument<T> {
		protected T default_value;
		protected boolean has_default;
		
		protected String value_information;
		
		protected Function<T, String> function_display;
		
		public ArgumentImpl(String information, boolean has_default, T default_value) {
			this.value_information = information;
			this.has_default = has_default;
			this.default_value = default_value;
		}
		
		public ArgumentImpl(String information) {
			this(information, false, null);
		}
		
		public void setDisplayFunction(Function<T, String> function_display) {
			this.function_display = function_display;
		}
		
		public boolean hasDefault() {
			return this.has_default;
		}
		
		public boolean requiresText() {
			return true;
		}
		
		public boolean canBeEndless() {
			return true;
		}
		
		public boolean hasToBeLast() {
			return false;
		}
		
		public String getValueInformation() {
			return this.value_information;
		}
		
		public String getDisplayableName() {
			return "UNKNOWN";
		}
		
		public T getDefault(MessageReceivedEvent event) {
			return default_value;
		}
		
		public String getDisplayableDefault(MessageReceivedEvent event) {
			if(function_display != null)
				return function_display.apply(this.getDefault(event));
			return this.getDefault(event).toString();
		}
		
		public abstract VerifiedValue<T> verify(MessageReceivedEvent event, Command command, String value);
	}
	
	public static class VerifiedValue<T> {
		
		private int verified;
		private T verified_value;
		
		private VerifiedValue(int verified, T verified_value) {
			this.verified = verified;
			this.verified_value = verified_value;
		}
		
		public int getVerified() {
			return this.verified;
		}
		
		public T getVerfiedValue() {
			return this.verified_value;
		}
	}
	
	public static class ArgumentString extends ArgumentImpl<String> {
		
		private boolean is_space_seperated;
		
		public ArgumentString(String information, String default_value) {
			super(information, true, default_value);
		}
		
		public ArgumentString(String information) {
			super(information);
		}
		
		public ArgumentString setSpaceSeperated(boolean is_space_seperated) {
			this.is_space_seperated = is_space_seperated;
			return this;
		}
		
		public boolean canBeEndless() {
			return (is_space_seperated) ? true : false;
		}
		
		public boolean hasToBeLast() {
			return (is_space_seperated) ? false : true;
		}
		
		public String getDisplayableName() {
			return "TEXT";
		}
		
		public VerifiedValue<String> verify(MessageReceivedEvent event, Command command, String value) {
			if(is_space_seperated) {
				return new VerifiedValue<String>(ARGUMENT_CORRECT, value);
			}else return new VerifiedValue<String>(COMMAND_END, value);
		}
	}
	
	public static class ArgumentNumber extends ArgumentImpl<Long> {
		
		public ArgumentNumber(String information, long default_value) {
			super(information, true, default_value);
		}
		
		public ArgumentNumber(String information) {
			super(information);
		}
		
		public String getDisplayableName() {
			return "NUMBER";
		}
		
		public VerifiedValue<Long> verify(MessageReceivedEvent event, Command command, String value) {
			try {
				long long_value = Long.parseLong(value);
				
				return new VerifiedValue<Long>(ARGUMENT_CORRECT, long_value);
			}catch(NumberFormatException e) {}
			
			return new VerifiedValue<Long>(ARGUMENT_INCORRECT, null);
		}
	}
	
	public static class ArgumentUser extends ArgumentImpl<User> {
		
		private boolean bot_triggerable = true;
		
		public ArgumentUser(String information, boolean has_default) {
			super(information, has_default, null);
			super.setDisplayFunction(User::getAsMention);
		}
		
		public ArgumentUser(boolean has_default) {
			this("Mention of user or their user id", has_default);
		}
		
		public ArgumentUser(String information) {
			this(information, false);
		}
		
		public ArgumentUser() {
			this("Mention of user or their user id", false);
		}
		
		public ArgumentUser setBotTriggerable(boolean bot_triggerable) {
			if(!bot_triggerable) {
				if(this.getValueInformation().equals("Mention of user or their user id")) {
					this.value_information = "Mention of non-bot user or their user id";
				}
			}else{
				if(this.getValueInformation().equals("Mention of non-bot user or their user id")) {
					this.value_information = "Mention of user or their user id";
				}
			}
			
			this.bot_triggerable = bot_triggerable;
			
			return this;
		}
		
		public boolean isBotTriggerable() {
			return this.bot_triggerable;
		}
		
		public String getDisplayableName() {
			return "USER";
		}
		
		public User getDefault(MessageReceivedEvent event) {
			return event.getAuthor();
		}
		
		public VerifiedValue<User> verify(MessageReceivedEvent event, Command command, String value) {
			if(value.length() >= 15) {
				if(value.startsWith("<@!"))
					value = value.substring(3);
				else if(value.startsWith("<@"))
					value = value.substring(2);
				
				if(value.charAt(value.length() - 1) == '>')
					value = value.substring(0, value.length() - 1);
				
				try {
					User user = event.getJDA().retrieveUserById(value).complete();
					
					if(user.isBot())
						if(this.isBotTriggerable() != true)
							return new VerifiedValue<User>(ARGUMENT_INCORRECT, null);
					
					return new VerifiedValue<User>(ARGUMENT_CORRECT, user);
				}catch(Exception e) {}
			}
			
			return new VerifiedValue<User>(ARGUMENT_INCORRECT, null);
		}
	}
	
	/**
	 * Could be replaced by ArgumentTypeValue
	 * @author Joakim
	 */
	public static class ArgumentBoolean extends ArgumentImpl<Boolean> {
		
		public ArgumentBoolean(String information, boolean default_value) {
			super(information, true, default_value);
		}
		
		public ArgumentBoolean(String information) {
			super(information);
		}
		
		public String getDisplayableName() {
			return "BOOLEAN";
		}
		
		public VerifiedValue<Boolean> verify(MessageReceivedEvent event, Command command, String value) {
			if(value.toLowerCase().equals("true")) {
				return new VerifiedValue<Boolean>(ARGUMENT_CORRECT, true);
			}else if(value.toLowerCase().equals("false")) {
				return new VerifiedValue<Boolean>(ARGUMENT_CORRECT, false);
			}
			
			return new VerifiedValue<Boolean>(ARGUMENT_INCORRECT, null);
		}
	}
	
	public static class ArgumentTypeValue extends ArgumentImpl<String> {
		
		public static class ArgumentEntry {
			
			private String description;
			
			private String[] triggers;
			
			private String value;
			
			public ArgumentEntry(String description, String value, String... triggers) {
				this.description = description;
				this.value = value;
				this.triggers = triggers;
			}
			
			public String getDescription() {
				return this.description;
			}
			
			public String[] getTriggers() {
				return this.triggers;
			}
			
			public String getValue() {
				return this.value;
			}
			
			public String valueOf(String str) {
				for(int i = 0; i < this.triggers.length; i++)
					if(this.triggers[i].toLowerCase().equals(str.toLowerCase()))
						return this.value;
				return null;
			}
		}
		
		private ArgumentEntry[] entries;
		
		public ArgumentTypeValue(String information, ArgumentEntry default_value, ArgumentEntry... argument_entries) {
			super(information, (default_value != null) ? true : false, (default_value != null) ? default_value.getValue() : null);
			
			if(default_value != null) {
				ArgumentEntry[] entries = new ArgumentEntry[argument_entries.length + 1];
				entries[0] = default_value;
				
				System.arraycopy(argument_entries, 0, entries, 1, argument_entries.length);
				
				this.entries = entries;
			}else{
				this.entries = argument_entries;
			}
		}
		
		public String[] getPossibleValues() {
			String[] str = new String[this.entries.length];
			for(int i = 0; i < this.entries.length; i++)
				str[i] = this.entries[i].getValue();
			return str;
		}
		
		public String getDisplayableName() {
			return "TYPE_VALUE";
		}
		
		public String valueOf(String str) {
			for(int i = 0; i < this.entries.length; i++)
				if(this.entries[i].valueOf(str) != null)
					return this.entries[i].valueOf(str);
			return null;
		}
		
		public ArgumentEntry[] getEntries() {
			return this.entries;
		}
		
		public VerifiedValue<String> verify(MessageReceivedEvent event, Command command, String value) {
			String str_value = valueOf(value);
			
			if(str_value != null)
				return new VerifiedValue<String>(ARGUMENT_CORRECT, str_value);
				
			return new VerifiedValue<String>(ARGUMENT_INCORRECT, null);
		}
	}
	
	public static class ArgumentDatabaseValue<T> extends ArgumentImpl<String> {
		
		private Column get_column;
		
		private Table table;
		private Operator where_operator;
		private Column where_column;
		
		private Function<MessageReceivedEvent, String> value_function;
		
		private Argument<T> argument_other;
		private Function<T, String> function_from_argument;
		
		public ArgumentDatabaseValue(Argument<T> argument_other, Function<T, String> function_from_argument, Column get_column, Table table, Operator where_operator, Column where_column, Function<MessageReceivedEvent, String> value_function) {
			super(argument_other.getValueInformation(), true, null);
			
			this.argument_other = argument_other;
			this.function_from_argument = function_from_argument;
			
			this.get_column = get_column;
			this.table = table;
			this.where_operator = where_operator;
			this.where_column = where_column;
			
			this.value_function = value_function;
		}
		
		public ArgumentDatabaseValue(Argument<T> argument_other, Function<T, String> function_from_argument, Column get_column, Table table, Column where_column, Function<MessageReceivedEvent, String> value_function) {
			this(argument_other, function_from_argument, get_column, table, Operator.EQUAL, where_column, value_function);
		}
		
		/**
		 * Null possible value, should be handled by the commands.
		 */
		public String getDefault(MessageReceivedEvent event) {
			ActionGet get = JockieBot.getDatabase().get(this.table);
			get.getWhere().where(this.where_column, this.where_operator, this.value_function.apply(event));
			get.getSelect().select(this.get_column);
			
			Result result = get.execute();
			
			if(result.next())
				return (String) result.getRows().get(0).getColumn(this.get_column.getValue());
			
			if(this.argument_other.hasDefault())
				return this.function_from_argument.apply(this.argument_other.getDefault(event));
			
			return null;
		}
		
		public String getDisplayableName() {
			return "DATABASE_$(" + this.argument_other.getDisplayableName() + ")";
		}
		
		public VerifiedValue<String> verify(MessageReceivedEvent event, Command command, String value) {
			VerifiedValue<T> verified = this.argument_other.verify(event, command, value);
			
			if(verified.getVerified() == Argument.ARGUMENT_CORRECT || verified.getVerified() == Argument.COMMAND_END)
				return new VerifiedValue<String>(verified.getVerified(), function_from_argument.apply(verified.getVerfiedValue()));
			
			return new VerifiedValue<String>(ARGUMENT_INCORRECT, null);
		}
	}
	
	/**
	 * Unsure of how to implement this because of the attachment offset, will remain with the old implementation and a deprecated annotation.
	 * @author Joakim
	 */
	
	@Deprecated
	public static class ArgumentAttachment extends ArgumentImpl<Attachment> {
		public ArgumentAttachment(String information, Attachment default_value) {
			super(information, true, default_value);
		}
		
		public ArgumentAttachment(String information) {
			super(information);
		}
		
		public String getDisplayableName() {
			return "ATTACHMENT";
		}
		
		public VerifiedValue<Attachment> verify(MessageReceivedEvent event, Command command, String value) {
			return new VerifiedValue<Attachment>(NOT_TESTED, null);
		}
	}
}