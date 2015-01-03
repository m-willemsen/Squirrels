package com.skype.sample;

import com.skype.ContactList;
import com.skype.Friend;
import com.skype.Skype;
import com.skype.SkypeException;

public class ShowContactList {
	public static void main (String[] args){
		try {
			Friend[] list = Skype.getContactList().getAllFriends();
			for (Friend friend: list){
				System.out.println("Friend: "+friend.getFullName()+" - "+friend.getId());
			}
		} catch (SkypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
