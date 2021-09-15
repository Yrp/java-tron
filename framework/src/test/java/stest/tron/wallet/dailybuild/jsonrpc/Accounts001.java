package stest.tron.wallet.dailybuild.jsonrpc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.junit.Assert;
import org.testng.annotations.Test;
import org.tron.api.GrpcAPI;
import org.tron.common.utils.ByteArray;
import org.tron.protos.Protocol.Block;
import stest.tron.wallet.common.client.utils.HttpMethed;

import stest.tron.wallet.common.client.utils.JsonRpcBase;

@Slf4j
public class Accounts001 extends JsonRpcBase {
  private JSONObject responseContent;
  private HttpResponse response;
  String realGasPrice;
  String bid = null;
  int indexNum = 0;
  String indexHex = null;
  JSONObject result = null;
  String transacionHash = null;
  String blockHash = null;
  String blockNumHex = null;
  String parentHash = null;
  String txTrieRoot = null;
  String witnessAddress = null;
  String gas = null;
  String feeLimit = null;

  List<String> transactionIdList = null;
  long size = 0;
  long blockTimeStamp = 0;

  @Test(enabled = true, description = "Json rpc api of eth_accounts")
  public void test01JsonRpcApiTestForEthAccounts() throws Exception {
    JsonArray params = new JsonArray();
    JsonObject requestBody = getJsonRpcBody("eth_accounts", params);
    response = getJsonRpc(jsonRpcNode, requestBody);
    responseContent = HttpMethed.parseResponseContent(response);
    List result = new ArrayList();
    logger.info(String.valueOf(result));
    Assert.assertEquals(responseContent.get("result"), result);
  }

  @Test(enabled = true, description = "Json rpc api of eth_blockNumber")
  public void test02JsonRpcApiTestForEthBlockNumber() throws Exception {
    JsonArray params = new JsonArray();
    JsonObject requestBody = getJsonRpcBody("eth_blockNumber", params);
    response = getJsonRpc(jsonRpcNode, requestBody);
    responseContent = HttpMethed.parseResponseContent(response);
    responseContent.get("result");
    String blockNum = responseContent.getString("result").substring(2);
    int blocknum1 = Integer.parseInt(blockNum, 16);
    response = HttpMethed.getNowBlock(httpFullNode);
    responseContent = HttpMethed.parseResponseContent(response);
    int blocknum2 =
        responseContent
            .getJSONObject("block_header")
            .getJSONObject("raw_data")
            .getInteger("number");
    logger.info(String.valueOf(blocknum1));
    logger.info(String.valueOf(blocknum2));
    Assert.assertTrue(Math.abs(blocknum1 - blocknum2) <= 2);
  }

  @Test(enabled = true, description = "Json rpc api of eth_call")
  public void test03JsonRpcApiTestForEthCall() throws Exception {
    JsonObject param = new JsonObject();
    param.addProperty("from", ByteArray.toHexString(jsonRpcOwnerAddress));
    param.addProperty("to", trc20AddressHex);
    param.addProperty("gas", "0");
    param.addProperty("gasPrice", "0");
    param.addProperty("value", "0");
    param.addProperty("data", "0x06fdde03");
    JsonArray params = new JsonArray();
    params.add(param);
    params.add("latest");
    JsonObject requestBody = getJsonRpcBody("eth_call", params);
    response = getJsonRpc(jsonRpcNode, requestBody);
    responseContent = HttpMethed.parseResponseContent(response);
    String dataResult = responseContent.getString("result");
    Assert.assertEquals(
        dataResult,
        "0x000000000000000000000000000000000000000000000000000"
            + "00000000000200000000000000000000000000000000000000000"
            + "00000000000000000000000a546f6b656e5452433230000000000"
            + "00000000000000000000000000000000000");
  }

  @Test(enabled = true, description = "Json rpc api of eth_chainId")
  public void test04JsonRpcApiTestForEthChainId() throws Exception {
    JsonArray params = new JsonArray();
    JsonObject requestBody = getJsonRpcBody("eth_chainId", params);
    response = getJsonRpc(jsonRpcNode, requestBody);
    responseContent = HttpMethed.parseResponseContent(response);
    responseContent.get("result");
    String blockId1 = responseContent.get("result").toString().substring(2);
    response = HttpMethed.getBlockByNum(httpFullNode, 0);
    responseContent = HttpMethed.parseResponseContent(response);
    String blockId2 = responseContent.getString("blockID");
    logger.info(blockId1);
    logger.info(blockId2);
    Assert.assertEquals(blockId1, blockId2);
  }

  @Test(enabled = true, description = "Json rpc api of eth_coinbase")
  public void test05JsonRpcApiTestForEthCoinbase() throws Exception {
    JsonArray params = new JsonArray();
    JsonObject requestBody = getJsonRpcBody("eth_coinbase", params);
    response = getJsonRpc(jsonRpcNode, requestBody);
    responseContent = HttpMethed.parseResponseContent(response);

    Assert.assertEquals(
        responseContent.getString("result"), "0x41bafb56091591790e00aa05eaddcc7dc1474b5d4b");
  }

  @Test(enabled = true, description = "Json rpc api of eth_gasPrice")
  public void test07JsonRpcApiTestForEthGasPrice() throws Exception {
    JsonArray params = new JsonArray();
    JsonObject requestBody = getJsonRpcBody("eth_gasPrice", params);
    response = getJsonRpc(jsonRpcNode, requestBody);
    responseContent = HttpMethed.parseResponseContent(response);
    responseContent.get("result");
    String gasPrice = responseContent.get("result").toString().substring(2);
    int gasPrice1 = Integer.parseInt(gasPrice, 16);
    logger.info(String.valueOf(gasPrice1));
    response = HttpMethed.getChainParameter(httpFullNode);
    responseContent = HttpMethed.parseResponseContent(response);
    JSONArray temp;
    temp = responseContent.getJSONArray("chainParameter");
    int gasPrice2 = 0;
    for (int i = 0; i < temp.size(); i++) {
      if (temp.getJSONObject(i).get("key").equals("getEnergyFee")) {
        gasPrice2 = temp.getJSONObject(i).getInteger("value");
      }
    }
    logger.info(String.valueOf(gasPrice2));
    Assert.assertEquals(gasPrice1, gasPrice2);
  }

