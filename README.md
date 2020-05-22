# Jchain
A simplified blockchain implement by java

java实现的简易区块链

```
usage: Jchain [-h] [--querybalance] [--queryblock <n>] [-t] [--to <toAddress>] [--value <value>] [-w]
 -h,--help             Print help
    --querybalance     query balance
    --queryblock <n>   query block by number
 -t,--transfer         transfer coin
    --to <toAddress>   transfer to where
    --value <value>    coin value
 -w,--wallet           get wallet information
#############################################
# 查询区块内容
    --queryblock 2   #查看高度为2的区块
# 查询钱包余额
    --querybalance 
# 转账
    -t --to address --value 20 #向地址address 转账20
# 查看钱包
    -w 
```


## 文档地址(更新中):[文档](https://ifican.top/2020/05/19/blog/blockchain/Jchain1/)

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
    * App               启动类