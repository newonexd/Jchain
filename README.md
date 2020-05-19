# Jchain
a simple blockchain implement by java
java实现的简易区块链
已实现:

    * 钱包
    * Pow
    * UTXO
    * Cli

未实现:

    * 网络

## 文档地址(更新中):[文档](https://www.cnblogs.com/cbkj-xd/category/1766558.html)
### 目录结构:
    * application   
        * Cli           用于解析命令行与客户端交互
    * core          核心包
        * Block         简单区块定义
        * Blockchain    区块链定义
        * Pow           Pow共识机制
    * tools         
        * BloomFilter   布隆过滤器
        * Merkle        默克尔树
        * Storage       存储工具
    * transaction  
        * Transaction   交易类
        * TxInput       UTXO中的输入
        * TxOutput      UTXO中的输出
    * util          工具包
    * wallet        钱包
        * RSAKey        RSA算法实现的加密
        * Wallet        钱包