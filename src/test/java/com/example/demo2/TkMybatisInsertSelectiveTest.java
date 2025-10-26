package com.example.demo2;

import com.example.demo2.entity.TestUser;
import com.example.demo2.mapper.TestUserMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TkMybatisInsertSelectiveTest {

    @Autowired
    private TestUserMapper testUserMapper;

    @BeforeEach
    public void setUp() {
        testUserMapper.delete(new TestUser());
    }

    @AfterEach
    public void tearDown() {
        testUserMapper.delete(new TestUser());
    }

    @Test
    public void test01_insertSelective_withAllRequiredFields() {
        System.out.println("\n========== 测试1: 仅设置必填字段，验证数据库默认值是否生效 ==========");
        
        TestUser user = new TestUser();
        user.setUsername("testuser1");
        user.setPhone("13800138001");
        
        int result = testUserMapper.insertSelective(user);
        assertEquals(1, result, "插入应该成功");
        assertNotNull(user.getId(), "ID应该自动生成");
        
        TestUser queryUser = testUserMapper.selectByPrimaryKey(user.getId());
        assertNotNull(queryUser);
        assertEquals("testuser1", queryUser.getUsername());
        assertEquals("13800138001", queryUser.getPhone());
        assertEquals("Guest", queryUser.getNickname(), "nickname 应该使用数据库默认值 'Guest'");
        assertEquals(0, queryUser.getAge(), "age 应该使用数据库默认值 0");
        assertEquals(1, queryUser.getStatus(), "status 应该使用数据库默认值 1");
        assertEquals("Unknown", queryUser.getAddress(), "address 应该使用数据库默认值 'Unknown'");
        assertNull(queryUser.getEmail(), "email 未设置应该为 NULL");
        assertNull(queryUser.getRemark(), "remark 未设置应该为 NULL");
        assertNotNull(queryUser.getCreateTime(), "create_time 应该自动设置当前时间");
        
        System.out.println("插入的用户: " + queryUser);
    }

    @Test
    public void test02_insertSelective_withNullableFields() {
        System.out.println("\n========== 测试2: 显式设置可空字段为非 null 值 ==========");
        
        TestUser user = new TestUser();
        user.setUsername("testuser2");
        user.setPhone("13800138002");
        user.setNickname("CustomNick");
        user.setAge(25);
        user.setEmail("test@example.com");
        user.setAddress("Custom Address");
        
        int result = testUserMapper.insertSelective(user);
        assertEquals(1, result);
        
        TestUser queryUser = testUserMapper.selectByPrimaryKey(user.getId());
        assertNotNull(queryUser);
        assertEquals("CustomNick", queryUser.getNickname(), "应该使用设置的值而不是数据库默认值");
        assertEquals(25, queryUser.getAge());
        assertEquals("test@example.com", queryUser.getEmail());
        assertEquals("Custom Address", queryUser.getAddress());
        
        System.out.println("插入的用户: " + queryUser);
    }

    @Test
    public void test03_insertSelective_withNotNullFieldsHavingDefault() {
        System.out.println("\n========== 测试3: 非空字段带默认值 (status) 不设置时是否使用默认值 ==========");
        
        TestUser user = new TestUser();
        user.setUsername("testuser3");
        user.setPhone("13800138003");
        
        int result = testUserMapper.insertSelective(user);
        assertEquals(1, result);
        
        TestUser queryUser = testUserMapper.selectByPrimaryKey(user.getId());
        assertNotNull(queryUser);
        assertEquals(1, queryUser.getStatus(), "status 未设置应该使用数据库默认值 1");
        
        System.out.println("插入的用户: " + queryUser);
    }

    @Test
    public void test04_insertSelective_overrideDefaultValue() {
        System.out.println("\n========== 测试4: 显式设置为 0 或空字符串，验证是否会覆盖默认值 ==========");
        
        TestUser user = new TestUser();
        user.setUsername("testuser4");
        user.setPhone("13800138004");
        user.setStatus(0);
        user.setNickname("");
        user.setAge(0);
        
        int result = testUserMapper.insertSelective(user);
        assertEquals(1, result);
        
        TestUser queryUser = testUserMapper.selectByPrimaryKey(user.getId());
        assertNotNull(queryUser);
        assertEquals(0, queryUser.getStatus(), "显式设置为 0 应该生效");
        assertEquals("", queryUser.getNickname(), "显式设置为空字符串应该生效，而不是使用默认值");
        assertEquals(0, queryUser.getAge());
        
        System.out.println("插入的用户: " + queryUser);
    }

    @Test
    public void test05_insertSelective_missingRequiredFieldWithoutDefault() {
        System.out.println("\n========== 测试5: 缺少必填字段（无默认值）应该抛异常 ==========");
        
        TestUser user = new TestUser();
        user.setUsername("testuser5");
        
        Exception exception = assertThrows(Exception.class, () -> {
            testUserMapper.insertSelective(user);
        });
        
        System.out.println("预期的异常: " + exception.getClass().getName());
        System.out.println("异常信息: " + exception.getMessage());
        assertTrue(exception.getMessage().contains("phone") || 
                   exception.getMessage().contains("cannot be null") ||
                   exception.getMessage().contains("Column") ||
                   exception.getMessage().contains("doesn't have a default value"),
                "异常信息应该包含字段名或相关错误描述");
    }

    @Test
    public void test06_insertSelective_nullFieldsAreIgnored() {
        System.out.println("\n========== 测试6: 验证 insertSelective 确实忽略了 null 字段 ==========");
        
        TestUser user = new TestUser();
        user.setUsername("testuser6");
        user.setPhone("13800138006");
        user.setEmail(null);
        user.setRemark(null);
        user.setNickname(null);
        
        int result = testUserMapper.insertSelective(user);
        assertEquals(1, result);
        
        TestUser queryUser = testUserMapper.selectByPrimaryKey(user.getId());
        assertNotNull(queryUser);
        assertNull(queryUser.getEmail(), "email 为 null 应该插入 NULL");
        assertNull(queryUser.getRemark(), "remark 为 null 应该插入 NULL");
        assertEquals("Guest", queryUser.getNickname(), "nickname 为 null 时应该使用数据库默认值");
        
        System.out.println("插入的用户: " + queryUser);
    }

    @Test
    public void test07_insert_vs_insertSelective() {
        System.out.println("\n========== 测试7: 对比 insert 和 insertSelective 的区别 ==========");
        
        TestUser user1 = new TestUser();
        user1.setUsername("insert_user");
        user1.setPhone("13800138007");
        
        try {
            testUserMapper.insert(user1);
            System.out.println("insert() 方法执行成功 - 这可能会因为 null 字段而失败");
        } catch (Exception e) {
            System.out.println("insert() 方法失败: " + e.getMessage());
            System.out.println("原因: insert() 会尝试插入所有字段，包括 null 值");
        }
        
        TestUser user2 = new TestUser();
        user2.setUsername("insertSelective_user");
        user2.setPhone("13800138008");
        
        int result = testUserMapper.insertSelective(user2);
        assertEquals(1, result);
        System.out.println("insertSelective() 方法执行成功");
        System.out.println("原因: insertSelective() 只插入非 null 字段，让数据库默认值生效");
        
        TestUser queryUser = testUserMapper.selectByPrimaryKey(user2.getId());
        System.out.println("插入的用户: " + queryUser);
    }
}

