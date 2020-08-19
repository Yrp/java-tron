package org.tron.common.runtime.vm;

import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import org.junit.*;
import org.spongycastle.util.encoders.Hex;
import org.testng.Assert;
import org.tron.common.crypto.ECKey;
import org.tron.common.parameter.CommonParameter;
import org.tron.common.runtime.TVMTestResult;
import org.tron.common.runtime.TvmTestUtils;
import org.tron.common.utils.*;
import org.tron.consensus.base.Param;
import org.tron.core.capsule.BlockCapsule;
import org.tron.core.capsule.WitnessCapsule;
import org.tron.core.exception.*;
import org.tron.core.vm.config.ConfigLoader;
import org.tron.core.vm.config.VMConfig;
import org.tron.protos.Protocol;
import org.tron.protos.Protocol.Transaction;
import stest.tron.wallet.common.client.utils.AbiUtil;

import java.math.BigInteger;
import java.util.Arrays;

@Slf4j
public class WithdrawRewardTest extends VMContractTestBase {

/*  pragma solidity ^0.5.0;

  contract ContractB{
    address user;

    constructor() payable public {
      user = msg.sender;
    }

    function stakeTest(address sr, uint256 amount) public returns (bool) {
      return stake(sr, amount);
    }

    function withdrawRewardTest() public returns (uint) {
      return withdrawreward();
    }
  }

  contract TestRewardBalance{
    address user;

    ContractB contractB = new ContractB();

    constructor() payable public {
      user = msg.sender;
    }

    function stakeTest(address sr, uint256 amount) public returns (bool) {
      return stake(sr, amount);
    }

    function unstakeTest() public {
      unstake();
    }

    function contractBStakeTest(address sr, uint256 amount) public returns (bool) {
      return contractB.stakeTest(sr, amount);
    }

    function withdrawRewardTest() public returns (uint) {
      return withdrawreward();
    }

    function rewardBalanceTest(address addr) public returns (uint) {
      return addr.rewardbalance;
    }

    function localContractAddrTest() view public returns (uint256) {
      address payable localContract = address(uint160(address(this)));
      return localContract.rewardbalance;
    }

    function otherContractAddrTest() view public returns (uint256) {
      address payable localContract = address(uint160(address(contractB)));
      return localContract.rewardbalance;
    }

    function contractBWithdrawRewardTest() public returns (uint) {
      return contractB.withdrawRewardTest();
    }

    function getContractBAddressTest() public returns (address) {
      return address(contractB);
    }
  }*/

