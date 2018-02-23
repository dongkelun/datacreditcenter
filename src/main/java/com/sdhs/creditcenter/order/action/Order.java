package com.sdhs.creditcenter.order.action;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 * @description 订单数据处理
 * @author 		董可伦
 * @time   		2018-1-17
 *
 */
public class Order extends SqlMapClientDaoSupport {
	private static final Logger LOG = LoggerFactory.getLogger(Order.class);

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void execute() throws SQLException {
		LOG.info("Order start");
		SqlMapClient client = getSqlMapClient();
		HashMap paraMap = new HashMap();

		paraMap.put("year", 2017);
		List<HashMap> info = client.queryForList("order.getInfo", paraMap);
		try {
			client.startTransaction();
			client.getCurrentConnection().setAutoCommit(false);
			client.delete("order.delete");
			for (HashMap tempMap : info) {
				paraMap.put("signObjId", tempMap.get("SIGN_OBJ_ID"));
				paraMap.put("num1", tempMap.get("NUM1"));
				paraMap.put("num2", tempMap.get("NUM2"));
				paraMap.put("num3", tempMap.get("NUM3"));
				paraMap.put("num4", tempMap.get("NUM4"));
				paraMap.put("numFail", tempMap.get("NUM_FAIL"));
				paraMap.put("totalNum", tempMap.get("TOTAL_NUM"));
				paraMap.put("sumAmt", tempMap.get("SUM_AMT"));
				paraMap.put("avgAmt", tempMap.get("AVG_AMT"));
				paraMap.put("maxAmt", tempMap.get("MAX_AMT"));
				paraMap.put("minAmt", tempMap.get("MIN_AMT"));
				client.insert("order.insertResult", paraMap);
			}
			//测试异常
			//System.out.println(1/0);
			client.getCurrentConnection().commit();
			client.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			client.endTransaction();
		}
		LOG.info("Order end");

	}
}
