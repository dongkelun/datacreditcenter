package com.sdhs.creditcenter.idcard.action;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.sdhs.pub.BaseSqlMapClientDaoSupport;
import com.sdhs.pub.DateHelp;

/**
 * 
 * @description 身份证预处理
 * @author 		董可伦
 * @time   		2018-1-25
 *
 */
public class IdCard extends BaseSqlMapClientDaoSupport {
	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(IdCard.class);

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void execute() throws SQLException {
		LOG.info("IdCard start");

		//选择mysql数据源
		choseSqlClient("sqlMapClient_mysql");
		SqlMapClient client = getSqlMapClient();
		System.out.println(client.queryForObject("idCard.getDBS"));

		//选择oracle数据源
		choseSqlClient("sqlMapClient");
		client = getSqlMapClient();
		System.out.println(client.queryForObject("idCard.getRegionName", "371426"));

		// 身份证数组
		String[] idArray = { "360105198911211315", "362532740611002", "370285940922007", "370402197706211234",
				"370602440101005","371426199401011618" };
		//身份证链表
		List arrayList = new ArrayList();
		Map temMap = new HashMap();
		for (String tmpId : idArray) {
			temMap = new HashMap();
			temMap.put("CERT_NO", tmpId);
			arrayList.add(temMap);
		}
		//List idList = client.queryForList("idCard.getId", null);
		//
		insertCard(client, arrayList);
		LOG.info("IdCard end");
	}

	/**
	 * 将链表中的身份证处理并插入新表中
	 * @param client
	 * @param arrayList 身份证链表
	 * @throws SQLException
	 */
	@SuppressWarnings({ "rawtypes" })
	public static void insertCard(SqlMapClient client, List arrayList) throws SQLException {
		String id = "";
		int thisYear = Integer.parseInt(DateHelp.getCurrentTimeMillisAsString().substring(0, 4));
		String code = "";
		int year = 0;
		int age = 0;
		String birth = "";
		//0：女   1：男
		int sex = 0;
		boolean flag = true;
		HashMap<String, Object> paraMap = new HashMap<String, Object>();
		try {
			client.startTransaction();
			client.getCurrentConnection().setAutoCommit(false);
			client.delete("idCard.deleteCard");

			for (int i = 0; i < arrayList.size(); i++) {
				id = ((HashMap) arrayList.get(i)).get("CERT_NO").toString();
				if (id.length() == 18) {//18位身份证

					code = id.substring(0, 6);
					year = Integer.parseInt(id.substring(6, 10));
					thisYear = Integer.parseInt(DateHelp.getCurrentTimeMillisAsString().substring(0, 4));
					age = thisYear - year;
					birth = id.substring(10, 14);
					sex = Integer.parseInt(id.substring(16, 17)) % 2;

				} else if (id.length() == 15) {//15位身份证
					code = id.substring(0, 6);
					year = 1900 + Integer.parseInt(id.substring(6, 8));
					thisYear = Integer.parseInt(DateHelp.getCurrentTimeMillisAsString().substring(0, 4));
					age = thisYear - year;
					birth = id.substring(8, 12);
					sex = Integer.parseInt(id.substring(14, 15)) % 2;
				} else {
					flag = false;
				}

				if (flag) {
					System.out.println(id);
					HashMap region = (HashMap) client.queryForObject("idCard.getRegionName", code);

					System.out.println("地区：" + region + " 出生年：" + year + " 年龄：" + age + " 生日：" + birth + " 性别：" + sex);
					paraMap.put("id", id);
					paraMap.put("region", region.get("NAME"));
					paraMap.put("year", year);
					paraMap.put("age", age);
					paraMap.put("birth", birth);
					paraMap.put("sex", sex);
					paraMap.put("province", region.get("PROVINCE"));
					paraMap.put("city", region.get("CITY"));
					paraMap.put("country", region.get("COUNTRY"));
					client.insert("idCard.insertCard", paraMap);

				}
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
	}
}