  @Test(enabled = true, description = "Json rpc api of eth_getBalance")
  public void test08JsonRpcApiTestForEthGetBalance() throws Exception {
    JsonArray params = new JsonArray();
    params.add("0x415624C12E308B03A1A6B21D9B86E3942FAC1AB92B");
    params.add("latest");
    JsonObject requestBody = getJsonRpcBody("eth_getBalance", params);
    response = getJsonRpc(jsonRpcNode, requestBody);
    responseContent = HttpMethed.parseResponseContent(response);
    String balance = responseContent.getString("result").substring(2);
    Long balance1 = Long.parseLong(balance, 16);
    Long balance2 = HttpMethed.getBalance(httpFullNode, foundationAccountAddress);
    logger.info(balance1.toString());
    logger.info(balance2.toString());
    Assert.assertEquals(balance1, balance2);
  }

  @Test(enabled = true, description = "Json rpc api of eth_getBlockTransactionCountByHash")
  public void test11JsonRpcApiTestForEthGetBlockTransactionCountByHash() throws Exception {
    response = HttpMethed.getNowBlock(httpFullNode);
    responseContent = HttpMethed.parseResponseContent(response);
    String blockIdHash = responseContent.getString("blockID");
    long blockNum =
        responseContent.getJSONObject("block_header").getJSONObject("raw_data").getLong("number");
    logger.info("blockNum:" + blockNum);
    JsonArray params = new JsonArray();
    params.add("0x" + blockIdHash);
    logger.info("0x" + blockIdHash);
    JsonObject requestBody = getJsonRpcBody("eth_getBlockTransactionCountByHash", params);
    response = getJsonRpc(jsonRpcNode, requestBody);
    responseContent = HttpMethed.parseResponseContent(response);
    String transactionNum = responseContent.getString("result").substring(2);
    int transactionNum1 = Integer.parseInt(transactionNum, 16);
    logger.info(String.valueOf(transactionNum1));
    response = HttpMethed.getTransactionCountByBlocknum(httpFullNode, blockNum);
    responseContent = HttpMethed.parseResponseContent(response);
    int transactionNum2 = responseContent.getInteger("count");
    logger.info(String.valueOf(transactionNum2));
    Assert.assertEquals(transactionNum1, transactionNum2);
  }

  @Test(enabled = true, description = "Json rpc api of eth_getBlockTransactionCountByNumber")
  public void test12JsonRpcApiTestForEthGetBlockTransactionCountByNum01() throws Exception {
    response = HttpMethed.getNowBlock(httpFullNode);
    responseContent = HttpMethed.parseResponseContent(response);
    long blockNum =
        responseContent.getJSONObject("block_header").getJSONObject("raw_data").getLong("number");
    JsonArray params = new JsonArray();
    params.add(blockNum);
    logger.info(String.valueOf(blockNum));
    JsonObject requestBody = getJsonRpcBody("eth_getBlockTransactionCountByNumber", params);
    response = getJsonRpc(jsonRpcNode, requestBody);
    responseContent = HttpMethed.parseResponseContent(response);
    String transactionNum = responseContent.getString("result").substring(2);
    int transactionNum1 = Integer.parseInt(transactionNum, 16);
    logger.info(String.valueOf(transactionNum1));
    response = HttpMethed.getTransactionCountByBlocknum(httpFullNode, blockNum);
    responseContent = HttpMethed.parseResponseContent(response);
    int transactionNum2 = responseContent.getInteger("count");
    logger.info(String.valueOf(transactionNum2));
    Assert.assertEquals(transactionNum1, transactionNum2);
  }

  @Test(enabled = true, description = "Json rpc api of eth_getBlockTransactionCountByNumber")
  public void test12JsonRpcApiTestForEthGetBlockTransactionCountByNum02() throws Exception {
    response = HttpMethed.getNowBlock(httpFullNode);
    responseContent = HttpMethed.parseResponseContent(response);
    JsonArray params = new JsonArray();
    params.add("earliest");
    JsonObject requestBody = getJsonRpcBody("eth_getBlockTransactionCountByNumber", params);
    response = getJsonRpc(jsonRpcNode, requestBody);
    responseContent = HttpMethed.parseResponseContent(response);
    String transactionNum = responseContent.getString("result").substring(2);
    int transactionNum1 = Integer.parseInt(transactionNum, 16);
    logger.info(String.valueOf(transactionNum1));
    response = HttpMethed.getTransactionCountByBlocknum(httpFullNode, 0);
    responseContent = HttpMethed.parseResponseContent(response);
    int transactionNum2 = responseContent.getInteger("count");
    logger.info(String.valueOf(transactionNum2));
    Assert.assertEquals(transactionNum1, transactionNum2);
  }

  @Test(enabled = true, description = "Json rpc api of eth_getCode")
  public void test13JsonRpcApiTestForEthGetCode() throws Exception {

    JsonArray params = new JsonArray();
    params.add(contractAddressFrom58);
    params.add("latest");
    JsonObject requestBody = getJsonRpcBody("eth_getCode", params);
    response = getJsonRpc(jsonRpcNode, requestBody);
    responseContent = HttpMethed.parseResponseContent(response);
    String codeFromJsonRpc = responseContent.getString("result").substring(2);
    logger.info(codeFromJsonRpc);
    response = HttpMethed.getContractInfo(httpFullNode, contractAddressFrom58);
    logger.info("13contractAddressFrom58:" + contractAddressFrom58);
    responseContent = HttpMethed.parseResponseContent(response);
    String codeFromHttp = responseContent.getString("runtimecode");
    logger.info(codeFromHttp);
    Assert.assertEquals(codeFromJsonRpc, codeFromHttp);
  }