  @Test
  public void testWithdrawRewardInLocalContract()
          throws ContractExeException, ReceiptCheckErrException, VMIllegalException,
          ContractValidateException, DupTransactionException, TooBigTransactionException, AccountResourceInsufficientException, BadBlockException, NonCommonBlockException, TransactionExpirationException, UnLinkedBlockException, ZksnarkException, TaposException, TooBigTransactionResultException, ValidateSignatureException, BadNumberBlockException, ValidateScheduleException {
    ConfigLoader.disable = true;
    VMConfig.initAllowTvmTransferTrc10(1);
    VMConfig.initAllowTvmConstantinople(1);
    VMConfig.initAllowTvmSolidity059(1);
    VMConfig.initAllowTvmStake(1);
    manager.getDynamicPropertiesStore().saveChangeDelegation(1);

    String contractName = "TestWithdrawReward";
    byte[] address = Hex.decode(OWNER_ADDRESS);
    String ABI = "[{\"inputs\":[],\"payable\":true,\"stateMutability\":\"payable\",\"type\":\"constructor\"}" +
            ",{\"constant\":false,\"inputs\":[{\"internalType\":\"address\",\"name\":\"sr\",\"type\":\"address" +
            "\"},{\"internalType\":\"uint256\",\"name\":\"amount\",\"type\":\"uint256\"}],\"name\":\"" +
            "contractBStakeTest\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}]," +
            "\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false," +
            "\"inputs\":[],\"name\":\"contractBWithdrawRewardTest\",\"outputs\":[{\"internalType\":\"uint256\"," +
            "\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":" +
            "\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"localContractAddrTest\",\"outputs\":" +
            "[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\"" +
            ":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"otherContractAddrTest\"" +
            ",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"" +
            "stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"internalType\":" +
            "\"address\",\"name\":\"addr\",\"type\":\"address\"}],\"name\":\"rewardBalanceTest\",\"outputs\":[{\"" +
            "internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\"" +
            ":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"internalType\":\"address\"," +
            "\"name\":\"sr\",\"type\":\"address\"},{\"internalType\":\"uint256\",\"name\":\"amount\",\"type\":" +
            "\"uint256\"}],\"name\":\"stakeTest\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\"" +
            ":\"bool\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\"" +
            ":false,\"inputs\":[],\"name\":\"unstakeTest\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"" +
            "nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[],\"name\":\"withdrawRewardTest\"," +
            "\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"" +
            "stateMutability\":\"nonpayable\",\"type\":\"function\"}]";
    String factoryCode = "60806040526040516100109061005c565b604051809103906000f08015801561002c573d600080" +
            "3e3d6000fd5b50600180546001600160a01b03929092166001600160a01b0319928316179055600080549091163" +
            "3179055610069565b6101258061035e83390190565b6102e6806100786000396000f3fe60806040523480156100" +
            "1057600080fd5b50d3801561001d57600080fd5b50d2801561002a57600080fd5b50600436106100a2576000356" +
            "0e01c8063c290120a11610075578063c290120a14610131578063cb2d51cf14610139578063d30a28ee14610141" +
            "578063e49de2d014610149576100a2565b806325a26c30146100a75780638db848f1146100e7578063a223c65f1" +
            "4610101578063b3e835e114610127575b600080fd5b6100d3600480360360408110156100bd57600080fd5b5060" +
            "01600160a01b038135169060200135610175565b604080519115158252519081900360200190f35b6100ef61020" +
            "1565b60408051918252519081900360200190f35b6100ef6004803603602081101561011757600080fd5b503560" +
            "01600160a01b0316610278565b61012f610285565b005b6100ef610289565b6100ef610291565b6100ef6102965" +
            "65b6100d36004803603604081101561015f57600080fd5b506001600160a01b0381351690602001356102a6565b" +
            "60015460408051630e49de2d60e41b81526001600160a01b0385811660048301526024820185905291516000939" +
            "29092169163e49de2d09160448082019260209290919082900301818787803b1580156101ce57600080fd5b505a" +
            "f11580156101e2573d6000803e3d6000fd5b505050506040513d60208110156101f857600080fd5b50519392505" +
            "050565b60015460408051636148090560e11b815290516000926001600160a01b03169163c290120a9160048083" +
            "0192602092919082900301818787803b15801561024757600080fd5b505af115801561025b573d6000803e3d600" +
            "0fd5b505050506040513d602081101561027157600080fd5b5051905090565b6001600160a01b0316d890565bd6" +
            "50565b6000d7905090565b30d890565b6001546001600160a01b0316d890565b60008183d5939250505056fea26" +
            "474726f6e5820b122fe49503fd85399547fe5895d4a9a7f4a4abc9d439d86890b31417534437464736f6c634300" +
            "050d0031608060405234801561001057600080fd5b50d3801561001d57600080fd5b50d2801561002a57600080f" +
            "d5b5060ec806100396000396000f3fe6080604052348015600f57600080fd5b50d38015601b57600080fd5b50d2" +
            "8015602757600080fd5b5060043610604a5760003560e01c8063c290120a14604f578063e49de2d0146067575b6" +
            "00080fd5b605560a4565b60408051918252519081900360200190f35b609060048036036040811015607b576000" +
            "80fd5b506001600160a01b03813516906020013560ac565b604080519115158252519081900360200190f35b600" +
            "0d7905090565b60008183d5939250505056fea26474726f6e582072b0b3cf06e26167acb5abfb54a4059620fb9c" +
            "f6c3d2f5006c4376049df4c53164736f6c634300050d0031";
    long value = 1000000000;
    long fee = 100000000;
    long consumeUserResourcePercent = 0;

    // deploy contract
    Transaction trx = TvmTestUtils.generateDeploySmartContractAndGetTransaction(
            contractName, address, ABI, factoryCode, value, fee, consumeUserResourcePercent,
            null);
    byte[] factoryAddress = WalletUtil.generateContractAddress(trx);
    String factoryAddressStr = StringUtil.encode58Check(factoryAddress);
    runtime = TvmTestUtils.processTransactionAndReturnRuntime(trx, manager, null);
    Assert.assertNull(runtime.getRuntimeError());

    trx = TvmTestUtils.generateDeploySmartContractAndGetTransaction(
            "", address, ABI, factoryCode, value, fee, consumeUserResourcePercent,
            null);
    byte[] factoryAddressOther = WalletUtil.generateContractAddress(trx);
    String factoryAddressStrOther = StringUtil.encode58Check(factoryAddressOther);
    runtime = TvmTestUtils.processTransactionAndReturnRuntime(trx, manager, null);
    Assert.assertNull(runtime.getRuntimeError());

    // Trigger contract method: stakeTest(address,uint256)
    String methodByAddr = "stakeTest(address,uint256)";
    String witness = "27Ssb1WE8FArwJVRRb8Dwy3ssVGuLY8L3S1";
    String hexInput = AbiUtil.parseMethod(methodByAddr, Arrays.asList(witness, 100000000));
    TVMTestResult result = TvmTestUtils
            .triggerContractAndReturnTvmTestResult(Hex.decode(OWNER_ADDRESS),
                    factoryAddress, Hex.decode(hexInput), 0, fee, manager, null);
    Assert.assertNull(result.getRuntime().getRuntimeError());

    byte[] returnValue = result.getRuntime().getResult().getHReturn();
    Assert.assertEquals(Hex.toHexString(returnValue),
            "0000000000000000000000000000000000000000000000000000000000000001");

    // Do Maintenance & Generate New Block
    maintenanceManager.doMaintenance();
    String key = "f31db24bfbd1a2ef19beddca0a0fa37632eded9ac666a05d3bd925f01dde1f62";
    byte[] privateKey = ByteArray.fromHexString(key);
    final ECKey ecKey = ECKey.fromPrivate(privateKey);
    byte[] witnessAddress = ecKey.getAddress();
    WitnessCapsule witnessCapsule = new WitnessCapsule(ByteString.copyFrom(witnessAddress));
    chainBaseManager.addWitness(ByteString.copyFrom(witnessAddress));
    Protocol.Block block = getSignedBlock(witnessCapsule.getAddress(), 1533529947843L, privateKey);
    manager.pushBlock(new BlockCapsule(block));//cycle: 1 addReward

    // Trigger contract method: rewardBalanceTest(address)
    methodByAddr = "rewardBalanceTest(address)";
    hexInput = AbiUtil.parseMethod(methodByAddr, Arrays.asList(factoryAddressStr));
    result = TvmTestUtils
            .triggerContractAndReturnTvmTestResult(Hex.decode(OWNER_ADDRESS),
                    factoryAddress, Hex.decode(hexInput), 0, fee, manager, null);
    Assert.assertNull(result.getRuntime().getRuntimeError());

    returnValue = result.getRuntime().getResult().getHReturn();

    Assert.assertEquals(Hex.toHexString(returnValue),
            "0000000000000000000000000000000000000000000000000000000000000000");

    // Trigger contract method: localContractAddrTest()
    methodByAddr = "localContractAddrTest()";
    hexInput = AbiUtil.parseMethod(methodByAddr, Arrays.asList(""));
    result = TvmTestUtils
            .triggerContractAndReturnTvmTestResult(Hex.decode(OWNER_ADDRESS),
                    factoryAddress, Hex.decode(hexInput), 0, fee, manager, null);
    Assert.assertNull(result.getRuntime().getRuntimeError());

    returnValue = result.getRuntime().getResult().getHReturn();

    Assert.assertEquals(Hex.toHexString(returnValue),
            "0000000000000000000000000000000000000000000000000000000000000000");

    Protocol.Block newBlock = getBlock(witnessCapsule.getAddress(), System.currentTimeMillis(), privateKey);
    BlockCapsule blockCapsule = new BlockCapsule(newBlock);
    blockCapsule.generatedByMyself = true;

    // Trigger contract method: withdrawRewardTest()
    methodByAddr = "withdrawRewardTest()";
    hexInput = AbiUtil.parseMethod(methodByAddr, Arrays.asList(""));
    result = TvmTestUtils
            .triggerContractAndReturnTvmTestResult(Hex.decode(OWNER_ADDRESS),
                    factoryAddress, Hex.decode(hexInput), 0, fee, manager, blockCapsule);
    Assert.assertNull(result.getRuntime().getRuntimeError());

    returnValue = result.getRuntime().getResult().getHReturn();
    Assert.assertEquals(Hex.toHexString(returnValue),
            "0000000000000000000000000000000000000000000000000000000000000000");

    // Execute Next Cycle
    maintenanceManager.doMaintenance();
    WitnessCapsule localWitnessCapsule = manager.getWitnessStore()
            .get(StringUtil.hexString2ByteString(WITNESS_SR1_ADDRESS).toByteArray());
    Assert.assertEquals(205, localWitnessCapsule.getVoteCount());

    // Trigger contract method: rewardBalanceTest(address)
    methodByAddr = "rewardBalanceTest(address)";
    hexInput = AbiUtil.parseMethod(methodByAddr, Arrays.asList(factoryAddressStr));
    result = TvmTestUtils
            .triggerContractAndReturnTvmTestResult(Hex.decode(OWNER_ADDRESS),
                    factoryAddress, Hex.decode(hexInput), 0, fee, manager, null);
    Assert.assertNull(result.getRuntime().getRuntimeError());

    returnValue = result.getRuntime().getResult().getHReturn();
    BigInteger reward = new BigInteger(Hex.toHexString(returnValue), 16);

    // Current Reward: Total Reward * Vote Rate
//    BigInteger reward = new BigInteger(Hex.toHexString(returnValue), 16);
//    byte[] sr1 = decodeFromBase58Check(witness);
//    long totalReward = (long) ((double) rootRepository.getDelegationStore().getReward(1, sr1));
//    long totalVote = rootRepository.getDelegationStore().getWitnessVote(1, sr1);
//    double voteRate = (double) 100 / totalVote;
//    long curReward = (long) (totalReward * voteRate);
//    Assert.assertEquals(reward.longValue(), curReward);

    //total reward: block reward + vote reward
    long blockReward = 25600000;
    long voteReward = 2186667;
    long totalReward = blockReward + voteReward;
    double voteRate = (double) 100 / 205;
    long curReward = (long) (totalReward * voteRate);
    Assert.assertEquals(reward.longValue(), curReward);

    // Trigger contract method: localContractAddrTest()
    methodByAddr = "localContractAddrTest()";
    hexInput = AbiUtil.parseMethod(methodByAddr, Arrays.asList(""));
    result = TvmTestUtils
            .triggerContractAndReturnTvmTestResult(Hex.decode(OWNER_ADDRESS),
                    factoryAddress, Hex.decode(hexInput), 0, fee, manager, null);
    Assert.assertNull(result.getRuntime().getRuntimeError());

    returnValue = result.getRuntime().getResult().getHReturn();

    Assert.assertEquals((new BigInteger(Hex.toHexString(returnValue), 16)).longValue(), curReward);

    // Trigger contract method: withdrawRewardTest()
    methodByAddr = "withdrawRewardTest()";

    hexInput = AbiUtil.parseMethod(methodByAddr, Arrays.asList(""));
    result = TvmTestUtils
            .triggerContractAndReturnTvmTestResult(Hex.decode(OWNER_ADDRESS),
                    factoryAddress, Hex.decode(hexInput), 0, fee, manager, blockCapsule);
    Assert.assertNull(result.getRuntime().getRuntimeError());

    returnValue = result.getRuntime().getResult().getHReturn();

    Assert.assertEquals((new BigInteger(Hex.toHexString(returnValue), 16)).longValue(), curReward);

    // Trigger contract method: rewardBalanceTest(address)
    methodByAddr = "rewardBalanceTest(address)";
    hexInput = AbiUtil.parseMethod(methodByAddr, Arrays.asList(factoryAddressStr));
    result = TvmTestUtils
            .triggerContractAndReturnTvmTestResult(Hex.decode(OWNER_ADDRESS),
                    factoryAddress, Hex.decode(hexInput), 0, fee, manager, null);
    Assert.assertNull(result.getRuntime().getRuntimeError());

    returnValue = result.getRuntime().getResult().getHReturn();

    Assert.assertEquals(Hex.toHexString(returnValue),
            "0000000000000000000000000000000000000000000000000000000000000000");

    // Trigger contract method: localContractAddrTest()
    methodByAddr = "localContractAddrTest()";
    hexInput = AbiUtil.parseMethod(methodByAddr, Arrays.asList(""));
    result = TvmTestUtils
            .triggerContractAndReturnTvmTestResult(Hex.decode(OWNER_ADDRESS),
                    factoryAddress, Hex.decode(hexInput), 0, fee, manager, null);
    Assert.assertNull(result.getRuntime().getRuntimeError());

    returnValue = result.getRuntime().getResult().getHReturn();

    Assert.assertEquals(Hex.toHexString(returnValue),
            "0000000000000000000000000000000000000000000000000000000000000000");

    // Trigger contract method: withdrawRewardTest()
    methodByAddr = "withdrawRewardTest()";
    hexInput = AbiUtil.parseMethod(methodByAddr, Arrays.asList(""));
    result = TvmTestUtils
            .triggerContractAndReturnTvmTestResult(Hex.decode(OWNER_ADDRESS),
                    factoryAddress, Hex.decode(hexInput), 0, fee, manager, blockCapsule);
    Assert.assertNull(result.getRuntime().getRuntimeError());

    returnValue = result.getRuntime().getResult().getHReturn();
    Assert.assertEquals(Hex.toHexString(returnValue),
            "0000000000000000000000000000000000000000000000000000000000000000");

    ConfigLoader.disable = false;
  }

