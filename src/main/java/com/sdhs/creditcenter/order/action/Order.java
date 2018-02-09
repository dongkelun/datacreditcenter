package com.sdhs.creditcenter.order.action;

import java.sql.SQLException;
import java.util.HashMap;

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
		LOG.info("quartz start");
		System.out.println("hello quartz ");
		SqlMapClient client = getSqlMapClient();
		try {
			client.startTransaction();
			client.getCurrentConnection().setAutoCommit(false);
			client.delete("order.delete");
			HashMap paraMap = new HashMap();
			paraMap.put("year", 2017);
			client.insert("order.insertResult", paraMap);

			//测试异常
			//System.out.println(1/0);
			client.getCurrentConnection().commit();
			client.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			client.endTransaction();
		}
		LOG.info("quartz end");

	}
}