  @Test(enabled = false, description = "Json rpc api of eth_getStorageAt")
  public void test14JsonRpcApiTestForEthGetStorageAt01() throws Exception {

    JsonArray params = new JsonArray();
    params.add("41e5dc01c00136012136e82a5bfda5959110a3231f");
    params.add("latest");
    JsonObject requestBody = getJsonRpcBody("c", params);
    response = getJsonRpc(jsonRpcNode, requestBody);
    responseContent = HttpMethed.parseResponseContent(response);
    String codeFromJsonRpc = responseContent.getString("result").substring(2);
    logger.info(codeFromJsonRpc);
    response =
        HttpMethed.getContractInfo(httpFullNode, "41e5dc01c00136012136e82a5bfda5959110a3231f");
    responseContent = HttpMethed.parseResponseContent(response);
    String codeFromHttp = responseContent.getString("runtimecode");
    logger.info(codeFromHttp);
    Assert.assertEquals(codeFromJsonRpc, codeFromHttp);
  }

  @Test(enabled = true, description = "Json rpc api of eth_getTransactionByBlockNumberAndIndex")
  public void test15JsonRpcApiTestForEthGetTransactionByBlockNumberAndIndex() throws Exception {
    logger.info("15blockNum:" + blockNum);
    blockNumHex = "0x" + Integer.toHexString(blockNum);
    logger.info("blockNumHex:" + blockNumHex);
    JsonArray params = new JsonArray();
    params.add(blockNumHex);
    // 判断是块中的第几个交易
    indexNum = 0;
    response = HttpMethed.getBlockByNum(httpFullNode, blockNum);
    responseContent = HttpMethed.parseResponseContent(response);
    parentHash =
        responseContent
            .getJSONObject("block_header")
            .getJSONObject("raw_data")
            .getString("parentHash");
    txTrieRoot =
        responseContent
            .getJSONObject("block_header")
            .getJSONObject("raw_data")
            .getString("txTrieRoot");
    witnessAddress =
        responseContent
            .getJSONObject("block_header")
            .getJSONObject("raw_data")
            .getString("witness_address");
    feeLimit =
        responseContent
            .getJSONArray("transactions")
            .getJSONObject(0)
            .getJSONObject("raw_data")
            .getString("fee_limit");
    logger.info(feeLimit);

    JSONObject getBlockByNumResult = null;
    for (int i = 0; i < responseContent.getJSONArray("transactions").size(); i++) {
      if (txid.equals(
          responseContent.getJSONArray("transactions").getJSONObject(i).getString("txID"))) {
        indexNum = i;
        getBlockByNumResult = responseContent.getJSONArray("transactions").getJSONObject(i);
        bid = responseContent.getString("blockID");
        break;
      }
    }
    transactionIdList = new ArrayList<>();
    if (responseContent.getJSONArray("transactions").size() > 0) {
      for (int i = 0; i < responseContent.getJSONArray("transactions").size(); i++) {
        transactionIdList.add(
            "0x" + responseContent.getJSONArray("transactions").getJSONObject(i).getString("txID"));
      }
    }
    logger.info("15transactionIdList:" + transactionIdList);
    logger.info(String.valueOf(indexNum));
    indexHex = "0x" + Integer.toHexString(indexNum);
    logger.info("indexHex：" + indexHex);
    params.add(indexHex);
    JsonObject requestBody = getJsonRpcBody("eth_getTransactionByBlockNumberAndIndex", params);
    response = getJsonRpc(jsonRpcNode, requestBody);
    responseContent = HttpMethed.parseResponseContent(response);
    result = responseContent.getJSONObject("result");
    logger.info("15 result" + result);
    Map<String, Object> jsonrpcResult = new HashMap();
    for (Map.Entry<String, Object> entry : result.entrySet()) {
      jsonrpcResult.put(entry.getKey(), entry.getValue());
    }
    transacionHash = jsonrpcResult.get("hash").toString();
    logger.info("transactionHash：" + transacionHash);
    blockHash = jsonrpcResult.get("blockHash").toString();
    logger.info("jsonrpcResult:" + jsonrpcResult);
    response = HttpMethed.getTransactionInfoByBlocknum(httpFullNode, blockNum);
    logger.info("response:" + response);
    gas = jsonrpcResult.get("gas").toString();
    System.out.println(blockNum);
    List<JSONObject> responseContent1 = HttpMethed.parseResponseContentArray(response);
    logger.info("responseContent1:" + responseContent1);
    blockTimeStamp = responseContent1.get(0).getLong("blockTimeStamp");
    long gas = 0;
    for (int i = 0; i < responseContent1.size(); i++) {
      if (responseContent1.get(i).getString("id").equals(transactionIdList.get(0).substring(2))) {
        gas = responseContent1.get(i).getJSONObject("receipt").getLong("energy_usage_total");
        break;
      }
    }

    Assert.assertEquals(jsonrpcResult.get("gas").toString(), "0x" + Long.toHexString(gas));
    Assert.assertNull(jsonrpcResult.get("nonce"));
    Assert.assertEquals(
        jsonrpcResult.get("hash").toString(), "0x" + getBlockByNumResult.getString("txID"));
    Assert.assertEquals(jsonrpcResult.get("blockHash").toString(), "0x" + bid);
    Assert.assertEquals(jsonrpcResult.get("blockNumber").toString(), blockNumHex);
    Assert.assertEquals(jsonrpcResult.get("transactionIndex").toString(), indexHex);
    Assert.assertEquals(
        jsonrpcResult.get("from").toString(),
        "0x"
            + getBlockByNumResult
                .getJSONObject("raw_data")
                .getJSONArray("contract")
                .getJSONObject(0)
                .getJSONObject("parameter")
                .getJSONObject("value")
                .getString("owner_address")
                .substring(2));
    Assert.assertEquals(
        jsonrpcResult.get("to").toString(),
        "0x"
            + getBlockByNumResult
                .getJSONObject("raw_data")
                .getJSONArray("contract")
                .getJSONObject(0)
                .getJSONObject("parameter")
                .getJSONObject("value")
                .getString("contract_address")
                .substring(2));
    // Assert.assertEquals(jsonrpcResult.get("gasPrice").toString(),realGasPrice);
    Assert.assertEquals(jsonrpcResult.get("value").toString(), "0x1389");
    String data;
    if (getBlockByNumResult.getJSONObject("raw_data").getString("data") == null) {
      data = "0x";
    } else {
      data = getBlockByNumResult.getJSONObject("raw_data").getString("data").substring(2);
    }
    Assert.assertEquals(jsonrpcResult.get("input").toString(), data);

    long temp = Long.parseLong(getBlockByNumResult.getString("signature").substring(130, 131), 16);
    long v = Long.parseLong(getBlockByNumResult.getString("signature").substring(130, 132), 16);
    if (temp < 27) {
      v += 27;
    }
    Assert.assertEquals(Long.parseLong(jsonrpcResult.get("v").toString().substring(2), 16), v);
    Assert.assertEquals(
        jsonrpcResult.get("r").toString().substring(2),
        getBlockByNumResult.getString("signature").substring(2, 66));
    Assert.assertEquals(
        jsonrpcResult.get("s").toString().substring(2),
        getBlockByNumResult.getString("signature").substring(66, 130));
  }