  @Test
  public void testWithdrawRewardInAnotherContract()
          throws ContractExeException, ReceiptCheckErrException, VMIllegalException,
          ContractValidateException, DupTransactionException, TooBigTransactionException, AccountResourceInsufficientException, BadBlockException, NonCommonBlockException, TransactionExpirationException, UnLinkedBlockException, ZksnarkException, TaposException, TooBigTransactionResultException, ValidateSignatureException, BadNumberBlockException, ValidateScheduleException {
    ConfigLoader.disable = true;
    VMConfig.initAllowTvmTransferTrc10(1);
    VMConfig.initAllowTvmConstantinople(1);
    VMConfig.initAllowTvmSolidity059(1);
    VMConfig.initAllowTvmStake(1);
    manager.getDynamicPropertiesStore().saveChangeDelegation(1);

    String contractName = "TestWithdrawRewardWithContract";
    byte[] address = Hex.decode(OWNER_ADDRESS);
    String ABI = "[{\"inputs\":[],\"payable\":true,\"stateMutability\":\"payable\",\"type\":" +
            "\"constructor\"},{\"constant\":false,\"inputs\":[{\"internalType\":\"address\"," +
            "\"name\":\"sr\",\"type\":\"address\"},{\"internalType\":\"uint256\",\"name\":\"" +
            "amount\",\"type\":\"uint256\"}],\"name\":\"contractBStakeTest\",\"outputs\":[{\"" +
            "internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"" +
            "stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\"" +
            ":[],\"name\":\"contractBWithdrawRewardTest\",\"outputs\":[{\"internalType\":\"uint256\"" +
            ",\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\"" +
            ",\"type\":\"function\"},{\"constant\":false,\"inputs\":[],\"name\":\"getContractBAddressTest\"" +
            ",\"outputs\":[{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"}],\"payable\"" +
            ":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true," +
            "\"inputs\":[],\"name\":\"localContractAddrTest\",\"outputs\":[{\"internalType\":\"uint256\"," +
            "\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":" +
            "\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"otherContractAddrTest\",\"outputs\":" +
            "[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false," +
            "\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"internalType\"" +
            ":\"address\",\"name\":\"addr\",\"type\":\"address\"}],\"name\":\"rewardBalanceTest\",\"outputs\":" +
            "[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\"" +
            ":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"internalType\":\"address\"," +
            "\"name\":\"sr\",\"type\":\"address\"},{\"internalType\":\"uint256\",\"name\":\"amount\",\"type\"" +
            ":\"uint256\"}],\"name\":\"stakeTest\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\"" +
            ":\"bool\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\"" +
            ":false,\"inputs\":[],\"name\":\"unstakeTest\",\"outputs\":[],\"payable\":false,\"stateMutability\":" +
            "\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[],\"name\":\"withdrawRewardTest\"" +
            ",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"" +
            "stateMutability\":\"nonpayable\",\"type\":\"function\"}]";
    String factoryCode = "60806040526040516100109061005c565b604051809103906000f08015801561002c573d6000803" +
            "e3d6000fd5b50600180546001600160a01b03929092166001600160a01b031992831617905560008054909116331" +
            "79055610069565b6101108061039c83390190565b610324806100786000396000f3fe60806040523480156100105" +
            "7600080fd5b50d3801561001d57600080fd5b50d2801561002a57600080fd5b50600436106100ad5760003560e01" +
            "c8063b3e835e111610080578063b3e835e114610156578063c290120a14610160578063cb2d51cf1461016857806" +
            "3d30a28ee14610170578063e49de2d014610178576100ad565b806310198157146100b257806325a26c30146100d" +
            "65780638db848f114610116578063a223c65f14610130575b600080fd5b6100ba6101a4565b60408051600160016" +
            "0a01b039092168252519081900360200190f35b610102600480360360408110156100ec57600080fd5b506001600" +
            "160a01b0381351690602001356101b3565b604080519115158252519081900360200190f35b61011e61023f565b6" +
            "0408051918252519081900360200190f35b61011e6004803603602081101561014657600080fd5b5035600160016" +
            "0a01b03166102b6565b61015e6102c3565b005b61011e6102c7565b61011e6102cf565b61011e6102d4565b61010" +
            "26004803603604081101561018e57600080fd5b506001600160a01b0381351690602001356102e4565b600154600" +
            "1600160a01b031690565b60015460408051630e49de2d60e41b81526001600160a01b03858116600483015260248" +
            "2018590529151600093929092169163e49de2d09160448082019260209290919082900301818787803b158015610" +
            "20c57600080fd5b505af1158015610220573d6000803e3d6000fd5b505050506040513d602081101561023657600" +
            "080fd5b50519392505050565b60015460408051636148090560e11b815290516000926001600160a01b03169163c" +
            "290120a91600480830192602092919082900301818787803b15801561028557600080fd5b505af11580156102995" +
            "73d6000803e3d6000fd5b505050506040513d60208110156102af57600080fd5b5051905090565b6001600160a01" +
            "b0316d890565bd650565b6000d7905090565b30d890565b6001546001600160a01b0316d890565b60008183d5939" +
            "250505056fea26474726f6e58200f159acc541e931dc3493937394669085432201f51cc879b468fd11e81e425dc6" +
            "4736f6c634300050d00316080604052600080546001600160a01b0319163317905560ec806100246000396000f3f" +
            "e6080604052348015600f57600080fd5b50d38015601b57600080fd5b50d28015602757600080fd5b50600436106" +
            "04a5760003560e01c8063c290120a14604f578063e49de2d0146067575b600080fd5b605560a4565b60408051918" +
            "252519081900360200190f35b609060048036036040811015607b57600080fd5b506001600160a01b03813516906" +
            "020013560ac565b604080519115158252519081900360200190f35b6000d7905090565b60008183d593925050505" +
            "6fea26474726f6e58206c8eb8040501e8bc775fed429ec6e2ff16ae8313e3b626c7320c11844e7aca7a64736f6c6" +
            "34300050d0031";
    long value = 1000000000;
    long fee = 100000000;
    long consumeUserResourcePercent = 0;

    // deploy contract - 27kR8yXGYQykQ2fgH3h9sqfNBSeEh23ggja
    Transaction trx = TvmTestUtils.generateDeploySmartContractAndGetTransaction(
            contractName, address, ABI, factoryCode, value, fee, consumeUserResourcePercent,
            null);
    byte[] factoryAddress = WalletUtil.generateContractAddress(trx);
    String factoryAddressStr = StringUtil.encode58Check(factoryAddress);
    runtime = TvmTestUtils.processTransactionAndReturnRuntime(trx, manager, null);
    Assert.assertNull(runtime.getRuntimeError());

    // deploy contract - 27QGwFVehKHrjhjoLXsUtmS7BuaqAVGdHR3
    trx = TvmTestUtils.generateDeploySmartContractAndGetTransaction(
            "", address, ABI, factoryCode, value, fee, consumeUserResourcePercent,
            null);
    byte[] factoryAddressOther = WalletUtil.generateContractAddress(trx);
    String factoryAddressStrOther = StringUtil.encode58Check(factoryAddressOther);
    runtime = TvmTestUtils.processTransactionAndReturnRuntime(trx, manager, null);
    Assert.assertNull(runtime.getRuntimeError());

    // Trigger contract method: getContractBAddressTest()
    String methodByAddr = "getContractBAddressTest()";
    String hexInput = AbiUtil.parseMethod(methodByAddr, Arrays.asList(""));
    TVMTestResult result = TvmTestUtils
            .triggerContractAndReturnTvmTestResult(Hex.decode(OWNER_ADDRESS),
                    factoryAddress, Hex.decode(hexInput), 0, fee, manager, null);
    Assert.assertNull(result.getRuntime().getRuntimeError());
    byte[] returnValue = result.getRuntime().getResult().getHReturn();

    // Contract B Address: 27Wvtyhk4hHqRzogLPSJ21TjDdpuTJZWvQD"
    String tmpAddress = "a0" + Hex.toHexString(returnValue).substring(24);
    String contractBAddress = StringUtil.encode58Check(ByteArray.fromHexString(tmpAddress));
    rootRepository.addBalance(Hex.decode(tmpAddress), 30000000000000L);
    rootRepository.commit();

    // Trigger contract method: contractBStakeTest(address,uint256)
    methodByAddr = "contractBStakeTest(address,uint256)";
    String witness = "27Ssb1WE8FArwJVRRb8Dwy3ssVGuLY8L3S1";
    hexInput = AbiUtil.parseMethod(methodByAddr, Arrays.asList(witness, 200000000));
    result = TvmTestUtils
            .triggerContractAndReturnTvmTestResult(Hex.decode(OWNER_ADDRESS),
                    factoryAddress, Hex.decode(hexInput), 0, fee, manager, null);
    Assert.assertNull(result.getRuntime().getRuntimeError());

    returnValue = result.getRuntime().getResult().getHReturn();

    Assert.assertEquals(Hex.toHexString(returnValue),
            "0000000000000000000000000000000000000000000000000000000000000001");

    // Do Maintenance & Generate New Block
    maintenanceManager.doMaintenance();
    String key = "f31db24bfbd1a2ef19beddca0a0fa37632eded9ac666a05d3bd925f01dde1f62";
    byte[] privateKey = ByteArray.fromHexString(key);
    final ECKey ecKey = ECKey.fromPrivate(privateKey);
    byte[] witnessAddress = ecKey.getAddress();
    WitnessCapsule witnessCapsule = new WitnessCapsule(ByteString.copyFrom(witnessAddress));
    chainBaseManager.addWitness(ByteString.copyFrom(witnessAddress));
    Protocol.Block block = getSignedBlock(witnessCapsule.getAddress(), 1533529947843L, privateKey);
    manager.pushBlock(new BlockCapsule(block));//cycle: 1 addReward

    // Trigger contract method: rewardBalanceTest(address)
    methodByAddr = "rewardBalanceTest(address)";
    hexInput = AbiUtil.parseMethod(methodByAddr, Arrays.asList(contractBAddress));
    result = TvmTestUtils
            .triggerContractAndReturnTvmTestResult(Hex.decode(OWNER_ADDRESS),
                    factoryAddress, Hex.decode(hexInput), 0, fee, manager, null);
    Assert.assertNull(result.getRuntime().getRuntimeError());

    returnValue = result.getRuntime().getResult().getHReturn();

    Assert.assertEquals(Hex.toHexString(returnValue),
            "0000000000000000000000000000000000000000000000000000000000000000");

    // Trigger contract method: otherContractAddrTest()
    methodByAddr = "otherContractAddrTest()";
    hexInput = AbiUtil.parseMethod(methodByAddr, Arrays.asList(""));
    result = TvmTestUtils
            .triggerContractAndReturnTvmTestResult(Hex.decode(OWNER_ADDRESS),
                    factoryAddress, Hex.decode(hexInput), 0, fee, manager, null);
    Assert.assertNull(result.getRuntime().getRuntimeError());

    returnValue = result.getRuntime().getResult().getHReturn();

    Assert.assertEquals(Hex.toHexString(returnValue),
            "0000000000000000000000000000000000000000000000000000000000000000");

    // Trigger contract method: contractBWithdrawRewardTest()
    Protocol.Block newBlock = getBlock(witnessCapsule.getAddress(), System.currentTimeMillis(), privateKey);
    BlockCapsule blockCapsule = new BlockCapsule(newBlock);
    blockCapsule.generatedByMyself = true;

    methodByAddr = "contractBWithdrawRewardTest()";
    hexInput = AbiUtil.parseMethod(methodByAddr, Arrays.asList(""));
    result = TvmTestUtils
            .triggerContractAndReturnTvmTestResult(Hex.decode(OWNER_ADDRESS),
                    factoryAddress, Hex.decode(hexInput), 0, fee, manager, blockCapsule);
    Assert.assertNull(result.getRuntime().getRuntimeError());

    returnValue = result.getRuntime().getResult().getHReturn();

    Assert.assertEquals(Hex.toHexString(returnValue),
            "0000000000000000000000000000000000000000000000000000000000000000");

    // Execute Next Cycle
    maintenanceManager.doMaintenance();
    WitnessCapsule localWitnessCapsule = manager.getWitnessStore()
            .get(StringUtil.hexString2ByteString(WITNESS_SR1_ADDRESS).toByteArray());
    Assert.assertEquals(localWitnessCapsule.getVoteCount(), 305);

    // Trigger contract method: rewardBalanceTest(address)
    methodByAddr = "rewardBalanceTest(address)";
    hexInput = AbiUtil.parseMethod(methodByAddr, Arrays.asList(contractBAddress));
    result = TvmTestUtils
            .triggerContractAndReturnTvmTestResult(Hex.decode(OWNER_ADDRESS),
                    factoryAddress, Hex.decode(hexInput), 0, fee, manager, null);
    Assert.assertNull(result.getRuntime().getRuntimeError());

    returnValue = result.getRuntime().getResult().getHReturn();
    BigInteger reward = new BigInteger(Hex.toHexString(returnValue), 16);

    // Current Reward: Total Reward * Vote Rate
