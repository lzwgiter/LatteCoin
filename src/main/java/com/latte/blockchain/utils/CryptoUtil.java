package com.latte.blockchain.utils;

import com.latte.blockchain.entity.Transaction;
import com.latte.blockchain.enums.LatteChainEnum;

import java.util.List;
import java.util.Base64;
import java.util.ArrayList;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;

import cn.hutool.crypto.SmUtil;

/**
 * 生成电子签名的工具类
 *
 * @author float
 * @since 2021/1/27
 */
public class CryptoUtil {

    /**
     * Sm4对称加密函数
     *
     * @param msg 待加密消息
     * @return 加密结果
     */
    public static String applySm4Encrypt(String msg, byte[] secretKey) {
        return SmUtil.sm4(secretKey).encryptBase64(msg);
    }

    /**
     * Sm4对称解密函数
     *
     * @param input 待解密消息
     * @return 解密结果
     */
    public static String applySm4Decrypt(String input, byte[] secretKey) {
        return SmUtil.sm4(secretKey).decryptStr(input);
    }

    /**
     * Sm3哈希函数
     *
     * @param msg 待哈希消息
     * @return String 哈希值
     */
    public static String applySm3Hash(String msg) {
        return SmUtil.sm3(msg);
    }

    /**
     * SM2签名函数
     *
     * @param privateKey 私钥
     * @param msg        消息
     * @return byte[] 签名信息
     */
    public static byte[] applySm2Signature(PrivateKey privateKey, String msg) {
        return SmUtil.sm2(privateKey, null).sign(msg.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * SM2签名验证函数
     *
     * @param publicKey 公钥
     * @param msg       消息
     * @param signature 签名
     * @return boolean
     */
    public static boolean verifySm2Signature(PublicKey publicKey, String msg, byte[] signature) {
        return SmUtil.sm2(null, publicKey).verify(msg.getBytes(StandardCharsets.UTF_8), signature);
    }

    /**
     * 构造指定难度的0填充字符串
     *
     * @return String
     */
    public static String getDifficultyString() {
        return new String(new char[LatteChainEnum.DIFFICULTY]).replace('\0', '0');
    }

    /**
     * Base64加密
     *
     * @param key {@link Key}
     * @return Base64加密结果
     */
    public static String getStringFromKey(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    /**
     * 计算Merkle根值
     *
     * @param transactions 交易@{@link Transaction}
     * @return String
     */
    public static String calculateMerkleRoot(ArrayList<Transaction> transactions) {
        int count = transactions.size();

        List<String> previousTreeLayer = new ArrayList<>();
        for (Transaction transaction : transactions) {
            previousTreeLayer.add(transaction.getId());
        }
        List<String> treeLayer = previousTreeLayer;

        while (count > 1) {
            treeLayer = new ArrayList<>();
            // 每次选择两个交易的散列值重新计算
            for (int i = 1; i < previousTreeLayer.size(); i += 2) {
                treeLayer.add(applySm3Hash(previousTreeLayer.get(i - 1) + previousTreeLayer.get(i)));
            }
            count = treeLayer.size();
            previousTreeLayer = treeLayer;
        }

        return (treeLayer.size() == 1) ? treeLayer.get(0) : "";
    }
}