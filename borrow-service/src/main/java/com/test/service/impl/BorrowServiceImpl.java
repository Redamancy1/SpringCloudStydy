package com.test.service.impl;

import com.test.entity.Book;
import com.test.entity.Borrow;
import com.test.entity.User;
import com.test.entity.UserBorrowDetail;
import com.test.mapper.BorrowMapper;
import com.test.service.BookClient;
import com.test.service.BorrowService;
import com.test.service.UserClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BorrowServiceImpl implements BorrowService {

    @Resource
    BorrowMapper mapper;

    @Resource
    RestTemplate template;

    @Resource
    UserClient userClient;

    @Resource
    BookClient bookClient;

    @Override
    public UserBorrowDetail getUserBorrowDetailByUid(int uid) {
        List<Borrow> borrow = mapper.getBorrowsByUid(uid);
        //RestTemplate支持多种方式的远程调用
        //RestTemplate template = new RestTemplate();

        //这里通过调用getForObject来请求其他服务，并将结果自动进行封装
        //获取User信息
        //User user = template.getForObject("http://localhost:8101/user/"+uid, User.class);
        //注册到eureka中，不用再写ip，直接写服务名称，下面同
        //User user = template.getForObject("http://userservice/user/"+uid, User.class);

        //升级-直接注入 --OpenFeign实现负载均衡
        User user = userClient.getUserById(uid);

        //获取每一本书的详细信息
        List<Book> bookList = borrow
                .stream()
                //.map(b -> template.getForObject("http://localhost:8201/book/"+b.getBid(), Book.class))
                //.map(b -> template.getForObject("http://bookservice/book/"+b.getBid(), Book.class))
                .map(b -> bookClient.getBookById(b.getBid()))
                .collect(Collectors.toList());
        return new UserBorrowDetail(user, bookList);
    }
}
