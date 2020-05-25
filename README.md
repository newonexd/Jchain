# Jchain(version 1.0)
A simplified blockchain implement by java

java实现的简易区块链

## 实现功能(Implemented function)

* ``查询区块(query block)``
* ``查询钱包余额(query wallet balance)``
* ``转账(Transfer balance)``
* ``查看钱包密钥(query secret_Key)``

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
# 查看帮助信息
    -h      查看帮助信息
# 查询区块内容
    --queryblock 2   #查看高度为2的区块
# 查询钱包余额
    --querybalance 
# 转账
    -t --to address --value 20 #向地址address 转账20
# 查看钱包
    -w 
```

## 启动方式(Set up)
```
#V1.0
git clone https://github.com/newonexd/Jchain.git
<<<<<<< HEAD
git checkout  v1.0
=======
git checkout  1.0
>>>>>>> e771e134671d59828667c48340df9270913d5376
```

``App.java -> Function main``

## 文档地址(更新中):[文档](https://ifican.top/2020/05/19/blog/blockchain/Jchain1/)
**Doc(updating):[documetion](https://ifican.top/2020/05/19/blog/blockchain/Jchain1/)**

### 目录结构:
    * App           启动类
    * Cli           用于解析命令行与客户端交互
    * Block         简单区块定义
    * Blockchain    区块链定义
    * Pow           Pow共识机制
    * Merkle        默克尔树
    * Storage       存储工具(序列化到本地)
    * Transaction   UTXO交易
    * TxInput       UTXO中的输入
    * TxOutput      UTXO中的输出
    * util          工具包
    * RSAKey        RSA算法实现的加密
    * Wallet        钱包 密钥
