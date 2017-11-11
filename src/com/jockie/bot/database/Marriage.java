package com.jockie.bot.database;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import com.jockie.bot.database.column.PersonColumn;
import com.jockie.bot.main.JockieBot;
import com.jockie.sql.action.ActionDelete;
import com.jockie.sql.action.ActionGet;
import com.jockie.sql.action.ActionInsert;
import com.jockie.sql.action.ActionSet;
import com.jockie.sql.base.Result;
import com.jockie.sql.base.Where.Operator;

public class Marriage {
	
	public enum Propose {
		PROPOSER,
		PROPOSEDTO,
		NONE;
	}
	
	public static void forceMarry(String proposer, String partner, boolean canDivorce) {
		Result proposer_info = getMarriageInfo(proposer);
		proposer_info.next();
		
		Result partner_info = getMarriageInfo(partner);
		partner_info.next();
		
		ArrayList<ActionSet> set_all = new ArrayList<ActionSet>();
		
		if(Boolean.parseBoolean((String) proposer_info.getRows().get(0).getColumn("MARRIED"))) {
			ActionSet set = JockieBot.getDatabase().set(Database.PERSON);
			set.getSet()
				.set(PersonColumn.MARRIED, "FALSE")
				.set(PersonColumn.PROPOSE, Propose.NONE.toString())
				.set(PersonColumn.MARRIAGE_DATE, null)
				.set(PersonColumn.PARTNER, null);
			set.getWhere().where(PersonColumn.USER_ID, Operator.EQUAL, proposer_info.getRows().get(0).getColumn("PARTNER"));
			
			set_all.add(set);
		}else if(Propose.valueOf((String) proposer_info.getRows().get(0).getColumn("PROPOSE")) != Propose.NONE) {
			ActionSet set = JockieBot.getDatabase().set(Database.PERSON);
			set.getSet()
				.set(PersonColumn.PROPOSE, Propose.NONE.toString())
				.set(PersonColumn.PARTNER, null);
			set.getWhere().where(PersonColumn.USER_ID, Operator.EQUAL, proposer_info.getRows().get(0).getColumn("PARTNER"));
			
			set_all.add(set);
		}
		
		if(Boolean.parseBoolean((String) partner_info.getRows().get(0).getColumn("MARRIED"))) {
			ActionSet set = JockieBot.getDatabase().set(Database.PERSON);
			set.getSet()
				.set(PersonColumn.MARRIED, "FALSE")
				.set(PersonColumn.PROPOSE, Propose.NONE.toString())
				.set(PersonColumn.MARRIAGE_DATE, null)
				.set(PersonColumn.PARTNER, null);
			set.getWhere().where(PersonColumn.USER_ID, Operator.EQUAL, partner_info.getRows().get(0).getColumn("PARTNER"));
			
			set_all.add(set);
		}else if(Propose.valueOf((String) partner_info.getRows().get(0).getColumn("PROPOSE")) != Propose.NONE) {
			ActionSet set = JockieBot.getDatabase().set(Database.PERSON);
			set.getSet()
				.set(PersonColumn.PROPOSE, Propose.NONE.toString())
				.set(PersonColumn.PARTNER, null);
			set.getWhere().where(PersonColumn.USER_ID, Operator.EQUAL, partner_info.getRows().get(0).getColumn("PARTNER"));
			
			set_all.add(set);
		}
		
		Date date = new Date();
		TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
		
		ActionSet set_proposer = JockieBot.getDatabase().set(Database.PERSON);
		set_proposer.getSet()
			.set(PersonColumn.MARRIED, "TRUE")
			.set(PersonColumn.PROPOSE, Propose.PROPOSER.toString())
			.set(PersonColumn.PARTNER, partner)
			.set(PersonColumn.MARRIAGE_DATE, new Timestamp(date.getTime()).toString())
			.set(PersonColumn.CAN_DIVORCE, canDivorce);
		set_proposer.getWhere().where(PersonColumn.USER_ID, Operator.EQUAL, proposer);
		
		set_all.add(set_proposer);
		
		ActionSet set_partner = JockieBot.getDatabase().set(Database.PERSON);
		set_partner.getSet()
			.set(PersonColumn.MARRIED, "TRUE")
			.set(PersonColumn.PROPOSE, Propose.PROPOSEDTO.toString())
			.set(PersonColumn.PARTNER, proposer)
			.set(PersonColumn.MARRIAGE_DATE, new Timestamp(date.getTime()).toString())
			.set(PersonColumn.CAN_DIVORCE, canDivorce);
		set_partner.getWhere().where(PersonColumn.USER_ID, Operator.EQUAL, partner);
		
		set_all.add(set_partner);
		
		JockieBot.getDatabase().cluster(set_all.toArray(new ActionSet[0])).execute();
	}
	
