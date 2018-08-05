package rsautil

import com.sun.org.apache.xml.internal.security.utils.Base64
import java.io.File
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec


/**
 * ClassName:RSAUtil
 * Description:
 */
object RSAUtil {
    val factory = KeyFactory.getInstance("RSA")

    val generator = KeyPairGenerator.getInstance("RSA")
    /**
     * 根据字符串生成公钥对象
     */
    fun createPublicKey(publicStr: String): PublicKey {
        return factory.generatePublic(X509EncodedKeySpec(Base64.decode(publicStr.toByteArray())))
    }

    /**
     * 根据字符串生成私钥对象
     */
    fun createPrivateKey(privateStr: String): PrivateKey {
        return factory.generatePrivate(PKCS8EncodedKeySpec(Base64.decode(privateStr.toByteArray())))
    }

    /**
     * 创建公钥和私钥保存到本地文件中
     */
    fun generatePublicAndPrivateKey() {
        generator.initialize(1024)
        val keyPair = generator.generateKeyPair()
        val privateArr = keyPair.private.encoded
        val publicArr = keyPair.public.encoded

        val private = Base64.encode(privateArr)
        val public = Base64.encode(publicArr)


        val privateFile = File("private.txt")
        privateFile.writeText("-----BEGIN PRIVATE KEY-----\r\n" + private + "\r\n-----END PRIVATE KEY-----")
        val publicFile = File("public.txt")
        publicFile.writeText(public)
    }

    /**
     * 生成转账地址 公钥
     */
    fun generatePublicKey() {
        val keyPair = generator.generateKeyPair()
        val publicArr = keyPair.public.encoded
        val public = Base64.encode(publicArr)

        val publicFile = File("toPublic.txt")
        publicFile.writeText(public)
    }
}
