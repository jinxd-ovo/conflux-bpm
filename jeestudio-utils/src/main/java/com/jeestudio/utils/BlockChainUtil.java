package com.jeestudio.utils;

import conflux.web3j.Account;
import conflux.web3j.Cfx;
import conflux.web3j.contract.ContractCall;
import conflux.web3j.contract.abi.DecodeUtil;
import conflux.web3j.response.Status;
import conflux.web3j.types.RawTransaction;
import java.math.BigInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.abi.datatypes.Utf8String;

/**
 * @Description: BlockChain util
 * @author houxl
 * @Date: 2020-11-26
 */
public class BlockChainUtil {

    protected static final Logger logger = LoggerFactory.getLogger(BlockChainUtil.class);

    private static Cfx cfx = null;
    private static String defaultNode = null;
    private static int retry = 2;
    private static long timeout = 3000;

    public static void initCfx(String node, long timeoutValue) {
        if (defaultNode == null || false == node.equals(defaultNode)) {
            defaultNode = node;
            timeout = timeoutValue;
            cfx = Cfx.create(node, retry, timeout);
            Status status = cfx.getStatus().sendAndGet();
            BigInteger chainId = status.getChainId();
            RawTransaction.setDefaultChainId(chainId);
        }
    }

    public static Cfx getCfx() {
        return cfx;
    }

    public static String getAddress(String privateKey) {
        Account account = Account.create(getCfx(), privateKey);
        return account.getAddress();
    }

    public static String getValue(String conAddress, String functionName, String key) throws Exception {
        ContractCall contract = new ContractCall(cfx, conAddress);
        String value = null;
        try {
            value = contract.call(functionName, new org.web3j.abi.datatypes.Utf8String(key)).sendAndGet();
            return DecodeUtil.decode(value, Utf8String.class);
        } catch (Exception e) {
            logger.info("The key " + key +" does not exist on the blockchain");
            return value;
        }
    }

    public static String setValue(String conAddress, String functionName, String privateKey, String key, String value) throws Exception {
        Account account = Account.create(cfx, privateKey);
        return account.call(new Account.Option().withGasPrice(BigInteger.ONE), conAddress, functionName, new org.web3j.abi.datatypes.Utf8String(key), new org.web3j.abi.datatypes.Utf8String(value));
    }

    public static String deploy(String privateKey, String byteCodes) throws Exception {
        Account account = Account.create(cfx, privateKey);
        String hash = account.deploy(new Account.Option().withGasPrice(BigInteger.ONE), byteCodes);
        return hash;
    }
}
