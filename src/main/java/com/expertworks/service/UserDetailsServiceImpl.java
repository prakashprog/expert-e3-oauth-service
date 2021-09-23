package com.expertworks.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.expertworks.config.AuthServerConfig;
import com.expertworks.model.ExpertUser;
import com.expertworks.model.UserDTO;
import com.expertworks.model.UserDetailDynamoModel;
import com.expertworks.repository.UserRepository;

@Service(value = "userService")
public class UserDetailsServiceImpl implements UserDetailsService {
	
    private final static Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

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

		logger.info("userDetailDynamoModel : " + userDetailDynamoModel);

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
		logger.info("calling loadUserByUsername service :  " + userId);
		ExpertUser expertUser = null;
		UserBuilder builder = null;
		String userRole = null;

		this.removeOlderTokens(userId);  // to get new token everytime

		Optional<UserDetailDynamoModel> userDetailDynamoModel = loadUserFromDB(userId);
		UserDetailDynamoModel userDetailDynamoModelDB = userDetailDynamoModel.get();
		logger.info("======================================");
		logger.info("UserId = " + userDetailDynamoModelDB.getUserId());
		logger.info("Name = " + userDetailDynamoModelDB.getUserName());
		logger.info("Role = " + userDetailDynamoModelDB.getUserRole());
		logger.info("GroupId = " + userDetailDynamoModelDB.getGroupId());
		logger.info("TeamId = " + userDetailDynamoModelDB.getTeamId());
		logger.info("PartnerId = " + userDetailDynamoModelDB.getPartnerId());
		logger.info("logo = " + userDetailDynamoModelDB.getPartnerImg());
		logger.info("enabled = " + userDetailDynamoModelDB.isEnabled());
		logger.info("==============DB=======================");

		userRole = userDetailDynamoModelDB.getUserRole();
		
		//if (userDetailDynamoModelDB != null && userDetailDynamoModelDB.isEnabled()) {

		if (userDetailDynamoModelDB != null ) {
			List authorities = new ArrayList();
			authorities.add(new SimpleGrantedAuthority(userRole));
			expertUser = new ExpertUser(userDetailDynamoModelDB.getUserId(), userDetailDynamoModelDB.getPassword(), true,
					true, true, true, authorities, userDetailDynamoModelDB.getUserName(), "", "", 0, 0,
					userDetailDynamoModelDB.getTeamId(), userDetailDynamoModelDB.getUserRole(),
					userDetailDynamoModelDB.getPartnerImg(),userDetailDynamoModelDB.getPartnerId(),userDetailDynamoModelDB.getGroupId());

			UserDTO userDTO = convertToDTO(userDetailDynamoModelDB);

			builder = org.springframework.security.core.userdetails.User.withUsername(userDTO.getUsername());
			// builder.password(new BCryptPasswordEncoder().encode(user.getPassword()));
			builder.password(userDTO.getPassword());
			builder.roles(userDTO.getRoles());  

		} else {
			System.out.println("Exception occured");
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