  @Test(enabled = true, description = "Json rpc api of eth_getTransactionByBlockHashAndIndex")
  public void test16JsonRpcApiTestForEthGetTransactionByBlockHashAndIndex() throws Exception {
    JsonArray params = new JsonArray();
    params.add("0x" + bid);
    params.add(indexHex);
    logger.info("indexHex:" + indexHex);
    JsonObject requestBody = getJsonRpcBody("eth_getTransactionByBlockHashAndIndex", params);
    response = getJsonRpc(jsonRpcNode, requestBody);
    responseContent = HttpMethed.parseResponseContent(response);
    JSONObject resultForGetTransactionByBlockHashAndIndex = responseContent.getJSONObject("result");
    Assert.assertEquals(result, resultForGetTransactionByBlockHashAndIndex);
  }

  @Test(enabled = true, description = "Json rpc api of eth_getTransactionByHash")
  public void test17JsonRpcApiTestForEthGetTransactionByHash() throws Exception {
    JsonArray params = new JsonArray();
    params.add(transacionHash);
    JsonObject requestBody = getJsonRpcBody("eth_getTransactionByHash", params);
    response = getJsonRpc(jsonRpcNode, requestBody);
    responseContent = HttpMethed.parseResponseContent(response);
    JSONObject result1 = responseContent.getJSONObject("result");
    Assert.assertEquals(result, result1);
  }

  @Test(enabled = false, description = "Json rpc api of eth_getTransactionReceipt")
  public void test18JsonRpcApiTestForEthGetTransactionReceipt() throws Exception {
    JsonArray params = new JsonArray();
    params.add(transacionHash);
    JsonObject requestBody = getJsonRpcBody("eth_getTransactionReceipt", params);
    response = getJsonRpc(jsonRpcNode, requestBody);
    responseContent = HttpMethed.parseResponseContent(response);
    JSONObject result1 = responseContent.getJSONObject("result");
    logger.info("result:" + result.toString());
    Assert.assertEquals(result, result1);
  }

  @Test(enabled = true, description = "Json rpc api of eth_getUncleByBlockHashAndIndex")
  public void test19JsonRpcApiTestForEthGetUncleByBlockHashAndIndex() throws Exception {
    JsonArray params = new JsonArray();
    params.add("0x0000000000f9cc56243898cbe88685678855e07f51c5af91322c225ce3693868");
    params.add("0x");
    JsonObject requestBody = getJsonRpcBody("eth_getUncleByBlockHashAndIndex", params);
    response = getJsonRpc(jsonRpcNode, requestBody);
    responseContent = HttpMethed.parseResponseContent(response);
    String result = responseContent.getString("result");
    logger.info(result);
    Assert.assertNull(result);
  }

  @Test(enabled = true, description = "Json rpc api of eth_getUncleByBlockNumberAndIndex")
  public void test20JsonRpcApiTestForEthGetUncleByBlockNumberAndIndex() throws Exception {
    JsonArray params = new JsonArray();
    params.add("0xeb82f0");
    params.add("0x");
    JsonObject requestBody = getJsonRpcBody("eth_getUncleByBlockNumberAndIndex", params);
    response = getJsonRpc(jsonRpcNode, requestBody);
    responseContent = HttpMethed.parseResponseContent(response);
    String result = responseContent.getString("result");
    logger.info(result);
    Assert.assertNull(result);
  }

  @Test(enabled = true, description = "Json rpc api of eth_getUncleCountByBlockHash")
  public void test21JsonRpcApiTestForEthGetUncleCountByBlockHash() throws Exception {
    JsonArray params = new JsonArray();
    params.add("0x0000000000f9cc56243898cbe88685678855e07f51c5af91322c225ce3693868");
    JsonObject requestBody = getJsonRpcBody("eth_getUncleCountByBlockHash", params);
    response = getJsonRpc(jsonRpcNode, requestBody);
    responseContent = HttpMethed.parseResponseContent(response);
    String result = responseContent.getString("result");
    logger.info(result);
    Assert.assertEquals(result, "0x0");
  }

  @Test(enabled = true, description = "Json rpc api of eth_getUncleCountByBlockNumber")
  public void test22JsonRpcApiTestForEthGetUncleCountByBlockNumber() throws Exception {
    JsonArray params = new JsonArray();
    params.add("eth_getUncleCountByBlockNumber");
    JsonObject requestBody = getJsonRpcBody("eth_getUncleCountByBlockNumber", params);
    response = getJsonRpc(jsonRpcNode, requestBody);
    responseContent = HttpMethed.parseResponseContent(response);
    String result = responseContent.getString("result");
    logger.info(result);
    Assert.assertEquals(result, "0x0");
  }

  @Test(enabled = true, description = "Json rpc api of eth_getWork")
  public void test23JsonRpcApiTestForEthGetWork() throws Exception {
    JsonArray params = new JsonArray();
    JsonObject requestBody = getJsonRpcBody("eth_getWork", params);
    response = getJsonRpc(jsonRpcNode, requestBody);
    responseContent = HttpMethed.parseResponseContent(response);
    String result = responseContent.getString("result");
    int resultLen = result.length();
    String result1 = result.substring(4, resultLen - 12);
    response = HttpMethed.getNowBlock(httpFullNode);
    responseContent = HttpMethed.parseResponseContent(response);
    String result2 = responseContent.getString("blockID");
    logger.info(result1);
    logger.info(result2);
    Assert.assertEquals(result1, result2);
  }

