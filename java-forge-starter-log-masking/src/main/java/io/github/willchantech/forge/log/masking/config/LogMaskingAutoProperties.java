package io.github.willchantech.forge.log.masking.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Desc :
 * @Author : Will Chan
 * @Date : 2026/4/27 14:33
 */
@ConfigurationProperties(prefix = "java-forge.log-mask", ignoreInvalidFields = true)
public class LogMaskingAutoProperties  {
    private boolean enabled; //是否开启日志脱敏
    private boolean phone; //是否开启手机号脱敏
    private boolean email; //是否开启邮箱脱敏
    private boolean idCard; //是否开启身份证号脱敏
    private boolean passport; //是否开启护照号脱敏

    public boolean isPassport() {
        return passport;
    }

    public void setPassport(boolean passport) {
        this.passport = passport;
    }


    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isPhone() {
        return phone;
    }

    public void setPhone(boolean phone) {
        this.phone = phone;
    }

    public boolean isEmail() {
        return email;
    }

    public void setEmail(boolean email) {
        this.email = email;
    }

    public boolean isIdCard() {
        return idCard;
    }

    public void setIdCard(boolean idCard) {
        this.idCard = idCard;
    }

    /**
     * 将所有的状态位叠加成一个数字
     * @return
     */
    public long toFlagsNum() {

        if (!enabled) {
            return 0L;
        }

        long flags = 0L;

        if (phone) {
            flags |= MaskFlags.PHONE;
        }

        if (email) {
            flags |= MaskFlags.EMAIL;
        }

        if (idCard) {
            flags |= MaskFlags.ID_CARD;
        }

        if (passport) {
            flags |= MaskFlags.PASSPORT;
        }

        return flags;
    }
    /**
     * 是否是有效的日志配置？
     *      判断依据：总开关开启 + 至少有一个脱敏类型开关开启
     * @return
     */
    public boolean isValid() {
        return enabled && toFlagsNum() != 0L;
    }
}
