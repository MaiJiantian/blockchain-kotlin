package rsautil

import com.sun.org.apache.xml.internal.security.utils.Base64
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature


/**
 * ClassName:SignatureUtils
 * Description:
 */
object SignatureUtils {
    /**
     * 生成签名
     *
     * @param input      : 原文
     * @param algorithm  : 算法
     * @param privateKey : 私钥
     * @return : 签名
     * @throws Exception
     */
    @Throws(Exception::class)
    fun getSignature(input: String, algorithm: String, privateKey: PrivateKey): String? {
        // 获取签名对象
        val signature = Signature.getInstance(algorithm)
        // 初始化签名
        signature.initSign(privateKey)
        // 传入原文
        signature.update(input.toByteArray())
        // 开始签名
        val sign = signature.sign()
        // 对签名数据进行Base64编码
        return Base64.encode(sign)
    }

    /**
     * 校验签名
     *
     * @param input          : 原文
     * @param algorithm      : 算法
     * @param publicKey      : 公钥
     * @param signaturedData : 签名
     * @return : 数据是否被篡改
     * @throws Exception
     */
    @Throws(Exception::class)
    fun verifySignature(input: String, publicKey: PublicKey, signaturedData: String): Boolean {
        // 获取签名对象
        val signature = Signature.getInstance("SHA256withRSA")
        // 初始化签名
        signature.initVerify(publicKey)
        // 传入原文
        signature.update(input.toByteArray())
        // 校验数据
        return signature.verify(toBytes(signaturedData))

    }
    fun toBytes(str: String?): ByteArray {
        if (str == null || str.trim { it <= ' ' } == "") {
            return ByteArray(0)
        }

        val bytes = ByteArray(str.length / 2)
        for (i in 0 until str.length / 2) {
            val subStr = str.substring(i * 2, i * 2 + 2)
            bytes[i] = Integer.parseInt(subStr, 16).toByte()
        }
        return bytes
    }
}