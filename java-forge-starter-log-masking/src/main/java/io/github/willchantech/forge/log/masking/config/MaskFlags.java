package io.github.willchantech.forge.log.masking.config;

/**
 * @Desc :  状态位常量
 * @Author : Will Chan
 * @Date : 2026/4/27 15:16
 */
public final class MaskFlags {
    private MaskFlags() {}

    public static final long PHONE   = 1L << 0;
    public static final long EMAIL   = 1L << 1;
    public static final long ID_CARD = 1L << 2;
    public static final long PASSPORT = 1L << 3;
}