  @Test(enabled = true, description = "Json rpc api of eth_hashrate")
  public void test24JsonRpcApiTestForEthHashRate() throws Exception {
    JsonArray params = new JsonArray();
    JsonObject requestBody = getJsonRpcBody("eth_hashrate", params);
    response = getJsonRpc(jsonRpcNode, requestBody);
    responseContent = HttpMethed.parseResponseContent(response);
    String result = responseContent.getString("result");
    logger.info(result);
    Assert.assertEquals(result, "0x0");
  }

  @Test(enabled = true, description = "Json rpc api of eth_mining")
  public void test25JsonRpcApiTestForEthMining() throws Exception {
    JsonArray params = new JsonArray();
    JsonObject requestBody = getJsonRpcBody("eth_mining", params);
    response = getJsonRpc(jsonRpcNode, requestBody);
    responseContent = HttpMethed.parseResponseContent(response);
    String result = responseContent.getString("result");
    logger.info(result);
    Assert.assertEquals(result, "true");
  }

  @Test(enabled = true, description = "Json rpc api of eth_protocolVersion")
  public void test26JsonRpcApiTestForEthProtocolVersion() throws Exception {
    JsonArray params = new JsonArray();
    JsonObject requestBody = getJsonRpcBody("eth_protocolVersion", params);
    response = getJsonRpc(jsonRpcNode, requestBody);
    responseContent = HttpMethed.parseResponseContent(response);
    String protocolVersion = responseContent.getString("result").substring(2);
    Long protocolVersion1 = Long.parseLong(protocolVersion, 16);
    System.out.println(protocolVersion1);
    response = HttpMethed.getNowBlock(httpFullNode);
    responseContent = HttpMethed.parseResponseContent(response);
    Long protocolVersion2 =
        responseContent.getJSONObject("block_header").getJSONObject("raw_data").getLong("version");
    logger.info(protocolVersion1.toString());
    logger.info(protocolVersion2.toString());
    Assert.assertEquals(protocolVersion1, protocolVersion2);
  }

  @Test(enabled = true, description = "Json rpc api of eth_syncing")
  public void test27JsonRpcApiTestForEthSyncing() throws Exception {
    JsonArray params = new JsonArray();
    JsonObject requestBody = getJsonRpcBody("eth_syncing", params);
    response = getJsonRpc(jsonRpcNode, requestBody);
    responseContent = HttpMethed.parseResponseContent(response);
    JSONObject temp = responseContent.getJSONObject("result");
    logger.info(temp.toString());
    Assert.assertTrue(temp.containsKey("startingBlock"));
    Assert.assertTrue(temp.containsKey("currentBlock"));
    Assert.assertTrue(temp.containsKey("highestBlock"));
  }

  @Test(enabled = true, description = "Json rpc api of net_listening")
  public void test28JsonRpcApiTestForNetListening() throws Exception {
    JsonArray params = new JsonArray();
    JsonObject requestBody = getJsonRpcBody("net_listening", params);
    response = getJsonRpc(jsonRpcNode, requestBody);
    responseContent = HttpMethed.parseResponseContent(response);
    Boolean temp = responseContent.getBoolean("result");
    logger.info(temp.toString());
    response = HttpMethed.getNodeInfo(httpFullNode);
    responseContent = HttpMethed.parseResponseContent(response);
    boolean expect = false;
    System.out.println(responseContent);
    int num = responseContent.getInteger("activeConnectCount");
    if (num >= 1) {
      expect = true;
    }
    Assert.assertEquals(temp, expect);
  }

  @Test(enabled = true, description = "Json rpc api of net_peerCount")
  public void test29JsonRpcApiTestForNetPeerCount() throws Exception {
    JsonArray params = new JsonArray();
    JsonObject requestBody = getJsonRpcBody("net_peerCount", params);
    response = getJsonRpc(jsonRpcNode, requestBody);
    responseContent = HttpMethed.parseResponseContent(response);
    String result = responseContent.getString("result");
    logger.info(result);
    Assert.assertNotNull(result);
  }

  @Test(enabled = true, description = "Json rpc api of net_version")
  public void test30JsonRpcApiTestForEthVersion() throws Exception {
    JsonArray params = new JsonArray();
    JsonObject requestBody = getJsonRpcBody("net_version", params);
    response = getJsonRpc(jsonRpcNode, requestBody);
    responseContent = HttpMethed.parseResponseContent(response);
    String firstBlockHash1 = responseContent.getString("result").substring(2);
    response = HttpMethed.getBlockByNum(httpFullNode, 0);
    responseContent = HttpMethed.parseResponseContent(response);
    String firstBlockHash2 = responseContent.getString("blockID");
    logger.info(firstBlockHash1);
    logger.info(firstBlockHash2);
    Assert.assertEquals(firstBlockHash1, firstBlockHash2);
  }

  @Test(enabled = true, description = "Json rpc api of web3_clientVersion")
  public void test31JsonRpcApiTestForWeb3ClientVersion() throws Exception {
    JsonArray params = new JsonArray();
    JsonObject requestBody = getJsonRpcBody("web3_clientVersion", params);
    response = getJsonRpc(jsonRpcNode, requestBody);
    responseContent = HttpMethed.parseResponseContent(response);
    String result = responseContent.getString("result");
    List<String> resultList = new ArrayList<>();
    for (String str : result.split("/")) {
      resultList.add(str);
    }
    Assert.assertEquals(resultList.size(), 5);
    Assert.assertEquals(resultList.get(0), "TRON");
    Assert.assertEquals(resultList.get(1).substring(0, 1), "v");
    Assert.assertEquals(resultList.get(2), "Linux");
    Assert.assertEquals(resultList.get(3), "Java1.8");
    Assert.assertEquals(resultList.get(4).substring(0, 11), "GreatVoyage");
  }

  @Test(enabled = true, description = "Json rpc api of web3_sha3")
  public void test32JsonRpcApiTestForWeb3Sha3() throws Exception {
    JsonArray params = new JsonArray();
    params.add("0x08");
    JsonObject requestBody1 = getJsonRpcBody("web3_sha3", params);
    response = getEthHttps(ethHttpsNode, requestBody1);
    responseContent = HttpMethed.parseResponseContent(response);
    System.out.println(responseContent);
    String result1 = responseContent.getString("result");
    JsonObject requestBody2 = getJsonRpcBody("web3_sha3", params);
    response = getJsonRpc(jsonRpcNode, requestBody2);
    responseContent = HttpMethed.parseResponseContent(response);
    String result2 = responseContent.getString("result");
    Assert.assertEquals(result1, result2);
    System.out.println(result2);
  }

