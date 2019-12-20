# RSA-DEMO
RSA非对称加密demo
这是一个RSA的demo，这里的RSA经过了严谨的封装，很成熟，可以直接拿过来使用！
是在Util包和Pojo包中的内容，在testcontroller类中进行了加密测试，解密也很简单，通过调用封装好的方法。
加密参数是json格式的字符串，就是要把加密的内容转成json格式字符串（比如说是一个map，或者map套map，然后把map转成json）
String reqJson = JSONObject.toJSONString(allMessage);具体内容你们自己研究吧！

```
注意：当公钥私钥的key的长度为1024时，RSAUtil加解密的工具类KEY_SIZE=1024，MAX_ENCRYPT_BLOCK=117，MAX_DECRYPT_BLOCK=128，如果长度为2048时，KEY_SIZE=2048，MAX_ENCRYPT_BLOCK=117，MAX_DECRYPT_BLOCK=256.
```
