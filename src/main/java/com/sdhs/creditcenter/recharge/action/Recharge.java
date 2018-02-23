package com.sdhs.creditcenter.recharge.action;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.sdhs.pub.BaseSqlMapClientDaoSupport;

/**
 * 
 * @description 充值表处理
 * @author 		董可伦
 * @time   		2018年2月22日
 *
 */
public class Recharge extends BaseSqlMapClientDaoSupport {
	private static final Logger LOG = LoggerFactory.getLogger(Recharge.class);

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void execute() throws SQLException {
		LOG.info("Recharge start");
		choseSqlClient("sqlMapClient_ltch");
		SqlMapClient client = getSqlMapClient();
		List<HashMap> info = client.queryForList("recharge.getInfo");
		choseSqlClient("sqlMapClient");
		client = getSqlMapClient();
		try {
			client.startTransaction();
			client.getCurrentConnection().setAutoCommit(false);
			client.delete("recharge.delete");
			HashMap paraMap = new HashMap();
			for (HashMap tempMap : info) {
				paraMap.put("cardNo", tempMap.get("CARDNO"));
				paraMap.put("num", tempMap.get("NUM"));
				paraMap.put("totalMoney", tempMap.get("TOTAL_MONEY"));
				paraMap.put("maxMoney", tempMap.get("MAX_MONEY"));
				paraMap.put("minMoney", tempMap.get("MIN_MONEY"));
				paraMap.put("avgMoney", tempMap.get("AVG_MONEY"));
				client.insert("recharge.insertRecharge", paraMap);
			}
			client.getCurrentConnection().commit();
			client.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			client.endTransaction();
		}
		LOG.info("Recharge end");

	}

}