  @Test(enabled = true, description = "Json rpc api of eth_compileLLL")
  public void test33JsonRpcApiTestForEthCompileLll() throws Exception {
    JsonArray params = new JsonArray();
    params.add("(returnlll (suicide (caller)))");
    JsonObject requestBody1 = getJsonRpcBody("eth_compileLLL", params);
    response = getJsonRpc(jsonRpcNode, requestBody1);
    responseContent = HttpMethed.parseResponseContent(response);
    String errorMessage = responseContent.getJSONObject("error").getString("message");
    Assert.assertEquals(errorMessage, "the method eth_compileLLL does not exist/is not available");
  }

  @Test(enabled = true, description = "Json rpc api of eth_compileSerpent")
  public void test34JsonRpcApiTestForEthCompileSerpent() throws Exception {
    JsonArray params = new JsonArray();
    params.add("/* some serpent */");
    JsonObject requestBody = getJsonRpcBody("eth_compileSerpent", params);
    response = getJsonRpc(jsonRpcNode, requestBody);
    responseContent = HttpMethed.parseResponseContent(response);
    String errorMessage = responseContent.getJSONObject("error").getString("message");
    Assert.assertEquals(
        errorMessage, "the method eth_compileSerpent does not exist/is not available");
  }

  @Test(enabled = true, description = "Json rpc api of eth_compileSolidity")
  public void test35JsonRpcApiTestForEthCompileSolidity() throws Exception {
    JsonArray params = new JsonArray();
    params.add("contract test { function multiply(uint a) returns(uint d) {   return a * 7;   } }");
    JsonObject requestBody = getJsonRpcBody("eth_compileSolidity", params);
    response = getJsonRpc(jsonRpcNode, requestBody);
    responseContent = HttpMethed.parseResponseContent(response);
    String errorMessage = responseContent.getJSONObject("error").getString("message");
    Assert.assertEquals(
        errorMessage, "the method eth_compileSolidity does not exist/is not available");
  }

  @Test(enabled = true, description = "Json rpc api of eth_getCompilers")
  public void test36JsonRpcApiTestForEthCompileSolidity() throws Exception {
    JsonArray params = new JsonArray();
    JsonObject requestBody = getJsonRpcBody("eth_getCompilers", params);
    response = getJsonRpc(jsonRpcNode, requestBody);
    responseContent = HttpMethed.parseResponseContent(response);
    String errorMessage = responseContent.getJSONObject("error").getString("message");
    Assert.assertEquals(
        errorMessage, "the method eth_getCompilers does not exist/is not available");
  }

  @Test(enabled = true, description = "Json rpc api of eth_getTransactionCount")
  public void test37JsonRpcApiTestForEthGetTransactionCount() throws Exception {
    JsonArray params = new JsonArray();
    params.add("0x407d73d8a49eeb85d32cf465507dd71d507100c1");
    params.add("latest");
    JsonObject requestBody = getJsonRpcBody("eth_getTransactionCount", params);
    response = getJsonRpc(jsonRpcNode, requestBody);
    responseContent = HttpMethed.parseResponseContent(response);
    String errorMessage = responseContent.getJSONObject("error").getString("message");
    Assert.assertEquals(
        errorMessage, "the method eth_getTransactionCount does not exist/is not available");
  }

  @Test(enabled = true, description = "Json rpc api of eth_sendRawTransaction")
  public void test38JsonRpcApiTestForEthSendRawTransaction() throws Exception {
    JsonArray params = new JsonArray();
    params.add("0x234");
    JsonObject requestBody = getJsonRpcBody("eth_sendRawTransaction", params);
    response = getJsonRpc(jsonRpcNode, requestBody);
    responseContent = HttpMethed.parseResponseContent(response);
    String errorMessage = responseContent.getJSONObject("error").getString("message");
    Assert.assertEquals(
        errorMessage, "the method eth_sendRawTransaction does not exist/is not available");
  }

  @Test(enabled = true, description = "Json rpc api of eth_sendTransaction")
  public void test39JsonRpcApiTestForEthSendTransaction() throws Exception {
    JsonArray params = new JsonArray();
    JsonObject temp = new JsonObject();
    params.add(temp);
    temp.addProperty("from", "0xb60e8dd61c5d32be8058bb8eb970870f07233155");
    temp.addProperty("to", "0xd46e8dd67c5d32be8058bb8eb970870f07244567");
    temp.addProperty("gas", "0x76c0");
    temp.addProperty("gasPrice", "0x9184e72a000");
    temp.addProperty(
        "data",
        "0xd46e8dd67c5d32be8d46e8dd67c5d32be8058bb8eb970870f072445675058bb8eb970870f072445675");
    temp.addProperty("value", "0x9184e72a");

    JsonObject requestBody = getJsonRpcBody("eth_sendTransaction", params);
    response = getJsonRpc(jsonRpcNode, requestBody);
    responseContent = HttpMethed.parseResponseContent(response);
    String errorMessage = responseContent.getJSONObject("error").getString("message");
    Assert.assertEquals(
        errorMessage, "the method eth_sendTransaction does not exist/is not available");
  }

  @Test(enabled = true, description = "Json rpc api of eth_sign")
  public void test40JsonRpcApiTestForEthSign() throws Exception {
    JsonArray params = new JsonArray();
    params.add("0x9b2055d370f73ec7d8a03e965129118dc8f5bf83");
    params.add("0xdeadbeaf");
    JsonObject requestBody = getJsonRpcBody("eth_sign", params);
    response = getJsonRpc(jsonRpcNode, requestBody);
    responseContent = HttpMethed.parseResponseContent(response);
    String errorMessage = responseContent.getJSONObject("error").getString("message");
    Assert.assertEquals(errorMessage, "the method eth_sign does not exist/is not available");
  }

