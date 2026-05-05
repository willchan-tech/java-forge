package io.github.willchantech.forge.log.mask;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Desc :  日志脱敏测试
 * @Author : Will Chan
 * @Date : 2026/4/27 19:02
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest()
public class LogMaskTest {
    // ==============================
// 中国大陆手机号（5条）
// ==============================
    private String phone1 = "13812345678";
    private String phone2 = "13987654321";
    private String phone3 = "15011223344";
    private String phone4 = "17655667788";
    private String phone5 = "19999887766";


    // ==============================
// 邮箱 Email（5条）
// ==============================
    private String email1 = "zhangsan88@qq.com";
    private String email2 = "li.si2024@gmail.com";
    private String email3 = "wangwu_dev@outlook.com";
    private String email4 = "user_test123@163.com";
    private String email5 = "forge.admin@yahoo.com";


    // ==============================
// 中国身份证号 ID Card（18位）
// 仅模拟格式，不保证真实校验码
// ==============================
    private String idCard1 = "440103199001011234";
    private String idCard2 = "110101198805203456";
    private String idCard3 = "320311199512123210";
    private String idCard4 = "510107197703154567";
    private String idCard5 = "330106200112089876";


    // ==============================
// 中国护照 Passport（模拟常见格式）
// E/G + 8位数字
// ==============================
    private String passport1 = "E12345678";
    private String passport2 = "G87654321";
    private String passport3 = "E23456789";
    private String passport4 = "G11223344";
    private String passport5 = "E99887766";


    // ==============================
// 混合日志测试文本（推荐你测试脱敏用）
// ==============================
    private String log1 = "用户手机号: 13812345678";
    private String log2 = "注册邮箱: zhangsan88@qq.com";
    private String log3 = "身份证号码: 440103199001011234";
    private String log4 = "护照号码: E12345678";
    private String log5 = "用户信息 => phone=19999887766,email=forge.admin@yahoo.com,idCard=330106200112089876";
    
    @Test
    public void print() {
        log.info(log1 + " " + log2 + " " + log3 + " " + log4 + " " + log5);
    }
}
