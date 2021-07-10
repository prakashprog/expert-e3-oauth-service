package com.expertworks.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;

import com.expertworks.model.ExpertUser;
import com.expertworks.model.UserDTO;
import com.expertworks.model.UserDetailDynamoModel;
import com.expertworks.repository.UserRepository;

@Service(value = "userService")
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	UserRepository userDetailRepositry;

	@Autowired
	private TokenStore tokenStore;

	@Autowired
	DefaultTokenServices defaultTokenServices;

	public Optional<UserDetailDynamoModel> loadUserFromDB(String username) {

		List<UserDetailDynamoModel> list = userDetailRepositry.findByUserId(username);
		UserDetailDynamoModel userDetailDynamoModel = null;
		if (list != null && list.size() > 0) {
			userDetailDynamoModel = userDetailRepositry.findByUserId(username).get(0);
		}

		System.out.println("userDetailDynamoModel : " + userDetailDynamoModel);

		if (null != userDetailDynamoModel && username.equals(userDetailDynamoModel.getUserId())) {
			return Optional.of(userDetailDynamoModel);
		} else {
			return Optional.empty();
		}
	}

	public UserDTO convertToDTO(UserDetailDynamoModel userDetailDynamoModel) {

		String name = userDetailDynamoModel.getUserId();
		String password = userDetailDynamoModel.getPassword();

		return new UserDTO(name, password, "ADMIN");
	}

	@Override
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

		/*
		 * Here we are using dummy data, you need to load user data from database or
		 * other third party application
		 */
		System.out.println("calling loadUserByUsername service :  " + userId);
		ExpertUser expertUser = null;
		UserBuilder builder = null;
		String userRole = null;

		this.removeOlderTokens(userId);  // to get new token everytime

		Optional<UserDetailDynamoModel> userDetailDynamoModel = loadUserFromDB(userId);
		UserDetailDynamoModel userDetailDynamoModel1 = userDetailDynamoModel.get();
		System.out.println("======================================");
		System.out.println("UserId = " + userDetailDynamoModel1.getUserId());
		System.out.println("Name = " + userDetailDynamoModel1.getUserName());
		System.out.println("Role = " + userDetailDynamoModel1.getUserRole());
		System.out.println("TeamId = " + userDetailDynamoModel1.getTeamId());
		System.out.println("PartnerId = " + userDetailDynamoModel1.getPartnerId());
		System.out.println("logo = " + userDetailDynamoModel1.getPartnerImg());
		System.out.println("==============DB=======================");

		userRole = userDetailDynamoModel1.getUserRole();

		if (userDetailDynamoModel1 != null) {
			List authorities = new ArrayList();
			authorities.add(new SimpleGrantedAuthority(userRole));
			expertUser = new ExpertUser(userDetailDynamoModel1.getUserId(), userDetailDynamoModel1.getPassword(), true,
					true, true, true, authorities, userDetailDynamoModel1.getUserName(), "", "", 0, 0,
					userDetailDynamoModel1.getTeamId(), userDetailDynamoModel1.getUserRole(),
					userDetailDynamoModel1.getPartnerImg(),userDetailDynamoModel1.getPartnerId());

			UserDTO userDTO = convertToDTO(userDetailDynamoModel1);

			builder = org.springframework.security.core.userdetails.User.withUsername(userDTO.getUsername());
			// builder.password(new BCryptPasswordEncoder().encode(user.getPassword()));
			builder.password(userDTO.getPassword());
			builder.roles(userDTO.getRoles());  

		} else {
			throw new UsernameNotFoundException("User not found.");
		}

		return expertUser;
	}

	public void removeOlderTokens(String userName) {

		Collection<OAuth2AccessToken> tokens = tokenStore.findTokensByClientIdAndUserName("client", userName);
		for (OAuth2AccessToken token : tokens) {
			defaultTokenServices.revokeToken(token.getValue());
		}

	}

}
