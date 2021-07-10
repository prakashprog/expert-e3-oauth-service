package com.expertworks.repository;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.expertworks.model.UserDetailDynamoModel;

@Repository
public class UserRepository {

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

   
    
    public List<UserDetailDynamoModel> findByUserId(String userId) {
    	
    	UserDetailDynamoModel userDetailDynamoModel = new UserDetailDynamoModel();
    	userDetailDynamoModel.setUserId(userId);
    	
    	DynamoDBQueryExpression<UserDetailDynamoModel> dynamoDBQueryExpression = new DynamoDBQueryExpression().withHashKeyValues(userDetailDynamoModel);
    	PaginatedQueryList<UserDetailDynamoModel> paginatedQueryList  =  dynamoDBMapper.query(UserDetailDynamoModel.class, dynamoDBQueryExpression);
    	paginatedQueryList.loadAllResults();

		List<UserDetailDynamoModel> list = new ArrayList<UserDetailDynamoModel>(paginatedQueryList.size());

		Iterator<UserDetailDynamoModel> iterator = paginatedQueryList.iterator();
		while (iterator.hasNext()) {
			UserDetailDynamoModel element = iterator.next();
			list.add(element);
		}

		return list;

    }
    

} 