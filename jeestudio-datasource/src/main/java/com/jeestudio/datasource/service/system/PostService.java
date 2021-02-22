package com.jeestudio.datasource.service.system;

import com.jeestudio.common.entity.system.Post;
import com.jeestudio.datasource.mapper.base.system.PostDao;
import com.jeestudio.datasource.service.common.CrudService;
import org.springframework.stereotype.Service;

/**
 * @Description: Post Service
 * @author: David
 * @Date: 2020-01-14
 */
@Service
public class PostService extends CrudService<PostDao, Post> {
}