	public static void marry(String proposer, String partner) {
		TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
		
		Date date = new Date();
		
		ActionSet set_proposer = JockieBot.getDatabase().set(Database.PERSON);
		set_proposer.getSet()
			.set(PersonColumn.MARRIED, "TRUE")
			.set(PersonColumn.MARRIAGE_DATE, new Timestamp(date.getTime()).toString());
		set_proposer.getWhere().where(PersonColumn.USER_ID, Operator.EQUAL, proposer);
			
		ActionSet set_partner = JockieBot.getDatabase().set(Database.PERSON);
		set_partner.getSet()
			.set(PersonColumn.MARRIED, "TRUE")
			.set(PersonColumn.MARRIAGE_DATE, new Timestamp(date.getTime()).toString());
		set_partner.getWhere().where(PersonColumn.USER_ID, Operator.EQUAL, partner);
		
		JockieBot.getDatabase().cluster(set_proposer, set_partner).execute();
	}
	
	public static void divorce(String divorcer, String partner) {
		ActionDelete delete_divorcer = JockieBot.getDatabase().delete(Database.PERSON);
		delete_divorcer.getWhere().where(PersonColumn.USER_ID, Operator.EQUAL, divorcer);
		
		ActionDelete delete_partner = JockieBot.getDatabase().delete(Database.PERSON);
		delete_partner.getWhere().where(PersonColumn.USER_ID, Operator.EQUAL, partner);
		
		JockieBot.getDatabase().cluster(delete_divorcer, delete_partner).execute();
	}
	
	public static void propose(String proposer, String partner) {
		ActionInsert insert_proposer = JockieBot.getDatabase().insert(Database.PERSON);
		insert_proposer.getInsert()
			.insert(PersonColumn.USER_ID, proposer)
			.insert(PersonColumn.PROPOSE, Propose.PROPOSER.toString())
			.insert(PersonColumn.PARTNER, partner);
		
		ActionInsert insert_partner = JockieBot.getDatabase().insert(Database.PERSON);
		insert_partner.getInsert()
			.insert(PersonColumn.USER_ID, partner)
			.insert(PersonColumn.PROPOSE, Propose.PROPOSEDTO.toString())
			.insert(PersonColumn.PARTNER, proposer);
		
		JockieBot.getDatabase().cluster(insert_proposer, insert_partner).execute();
	}
	
	public static void removeProposal(String proposer, String partner) {
		ActionDelete delete_proposer = JockieBot.getDatabase().delete(Database.PERSON);
		delete_proposer.getWhere().where(PersonColumn.USER_ID, Operator.EQUAL, proposer);
		
		ActionDelete delete_partner = JockieBot.getDatabase().delete(Database.PERSON);
		delete_partner.getWhere().where(PersonColumn.USER_ID, Operator.EQUAL, partner);
		
		JockieBot.getDatabase().cluster(delete_proposer, delete_partner).execute();
	}
	
	public static Result getMarriageInfo(String... userIds) {
		ActionGet[] get = new ActionGet[userIds.length];
		for(int i = 0; i < userIds.length; i++) {
			ActionGet get_user = JockieBot.getDatabase().get(Database.PERSON);
			get_user.getSelect()
				.select(PersonColumn.MARRIED)
				.select(PersonColumn.PROPOSE)
				.select(PersonColumn.PARTNER)
				.select(PersonColumn.MARRIAGE_DATE);
			get_user.getWhere().where(PersonColumn.USER_ID, Operator.EQUAL, userIds[i]);
			
			get[i] = get_user;
		}
		
		return JockieBot.getDatabase().cluster(get).execute();
	}
	
	public static Result getMarriageValue(String user_id, PersonColumn... columns) {
		ActionGet get_user = JockieBot.getDatabase().get(Database.PERSON);
		for(int i = 0; i < columns.length; i++)
			get_user.getSelect().select(columns[i]);
		get_user.getWhere().where(PersonColumn.USER_ID, Operator.EQUAL, user_id);
		return get_user.execute();
	}
}