//    byte[] sr1 = decodeFromBase58Check(witness);
//    long totalReward = (long) ((double) rootRepository.getDelegationStore().getReward(1, sr1));
//    long totalVote = rootRepository.getDelegationStore().getWitnessVote(1, sr1);
//    double voteRate = (double) 200 / totalVote;
//    long curReward = (long) (totalReward * voteRate);
//    Assert.assertEquals(curReward, reward.longValue());

    //total reward: block reward + vote reward
    long blockReward = 25600000;
    long voteReward = 3003077;
    long totalReward = blockReward + voteReward;
    double voteRate = (double) 200 / 305;
    long curReward = (long) (totalReward * voteRate);
    Assert.assertEquals(reward.longValue(), curReward);

    // Trigger contract method: otherContractAddrTest()
    methodByAddr = "otherContractAddrTest()";
    hexInput = AbiUtil.parseMethod(methodByAddr, Arrays.asList(""));
    result = TvmTestUtils
            .triggerContractAndReturnTvmTestResult(Hex.decode(OWNER_ADDRESS),
                    factoryAddress, Hex.decode(hexInput), 0, fee, manager, null);
    Assert.assertNull(result.getRuntime().getRuntimeError());

    returnValue = result.getRuntime().getResult().getHReturn();

    Assert.assertEquals((new BigInteger(Hex.toHexString(returnValue), 16)).longValue(), curReward);

    // Trigger contract method: contractBWithdrawRewardTest()
    methodByAddr = "contractBWithdrawRewardTest()";
    hexInput = AbiUtil.parseMethod(methodByAddr, Arrays.asList(""));
    result = TvmTestUtils
            .triggerContractAndReturnTvmTestResult(Hex.decode(OWNER_ADDRESS),
                    factoryAddress, Hex.decode(hexInput), 0, fee, manager, blockCapsule);
    Assert.assertNull(result.getRuntime().getRuntimeError());

    returnValue = result.getRuntime().getResult().getHReturn();

    Assert.assertEquals((new BigInteger(Hex.toHexString(returnValue), 16)).longValue(), curReward);

    // Trigger contract method: rewardBalanceTest(address)
    methodByAddr = "rewardBalanceTest(address)";
    hexInput = AbiUtil.parseMethod(methodByAddr, Arrays.asList(contractBAddress));
    result = TvmTestUtils
            .triggerContractAndReturnTvmTestResult(Hex.decode(OWNER_ADDRESS),
                    factoryAddress, Hex.decode(hexInput), 0, fee, manager, null);
    Assert.assertNull(result.getRuntime().getRuntimeError());

    returnValue = result.getRuntime().getResult().getHReturn();

    Assert.assertEquals(Hex.toHexString(returnValue),
            "0000000000000000000000000000000000000000000000000000000000000000");

    // Trigger contract method: otherContractAddrTest()
    methodByAddr = "otherContractAddrTest()";
    hexInput = AbiUtil.parseMethod(methodByAddr, Arrays.asList(""));
    result = TvmTestUtils
            .triggerContractAndReturnTvmTestResult(Hex.decode(OWNER_ADDRESS),
                    factoryAddress, Hex.decode(hexInput), 0, fee, manager, null);
    Assert.assertNull(result.getRuntime().getRuntimeError());

    returnValue = result.getRuntime().getResult().getHReturn();

    Assert.assertEquals(Hex.toHexString(returnValue),
            "0000000000000000000000000000000000000000000000000000000000000000");

    // Trigger contract method: contractBWithdrawRewardTest()
    methodByAddr = "contractBWithdrawRewardTest()";
    hexInput = AbiUtil.parseMethod(methodByAddr, Arrays.asList(""));
    result = TvmTestUtils
            .triggerContractAndReturnTvmTestResult(Hex.decode(OWNER_ADDRESS),
                    factoryAddress, Hex.decode(hexInput), 0, fee, manager, null);
    Assert.assertNull(result.getRuntime().getRuntimeError());

    returnValue = result.getRuntime().getResult().getHReturn();

    Assert.assertEquals(Hex.toHexString(returnValue),
            "0000000000000000000000000000000000000000000000000000000000000000");

    ConfigLoader.disable = false;
  }

  public Protocol.Block getSignedBlock(ByteString witness, long time, byte[] privateKey) {
    long blockTime = System.currentTimeMillis() / 3000 * 3000;
    if (time != 0) {
      blockTime = time;
    } else {
      if (chainBaseManager.getHeadBlockId().getNum() != 0) {
        blockTime = chainBaseManager.getHeadBlockTimeStamp() + 3000;
      }
    }
    Param param = Param.getInstance();
    Param.Miner miner = param.new Miner(privateKey, witness, witness);
    BlockCapsule blockCapsule = manager
            .generateBlock(miner, time, System.currentTimeMillis() + 1000);
    Protocol.Block block = blockCapsule.getInstance();

    Protocol.BlockHeader.raw raw = block.getBlockHeader().getRawData().toBuilder()
            .setParentHash(ByteString
                    .copyFrom(chainBaseManager.getDynamicPropertiesStore()
                            .getLatestBlockHeaderHash().getBytes()))
            .setNumber(chainBaseManager.getDynamicPropertiesStore().getLatestBlockHeaderNumber() + 1)
            .setTimestamp(blockTime)
            .setWitnessAddress(witness)
            .build();

    ECKey ecKey = ECKey.fromPrivate(privateKey);
    ECKey.ECDSASignature signature = ecKey.sign(Sha256Hash.of(CommonParameter
            .getInstance().isECKeyCryptoEngine(), raw.toByteArray()).getBytes());
    ByteString sign = ByteString.copyFrom(signature.toByteArray());

    Protocol.BlockHeader blockHeader = block.getBlockHeader().toBuilder()
            .setRawData(raw)
            .setWitnessSignature(sign)
            .build();

    Protocol.Block signedBlock = block.toBuilder().setBlockHeader(blockHeader).build();

    return signedBlock;
  }

  public Protocol.Block getBlock(ByteString witness, long time, byte[] privateKey) {
    long blockTime = System.currentTimeMillis() / 3000 * 3000;
    if (time != 0) {
      blockTime = time;
    } else {
      if (chainBaseManager.getHeadBlockId().getNum() != 0) {
        blockTime = chainBaseManager.getHeadBlockTimeStamp() + 3000;
      }
    }
    Param param = Param.getInstance();
    Param.Miner miner = param.new Miner(privateKey, witness, witness);
    BlockCapsule blockCapsule = manager
            .generateBlock(miner, time, System.currentTimeMillis() + 1000);
    Protocol.Block block = blockCapsule.getInstance();
    Protocol.BlockHeader.raw raw = block.getBlockHeader().getRawData().toBuilder()
            .setParentHash(ByteString
                    .copyFrom(chainBaseManager.getDynamicPropertiesStore()
                            .getLatestBlockHeaderHash().getBytes()))
            .setNumber(chainBaseManager.getDynamicPropertiesStore().getLatestBlockHeaderNumber() + 1)
            .setTimestamp(blockTime)
            .setWitnessAddress(witness)
            .build();
    ECKey ecKey = ECKey.fromPrivate(privateKey);
    ECKey.ECDSASignature signature = ecKey.sign(Sha256Hash.of(CommonParameter
            .getInstance().isECKeyCryptoEngine(), raw.toByteArray()).getBytes());
    ByteString sign = ByteString.copyFrom(signature.toByteArray());
    Protocol.BlockHeader blockHeader = block.getBlockHeader().toBuilder()
            .setRawData(raw)
            .setWitnessSignature(ByteString.copyFromUtf8(""))
            .build();
    Protocol.Block signedBlock = block.toBuilder().setBlockHeader(blockHeader).build();
    return signedBlock;
  }
}

