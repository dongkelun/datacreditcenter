package com.sdhs.creditcenter.wechat.action;

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
public class WeChat extends BaseSqlMapClientDaoSupport {
	private static final Logger LOG = LoggerFactory.getLogger(WeChat.class);

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void execute() throws SQLException {
		LOG.info("WeChat start");
		choseSqlClient("sqlMapClient_etcmob");
		SqlMapClient client = getSqlMapClient();
		List<HashMap> info = client.queryForList("wechat.getInfo");

		LOG.info(info.toString());
		choseSqlClient("sqlMapClient");
		client = getSqlMapClient();
		try {
			client.startTransaction();
			client.getCurrentConnection().setAutoCommit(false);
			client.delete("wechat.delete");
			HashMap paraMap = new HashMap();
			for (HashMap tempMap : info) {
				paraMap.put("card_no", tempMap.get("CARD_NO"));
				paraMap.put("nickname", tempMap.get("NICKNAME"));
				paraMap.put("sex", tempMap.get("SEX"));
				paraMap.put("mobile", tempMap.get("MOBILE"));
				paraMap.put("sign_cerno", tempMap.get("SIGN_CERNO"));
				paraMap.put("sign_name", tempMap.get("SIGN_NAME"));
				paraMap.put("num1", tempMap.get("NUM1"));
				paraMap.put("num2", tempMap.get("NUM2"));
				paraMap.put("num3", tempMap.get("NUM3"));
				paraMap.put("num4", tempMap.get("NUM4"));
				paraMap.put("total_num", tempMap.get("TOTAL_NUM"));
				paraMap.put("num_fail", tempMap.get("NUM_FAIL"));
				paraMap.put("sum_amt", tempMap.get("SUM_AMT"));
				paraMap.put("avg_amt", tempMap.get("AVG_AMT"));
				paraMap.put("max_amt", tempMap.get("MAX_AMT"));
				paraMap.put("min_amt", tempMap.get("MIN_AMT"));
				client.insert("wechat.insertWeChat", paraMap);
			}
			client.getCurrentConnection().commit();
			client.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			client.endTransaction();
		}
		LOG.info("WeChat end");

	}

}
