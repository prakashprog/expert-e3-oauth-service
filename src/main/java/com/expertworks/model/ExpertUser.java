package com.expertworks.model;

import java.util.Collection;

import org.springframework.security.core.userdetails.User;

public class ExpertUser extends User {

	private static final long serialVersionUID = -3531439484732724601L;

	private final String firstName;
	private final String middleName;
	private final String lastName;
	private final String teamId;
	private final String role;
	private final String logo;
	private String groupId;
	private String partnerId;
	private final long phNumber;
	private final long altPhNumber;
	
	

	public ExpertUser(String username, String password, boolean enabled, boolean accountNonExpired,
			boolean credentialsNonExpired, boolean accountNonLocked, Collection authorities, String firstName,
			String middleName, String lastName, long phNumber, long altPhNumber, String teamId,String role, String logo,
			String partnerId,String groupId) {

		super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);

		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.phNumber = phNumber;
		this.altPhNumber = altPhNumber;
		this.teamId = teamId;
		this.role = role;
		this.logo = logo;
		this.partnerId=partnerId;
		this.groupId=groupId;
		
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public String getLastName() {
		return lastName;
	}


	public long getPhNumber() {
		return phNumber;
	}

	public String getTeamId() {
		return teamId;
	}

	public long getAltPhNumber() {
		return altPhNumber;
	}

	public String getRole() {
		return role;
	}

	public String getLogo() {
		return logo;
	}

	public String getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

}
