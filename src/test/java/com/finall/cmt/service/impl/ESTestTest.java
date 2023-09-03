package com.finall.cmt.service.impl;

import com.finall.cmt.CmtApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CmtApplication.class)
public class ESTestTest {

    @Autowired
    private ESTest esTest;

    @Test
    public void testSA(){
        esTest.testInsert();
    }

    @Test
    public void testSelectAll(){
        esTest.testSelectAll();
    }
}