  @Test(enabled = true, description = "Json rpc api of eth_signTransaction")
  public void test41JsonRpcApiTestForEthSignTransaction() throws Exception {
    JsonArray params = new JsonArray();
    JsonObject temp = new JsonObject();
    params.add(temp);
    temp.addProperty(
        "data",
        "0xd46e8dd67c5d32be8d46e8dd67c5d32be8058bb8eb970870f072445675058bb8eb970870f072445675");
    temp.addProperty("from", "0xb60e8dd61c5d32be8058bb8eb970870f07233155");
    temp.addProperty("gas", "0x76c0");
    temp.addProperty("gasPrice", "0x9184e72a000");
    temp.addProperty("to", "0xd46e8dd67c5d32be8058bb8eb970870f07244567");
    temp.addProperty("value", "0x9184e72a");

    JsonObject requestBody = getJsonRpcBody("eth_signTransaction", params);
    response = getJsonRpc(jsonRpcNode, requestBody);
    responseContent = HttpMethed.parseResponseContent(response);
    String errorMessage = responseContent.getJSONObject("error").getString("message");
    Assert.assertEquals(
        errorMessage, "the method eth_signTransaction does not exist/is not available");
  }

  @Test(enabled = true, description = "Json rpc api of eth_submitWork")
  public void test42JsonRpcApiTestForEthSubmitWork() throws Exception {
    JsonArray params = new JsonArray();
    params.add("0x0000000000000001");
    params.add("0x1234567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef");
    params.add("0xD1GE5700000000000000000000000000D1GE5700000000000000000000000000");
    JsonObject requestBody = getJsonRpcBody("eth_submitWork", params);
    response = getJsonRpc(jsonRpcNode, requestBody);
    responseContent = HttpMethed.parseResponseContent(response);
    String errorMessage = responseContent.getJSONObject("error").getString("message");
    Assert.assertEquals(errorMessage, "the method eth_submitWork does not exist/is not available");
  }

  @Test(enabled = true, description = "Json rpc api of parity_nextNonce")
  public void test43JsonRpcApiTestForParityNextNonce() throws Exception {
    JsonArray params = new JsonArray();
    params.add("0x9b2055d370f73ec7d8a03e965129118dc8f5bf83");
    JsonObject requestBody = getJsonRpcBody("parity_nextNonce", params);
    response = getJsonRpc(jsonRpcNode, requestBody);
    responseContent = HttpMethed.parseResponseContent(response);
    String errorMessage = responseContent.getJSONObject("error").getString("message");
    Assert.assertEquals(
        errorMessage, "the method parity_nextNonce does not exist/is not available");
  }

  @Test(enabled = true, description = "Json rpc api of eth_submitHashrate")
  public void test44JsonRpcApiTestForEthSubmitHashrate() throws Exception {
    JsonArray params = new JsonArray();
    params.add("0x0000000000000000000000000000000000000000000000000000000000500000");
    params.add("0x59daa26581d0acd1fce254fb7e85952f4c09d0915afd33d3886cd914bc7d283c");
    JsonObject requestBody = getJsonRpcBody("eth_submitHashrate", params);
    response = getJsonRpc(jsonRpcNode, requestBody);
    responseContent = HttpMethed.parseResponseContent(response);
    String errorMessage = responseContent.getJSONObject("error").getString("message");
    Assert.assertEquals(
        errorMessage, "the method eth_submitHashrate does not exist/is not available");
  }

  @Test(enabled = true, description = "Json rpc api of eth_getBlockByHash params is false")
  public void test45JsonRpcApiTestForEthGetBlockByHash() throws Exception {
    JsonArray params = new JsonArray();
    params.add(blockHash);
    params.add(false);
    JsonObject requestBody = getJsonRpcBody("eth_getBlockByHash", params);
    response = getJsonRpc(jsonRpcNode, requestBody);
    responseContent = HttpMethed.parseResponseContent(response);
    JSONObject getBlockByHashResult = responseContent.getJSONObject("result");

    Assert.assertNull(getBlockByHashResult.getString("nonce"));
    Assert.assertNull(getBlockByHashResult.getString("sha3Uncles"));
    Assert.assertNull(getBlockByHashResult.getString("receiptsRoot"));
    Assert.assertNull(getBlockByHashResult.getString("difficulty"));
    Assert.assertNull(getBlockByHashResult.getString("totalDifficulty"));
    Assert.assertNull(getBlockByHashResult.getString("extraData"));
    Assert.assertNull(getBlockByHashResult.getString("baseFeePerGas"));
    Assert.assertNull(getBlockByHashResult.getString("mixHash"));
    Assert.assertEquals(getBlockByHashResult.getString("uncles"), new ArrayList<>().toString());
    Assert.assertEquals(getBlockByHashResult.getString("stateRoot"), "0x");
    Assert.assertEquals(
        getBlockByHashResult.getString("logsBloom"),
        "0x00000000000000000000000000000000000000000000000000"
            + "0000000000000000000000000000000000000000000000000000000000000000"
            + "00000000000000000000000000000000000000000000000000000000000000000000000"
            + "00000000000000000000000000000000000000000000000000000000000000000000000000000000"
            + "000000000000000000000000000000000000000000000000000000000000000000000000000000"
            + "0000000000000000000000000000000000000000000000000000000000000000000000000"
            + "00000000000000000000000000000000000000000000000000000000000"
            + "0000000000000000000000000000000000000");
    Assert.assertEquals(getBlockByHashResult.getString("number"), blockNumHex);
    Assert.assertEquals(getBlockByHashResult.getString("hash"), "0x" + bid);
    Assert.assertEquals(getBlockByHashResult.getString("parentHash"), "0x" + parentHash);
    Assert.assertEquals(getBlockByHashResult.getString("transactionsRoot"), "0x" + txTrieRoot);
    Assert.assertEquals(getBlockByHashResult.getString("miner"), "0x" + witnessAddress);
    Assert.assertEquals(getBlockByHashResult.getString("gasUsed"), gas);
    Assert.assertEquals(
        String.valueOf(Long.parseLong(getBlockByHashResult.getString("gasLimit").substring(2), 16)),
        feeLimit);
    Assert.assertEquals(
        Long.parseLong(getBlockByHashResult.getString("timestamp").substring(2), 16),
        blockTimeStamp);
    final GrpcAPI.NumberMessage message =
        GrpcAPI.NumberMessage.newBuilder().setNum(blockNum).build();
    Block bl = blockingStubFull.getBlockByNum(message);
    logger.info("sizeFromJrpc:" + bl.getSerializedSize());
    logger.info(
        "sizeFromJsonRPc:"
            + Long.parseLong(getBlockByHashResult.getString("size").substring(2), 16));
    size = bl.getSerializedSize();
    Assert.assertEquals(
        Long.parseLong(getBlockByHashResult.getString("size").substring(2), 16),
        bl.getSerializedSize());

    Long.parseLong(getBlockByHashResult.getString("timestamp").substring(2), 16);
    JSONArray transactionId = getBlockByHashResult.getJSONArray("transactions");
    List<String> transactionIdListFromGetBlockByHash = new ArrayList<>();
    if (transactionId.size() > 0) {
      for (int i = 0; i < transactionId.size(); i++) {
        transactionIdListFromGetBlockByHash.add(transactionId.get(i).toString());
      }
    }
    Assert.assertEquals(transactionIdListFromGetBlockByHash, transactionIdList);
  }

  @Test(enabled = true, description = "Json rpc api of eth_getBlockByNumber params is true")
  public void test46JsonRpcApiTestForEthGetBlockByNumber() throws Exception {
    JsonArray params = new JsonArray();
    params.add(blockNumHex);
    logger.info("46blockNumHex:" + blockNumHex);
    params.add(true);
    JsonObject requestBody = getJsonRpcBody("eth_getBlockByNumber", params);
    response = getJsonRpc(jsonRpcNode, requestBody);
    responseContent = HttpMethed.parseResponseContent(response);
    JSONObject getBlockByHashResult = responseContent.getJSONObject("result");
    logger.info("getBlockByHashResult:" + getBlockByHashResult);

    Assert.assertNull(getBlockByHashResult.getString("nonce"));
    Assert.assertNull(getBlockByHashResult.getString("sha3Uncles"));
    Assert.assertNull(getBlockByHashResult.getString("receiptsRoot"));
    Assert.assertNull(getBlockByHashResult.getString("difficulty"));
    Assert.assertNull(getBlockByHashResult.getString("totalDifficulty"));
    Assert.assertNull(getBlockByHashResult.getString("extraData"));
    Assert.assertNull(getBlockByHashResult.getString("baseFeePerGas"));
    Assert.assertNull(getBlockByHashResult.getString("mixHash"));
    Assert.assertEquals(getBlockByHashResult.getString("uncles"), new ArrayList<>().toString());
    Assert.assertEquals(getBlockByHashResult.getString("stateRoot"), "0x");
    Assert.assertEquals(
        getBlockByHashResult.getString("logsBloom"),
        "0x00000000000000000000000000000000000000000000000000000000000000000000"
            + "0000000000000000000000000000000000000000000000000000000000000000000000"
            + "00000000000000000000000000000000000000000000000000000000000000000000000"
            + "000000000000000000000000000000000000000000000000000000000000000000000000"
            + "000000000000000000000000000000000000000000000000000000000000000000000000"
            + "0000000000000000000000000000000000000000000000000000000000000000000000000"
            + "0000000000000000000000000000000000000000000000000000000000000000000000000"
            + "0000000000000");
    Assert.assertEquals(getBlockByHashResult.getString("number"), blockNumHex);
    Assert.assertEquals(getBlockByHashResult.getString("hash"), "0x" + bid);
    Assert.assertEquals(getBlockByHashResult.getString("parentHash"), "0x" + parentHash);
    Assert.assertEquals(getBlockByHashResult.getString("transactionsRoot"), "0x" + txTrieRoot);
    Assert.assertEquals(getBlockByHashResult.getString("miner"), "0x" + witnessAddress);
    Assert.assertEquals(getBlockByHashResult.getString("gasUsed"), gas);
    Assert.assertEquals(
        String.valueOf(Long.parseLong(getBlockByHashResult.getString("gasLimit").substring(2), 16)),
        feeLimit);
    Assert.assertEquals(
        Long.parseLong(getBlockByHashResult.getString("timestamp").substring(2), 16),
        blockTimeStamp);
    Assert.assertEquals(
        Long.parseLong(getBlockByHashResult.getString("size").substring(2), 16), size);

    JSONArray transactionsList = getBlockByHashResult.getJSONArray("transactions");
    logger.info("transactionsList:" + transactionsList);
    List<String> transactionInfoListFromGetBlockByHash = new ArrayList<>();
    if (transactionsList.size() > 0) {
      for (int i = 0; i < transactionsList.size(); i++) {
        transactionInfoListFromGetBlockByHash.add(transactionsList.get(i).toString());
      }
    }
    List<String> transactionInfoListFromTransactionByBlockNumberAndIndex = new ArrayList<>();
    for (int i = 0; i < transactionsList.size(); i++) {
      JsonArray paramsForEthGetTransactionByBlockNumberAndIndex = new JsonArray();
      paramsForEthGetTransactionByBlockNumberAndIndex.add(blockNumHex);
      String index = "0x" + Integer.toHexString(i);
      logger.info("index:" + index);
      paramsForEthGetTransactionByBlockNumberAndIndex.add(index);
      logger.info(
          "paramsForEthGetTransactionByBlockNumberAndIndex:"
              + paramsForEthGetTransactionByBlockNumberAndIndex);
      JsonObject requestBodyForTransactionByBlockNumberAndIndex =
          getJsonRpcBody(
              "eth_getTransactionByBlockNumberAndIndex",
              paramsForEthGetTransactionByBlockNumberAndIndex);
      response = getJsonRpc(jsonRpcNode, requestBodyForTransactionByBlockNumberAndIndex);
      responseContent = HttpMethed.parseResponseContent(response);
      logger.info("responseContent:" + responseContent);
      result = responseContent.getJSONObject("result");
      logger.info("result:" + result);
      transactionInfoListFromTransactionByBlockNumberAndIndex.add(result.toString());
    }
    Assert.assertEquals(
        transactionInfoListFromGetBlockByHash,
        transactionInfoListFromTransactionByBlockNumberAndIndex);
  }
}